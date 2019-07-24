package com.xier.lora.gateway.memcache.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.LongUnaryOperator;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.xier.lora.constant.DictCodeConstants;
import com.xier.lora.constant.EnumAccessMode;
import com.xier.lora.constant.EnumDataRate;
import com.xier.lora.constant.EnumMaxDutyCycle;
import com.xier.lora.constant.EnumTXPower;
import com.xier.lora.gateway.dto.UpstreamData.RxpkData;
import com.xier.lora.gateway.service.DeviceNodeGatewayService;
import com.xier.lora.retransmit.service.DownlinkMsgService;
import com.xier.lora.server.source.LoraServerSource;
import com.xier.lora.sys.pub.api.loraserver.dto.DeviceNodeResp;
import com.xier.lora.sys.pub.api.loraserver.dto.GatewayResp;
import com.xier.lora.util.BeanUtil;
import com.xier.lora.util.StringUtils;
import com.xier.lorawan.mac.DevStatusReq;
import com.xier.lorawan.mac.DutyCycleReq;
import com.xier.lorawan.mac.LinkADRReq;
import com.xier.lorawan.mac.LinkCheckAnsReq;
import com.xier.lorawan.mac.MacCommandAntHandler;
import com.xier.lorawan.mac.MacReq;
import com.xier.lorawan.util.HexUtil;

/**
 * 设备节点缓存信息
 * <p>
 * 用于维护 1、设备基本信息 2、设备连接网关状态（用于网关选举） 3、设备下行消息队列
 * </p>
 * @author lvhui5 2017年11月20日 下午4:14:44
 * @version V1.0
 */
public class DeviceNodeInfo extends BaseDomain implements MacCommandAntHandler{
	
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 7521802270720957158L;
	/**
	 * 节点心跳发送频率
	 */
	private static final long nodeAliveRate = 1000*60*10;
	/**
	 * 设备类型编号
	 */
	private String deviceTypeCode;
	/**
	 * 设备编号
	 */
	private String deviceIndexCode;
	
	/**
	 * 中文字符串编码类型
	 */
	private String charsetEncode = "gb2312";

	/**
	 * 接入模式
	 * 默认classA
	 */
	private EnumAccessMode accessMode = EnumAccessMode.CLASS_A;
	
	/**
	 * 接入方式
	 */
	private String activation;
	/**
	 * 设备标识
	 */
	private String devEUI;
	
	/**
	 * 申请入网使用过的devNonce 由设备随机生成
	 */
	private List<byte[]> devNonce = new ArrayList<byte[]>();;
	/**
	 * 入网申请计数
	 */
	private long joinRequestCount = 0;
	/**
	 * 上行消息计数
	 */
	private long upLinkPayloadCount = 0;

	/**
	 * OTAA设备入网秘钥 appkey
	 */
	private String applicationKey;
	/**
	 * 消息会话秘钥
	 */
	private String appsKey;
	/**
	 * 消息会话与mic计算秘钥
	 */
	private String nwksKey;
	/**
	 * 应用标识 appEUI
	 */
	private String applicationEUI;
	/**
	 * 可配置 rx1初始数据速率配偏移量 参考ENUMDR offset上行速率偏移量
	 *  RX1DRoffset代表终端设备在第一个接收时隙（RX1）进行通信的下发包数据速率与其上传包数据速率的偏差。缺省为0 这个偏差用来考虑基站在某些地区因为很大的功率密度限制，平衡上传包与下发包的链路差别。这种偏差与地域有关。
	 */
	private Integer RX1dataRateOffset = 0;
	/**
	 * 可配置 rx2初始数据速率
	 */
	private EnumDataRate RX2dataRate = EnumDataRate.DR0;
	
	/**
	 * 发送给节点的最新速率
	 */
	private EnumDataRate nodeDataRate = EnumDataRate.DR0;
	
	/**
	 * 开启adr标志位  可扩展为节点配置项
	 */
	private boolean openADR = true;
	
	/**
	 * 运营商分配给每个模块节点，用于在otaa入网接受中构造devAddr和netId。
	 * 3个字节  6位
	 */
	private String netId;
	/**
	 * 运营商分配给每个模块节点，用于在otaa入网接受中构造devAddr
	 * Bit#                        [31..25]                 [24..0]
     * DevAddr bits               NwkID                    NwkAddr	
     * NwkID  为netId低7位  
     * 4个字节  8位
	 */
	private String devAddr;
	
	/**
	 * ack接收创建窗口延迟时间
	 */
	private Integer receiveDelay1 = 1;
	/**
	 * 下行接收窗口延迟时间
	 */
	private Integer recieveDelay2 = 2;
	/**
	 * 入网申请ack接收窗口延迟时间
	 */
	private Integer joinAcceptDelay1 = 5;
	/**
	 * 入网申请下行接收窗口延迟时间
	 */
	private Integer joinAcceptDelay2 = 6;
	/**
	 * 最近的节点上报时间（用于做最大占空比数据过滤）
	 */
	private long lastReceiveTime = System.currentTimeMillis();
	/**
	 * 下行消息队列 节点上行消息打开窗口后发送
	 */
	private BlockingQueue<NotifyMsg> downlinkQueue = new LinkedBlockingQueue<NotifyMsg>(100);
	/**
	 * 设备网关信息 gatewayId:GatewayInfo
	 */
	private Map<String, NodeGatewayInfo> ngrs = new ConcurrentHashMap<String, NodeGatewayInfo>();
	
	private ReentrantReadWriteLock fcntLock = new ReentrantReadWriteLock();
	
	private ReadLock fcntReadLock = fcntLock.readLock();
	
	private WriteLock fcntWriteLock = fcntLock.writeLock();
	
	private ReentrantReadWriteLock voteGatewayIdLock = new ReentrantReadWriteLock();
	
	private WriteLock voteGatewayIdWriteLock = voteGatewayIdLock.writeLock();
	
	private ReadLock voteGatewayIdReadLock = voteGatewayIdLock.readLock();
	
	private ReentrantReadWriteLock dateRateAdrLock = new ReentrantReadWriteLock();
	
	private WriteLock dateRateWriteLock = dateRateAdrLock.writeLock();
	
	private ReadLock dateRateReadLock = dateRateAdrLock.readLock();

	
	/**
	 * 
	 * 节点发送消息时间（默认为10ms）  单位毫秒
	 * 用于计算占空比 DutyCycle
	 * 发射占空比的最大值
	 * 总发射占空比=1/2的MaxDCycle次方
	 * 在没有区域调节设置占空比限制的情况下，使用 0 表示“占空比没有限制”
	 * 最大占空比
	 * 默认不做最大占空比限制
	 * 配置为0~15的整数
	 */
	private EnumMaxDutyCycle maxDutyCycle = EnumMaxDutyCycle.MDC0;
	
