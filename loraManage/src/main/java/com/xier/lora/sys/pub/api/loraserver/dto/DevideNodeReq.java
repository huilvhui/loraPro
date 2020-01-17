package com.xier.lora.sys.pub.api.loraserver.dto;

/**
 * 节点请求
 * <p>
 * </p>
 * @author lvhui5 2017年12月21日 下午4:41:23
 * @version V1.0
 */
public class DevideNodeReq {
	
	private Integer receiveDelay;
	
	private Integer RX1dataRateOffset;
	
	private Integer RX2dataRate;
	
	private String devAddr;
	
	private Integer maxDutyCycle;
	
	public Integer getReceiveDelay() {
		return receiveDelay;
	}
	
	public void setReceiveDelay(Integer receiveDelay) {
		this.receiveDelay = receiveDelay;
	}
	
	public Integer getRX1dataRateOffset() {
		return RX1dataRateOffset;
	}
	
	public void setRX1dataRateOffset(Integer rX1dataRateOffset) {
		RX1dataRateOffset = rX1dataRateOffset;
	}
	
	public Integer getRX2dataRate() {
		return RX2dataRate;
	}
	
	public void setRX2dataRate(Integer rX2dataRate) {
		RX2dataRate = rX2dataRate;
	}
	
	public String getDevAddr() {
		return devAddr;
	}
	
	public void setDevAddr(String devAddr) {
		this.devAddr = devAddr;
	}
	
	public Integer getMaxDutyCycle() {
		return maxDutyCycle;
	}
	
	public void setMaxDutyCycle(Integer maxDutyCycle) {
		this.maxDutyCycle = maxDutyCycle;
	}
	
}
