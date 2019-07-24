package com.xier.lora.gateway.dto;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xier.lora.constant.EnumMsgType;
import com.xier.lora.constant.IGeneralErrorCode;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lorawan.util.HexUtil;

/**
 * 基础消息
 * <p>
 * 与网关之间的约定 协议版本 Token 消息类型 网关ID 1字节 2字节 1字节(0x02) 8字节
 * </p>
 * @author lvhui5 2017年11月20日 下午5:55:07
 * @version V1.0
 */
public class BaseMessage{
	
	/**
	 * 协议版本 1个字节
	 */
	private Integer protrolVer;
	
	/**
	 * 2字节
	 */
	private String token;
	
	/**
	 * 消息类型 字典： KEEPLIVE_DATA = 2 KEEPLIVE _ACK = 4 UPSTREAM_DATA = 0 UPSTREAM_ACK = 1 DOWNSTREAM_DATA = 3 DOWNSTREAM_ACK = 5
	 */
	private Integer msgType;
	/**
	 * 网关Id
	 */
	private String gateWayId;
	
	private ByteBuffer textByteBuffer;
	
	private GatewayData gateWayData;
	
	protected BaseMessage(GatewayData gateWayData) {
		this.gateWayData = gateWayData;
		this.textByteBuffer = ByteBuffer.wrap(gateWayData.getData());
		textByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byte[] token = new byte[2];
		byte[] msgType = new byte[1];
		byte[] gatewayId = new byte[8];
		byte proVer = textByteBuffer.get();
		textByteBuffer.get(token);
		textByteBuffer.get(msgType);
		textByteBuffer.get(gatewayId);
		//一位字节使用bytesToHexString 转换
		this.protrolVer = Integer.valueOf(proVer);
		//16进制转换
		this.token = HexUtil.bytesToHexString(token);
		//this.msgType = Integer.parseInt(HexUtil.bytesToHexString(msgType));
		this.msgType = msgType[0] & 0xFF;
		if(msgType() == null){ //不符合约定的消息类型
			throw new IllegalArgumentException("msgType [" + getMsgType() + "] not exsit");
		}
		//16进制转换
		this.gateWayId = HexUtil.bytesToHexString(gatewayId);
		System.out.println("=====================protrolVer =" + this.protrolVer + " token=" + this.token+ " msgType=" + this.msgType + " gatewayId=" + this.gateWayId);
	}
	
	
	public BaseMessage(Integer protrolVer, String token, Integer msgType, String gateWayId) {
	    super();
	    this.protrolVer = protrolVer;
	    this.token = token;
	    this.msgType = msgType;
	    this.gateWayId = gateWayId;
    }


	public GatewayData getGateWayData() {
		return gateWayData;
	}
	
	public void setGateWayData(GatewayData gateWayData) {
		this.gateWayData = gateWayData;
	}
	
	public ByteBuffer getTextByteBuffer() {
		return textByteBuffer;
	}
	
	public void setTextByteBuffer(ByteBuffer textByteBuffer) {
		this.textByteBuffer = textByteBuffer;
	}
	
	public Integer getProtrolVer() {
		return protrolVer;
	}
	
	public void setProtrolVer(Integer protrolVer) {
		this.protrolVer = protrolVer;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getGateWayId() {
		return gateWayId;
	}
	
	public void setGateWayId(String gateWayId) {
		this.gateWayId = gateWayId;
	}
	
	public Integer getMsgType() {
		return msgType;
	}
	
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
	
	public EnumMsgType msgType() {
		return EnumMsgType.indexByValue(getMsgType());
	}
	
	
	/**
	 * 网关消息处理
	 * @author lvhui5 2017年11月21日 下午3:14:36
	 * @return
	 */
	public LoraDataResp gatewayHandler() {
		EnumMsgType msgType;
		if ((msgType = msgType()) == null) {
			LoraDataResp resp = new LoraDataResp();
			resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, this.msgType);
			return resp;
		}
		return msgType.getMsgHandler().gatewayMsgDispatch(this,msgType);
	}

	/**
	 * 下行token由服务生成
	 * @author lvhui5 2017年12月28日 下午5:05:12
	 * @param ack 是否网关的ack
	 * @return
	 */
    public BaseMessage buildMsg(boolean gatewayResp) {
    	BaseMessage baseMessage = new BaseMessage(protrolVer, gatewayResp?this.token:HexUtil.bytesToHexString(HexUtil
		        .generalHexLength(2)), gatewayResp?EnumMsgType.UPSTREAM_ACK.getKey().intValue():EnumMsgType.DOWNSTREAM_DATA
		        .getKey().intValue(), gateWayId);
    	return baseMessage;
	}
    
    
    public byte[] toByteArray(){
    	//12位报文前缀
		byte[] byteArray = new byte[12];
		byteArray[0] = protrolVer.byteValue();
		//byte[] tokenByte  = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(token),2);
		//下发给节点的消息由服务生成token
		byte[] tokenByte  = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(this.token), 2);
		byteArray[1] = tokenByte[0];
		byteArray[2] = tokenByte[1];
		byteArray[3] = this.msgType.byteValue();
		byte[] gateWayIdByte = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(gateWayId),8);
		for(int i = 4 ; i < 12; i++){
			byteArray[i] = gateWayIdByte[i-4];
		}
		return byteArray;	
    }


    
}