	/**
	 * 返回节点占空比，计算公式nodeSendTime/nodeSendCycle
	 * 例如：470频段占空比 1%，节点使用该频段发送一组数据耗时 10 ms，那么这个节点的本次发送周期为1000。节点在本周期结束，也就是 1000ms−10 ms 以后才可以再次发送数据
	 * mac命令既可以放在FOpts中和正常数据一起发送；也可以放在FRMPayload中单独发送，此时FPort = 0，但不能同时在两个字段携带MAC命令
	 * MaxDutyCycle有效范围[0:15]。在没有区域调节设置占空比限制的情况下，使用 0 表示“占空比没有限制”，有区域限制的除外
	 * @author lvhui5 2017年12月18日 上午10:35:34
	 */
	public EnumMaxDutyCycle getMaxDutyCycle(){
		return maxDutyCycle;
	}
	
	
	/**
	 * 节点发送消息时间，用于做节点占空比的控制 单位  毫秒
	 * 不同速率,不同负载长度发送时间是不一样的.限制占空比目的是为了不让一个节点长期占用无线信道,导致其他节点无法传输
	 * 这个值不开放配置可以设置的小点，因为设置越大，过滤的范围越大，系统已经有去重的部分
	 */
	private Integer nodeSendTime = 1;
	
	/**
	 * 占空比控制的节点消息时间范围   毫秒
	 */
	private Integer  dutyCycleRange = 0;
	
	/**
	 * 网关到服务消息传输的最长时间 用于计算MAX_FCNT_DROP 单位是秒 
	 * 
	 */
	private static final Integer gatewaySendTime = 5;
	/**
	 * 消息发布的频率 每秒
	 */
	private Integer sendRate = 1;
	
	
	/**
	 * 考虑到上行计数器重置和32位帧计数器低有效位的情况
	 * 假如本地计数值-上行的计数器  >= MAX_FCNT_DROP 则认为节点的计数器已重置
	 * 比如：消息发送的间隔是每秒一次，网关到服务最长消息传输时间是10秒 ，则该值设置为11较为合适
	 * 该值的计算方式是：高峰期每秒消息发送次数*网关到服务最长消息传输时间 +X
	 * X取决于网关数据传输的稳定性 太大有可能会影响重置后的消息接收
	 * 主要作用为了消除节点重置计数器带来的影响同时防止重复数据处理
	 * 
	 * 另外一种方式是只处理前一条数据x秒后的数据来避免重复数据,这种方式比较依赖节点的消息发布频率，需要控制设备节点最大发射占空比,同时丢弃频繁发送的消息
	 * 容易丢失数据,不能完全控制重复数据的处理
	 * 
	 */
	private Integer MAX_FCNT_DROP = sendRate * gatewaySendTime + 1;
	
	/**
	 * 设备包序号 用于去重和超时数据过滤
	 */
	private Long uplinkFcnt = Long.MIN_VALUE;
	/**
	 * 下行消息的fcnt计数
	 * 由于在节点端也有MAX_FCNT_GAP机制
	 * 所以下行计数值不持久化，大部分情况下，服务断电或者宕机后启动后重置不影响下行消息发布，但是测试的时候要注意下
	 */
	private AtomicLong downlinkFcnt  = new AtomicLong(0);
	/**
	 * 选举网关id
	 */
	private volatile String voteGatewayId;
	
	/**
	 * 最近的网关信息
	 */
	private transient DataRateIncreaseFactor lastDataRateIncreaseFactor;
	
	/**
	 * 节点状态维护的方法：
	 * 定时使用节点状态检查命令
	 * 返回消息更行节点状态
	 */
	private boolean status = false;
	/**
	 *  电量
	 * 	0	终端在使用外接电源
	 *	1..254	电池电量，1是最小值，254是最大值
	 *	255	终端设备无法获取电池电量
	 */
	private int battery = 255;
	
	
	public boolean isOnLine(){
		return status;
	}

	/**
	 * 设备上线
	 * @author lvhui5 2017年12月27日 上午9:51:42
	 */
	public void online() {
	    if(!status)
	    	this.status = true;
    }
	/**
	 * 设备下线
	 * @author lvhui5 2017年12月27日 上午9:51:24
	 */
	public void offline() {
	    this.status = false;
    }
	
    public int getBattery() {
    	return battery;
    }

	/**
	 * 创建一个新的实例DeviceNodeInfo.
	 * @param object
	 */
	public DeviceNodeInfo(JSONObject jsonObject) {
		this.deviceTypeCode = jsonObject.getString(DictCodeConstants.DMS_DEVICE_TYPE_CODE);
		this.deviceIndexCode = jsonObject.getString(DictCodeConstants.DMS_DEVICE_CODE);
		this.activation = jsonObject.getString(DictCodeConstants.DMS_ACTIVATION);
		if(isABP()){
			this.devEUI = jsonObject.getString(DictCodeConstants.DMS_DEVICE_EUI);	
		}
		this.devAddr = jsonObject.getString(DictCodeConstants.DMS_DEVICE_ADDR);
		this.netId = jsonObject.getString(DictCodeConstants.DMS_NET_ID);
		if(jsonObject.getInteger(DictCodeConstants.DMS_RECEIVE_DELAY) != null){
			this.receiveDelay1 = jsonObject.getInteger(DictCodeConstants.DMS_RECEIVE_DELAY);
			this.recieveDelay2 = this.receiveDelay1 + 1;
		}
		if(jsonObject.getInteger(DictCodeConstants.DMS_RX1_DATARATE_OFFSET) != null){
			this.RX1dataRateOffset = jsonObject.getInteger(DictCodeConstants.DMS_RX1_DATARATE_OFFSET);
		}
		if(jsonObject.getInteger(DictCodeConstants.DMS_RX2_DATARATE) != null){
			this.RX2dataRate = EnumDataRate.indexByValue(jsonObject.getInteger(DictCodeConstants.DMS_RX2_DATARATE));
		}
		this.appsKey = jsonObject.getString(DictCodeConstants.DMS_APPSKEY);
		this.nwksKey = jsonObject.getString(DictCodeConstants.DMS_NWKSKEY);
		this.applicationKey = jsonObject.getString(DictCodeConstants.DMS_APPKEY);
		JSONObject info = jsonObject.getJSONObject(DictCodeConstants.DMS_EXTEND_JSON);
		if (info != null) {

			
			
		}
		//最大占空比
		if(EnumMaxDutyCycle.indexByValue(jsonObject.getIntValue("maxDutyCycle")) != null){
			/**
			 * 网关到服务的时间和节点发送的时间目前不可配 由上层平台根据实际情况配置最大占空比
			 */
			this.maxDutyCycle = EnumMaxDutyCycle.indexByValue(jsonObject.getIntValue("maxDutyCycle"));
			//占空比时间间隔控制范围   2的maxDutyCycle次方 * 发送时间（毫秒）- 发送时间 = 可以接收消息的时间间隔
			this.dutyCycleRange = new BigDecimal(2).pow(this.maxDutyCycle.getKey()).multiply(new BigDecimal(this.nodeSendTime)).intValue() - this.nodeSendTime;
		}
		//消息模式
		if(EnumAccessMode.indexByValue(jsonObject.getString("accessMode")) != null){
			this.accessMode = EnumAccessMode.indexByValue(jsonObject.getString("accessMode"));
		}
		//设备状态参数定时任务检测
		timer.schedule(new TimerTask(){
			@Override
	        public void run() {
				if (log.isDebugEnabled()) {
					log.debug(Thread.currentThread()
									+ "time to node keepalive request. "
									+ getDevAddr());
				}
		        offline();
		        addMacQueue(new DevStatusReq(getDevAddr())); 
	        }
		}, 5000, nodeAliveRate);
	}
	
