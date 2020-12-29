package com.dxhy.order.consumer.utils;
/**
 * AES 128bit 加密解密工具类
 *
 * @author dufy
 */

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class AesEncryptUtil {
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/NOPadding";
    
    /**
     * 使用AES-128-CBC加密模式，key需要为16位,key和iv可以相同！
     */
    private static final String KEY = "1234567890123456";
    
    private static final String IV = "1234567890123456";
    
    private static final String KEY_ALGORITHM = "AES";
    
    /**
     * 加密方法
     *
     * @param data 要加密的数据
     * @param key  加密key
     * @param iv   加密iv
     * @return 加密的结果
     */
    public static String encrypt(String data, String key, String iv) {
        try {
            //"算法/模式/补码方式"NoPadding PkcsPadding
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            int blockSize = cipher.getBlockSize();
    
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
    
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
    
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);
    
            return new String(Base64Encoding.decode(encrypted), StandardCharsets.UTF_8);
    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     * @throws Exception
     */
    public static String desEncrypt(String data, String key, String iv) {
        try {
            byte[] encrypted1 = Base64Encoding.decode(data.getBytes(StandardCharsets.UTF_8));
    
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
    
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
    
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, StandardCharsets.UTF_8);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String decryptAes(String data, String pass) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] result = cipher.doFinal(Base64Encoding.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8).trim();
    }
    
    /**
     * 使用默认的key和iv加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String encrypt(String data) {
        return encrypt(data, KEY, IV);
    }
    
    /**
     * 使用默认的key和iv解密
     * @param data
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKeySpec keyspec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(KEY.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
        byte[] result = cipher.doFinal(Base64Encoding.decode(data.getBytes(StandardCharsets.UTF_8)));
        return new String(result, StandardCharsets.UTF_8).trim();
    }
    
    
    /**
     * 测试
     */
    public static void main(String[] args) {
    
        String test1 = "12345678";
        String test = new String(test1.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        String data = null;
        String key = KEY;
        String iv = IV;
        // /g2wzfqvMOeazgtsUVbq1kmJawROa6mcRAzwG1/GeJ4=
        data = encrypt(test, key, iv);
        System.out.println("数据：" + test);
        System.out.println("加密：" + data);
        String jiemi = desEncrypt("Qxx/Jo66zZGnoM/blCdATg==", key, iv).trim();
        System.out.println("解密：" + jiemi);
        
        
    }
    
}
