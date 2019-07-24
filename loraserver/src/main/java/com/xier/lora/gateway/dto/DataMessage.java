package com.xier.lora.gateway.dto;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import com.xier.lora.constant.EnumDataRate;
import com.xier.lora.gateway.dto.DownstreamData.TxpkData;
import com.xier.lora.gateway.dto.UpstreamData.RxpkData;
import com.xier.lora.gateway.memcache.entity.DeviceNodeInfo;
import com.xier.lora.gateway.memcache.entity.GatewayInfo;
import com.xier.lora.gateway.service.GatawayMessageAdaptor;
import com.xier.lora.util.DateUtil;
import com.xier.lora.util.JsonUtils;
import com.xier.lorawan.enums.MType;

/**
 * Json数据消息
 * <p>
 * 网关的上行消息：分为网关状态与节点相关以json格式上报
 * </p>
 * @author lvhui5 2017年11月20日 下午6:07:03
 * @version V1.0
 */
public class DataMessage extends BaseMessage implements GatawayMessageAdaptor{
	
	public DataMessage(BaseData data,Integer protrolVer, String token, Integer msgType, String gateWayId) {
		super(protrolVer, token, msgType, gateWayId);
		this.data = data;
	}
	
	public DataMessage(GatewayData gateWayData) {
		super(gateWayData);
		if(getTextByteBuffer().array().length > 12){
			byte[] dataByte = new byte[getTextByteBuffer().array().length-12];
			getTextByteBuffer().get(dataByte);
			String msgData;
            try {
	            msgData = new String(dataByte,"utf-8");
            } catch (UnsupportedEncodingException e) {
            	throw new IllegalArgumentException("data encode error");
            }
			try {
	            // 网关报文的json消息体
	            this.data = JsonUtils.deserialize(msgData, msgType().getDtoClass());
            } catch (Exception e) {
            	//do nothing
            }
		}
	}
	
	
	private BaseData data;
	
	public BaseData getData() {
		return data;
	}
	
	public void setData(BaseData data) {
		this.data = data;
	}
	/**
	 * 用于class c的下行消息组装
	 * @author lvhui5 2018年3月7日 下午4:04:40
	 * @return
	 */
	public DataMessage buildDownlinkMsg(String loraData, RxpkData data, GatewayInfo gatewayInfo,DeviceNodeInfo deviceNodeInfo, boolean rx1,int frmPayloadSize){
		/**
		 * TODO
		 */
		return null;
	}

	public DataMessage buildDownlinkMsg(String loraData, RxpkData data, GatewayInfo gatewayInfo,MType mtype, DeviceNodeInfo deviceNodeInfo, boolean rx1,int frmPayloadSize) {
		DownstreamData downstreamData = new DownstreamData();
		TxpkData td = new TxpkData();
		// lora消息体
		td.setData(loraData);
		td.setImme(false);
		// 射频点
		td.setRfch(data.getRfch());
		// ECC编码率 4/5
		td.setCodr(data.getCodr());
		//td.setSize(loraData.length());
		td.setSize(frmPayloadSize);
		//tmst打开下行窗口时间，为了确保在窗口打开前把消息发布到网关，下行使用节点的rx2窗口
		switch (mtype) {
			case JOIN_REQUEST:
				// rx1 ack接受窗口在1秒后打开
				td.setTmst(DateUtil.getRelativeMicroSecond(data.getTmst(),
						rx1?deviceNodeInfo.getJoinAcceptDelay1():deviceNodeInfo.getJoinAcceptDelay2()));
				break;
			case CONFIRMED_DATA_UP:
				// rx1 ack接受窗口在1秒后打开
				td.setTmst(DateUtil.getRelativeMicroSecond(
				        data.getTmst(),rx1?deviceNodeInfo.getReceiveDelay1():deviceNodeInfo.getRecieveDelay2()));
				break;
			case UN_CONFIRMED_DATA_UP:
				// rx1 ack接受窗口在1秒后打开
				td.setTmst(DateUtil.getRelativeMicroSecond(
				        data.getTmst(),rx1?deviceNodeInfo.getReceiveDelay1():deviceNodeInfo.getRecieveDelay2()));
				break;
			default:
				throw new IllegalArgumentException("mtype [" + mtype.getDir()+ "] error");
		}
		//数据速率
		if(rx1){ 
			//rx1消息不做adr适配,做上行的速率的偏移
			//上行的数据速率从网关的datr取
			td.setDatr(EnumDataRate.offsetDataRate(data.getDatr(),deviceNodeInfo.getRX1dataRateOffset()).getDatr());
			if(gatewayInfo != null && gatewayInfo.getRx1FreStart() != null && gatewayInfo.getTxFreStart() != null){
				// 频率 计算规则 ： 下行初始频率 + （上行频率 - 上行初始频率）
				BigDecimal freq = new BigDecimal(gatewayInfo.getRx1FreStart()+(data.getFreq() - gatewayInfo.getTxFreStart())).setScale(6, BigDecimal.ROUND_HALF_DOWN);
				td.setFreq(freq.doubleValue());	
			}else{
				//没有stat用上行的freq
				td.setFreq(data.getFreq());
			}
		}else{
			//下行消息做adr适配 取当前的数据速率
			td.setDatr(deviceNodeInfo.getRX2dataRate().getDatr());
			if(gatewayInfo.getRx2Fre() != null){
				// 频率  固定频率
				td.setFreq(gatewayInfo.getRx2Fre());	
			}else{
				//没有stat用上行的freq
				td.setFreq(data.getFreq());
			}
		}
		downstreamData.setTxpk(td);
		BaseMessage baseMsg = buildMsg(false);
		DataMessage dataMessage = new DataMessage(downstreamData, baseMsg.getProtrolVer(), baseMsg.getToken(), baseMsg.getMsgType(), baseMsg.getGateWayId());
		return dataMessage;
	}
	

	public byte[] toByteArray(){
		byte[] dataStr = new byte[0];
        try {
        	/**
        	 * TODO
        	 */
	        dataStr = JsonUtils.toJson(this.getData()).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
        } 
        //网关的消息体假如会包含中文也需要约定编码格式
		byte[] baseMsgByte = super.toByteArray();
		byte[] returnByte = new byte[baseMsgByte.length + dataStr.length];
		for(int i = 0; i< returnByte.length ; i++){
			if(i < baseMsgByte.length){
				returnByte[i] = baseMsgByte[i];	
			}else{
				returnByte[i] = dataStr[i-baseMsgByte.length];
			}
		}
		return returnByte;
	}
	
}
