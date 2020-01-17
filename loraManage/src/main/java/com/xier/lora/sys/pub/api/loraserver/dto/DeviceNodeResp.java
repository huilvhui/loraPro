package com.xier.lora.sys.pub.api.loraserver.dto;

import java.util.Date;

import com.xier.lora.query.BaseResp;

/**
 * 设备节点查询接口返回
 * <p></p>
 * @author lvhui5 2017年12月13日 上午10:15:53
 * @version V1.0
 */
public class DeviceNodeResp extends BaseResp{
	
	
	private Integer id;

	private String nodeName;

	private String nodeDesc;

	private String devEUI;
	
	private String appEUI;
	
	private String activation;
	
	private Integer receiveDelay;
	
	private Integer RX1dataRateOffset;
	
	private Integer RX2dataRate;

	private String appsKey;
	
	private String nwksKey;
	
	private String appKey;
	
	private String netId;
	
	private String devAddr;
	
	private Date createTime;
	
	private Integer maxDutyCycle;
	
	private String accessMode;

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getNodeDesc() {
		return nodeDesc;
	}
	
	public void setNodeDesc(String nodeDesc) {
		this.nodeDesc = nodeDesc;
	}
	
	public String getDevEUI() {
		return devEUI;
	}
	
	public void setDevEUI(String devEUI) {
		this.devEUI = devEUI;
	}
	
	public String getAppEUI() {
		return appEUI;
	}
	
	public void setAppEUI(String appEUI) {
		this.appEUI = appEUI;
	}
	
	public String getActivation() {
		return activation;
	}
	
	public void setActivation(String activation) {
		this.activation = activation;
	}
	
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
	
	public String getAppsKey() {
		return appsKey;
	}
	
	public void setAppsKey(String appsKey) {
		this.appsKey = appsKey;
	}
	
	public String getNwksKey() {
		return nwksKey;
	}
	
	public void setNwksKey(String nwksKey) {
		this.nwksKey = nwksKey;
	}
	
	public String getAppKey() {
		return appKey;
	}
	
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	public String getNetId() {
		return netId;
	}
	
	public void setNetId(String netId) {
		this.netId = netId;
	}
	
	public String getDevAddr() {
		return devAddr;
	}
	
	public void setDevAddr(String devAddr) {
		this.devAddr = devAddr;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Integer getMaxDutyCycle() {
		return maxDutyCycle;
	}
	
	public void setMaxDutyCycle(Integer maxDutyCycle) {
		this.maxDutyCycle = maxDutyCycle;
	}
	
	public String getAccessMode() {
		return accessMode;
	}
	
	public void setAccessMode(String accessMode) {
		this.accessMode = accessMode;
	}
	
}
