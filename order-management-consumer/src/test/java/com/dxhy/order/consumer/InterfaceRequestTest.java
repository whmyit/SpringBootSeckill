package com.dxhy.order.consumer;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 接口调用测试方法
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/17 11:43
 */
public class InterfaceRequestTest {

    public static void main(String[] args) throws Exception {
//        String restUrl = "http://sims.dxyun.com/order-api/invoice/v1/GetInvoicePdfFiles";
//        String content = "";
//        String Nonce = "";
//        String SecretId = "";
//        String Timestamp = String.valueOf(System.currentTimeMillis());
//        String encryptCode = "0";
//        String zipCode = "0";

//        String restUrl = "http://sims.dxyun.com/order-api/invoice/v1/AllocateInvoices";
//        String content = "eyJDT01NT05fSU5WT0lDRSI6W3siQ09NTU9OX0lOVk9JQ0VfREVUQUlMIjpbeyJYTU1DIjoi54Ot5YqbIiwiSFNCWiI6IjAiLCJaWEJNIjoiIiwiTFNMQlMiOiIzIiwiWE1TTCI6MSwiWlpTVFNHTCI6IiIsIlhNWEgiOjEsIlNQQk0iOiIxMTAwMDAwMDAwMDAwMDAwMDAwIiwiR0dYSCI6bnVsbCwiWE1ESiI6MTIwMDAsIlNFIjowLCJEVyI6IumhuSIsIllIWkNCUyI6IjAiLCJYTUpFIjoxMjAwMCwiU0wiOjAsIkZQSFhaIjoiMCJ9LHsiWE1NQyI6IueDreWKmyIsIkhTQloiOiIwIiwiWlhCTSI6IiIsIkxTTEJTIjoiMyIsIlhNU0wiOjEsIlpaU1RTR0wiOiIiLCJYTVhIIjoyLCJTUEJNIjoiMTEwMDAwMDAwMDAwMDAwMDAwMCIsIkdHWEgiOiI1NTg4NzQiLCJYTURKIjo1OTQwMCwiU0UiOjAsIkRXIjoi55O2IiwiWUhaQ0JTIjoiMCIsIlhNSkUiOjU5NDAwLCJTTCI6MCwiRlBIWFoiOiIwIn1dLCJDT01NT05fSU5WT0lDRV9PUkRFUiI6eyJEREgiOiJSZXEyMDE4MTIxOTAwMDAwNTAiLCJERERBVEUiOiIiLCJUSERIIjpudWxsfSwiQ09NTU9OX0lOVk9JQ0VfSEVBRCI6eyJRRFhNTUMiOiIiLCJHTUZfTlNSU0JIIjoiOTE0NTA3MDA3NTEyNDQ5Mlg3IiwiWFNGX0RIIjoiNTkyODE4ODgiLCJLUFIiOiLmnY7msYDlhbAiLCJHTUZfU0oiOiIiLCJGSFIiOiLmnY7msYDlhbAiLCJRRF9CWiI6IjAiLCJLUExYIjowLCJHTUZfTUMiOiLlub_opb_ph5HmoYLmtYbnurjkuJrmnInpmZDlhazlj7giLCJHTUZfRFoiOiLpkqblt57luILpkqblt57muK_ph5HlhYnlt6XkuJrlm60wNzc3LTM2OTg4NTEiLCJHTUZfU0YiOiIiLCJCWiI6bnVsbCwiQllaRDQiOiIiLCJCWVpEMyI6IiIsIkJZWkQyIjoiIiwiQllaRDEiOiIiLCJISkpFIjoiNzE0MDAuMDAiLCJOU1JTQkgiOiI5MTExMDEwODYwMDAxMTUwNDMiLCJCWVpENSI6IiIsIkdNRl9HRERIIjoiIiwiWFNGX05TUlNCSCI6IjkxMTEwMTA4NjAwMDExNTA0MyIsIkZQUVFMU0giOiIzOGU2YmMxNC0zMTI0LTQwNGItYmM0MC0yNzJkZjQyNjY3ZmIwMDEiLCJHTUZfV1giOiIiLCJKU0hKIjo3MTQwMCwiR01GX0VNQUlMIjoiIiwiQk1CX0JCSCI6IjEiLCJOU1JNQyI6IuWMl-S6rOaxn-ajruiHquaOp-aciemZkOWFrOWPuCIsIkdNRl9RWUxYIjoiMDEiLCJUU0NIQloiOjAsIlhTRl9ZSFpIIjoi5Lit5Zu95bel5ZWG6ZO26KGM5YyX5Lqs5aSn6YO95biC5pSv6KGMIDAyMDAwODAzMTkwMjAxNTgyMjQiLCJISlNFIjowLCJHTUZfWUhaSCI6IumSpuW3nuW4guWGnOadkeS_oeeUqOWQiOS9nOekvuiBlOWQiOekvuiQpeS4mumDqDgwMDcxMjAxMDEyNzc2Nzg4MCIsIlhTRl9NQyI6IuWMl-S6rOaxn-ajruiHquaOp-aciemZkOWFrOWPuCIsIlhTRl9EWiI6IuWMl-S6rOW4gua1t-a3gOWMuuefpeaYpei3rzUx5Y-35oWO5piM5aSn5Y6mNTEx5a6kIDU5MjgxODg4IiwiUFlETSI6IiIsIlNLUiI6IuadjuaxgOWFsCJ9fV0sIkNPTU1PTl9JTlZPSUNFU19CQVRDSCI6eyJLUEpIIjoiMCIsIkZQTEIiOiIyIiwiU0xESUQiOiI5NSIsIkZQTFgiOiIxIiwiRlBRUVBDSCI6IjM4ZTZiYzE0LTMxMjQtNDA0Yi1iYzQwLTI3MmRmNDI2NjdmYiIsIktaWkQiOiIiLCJOU1JTQkgiOiI5MTExMDEwODYwMDAxMTUwNDMifX0";
//        String SecretId = "289efb7512e54146273b982456b03f42ea93";
//        String timestamp = "1545886419834";
//        String encryptCode = "0";
//        String zipCode = "0";
//        int Nonce = 1487208689;

        String restUrl = "http://sims.dxyun.com/order-api/invoice/api/v3/AllocateInvoices";
        String content = "eyJDT01NT05fT1JERVJfQkFUQ0giOnsiRERRUVBDSCI6InRlc3RfMTU1MDQ1OTE2OCIsIk5TUlNCSCI6IjkxMTEwMTA4MDk5MDI5MjQwSCIsIlNMRElEIjoiIiwiS1BKSCI6IiIsIkZQTFgiOiIyIiwiRlBMQiI6IjUxIiwiS1paRCI6IiJ9LCJDT01NT05fT1JERVJTIjp7IkNPTU1PTl9PUkRFUl9IRUFEIjp7IkREUVFMU0giOiJ0ZXN0XzE1NTA0NTkxNjgiLCJOU1JTQkgiOiI5MTExMDEwODA5OTAyOTI0MEgiLCJOU1JNQyI6Ilx1NTMxN1x1NGVhY1x1NWZlYlx1NTNjOFx1NTk3ZFx1NGZlMVx1NjA2Zlx1NjI4MFx1NjcyZlx1NjcwOVx1OTY1MFx1OGQyM1x1NGVmYlx1NTE2Y1x1NTNmOCIsIktQTFgiOiIwIiwiQk1CX0JCSCI6IjMxLjAiLCJYU0ZfTlNSU0JIIjoiOTExMTAxMDgwOTkwMjkyNDBIIiwiWFNGX01DIjoiXHU1MzE3XHU0ZWFjXHU1ZmViXHU1M2M4XHU1OTdkXHU0ZmUxXHU2MDZmXHU2MjgwXHU2NzJmXHU2NzA5XHU5NjUwXHU4ZDIzXHU0ZWZiXHU1MTZjXHU1M2Y4IiwiWFNGX0RaIjoiXHU1MzE3XHU0ZWFjXHU1ZTAyXHU2ZDc3XHU2ZGMwXHU1MzNhXHU0ZTMwXHU4ZDI0XHU0ZTJkXHU4ZGVmN1x1NTNmNzRcdTUzZjdcdTY5N2NcdTU2ZGJcdTVjNDI0MDM2XHU1M2Y3IiwiWFNGX0RIIjoiMDEwLTg0MTc3ODc3IiwiWFNGX1lIIjoiIiwiWFNGX1pIIjoiIiwiR01GX05TUlNCSCI6IiIsIkdNRl9NQyI6IiIsIkdNRl9EWiI6IiIsIkdNRl9RWUxYIjoiMDEiLCJHTUZfU0YiOiIiLCJHTUZfR0RESCI6IiIsIkdNRl9TSiI6IiIsIkdNRl9FTUFJTCI6IiIsIkdNRl9ZSCI6IiIsIkdNRl9aSCI6IiIsIktQUiI6Ilx1NzM4Ylx1ODJkMiIsIlNLUiI6IiIsIkZIUiI6IiIsIllGUF9ETSI6IiIsIllGUF9ITSI6IiIsIlFEX0JaIjoiMCIsIlFEWE1NQyI6IiIsIkpTSEoiOiIxMDEuMDAiLCJISkpFIjoiMCIsIkhKU0UiOiIwIiwiQloiOiIiLCJDSFlZIjoiIiwiVFNDSEJaIjoiIiwiRERIIjoiMjAxOTAxMDMwNTQ2Mzc5MzEyODkiLCJUSERIIjoiIiwiREREQVRFIjoiIiwiQllaRDEiOiIiLCJCWVpEMiI6IiIsIkJZWkQzIjoiIiwiQllaRDQiOiIiLCJCWVpENSI6IiJ9LCJPUkRFUl9JTlZPSUNFX0lURU1TIjpbeyJYTVhIIjoiIiwiRlBIWFoiOiIwIiwiU1BCTSI6IjMwNDA1MDIwMjAwMDAwMDAwMDAiLCJaWEJNIjoiIiwiWUhaQ0JTIjoiMCIsIkxTTEJTIjoiMyIsIlpaU1RTR0wiOiIiLCJYTU1DIjoiXHU0ZWUzXHU3NDA2XHU4ZDM5IiwiR0dYSCI6IiIsIkRXIjoiIiwiWE1TTCI6IjEiLCJYTURKIjoiMTAwIiwiWE1KRSI6IjEwMCIsIkhTQloiOiIwIiwiU0wiOiIwIiwiU0UiOiIxIiwiQllaRDEiOiIiLCJCWVpEMiI6IiIsIkJZWkQzIjoiIn1dfX0%3D";
        String SecretId = "77d8cd9bae494335a8898f097b7cb3e54744";
        String timestamp = "1550458663";
        String encryptCode = "0";
        String zipCode = "0";
        int Nonce = 1;

//        String restUrl = "http://sims.dxyun.com/order-api/invoice/v2/AllocateInvoices";
//        String content = "eyJDT01NT05fSU5WT0lDRSI6eyJGUEtKWFhfRERYWCI6eyJEREgiOiIxMTFHVEtHUDUxOTU0NDMzNTI5NyJ9LCJGUEtKWFhfRlBUWFgiOnsiQk1CX0JCSCI6IjI2LjAiLCJGUExCIjoiNTEiLCJGUFFRTFNIIjoiMTExR1RLR1A1MTk1NDQzMzUyOTciLCJHTUZfTUMiOiLljJfkuqzonILkupHnp5HmioDmnInpmZDlhazlj7giLCJHTUZfTlNSU0JIIjoiOTExMTAxMDgzMTgzMTc4MzNRIiwiR01GX1FZTFgiOiIwMSIsIkdNRl9TSiI6IjE3NjAxMDAzODg2IiwiSEpKRSI6IjAuOTEiLCJISlNFIjoiMC4wOSIsIkpTSEoiOiIxLjAiLCJLUExYIjoiMSIsIktQUiI6IueuoeeQhuWRmCIsIktQWE0iOiIo6K+m6KeB6ZSA6LSn5riF5Y2VKSIsIk5TUk1DIjoi5YyX5Lqs5q+P5pel5LyY6bKc55S15a2Q5ZWG5Yqh5pyJ6ZmQ5YWs5Y+4IiwiTlNSU0JIIjoiMTUwMDAxMjA1MTEwMjc4NTU1IiwiUURYTU1DIjoiKOivpuingemUgOi0p+a4heWNlSkiLCJRRF9CWiI6IjAiLCJYU0ZfREgiOiIwMTAtNjQ3OTYzODkiLCJYU0ZfRFoiOiLljJfkuqzluILmnJ3pmLPljLrlub/pobrljZflpKfooZcxOeWPt+mZojLlj7fmpbwy5bGC5YaZ5a2X5qW8OOWPtyIsIlhTRl9NQyI6IuWMl+S6rOavj+aXpeS8mOmynOeUteWtkOWVhuWKoeaciemZkOWFrOWPuCIsIlhTRl9OU1JTQkgiOiIxNTAwMDEyMDUxMTAyNzg1NTUiLCJYU0ZfWUhaSCI6IuaLm+WVhumTtuihjOWMl+S6rOWMl+iLkei3r+aUr+ihjDExMDkxMzc3MjExMDYwMSJ9LCJGUEtKWFhfWE1YWFMiOlt7IkZQSFhaIjoiMCIsIkhTQloiOiIxIiwiU0wiOiIwLjEiLCJTUEJNIjoiMTAxMDExNTAxMDEwMDAwMDAwMCIsIlhNREoiOiIxLjAiLCJYTUpFIjoiMS4wIiwiWE1NQyI6Iue+juWbveibh+aenDE4MGfotbcqMuS4qiIsIlhNU0wiOiIxIiwiWUhaQ0JTIjoiMCJ9XX0sIkNPTU1PTl9JTlZPSUNFU19CQVRDSCI6eyJGUExYIjoiMCIsIkZQUVFQQ0giOiJCVF82NWEwZDJhYy0zYmU1LTRkNWMtYmYzZi1jOGQ1MTExZDBjZjMiLCJOU1JTQkgiOiIxNTAwMDEyMDUxMTAyNzg1NTUifX0=";
//        String SecretId = "289efb7512e54146273b982456b03f42ea93";
//        String timestamp = "1543910622465";
//        String encryptCode = "0";
//        String zipCode = "0";
//        int Nonce = 9881571;
            //测试开票
//        String result = HttpUtils.post(restUrl, content);

        System.out.println(restUrl.replace("http://", ""));

        System.out.println("请求接口内容" + getPostContent(Nonce,restUrl, content,timestamp));
    
    
        System.out.println("最终请求URL为:" + restUrl + "?" + getPostContent(Nonce, restUrl, content, timestamp));


    }

