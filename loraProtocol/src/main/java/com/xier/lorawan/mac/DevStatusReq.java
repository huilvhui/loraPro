package com.xier.lorawan.mac;

import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * 请求终端状态mac命令
 * <p></p>
 * @author lvhui5 2017年12月26日 上午10:29:37
 * @version V1.0
 */
public class DevStatusReq  extends  AbstractReqMacCommand{

	private static final String DEV_STATUS_REQ = "dev_status_req";
	
	private String devAddr;
	
	public DevStatusReq(String devAddr) {
	    this.devAddr = devAddr;
    }

	@Override
    protected byte getType() {
	    return 0x06;
    }

	@Override
    protected int getLength() {
	    return 1;
    }

	@Override
    protected void inner(byte[] newArray) {
	    //do nothing   
    }
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(DEV_STATUS_REQ).append(devAddr).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DevStatusReq) {
			DevStatusReq rhs = (DevStatusReq) obj;
			return this.devAddr.equals(rhs.devAddr);
		}
		return false;
	}

	@Override
	public String toString() {
		return DEV_STATUS_REQ + devAddr;
	}
	
}
