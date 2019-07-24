package com.xier.lorawan.mac;


public class LinkADRReq  extends  AbstractReqMacCommand{

	/**
	 * 数据速率及tx输出功率
	 * 命令中的TX输出功率指的是设备可以使用的最大发射功率
	 * 该命令用于控制终端的数据速率
	 * 
	 *	大小(位 bits)	     [7:4]	   [3:0]
	 *	DataRate_TXPower	DataRate	TXPower
	 */
	private byte dataRate_TXPower = 0x00;
	/**
	 * 信号编码
	 * ChMaskCntl	ChMask用于
		0	信道 0 到 15
		1	信道 16 到 31
		2	信道 32 到 47
		3	信道 48 到 63
		4	信道 64 到 79
		5	信道 80 到 95
		6	所有信道打开，不管ChMask字段，所有定义的信道都可用
		7	RFU
	 */
	private byte[] chMask = new byte[2];

	/**
	 * 冗余位置
	 */
	private byte redundancy = 0x00;
	

    public byte getDataRate_TXPower() {
    	return dataRate_TXPower;
    }

    public LinkADRReq(byte dataRate,byte TXPower, byte[] chMask, byte redundancy) {
	    super();
    	this.dataRate_TXPower = (byte)(dataRate << 4 | TXPower);
	    this.chMask = chMask;
	    this.redundancy = redundancy;
    }

	public void setDataRate_TXPower(byte dataRate_TXPower) {
    	this.dataRate_TXPower = dataRate_TXPower;
    }
    
    public void setDataRate_TXPower(byte dataRate,byte TXPower) {
    	this.dataRate_TXPower = (byte)(dataRate << 4 | TXPower);
    }
    
	
    public byte[] getChMask() {
    	return chMask;
    }

	
    public void setChMask(byte[] chMask) {
    	this.chMask = chMask;
    }

	
    public byte getRedundancy() {
    	return redundancy;
    }

	
    public void setRedundancy(byte redundancy) {
    	this.redundancy = redundancy;
    }

	@Override
    protected byte getType() {
	    return EnumMacType.LinkADR.getType();
    }

	@Override
    protected int getLength() {
	    return 5;
    }
	

	@Override
    protected void inner(byte[] newArray) {
		//mac命令类型
		newArray[newArray.length-4] = this.dataRate_TXPower;
		newArray[newArray.length-3] = this.chMask[0];
		newArray[newArray.length-2] = this.chMask[1];
		newArray[newArray.length-1] = this.redundancy;
    }
}
