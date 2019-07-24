package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;

import com.xier.lora.gateway.dto.BaseData;
import com.xier.lora.gateway.dto.DownstreamAck;
import com.xier.lora.gateway.dto.DownstreamData;
import com.xier.lora.gateway.dto.UpstreamData;
import com.xier.lora.gateway.service.CommonReturnGatewayService;
import com.xier.lora.gateway.service.DownstreamAckGatewayService;
import com.xier.lora.gateway.service.DownstreamGatewayService;
import com.xier.lora.gateway.service.KeepliveGatewayService;
import com.xier.lora.gateway.service.LoraGatewayDispatcherService;
import com.xier.lora.gateway.service.UpstreamGatewayService;

/**
 * 网关定义消息类型
 * <p>
 * KEEPLIVE_DATA = 2 
 * KEEPLIVE _ACK = 4 
 * UPSTREAM_DATA = 0 
 * UPSTREAM_ACK = 1 
 * DOWNSTREAM_DATA = 3 
 * DOWNSTREAM_ACK = 5 
 * </p>
 * @author lvhui5 2017年11月20日 下午7:14:06
 * @version V1.0
 */
public enum EnumMsgType {
	
	KEEPLIVE_ACK(4, "心跳回复",BaseData.class,new CommonReturnGatewayService(),null), 
	KEEPLIVE_DATA(2, "心跳",BaseData.class,new KeepliveGatewayService(),EnumMsgType.KEEPLIVE_ACK),
	UPSTREAM_ACK(1, "上行回复",BaseData.class,new CommonReturnGatewayService(),null),
	UPSTREAM_DATA(0, "上行",UpstreamData.class,new UpstreamGatewayService(),EnumMsgType.UPSTREAM_ACK), 
	DOWNSTREAM_ACK(5, "下行回复",DownstreamAck.class,new DownstreamAckGatewayService(),null),
	DOWNSTREAM_DATA(3,"下行",DownstreamData.class,new DownstreamGatewayService(),null),
	;
	
	private Integer key;
	private String description;
	private Class<? extends BaseData> dtoClass;

	/**
	 * 消息处理
	 */
	private LoraGatewayDispatcherService msgHandler;
	
	/**
	 * 回调网关
	 */
	private EnumMsgType callBack;
	
	/**
	 * 全局索引池
	 */
	private static Map<Integer, EnumMsgType> pool = new HashMap<Integer, EnumMsgType>();
	static {
		for (EnumMsgType et : EnumMsgType.values()) {
			pool.put(et.key, et);
		}
	}
	
	private EnumMsgType(Integer key, String description, Class<? extends BaseData> dtoClass,
			LoraGatewayDispatcherService msgHandler, EnumMsgType callBack) {
		this.key = key;
		this.description = description;
		this.dtoClass = dtoClass;
		this.msgHandler = msgHandler;
		this.callBack = callBack;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public Class<? extends BaseData> getDtoClass() {
		return dtoClass;
	}
	
	public LoraGatewayDispatcherService getMsgHandler() {
		return msgHandler;
	}
	
	public EnumMsgType getCallBack() {
		return callBack;
	}
	
	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumMsgType indexByValue(Integer value) {
		return pool.get(value);
	}

}
