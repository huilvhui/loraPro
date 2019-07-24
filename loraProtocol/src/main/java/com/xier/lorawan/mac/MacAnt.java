package com.xier.lorawan.mac;

import java.nio.ByteBuffer;

/**
 * mac命令回复接口 
 * @author lvhui5 2017年12月25日 下午3:49:27
 * @version V1.0
 */
public interface MacAnt {
	/**
	 * 遍历处理所有mac命令回复
	 * 根据类型 处理命令
	 * @author lvhui5 2017年12月25日 下午8:29:02
	 * @param byteBuffer
	 * @param deviceCache
	 * @return
	 */
	public boolean getCommandAnsAndNext(ByteBuffer byteBuffer,MacCommandAntHandler... handler);
	
}
