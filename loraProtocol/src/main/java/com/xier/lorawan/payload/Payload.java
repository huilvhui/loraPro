package com.xier.lorawan.payload;

/**
 * function:
 * date:2017-09-06 15:36
 */
public interface Payload {
    byte[] reCalculateMic(byte[] key);
}
