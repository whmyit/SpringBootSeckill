import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/5/25 10:43
 */
public class mryt_online_rushred {
    
    public static void main(String[] args) throws Exception {
        List<String> gbk = FileUtils.readLines(new File("d://mryt_cf.txt"), StandardCharsets.UTF_8);
        List<String> errorfphm = new ArrayList<>();
        List<String> successfphm = new ArrayList<>();
        for (int i = 0; i < gbk.size(); i++) {
            String fphm = gbk.get(i);
            
            
            String url = "http://sims.ele-cloud.com/order-api/invoice/rushRed";
            
            String result = "fpdm=011001900211&fphm=" + fphm + "&chyy=%E5%BC%80%E9%87%8D&uId=14&deptId=15";
            String post = HttpUtils.doPost(url, result);
            System.out.println(post);
            System.out.println("处理了" + i);
            Map map = JsonUtils.getInstance().parseObject(post, Map.class);
            String code = map.get(OrderManagementConstant.CODE).toString();
            String msg = map.get("msg").toString();
            if ("0000".equals(code)) {
                System.out.println("==>处理成功的号码：" + fphm);
                successfphm.add(fphm);
            } else {
                System.out.println("处理失败的序号为：" + i);
                System.out.println("处理失败的号码：" + fphm);
                errorfphm.add(fphm);
            }
            Thread.sleep(1000);
            
        }
        
        System.err.println(JsonUtils.getInstance().toJsonString(successfphm));
        System.err.println(JsonUtils.getInstance().toJsonString(errorfphm));
    }
}
