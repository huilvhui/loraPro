package com.xier.lorawan.mac;



public abstract class AbstractReqMacCommand extends MacCommand implements MacReq{
	

	protected abstract void inner(byte[] newArray);
	
	@Override
    public byte[] payLoadMac(byte[] macByteArray) {
		byte[] newArray; 
		if(macByteArray == null)
			newArray = new byte[getLength()];
	    else{
	    	newArray = new byte[macByteArray.length + getLength()];
	    	for(int i = 0 ; i < macByteArray.length ; i++){
	    		newArray[i] = macByteArray[i];
	    	}
	    }
		//类型位
		newArray[newArray.length-getLength()] = getType();
		inner(newArray);
		return newArray;
    }

	@Override
    public int getReqLength() {
	    return getLength();
    }
}
