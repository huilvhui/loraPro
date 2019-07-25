package com.xier.lora.retransmit.service;



/**
 * 下行ack接收窗口 对应本地端口
 * <p>
 * 维护接收窗口对应的消息ack队列
 * </p>
 * @author lvhui5 2018年2月27日 上午9:51:13
 * @version V1.0
 */
public class ReceiveWindow {
	
	/**
	 * 窗口持续时间  400ms
	 * 时间过长会导致消息下发时间超过节点接收窗口持续时间
	 * 当消息下发到网关出现大量延时的时候考虑减小
	 */
	private static final Long lastTime = 400L;
	/**
	 * 最大重发次数
	 */
	private static final int maxRetransmitNum = 1;
	/**
	 * 窗口打开时间 (毫秒)
	 */
	private Long openTime;
	/**
	 * 需要下发的消息
	 */
	private byte[] msg;
	/**
	 * 下发地址
	 */
	private String address;
	/**
	 * 下发地址端口(目标端口)
	 */
	private int port;
	/**
	 * 下发消息token
	 */
	private String token;
	/**
	 * 网关id
	 */
	private String gatewayId;
	/**
	 * 当前重发计数
	 * 用于控制下行消息重发次数
	 */
	private int retransmitNum = 1;
	
    public Long getOpenTime() {
    	return openTime;
    }
	
    public void setOpenTime(Long openTime) {
    	this.openTime = openTime;
    }
	
    public int getPort() {
    	return port;
    }
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public byte[] getMsg() {
		return msg;
	}
	
	public void setMsg(byte[] msg) {
		this.msg = msg;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getGatewayId() {
		return gatewayId;
	}
	
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	/**
	 * 是否需要重发
	 * 在配置时间内没有收到网关ack
	 * @author lvhui5 2018年2月27日 下午7:21:20
	 * @return
	 */
	public boolean needRetransmit(){
		return System.currentTimeMillis()-this.openTime>lastTime;
	}
	/**
	 * 窗口是否结束(是否已经达到重发次数的上限)
	 * 达到上限 移除窗口
	 * @author lvhui5 2018年2月27日 下午7:44:06
	 * @return
	 */
	public boolean windowEnd(){
		 if(this.retransmitNum >=  maxRetransmitNum){
			 return true;
		 }
		 retransmitNum++;
		 return false;
	}
	
	public ReceiveWindow(byte[] msg, String address, int port, String token, String gatewayId) {
	    super();
	    this.openTime = System.currentTimeMillis();
	    this.msg = msg;
	    this.address = address;
	    this.port = port;
	    this.token = token;
	    this.gatewayId = gatewayId;
	    
    }
	
}
