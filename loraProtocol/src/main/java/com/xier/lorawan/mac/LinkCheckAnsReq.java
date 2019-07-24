package com.xier.lorawan.mac;


/**
 * 终端连接状态请求（由服务返回）
 * <p></p>
 * @author lvhui5 2017年12月25日 下午5:46:31
 * @version V1.0
 */
public class LinkCheckAnsReq  extends AbstractReqMacCommand{
	/**
	 *（解调余量）是最近一条被成功收到的 LinkCheckReq 命令的链路预算（单位dB），是一个8位（bits）无符号整型，范围 [0,254]。
	 * 值为 0 表示在解调的下限（0dB或者没有余量）上收到了数据，
	 * 值20表示网关在比解调下限高出 20 dB 的信号强度上收到了数据。
	 * 255是保留值。
	 */
	private byte margin;
	/**
	 * 最近一次成功收到 LinkCheckReq 的网关的数量
	 */
	private byte gwCnt;
	
	public LinkCheckAnsReq(byte margin, byte gwCnt) {
	    this.margin = margin;
	    this.gwCnt = gwCnt;
    }

	public byte getMargin() {
		return margin;
	}
	
	public void setMargin(byte margin) {
		this.margin = margin;
	}

	
    public byte getGwCnt() {
    	return gwCnt;
    }

	
    public void setGwCnt(byte gwCnt) {
    	this.gwCnt = gwCnt;
    }

	@Override
    protected byte getType() {
	    return 0x02;
    }

	@Override
    protected int getLength() {
	    return 3;
    }

	@Override
    protected void inner(byte[] newArray) {
		newArray[newArray.length-2] = this.margin;
		newArray[newArray.length-1] = this.gwCnt;
    }
}