	public DeviceNodeInfo(DeviceNodeResp deviceNode) {
		if(StringUtils.isMoreBlank(deviceNode.getDevAddr(),deviceNode.getActivation())){
			throw new IllegalArgumentException("deviceNode argument null");
		}
		this.activation = deviceNode.getActivation();
		if(!isABP()){
			this.devEUI = deviceNode.getDevEUI();	
			this.applicationEUI = deviceNode.getAppEUI();
		}
		this.devAddr = deviceNode.getDevAddr();
		this.netId = deviceNode.getNetId();
		if(deviceNode.getReceiveDelay() != null){
			this.receiveDelay1 = deviceNode.getReceiveDelay();
			this.recieveDelay2 = this.receiveDelay1 + 1;
		}
		if(deviceNode.getRX1dataRateOffset() != null){
			this.RX1dataRateOffset = deviceNode.getRX1dataRateOffset();
		}
		if(EnumDataRate.indexByValue(deviceNode.getRX2dataRate()) != null){
			this.RX2dataRate = EnumDataRate.indexByValue(deviceNode.getRX2dataRate());
		}
		this.applicationKey = deviceNode.getAppKey();
		this.appsKey = deviceNode.getAppsKey();
		this.nwksKey = deviceNode.getNwksKey();
		
		//最大占空比
		if(EnumMaxDutyCycle.indexByValue(deviceNode.getMaxDutyCycle()) != null){
			 // 网关到服务的时间和节点发送的时间目前不可配 由上层平台根据实际情况配置最大占空比
			this.maxDutyCycle = EnumMaxDutyCycle.indexByValue(deviceNode.getMaxDutyCycle());
			//占空比时间间隔控制范围   2的maxDutyCycle次方 * 发送时间（毫秒）- 发送时间 = 可以接收消息的时间间隔
			this.dutyCycleRange = new BigDecimal(2).pow(this.maxDutyCycle.getKey()).multiply(new BigDecimal(this.nodeSendTime)).intValue() - this.nodeSendTime;
			//添加占空比控制命令到命令队列    终端重启如何重新发送mac命令
			addMacQueue(new DutyCycleReq(Double
	                .valueOf(this.maxDutyCycle.getKey()).byteValue()));
		}
		//消息模式
		if(EnumAccessMode.indexByValue(deviceNode.getAccessMode()) != null){
			this.accessMode = EnumAccessMode.indexByValue(deviceNode.getAccessMode());
		}
		//设备状态参数定时任务检测
		timer.schedule(new TimerTask(){
			@Override
	        public void run() {
				if (log.isDebugEnabled()) {
					log.debug(Thread.currentThread()
									+ "time to node keepalive request. "
									+ getDevAddr());
				}
		        offline();
		        addMacQueue(new DevStatusReq(getDevAddr())); 
	        }
		}, 5000, nodeAliveRate);
	}

	/**
	 * 扩展成任务池
	 */
	private static transient Timer timer = new Timer(true);
	
	
	public void setMaxDutyCycle(EnumMaxDutyCycle maxDutyCycle) {
    	this.maxDutyCycle = maxDutyCycle;
		//最大占空比配置修改需要下发命令到终端
    	addMacQueue(new DutyCycleReq(Double
	                .valueOf(this.maxDutyCycle.getKey()).byteValue()));
    }
	
	public String getCharsetEncode() {
		return charsetEncode;
	}
	
	public void setCharsetEncode(String charsetEncode) {
		this.charsetEncode = charsetEncode;
	}

	public EnumAccessMode getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(EnumAccessMode accessMode) {
		this.accessMode = accessMode;
	}
	
	public boolean isOpenADR() {
		return openADR;
	}
	
	public void setOpenADR(boolean openADR) {
		this.openADR = openADR;
	}
	
	public String getVoteGatewayId() {
		voteGatewayIdReadLock.lock();
		try {
			return voteGatewayId;
		} finally {
			voteGatewayIdReadLock.unlock();
		}
	}
	
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	
	public String getDeviceIndexCode() {
		return deviceIndexCode;
	}
	
	public void setDeviceIndexCode(String deviceIndexCode) {
		this.deviceIndexCode = deviceIndexCode;
	}
	
	public String getActivation() {
		return activation;
	}
	
	public void setActivation(String activation) {
		this.activation = activation;
	}
	
	public AtomicLong getDownlinkFcnt() {
		return downlinkFcnt;
	}
	
	public void setDownlinkFcnt(AtomicLong downlinkFcnt) {
		this.downlinkFcnt = downlinkFcnt;
	}

	public List<byte[]> getDevNonce() {
		return devNonce;
	}
	
	public void setDevNonce(List<byte[]> devNonce) {
		this.devNonce = devNonce;
	}
	
    public long getJoinRequestCount() {
    	return joinRequestCount;
    }

	
    public void setJoinRequestCount(long joinRequestCount) {
    	this.joinRequestCount = joinRequestCount;
    }

	
    public long getUpLinkPayloadCount() {
    	return upLinkPayloadCount;
    }

	
    public void setUpLinkPayloadCount(long upLinkPayloadCount) {
    	this.upLinkPayloadCount = upLinkPayloadCount;
    }

	public String getApplicationKey() {
		return applicationKey;
	}
	
	public void setApplicationKey(String applicationKey) {
		this.applicationKey = applicationKey;
	}
	
	public String getApplicationEUI() {
		return applicationEUI;
	}
	
