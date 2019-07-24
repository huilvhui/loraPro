package com.xier.lorawan.security.aes;

/**
 * function:lora 负载数据部分的加解密 一致性校验
 * date:2017-09-06 14:53
 */
public interface LoraPayloadAesCrypto {
    /**
     * 计算MIC用于一致性校验
     * @param key  appKey|appSKey|nwkSKey
     * @param data
     * @return
     */
    byte[] calcMic(byte[] key,byte[] data);

    /**
     * AES-ECB模式解密算法
     * @param data
     * @param key appKey|appSKey|nwkSKey
     * @return
     */
    byte[] decryptECB(byte[] data, byte[] key);

    /**
     * AES-ECB模式加密算法
     * @param data 需要加密的数据
     * @param key appKey|appSKey|nwkSKey
     * @return
     */
    byte[] encryptECB(byte[] data, byte[] key);

    byte[] decryptCBC(byte[] data, byte[] key);
    byte[] encryptCBC(byte[] data, byte[] key);

}
