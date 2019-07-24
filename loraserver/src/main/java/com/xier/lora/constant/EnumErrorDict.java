package com.xier.lora.constant;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;



/**
 * 
 * <p>
 * 错误信息维护
 * </p>
 * @author lvhui5 2017年6月20日 下午4:15:41
 * @version V1.0
 */
public enum EnumErrorDict {
	
	
	UN_KNOWN_ERROR(IGeneralErrorCode.UN_KNOWN_ERROR,"internal error"),
	PARAMS_ERROR(IGeneralErrorCode.PARAMS_ERROR,"param  error : {0}"),
	MSG_PUBLISH_FAILED(IGeneralErrorCode.MSG_PUBLISH_FAILED,"publish error : {0}"),
	DATA_DECRYPT_FAILED(IGeneralErrorCode.DATA_DECRYPT_FAILED,"decrypt error"),
	NEGOTIATION_OUTDATE(IGeneralErrorCode.NEGOTIATION_OUTDATE,"access authentication failed"),
	DEVICE_OFFLINE(IGeneralErrorCode.DEVICE_OFFLINE,"device offline: {0}"),
	DATA_ENCRYPT_FAILED(IGeneralErrorCode.DATA_ENCRYPT_FAILED,"encrypt error: {0}"),
	
    ;
	private static final String PLACE_HOLDER_0 = "{0}";
    private String key;
    private String description;
    
    /**
     * 全局索引池
     */
    private static Map<String, EnumErrorDict> pool = new HashMap<String, EnumErrorDict>();
    static {
        for (EnumErrorDict et : EnumErrorDict.values()) {
            pool.put(et.key, et);
        }
    }
    
    private EnumErrorDict(String key,String description) {
    	this.key = key;
    	this.description = description;
    }

    public String getDescription() {
        return description;
    }

	public String getKey() {
		return key;
	}
	
	
    /**
     * 根据内容索引
     * @param value
     * @return
     */
    public static EnumErrorDict indexByValue(String value) {
        return pool.get(value);
    }

	public static String getErrorInfo(String errorCode, Object[] params) {
		if ("0".equals(errorCode)) {
			return "操作成功！";
		}
		EnumErrorDict hsError = pool.get(errorCode);
		if (hsError != null) {// 有配置对应的错误信息
			String errorInfo = hsError.getDescription();
			if (errorInfo.contains(PLACE_HOLDER_0)) {
				return MessageFormat.format(errorInfo, params);
			} else {
				return MessageFormat.format(PLACE_HOLDER_0, errorInfo);
			}
		} else {
			// 未配置对应的错误信息，直接抛出异常
			// throw new RuntimeException("错误信息为空，错误号[" + errorNo
			// + "]，请确认已配置！");
			return "错误信息为空，错误号[" + errorCode + "]，请确认已配置";
		}
    }
}
