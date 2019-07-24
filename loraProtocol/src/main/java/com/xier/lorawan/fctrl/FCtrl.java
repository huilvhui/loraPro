package com.xier.lorawan.model.fctrl;

import com.xier.lorawan.model.IMessageSegment;

/**
 * function:1字节长度
 * date:2017-09-06 19:18
 * author: he dong yu
 * email:554151811@qq.com
 */
public class FCtrl implements IMessageSegment{
    private byte adr;//7b adaptive data rate 适应数据速率 如果被禁用 网络就不能控制速率 adr的设计为了电池续航和服务端性能考虑

    private byte ack; //5b

    private byte foptsLen; //0b..3b

    public byte getAdr() {
        return adr;
    }

    public void setAdr(byte adr) {
        this.adr = adr;
    }

    public byte getAck() {
        return ack;
    }

    public void setAck(byte ack) {
        this.ack = ack;
    }

    public byte getFoptsLen() {
        return foptsLen;
    }

    public void setFoptsLen(byte foptsLen) {
        this.foptsLen = foptsLen;
    }

    @Override
    public int length() {
        return 1;
    }
}
