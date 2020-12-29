import com.dxhy.order.model.a9.kp.AllocateInvoicesReq;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * 每日优鲜-线上数据推送
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/24 22:55
 */
public class mryx_online_push {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
    
        List<String> gbk = FileUtils.readLines(new File("C://Users/thinkpad/Desktop/31.txt"), StandardCharsets.UTF_8);
        for (int i = 0; i < gbk.size(); i++) {
            AllocateInvoicesReq parseObject = JsonUtils.getInstance().parseObject(gbk.get(i), AllocateInvoicesReq.class);
    
            String baseUrl = Base64.encodeBase64URLSafeString(gbk.get(i).getBytes(StandardCharsets.UTF_8));
            
            String content = "Nonce=3192706&SecretId=55a7b76015704357b5369aa3caab9fe97900&Timestamp=1544520105543&content=" + baseUrl + "&encryptCode=0&zipCode=0";
            String srcStr = "POSTsims.ele-cloud.com/order-api/invoice/v2/AllocateInvoices?" + content;
            String secretKey = "86f290a794d441209033ca0e777829e8";
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            byte[] signBytes = mac.doFinal(srcStr.getBytes(StandardCharsets.UTF_8));
            String signStr = Base64.encodeBase64URLSafeString(signBytes);
            
            String result = content + "&Signature=" + signStr;
            
            String url = "http://sims.ele-cloud.com/order-api/invoice/v2/AllocateInvoices";
            String post = HttpUtils.doPost(url, result);
            System.out.println(post);
            System.out.println("处理了" + i);
            Map map = JsonUtils.getInstance().parseObject(post, Map.class);
            Map map1 = JsonUtils.getInstance().parseObject(map.get("responseData").toString(), Map.class);
            String content1 = map1.get("content").toString();
            String result1 = new String(Base64.decodeBase64(content1.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            Map m2 = JsonUtils.getInstance().parseObject(result1, Map.class);
            if("010000".equals(m2.get("STATUS_CODE"))){
                System.out.println("==>处理成功的批次号："+parseObject.getCOMMON_INVOICES_BATCH().getFPQQPCH());
            }else{
                System.out.println("处理失败的序号为："+i);
                System.out.println("处理失败的批次号："+parseObject.getCOMMON_INVOICES_BATCH().getFPQQPCH());
            }
            if ("702001".equals(m2.get("STATUS_CODE"))) {
                
                System.err.println(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getFPQQLSH());
                Thread.sleep(10000);
                for (int j = 0; j < 2; j++) {
                    post = HttpUtils.doPost(url, result);
                    map = JsonUtils.getInstance().parseObject(post, Map.class);
                    map1 = JsonUtils.getInstance().parseObject(map.get("responseData").toString(), Map.class);
                    content1 = map1.get("content").toString();
                    result1 = new String(Base64.decodeBase64(content1.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                    if ("000000".equals(JsonUtils.getInstance().parseObject(result1, Map.class).get("STATUS_CODE"))) {
                        break;
                    }
                }
            } else {
                System.out.println("zanting");
                
            }


//            System.out.println(JsonUtils.getInstance().toJsonString(parseObject));

//        ALLOCATE_INVOICES_REQ parseObject = JsonUtils.getInstance().parseObject(gbk.get(i), ALLOCATE_INVOICES_REQ.class);
//        System.out.println(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getFPQQLSH());
        
        
        }
    }
}
