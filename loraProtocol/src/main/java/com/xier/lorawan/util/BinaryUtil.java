package com.xier.lorawan.util;

/**
 * function:
 * date:2017-09-06 16:14
 */
public class BinaryUtil {
    /**
     * 如果两个数组内容完全一致 则返回true
     * @param source
     * @param target
     * @return
     */
    public static boolean equals(byte[] source,byte[] target){
        if(source==null && target==null){
            return true;
        }else if(source!=null && target!=null){
            if(source.length == target.length){
                return containsSameItem(source,target);
            }
        }
        return false;
    }

    private static boolean containsSameItem(byte[] source,byte[] target){
        for(int i=0;i<source.length;i++){
            if(source[i] != target[i]){
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        byte[] a = {0x11};
        byte[] b = {0x12};
        System.out.println(equals(a,b));
    }
}
