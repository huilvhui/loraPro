package com.xier.lora.gateway.service;

import com.xier.lora.constant.EnumMsgType;
import com.xier.lora.constant.IGeneralErrorCode;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.gateway.dto.BaseMessage;


public abstract class AbstractLoraGatewayHandler implements LoraGatewayDispatcherService{
	

	@Override
    public LoraDataResp gatewayMsgDispatch(BaseMessage message,EnumMsgType msgType) {
		LoraDataResp resp = new LoraDataResp();
		//网关是否注册
		if(!deviceNodeGatewayService.gatewayValidate(message.getGateWayId(),message.getGateWayData())){
			resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "gateway [" + message.getGateWayId() + "] not register or address error");
			return resp;
		}
		//保持网关在线状态
		deviceNodeGatewayService.uplinkGatewayAliveUpdate(message.getGateWayId());
		/**
		 *返回接收确认消息给网关
		 */
		if(msgType.getCallBack()!= null){
			//异步处理下
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					msgType.getCallBack().getMsgHandler().gatewayMsgDispatch(message,msgType.getCallBack());
				}
			});
		}
		try {
	        return msgHandler(message);
        } catch (Exception e) {
        	logger.error("msgHandler error", e);
        	resp.setErrorCode(IGeneralErrorCode.UN_KNOWN_ERROR, e.getMessage());
        	return resp;
        }
    }
	
	/**
	 * 消息体处理
	 * @author lvhui5 2017年11月21日 下午3:44:14
	 * @param message
	 */
	protected abstract LoraDataResp msgHandler(BaseMessage message);
	
}
