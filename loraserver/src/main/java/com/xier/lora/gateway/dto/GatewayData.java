package com.xier.lora.gateway.dto;

public class GatewayData {
	
	/**
	 * 网关地址
	 */
	private String address;
	/**
	 * 网关端口
	 */
	private int port;
	/**
	 * 网关上报数据
	 */
	private byte[] data;
	
	public GatewayData(String address, int port, byte[] data) {
		this.address = address;
		this.port = port;
		this.data = data;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
