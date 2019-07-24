package com.xier.lorawan.payload;

import java.nio.ByteBuffer;

import com.xier.lorawan.security.aes.LoraPayloadAesCrypto;
import com.xier.lorawan.security.aes.LoraPayloadAesCryptoImpl;

/**
 * 接收消息的0payload
 * <p>
 * 头部
 * 
 * </p>
 * @author lvhui5 2017年11月22日 下午2:33:01
 * @version V1.0
 */
public abstract class AbstractAcceptPayload{
    protected byte[] appKey;//applicationKey 提取配置好
    protected ByteBuffer plaintextByteBuffer;
    protected LoraPayloadAesCrypto loraPayloadAesCrypto = new LoraPayloadAesCryptoImpl();
    
    
    
    //重新计算mic 模拟设备端重新生成一遍mic mhdr|appEUI|devEUI|devNonce 注意没有mic结尾了
    protected byte[] reCalculateMic(){
        plaintextByteBuffer.rewind();
        byte[] data = new byte[plaintextByteBuffer.capacity()-4];//去掉mic的的数据
        plaintextByteBuffer.get(data);
        byte[] newMic = loraPayloadAesCrypto.calcMic(appKey,data);
        return newMic;
    }
}
