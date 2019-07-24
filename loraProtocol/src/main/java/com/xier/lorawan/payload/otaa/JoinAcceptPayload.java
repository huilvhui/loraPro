package com.xier.lorawan.payload.otaa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.message.JoinAccept;
import com.xier.lorawan.payload.Payload;
import com.xier.lorawan.security.aes.LoraPayloadAesCrypto;
import com.xier.lorawan.security.aes.LoraPayloadAesCryptoImpl;
import com.xier.lorawan.util.HexUtil;

/**
 * function:
 * date:2017-09-06 15:13
 */
public class JoinAcceptPayload  implements Payload {
    private Logger logger = LoggerFactory.getLogger(JoinAcceptPayload.class);
    private byte[] appKey;//applicationKey 提前配置好

    private LoraPayloadAesCrypto loraPayloadAesCrypto = new LoraPayloadAesCryptoImpl();

    private PHYPayload phyPayload;//这些对象请自行构造 因为join accept message都是由server端生成的
    public JoinAcceptPayload(String applicationKey,PHYPayload phyPayload) {
        this.appKey = HexUtil.hexStringToBytes(applicationKey);
        this.phyPayload = phyPayload;
    }

    //生成算法|代表连接的意思  mhdr|appnonce|netId|devAddr|dlsetting|rxDelay|cflist
    public byte[] reCalculateMic(byte[] key){
        ByteBuffer byteBuffer = ByteBuffer.allocate(calcMicByteLen());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(phyPayload.getMhdr().get());
        JoinAccept joinAccept = (JoinAccept)phyPayload.getMessage();
        byteBuffer.put(joinAccept.getAppNonce());
        byteBuffer.put(joinAccept.getNetId());
        byteBuffer.put(joinAccept.getDevAddr());
        byteBuffer.put(joinAccept.getDlSettings());
        byteBuffer.put(joinAccept.getRxDelay());
        byteBuffer.put(joinAccept.getCfList());
        return loraPayloadAesCrypto.calcMic(key,byteBuffer.array());
    }

    private int lengthBeforeEncode = 0;
    
    
    public int getLengthBeforeEncode(){
    	return lengthBeforeEncode;
    }
    

    //先计算mic，再加密，加密的内容不包括mhdr
    public String encrypt(){
        //第一步重新计算MIC
        byte[] mic = reCalculateMic(appKey);
        logger.info("MIC:"+HexUtil.bytesToHexString(mic));
        //使用解密算法进行加密 挺绕的 这块实验了很多次 只有这种方式才能得到期望的结果
        ByteBuffer byteBuffer = ByteBuffer.allocate(calcEncryptLen(mic.length));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        //此次加密不带mhdr
        JoinAccept joinAccept = (JoinAccept)phyPayload.getMessage();
        byteBuffer.put(joinAccept.getAppNonce());
        byteBuffer.put(joinAccept.getNetId());
        byteBuffer.put(joinAccept.getDevAddr());
        byteBuffer.put(joinAccept.getDlSettings());
        byteBuffer.put(joinAccept.getRxDelay());
        byteBuffer.put(joinAccept.getCfList());
        byteBuffer.put(mic);
        

        //加密？
        
        byte[] encrypt = loraPayloadAesCrypto.decryptECB(byteBuffer.array(),appKey);
		lengthBeforeEncode = 1 + encrypt.length;
        ByteBuffer returnBuffer = ByteBuffer.allocate(1+encrypt.length);//为mhdr多申请一个
        returnBuffer.order(ByteOrder.LITTLE_ENDIAN);
        returnBuffer.put(phyPayload.getMhdr().get());//把mhdr重新包裹到头部 因为mhdr不能变 设备端还要根据这个来判断是joinAccept 头信息
        returnBuffer.put(encrypt);
        return  Base64.getEncoder().encodeToString(returnBuffer.array());
    }

    //这一块不用服务端实现 客户端负责解密join_accept_request 处于兴趣 我们实现下 其实就是将encrypt逆向编码
    public void decrypt(){

    }


    private int calcMicByteLen(){
        //暂时认为都不为null
        JoinAccept joinAccept = (JoinAccept)phyPayload.getMessage();
        //1代表mhdr的长度
        return 1 + joinAcceptLen(joinAccept);
    }

    private int joinAcceptLen(JoinAccept joinAccept){
        return joinAccept.getAppNonce().length+joinAccept.getCfList().length+joinAccept.getDevAddr().length
                +joinAccept.getDlSettings().length+joinAccept.getNetId().length+joinAccept.getRxDelay().length;
    }
    //忽略mhdr长度
    private int calcEncryptLen(int micLen){
        JoinAccept joinAccept = (JoinAccept)phyPayload.getMessage();
        return  joinAcceptLen(joinAccept) + micLen;
    }
}
