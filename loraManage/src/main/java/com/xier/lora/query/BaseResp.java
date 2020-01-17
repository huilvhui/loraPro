package com.xier.lora.query;



import com.xier.lora.sys.constant.EnumErrorDict;

/**
 * <p>
 * 响应基类
 * </p>
 * @author lvhui5 2017年5月18日 上午11:23:28
 * @version V1.0
 */
public class BaseResp {
	
	private String errorCode = "0";
	
	private String errorInfo = "success";
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.setErrorCode(errorCode, new Object[] {});
	}

	public void setErrorCode(String errorCode, Object... params) {
		this.errorCode = errorCode;
		this.setErrorInfo(EnumErrorDict.getErrorInfo(errorCode, params));
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
	
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	/**
	 * 判断是否出错<br>
	 * true - 出错<br>
	 * false - 未出错
	 * @return b
	 */
	//@JsonIgnore
	public boolean isError() {
		return !"0".equals(this.errorCode);
	}
	
}