	public void setApplicationEUI(String applicationEUI) {
		this.applicationEUI = applicationEUI;
	}
	
	public Integer getReceiveDelay1() {
		return receiveDelay1;
	}
	
	public Integer getRX1dataRateOffset() {
		return RX1dataRateOffset;
	}
	/**
	 * 组合dlsetting
	 * Bits        7      6:4     3:0
	 * DLsettings RFU RX1DRoffset RX2 Data rate
	 * @author lvhui5 2017年12月28日 下午2:10:55
	 * @return
	 */
	public byte getJoinAcceptDlSetting(){
		return (byte)(this.RX1dataRateOffset.byteValue() << 4 + this.RX2dataRate.getKey().byteValue());
	}
	
	public void setRX1dataRateOffset(Integer rX1dataRateOffset) {
		RX1dataRateOffset = rX1dataRateOffset;
	}
	
	public EnumDataRate getRX2dataRate() {
		dateRateReadLock.lock();
		try{
			return RX2dataRate;
		}finally{
			dateRateReadLock.unlock();
		}
	}
	
	public void setRX2dataRate(EnumDataRate rX2dataRate) {
		dateRateWriteLock.lock();
		try{
			RX2dataRate = rX2dataRate;
		}finally{
			dateRateWriteLock.unlock();
		}
	}
	
    public EnumDataRate getNodeDataRate() {
    	return nodeDataRate;
    }

	public void setReceiveDelay1(Integer receiveDelay1) {
		this.receiveDelay1 = receiveDelay1;
	}	
	
    public String getNetId() {
    	return netId;
    }

	
    public void setNetId(String netId) {
    	this.netId = netId;
    }

	public BlockingQueue<NotifyMsg> getDownlinkQueue() {
		return downlinkQueue;
	}
	
	public void setDownlinkQueue(BlockingQueue<NotifyMsg> downlinkQueue) {
		this.downlinkQueue = downlinkQueue;
	}
	
	public String getDevEUI() {
		return devEUI;
	}
	
	public void setDevEUI(String devEUI) {
		this.devEUI = devEUI;
	}
	
	public String getDevAddr() {
		return devAddr;
	}
	
	public void setDevAddr(String devAddr) {
		this.devAddr = devAddr;
	}
	
	public Map<String, NodeGatewayInfo> getNgrs() {
		return ngrs;
	}
	
	public void setNgrs(Map<String, NodeGatewayInfo> ngrs) {
		this.ngrs = ngrs;
	}

	public Long getUplinkFcnt() {
    	return uplinkFcnt;
    }

	
    public void setUplinkFcnt(Long uplinkFcnt) {
    	this.uplinkFcnt = uplinkFcnt;
    }

	public Integer getRecieveDelay2() {
		return recieveDelay2;
	}
	
	public void setRecieveDelay2(Integer recieveDelay2) {
		this.recieveDelay2 = recieveDelay2;
	}
	
	public Integer getJoinAcceptDelay1() {
		return joinAcceptDelay1;
	}
	
	public void setJoinAcceptDelay1(Integer joinAcceptDelay1) {
		this.joinAcceptDelay1 = joinAcceptDelay1;
	}
	
	public Integer getJoinAcceptDelay2() {
		return joinAcceptDelay2;
	}
	
	public void setJoinAcceptDelay2(Integer joinAcceptDelay2) {
		this.joinAcceptDelay2 = joinAcceptDelay2;
	}
	
	public String getAppsKey() {
		return appsKey;
	}
	
	public void setAppsKey(String appsKey) {
		this.appsKey = appsKey;
	}
	
	public String getNwksKey() {
		return nwksKey;
	}
	
	public void setNwksKey(String nwksKey) {
		this.nwksKey = nwksKey;
	}
	
	/**
	 * 重置消息计数器
	 * 上行计数器
	 * 下行计数器
	 * linkcheck计数器
	 * @author lvhui5 2017年12月5日 上午10:17:57
	 */
	private void fcntReset() {
		fcntWriteLock.lock();
		try {
			this.uplinkFcnt = Long.MIN_VALUE;
		} finally {
			fcntWriteLock.unlock();
		}
		this.downlinkFcnt.set(0);
		this.lastLinkcheckFcnt.set(Long.MIN_VALUE);
	}

	/**
	 * 主要用于处理节点重发 fcnt相同的情况
	 */
	private final Set<String> fcntGw = new HashSet<String>();
	
	/**
	 * fcnt是否满足正常消息条件
	 * @author lvhui5 2017年11月29日 下午7:07:49
	 * @param gatewayId 
	 * @param fcnt设备消息计数位 fcnt>：超时数据 fcnt=：重复数据
	 * @return
	 */
	private boolean fcntRight(Long fcnt, String gatewayId) {
		fcntReadLock.lock();
		try {
			if (this.uplinkFcnt < fcnt || this.uplinkFcnt >= fcnt + MAX_FCNT_DROP)
				return true;
			else if(this.uplinkFcnt == fcnt)//需要处理fcnt相同，但是同一个网关的上报消息
				return true;
			return false;
		} finally {
			fcntReadLock.unlock();
		}
	}
	
	/**
	 * 更新设备节点消息计数器
	 * @author lvhui5 2017年11月29日 下午7:11:18
	 * @param fcnt
	 * @param gatewayId 
	 */
	private void fcntUpdate(Long fcnt, String gatewayId) {
		fcntWriteLock.lock();
		try {
			if (this.uplinkFcnt < fcnt){
				this.uplinkFcnt = fcnt;
				this.fcntGw.clear();
				this.fcntGw.add(gatewayId);
			}else if(this.uplinkFcnt == fcnt){
				if(!fcntGw.contains(gatewayId)){
					this.fcntGw.add(gatewayId);
					throw new IllegalArgumentException("node [" + this.devAddr + "] fcnt  [" + fcnt + "] has  expired : uplinkFcnt = fcnt but fcntGw not contants gatewayId [" + gatewayId + "]");
				}
			}else if(this.uplinkFcnt >= fcnt + MAX_FCNT_DROP){//符合终端节点重启条件
				init();
				this.uplinkFcnt = fcnt;
				this.fcntGw.clear();
				this.fcntGw.add(gatewayId);
			}else{
				// 出现了并发的情况
				throw new IllegalArgumentException("node [" + this.devAddr + "] fcnt  [" + fcnt + "] has  expired");
			}
		} finally {
			fcntWriteLock.unlock();
		}
	}
	
	/**
	 * 添加下行消息到消息队列
	 * @author lvhui5 2017年11月29日 下午7:12:39
	 * @param downlinkMsg
	 */
	public void addMsg(NotifyMsg downlinkMsg) {
		downlinkQueue.add(downlinkMsg);
	}
	
