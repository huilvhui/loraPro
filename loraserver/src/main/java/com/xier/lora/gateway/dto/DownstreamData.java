package com.xier.lora.gateway.dto;


/**
 * 下行请求数据
 * <p>
 * </p>
 * @author lvhui5 2017年11月20日 下午7:26:58
 * @version V1.0
 */
public class DownstreamData extends BaseData {
	
	/**
	 * 下行消息体
	 */
	private TxpkData txpk;
	
	public TxpkData getTxpk() {
		return txpk;
	}
	
	public void setTxpk(TxpkData txpk) {
		this.txpk = txpk;
	}
	
	public static class TxpkData {
		
		/**
		 * transmit the frame immediately
		 */
		private boolean imme;
		/**
		 * delay send time
		 */
		private Long tmst;
		/**
		 * centre frequency=470.125MHz
		 */
		private Double freq;
		/**
		 * RFchain=Radio A
		 */
		private Integer rfch;
		/**
		 * outputpower=17dBm
		 */
		private Integer powe = 17;
		/**
		 * modulation is LoRa
		 */
		private String modu = "LORA";
		/**
		 * SF=9, BW=125kHz
		 */
		private String datr;
		/**
		 * FEC code rate=4/5
		 */
		private String codr;
		/**
		 * doNOT invert the polarity of the transmitted bits
		 */
		private boolean ipol = true;
		/**
		 * numberof octets=10
		 */
		private Integer size;
		/**
		 * data that encoded into Base64
		 */
		private String data;
		

		public Long getTmst() {
			return tmst;
		}
		
		public void setTmst(Long tmst) {
			this.tmst = tmst;
		}
		
		public boolean isImme() {
			return imme;
		}
		
		public void setImme(boolean imme) {
			this.imme = imme;
		}
		
		public Double getFreq() {
			return freq;
		}
		
		public void setFreq(Double freq) {
			this.freq = freq;
		}
		
		public Integer getRfch() {
			return rfch;
		}
		
		public void setRfch(Integer rfch) {
			this.rfch = rfch;
		}
		
		public Integer getPowe() {
			return powe;
		}
		
		public void setPowe(Integer powe) {
			this.powe = powe;
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
		
		public boolean isIpol() {
			return ipol;
		}
		
		public void setIpol(boolean ipol) {
			this.ipol = ipol;
		}
		
		public Integer getSize() {
			return size;
		}
		
		public void setSize(Integer size) {
			this.size = size;
		}
		
		public String getData() {
			return data;
		}
		
		public void setData(String data) {
			this.data = data;
		}
		
	}
	
}
