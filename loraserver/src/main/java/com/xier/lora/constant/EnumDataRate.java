package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 数据速率
 * </p>
 * @author lvhui5 2017年11月28日 下午6:42:15
 * @version V1.0
 */
public enum EnumDataRate {
	DR0(0, "SF12BW125", 0, 1,12), 
	DR1(1, "SF11BW125", 0, 2,11), 
	DR2(2, "SF10BW125", 1, 3,10), 
	DR3(3, "SF9BW125", 2, 4,9), 
	DR4(4, "SF8BW125", 3, 5,8), 
	DR5(5, "SF7BW125", 4, 5,7),
	
	;
	
	private Integer key;
	/**
	 * 数据速率  kHz
	 */
	private String datr;
	
	private Integer reduceDr;
	
	private Integer increaseDr;
	/**
	 * 扩频因子
	 */
	private Integer sf;
	/**
	 * 全局索引池
	 */
	private static Map<Integer, EnumDataRate> pool = new HashMap<Integer, EnumDataRate>();
	/**
	 * 网关数据速率格式索引
	 */
	private static Map<String, EnumDataRate> datrPool = new HashMap<String, EnumDataRate>();
	static {
		for (EnumDataRate et : EnumDataRate.values()) {
			pool.put(et.key, et);
			datrPool.put(et.getDatr(), et);
		}
	}
	
	private EnumDataRate(Integer key, String datr, Integer reduceDr, Integer increaseDr, Integer sf) {
		this.key = key;
		this.datr = datr;
		this.reduceDr = reduceDr;
		this.increaseDr = increaseDr;
		this.sf = sf;
	}
	
	public String getDatr() {
		return datr;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public Integer getReduceDr() {
		return reduceDr;
	}
	
	public Integer getIncreaseDr() {
		return increaseDr;
	}
	
    public Integer getSf() {
    	return sf;
    }

	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumDataRate indexByValue(Integer key) {
		return pool.get(key);
	}
	public static EnumDataRate indexByDatr(String datr) {
		return datrPool.get(datr);
	}
	/**
	 * 数据速率偏移
	 * @author lvhui5 2017年12月5日 下午4:18:18
	 * @param data 原始速度
	 * @param offset 偏移量
	 * @return
	 */
	public static EnumDataRate offsetDataRate(String datr, Integer offset) {
		EnumDataRate inputDataRate = indexByDatr(datr);
		if (inputDataRate == null) {
			return null;
		}
		return inputDataRate.getOffsetDataRate(offset);
	}
	
	private EnumDataRate getOffsetDataRate(Integer offset) {
		EnumDataRate dataRate = this;
		if (offset < 0) {
			for (int i = offset; i < 0; i++) {
				dataRate = EnumDataRate.indexByValue(dataRate.getReduceDr());
			}
		}
		else if (offset > 0) {
			for (int i = offset; i > 0; i--) {
				dataRate = EnumDataRate.indexByValue(dataRate.getIncreaseDr());	
			}
		}
		return dataRate;
	}
	
}
