package com.dxhy.order.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * HmacSHA1工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:04
 */
@Slf4j
public class HmacSHA1Util {
    
    /**
     * HmacSHA1生成签名值
     *
     * @param data 数据
     * @param key  密钥
     * @return 签名值
     */
    public static byte[] hmacsha1(byte[] data, byte[] key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        } catch (InvalidKeyException e) {
            log.error("InvalidKeyException", e);
        } catch (Exception e) {
            log.error("未知异常", e);
        }
        return null;
    }
    
    /**
     * 生成签名
     *
     * @param url       url请求+？
     * @param signMap   map参数
     * @param secretKey 加密key
     * @return 签名
     * @throws Exception
     */
    public static String genSign(String url, TreeMap<String, String> signMap, String secretKey) throws Exception {
        Boolean flag = Boolean.TRUE;
        StringBuilder keyVal = new StringBuilder();
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            // 值为空的不参与签名
            if (!entry.getValue().isEmpty()) {
                keyVal.append(String.format("%s=%s&", entry.getKey(), entry.getValue()));
            } else {
                flag = Boolean.FALSE;
                break;
            }
        }
        String signStr = StringUtils.EMPTY;
        if (flag) {
            // 2、拼接API密钥
            String content = url + keyVal.toString().substring(0, keyVal.length() - 1);
            log.debug("签名生成的String:{}", content);
            byte[] signBytes = hmacsha1(content.getBytes(StandardCharsets.UTF_8), secretKey.getBytes(StandardCharsets.UTF_8));
            signStr = Base64.encodeBase64URLSafeString(signBytes);
        }
        return signStr;
    }
    
    
    public static int getRandNum(int min, int max) {
        int randNum = min + (int) (Math.random() * ((max - min) + 1));
        return randNum;
    }

    public static void main(String[] args) {
        System.out.println(
                Base64.encodeBase64URLSafeString(
                        hmacsha1("Nonce=107688&SecretId=289efb7512e54146273b982456b03f42ea93&Timestamp=1600774508268&content=eyJERFpYWCI6W3siRERNWFhYIjpbeyJYTU1DIjoi54mp5Lia5pyN5YqhMjEuODgiLCJIU0JaIjoiMSIsIlpYQk0iOiIiLCJMU0xCUyI6IiIsIkRKIjoyMS44ODA4NTQ1OSwiWlpTVFNHTCI6IiIsIktDRSI6IiIsIlNQQk0iOiIxMDEwMTE3MDAwMDAwMDAwMDAwIiwiWEgiOiIxIiwiU1BTTCI6MzkuMzc2ODkyMjAsIkdHWEgiOiLop4TmoLzlnovlj7ciLCJTRSI6IjAuMDAiLCJEVyI6IuS7vSIsIllIWkNCUyI6IjAiLCJTTCI6IjAuMDMiLCJCWVpEMyI6IiIsIkJZWkQyIjoiIiwiSkUiOjg2MS42MCwiQllaRDEiOiIiLCJGUEhYWiI6IjAifSx7IlhNTUMiOiLniankuJrmnI3liqEzMy43OSIsIkhTQloiOiIxIiwiWlhCTSI6IiIsIkxTTEJTIjoiIiwiREoiOjMzLjc4OTk0OTk1LCJaWlNUU0dMIjoiIiwiS0NFIjoiIiwiU1BCTSI6IjEwMTAxMTcwMDAwMDAwMDAwMDAiLCJYSCI6IjIiLCJTUFNMIjo4Ny4zMTkyNDc0MSwiR0dYSCI6IuinhOagvOWei%2BWPtyIsIlNFIjoiMC4wMCIsIkRXIjoi5Lu9IiwiWUhaQ0JTIjoiMCIsIlNMIjoiMC4wMyIsIkJZWkQzIjoiIiwiQllaRDIiOiIiLCJKRSI6Mjk1MC41MSwiQllaRDEiOiIiLCJGUEhYWiI6IjAifV0sIkREVFhYIjp7IlhIRlNCSCI6IjE0MDMwMTIwNjExMTA5OTU2NiIsIlFEWE1NQyI6IiIsIkdNRlpIIjoiNjQ1ODc3Nzc3ODg4OTk5IiwiWVdMWCI6IiIsIkREU0oiOiIyMDIwMDkyMjE5MzUwOCIsIkRESCI6IlY0MDkyMjE5MzUwOF9DRmgiLCJLUFIiOiJ3enh4IiwiRERRUUxTSCI6IjA5MjIxOTM1MDhfQ0ZoIiwiUURCWiI6IjAiLCJGSFIiOiJmaHp6IiwiS1BMWCI6IjAiLCJYSEZNQyI6IjE0MDMwMTIwNjExMTA5OTU2NiIsIlhIRkRaIjoieHjplIDmlrnlnLDlnYAiLCJCWiI6IiIsIkJZWkQ0IjoiIiwiQllaRDMiOiIiLCJDSFlZIjoiIiwiQllaRDIiOiIiLCJCWVpEMSI6IiIsIlhIRllIIjoieHjplIDmlrliYW5rIiwiSEpKRSI6MzgxMi4xMSwiTlNSU0JIIjoiMTQwMzAxMjA2MTExMDk5NTY2IiwiVEhESCI6IiIsIkJZWkQ1IjoiIiwiR01GTFgiOiIwMSIsIlhIRkRIIjoiMDEwLTgxMjM0NzgiLCJHTUZZSCI6Ind6eHhiYW5rIiwiR01GQk0iOiIiLCJHTUZESCI6IjAxMC04NDU2Nzg5MSIsIkpTSEoiOjM4MTIuMTEsIkJNQkJCSCI6IjM1LjAiLCJZRlBETSI6IiIsIkdNRkRaWVgiOiJ3dXpoZW4wNjA1QHNpbmEuY29tIiwiWUZQSE0iOiIiLCJOU1JNQyI6IjE0MDMwMTIwNjExMTA5OTU2NiIsIlRRTSI6IiIsIlRTQ0hCWiI6IiIsIkhKU0UiOjAuMDAsIkdNRlNKSCI6IjEzMTIzNDU2Nzg5IiwiR01GTUMiOiLljJfkuqzmo7HogZrlvbHop4bmlofljJbkvKDlqpLmnInpmZDlhazlj7giLCJHTUZEWiI6Ind66LSt5pa55Zyw5Z2AIiwiU0tSIjoic2t4eCIsIlhIRlpIIjoiNjIxMTQ3ODk2NDc3NyIsIlhYTUJCSCI6IiIsIkdNRlNCSCI6IjkxMTEwMTA4VkhOQzVaMjg2OSIsIkdNRlNGIjoiIn19XSwiRERQQ1hYIjp7IktQRlMiOiIwIiwiRlBMWERNIjoiMDI2IiwiQ1BZQlMiOiIiLCJERFFRUENIIjoid3oyMDA5MjIxOTM1MDgyNjIiLCJLWlpEIjoiIiwiS1BaRCI6IiIsIk5TUlNCSCI6IjE0MDMwMTIwNjExMTA5OTU2NiJ9fQ%3D%3D&encryptCode=0&zipCode=0".getBytes(StandardCharsets.UTF_8)
                                , "27a06832a2214a4fa3b7105e4a72d370".getBytes(StandardCharsets.UTF_8))));
    }
}
