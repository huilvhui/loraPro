package com.xier.lorawan.parse.macpayload;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xier.lorawan.enums.MType;
import com.xier.lorawan.model.FHDR;
import com.xier.lorawan.model.fctrl.FCtrl;

/**
 * function:针对macPayload进行解析 注意这里mhdr不应该再出现在头
 * date:2017-09-09 11:05
 */
public class FHDRParser {
    FCtrlParser fCtrlParser = new FCtrlParser();
    //data的第一位已经过滤掉 mhdr
    protected FHDR parse(MType mType,ByteBuffer macPayloadLeft){
        assert macPayloadLeft.remaining() >=7;
        return begin(mType,macPayloadLeft);
    }

    public FHDR parse(MType mType,byte[] fhdr){
        ByteBuffer byteBuffer = ByteBuffer.wrap(fhdr);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return begin(mType,byteBuffer);
    }

    private FHDR begin(MType mType,ByteBuffer macPayloadLeft){
        byte[] devAddr = new byte[4];
        macPayloadLeft.get(devAddr);
        FCtrl fCtrl = fCtrlParser.parse(mType,macPayloadLeft);
        //这里是不固定的
        short fcnt = macPayloadLeft.getShort();
        /*try {
	        macPayloadLeft.get(fcnt);
        } catch (IndexOutOfBoundsException e) {
        	throw new RuntimeException("fcnt is null");
        }*/
        int foptsLen = fCtrl.getFoptsLen();
        byte[] fopts = new byte[foptsLen];//依赖fctrl中的foptsLen字段
        macPayloadLeft.get(fopts);

        return new FHDR(devAddr,fCtrl,fcnt,fopts);
    }


}
