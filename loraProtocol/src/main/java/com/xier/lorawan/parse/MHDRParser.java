package com.xier.lorawan.parse;

import java.nio.ByteBuffer;

import com.xier.lorawan.model.MHDR;
import com.xier.lorawan.util.MessageSerializeUtil;

/**
 * function:解析消息头部第一个字节
 * date:2017-09-09 11:37
 */
public class MHDRParser {

    public MHDR parse(ByteBuffer phyPayload){
        return MHDR.wrap(phyPayload.get());
    }

    public byte binary(MHDR mhdr){
        return MessageSerializeUtil.toBinary(mhdr);
    }

}
