package com.xier.lorawan.parse.macpayload;

import java.nio.ByteBuffer;

import com.xier.lorawan.enums.MType;
import com.xier.lorawan.model.FHDR;
import com.xier.lorawan.model.message.MAC;
import com.xier.lorawan.util.MessageSerializeUtil;

/**
 * function:解析正常传输消息时消息体 已排除头部mhdr
 * date:2017-09-09 11:49
 */
public class MacParser {
    FHDRParser fhdrParser = new FHDRParser();

    public MAC parse(MType mType,ByteBuffer macPayload){
        FHDR fhdr = fhdrParser.parse(mType,macPayload);

        MAC mac = new MAC();
        mac.setFhdr(fhdr);
        //解析完fhdr 开始解析fport和frmPayload
        //这里要注意一种情况 就是fport和frmPayload都为空 或者fport不等于0 frmPayload为空的情况
        if(macPayload.remaining() > 4){
            byte[] fport = new byte[1];
            macPayload.get(fport);
            mac.setFport(fport);
            //有frmPayload
            if(macPayload.remaining()>4){
                byte[] frmPayload = new byte[macPayload.remaining()-4];//4是mic的长度 这里的frmPayload是经过appSKey加密的
                macPayload.get(frmPayload);
                mac.setFrmPayload(frmPayload);		
            }else{//没有frmPayload fport不应该等于0
            	if(fport[0] == 0){
        			throw new UnsupportedOperationException(
        					"Unsupported lora fport: ");	
            	}
            }
        }
        return mac;
    }

    public byte[] binary(MType mType,MAC mac){
        return MessageSerializeUtil.toBinary(mType,mac);
    }

}
