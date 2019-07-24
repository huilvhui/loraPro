package com.xier.lorawan.mac;

import java.nio.ByteBuffer;

/**
 * 设备终端状态请求返回
 * <p></p>
 * @author lvhui5 2017年12月26日 上午10:40:45
 * @version V1.0
 */
public class DevStatusAns  extends  AbstractAntMacCommand{

	/**
	 * 电池电量
	 *  0		终端在使用外接电源
	 * 	1..254	电池电量，1是最小值，254是最大值
	 * 	255		终端设备无法获取电池电量
	 */
	private byte battery;
	/**
	 * 最近一次接收成功 DevStatusReq 命令的解调信噪比，其值（四舍五入）取整，单位dB。余量值是一个有符号整型，长度6个比特位，最小值 -32，最大值31
	 */
	private byte margin;
	
	public byte getBattery() {
		return battery;
	}
	
	public void setBattery(byte battery) {
		this.battery = battery;
	}
	
	public byte getMargin() {
		return margin;
	}
	
	public void setMargin(byte margin) {
		this.margin = margin;
	}

	@Override
    protected byte getType() {
	    return 0x06;
    }

	@Override
    protected int getLength() {
	    return 3;
    }
	

	@Override
    protected void wrap(ByteBuffer byteBuffer) {
		this.battery = byteBuffer.get();
		this.margin = byteBuffer.get(); 
    }

	@Override
    protected void commandAntHandler(MacCommandAntHandler handler) {
		handler.devStatus(battery);
		if(logger.isDebugEnabled()){
			logger.debug("==============DevStatusAns getCommandAns battery :" + battery);
		}
    }

	@Override
    protected boolean wrapInner(ByteBuffer byteBuffer) {
		int remain = byteBuffer.remaining();
		if(remain < 2){
			logger.error("==============DevStatusAns size expect 2 but " + remain);
			return false;
		} 
		return true;
    }


}
