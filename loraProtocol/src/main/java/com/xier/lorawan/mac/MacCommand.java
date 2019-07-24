package com.xier.lorawan.mac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mac命令
 * <p></p>
 * @author lvhui5 2017年12月25日 下午3:07:07
 * @version V1.0
 */
public abstract class MacCommand{
	
	protected static final Logger logger = LoggerFactory.getLogger(MacCommand.class);
	/**
	 * 命令类型
	 * @author lvhui5 2017年12月25日 下午3:26:53
	 * @return
	 */
	protected abstract byte getType();
	
	/**
	 * mac命令字节总长度 包含type位
	 * @author lvhui5 2017年12月25日 下午3:26:25
	 * @return
	 */
	protected abstract int getLength();
		
}
