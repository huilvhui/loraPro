package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 最大占空比 
 * 占空比计算 1/2最大占空比次方
 * </p>
 * @author lvhui5 2017年11月28日 下午6:42:15
 * @version V1.0
 */
public enum EnumMaxDutyCycle {
	MDC0(0), MDC1(1), MDC2(2), MDC3(3), MDC4(4), MDC5(5), MDC6(6), MDC7(7), MDC8(8), MDC9(9), MDC10(10), MDC11(11), MDC12(12), MDC13(
	        13), MDC14(14), MDC15(15),
	;
	
	private Integer key;
	
	/**
	 * 全局索引池
	 */
	private static Map<Integer, EnumMaxDutyCycle> pool = new HashMap<Integer, EnumMaxDutyCycle>();

	static {
		for (EnumMaxDutyCycle et : EnumMaxDutyCycle.values()) {
			pool.put(et.key, et);
		}
	}
	
	private EnumMaxDutyCycle(Integer key) {
		this.key = key;
	}

	public Integer getKey() {
		return key;
	}
	

	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumMaxDutyCycle indexByValue(Integer key) {
		return pool.get(key);
	}


	
	
}
