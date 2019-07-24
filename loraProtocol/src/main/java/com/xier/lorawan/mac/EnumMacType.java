package com.xier.lorawan.mac;

import java.util.HashMap;
import java.util.Map;

/**
 * mac命令类型
 * 由服务下发给客户端
 * <p>
 * 目前支持类型
 * adr速率配置
 * 占空比
 * 节点连接检查
 * 节点状态
 * </p>
 * @author lvhui5 2017年12月25日 下午6:58:20
 * @version V1.0
 */
public enum EnumMacType {

	/**
	 * 终端连接验证
	 */
	LinkCheck((byte)0x02,LinkCheckReqAnt.class),
	/**
	 * adr适配
	 */
	LinkADR((byte)0x03,LinkADRAns.class),
	/**
	 * 占空比
	 */
	DutyCycle((byte)0x04,DutyCycleAnt.class),
	/**
	 * 返回终端 状态，即电量和解调情况
	 */
	DevStatus((byte)0x04,DevStatusAns.class),
	;

    private byte type;
    
	private Class<? extends MacAnt> commandAnt;
   
    EnumMacType(byte type,Class<? extends MacAnt> commandAnt) {
        this.type = type;
        this.commandAnt = commandAnt;
    }
	
	public byte getType() {
		return type;
	}
	
	public Class<? extends MacAnt> getCommandAnt() {
		return commandAnt;
	}

	/**
	 * 全局索引池
	 */
	private static Map<Byte, EnumMacType> pool = new HashMap<Byte, EnumMacType>();
	static {
		for (EnumMacType et : EnumMacType.values()) {
			pool.put(et.type, et);
		}
	}
	
	/**
	 * 根据内容索引
	 * @param value
	 * @return
	 */
	public static EnumMacType indexByValue(Byte name) {
		return pool.get(name);
	}
    
}
