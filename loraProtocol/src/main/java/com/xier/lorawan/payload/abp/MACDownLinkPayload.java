package com.xier.lorawan.payload.abp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.message.MAC;
import com.xier.lorawan.parse.MHDRParser;
import com.xier.lorawan.parse.macpayload.MacParser;
import com.xier.lorawan.util.HexUtil;

/**
 * function:封装第二阶段入网传输中的下行数据
 * date:2017-09-12 14:01
 */
public class MACDownLinkPayload extends MACPayload{
	
	protected static final Logger logger = LoggerFactory.getLogger(MACDownLinkPayload.class);
    //请自行保证phyPayload中frmPayload是明文
    public MACDownLinkPayload(PHYPayload phyPayload) {
        //this.appSKey = HexUtil.hexStringToBytes(appSKey);
        //this.nwkSKey = HexUtil.hexStringToBytes(nwkSKey);
        this.phyPayload = phyPayload;
        //appSKey,nwkSKey,
    }

    /**
     * 加密 简单模拟下 实际情况肯定不是调用这个方法 因为phyPayload还需要自行构造
     * 把decryptFRMPayload作为源进行aes加密可以得到encryptFRMPayload  decryptFRMPayload=aes_encrypt(key,encryptFRMPayload)
     * 把 encryptFRMPayload作为源进行aes加密可以得到decryptFRMPayload encryptFRMPayload=aes_encrypt(key,decryptFRMPayload)
     * @return
     */
    private byte[] encryptFRMPayload(byte[] appSKey,byte[] nwkSKey){
        return super.decryptOrEncryptFRMPayload(appSKey,nwkSKey);
    }

    public String encrypt(byte[] appSKey,byte[] nwkSKey){
    	//第一步 加密pyhPayload
        MAC mac = (MAC)phyPayload.getMessage();
        if(mac.getFrmPayload() != null && mac.getFrmPayload().length>0){
            mac.setFrmPayload(encryptFRMPayload(appSKey,nwkSKey));//明文变密文
        }
    	//第二步 填充mic
        //重新计算mic
        if(phyPayload.getMic()==null || phyPayload.getMic().length<=0){
            byte[] micBytes = reCalculateMic(nwkSKey);
            phyPayload.setMic(micBytes);
        }
        lengthBeforeEncode = phyPayload.length();
        //第三步组合
        ByteBuffer byteBuffer = ByteBuffer.allocate(phyPayload.length());
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        //mhdr
        MHDRParser mhdrParser = new MHDRParser();
        byte mhdrBytes = mhdrParser.binary(phyPayload.getMhdr());
        //mac
        MacParser macParser = new MacParser();
        byte[] macBytes = macParser.binary(phyPayload.getMhdr().getMtype(),mac);
        //组合
        byteBuffer.put(mhdrBytes).put(macBytes).put(phyPayload.getMic());
        System.out.println("down lora data byte ================" + HexUtil.bytesToHexString(byteBuffer.array()));
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    private int lengthBeforeEncode = 0;
    
    
    public int getLengthBeforeEncode(){
    	return lengthBeforeEncode;
    }
  
}
