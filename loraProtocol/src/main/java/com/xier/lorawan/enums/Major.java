package com.xier.lorawan.enums;

/**
 * function:
 * date:2017-09-06 19:12
 * author: lvhui
 */
public enum  Major {
    LORA_WAN_R1((byte)0x00),
    RFU((byte)0x01)
    ;
    private byte major;
    Major(byte major) {
        this.major = major;
    }
	
    public byte getMajor() {
    	return major;
    }
    
}
