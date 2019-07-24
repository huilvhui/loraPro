package com.xier.lora.gateway.dto;

import org.codehaus.jackson.annotate.JsonIgnore;



/**
 * 下行返回数据
 * <p>
 * 网关协议的下行回复json
 * </p>
 * @author lvhui5 2017年11月20日 下午7:26:58
 * @version V1.0
 */
public class DownstreamAck extends BaseData{
	
	private String error;
	
	public String getError() {
		return error;
	}
	
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * 判断是否出错<br>
	 * true - 出错<br>
	 * false - 未出错
	 * @return b
	 */
	@JsonIgnore
	public boolean isError() {
		return !"0".equals(this.error);
	}

	
}
