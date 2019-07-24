package com.xier.lorawan.payload;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * function:入口路由 自动解析mhdr 选择对应的处理器
 * date:2017-09-06 15:24
 */
public class PayloadRoute {

    private byte mhdr;

    public void parse(String payload){
        //
        ByteBuffer payloadByteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(payload));
        payloadByteBuffer.get(mhdr);

    }



}
