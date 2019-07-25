package com.xier.lora.gateway.service;

import com.xier.lora.gateway.dto.BaseMessage;
import com.xier.lora.gateway.dto.UpstreamData.RxpkData;
import com.xier.lora.gateway.memcache.entity.DeviceNodeInfo;
import com.xier.lora.gateway.memcache.entity.GatewayInfo;
import com.xier.lorawan.enums.MType;


/**
 * 网关消息适配
 * <p>
 * 转换byte[]消息用来发布给网关
 * </p>
 * @author lvhui5 2017年11月21日 下午6:44:17
 * @version V1.0
 */
public interface GatawayMessageAdaptor {

	/**
	 * 构造一个返回message
	 * 目前ack是不能批量下发的
	 * 组装网关下行需要几个要素：
	 * 1、设备节点信息(设备rx1，rx2时间延迟)
	 * 2、消息类型(入网申请或者正常消息上行)
	 * 3、上行消息(起始时间点)
	 * @author lvhui5 2017年11月30日 下午8:45:32
	 * @param loraData
	 * @param data
	 * @param mtype 上行mtype
	 * @param deviceNodeInfo
	 * @param ack  是否ack消息
	 * <p>
	 * ack消息在节点rx1窗口下发，对应delay1
	 * 下行消息在节点rx2窗口下发，对应delay2
	 * </p>
	 * @return
	 */
	public BaseMessage  buildDownlinkMsg(String loraData,RxpkData data,GatewayInfo gatewayInfo,MType mtype,DeviceNodeInfo deviceNodeInfo, boolean ack,int frmPayloadSize);
}
