package com.xier.lorawan.payload.abp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.message.MAC;
import com.xier.lorawan.payload.Payload;
import com.xier.lorawan.security.aes.LoraPayloadAesCrypto;
import com.xier.lorawan.security.aes.LoraPayloadAesCryptoImpl;
import com.xier.lorawan.util.HexUtil;
import com.xier.lorawan.util.MessageSerializeUtil;

/**
 * function: 正常传输过程中基础处理父类
 * date:2017-09-12 14:04
 */
public class MACPayload implements Payload{
    protected LoraPayloadAesCrypto loraPayloadAesCrypto = new LoraPayloadAesCryptoImpl();

    //protected byte[] appSKey;
    //protected byte[] nwkSKey;
    protected PHYPayload phyPayload;

    public PHYPayload getPhyPayload() {
        return phyPayload;
    }

    /**
     * 重新生成MIC 可以进行校验 这里需要使用nwkSKey进行校验
     * @return
     */
    @Override
    public byte[] reCalculateMic(byte[] key) {
        //构造msg
        byte[] msg = buildMsg();
        //构造B0 查看文档 page20
        byte[] b0 = buildB0(msg);
        ByteBuffer byteBuffer = ByteBuffer.allocate(msg.length + b0.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(b0);
        byteBuffer.put(msg);
        return loraPayloadAesCrypto.calcMic(key,byteBuffer.array());
    }

    //msg = MHDR | FHDR | FPort | FRMPayload
    private byte[] buildMsg(){
        MHDR mhdr = phyPayload.getMhdr();
        MAC mac = (MAC)phyPayload.getMessage();
        byte mhdrB = MessageSerializeUtil.toBinary(mhdr);
        byte[] macB = MessageSerializeUtil.toBinary(mhdr.getMtype(),mac);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1+macB.length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(mhdrB);
        byteBuffer.put(macB);
        return byteBuffer.array();
    }

   
    
    //0x49 4 x 0x00 Dir DevAddr FCntUp or FCntDown 0x00 len(msg) 请参考文档 page20
    private byte[] buildB0(byte[] msg){
        MAC mac = (MAC)phyPayload.getMessage();
        ByteBuffer b0 = ByteBuffer.allocate(16);
        b0.order(ByteOrder.LITTLE_ENDIAN);
        b0.put((byte)0x49);
        b0.put(new byte[]{0x00,0x00,0x00,0x00});
        //根据文档看 0代表uplink 1代表downlink
        byte dir = phyPayload.getMhdr().getMtype().getDir();
        b0.put(dir);
        b0.put(mac.getFhdr().getDevAddr());
        b0.putInt(mac.getFhdr().getFcnt());//这个是细节部分 需要4个字节
        b0.put((byte)0x00);
        b0.put((byte)msg.length);
        return b0.array();
    }

    public static void main(String[] args){
    	
    	ByteBuffer b0 = ByteBuffer.allocate(16);
    	b0.putInt(1);
    	System.out.println(HexUtil.bytesToHexString(b0.array()));
    }
    
    protected byte[] calcSKey(MAC mac,byte[] appSKey,byte[] nwkSKey){
        byte[] fport = mac.getFport();
        if(fport.length==1){
            if(fport[0] == (byte)0x00){
            	//也就是消息加密也有可能用 nwkSKey
                return nwkSKey;
            }else {
                return appSKey;
            }
        }else {
            throw new RuntimeException("fport长度不为1B");
        }
    }

    //参考技术规格文档page19 返回16B
    protected byte[] buildAi(int i){
        MAC mac = (MAC)phyPayload.getMessage();
        ByteBuffer ai = ByteBuffer.allocate(16);
        ai.order(ByteOrder.LITTLE_ENDIAN);
        ai.put((byte)0x01);
        ai.put(new byte[]{0x00,0x00,0x00,0x00});
        byte dir = phyPayload.getMhdr().getMtype().getDir();
        ai.put(dir);
        ai.put(mac.getFhdr().getDevAddr());
        ai.putInt(mac.getFhdr().getFcnt());//这个是细节部分 需要4个字节
        ai.put((byte)0x00);
        ai.put((byte)i);
        return ai.array();
    }


    /**
     * 解密 其实调用的也是加密算法 参考官方提供的c语言版本
     * 
     * 消息体加密
     * @return
     */
    public byte[] decryptOrEncryptFRMPayload(byte[] appSKey,byte[] nwkSKey){
        MAC mac = (MAC)phyPayload.getMessage();
        byte[] key = calcSKey(mac,appSKey,nwkSKey);
        byte[] frmPayload = mac.getFrmPayload();
 
        
        int k = (int)Math.ceil(frmPayload.length / 16.0);//注意这里要求返回大于等于这个值的最小整数 如果length=15的话15/16=0 15/16.0=0.9375结果就会不一样
        //强行分成16个单位？
        ByteBuffer s = ByteBuffer.allocate(16 * k);
        

        s.order(ByteOrder.LITTLE_ENDIAN);
        for(int i=1;i<=k;i++){
            byte[] ai = buildAi(i);
            byte[] si = loraPayloadAesCrypto.encryptECB(ai,key);
            s.put(si);
        }
        //根据官方提供的C程序 还要做一次异或 很奇怪
        byte[] decryptFRMPlayload = new byte[frmPayload.length];
        for(int i=0;i<frmPayload.length;i++){
            decryptFRMPlayload[i] = (byte)(s.get(i) ^ frmPayload[i]);
        }
        return decryptFRMPlayload;
    }
}
