package com.xier.lora.sys.pub.service;

import com.xier.lora.sys.pub.api.loraserver.dto.DeviceNodeResp;
import com.xier.lora.sys.pub.api.loraserver.dto.GatewayResp;

/**
 * 提供给loraServer的内部接口
 * <p></p>
 * @author lvhui5 2017年12月13日 上午10:01:00
 * @version V1.0
 */
public interface ILoraServerPubService {
	
	/**
	 * 根据devAddr 查询设备节点  loraserver设备消息验证业务需要
	 * @author lvhui5 2017年12月13日 上午10:13:47
	 * @param devAddr
	 * @return
	 */
	public DeviceNodeResp  getDeviceNodeByDevAddr(String devAddr);
	/**
	 * 根据devEUI 查询设备节点  loraserver设备入网验证业务需要
	 * @author lvhui5 2017年12月13日 上午10:14:37
	 * @param devEUI
	 * @return
	 */
	public DeviceNodeResp  getDeviceNodeByDevEUI(String devEUI);
	
	/**
	 * 根据gatewayId查询注册网关 接入网关消息业务需要
	 * @author lvhui5 2017年12月13日 上午10:18:07
	 * @param gatewayId
	 * @return
	 */
	public GatewayResp  getGatewayByGatewayId(String gatewayId);
	
	
	/**
	 * 入网申请成功更新会话秘钥信息  支持服务断电保存
	 * @author lvhui5 2017年12月13日 上午10:51:14
	 * @param devAddr
	 * @param appSKey
	 * @param nwkSkey
	 * @return
	 */
	public DeviceNodeResp updateDeviceNode(String devAddr, String appSKey, String nwkSkey);
	

}
