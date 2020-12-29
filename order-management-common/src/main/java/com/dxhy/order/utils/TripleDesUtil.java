package com.dxhy.order.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


/**
 * 3DES加解密工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:45
 */
@Slf4j
public class TripleDesUtil {
    
    
    /**
     * 定义 加密算法,可用 DES,DESede,Blowfish
     */
    private static final String ALGORITHM = "DESede";
    
    /**
     * 加密算法
     * password为加密密钥，长度24字节
     * src为被加密的数据缓冲区
     */
    
    
    public static byte[] encryptMode(String password, byte[] src) {
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            //加密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            log.error("3DES加密异常:{}", e1);
            e1.printStackTrace();
        } catch (Exception e2) {
            log.error("3DES加密异常:", e2);
            e2.printStackTrace();
        }
        return null;
    }
    
    
    /**
     * 解密算法
     * keybyte为加密密钥，长度�?4字节
     * src为被加密的数据缓冲区（源�?
     */
    public static byte[] decryptMode(String password, byte[] src) {
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            //解密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (javax.crypto.NoSuchPaddingException e2) {
            log.error("3DES解密异常:", e2);
            e2.printStackTrace();
        } catch (Exception e1) {
            log.error("3DES解密异常:", e1);
            e1.printStackTrace();
        }
        return null;
    }
    
    /**
     * 转换成十六进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
            if (n < b.length - 1) {
                hs.append(":");
            }
        }
        return hs.toString().toUpperCase();
    }
}