	public static class NodeGatewayInfo  extends BaseDomain{
		/**
         * 序列化ID
         */
        private static final long serialVersionUID = 3442529605525499957L;
		/**
		 * 设备网关信号
		 */
		private int rssi;
		/**
		 * 网关id
		 */
		private String gatewayId;
		/**
		 * 网关地址
		 */
		private String address;
		/**
		 * 上行端口
		 */
		private int uplinkPort;
		/**
		 * 下行端口
		 * 对网关来说上行,目的端口就是1780,下行发送的时候目的端口是1782
		 */
		private int downlinkPort = 1782;
		
		private AtomicLong linkcheckFcnt = new AtomicLong(0);
		
		private ReentrantLock lock = new ReentrantLock();

        public ReentrantLock getLock() {
        	return lock;
        }
        
        public void lock() {
        	lock.lock();;
        }
        public void unlock() {
        	lock.unlock();
        }
		public NodeGatewayInfo(GatewayResp gateway) {
			this.gatewayId = gateway.getGatewayId();
			this.address = gateway.getAddress();
			if (gateway.getDownlinkPort() != null && gateway.getDownlinkPort() > 0) {
				this.downlinkPort = gateway.getDownlinkPort();
			}
		}
		/**
		 * 更新当前最新的linkcheck序列号  用于下发网关计数
		 * @author lvhui5 2018年1月4日 下午12:09:45
		 * @param linkcheckFcnt
		 */
		public void updateLinkcheckFcnt(Long linkcheckFcnt){
			this.linkcheckFcnt.getAndSet(linkcheckFcnt);
		}
		/**
		 * 网关是否接受到最新linkcheck请求
		 * @author lvhui5 2018年1月4日 下午12:17:44
		 * @param linkcheckFcnt
		 * @return
		 */
		public boolean  linkcheckFcntMatch(Long linkcheckFcnt){
			return this.linkcheckFcnt.longValue() == linkcheckFcnt;
		}
		
		public NodeGatewayInfo() {
			super();
		}
		
		public int getRssi() {
			return rssi;
		}
		
		public void setRssi(int rssi) {
			this.rssi = rssi;
		}
		
		public String getGatewayId() {
			return gatewayId;
		}
		
		public void setGatewayId(String gatewayId) {
			this.gatewayId = gatewayId;
		}
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public int getUplinkPort() {
			return uplinkPort;
		}
		
		public void setUplinkPort(int uplinkPort) {
			this.uplinkPort = uplinkPort;
		}
		
		public int getDownlinkPort() {
			return downlinkPort;
		}
		
		public void setDownlinkPort(int downlinkPort) {
			this.downlinkPort = downlinkPort;
		}
		/**
		 * 更新节点网关属性
		 * @author lvhui5 2018年3月12日 下午7:37:13
		 * @param rxpkData
		 */
		public void update(RxpkData rxpkData) {
	        this.rssi = rxpkData.getRssi();  
        }
		
	}
		
	/**
	 * 上行更新设备网关相关: 更新信号强度, 选举网关更新
	 * @author lvhui5 2017年12月1日 下午5:15:54
	 * @param gatewayId
	 * @param drReduce
	 * @param rxpkData
	 */
	public void updateGateway(String gatewayId, RxpkData rxpkData) {
		boolean increase = false;
		NodeGatewayInfo gatewayInfo = this.ngrs.get(gatewayId);
		//先添加网关
		if(gatewayInfo == null){
			synchronized(this){
				GatewayInfo gatewayInfoSour = DeviceNodeGatewayService.GATEWAY_CACHE.get(gatewayId);
				if(gatewayInfoSour == null){
					return;
				}
				if(this.ngrs.get(gatewayId) == null){
					//已注册网关 添加网关信息到设备节点
					gatewayInfo = new NodeGatewayInfo();
					gatewayInfo.setRssi(rxpkData.getRssi());
					BeanUtils.copyProperties(gatewayInfoSour, gatewayInfo);
					this.ngrs.put(gatewayId, gatewayInfo);
				}
			}	
		}else{
			increase = keepupLatestGatewayVoteAttr(gatewayInfo,rxpkData);
		}
		if(getVoteGatewayId() == null){
			this.voteGatewayId = gatewayId;
		}
		if(ngrs.size() <= 1){
			return;
		}
		/**
		 *当前网关和选举网关一致 选举网关的优先级下降 需要重新选举网关
		 */
		if(StringUtils.equals(getVoteGatewayId(), gatewayId)){
			//更新网关选举要素
			if(increase)
				return;
			//重新选举网关
			voteGatewayIdWriteLock.lock();
			try {
				gatewayRevote();
			} finally {
				voteGatewayIdWriteLock.unlock();
			}
			return;
		}
		voteGatewayIdWriteLock.lock();
		try {
			if (this.voteGatewayId == null || DeviceNodeGatewayService.GATEWAY_CACHE.get(voteGatewayId).offline()
			        || voteGatewayCompare.compare(gatewayInfo, ngrs.get(voteGatewayId)) > 0) {
				this.voteGatewayId = gatewayId;
			}
		} finally {
			voteGatewayIdWriteLock.unlock();
		}
	}
	
	/**
	 * 网关优先级是否增强
	 * @author lvhui5 2018年1月5日 上午11:58:29
	 * @return 返回更新后的选举优先级   true：高  false:低
	 */
	private boolean keepupLatestGatewayVoteAttr(final NodeGatewayInfo nodeGateway,RxpkData rxpkData){
		nodeGateway.lock();
		try {
			NodeGatewayInfo lastNodeGatewayInfo = new NodeGatewayInfo();
			BeanUtil.applyIf(lastNodeGatewayInfo, nodeGateway);
			// 信号强度
			nodeGateway.update(rxpkData);
			if(voteGatewayCompare.compare(nodeGateway, lastNodeGatewayInfo) > 0)
				return true;
			return false;
		} finally {
			nodeGateway.unlock();
		}
	}
	
