package com.xier.lorawan.mac;

import java.nio.ByteBuffer;


public abstract class AbstractAntMacCommand extends MacCommand implements MacAnt{

	@Override
    public final boolean getCommandAnsAndNext(ByteBuffer byteBuffer,MacCommandAntHandler... handler) {
		if(!wrapInner(byteBuffer)){
			return false;
		}
		wrap(byteBuffer);
		if(handler != null && handler.length > 0){
			for(int i = 0; i < handler.length; i++){
				try {
	                commandAntHandler(handler[i]);
                } catch (Exception e) {
                	logger.warn("getCommandAnsAndNext error:", e);
	                continue;
                }
			}
		}
		return hasNext(byteBuffer);
    }
	
	protected abstract boolean wrapInner(ByteBuffer byteBuffer);
	
	protected abstract void wrap(ByteBuffer byteBuffer);
	
	protected abstract void commandAntHandler(MacCommandAntHandler handler);

	private boolean hasNext(ByteBuffer byteBuffer){
		return byteBuffer.remaining()>0;
	}
	
}
