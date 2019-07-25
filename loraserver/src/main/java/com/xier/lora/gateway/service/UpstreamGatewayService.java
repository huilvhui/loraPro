package com.xier.lora.gateway.service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xier.lora.constant.IGeneralErrorCode;
import com.xier.lora.entity.LoraDataResp;
import com.xier.lora.entity.LoraDataResp.FrmData;
import com.xier.lora.entity.LoraNodeValResp;
import com.xier.lora.gateway.dto.BaseMessage;
import com.xier.lora.gateway.dto.DataMessage;
import com.xier.lora.gateway.dto.UpstreamData;
import com.xier.lora.gateway.dto.UpstreamData.RxpkData;
import com.xier.lora.gateway.memcache.entity.DeviceNodeInfo;
import com.xier.lora.gateway.memcache.entity.GatewayInfo;
import com.xier.lora.gateway.memcache.entity.NotifyMsg;
import com.xier.lorawan.enums.MType;
import com.xier.lorawan.key.SKeyGenorator;
import com.xier.lorawan.mac.EnumMacType;
import com.xier.lorawan.mac.MacCommandAntHandler;
import com.xier.lorawan.mac.MacReq;
import com.xier.lorawan.model.FHDR;
import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.model.PHYPayload;
import com.xier.lorawan.model.fctrl.FCtrlDownlink;
import com.xier.lorawan.model.message.JoinAccept;
import com.xier.lorawan.model.message.JoinRequest;
import com.xier.lorawan.model.message.MAC;
import com.xier.lorawan.model.message.Message;
import com.xier.lorawan.payload.Payload;
import com.xier.lorawan.payload.abp.MACDownLinkPayload;
import com.xier.lorawan.payload.abp.MACUpLinkPayload;
import com.xier.lorawan.payload.otaa.JoinAcceptPayload;
import com.xier.lorawan.payload.otaa.JoinRequestPayload;
import com.xier.lorawan.util.BinaryUtil;
import com.xier.lorawan.util.HexUtil;

public class UpstreamGatewayService extends AbstractLoraGatewayHandler{
	

