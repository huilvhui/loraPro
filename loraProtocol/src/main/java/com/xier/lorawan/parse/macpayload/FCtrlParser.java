package com.xier.lorawan.parse.macpayload;

import java.nio.ByteBuffer;

import com.xier.lorawan.enums.MType;
import com.xier.lorawan.model.fctrl.FCtrl;
import com.xier.lorawan.model.fctrl.FCtrlDownlink;
import com.xier.lorawan.model.fctrl.FCtrlUplink;

/**
 * function:对于macPayload剩余部分解析完fhdr中间继续解析fctrl部分
 * date:2017-09-09 11:05
 */
public class FCtrlParser {

    protected FCtrl parse(MType mType, ByteBuffer macPayloadLeft) {
        byte fctrl = macPayloadLeft.get();//1个字节
        return parse(mType,fctrl);
    }

    public FCtrl parse(MType mType, byte fctrl){
        switch (mType){
            case CONFIRMED_DATA_UP:
            case UN_CONFIRMED_DATA_UP:
                FCtrlUplink upLink = new FCtrlUplink();
                byte foptsLen = (byte) (fctrl & 0x0f);//0000 1111后四位
                byte rfu = (byte)((fctrl & 0x10) >> 4);//0001 0000第5位
                byte ack = (byte)((fctrl & 0x20) >> 5);//第6位
                byte adrAckReq = (byte)((fctrl & 0x40) >> 6);//第7位
                byte adr = (byte)((fctrl & 0x80) >> 7);//第8位 10000000
                upLink.setFoptsLen(foptsLen);
                upLink.setRfu(rfu);
                upLink.setAck(ack);
                upLink.setAdrAckReq(adrAckReq);
                upLink.setAdr(adr);
                return upLink;
            case CONFIRMED_DATA_DOWN:
            case UN_CONFIRMED_DATA_DOWN:
                FCtrlDownlink downlink = new FCtrlDownlink();
                foptsLen = (byte) (fctrl & 0x0f);//0000 1111后四位
                byte fpending = (byte)((fctrl & 0x10) >> 4);//第5位
                ack = (byte)((fctrl & 0x20) >> 5);//第6位
                rfu = (byte)((fctrl & 0x40) >> 6);//第7位
                adr = (byte)((fctrl & 0x80) >> 7);//第8位
                downlink.setFoptsLen(foptsLen);
                downlink.setRfu(rfu);
                downlink.setAck(ack);
                downlink.setFpending(fpending);
                downlink.setAdr(adr);
                return downlink;
            default:
                throw new RuntimeException("未知类型fctrl");
        }
    }

}
