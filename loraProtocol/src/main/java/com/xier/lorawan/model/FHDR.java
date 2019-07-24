package com.xier.lorawan.model;

import com.xier.lorawan.mac.MacReq;
import com.xier.lorawan.model.fctrl.FCtrl;
import com.xier.lorawan.util.HexUtil;

/**
 * function: MACPayload的头
 * 文档 4.3.1
 * date:2017-09-06 19:01
 */
public class FHDR implements IMessageSegment{

    private byte[] devAddr;//长度4B 端地址
    private FCtrl fctrl;//1B        frame control
    private short fcnt;//长度2B frame counter 帧计数器 fcntUpLink fcntDownlink 这里是小端 直接使用buffer.*Short()方法方便很多
    private byte[] fopts;//长度0B-15B 携带MAC commands的     fport非零时，MAC命令放在fopts中。 port = 0时，MAC命令放在FRMPayload中 MacCommandsBuffer

    public FHDR(byte[] devAddr, FCtrl fctrl,short fcnt, byte[] fopts) {
        this.devAddr = devAddr;
        this.fctrl = fctrl;
        this.fcnt = fcnt;
        this.fopts = fopts;
    }
    /**
     * 
     * 创建一个新的实例FHDR. frmpayload为正常消息体
     * @param devAddr
     * @param fctrl
     * @param fcnt
     * @param macs mac命令集合  最大占空比 可以在这里设置  例如：  {0x04  0x00}  设置最大占空比为0
     */
    public FHDR(byte[] devAddr, FCtrl fctrl,short fcnt, MacReq... macs) {
        this.devAddr = devAddr;
        this.fctrl = fctrl;
        this.fcnt = fcnt;
        byte[] foptsArray = new byte[0];
        if(macs != null && macs.length > 0){
        	for(int i = 0; i< macs.length ; i++){
        		foptsArray = macs[i].payLoadMac(foptsArray);
        	}
        }
        this.fopts = foptsArray;
		//mac命令长度
        this.fctrl.setFoptsLen(Integer.valueOf(this.fopts.length).byteValue());
    }
    /**
     *  
     * 创建一个新的实例FHDR. fopts不包含mac命令
     * @param devAddr
     * @param fctrl
     * @param fcnt
     */
    public FHDR(byte[] devAddr, FCtrl fctrl,short fcnt) {
        this.devAddr = devAddr;
        this.fctrl = fctrl;
        this.fcnt = fcnt;
        byte[] foptsArray = new byte[0];
        this.fopts = foptsArray;
		//mac命令长度
        this.fctrl.setFoptsLen(Integer.valueOf(this.fopts.length).byteValue());
    }
    

    public byte[] getDevAddr() {
        return devAddr;
    }

    public void setDevAddr(byte[] devAddr) {
        this.devAddr = devAddr;
    }

    public FCtrl getFctrl() {
        return fctrl;
    }

    public void setFctrl(FCtrl fctrl) {
        this.fctrl = fctrl;
    }

    public short getFcnt() {
        return fcnt;
    }

    public void setFcnt(short fcnt) {
        this.fcnt = fcnt;
    }

    public byte[] getFopts() {
        return fopts;
    }

    public void setFopts(byte[] fopts) {
        this.fopts = fopts;
    }

    @Override
    public int length() {
        int fcntLen = 2;
        return devAddr.length + fctrl.length() + fcntLen + fopts.length;
    }

    @Override
    public String toString() {
        return "FHDR{" +
                "devAddr=" + HexUtil.bytesToHexString(devAddr) +
                ", fctrl=" + fctrl +
                ", fcnt=" + fcnt +
                ", fopts=" + HexUtil.bytesToHexString(fopts) +
                '}';
    }
}