	/**
	 * ADR动态适配更新
	 * mac命令通知节点 衰减节点自己控制
	 * @author lvhui5 2017年12月26日 下午3:15:27
	 * @param gatewayId
	 * @param drReduce
	 * @param rxpkData
	 */
	public void updateNodeDataRate(boolean drReduce, RxpkData rxpkData) {
		/**
		 * 下行消息速率动态适配
		 */
		if(EnumDataRate.indexByDatr(rxpkData.getDatr()) != null){
			/*if (drReduce) { //信号衰减
				dateRateWriteLock.lock();
				try{
					this.RX2dataRate = EnumDataRate.indexByValue(RX2dataRate.getReduceDr());
				}finally{
					dateRateWriteLock.unlock();
				}
			}*/
			/**
			 * 信号增强逻辑 计算方式
			 * 需要和上一次的信号强度、扩频因子做比对
			 */
			DataRateIncreaseFactor drif = new DataRateIncreaseFactor(rxpkData.getRssi(),rxpkData.getLsnr(),EnumDataRate.indexByDatr(rxpkData.getDatr()).getSf());
			dateRateWriteLock.lock();
			try{
				if(this.lastDataRateIncreaseFactor != null){
					EnumDataRate reach = this.lastDataRateIncreaseFactor.dataRateIncreaseReach(drif);
					if(reach != null){
						this.nodeDataRate = reach;
						/**
						 * 通知节点增强速率
						 * 发射功率POWER0
						 */
						addMacQueue(new LinkADRReq(reach.getKey().byteValue(),EnumTXPower.POWER0.getKey().byteValue(),new byte[]{(byte)0x06,(byte)0x00},(byte)0x00));
					}
				}
				this.lastDataRateIncreaseFactor = drif;
			}finally{
				dateRateWriteLock.unlock();
			}	
		}
	}

	/**
	 * 网关选举发送消息
	 * @author lvhui5 2017年12月4日 下午2:51:39
	 * @param msg
	 */
	public void sendMsg(byte[] msg,String token) {
	    //注册网关可能已经被移除或是离线状态 触发重新选举
	    String vgwi = getVoteGatewayId();
		if (vgwi == null || DeviceNodeGatewayService.GATEWAY_CACHE.get(vgwi) == null || DeviceNodeGatewayService.GATEWAY_CACHE.get(vgwi).offline()) {	
			voteGatewayIdWriteLock.lock();
			try {
				gatewayRevote();
	        	if (voteGatewayId == null || ngrs.get(voteGatewayId) == null) {
	        		log.error("no gateway can send message");
	        		return;
	        	}
	    		vgwi = voteGatewayId;
	        } finally{
	        	voteGatewayIdWriteLock.unlock();
	        }
	    }
		NodeGatewayInfo nodeGatewayInfo = ngrs.get(vgwi);
		//下行网关连接失败重新选举网关重发 直到没有网关  被动网关状态
	    while(!LoraServerSource.sendMsg(msg, ngrs.get(vgwi).getAddress(), ngrs.get(vgwi).getDownlinkPort())){
	    	//发送连接失败 在这里做些处理
	    	voteGatewayIdWriteLock.lock();
			try {
				ngrs.remove(vgwi);
				gatewayRevote();
	        	if (voteGatewayId == null || ngrs.get(voteGatewayId) == null) {
	        		log.error("no gateway can send message");
	        		return;
	        	}
				vgwi = voteGatewayId;
				nodeGatewayInfo = ngrs.get(vgwi);
	        } finally{
	        	voteGatewayIdWriteLock.unlock();
	        }
	    }
	    //节点下行消息维护
	    DownlinkMsgService.addDownLinkWindow(msg, nodeGatewayInfo.getAddress(), nodeGatewayInfo.getDownlinkPort(), token, vgwi);
	}

	
	/**
     * 用于网关选举后重新组装消息(下行消息带入选举网关id时使用)
     * @author lvhui5 2018年1月25日 下午5:45:33
     * @param gateWayId
     * @param message
     */
    @SuppressWarnings("unused")
    private void buildNewMsg(String gateWayId,byte[] message) {
    	byte[] gateWayIdByte = HexUtil.hexLengthAdapt(HexUtil.hexStringToBytes(gateWayId),8);
    	for(int i = 4 ; i < 12; i++){
    		if(message.length >= i+1){
    			message[i] = gateWayIdByte[i-4];
    		}
    	}
	}
	/**
	 * 重新选举最优网关
	 * @author lvhui5 2017年12月5日 上午11:59:04
	 */
	private void gatewayRevote() {
    	this.voteGatewayId = null;
		if (!CollectionUtils.isEmpty(ngrs.values())) {
			Iterator<NodeGatewayInfo> it = ngrs.values().iterator();
			int i = 0;
			while (it.hasNext()) {
				NodeGatewayInfo gatewayInfo = it.next();
				if(DeviceNodeGatewayService.GATEWAY_CACHE.get(gatewayInfo.getGatewayId()) == null || DeviceNodeGatewayService.GATEWAY_CACHE.get(gatewayInfo.getGatewayId()).offline()){
					ngrs.remove(gatewayInfo.getGatewayId());
				}else{
					if (i == 0) {
						this.voteGatewayId = gatewayInfo.getGatewayId();
					} else {
						if (voteGatewayCompare.compare(gatewayInfo, ngrs.get(voteGatewayId)) > 0) {
							this.voteGatewayId = gatewayInfo.getGatewayId();
						}
					}
					i++;
				}
			}
		}
	}
	
	/**
	 * 设备入网
	 * @author lvhui5 2017年12月5日 下午5:48:33
	 * @param bs
	 */
	public boolean  joinRequest(byte[] bs) {
		//devNonce入网申请带入不可重复
		if(this.devNonce.contains(bs))
			return false;
		this.devNonce.add(bs);
	    try {
	        this.joinRequestCount++;
        } catch (Exception e) {
	        //ignore
        }
	    init();
	    return true;
    }
	
	/**
	 * 节点信息初始化
	 * 触发条件：
	 * 节点重新入网申请
	 * 节点重启
	 * @author lvhui5 2017年12月26日 下午8:36:44
	 */
	private void init(){
		//重置计数
	    this.fcntReset();
	    //重置上行消息count
	    this.upLinkPayloadCount = 0;
	    macCommandQueueWriteLock.lock();
		try{
			macCommandQueue.clear();
		}finally{
			macCommandQueueWriteLock.unlock();	
		}
	    //网关信息重置
	    //ngrs.clear();
		//添加占空比控制命令到命令队列 
		addMacQueue(new DutyCycleReq(Double
                .valueOf(this.maxDutyCycle.getKey()).byteValue()));
		//添加其他节点初始化需要配置的mac命令
		
	}
	
	/**
	 * 用于记录设备linkcheck命令的序列
	 */
	private AtomicLong lastLinkcheckFcnt = new AtomicLong(Long.MIN_VALUE);
	
