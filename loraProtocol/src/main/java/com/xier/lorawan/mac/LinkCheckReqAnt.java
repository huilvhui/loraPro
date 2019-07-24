package com.xier.lorawan.mac;

import java.nio.ByteBuffer;

/**
 *  终端连接状态请求  (由终端发起)
 * <p>
 * 
 * </p>
 * @author lvhui5 2017年12月26日 上午10:45:18
 * @version V1.0
 */
public class LinkCheckReqAnt extends  AbstractAntMacCommand{

	@Override
    protected byte getType() {
	    return 0x02;
    }

	@Override
    protected int getLength() {
	    return 1;
    }
	
	@Override
    protected void wrap(ByteBuffer byteBuffer) {
	    //do nothing 
    }
	
	@Override
    protected boolean wrapInner(ByteBuffer byteBuffer) {
		return true;
	}
	@Override
    protected void commandAntHandler(MacCommandAntHandler handler) {
		/**
		 * 终端在线检查返回更新设备在线状态
		 */
		handler.linkCheck();
		if(logger.isDebugEnabled()){
			logger.debug("==============LinkCheckReqAnt getCommandReq");
		}
    }
}
