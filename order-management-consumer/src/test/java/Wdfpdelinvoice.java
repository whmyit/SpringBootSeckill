import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 每日优鲜-线上数据推送
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/24 22:55
 */
public class Wdfpdelinvoice {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        
        String string = FileUtils.readFileToString(new File("d://wdfp.txt"), StandardCharsets.UTF_8);
        System.out.println(string);
        JSONObject jsonObject = JSON.parseObject(string);
        JSONArray readInvoiceFrontList = jsonObject.getJSONObject("data").getJSONArray("readInvoiceFrontList");
        for (int i = 0; i < readInvoiceFrontList.size(); i++) {
            String invioceId = readInvoiceFrontList.getJSONObject(i).getString("invoiceinfo_id");
            String json = "";
            String url = "https://www.5dfp.com/myinvoice-invoice/secured/invoice/deleteInvoice?invoiceId=" + invioceId + "&token=f39fea7f-ce23-453b-a2f2-7045df2ff0f7&type=0";
            String post = HttpUtils.doGet(url, "");
//            Thread.sleep(3);
            System.out.println(post);
            System.out.println("处理了" + i);
            Map<String, String> map = JsonUtils.getInstance().parseObject(post, Map.class);
//            if(map.get("code").equals("9999")){
//                url = "https://www.5dfp.com/myinvoice-invoice/secured/invoice/deleteInvoice?invoiceId="+invioceId+"&token=80744367-6927-403b-8b4a-fd19bc8d1b66&type=1";
//                post = HttpUtils.post(url);
//                System.out.println(post);
//                System.out.println("处理了" + i);
//            }
        }
        
        
    }
}
