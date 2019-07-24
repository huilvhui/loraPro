package com.xier.lorawan.model;

import com.xier.lorawan.enums.MType;
import com.xier.lorawan.model.message.Message;
import com.xier.lorawan.util.HexUtil;

/**
 * function:
 * 消息格式参考:http://blog.csdn.net/qingchuwudi/article/details/51859315
 * date:2017-09-06 19:00
 */
public class PHYPayload implements IMessageSegment {
    private MHDR mhdr;//1B 头信息
    private Message message;//变长 request=12B accept=[12,12+16] mac=[7,max]
    private byte[] mic;//4B 一致性校验码

    public PHYPayload(MHDR mhdr, Message message, byte[] mic) {
        this.mhdr = mhdr;
        this.message = message;
        this.mic = mic;
    }
    //这里会自动重新计算 先传空
    public PHYPayload(MType mtype, Message message) {
    	this.mhdr = MHDR.wrap(mtype.getMhdrByte());
        this.message = message;
        this.mic = null;
    }
    
    public static void main(String[] args){
    	byte mhdrByte = (byte)(MType.UN_CONFIRMED_DATA_DOWN.getmTypeValue() << 5);
    	System.out.println(Integer.toBinaryString(mhdrByte));
    	System.out.println(Integer.toHexString(mhdrByte));
        System.out.println(Integer.toHexString(mhdrByte & 0xff));
    }
    
    
    public MHDR getMhdr() {
        return mhdr;
    }

    public void setMhdr(MHDR mhdr) {
        this.mhdr = mhdr;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getMic() {
        return mic;
    }

    public void setMic(byte[] mic) {
        this.mic = mic;
    }

    @Override
    public int length() {
        return mhdr.length() + message.length() + mic.length;
    }

    @Override
    public String toString() {
        return "PHYPayload{" +
                "mhdr=" + HexUtil.bytesToHexString(mhdr.get()) +
                ", message=" + message +
                ", mic=" + HexUtil.bytesToHexString(mic) +
                '}';
    }
}
