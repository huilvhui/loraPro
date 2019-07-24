package com.xier.lorawan.model;

import com.xier.lorawan.enums.MType;

/**
 * function: 头信息 本身只有一个字节(8bits) 000 000 00
 * date:2017-09-06 19:00
 */
public class MHDR implements IMessageSegment{
    private MType mtype;//5..6..7..  message type
    private byte rfu;//2..3..4 保留
    private byte major;//0..1 //lorawan 主版本  00(0x00) LoRaWAN R1     01(0x01) 10(0x02) 11(0x03) RFU保留

    private byte mhdr;

    //传入1字节mhdr自动包装
    public static MHDR wrap(byte mhdr){
        MHDR entity = new MHDR();
        entity.major = (byte)(mhdr & 0x03);//0000 0011
        entity.rfu = (byte)((mhdr & 0x1C) >> 2);//0001 1100
        byte mTypeValue = (byte)((mhdr & 0xE0) >> 5);//1110 0000
        entity.mtype = MType.wrap(mTypeValue);
        entity.mhdr = mhdr;
        return entity;
    }
    
    
    
    
    public static void main(String[] args){
    	System.out.println(Integer.toHexString(0x02));
    	
    	Byte ss = (byte)((0x21 & 0x1C) >> 2);
    	System.out.println(ss.intValue());
    }

    public byte get(){
        return mhdr;
    }

    public MType getMtype() {
        return mtype;
    }

    public byte getRfu() {
        return rfu;
    }

    public byte getMajor() {
        return major;
    }

    @Override
    public int length() {
        return 1;
    }
}