	/**
	 * 设备节点信息校验
	 * @author lvhui5 2017年11月29日 下午8:22:24
	 * @param mtype lora消息类型
	 * @param message
	 * @param rxpkData 网关上报设备相关
	 * @return
	 */
	private LoraNodeValResp validateNode(MType mtype, Message message, BaseMessage baseMessage, RxpkData rxpkData) {
		LoraNodeValResp resp = new LoraNodeValResp();
		DeviceNodeInfo deviceNodeInfo = null;
		switch (mtype) {
			case JOIN_REQUEST:
				JoinRequest jq = (JoinRequest)message;
				deviceNodeInfo = deviceNodeGatewayService.getDeviceNodeByDevEUI(HexUtil.bytesToHexString(jq.getDevEUI()));
				if(deviceNodeInfo == null){
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "devEUI [" + HexUtil.bytesToHexString(jq.getDevEUI())
					        + "] not  exsit");
					break;
				}
				//节点网关更新
				deviceNodeInfo.updateGateway(baseMessage.getGateWayId(), rxpkData);
				// 校验appEUI 和 devEUI
				if (!BinaryUtil.equals(HexUtil.hexStringToBytes(deviceNodeInfo.getApplicationEUI()), jq.getAppEUI()) || !BinaryUtil.equals(HexUtil.hexStringToBytes(deviceNodeInfo.getDevEUI()), jq.getDevEUI())) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "appEUI " + HexUtil.bytesToHexString(jq.getAppEUI()) + " or devEUI [" + HexUtil.bytesToHexString(jq.getDevEUI()) + "] error");
					break;
				}
				if (!deviceNodeInfo.joinRequest(jq.getDevNonce())) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "devNonce [" + HexUtil.bytesToHexString(jq.getDevNonce()) + "] has exsit");
					break;
				}
				break;
			case UN_CONFIRMED_DATA_UP:
				MAC mac = (MAC)message;
				deviceNodeInfo = deviceNodeGatewayService.getDeviceNodeByDevAddr(HexUtil.bytesToHexString(mac.getFhdr()
				        .getDevAddr()));
				if (deviceNodeInfo == null) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR,
					        "devAddr [" +HexUtil.bytesToHexString(mac.getFhdr().getDevAddr()) + "] not  exsit");
					break;
				}
				//节点网关更新
				deviceNodeInfo.updateGateway(baseMessage.getGateWayId(), rxpkData);
				int fcnt = mac.getFhdr().getFcnt();
				if (!deviceNodeInfo.upLinkPayload(fcnt,mac.includeLinkcheckReq(),baseMessage.getGateWayId())) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "node [" + deviceNodeInfo.getDevAddr() + "] fcnt [" + fcnt + "] expired");
					break;
				}
				//占空比校验  不校验ack
				if( (!mac.isAck() || mac.macCommandInFrmPayload()) && !deviceNodeInfo.dutyCycleValid()){
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "dutyCycleValid  failed");
					break;
				}
				break;
			case CONFIRMED_DATA_UP:
				mac = (MAC)message;
				deviceNodeInfo = deviceNodeGatewayService.getDeviceNodeByDevAddr(HexUtil.bytesToHexString(mac.getFhdr()
				        .getDevAddr()));
				if (deviceNodeInfo == null) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR,
					        "devAddr [" + HexUtil.bytesToHexString(mac.getFhdr().getDevAddr()) + "] not  exsit");
					break;
				}
				//节点网关更新
				deviceNodeInfo.updateGateway(baseMessage.getGateWayId(), rxpkData);
				fcnt = mac.getFhdr().getFcnt();
				if (!deviceNodeInfo.upLinkPayload(fcnt,mac.includeLinkcheckReq(),baseMessage.getGateWayId())) {
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "node [" + deviceNodeInfo.getDevAddr() + "] fcnt [" + fcnt + "] expired");
					break;
				}
				//占空比校验
				if((!mac.isAck() || mac.macCommandInFrmPayload()) && !deviceNodeInfo.dutyCycleValid()){
					resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "dutyCycleValid  failed");
					break;
				}
				break;
			default:
				resp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "mtype not  exsit");
				break;
		}
		if (resp.isError()) {
			logger.error("-----------------node验证失败------------------");
			logger.error(resp.getErrorInfo());
			return resp;
		}
		resp.setDeviceNodeInfo(deviceNodeInfo);
		return resp;
	}
	/**
	 * mac命令回复执行
	 * 假如有多个ans的实现需要处理 可以传多个MacCommandAntHandler
	 * 可以实现解析多个命令的前提是命令内容的长度是一定的 Wrap
	 * @author lvhui5 2017年12月25日 下午8:09:51
	 * @param commands   mac命令存在fopts或者frmpayLoad中
	 * @param macCommandAntHandlers 处理mac命令回复的handler
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void macCommandeExcuteAns(byte[] commands,MacCommandAntHandler... macCommandAntHandlers){
		if(commands == null || commands.length == 0)
			return;
        ByteBuffer commandBuffer = ByteBuffer.wrap(commands);
        commandBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte type;
		try {
	        do{
	        	type = commandBuffer.get();
	        }while(EnumMacType.indexByValue(type) != null
	                && EnumMacType.indexByValue(type).getCommandAnt().newInstance().getCommandAnsAndNext(commandBuffer, macCommandAntHandlers));
        } catch (InstantiationException e) {
			logger.warn("macCommandeExcuteAns error",e);
        } catch (IllegalAccessException e) {
			logger.warn("macCommandeExcuteAns error",e);
        }
	}
	
	/**
	 * mic校验
	 * @author lvhui5 2017年11月30日 下午4:59:56
	 * @param mic 消息体的需要校验的mic
	 * @param payload 消息体
	 * @param key 计算mic的秘钥
	 * @return
	 */
	private boolean validateMic(byte[] mic, Payload payload, byte[] key) {
		// 重新生成MIC
		if(logger.isDebugEnabled()){
			logger.debug("服务端生成的MIC:[" + HexUtil.bytesToHexString(payload.reCalculateMic(key)) + "]");
		}
		// 比对两个MIC  不一致有可能需要重新做入网申请
		if (!BinaryUtil.equals(mic, payload.reCalculateMic(key))) {
			logger.error("-----------------mic验证失败------------------");
			return false;
		}
		return true;
	}
	
	@Override
	protected LoraDataResp msgHandler(BaseMessage message) {
		LoraDataResp loraDataResp = new LoraDataResp();
		DataMessage data = (DataMessage)message;
		if(data.getData() == null){
			logger.warn("DataMessage is null");
			return loraDataResp;
		}
		UpstreamData upstreamData = (UpstreamData)data.getData();
		GatewayInfo gatewayInfo = null;
		if(upstreamData.getStat() != null){
			gatewayInfo = deviceNodeGatewayService.updateGatewayStat(message.getGateWayId(),upstreamData.getStat());	
		}
		if (CollectionUtils.isEmpty(upstreamData.getRxpk())) {
			//loraDataResp.setErrorCode(IGeneralErrorCode.PARAMS_ERROR, "data is null");
			logger.warn("data rxpk is null");
			return loraDataResp;
		}
		//需要转发的消息集合
		List<FrmData> handlerData = new ArrayList<FrmData>();
		for (RxpkData rxpkData : upstreamData.getRxpk()) {
			if (rxpkData.getStat() == -1) {
				logger.error("-----------------stat验证失败------------------");
				/**
				 * 接收到的LORA帧的CRC验证结果：正确（1）、不正确（-1）、未校验（0）
				 */
				continue;
			}
			//fsk模式节点源数据处理
			if(rxpkData.isFskModu()){
				try {
	                handlerData.add(new FrmData(rxpkData.getData(),rxpkData.getData().getBytes("utf-8")));
                } catch (UnsupportedEncodingException e) {
                	logger.error("unsupportedEncoding",e);
                }
				continue;
			}
			String loraData = rxpkData.getData();
			//lora消息类型
			MType mtype = rxpkData.getMsgType();
			// 设备的节点前置校验下
			switch (mtype) {
				/**
				 * 入网申请处理流程：
				 * 1、解析入网申请lora消息体内容。
				 * 2、校验设备节点是否注册。
				 * 3、消息体内容篡改校验MIC。
				 * 4、组装入网接收下行消息。
				 * 5、下行消息发布。
				 */
				case JOIN_REQUEST:
					JoinRequestPayload requestPayload = new JoinRequestPayload(loraData);
					PHYPayload phyPayload = requestPayload.parseToPhyPayload();
					// 校验节点
					LoraNodeValResp resp = validateNode(mtype, phyPayload.getMessage(), message, rxpkData);
					if (resp.isError()) {
						continue;
					}
					if(logger.isDebugEnabled()){
						logger.debug("JOIN_REQUEST phyPayload :[{}]",phyPayload);
					}
					DeviceNodeInfo deviceNodeInfo = resp.getDeviceNodeInfo();
					// 校验mic
					if (!validateMic(phyPayload.getMic(), requestPayload,
					        HexUtil.hexStringToBytes(deviceNodeInfo.getApplicationKey()))) {
						continue;
					}
					/**************************** 开始组装JOIN_ACCEPT *******************************************/
					// 这些参数是服务端自行构造好的 有些可以到数据库拿
					byte[] appNonce = HexUtil.generalHexLength(3);// 随机3个字节
					byte[] devAddr = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(deviceNodeInfo.getDevAddr()), 4); // 随机唯一
					byte[] netId = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(deviceNodeInfo.getNetId()), 3);// 产生于服务器 network identifier
					byte[] dlSettings = {deviceNodeInfo.getJoinAcceptDlSetting()}; // server端配置在后台了
					byte[] rxDelay = {deviceNodeInfo.getReceiveDelay1().byteValue()}; // a delay between TX and RX server配置了
					byte[] cfList = new byte[0];// 目前中国统一是空
					JoinAccept joinAccept = new JoinAccept(appNonce, netId, devAddr, dlSettings, rxDelay, cfList);
					JoinAcceptPayload joinAcceptPayload = new JoinAcceptPayload(deviceNodeInfo.getApplicationKey(), new PHYPayload(MHDR.wrap(MType.JOIN_ACCEPT.getMhdrByte()),
					        joinAccept, null));
					String encrypt = joinAcceptPayload.encrypt();
					if(logger.isDebugEnabled()){
						logger.debug("经过计算后的request accept message 请发往设备进行解析:[{}]" , encrypt);
					}
					// 记录下appSKey,nwkSKey
					byte[] nwkSKey = SKeyGenorator.nwkSKey(HexUtil.hexStringToBytes(deviceNodeInfo.getApplicationKey()),
					        appNonce, netId, ((JoinRequest)phyPayload.getMessage()).getDevNonce());
					byte[] appSKey = SKeyGenorator.appSKey(HexUtil.hexStringToBytes(deviceNodeInfo.getApplicationKey()),
					        appNonce, netId, ((JoinRequest)phyPayload.getMessage()).getDevNonce());
					// 计算并更新nwkskey和appskey到设备节点
					deviceNodeGatewayService.deviceNodeJoinRequest(deviceNodeInfo, HexUtil.bytesToHexString(appSKey),
					        HexUtil.bytesToHexString(nwkSKey));
					DataMessage dataMessage = (DataMessage)message;
					DataMessage downlinkMsg = dataMessage.buildDownlinkMsg(encrypt, rxpkData, gatewayInfo,mtype, deviceNodeInfo, true,joinAcceptPayload.getLengthBeforeEncode());		
					deviceNodeInfo.sendMsg(downlinkMsg.toByteArray(),downlinkMsg.getToken());
					break;
					/**
					 * 二阶段正常上行消息处理流程：
					 * 1、解析lora消息体内容。
					 * 2、校验设备节点是否注册。
					 * 3、解密消息体消息frmPayload
					 * 4、消息体内容篡改校验MIC。  这里和加密的顺序对应，加密的顺序是 先加密frmPayload 再计算mic
					 * 5、数据速率适配。
					 * 6、mac命令处理。
					 * 7、下行消息发布。
					 */
				case UN_CONFIRMED_DATA_UP:
					MACUpLinkPayload macUpLinkPayload = new MACUpLinkPayload(loraData);
					// 校验节点
					resp = validateNode(mtype, macUpLinkPayload.getPhyPayload().getMessage(), message, rxpkData);
					if (resp.isError()) {
						continue;
					}
					if(logger.isDebugEnabled()){
						logger.debug("phyPayload :[{}]",macUpLinkPayload.getPhyPayload());
					}
					deviceNodeInfo = resp.getDeviceNodeInfo();
					MAC mac = (MAC)macUpLinkPayload.getPhyPayload().getMessage();
					//mac.getFhdr().getFctrl().getAdr()  上行的adr位处理
					if(logger.isDebugEnabled()){
						logger.debug("frmPayload :[{}]",HexUtil.bytesToHexString(macUpLinkPayload.decryptFRMPayload(HexUtil.hexStringToBytes(deviceNodeInfo.getAppsKey()),
    					        HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey()))));
					}
					// 校验mic
					if (!validateMic(macUpLinkPayload.getPhyPayload().getMic(), macUpLinkPayload,
					        HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey()))) {
						continue;
					}	
					//在这里把frmPayload解密掉
					mac.setFrmPayload(macUpLinkPayload.decryptFRMPayload(HexUtil.hexStringToBytes(deviceNodeInfo.getAppsKey()),
					        HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey())));
					if(deviceNodeInfo.isOpenADR()){
						//adr适配更新数据速率
						deviceNodeInfo.updateNodeDataRate(mac.drReduce(), rxpkData);
					}
					byte[] commands = null;
					if(mac.macCommandInFrmPayload()){
						commands = mac.getFrmPayload();
					}else{
						commands = mac.getFhdr().getFopts();
						//只分发非ack带的消息内容
						if(!mac.isAck()){
							try {
	                            handlerData.add(new FrmData(new String(mac.getFrmPayload(),deviceNodeInfo.getCharsetEncode()),mac.getFrmPayload()));
                            } catch (UnsupportedEncodingException e) {
                            	logger.warn("loraNodeDownlink error",e);
                            }
						}
					}
					//处理mac命令回复
					macCommandeExcuteAns(commands,deviceNodeInfo);
					try {
	                    loraNodeDownlink(deviceNodeInfo, message, rxpkData, gatewayInfo,mtype, false);
                    } catch (UnsupportedEncodingException e) {
						logger.warn("loraNodeDownlink error",e);
                    }
					break;
				case CONFIRMED_DATA_UP:
					macUpLinkPayload = new MACUpLinkPayload(loraData);
					// 校验节点
					resp = validateNode(mtype, macUpLinkPayload.getPhyPayload().getMessage(), message, rxpkData);
					deviceNodeInfo = resp.getDeviceNodeInfo();
					if (resp.isError()) {
						/**
						 * TODO  如果节点需要某些异常场景ack回复 可以直接下发ack
						 */
						//loraNodeDownlink(deviceNodeInfo, message, rxpkData, upstreamData.getStat(), mtype, true,resp.getErrorInfo());
						continue;
					}
					if(logger.isDebugEnabled()){
						logger.debug("phyPayload :[{}]",macUpLinkPayload.getPhyPayload());
					}
					//处理mac命令回复发布ack消息给网关 同时有下行消息要发布的情况下 fpending 发布下行消息 异步防止超时 
					mac = (MAC)macUpLinkPayload.getPhyPayload().getMessage();
					if(logger.isDebugEnabled()){
						logger.debug("frmPayload :[{}]",HexUtil.bytesToHexString(macUpLinkPayload.decryptFRMPayload(HexUtil.hexStringToBytes(deviceNodeInfo.getAppsKey()),
    					        HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey()))));
					}
					// 校验mic
					if (!validateMic(macUpLinkPayload.getPhyPayload().getMic(), macUpLinkPayload,
					        HexUtil.hexStringToBytes(resp.getDeviceNodeInfo().getNwksKey()))) {
						continue;
					}
					//在这里把frmPayload解密掉
					mac.setFrmPayload(macUpLinkPayload.decryptFRMPayload(HexUtil.hexStringToBytes(deviceNodeInfo.getAppsKey()),
					        HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey())));
					if(deviceNodeInfo.isOpenADR()){
						//adr适配更新数据速率
						deviceNodeInfo.updateNodeDataRate(mac.drReduce(), rxpkData);
					}
					commands = null;
					if(mac.macCommandInFrmPayload()){
						commands = mac.getFrmPayload();
					}else{
						commands = mac.getFhdr().getFopts();
						if(!mac.isAck()){
							try {
	                            handlerData.add(new FrmData(new String(mac.getFrmPayload(),deviceNodeInfo.getCharsetEncode()),mac.getFrmPayload()));
                            } catch (UnsupportedEncodingException e) {
        						logger.warn("loraNodeDownlink error",e);
                            }
						}
					}
					//处理mac命令回复
					macCommandeExcuteAns(commands,deviceNodeInfo);
					try {
	                    loraNodeDownlink(deviceNodeInfo, message, rxpkData, gatewayInfo, mtype, true);
                    } catch (UnsupportedEncodingException e) {
						logger.warn("loraNodeDownlink error",e);
                    }
					break;
				default:
					break;
			}
		}
		loraDataResp.setData(handlerData);
		return loraDataResp;
	}
	
	/**
	 * 普通消息网关下行发布
	 * unconfirm  没有下行不发消息 ack置0       有下行消息rx2下发 ack置0
	 * confirm    没有下行rx1下发ack置1          有下行消息rx2下发 ack置1
	 * fpending 表示还有消息要下发
	 * 
	 * TODO 小端下发字节
	 * @author lvhui5 2017年12月4日 上午11:03:31
	 * @param deviceNodeInfo
	 * @param message
	 * @param rxpkData
	 * @param mtype
	 * @param confirm
	 * @throws UnsupportedEncodingException 
	 */
	private void loraNodeDownlink(DeviceNodeInfo deviceNodeInfo, BaseMessage message, RxpkData rxpkData, GatewayInfo gatewayInfo,MType mtype,
	        boolean confirm) throws UnsupportedEncodingException {
		List<NotifyMsg> downlinkMsgs  = deviceNodeInfo.messageToSend();
		MacReq[] macReqs = deviceNodeInfo.drainMacQueue();
		DataMessage dataMessage = (DataMessage)message;
		//是否有下行消息需要发布或者 有mac命令需要下发
		if(confirm){
			if(CollectionUtils.isEmpty(downlinkMsgs) && (macReqs == null || macReqs.length == 0)){
				Object[] downLinkPHYPayload = buildDownLinkPHYPayload(deviceNodeInfo,false,null,macReqs,false,true,false);
				// 确认返回ack消息
				DataMessage downlinkMsg = dataMessage.buildDownlinkMsg(
						(String)downLinkPHYPayload[0], rxpkData,gatewayInfo, mtype, deviceNodeInfo, true,(int)downLinkPHYPayload[1]);
				System.out.println("downlinkMsg=====================protrolVer =" + downlinkMsg.getProtrolVer() + " token=" + downlinkMsg.getToken()+ " msgType=" + downlinkMsg.getMsgType() + " gatewayId=" + downlinkMsg.getGateWayId());
				deviceNodeInfo.sendMsg(downlinkMsg.toByteArray(),downlinkMsg.getToken());
			}else{
				// 确认返回fpedning ack消息(需要设备节点打开下行窗口)
				try {
					Object[] downLinkPHYPayload = buildDownLinkPHYPayload(deviceNodeInfo,false,CollectionUtils.isEmpty(downlinkMsgs)?null:downlinkMsgs.get(0).getDownlinkMsg(),macReqs,!CollectionUtils.isEmpty(downlinkMsgs),true,CollectionUtils.isEmpty(downlinkMsgs)?false:downlinkMsgs.get(0).needNodeConfirm());
					DataMessage downlinkMsg = dataMessage.buildDownlinkMsg(
                    		//下发mac命令
                    		(String)downLinkPHYPayload[0], rxpkData, gatewayInfo,mtype, deviceNodeInfo, CollectionUtils.isEmpty(downlinkMsgs)?false:downlinkMsgs.get(0).isRx1(),(int)downLinkPHYPayload[1]);
					System.out.println("downlinkMsg=====================protrolVer =" + downlinkMsg.getProtrolVer() + " token=" + downlinkMsg.getToken()+ " msgType=" + downlinkMsg.getMsgType() + " gatewayId=" + downlinkMsg.getGateWayId());
					deviceNodeInfo.sendMsg(downlinkMsg.toByteArray(),downlinkMsg.getToken());
                } catch (UnsupportedEncodingException e) {
					logger.warn("loraNodeDownlink error",e);
                }
			}
		}else{
			if(CollectionUtils.isEmpty(downlinkMsgs) && (macReqs == null || macReqs.length == 0)){
				return;
			}else{
				// 确认返回fpedning ack消息(需要设备节点打开下行窗口)
				try {
					Object[] downLinkPHYPayload = buildDownLinkPHYPayload(deviceNodeInfo,false,CollectionUtils.isEmpty(downlinkMsgs)?null:downlinkMsgs.get(0).getDownlinkMsg(),macReqs,!CollectionUtils.isEmpty(downlinkMsgs), false,CollectionUtils.isEmpty(downlinkMsgs)?false:downlinkMsgs.get(0).needNodeConfirm());
					DataMessage downlinkMsg = dataMessage.buildDownlinkMsg(
                    		//下发mac命令
                    		(String)downLinkPHYPayload[0], rxpkData, gatewayInfo,mtype, deviceNodeInfo, CollectionUtils.isEmpty(downlinkMsgs)?false:downlinkMsgs.get(0).isRx1(),(int)downLinkPHYPayload[1]);
					System.out.println("downlinkMsg=====================protrolVer =" + downlinkMsg.getProtrolVer() + " token=" + downlinkMsg.getToken()+ " msgType=" + downlinkMsg.getMsgType() + " gatewayId=" + downlinkMsg.getGateWayId());
					deviceNodeInfo.sendMsg(downlinkMsg.toByteArray(),downlinkMsg.getToken());
                } catch (UnsupportedEncodingException e) {
					logger.warn("loraNodeDownlink error",e);
                }
			}
		}
	}

	/**
	 * 构造一个下行消息
	 * @author lvhui5 2017年12月7日 下午3:08:23
	 * @param deviceNodeInfo
	 * @param macInFrm  mac命令是否放入frmpayload
	 * @param data
	 * @param fpending 是否需要发布下行消息
	 * @param ack      是否ack消息
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
    public Object[] buildDownLinkPHYPayload(DeviceNodeInfo deviceNodeInfo, boolean macInFrm,String data,MacReq[] macReqs,boolean fpending,
	        boolean ack,boolean confirm) throws UnsupportedEncodingException{
  
    	//一条MAC命令由一个字节的命令ID（CID）和特定的命令序列组成，命令序列可以是空
    	FHDR fhdr = macInFrm?new FHDR(HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(deviceNodeInfo.getDevAddr()), 4), new FCtrlDownlink(ack,fpending,deviceNodeInfo.isOpenADR()),
		        (short)deviceNodeInfo.downLinkFcnt()):new FHDR(HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(deviceNodeInfo.getDevAddr()), 4), new FCtrlDownlink(ack,fpending,deviceNodeInfo.isOpenADR()),
				        (short)deviceNodeInfo.downLinkFcnt(), macReqs);// 报文头
    	// 构造一个UN_CONFIRMED_DATA_DOWN 0110 0000  所有的下行消息目前都是UN_CONFIRMED类型  不支持针对节点重发控制功能
    	PHYPayload phyPayload = new PHYPayload(confirm?MType.CONFIRMED_DATA_DOWN:MType.UN_CONFIRMED_DATA_DOWN, macInFrm?new MAC(fhdr,macReqs):new MAC(fhdr,data == null?null:data.getBytes(deviceNodeInfo.getCharsetEncode())));
		if(logger.isDebugEnabled()){
			logger.debug("downLink phyPayload :[{}]",phyPayload.toString());
		}
		MACDownLinkPayload macDownLinkPayload = new MACDownLinkPayload(phyPayload);
		Object[] result = new Object[2];
		result[0] = macDownLinkPayload.encrypt(HexUtil.hexStringToBytes(deviceNodeInfo.getAppsKey()), HexUtil.hexStringToBytes(deviceNodeInfo.getNwksKey()));
		result[1] = macDownLinkPayload.getLengthBeforeEncode();
		return result;
    }
	
	
}
