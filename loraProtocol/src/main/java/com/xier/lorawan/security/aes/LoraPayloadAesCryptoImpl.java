package com.xier.lorawan.security.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * function: AES 加解密
 * date:2017-09-06 14:52
 */
public class LoraPayloadAesCryptoImpl implements LoraPayloadAesCrypto{
    public static final String KEY_ALGORITHM = "AES";
    //填充模式 缺位不填充 必须是16的倍数
    public static final String CIPHER_ALGORITHM_ECB_NOPADDING = "AES/ECB/Nopadding";
    public static final String CIPHER_ALGORITHM_CBC__NOPADDING = "AES/CBC/Nopadding";
    @Override
    public byte[] calcMic(byte[] key, byte[] data) {
        if(key.length!=16){
            throw new IllegalArgumentException("key length must 16");
        }
        try {
            AesCmac aesCmac = new AesCmac();
            aesCmac.init(new SecretKeySpec(key,"AES"));
            aesCmac.updateBlock(data);
            byte[] cmac = aesCmac.doFinal();
            return Arrays.copyOfRange(cmac,0,4);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    private static Key toKey(byte[] key){
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }
    //解密模式
    public byte[] decryptECB(byte[] data, byte[] key) {
        Key k = toKey(key);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encryptECB(byte[] data, byte[] key){
        Key k = toKey(key);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB_NOPADDING);
            cipher.init(Cipher.ENCRYPT_MODE, k);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public byte[] encryptCBC(byte[] data, byte[] key){
        String ivParameter = "0123456789abcdef";
        Key k = toKey(key);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC__NOPADDING);
            // CBC
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, k, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptCBC(byte[] data, byte[] key) {
        String ivParameter = "0123456123abcdef";
        Key k = toKey(key);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC__NOPADDING);
            // CBC
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, k, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}