	/**
	 * 设备上报消息
	 * @author lvhui5 2017年12月5日 下午5:52:45
	 * @param fcnt
	 * @param includeLinkcheck  是否包含linkcheck命令
	 * @param gatewayId  网关id
	 * @return
	 */
	public boolean upLinkPayload(long fcnt,boolean includeLinkcheck,String gatewayId){
		online();
		if(includeLinkcheck){
			lastLinkcheckFcnt.updateAndGet(new LongUnaryOperator(){
				@Override
                public long applyAsLong(long operand) {
					if(fcnt < operand){
						return operand;
					}
					NodeGatewayInfo nodeGateway = getNgrs().get(gatewayId);
					if(nodeGateway != null){
						nodeGateway.updateLinkcheckFcnt(fcnt);
					}
					return fcnt;
				}
			});
		}
		
		/*default LongUnaryOperator compose(LongUnaryOperator before) {
	        Objects.requireNonNull(before);
	        return (long v) -> applyAsLong(before.applyAsLong(v));  LongUnaryOperator的匿名实现类
	    }*/
		
		if(!fcntRight(fcnt,gatewayId))
			return false;
		fcntUpdate(fcnt,gatewayId);
	    try {
	        this.upLinkPayloadCount++;
        } catch (Exception e) {
	        //ignore
        }
	    return true;
    }
	/**
	 * 占空比校验
	 * 校验方式：
	 * 比对最近的消息时间和当前时间
	 * 是否在配置的发布时间周期范围内
	 * @author lvhui5 2017年12月19日 下午4:27:22
	 * @return
	 */
	public boolean dutyCycleValid(){
		if(EnumMaxDutyCycle.MDC0 == this.maxDutyCycle) //不做服务占空比控制
			return true;
		long currentTime  = System.currentTimeMillis();
		synchronized(this){
			if(this.lastReceiveTime + this.dutyCycleRange > currentTime)
				return false;
			this.lastReceiveTime = currentTime;
		}
		return true;
	}
	
	/**
	 * 下行Fcnt计算
	 * @author lvhui5 2017年12月5日 下午8:40:15
	 * @return
	 */
	public long downLinkFcnt(){
		downlinkFcnt.compareAndSet(Long.MAX_VALUE, 0);
		return this.downlinkFcnt.incrementAndGet();
    }
	
	/**
	 * 是否ABP模式接入
	 * @author lvhui5 2017年12月7日 下午2:08:36
	 * @return
	 */
	public boolean isABP(){
		return EnumActivation.ABP.getKey().equals(this.activation);
		
	}
	
	/**
	 * 消息接收窗口
	 * <p>
	 * rx1窗口、rx2窗口
	 * </p>
	 * @author lvhui5 2017年12月28日 下午8:39:08
	 * @version V1.0
	 */
	public static enum EnumReceiveWindow {
		
		rx1(1),
		
		rx2(2);
		
		private Integer key;
		
		private EnumReceiveWindow(Integer key) {
			this.key = key;
			
		}
		public Integer getKey() {
			return key;
		}
		/**
		 * 全局索引池
		 */
		private static Map<Integer, EnumReceiveWindow> pool = new HashMap<Integer, EnumReceiveWindow>();
		static {
			for (EnumReceiveWindow et : EnumReceiveWindow.values()) {
				pool.put(et.key, et);
			}
		}
		/**
		 * 根据内容索引
		 * @param value
		 * @return
		 */
		public static EnumReceiveWindow indexByValue(Integer key) {
			return pool.get(key);
		}
	}
	
	
	/**
	 * 设备接入模式
	 * <p>
	 * OTAA  空中接入
	 * ABP   设备写入
	 * </p>
	 * @author lvhui5 2017年12月7日 下午2:07:59
	 * @version V1.0
	 */
	public static enum EnumActivation {
		
		ABP("ABP"),
		
		OTAA("OTAA");
		
		private String key;
		
		private EnumActivation(String key) {
			this.key = key;
			
		}
		public String getKey() {
			return key;
		}
		
		/**
		 * 全局索引池
		 */
		private static Map<String, EnumActivation> pool = new HashMap<String, EnumActivation>();
		static {
			for (EnumActivation et : EnumActivation.values()) {
				pool.put(et.key, et);
			}
		}
		
		/**
		 * 根据内容索引
		 * @param value
		 * @return
		 */
		public static EnumActivation indexByValue(String key) {
			return pool.get(key);
		}
	}

	/**
	 * 是否classC
	 * @author lvhui5 2017年12月20日 上午11:14:57
	 * @return
	 */
	public boolean isClassC(){
		if(EnumAccessMode.CLASS_C == this.accessMode)
			return true;
		return false;
	}
	
	/**
	 * 取得需要下行发布的消息 用于需要打开接收窗口的模式
	 * 
	 * 不支持节点通知修改
	 * @author lvhui5 2017年12月26日 下午7:54:40
	 * @return
	 */
	public List<NotifyMsg> messageToSend(){
		List<NotifyMsg> downlinkMsgs = new ArrayList<NotifyMsg>();
		//deviceNodeInfo.getDownlinkQueue().drainTo(downlinkMsgs);
		switch (this.accessMode) {
			case CLASS_A:
				//classA模式下 rx2窗口每次打开都只接收一次请求
				NotifyMsg firstdDownlinkMsg = this.downlinkQueue.poll();
				if(firstdDownlinkMsg != null){
					downlinkMsgs.add(firstdDownlinkMsg);
				}
				break;
			case CLASS_B:
				/**
				 * TODO 后面要考虑到class B模式接入的扩展：
				 * 节点打开多个下行窗口
				 * 由网关控制接收信标，下行配置接收信标
				 * 当下行消息发布频率较高 并且对实时性要求高 可以考虑采用，功耗比class A高
				 */
				break;	
			case CLASS_C:
				/**
				 * classC下行消息直接发布
				 */
				break;	
			default:
				throw new IllegalArgumentException("unknown accessMode");
		}
		return downlinkMsgs;
	}
	
	/**
	 * ADR动态速率适配 增强要素
	 * 用于维护最近一次的相关数据和速率增强适配
	 * <p>
	 * 平台根据节点上报信息的rssi和snr调整节点的通信速率，平台需要下发命令来调整节点的通信速率。调整方式和门限值如下（门限值需要实地场景测试后给出，下表中的门限值为暂定数据，后续根据实测情况修改）：
	 *	1）连续两次收到同一节点的消息，Rssi大于-75，snr大于10，且扩频因子大于9，则调整节点的扩频因子为9。
	 *	2）连续两次收到同一节点的消息，Rssi大于-85，snr大于0，且扩频因子大于10，则调整节点的扩频因子为10。
	 *	3）连续两次收到同一节点的消息，Rssi大于-95，snr大于-5，且扩频因子大于11，则调整节点的扩频因子为11。
	 * </p>
	 * @author lvhui5 2017年12月15日 下午3:07:57
	 * @version V1.0
	 */
	private static class DataRateIncreaseFactor{

