package com.xier.lora.gateway.memcache.entity;

import com.xier.lora.constant.EnumAccessMode;
import com.xier.lora.gateway.memcache.entity.DeviceNodeInfo.EnumReceiveWindow;

/**
 * 通知消息
 * <p></p>
 * @author lvhui5 2018年3月2日 下午4:13:39
 * @version V1.0
 */

public class NotifyMsg {
	/**
	 * 下行消息内容
	 */
	private String downlinkMsg;
	/**
	 * 消息模式
	 */
	private EnumAccessMode accessMode;
	/**
	 * 是否需要节点ack
	 */
	private boolean ack;
	/**
	 * 下发窗口
	 */
	private EnumReceiveWindow receiveWindow;
	
	public String getDownlinkMsg() {
		return downlinkMsg;
	}
	
	public void setDownlinkMsg(String downlinkMsg) {
		this.downlinkMsg = downlinkMsg;
	}
	
	public EnumAccessMode getAccessMode() {
		return accessMode;
	}
	
	public void setAccessMode(EnumAccessMode accessMode) {
		this.accessMode = accessMode;
	}
	
	public boolean needNodeConfirm() {
		return ack;
	}
	
	public void setAck(boolean ack) {
		this.ack = ack;
	}
	
	public EnumReceiveWindow getReceiveWindow() {
		return receiveWindow;
	}
	
	public void setReceiveWindow(EnumReceiveWindow receiveWindow) {
		this.receiveWindow = receiveWindow;
	}

	/**
	 * 是否rx1窗口下发
	 * @author lvhui5 2018年3月2日 下午4:46:40
	 * @return
	 */
	public boolean isRx1(){
		return EnumReceiveWindow.rx1 == receiveWindow;
	}
	
	
	/**
	 * 是否classC
	 * @author lvhui5 2017年12月20日 上午11:14:57
	 * @return
	 */
	public boolean isClassC(){
		if(EnumAccessMode.CLASS_C == this.accessMode)
			return true;
		return false;
	}
	
	/**
	 * 是否classB
	 * @author lvhui5 2017年12月20日 上午11:14:57
	 * @return
	 */
	public boolean isClassB(){
		if(EnumAccessMode.CLASS_B == this.accessMode)
			return true;
		return false;
	}
}
