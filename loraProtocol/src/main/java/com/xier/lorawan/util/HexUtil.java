package com.xier.lorawan.util;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;



/**
 * function:16进制转换工具类
 * 该类下的方法主要用于AES加密相关的appkey、appskey、nwkskey和对应的MIC计算
 * 
 * date:2017-09-05 13:46
 */
public class HexUtil {

	/**
	 *  该字节字符串转换主要用于AES加密和MIC计算
	 * @author lvhui5 2017年12月7日 下午5:38:12
	 * @param b
	 * @return
	 */
    public static String bytesToHexString(byte b){
        byte[] bytes = new byte[1];
        bytes[0] = b;
        return bytesToHexString(bytes);
    }
    /**
     * 该字节字符串转换主要用于AES加密和MIC计算
     * @author lvhui5 2017年12月7日 下午5:39:07
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     *该字节字符串转换主要用于AES加密和MIC计算
     * @param hexString
     * @return 将十六进制转换为字节数组
     */
    public static byte[] hexStringToBytes(String hexString){
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    
    
    /**
     * 字节数组长度适配
     * 不足自动补0
     * @author lvhui5 2017年11月30日 上午11:07:32
     * @param hexBytes  
     * @param length   总长度
     * @return  
     */
    public static byte[] hexLengthAdapt(byte[] hexBytes,int length){
        if(hexBytes.length == length)
        	return hexBytes;
		byte[] returnByte = new byte[length];
		for(int i = 0; i < length ; i++){
			if(i<hexBytes.length){
				returnByte[i] = hexBytes[i];	
			}else{
				returnByte[i] = (byte)0x00;
			}
		}
		return returnByte;
    }

    
    /**
     * 随机定长字节数组
     * @author lvhui5 2017年12月4日 上午11:26:11
     * @return
     */
    public static byte[] generalHexLength(int length){
    	return hexLengthAdapt(hexStringToBytes(UUID.randomUUID().toString().replaceAll("-", StringUtils.EMPTY)),length);
    }
    
    

    public static void main(String[] args) {
        
		System.out.println(generalHexLength(10));
    	
		System.out.println(bytesToHexString(generalHexLength(5)));
		
		System.out.println(byte2hex(generalHexLength(10)));
		
		System.out.println(HexUtil.bytesToHexString(generalHexLength(10)));
    	
    	
		System.out.println(hexStringToBytes("1111c11a").length);
    	
		System.out.println(("1111c11a".getBytes()).length);
    	
    	System.out.println(Byte.MAX_VALUE);
    	Integer ss = 10;
    	System.out.println(ss.byteValue());
    	
    	
    	System.out.println(bytesToHexString(hexStringToBytes("Ab02000048")));
        
    	System.out.println(new String(hexStringToBytes("Ab02000048")));
    	
    	
    	
    	
    	System.out.println(Arrays.toString(hexStringToBytes("82C9D0F9")));

        
        
        
        byte b = (byte)0x81;
        //直接用Integer.toHexString方法处理byte需要做&运算 Integer.toBinaryString(c) 二进制转化
                System.out.println(Integer.toHexString(b));
                System.out.println(Integer.toHexString(b & 0xff));
        
       System.out.println(byte2hex(hexStringToBytes("82C9D0F9")));         
       int ivalue = 255;  
       String hexString = Integer.toHexString(ivalue);  
       System.out.println("binaryString = " + hexString);       
       System.out.println(bytesToHexString(hexStringToBytes(hexString)));
       
       
       System.out.println(byte2hex(hexLengthAdapt(hexStringToBytes(hexString),5))); 

       
       System.out.println(Integer.toBinaryString(0x01));
       System.out.println(Integer.toHexString(0x01));
       System.out.println(Integer.toBinaryString(0x60));
       
       System.out.println(Integer.toBinaryString(0x02));
       System.out.println(Integer.toBinaryString(0x04));
       System.out.println(Integer.toBinaryString(0xFC));
    }
    
    
    private static String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp;  
        }  
          
        return h;  
          
    } 
    
    
    
}
