package com.xier.lora.sys.pub.api.loraserver.dto;

import java.util.Date;

import com.xier.lora.query.BaseResp;

/**
 * 注册网关接口查询返回
 * <p></p>
 * @author lvhui5 2017年12月13日 上午10:16:28
 * @version V1.0
 */
public class GatewayResp extends BaseResp{
	
	
	private Integer id;
	
	private String gatewayId;
	
	private String gatewayName;
	
	private String gatewayDesc;
	
	private String address;
	
	private Integer downlinkPort;
	
	private Date createTime;
	

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getGatewayId() {
		return gatewayId;
	}
	
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getGatewayName() {
		return gatewayName;
	}
	
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	
	public String getGatewayDesc() {
		return gatewayDesc;
	}
	
	public void setGatewayDesc(String gatewayDesc) {
		this.gatewayDesc = gatewayDesc;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public Integer getDownlinkPort() {
		return downlinkPort;
	}
	
	public void setDownlinkPort(Integer downlinkPort) {
		this.downlinkPort = downlinkPort;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
