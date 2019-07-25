package com.xier.lora.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import com.xier.lora.base.service.SpringContext;
import com.xier.lora.constant.EnumMsgType;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;

/**
 * lora网关上报消息分发处理接口
 * <p>
 * 主要功能：
 * 1、上报消息处理
 * 
 * </p>
 * @author lvhui5 2017年11月21日 上午11:08:51
 * @version V1.0
 */
public interface LoraGatewayDispatcherService {
	
	Logger logger = LoggerFactory.getLogger(LoraGatewayDispatcherService.class);
	DeviceNodeGatewayService deviceNodeGatewayService = SpringContext.getBean("deviceNodeGatewayService",DeviceNodeGatewayService.class);
	TaskExecutor taskExecutor = SpringContext.getBean("taskExecutor",TaskExecutor.class);
	
	/**
	 * 
	 * @author lvhui5 2017年12月13日 下午10:43:14
	 * @param message  源消息
	 * @param msgType  处理的消息类型
	 * @return
	 */
	public LoraDataResp gatewayMsgDispatch(BaseMessage message,EnumMsgType handlerMsgType);
}
