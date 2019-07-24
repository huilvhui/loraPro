package com.xier.lorawan.payload.abp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.message.MAC;
import com.xier.lorawan.parse.MHDRParser;
import com.xier.lorawan.parse.macpayload.MacParser;

/**
 * function:解析第二阶段入网传输中的上行数据
 *          各种MType类型要处理好
 * date:2017-09-08 17:08
 */
public class MACUpLinkPayload  extends MACPayload {

    private byte[] phyPayloadEncrypt;

    public MACUpLinkPayload(String encryptPhyPayload) {
        //this.appSKey = HexUtil.hexStringToBytes(appSKey);
       // this.nwkSKey = HexUtil.hexStringToBytes(nwkSKey);
        this.phyPayloadEncrypt = Base64.getDecoder().decode(encryptPhyPayload);
        this.phyPayload = parseToPhyPayload();
    }

    private PHYPayload parseToPhyPayload(){
        ByteBuffer phyPayloadBuffer = ByteBuffer.wrap(phyPayloadEncrypt);
        phyPayloadBuffer.order(ByteOrder.LITTLE_ENDIAN);

        MHDRParser mhdrParser = new MHDRParser();
        MHDR mhdr = mhdrParser.parse(phyPayloadBuffer);
        //
        MacParser macParser = new MacParser();
        MAC mac = macParser.parse(mhdr.getMtype(),phyPayloadBuffer);
        //
        byte[] mic = new byte[4];
        phyPayloadBuffer.get(mic);
        PHYPayload phyPayload = new PHYPayload(mhdr,mac,mic);
        return phyPayload;
    }

    /**
     * 解密
     * @return
     */
    public byte[] decryptFRMPayload(byte[] appSKey,byte[] nwkSKey){
        return super.decryptOrEncryptFRMPayload(appSKey,nwkSKey);
    }

}
