package com.xier.lorawan.mac;


/**
 * 占空比命令
 * <p>
 * </p>
 * @author lvhui5 2017年12月25日 下午3:07:39
 * @version V1.0
 */
public class DutyCycleReq extends  AbstractReqMacCommand{
	
	/**
	 * Bits	        7:4	 3:0
	 * DutyCyclePL	RFU	MaxDCycle
	 */
	private byte dutyCyclePL;

	/**
	 * 
	 * 创建一个新的实例DutyCycleReq.
	 * @param byteValue  占空比的命令内容
	 */
    public DutyCycleReq(byte byteValue) {
    	//设置设备的最大总发射占空比  默认是0
	    this.dutyCyclePL = byteValue;
    }

	public byte getDutyCyclePL() {
    	return dutyCyclePL;
    }
	
    public void setDutyCyclePL(byte dutyCyclePL) {
    	this.dutyCyclePL = dutyCyclePL;
    }

	@Override
    public byte getType() {
	    return EnumMacType.DutyCycle.getType();
    }

	@Override
    public int getLength() {
	    return 2;
    }


	@Override
    protected void inner(byte[] newArray) {
		newArray[newArray.length-1] = this.dutyCyclePL; 
    }
}
