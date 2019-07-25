package com.xier.lora.gateway.service;

import com.xier.lora.constant.EnumMsgType;
import com.xier.lora.constant.IGeneralErrorCode;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;
import com.xier.lora.gateway.memcache.entity.GatewayInfo;
import com.xier.lora.server.source.LoraServerSource;

/**
 * 通用网关消息处理
 * <p>
 * 主要用于返回消息给网关
 * </p>
 * @author lvhui5 2017年11月21日 上午11:18:46
 * @version V1.0
 */
public class CommonReturnGatewayService implements LoraGatewayDispatcherService{

	@Override
	public LoraDataResp gatewayMsgDispatch(BaseMessage message,EnumMsgType handlerMsgType){
		LoraDataResp resp = new LoraDataResp();
		/**
		 * TODO 处理下心跳回调
		 */
		GatewayInfo gatewayInfo = deviceNodeGatewayService.getGatawayByGatewayId(message.getGateWayId());
		if(gatewayInfo == null){
			resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "gatewayId  [" + message.getGateWayId() + "] not exsit");
			return resp;
		}
		//给网关的回复往上行的网关发
		//LoraServerSource.sendMsg(message.buildMsg(true), gatewayInfo.getAddress(), gatewayInfo.getDownlinkPort());
		LoraServerSource.sendDirectMsg(message.buildMsg(true).toByteArray(), gatewayInfo.getAddress(), gatewayInfo.getUplinkPort());
		return new LoraDataResp();
    }	
	
}
