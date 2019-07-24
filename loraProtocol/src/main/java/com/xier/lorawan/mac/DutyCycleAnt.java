package com.xier.lorawan.mac;

import java.nio.ByteBuffer;

/**
 * 占空比命令回复 不包含任何payload
 * <p>
 * 服务端对终端的占空比命令回复只有类型 没有内容
 * </p>
 * @author lvhui5 2017年12月25日 下午3:07:39
 * @version V1.0
 */
public class DutyCycleAnt extends  AbstractAntMacCommand{

	@Override
    public byte getType() {
	    return EnumMacType.DutyCycle.getType();
    }

	@Override
    public int getLength() {
	    return 1;
    }


	@Override
    protected void wrap(ByteBuffer byteBuffer) {
	    //do nothing 
    }

	@Override
    protected void commandAntHandler(MacCommandAntHandler handler) {
		/**
		 * 处理逻辑
		 */
		if(logger.isDebugEnabled()){
			logger.debug("==============DutyCycleAnt getCommandAns");
		}
	    
    }

	@Override
    protected boolean wrapInner(ByteBuffer byteBuffer) {
	    return true;
    }
	
	
}
