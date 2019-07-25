package com.xier.lora.gateway.service;

import com.xier.lora.constant.EnumMsgType;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;

/**
 * 下行数据回复处理
 * <p>
 * 这里有个问题，下行回复数据主要用来确认下行数据是否处理，假如是异步，怎么确认执行情况
 * 
 * </p>
 * @author lvhui5 2017年11月21日 上午11:18:46
 * @version V1.0
 */
public class DownstreamGatewayService  implements LoraGatewayDispatcherService{

	@Override
    public LoraDataResp gatewayMsgDispatch(BaseMessage message, EnumMsgType handlerMsgType) {
		//do nothing
	    return new LoraDataResp();
    }


	
}