		/**
		 * 信号强度
		 */
		private Integer rssi;
		/**
		 * 信噪比
		 */
		private Float lsnr;
		
		/**
		 * 扩频因子
		 */
		private Integer sf;
		/**
		 * 更新后的速率
		 */
		private EnumDataRate dateRate;
		/**
		 * 
		 * 创建一个新的实例DataRateIncreaseFactor.
		 * @param rssi
		 * @param lsnr
		 * @param sf
		 */
		public DataRateIncreaseFactor(Integer rssi, Float lsnr, Integer sf) {
			super();
			this.rssi = rssi;
			this.lsnr = lsnr;
			this.sf = sf;
		}


		/**
		 * 连续两次达到调整数据速率的标准  返回调整后的数据速率
		 * @author lvhui5 2017年12月15日 下午3:14:56
		 * @param dataRateIncreaseFactor
		 * @return
		 */
		public EnumDataRate dataRateIncreaseReach(DataRateIncreaseFactor dataRateIncreaseFactor) {
			if(isDr3() && dataRateIncreaseFactor.isDr3()){
				dataRateIncreaseFactor.setDateRate(EnumDataRate.DR3);
			}else if(isDr2() && dataRateIncreaseFactor.isDr2()){
				dataRateIncreaseFactor.setDateRate(EnumDataRate.DR2);
			}else if(isDr1() && dataRateIncreaseFactor.isDr1()){
				dataRateIncreaseFactor.setDateRate(EnumDataRate.DR1);
			}
			if(dataRateIncreaseFactor.getDateRate() != null && dataRateIncreaseFactor.getDateRate() != this.dateRate){
				return  dataRateIncreaseFactor.getDateRate();
			}
			return null;
        }
		
        
        public void setDateRate(EnumDataRate dateRate) {
        	this.dateRate = dateRate;
        }


		public EnumDataRate getDateRate() {
        	return dateRate;
        }


		public boolean isDr3(){
			return this.rssi > -75 && this.lsnr > 10 && this.sf > 9;
		}

		public boolean isDr2(){
			return this.rssi > -85 && this.lsnr > 0 && this.sf > 10;
		}
		
		public boolean isDr1(){
			return this.rssi > -95 && this.lsnr > -5 && this.sf > 11;
		}
	}
	
	/**
	 * 网关选举规则
	 * <p>
	 * 根据rssi信号强度选举合适网关
	 * </p>
	 * @author lvhui5 2017年12月19日 下午7:01:05
	 * @version V1.0
	 */
	private static final Comparator<NodeGatewayInfo> voteGatewayCompare = new Comparator<NodeGatewayInfo>(){
		@Override
        public int compare(NodeGatewayInfo o1, NodeGatewayInfo o2) {
			if(o1 != null && o2 == null)
	        	return 1;
			if(o1 == null && o2 != null)
	        	return -1;
			if(o1 == null && o2 == null)
				return 0;
			if(o1 != null && o2 != null){
		        if(o1.getRssi() < o2.getRssi())	
					return -1;
				if(o1.getRssi() > o2.getRssi())
		        	return 1;
			}
	        return 0;
        }	
	};
	/**
	 * mac命令下发队列
	 * 终端对于mac命令的回复没有下发命令标识 所以不对下发的命令做终端回复等级处理
	 */
	private BlockingQueue<MacReq> macCommandQueue = new LinkedBlockingQueue<MacReq>(5);
	
	private ReentrantReadWriteLock macCommandQueueLock = new ReentrantReadWriteLock();
	
	private ReadLock macCommandQueueReadLock = macCommandQueueLock.readLock();
	
	private WriteLock macCommandQueueWriteLock = macCommandQueueLock.writeLock();
	/**
	 * 添加mac命令到队列
	 * 假如节点支持不携带内容的mac命令消息  需要直接组装下行消息
	 * @author lvhui5 2017年12月26日 下午2:24:44
	 * @param req
	 */
	public void addMacQueue(MacReq req){
		if(isClassC()){
			/**
			 * TODO  class c模式直接发下mac命令  需要dataMessage 上行rxpkData  gatawayInfo 因为不需要tmst 上行的mtype不需要
			 */
			return;
		}
		macCommandQueueReadLock.lock();
		try{
			//在线状态请求去重
			if(req instanceof DevStatusReq && macCommandQueue.contains(req))
				return;
		}finally{
			macCommandQueueReadLock.unlock();
		}
		macCommandQueueWriteLock.lock();
		try{
			if(!macCommandQueue.offer(req)){
	    		log.warn("node [{}] macCommandQueue is full",this.devAddr);
			}
		}finally{
			macCommandQueueWriteLock.unlock();
		}
	}
	
	
	public MacReq[] drainMacQueue(){
		macCommandQueueReadLock.lock();
		try{
			if(macCommandQueue.isEmpty())
				//没有mac命令需要下发
				return null;
		}finally{
			macCommandQueueReadLock.unlock();
		}
		
		List<MacReq> macReqList = new ArrayList<MacReq>();
		/**
		 * 多个命令需要控制长度在15个字节以内
		 */
		MacReq firstMacReq;
		int reqLength = 0;
		macCommandQueueWriteLock.lock();
		try{
			while((firstMacReq = this.macCommandQueue.peek())!= null){
				reqLength = reqLength + firstMacReq.getReqLength();
				if(reqLength >15)
					break;
				macReqList.add(this.macCommandQueue.poll());
			}	
		}finally{
			macCommandQueueWriteLock.unlock();
		}
		/**
		 * TODO 处理过时的adr命令
		 */
		return macReqList.toArray(new MacReq[macReqList.size()]);
	}
	
	@Override
    public void linkCheck() {
		//计算最近一次成功收到 LinkCheckReq 的网关的数量
		Long lastLinkcheckFcnt = this.lastLinkcheckFcnt.longValue();
		Integer gwCnt = 0;
		if(MapUtils.isNotEmpty(ngrs)){
			for(NodeGatewayInfo nodeGateway : ngrs.values()){
				if(nodeGateway.linkcheckFcntMatch(lastLinkcheckFcnt))
					gwCnt++;	
			}	
		}
		//添加linkCheck下行回复命令到队列
		addMacQueue(new LinkCheckAnsReq(this.lastDataRateIncreaseFactor.lsnr.byteValue(),gwCnt.byteValue()));
	}
	@Override
    public void devStatus(int battery) {
	    this.battery = battery;
	    online(); 
    }

	@Override
    public void linkAdr(String result) {
	    if(!StringUtils.isEmpty(result)){
	    	log.error("node [{}] linkAdr result:{}",new Object[]{this.devAddr,result});
	    }
    }
	@Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
