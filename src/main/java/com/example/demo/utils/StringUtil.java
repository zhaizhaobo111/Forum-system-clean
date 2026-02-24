package com.example.demo.utils;

import java.util.UUID;

public class StringUtil {
    /**
     * 判断字符串是否为空
     * @param value 判断的字符串
     * @return true 为空<br/>false 不为空
     */
    public static boolean isempty(String value){
        return value==null|| value.length()==0;
    }

}
