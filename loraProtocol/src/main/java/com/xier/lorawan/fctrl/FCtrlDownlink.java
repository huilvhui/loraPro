package com.xier.lorawan.model.fctrl;

import com.xier.lorawan.model.IMessageSegment;
import com.xier.lorawan.util.HexUtil;
import com.xier.lorawan.util.MessageSerializeUtil;

/**
 * function:下行
 * date:2017-09-06 19:23
 */
public class FCtrlDownlink extends FCtrl implements IMessageSegment{
    private byte rfu;// 6b

    private byte fpending;// 4b

    /**
     *
     * 创建一个新的实例FCtrlDownlink.
     * @param ack  是否ack消息
     * @param adr  是否ADR
     * @param fpending 是否要求节点开启rx2窗口接收数据
     * @param foptsLength fopts字段长度
     * @param openADR 是否开启ADR
     */
    public FCtrlDownlink(boolean ack,boolean fpending,boolean openADR) {
    	setAck(ack?(byte)0x01:(byte)0x00); //是否是ack消息
		setAdr(openADR?(byte)0x01:(byte)0x00); // 开启ADR  目前系统不需要配置adr开放选项 
		setFpending(fpending?(byte)0x01:(byte)0x00);// 是否需要下行消息发布
		setRfu((byte)0x00);
		setFoptsLen((byte)0x00);	
    }

	public FCtrlDownlink() {
	    super();
    }

	public byte getRfu() {
        return rfu;
    }

    public void setRfu(byte rfu) {
        this.rfu = rfu;
    }

    public byte getFpending() {
        return fpending;
    }

    public void setFpending(byte fpending) {
        this.fpending = fpending;
    }

    @Override
    public int length() {
        return 1;
    }

    @Override
    public String toString() {
        return "FCtrlDownlink{" +
                HexUtil.bytesToHexString(MessageSerializeUtil.toBinary(this))+
                '}' + " adr:" + getAdr() + " ack:" + getAck() + " foptsLen:" + getFoptsLen() + " fpending:" + fpending;  
    }
}
