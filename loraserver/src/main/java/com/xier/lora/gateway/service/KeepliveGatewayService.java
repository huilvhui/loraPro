package com.xier.lora.gateway.service;

import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;


/**
 * 心跳消息
 * <p>
 * 网关上报心跳
 * </p>
 * @author lvhui5 2017年11月21日 下午3:55:04
 * @version V1.0
 */
public class KeepliveGatewayService extends AbstractLoraGatewayHandler{

	@Override
	protected LoraDataResp msgHandler(BaseMessage message) {
		LoraDataResp loraDataResp  = new LoraDataResp();
		//deviceNodeGatewayService.uplinkGatewayAliveUpdate(message.getGateWayId());
		return loraDataResp;
    }
}
