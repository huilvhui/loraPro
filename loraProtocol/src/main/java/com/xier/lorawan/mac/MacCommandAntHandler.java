package com.xier.lorawan.mac;

/**
 * mac命令回复处理业务接口
 * <p></p>
 * @author lvhui5 2017年12月27日 上午10:45:59
 * @version V1.0
 */
public interface MacCommandAntHandler {
	
	/**
	 * 终端发起连接状态处理
	 * @author lvhui5 2017年12月27日 上午10:48:18
	 */
	public void linkCheck();
	
	/**
	 * 节点状态
	 * @author lvhui5 2017年12月28日 上午11:23:14
	 * @param battery 电量
	 */
	public void devStatus(int battery);
	
	/**
	 * adr配置返回结果
	 * @author lvhui5 2018年1月6日 上午11:21:44
	 * @param result
	 */
	public void linkAdr(String result);
	
}
