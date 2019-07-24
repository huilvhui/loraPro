package com.xier.lorawan.enums;

/**
 * function:消息类型
 * date:2017-09-06 15:26
 */
public enum MType {
    //入网请求
    JOIN_REQUEST((byte)0x00),
    //入网接受请求
    JOIN_ACCEPT((byte)0x01),
    //无需确认的上行消息，接收者不必回复 UDP
    UN_CONFIRMED_DATA_UP((byte)0x02),
    //无需确认的下行消息，接收者不必回复 UDP
    UN_CONFIRMED_DATA_DOWN((byte)0x03),
    //需要确认的上行消息，接收者必须回复 TCP
    CONFIRMED_DATA_UP((byte)0x04),
    //需要确认的下行消息，接收者必须回复 TCP
    CONFIRMED_DATA_DOWN((byte)0x05),
    //保留
    RFU((byte)0x06),
    //	用来实现自定义格式的消息，交互的设备之间必须有相同的处理逻辑，不能和标准消息互通 需要专利
    PROPRIETARY((byte)0x07)
    ;

    public static MType wrap(byte mTypeValue){
        switch (mTypeValue){
            case 0x00:
                return JOIN_REQUEST;
            case 0x01:
                return JOIN_ACCEPT;
            case 0x02:
                return UN_CONFIRMED_DATA_UP;
            case 0x03:
                return UN_CONFIRMED_DATA_DOWN;
            case 0x04:
                return CONFIRMED_DATA_UP;
            case 0x05:
                return CONFIRMED_DATA_DOWN;
            case 0x06:
                return RFU;
            case 0x07:
                return PROPRIETARY;
        }
        throw new RuntimeException("未被支持的MType");
    }

    private byte mTypeValue;
    
    public byte getmTypeValue() {
    	return mTypeValue;
    }
    
    public byte getMhdrByte() {
    	return (byte)(getmTypeValue() << 5);
    }
    
	MType(byte mTypeValue) {
        this.mTypeValue = mTypeValue;
    }

    //根据文档看 0代表uplink 1代表downlink
    public byte getDir(){
        switch (this){
            case JOIN_REQUEST:
            case UN_CONFIRMED_DATA_UP:
            case CONFIRMED_DATA_UP:
                return 0x00;
            case JOIN_ACCEPT:
            case UN_CONFIRMED_DATA_DOWN:
            case CONFIRMED_DATA_DOWN:
                return 0x01;
			default:
				break;
        }
        throw new RuntimeException("未被支持的Dir");
    }

    public UpDownLink upDownLink(){
        byte dir = getDir();
        if(dir == 0x00){
            return UpDownLink.UP_LINK;
        }else if(dir == 0x01){
            return UpDownLink.DOWN_LINK;
        }
        throw new RuntimeException("上行 下行未知");
    }

}
