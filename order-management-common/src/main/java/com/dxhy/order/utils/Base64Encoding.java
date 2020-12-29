package com.dxhy.order.utils;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @author ：张双超
 * @ClassName ：Base64Encoding
 * @Description ：base64加密工具类
 * @date ：2016年10月24日 下午5:06:21
 */
public class Base64Encoding {
    
    
    /**
     * @param @param  res
     * @param @return
     * @return byte[]
     * @throws
     * @Title : encode(base64加密)
     * @Description ：BASE64加密
     */
    public static byte[] encode(byte[] res) {
        return Base64.encodeBase64(res);
    }

    /**
     * BASE64加密
     * String==>String
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encode(String key) {
    
    
        return Base64.encodeBase64String(key.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * @param @param  str
     * @param @return
     * @return byte[]
     * @throws
     * @Title : decode(base64解密)
     * @Description ：base64解密
     */
    public static byte[] decode(byte[] key) {
        return Base64.decodeBase64(key);
    }
    
    /**
     * BASE64解密
     * String==>byte
     *
     * @param key
     * @return
     */
    public static byte[] decode(String key) {
        return decode(key.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * BASE64加密
     * byte==>String
     *
     * @param key
     * @return
     */
    public static String encodeToString(byte[] key) {
        return new String(encode(key), StandardCharsets.UTF_8);
    }
    
    /**
     * BASE64解密
     * String==>byte
     *
     * @param key
     * @return
     */
    public static String decodeToString(String key) {
        return new String(decode(key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