    private static String getPostContent(int Nonce,String strUrl, String content,String timestamp) throws Exception {

        Date currentDate = new Date();


//        String secretId = "289efb7512e54146273b982456b03f42ea93";
//        String secretkey = "27a06832a2214a4fa3b7105e4a72d370";

        String secretId = "77d8cd9bae494335a8898f097b7cb3e54744";
        String secretkey = "73c5f29c9951415ca0ab6e80ad337ab3";
    
    
        //请求方式
        String reqMethod = "POST";
        String authReqUrl = strUrl.replace("http://", "");


        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("Nonce=").append(Nonce).append("&SecretId=").append(secretId).append("&Timestamp=")
                .append(timestamp).append("&content=").append(content).append("&encryptCode=0&zipCode=0");
        String srcStr = reqMethod + authReqUrl + "?" + stringBuffer.toString();
        System.out.println("请求签名URL:" + srcStr);
    
        SecretKeySpec keySpec = new SecretKeySpec(secretkey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
    
        byte[] signBytes = mac.doFinal(srcStr.getBytes(StandardCharsets.UTF_8));
        String signStr = Base64.encodeBase64URLSafeString(signBytes);
        System.out.println("签名值:" + signStr);

        stringBuffer.append("&Signature=").append(signStr);
        return stringBuffer.toString();

    }
}
