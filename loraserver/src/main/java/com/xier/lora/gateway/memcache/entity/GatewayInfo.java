package com.xier.lora.gateway.memcache.entity;

import com.xier.lora.gateway.dto.UpstreamData.Stat;
import com.xier.lora.gateway.memcache.entity.DeviceNodeInfo.NodeGatewayInfo;
import com.xier.lora.sys.pub.api.loraserver.dto.GatewayResp;


public class GatewayInfo extends NodeGatewayInfo{
	/**
     * 序列化ID
     */
    private static final long serialVersionUID = 3126320703542312988L;
	/**
	 * 最新上线时间 用于维护网关的在线状态
	 */
	private long lastAliveTime;
	/**
	 * 心跳保持上线状态时间 秒
	 * 默认5分钟
	 */
	private static int stayOnlineSec = 5 * 60;
	
	/**
	 * 经度
	 */
	private Float longitude;
	/**
	 * 纬度
	 */
	private Float latitude;
	/**
	 * 高度
	 */
	private Integer alti;

	
	/**
	 * 网关转发过来的帧数
	 */
	private Integer txnb;
	
	//========新增
	/**
	 * 上行初始中心频率
	 */
	private Double txFreStart;
	/**
	 * rx1下行初始中心频率
	 */
	private Double Rx1FreStart;
	/**
	 * rx2下行固定频率
	 */
	private Double Rx2Fre;

	
	public GatewayInfo(GatewayResp gateway) {
		super(gateway);
		this.lastAliveTime = System.currentTimeMillis();
	}
	
	public Float getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	public Float getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	
	public Integer getAlti() {
		return alti;
	}
	
	public void setAlti(Integer alti) {
		this.alti = alti;
	}
	
	public Integer getTxnb() {
		return txnb;
	}
	
	public void setTxnb(Integer txnb) {
		this.txnb = txnb;
	}
	
	public Double getTxFreStart() {
		return txFreStart;
	}

	
    public void setTxFreStart(Double txFreStart) {
    	this.txFreStart = txFreStart;
    }

	
    public Double getRx1FreStart() {
    	return Rx1FreStart;
    }

	
    public void setRx1FreStart(Double rx1FreStart) {
    	Rx1FreStart = rx1FreStart;
    }

	
    public Double getRx2Fre() {
    	return Rx2Fre;
    }

	
    public void setRx2Fre(Double rx2Fre) {
    	Rx2Fre = rx2Fre;
    }

	/**
	 * 网关上线
	 * @author lvhui5 2017年12月13日 下午2:41:51
	 */
	public void online() {
	    this.lastAliveTime = System.currentTimeMillis();
    }

	/**
	 * 是否离线状态  由服务主动发起的网关状态检测
	 * @author lvhui5 2017年12月13日 下午2:45:19
	 * @return
	 */
	public boolean offline() {
		//判断网关离线的标准为超过5分钟没有心跳
		return System.currentTimeMillis() > this.lastAliveTime + stayOnlineSec*1000;
    }

	/**
	 * 更新网关状态
	 * @author lvhui5 2018年1月4日 下午3:14:49
	 * @param stat
	 */
	public void updateStat(Stat stat) {
	    this.longitude = stat.getLongitude();
	    this.latitude = stat.getLati(); 
	    this.alti = stat.getAlti();
	    this.txnb = stat.getTxnb();
	    this.txFreStart = stat.getTxFreStart();
	    this.Rx1FreStart = stat.getRx1FreStart();
	    this.Rx2Fre = stat.getRx2Fre();
    }	
}
