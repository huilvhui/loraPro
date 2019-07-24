package com.xier.lorawan.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xier.lorawan.enums.MType;
import com.xier.lorawan.model.FHDR;
import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.model.fctrl.FCtrlDownlink;
import com.xier.lorawan.model.fctrl.FCtrlUplink;
import com.xier.lorawan.model.message.MAC;

/**
 * function: 序列化所有体的工具类
 * date:2017-09-09 14:23
 */
public class MessageSerializeUtil {

    public static byte toBinary(MHDR mhdr){
        return mhdr.get();
    }

    public static byte toBinary(FCtrlDownlink fCtrl){
        byte fctrl =  (byte) (fCtrl.getAdr() << 7 | fCtrl.getRfu() << 6 | fCtrl.getAck() << 5 | fCtrl.getFpending() << 4 | fCtrl.getFoptsLen());
        return fctrl;
    }
    public static byte toBinary(FCtrlUplink fCtrl){
    	byte adr = fCtrl.getAdr();
    	//byte fctrlTest = (byte)(adr << 7);
    	//byte fctrlTest2 = (byte)(adr << 7 | fCtrl.getAdrAckReq() << 6 | fCtrl.getAck() << 5 | fCtrl.getRfu() << 4 | fCtrl.getFoptsLen());
    	byte fctrl =  (byte) (adr << 7 | fCtrl.getAdrAckReq() << 6 | fCtrl.getAck() << 5 | fCtrl.getRfu() << 4 | fCtrl.getFoptsLen());
        return fctrl;
    }

    public static byte[] toBinary(MType mType,FHDR fhdr){
        //1 fctrl的len
        int fctrlLen = 1;
        int fcntLen = 2;//short 占2个字节
        ByteBuffer bf = ByteBuffer.allocate(fhdr.getDevAddr().length + fctrlLen + fcntLen + fhdr.getFopts().length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.put(fhdr.getDevAddr());
        switch (mType.upDownLink()){
            case UP_LINK:
                FCtrlUplink uplink = (FCtrlUplink)fhdr.getFctrl();
                byte fctrl = toBinary(uplink);
                bf.put(fctrl);
                break;
            case DOWN_LINK:
                FCtrlDownlink downlink = (FCtrlDownlink)fhdr.getFctrl();
                fctrl = toBinary(downlink);
                bf.put(fctrl);
                break;
            default:
                throw new RuntimeException("未知上先行链路,无法序列化");
        }
        bf.putShort(fhdr.getFcnt());
        bf.put(fhdr.getFopts());
        return bf.array();
    }

    public static byte[] toBinary(MType mType ,MAC mac){
        FHDR fhdr = mac.getFhdr();
        byte[] fhdrB = toBinary(mType,fhdr);

        byte[] fport = mac.getFport();
        byte[] frmPayload = mac.getFrmPayload();
        ByteBuffer bf = ByteBuffer.allocate(fhdrB.length + fport.length + frmPayload.length);
        bf.order(ByteOrder.LITTLE_ENDIAN);
        bf.put(fhdrB);
        bf.put(fport);
        bf.put(frmPayload);
        return bf.array();
    }

}
