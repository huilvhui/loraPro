package com.xier.lorawan.model.message;

import com.xier.lorawan.util.HexUtil;


/**
 * function:join-request模式下的数据结构
 * date:2017-09-06 19:01
 */
public class JoinRequest extends Message{
    private byte[] appEUI;//后台配置8B
    private byte[] devEUI;//后台配置8B
    private byte[] devNonce;//这个是设备随机生成的2B

    public JoinRequest(byte[] appEUI, byte[] devEUI, byte[] devNonce) {
        this.appEUI = appEUI;
        this.devEUI = devEUI;
        this.devNonce = devNonce;
    }

    public byte[] getAppEUI() {
        return appEUI;
    }

    public void setAppEUI(byte[] appEUI) {
        this.appEUI = appEUI;
    }

    public byte[] getDevEUI() {
        return devEUI;
    }

    public void setDevEUI(byte[] devEUI) {
        this.devEUI = devEUI;
    }

    public byte[] getDevNonce() {
        return devNonce;
    }

    public void setDevNonce(byte[] devNonce) {
        this.devNonce = devNonce;
    }

    @Override
    public String toString() {
        return "JoinRequest{" +
                "appEUI=" + HexUtil.bytesToHexString(appEUI) +
                ", devEUI=" + HexUtil.bytesToHexString(devEUI) +
                ", devNonce=" + HexUtil.bytesToHexString(devNonce) +
                '}';
    }

    @Override
    public int length() {
        return appEUI.length + devEUI.length + devNonce.length;
    }
}
