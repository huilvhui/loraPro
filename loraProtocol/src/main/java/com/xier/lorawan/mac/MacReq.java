package com.xier.lorawan.mac;


/**
 * mac命令接口 
 * <p></p>
 * @author lvhui5 2017年12月25日 下午3:49:27
 * @version V1.0
 */
public interface MacReq {
	
	/**
	 * 装载mac命令
	 * @author lvhui5 2017年12月25日 下午4:38:22
	 * @param macByteArray
	 * @return
	 */
	public byte[] payLoadMac(byte[] macByteArray);
	
	/**
	 * 命令长度
	 * @author lvhui5 2018年1月3日 下午4:35:20
	 * @return
	 */
	public int getReqLength();
}
