package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;


public enum EnumGatewayError {
	TOO_LATE("TOO_LATE","拒绝接收，因为对于下发该数据来说，时间太迟了。"),
	TOO_EARLY("TOO_EARLY","拒绝接收，因为收到的需要下发的数据时间戳比计划下发数据的时间早太多了。"),
	COLLISION_PACKET("COLLISION_PACKET","拒绝接收，因为在请求的时间区域里已经有一个报文等待下发。"),
	COLLISION_BEACON("COLLISION_BEACON","拒绝接收，因为在请求的时间区域里已经有一个信标等待下发。"),
	TX_FREQ("TX_FREQ","拒绝接收，因为请求的频率不被下发射频链路支持。"),
	TX_POWER("TX_POWER","拒绝接收，因为请求的功率不被网关支持。"),
	GPS_UNLOCKED("GPS_UNLOCKED","拒绝接收，因为GPS信息未获取，GPS时间戳不可以。"),
	OTHER("other","未知错误"),
	;
	private String key;
	private String description;
	
	

	/**
	 * 全局索引池
	 */
	private static Map<String, EnumGatewayError> pool = new HashMap<String, EnumGatewayError>();
	static {
		for (EnumGatewayError et : EnumGatewayError.values()) {
			pool.put(et.key, et);
		}
	}
	
	private EnumGatewayError(String key, String description) {
		this.key = key;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getKey() {
		return key;
	}
	
	
	
	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumGatewayError indexByValue(String value) {
		if(pool.get(value) == null){
			return OTHER;
		}
		return pool.get(value);
	}
	
}
