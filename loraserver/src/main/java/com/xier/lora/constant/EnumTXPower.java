package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 发射功率
 * <p></p>
 * @author lvhui5 2018年1月19日 下午1:53:50
 * @version V1.0
 */
public enum EnumTXPower {
	POWER0(0,17), POWER1(1,16), POWER2(2,14), POWER3(3,12), POWER4(4,10), POWER5(5,7), POWER6(6,5), POWER7(7,2)
	;
	
	private Integer key;
	
	private Integer dbm;
	/**
	 * 全局索引池
	 */
	private static Map<Integer, EnumTXPower> pool = new HashMap<Integer, EnumTXPower>();

	static {
		for (EnumTXPower et : EnumTXPower.values()) {
			pool.put(et.key, et);
		}
	}
	
	private EnumTXPower(Integer key,Integer dbm) {
		this.key = key;
		this.dbm = dbm;
	}
	
	public Integer getKey() {
		return key;
	}
	
	public Integer getDbm() {
		return dbm;
	}

	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumTXPower indexByValue(Integer key) {
		return pool.get(key);
	}


	
	
}
