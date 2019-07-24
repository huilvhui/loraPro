package com.xier.lorawan.model.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.xier.lorawan.mac.EnumMacType;
import com.xier.lorawan.mac.MacReq;
import com.xier.lorawan.mac.MockMacCommandAntHandlerImpl;
import com.xier.lorawan.model.FHDR;
import com.xier.lorawan.model.fctrl.FCtrlUplink;
import com.xier.lorawan.util.HexUtil;

/**
 * function:表示已经握手成功,正常通讯下的数据格式
 * data frame 数据帧
 * date:2017-09-06 19:01
 */
public class MAC extends Message{
    private FHDR fhdr;//7-23B 帧头信息
    //帧负载数据（FRMPayload）不为空的时候端口号也不能是空。此时FPort=0表示FRMPayload中只有MAC命令,fport非零时，MAC命令放在fopts中。
    private byte[] fport = new byte[0];//0-1B 0x00-nwkSKey 0x01-0xDF 1-223     224是测试端口 225-255是保留 -appSKey 
    private byte[] frmPayload = new byte[0];//真正的消息体 0-N m 长度肯定是16的倍数 加密前后位数不变 fport = 0时，MAC命令放在FRMPayload中
//0x00
   
 
    public FHDR getFhdr() {
        return fhdr;
    }
    public MAC() {
	    super();
    }
    /**
     * 
     * 创建一个新的实例MAC.
     * @param fhdr
     * 否： fport=2 
     * @param frmPayload 消息内容
     */
    public MAC(FHDR fhdr,byte[] frmPayload) {
	    super();
	    this.fhdr = fhdr;
		// fport加密的秘钥选择 appSKey加密
		byte[] setFport = {0x02};
	    this.fport = setFport;
		if(frmPayload != null){
			//注意这里请传明文 来自设备端的待加密数据
		    this.frmPayload = frmPayload;
	    }
    }
    
    /**
     * 
     * 创建一个新的实例MAC.
     * 是： fport=0
     * mac命令放入frmpayload
     * @param fhdr
     * @param macs mac命令放入frm中
     */
    public MAC(FHDR fhdr,MacReq... macs) {
	    super();
	    this.fhdr = fhdr;
		byte[] setFport = {0x00};
	    this.fport = setFport;
	    byte[] foptsArray = new byte[0];
        if(macs != null && macs.length > 0){
        	for(int i = 0; i< macs.length ; i++){
        		foptsArray = macs[i].payLoadMac(foptsArray);
        	}
        }
		if(foptsArray != null){
			//注意这里请传明文 来自设备端的待加密数据
		    this.frmPayload = foptsArray;
	    }
    }
	public void setFhdr(FHDR fhdr) {
        this.fhdr = fhdr;
    }

    public byte[] getFport() {
        return fport;
    }

    public void setFport(byte[] fport) {
        this.fport = fport;
    }

    public byte[] getFrmPayload() {
        return frmPayload;
    }

    public void setFrmPayload(byte[] frmPayload) {
        this.frmPayload = frmPayload;
    }

    /**
     * 是否是ack mac消息
     * @author lvhui5 2017年12月15日 上午9:58:27
     * @return
     */
    public boolean isAck(){
    	return (byte)0x01 == getFhdr().getFctrl().getAck();
    }
	/**
	 * macCommand是否在frmpayload消息体内
	 * Fport == 0:FrmPayload只有mac命令
	 * Fport > 0:mac命令在Fopts中
     * @author lvhui5 2017年12月26日 下午4:21:55
     * @return
     */
    public boolean macCommandInFrmPayload(){
		if(this.fport == null || this.fport.length == 0)
			return false;
		if(Integer.valueOf(this.fport[0]) == 0)
			return true;
    	return false;
    }
    /**
     * 是否数据速率衰减
     * 衰减规则：
     * 0x01 表示信号有衰减 下降传输速率
     * rx1窗口时间接收不到ack消息
     * 
     * @author lvhui5 2017年12月6日 下午5:46:25
     * @return
     */
    public boolean  drReduce(){
    	if(!(getFhdr().getFctrl() instanceof FCtrlUplink))
    		return false;
    	return ((FCtrlUplink)getFhdr().getFctrl()).getAdrAckReq() == (byte)0x01;
    }
    /**
     * 设备上报mac命令是否包含linkcheck
     * @author lvhui5 2018年1月4日 上午10:30:13
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public boolean includeLinkcheckReq(){
		byte[] commands = null;
		if(macCommandInFrmPayload()){
			commands = getFrmPayload();
		}else{
			commands = getFhdr().getFopts();
		}
		if(commands == null || commands.length == 0)
			return false;
        ByteBuffer commandBuffer = ByteBuffer.wrap(commands);
        commandBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte type;
		try {
	        do{
	        	type = commandBuffer.get();
	        	if(EnumMacType.LinkCheck == EnumMacType.indexByValue(type))
	        		return true;
	        }while(EnumMacType.indexByValue(type) != null
	                && EnumMacType.indexByValue(type).getCommandAnt().newInstance().getCommandAnsAndNext(commandBuffer, new MockMacCommandAntHandlerImpl()));
        } catch (InstantiationException e) {
	       return false;
        } catch (IllegalAccessException e) {
 	       return false;
        }
		return false;	
    }
    
    @Override
    public int length() {
        return fhdr.length() + fport.length + frmPayload.length;
    }

    @Override
    public String toString() {
        return "MAC{" +
                "fhdr=" + fhdr +
                ", fport=" + HexUtil.bytesToHexString(fport) +
                ", frmPayload=" + new String(frmPayload) +
                '}';
    }
}
