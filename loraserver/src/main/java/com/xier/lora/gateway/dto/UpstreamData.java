package com.xier.lora.gateway.dto;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.xier.lorawan.enums.MType;

/**
 * 上行请求数据
 * <p>
 * </p>
 * @author lvhui5 2017年11月20日 下午7:26:58
 * @version V1.0
 */
public class UpstreamData  extends BaseData{
	/**
	 * 上报消息的集合
	 */
	private List<RxpkData> rxpk;
	/**
	 * 网关信息
	 */
	private Stat stat;
	
	
	public List<RxpkData> getRxpk() {
		return rxpk;
	}
	
	public void setRxpk(List<RxpkData> rxpk) {
		this.rxpk = rxpk;
	}
	
	public Stat getStat() {
		return stat;
	}
	
	public void setStat(Stat stat) {
		this.stat = stat;
	}
	
	/**
	 * 网关上报状态
	 * <p></p>
	 * @author lvhui5 2017年12月15日 下午5:02:30
	 * @version V1.0
	 */
	public static class Stat {
		
		/**
		 * 网关的UTC系统时间
		 */
		private String time;
		/**
		 * 纬度坐标
		 */
		private Float lati;
		/**
		 * 经度坐标
		 */
		@JSONField(name="long") 
		private Float longitude;
		/**
		 * 高度
		 */
		private Integer alti;
		/**
		 * 收到的帧数
		 */
		private Integer rxnb;
		/**
		 * CRC校验过的正确帧数
		 */
		private Integer rxok;
		/**
		 * 转发给HIK-SERVER的帧数
		 */
		private Integer rxfw;
		/**
		 * 响应报文的百分比
		 */
		private Float ackr;
		/**
		 * 从HIK-SERVER收到的帧数
		 */
		private Integer dwnb;
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
		
		public String getTime() {
			return time;
		}
		
		public void setTime(String time) {
			this.time = time;
		}
		
		public Float getLati() {
			return lati;
		}
		
		public void setLati(Float lati) {
			this.lati = lati;
		}
		
		public Float getLongitude() {
			return longitude;
		}
		
		public void setLongitude(Float longitude) {
			this.longitude = longitude;
		}
		
		public Integer getAlti() {
			return alti;
		}
		
		public void setAlti(Integer alti) {
			this.alti = alti;
		}
		
		public Integer getRxnb() {
			return rxnb;
		}
		
		public void setRxnb(Integer rxnb) {
			this.rxnb = rxnb;
		}
		
		public Integer getRxok() {
			return rxok;
		}
		
		public void setRxok(Integer rxok) {
			this.rxok = rxok;
		}
		
		public Integer getRxfw() {
			return rxfw;
		}
		
		public void setRxfw(Integer rxfw) {
			this.rxfw = rxfw;
		}
		
		public Float getAckr() {
			return ackr;
		}
		
		public void setAckr(Float ackr) {
			this.ackr = ackr;
		}
		
		public Integer getDwnb() {
			return dwnb;
		}
		
		public void setDwnb(Integer dwnb) {
			this.dwnb = dwnb;
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
		
	}
	
	public static class RxpkData {
		
		/**
		 * 2016-11-15T09:35:17.145359Z 网关的UTC系统时间
		 */
		private String time;
		/**
		 * 3526705714 网关计数器
		 */
		private Long tmst;
		/**
		 * 0 IF信道号是IF0
		 */
		private Integer chan;
		/**
		 * 0 射频频率0
		 */
		private Integer rfch;
		/**
		 * 469.87500 中心频率是469.875MHz
		 */
		private Double freq;
		/**
		 * 1 CRC校验正确
		 */
		private Integer stat;
		/**
		 * LORA modulation is LoRa
		 */
		private String modu;
		/**
		 * SF9BW125 扩频因子是9, 带宽是125kHz
		 */
		private String datr;
		/**
		 * ECC编码率
		 * 4/5 FEC code rate=4/5
		 */
		private String codr;
		/**
		 * 信号强度
		 * -13 RSSI=-13dBm
		 */
		private Integer rssi;
		/**
		 * 信噪比
		 * 5.6 SNR=5.6
		 */
		private Float lsnr;
		/**
		 * 12 numberof octets=12
		 */
		private Long size;
		/**
		 * rqyo15LfOP4J data that encoded into Base64
		 */
		private String data;
		
		/**
		 * 是否fsk模式
		 * @author lvhui5 2018年2月25日 上午11:52:12
		 */
		public boolean isFskModu(){
			return this.modu.equalsIgnoreCase(EnumModuType.FSK.name());
		}
		/**
		 * 当前消息lora体msgType
		 * @author lvhui5 2018年3月12日 上午11:28:03
		 * @return
		 */
		public MType getMsgType(){
			return MType.wrap((byte)((ByteBuffer.wrap(Base64.getDecoder().decode(this.data)).get() & 0xE0) >> 5));
		}
		
		public String getTime() {
			return time;
		}
		
		public void setTime(String time) {
			this.time = time;
		}
		
		public Long getTmst() {
			return tmst;
		}
		
		public void setTmst(Long tmst) {
			this.tmst = tmst;
		}
		
		public Integer getChan() {
			return chan;
		}
		
		public void setChan(Integer chan) {
			this.chan = chan;
		}
		
		public Integer getRfch() {
			return rfch;
		}
		
		public void setRfch(Integer rfch) {
			this.rfch = rfch;
		}
		
		public Double getFreq() {
			return freq;
		}
		
		public void setFreq(Double freq) {
			this.freq = freq;
		}
		
		public Integer getStat() {
			return stat;
		}
		
		public void setStat(Integer stat) {
			this.stat = stat;
		}
		
		public String getModu() {
			return modu;
		}
		
		public void setModu(String modu) {
			this.modu = modu;
		}
		
		public String getDatr() {
			return datr;
		}
		
		public void setDatr(String datr) {
			this.datr = datr;
		}
		
		public String getCodr() {
			return codr;
		}
		
		public void setCodr(String codr) {
			this.codr = codr;
		}
		
		public Integer getRssi() {
			return rssi;
		}
		
		public void setRssi(Integer rssi) {
			this.rssi = rssi;
		}
		
		public Float getLsnr() {
			return lsnr;
		}
		
		public void setLsnr(Float lsnr) {
			this.lsnr = lsnr;
		}
		
		public Long getSize() {
			return size;
		}
		
		public void setSize(Long size) {
			this.size = size;
		}
		
		public String getData() {
			return data;
		}
		
		public void setData(String data) {
			this.data = data;
		}
	}
	private enum EnumModuType{
		/**
		 * lorawan协议模式
		 */
		LORA(),
		/**
		 * fsk模式
		 */
		FSK();
	}
}
