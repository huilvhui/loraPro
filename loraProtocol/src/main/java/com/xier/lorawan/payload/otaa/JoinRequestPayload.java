package com.xier.lorawan.payload.otaa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.message.JoinRequest;
import com.xier.lorawan.payload.Payload;
import com.xier.lorawan.security.aes.LoraPayloadAesCrypto;
import com.xier.lorawan.security.aes.LoraPayloadAesCryptoImpl;

/**
 * function:入网请求包处理
 * date:2017-09-06 15:12
 */
public class JoinRequestPayload implements Payload {
    //private byte[] appKey;//applicationKey 提取配置好
    private ByteBuffer plaintextByteBuffer;
    private LoraPayloadAesCrypto loraPayloadAesCrypto = new LoraPayloadAesCryptoImpl();
    /**
     * 构造
     * @param applicationKey  秘钥
     * @param joinRequestPlaintext //入网请求串AAEAACAAxSYsFhAWIAB3SgBUe0At4Zo= 来自lora设备生成 第一次并没有加密 所有无需解密     
     */
    public JoinRequestPayload(String joinRequestPlaintext) {
        //解析入网包 base64解码 因为这数据是网关base64编码传来的 我们要还原
        byte[] bytes = Base64.getDecoder().decode(joinRequestPlaintext);
        this.plaintextByteBuffer = ByteBuffer.wrap(bytes);
        plaintextByteBuffer.order(ByteOrder.LITTLE_ENDIAN);

    }

    
    
    public PHYPayload parseToPhyPayload(){
    	//  plaintextByteBuffer.get() 拿一个byte
        MHDR mhdr = MHDR.wrap(plaintextByteBuffer.get());
        byte[] appEUI = new byte[8];
        byte[] devEUI = new byte[8];
        byte[] devNonce = new byte[2];
        plaintextByteBuffer.get(appEUI);
        plaintextByteBuffer.get(devEUI);
        plaintextByteBuffer.get(devNonce);
        JoinRequest message = new JoinRequest(appEUI,devEUI,devNonce);

        byte[] mic = new byte[4];
        plaintextByteBuffer.get(mic);

        PHYPayload phyPayload = new PHYPayload(mhdr,message,mic);
        return phyPayload;
    }
   
    
    
    
    //重新计算mic 模拟设备端重新生成一遍mic mhdr|appEUI|devEUI|devNonce 注意没有mic结尾了
    public byte[] reCalculateMic(byte[] key){
        plaintextByteBuffer.rewind();
        byte[] data = new byte[plaintextByteBuffer.capacity()-4];//去掉mic的的数据
        plaintextByteBuffer.get(data);
        byte[] newMic = loraPayloadAesCrypto.calcMic(key,data);
        return newMic;
    }

}
