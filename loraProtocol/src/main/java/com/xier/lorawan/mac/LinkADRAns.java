package com.xier.lorawan.mac;

import java.nio.ByteBuffer;

/**
 * 服务配置客户端的adr回复
 * <p></p>
 * @author lvhui5 2017年12月25日 下午5:46:31
 * @version V1.0
 */
public class LinkADRAns  extends AbstractAntMacCommand{
	/**
	 * 
	 *	大小（位）	[7:3]	2	              1	         0
	 *	Status bits	RFU	Power ACK	Data rate ACK	Channel mask ACK
	 */
	private byte status;
	
	public byte getStatus() {
		return status;
	}
	
	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getPowerACK(){
		return 	(byte)(status & 0xFC >> 2);
	}
	
	public byte getDatarateACK(){
		return (byte)(status & 0x02 >> 1);
	}
	
	public byte getChannelmaskACK(){
		return (byte)(status & 0x01);
	}
	
	public String setADRResult(){
		StringBuffer buffer = new StringBuffer(64);// 用于存储验证后的错误信息
		if((byte)(status & 0xFC >> 2) != 0x01){
			buffer.append("set power error"+ ";");
			buffer.append("\n");
		}
		if((byte)(status & 0x02 >> 1) != 0x01){
			buffer.append("set datarate error"+ ";");
			buffer.append("\n");
		}
		if((byte)(status & 0x01) != 0x01){
			buffer.append("set channelmask error"+ ";");
			buffer.append("\n");
		}
		return buffer.toString();
	}
	
	
	
	
	@Override
    protected byte getType() {
	    return EnumMacType.LinkADR.getType();
    }

	@Override
    protected int getLength() {
	    return 2;
    }

	@Override
    protected void wrap(ByteBuffer byteBuffer) {
		this.status = byteBuffer.get();	
    }

	@Override
    protected boolean wrapInner(ByteBuffer byteBuffer) {
		int remain = byteBuffer.remaining();
		if(remain < 1){
			logger.error("==============LinkADRAns size expect 1 but " + remain);
			return false;
		} 
		return true;
	}
	@Override
    protected void commandAntHandler(MacCommandAntHandler handler) {
		/**
		 * 处理逻辑
		 */
		handler.linkAdr(setADRResult());
		if(logger.isDebugEnabled()){
			logger.debug("==============LinkADRAns getCommandAns");
		}
    }

}
