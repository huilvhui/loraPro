package com.xier.lora.gateway.service;

import com.xier.lora.constant.EnumGatewayError;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;
import com.xier.lora.gateway.dto.DataMessage;
import com.xier.lora.gateway.dto.DownstreamAck;
import com.xier.lora.retransmit.service.DownlinkMsgService;

/**
 * 下行数据回复处理
 * <p>
 * 下行的token生成
 * 
 * </p>
 * @author lvhui5 2017年11月21日 上午11:18:46
 * @version V1.0
 */
public class DownstreamAckGatewayService extends AbstractLoraGatewayHandler{


	@Override
    protected LoraDataResp msgHandler(BaseMessage message) {
		if(logger.isDebugEnabled()){
			logger.debug("receive gateway ack, gatewayId:[{}], token:[{}]",message.getGateWayId(),message.getToken());
		}
		//下行回复确认
		DownlinkMsgService.receiveDownLinkAck(message.getGateWayId(), message.getToken());
		LoraDataResp resp = new LoraDataResp();
		DataMessage data = (DataMessage)message;
		DownstreamAck downstreamAck = (DownstreamAck)data.getData();
		if(downstreamAck != null && downstreamAck.isError()){
			logger.error("downstream return error : [{}]",EnumGatewayError.indexByValue(downstreamAck.getError()).getDescription());
			return resp;
		}
		return resp;
    }

	

	
}
