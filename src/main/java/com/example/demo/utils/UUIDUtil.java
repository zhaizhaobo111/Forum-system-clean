package com.example.demo.utils;

import java.util.UUID;
/**
 *生成一个标准的UUID的字符
 * @return
 */
public class UUIDUtil {
    public static String uuid_36() {
        return UUID.randomUUID().toString();

    }

    /**
     * 生成一个32位的UUID的字符
     * @return
     */
    public static String uuid_32() {
        return UUID.randomUUID().toString().replace("-", "");

    }
}
