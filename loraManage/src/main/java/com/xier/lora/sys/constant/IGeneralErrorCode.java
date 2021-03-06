package com.xier.lora.sys.constant;

public interface IGeneralErrorCode {
	
	String SUCCESS = "0";
	
	/**
	 * 未知错误
	 */
	String UN_KNOWN_ERROR = "100000";
	/**
	 * 未知错误msg
	 */
	String UN_KNOWN_ERROR_MSG = "unknown error";
	
	/**
	 * 数据不存在
	 */
	String DATA_NOT_EXIST = "100001";
	
	/**
	 * 参数错误
	 */
	String PARAMS_ERROR = "100002";
	
	/**
	 * 数据已存在
	 */
	String DATA_EXIST = "100003";
	
	/**
	 * 无权限调用接口
	 */
	String REQUEST_FORBIDDEN = "100004"; 
	
	/**
	 * 秘钥协商失效
	 */
	String NEGOTIATION_OUTDATE = "100005";
	
	/**
	 * 数据加密失败
	 */
	String DATA_ENCRYPT_FAILED = "100006";
	
	/**
	 * 数据解密失败
	 */
	String DATA_DECRYPT_FAILED = "100007";
	
	/**
	 * 消息发布失败
	 */
	String MSG_PUBLISH_FAILED = "200001";
	
	
	/**
	 * 设备离线
	 */
	String DEVICE_OFFLINE = "999999";
	
	
	
	String NODE_NAME_NOT_EMPTY="NODE_NAME_NOT_EMPTY";
	String NODE_NAME_LENGTH="NODE_NAME_LENGTH";
	String NODE_DESC_LENGTH="NODE_DESC_LENGTH";
	String DEVEUI_NOT_EMPTY="DEVEUI_NOT_EMPTY";
	String DEVEUI_LENGTH="DEVEUI_LENGTH";
	String APPEUI_NOT_EMPTY="APPEUI_NOT_EMPTY";
	String APPEUI_LENGTH="APPEUI_LENGTH";
	String ACTIVATION_NOT_EMPTY="ACTIVATION_NOT_EMPTY";
	String ACTIVATION_LENGTH="ACTIVATION_LENGTH";
	String RECEIVE_DELAY_LENGTH="RECEIVE_DELAY_LENGTH";
	String RX1_DATARATE_OFFSET_LENGTH="RX1_DATARATE_OFFSET_LENGTH";
	String RX2_DATARATE_LENGTH="RX2_DATARATE_LENGTH";
	String APPSKEY_NOT_EMPTY="APPSKEY_NOT_EMPTY";
	String APPSKEY_LENGTH="APPSKEY_LENGTH";
	String NWKSKEY_NOT_EMPTY="NWKSKEY_NOT_EMPTY";
	String NWKSKEY_LENGTH="NWKSKEY_LENGTH";
	String DEVADDR_NOT_EMPTY="DEVADDR_NOT_EMPTY";
	String DEVADDR_LENGTH="DEVADDR_LENGTH";
	String MAX_DUTY_CYCLE_LENGTH="MAX_DUTY_CYCLE_LENGTH";
	String ACCESS_MODE_LENGTH="ACCESS_MODE_LENGTH";
	String DEVICE_INDEX_CODE_LENGTH="DEVICE_INDEX_CODE_LENGTH";

	String GATEWAY_ID_NOT_EMPTY="GATEWAY_ID_NOT_EMPTY";
	String GATEWAY_ID_LENGTH="GATEWAY_ID_LENGTH";

	String GATEWAY_NAME_NOT_EMPTY="GATEWAY_NAME_NOT_EMPTY";
	String GATEWAY_NAME_LENGTH="GATEWAY_NAME_LENGTH";


	String GATEWAY_DESC_LENGTH="GATEWAY_DESC_LENGTH";
	String ADDRESS_LENGTH="ADDRESS_LENGTH";
	String DOWNLINK_PORT_LENGTH="DOWNLINK_PORT_LENGTH";


	String ILLEGAL_OTAA_PARAM="ILLEGAL_OTAA_PARAM";
	String ILLEGAL_ABP_PARAM="ILLEGAL_ABP_PARAM";

	String ILLEGAL_GATEWAY_PARAM="ILLEGAL_GATEWAY_PARAM";


	String ILLEGAL_GATEWAY_UNIQUE="ILLEGAL_GATEWAY_UNIQUE";

}
