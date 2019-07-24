package com.xier.lora.entity;

import java.util.List;

import org.springframework.util.CollectionUtils;



/**
 * 消息
 * @author lvhui5 2017年11月30日 下午5:32:58
 * @version V1.0
 */
public class LoraDataResp extends BaseResp {
	
	private List<FrmData> data;

	
	/**
	 * 是否是二阶段正常消息
	 * @author lvhui5 2017年11月30日 下午5:37:32
	 * @return
	 */
	public boolean haveMsgData(){
		if(!CollectionUtils.isEmpty(data)){
			return true;
		}
		return false;
	}

	
    public List<FrmData> getData() {
    	return data;
    }

	
    public void setData(List<FrmData> data) {
    	this.data = data;
    }


    public static class FrmData{
    	private String stringData;
    	private byte[] byteData;
		
        public FrmData(String stringData, byte[] byteData) {
	        this.stringData = stringData;
	        this.byteData = byteData;
        }

		public String getStringData() {
        	return stringData;
        }
		
        public void setStringData(String stringData) {
        	this.stringData = stringData;
        }
		
        public byte[] getByteData() {
        	return byteData;
        }
		
        public void setByteData(byte[] byteData) {
        	this.byteData = byteData;
        }
    }
	
}
