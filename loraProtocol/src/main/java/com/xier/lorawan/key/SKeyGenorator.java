package com.xier.lorawan.key;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.hikvision.lorawan.security.aes.LoraPayloadAesCrypto;
import com.hikvision.lorawan.security.aes.LoraPayloadAesCryptoImpl;
import com.hikvision.lorawan.util.HexUtil;

/**
 * function:用于计算skey的工具类
 * NwkSKey = aes128_encrypt(AppKey, 0x01 | AppNonce | NetID | DevNonce | pad16)
 * AppSKey = aes128_encrypt(AppKey, 0x02 | AppNonce | NetID | DevNonce | pad16)
 * date:2017-09-07 11:02
 */
public class SKeyGenorator {

    private static final LoraPayloadAesCrypto loraPayloadAesCrypto = new LoraPayloadAesCryptoImpl();

    private static int lenWithKeyType(byte[] appNonce,byte[] netId,byte[] devNonce){
        return 1 + appNonce.length + netId.length + devNonce.length;
    }

    public static byte[] nwkSKey(byte[] appKey,byte[] appNonce,byte[] netId,byte[] devNonce){
        return genKey((byte)0x01,appKey,appNonce,netId,devNonce);
    }
    public static byte[] appSKey(byte[] appKey,byte[] appNonce,byte[] netId,byte[] devNonce){
    	return genKey((byte)0x02,appKey,appNonce,netId,devNonce);
    }
    
    
    //pad16必须自动补零 目前是手动补零的 不足16位全部补0x00
    private static byte[] genKey(byte keyType,byte[] appKey,byte[] appNonce,byte[] netId,byte[] devNonce){
        int len = lenWithKeyType(appNonce,netId,devNonce);
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(keyType);
        byteBuffer.put(appNonce);
        byteBuffer.put(netId);
        byteBuffer.put(devNonce);
        return loraPayloadAesCrypto.encryptECB(HexUtil.hexLengthAdapt(byteBuffer.array(), 16),appKey);
    }

}
