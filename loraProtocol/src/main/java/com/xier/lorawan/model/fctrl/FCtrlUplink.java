package com.xier.lorawan.model.fctrl;

import com.xier.lorawan.model.IMessageSegment;
import com.xier.lorawan.util.HexUtil;
import com.xier.lorawan.util.MessageSerializeUtil;

/**
 * function:上行
 * date:2017-09-06 19:24
 */
public class FCtrlUplink  extends FCtrl implements IMessageSegment{
    private byte adrAckReq;// 6b adaptive data rate

    private byte rfu;// 4b

    public byte getAdrAckReq() {
        return adrAckReq;
    }

    public void setAdrAckReq(byte adrAckReq) {
        this.adrAckReq = adrAckReq;
    }

    public byte getRfu() {
        return rfu;
    }

    public void setRfu(byte rfu) {
        this.rfu = rfu;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public String toString() {
        return "FCtrlUplink{"+ HexUtil.bytesToHexString(MessageSerializeUtil.toBinary(this))+"}";
    }
}
