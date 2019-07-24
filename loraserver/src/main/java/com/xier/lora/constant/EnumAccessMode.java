package com.xier.lora.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 节点接入模式
 * <p>
 * class A:
 * 通过随机时间对间隔进行微调来实现随机访问，让所发送者平等、自由地竞争信道的使用权。
 * 低功耗，先发送后接收，发送和接收交替进行。终端只有在发送数据后才能接收处理服务器发送来的数据，发送数据不受接收数据的影响
 * class B:
 * 同样是先发送后接收，不同的是每次发送后按照一定时间间隔启动接收窗口，接收多条数据。时间间隔从网关获取，以便服务器知晓终端接收消息的时刻。
 * class C:
 * C类终端设备的接收窗口，除了在发送数据的时候关闭外一直处于打开状态。C类终端功耗比A类和B类都大，但对于和服务器之间的交互来说延迟也最低。
 * </p>
 * @author lvhui5 2017年12月20日 上午10:29:05
 * @version V1.0
 */
public enum EnumAccessMode {
	
	CLASS_A("A"),
	
	CLASS_B("B"),
	
	CLASS_C("C"), ;
	
	private String key;
	
	private EnumAccessMode(String key) {
		this.key = key;
		
	}
	
	/**
	 * 全局索引池
	 */
	private static Map<String, EnumAccessMode> pool = new HashMap<String, EnumAccessMode>();
	static {
		for (EnumAccessMode et : EnumAccessMode.values()) {
			pool.put(et.key, et);
		}
	}
	
	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumAccessMode indexByValue(String key) {
		return pool.get(key);
	}

}
