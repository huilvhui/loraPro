package com.xier.lorawan.model.message;

/**
 * function:join-accept模式下的数据结构 数据由server端生成
 * date:2017-09-06 19:01
 */
public class JoinAccept extends Message{
    private byte[] appNonce;//3B server端随机生成的
    private byte[] netId;//3B    网络地址
    private byte[] devAddr;//4B  server devEui置换的短地址
    private byte[] dlSettings;//1B
    private byte[] rxDelay;//1B
    private byte[] cfList;//16B变长

    /**
     * 
     * 创建一个新的实例JoinAccept.
     * @param appNonce 随机3个字节
     * @param netId
     * @param devAddr
     * @param dlSettings
     * @param rxDelay
     * @param cfList
     */
    public JoinAccept(byte[] appNonce, byte[] netId, byte[] devAddr, byte[] dlSettings, byte[] rxDelay, byte[] cfList) {
        this.appNonce = appNonce;
        this.netId = netId;
        this.devAddr = devAddr;
        this.dlSettings = dlSettings;
        this.rxDelay = rxDelay;
        this.cfList = cfList;
    }

    public byte[] getAppNonce() {
        return appNonce;
    }

    public void setAppNonce(byte[] appNonce) {
        this.appNonce = appNonce;
    }

    public byte[] getNetId() {
        return netId;
    }

    public void setNetId(byte[] netId) {
        this.netId = netId;
    }

    public byte[] getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(byte[] devAddr) {
        this.devAddr = devAddr;
    }

    public byte[] getDlSettings() {
        return dlSettings;
    }

    public void setDlSettings(byte[] dlSettings) {
        this.dlSettings = dlSettings;
    }

    public byte[] getRxDelay() {
        return rxDelay;
    }

    public void setRxDelay(byte[] rxDelay) {
        this.rxDelay = rxDelay;
    }

    public byte[] getCfList() {
        return cfList;
    }

    public void setCfList(byte[] cfList) {
        this.cfList = cfList;
    }

    @Override
    public int length() {
        return appNonce.length + netId.length + devAddr.length + dlSettings.length + rxDelay.length + cfList.length;
    }
}
