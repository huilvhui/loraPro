package com.xier.lora.sys.pub.service;

import com.xier.lora.sys.pub.api.loraserver.dto.DevideNodeReq;



/**
 * 设备管理调用接口(loraServer提供接口)
 * <p></p>
 * @author lvhui5 2017年12月13日 下午1:43:55
 * @version V1.0
 */
public interface ILoraManageNotifyPubService {
	
	/**
	 * 移除设备节点通知loraServer
	 * @author lvhui5 2017年12月13日 下午1:46:29
	 * @param devAddr
	 * @return
	 */
	public void removeDeviceNode(String devAddr);
	
	/**
	 * 移除注册网关通知loraServer
	 * @author lvhui5 2017年12月13日 下午1:46:41
	 * @param gatewayId
	 * @return
	 */
	public void removeGateway(String gatewayId);
	
	/**
	 * 节点更新通知loraServer
	 * @author lvhui5 2017年12月21日 下午4:42:19
	 * @param devAddr
	 */
	public void updateDeviceNode(DevideNodeReq devAddr);
}
