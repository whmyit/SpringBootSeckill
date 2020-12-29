package com.dxhy.order.consumer;

import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/4/13 11:43
 */
public class userInfoTest {
    public static void main(String[] args) {
        
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsaWNlbnNlIjoibWFkZSBieSBoZWF2ZW4iLCJ1c2VyX25hbWUiOiIxODgxMTIyMzM0NENQIiwic2NvcGUiOlsic2VydmVyIl0sInVzZXJUeXBlIjoiMSIsImV4cCI6MTU4Njc5MDU5OSwidXNlcklkIjoxOTksImF1dGhvcml0aWVzIjpbIkEwMDUiLCJBMDA0Iiwic2Rmc2RmIiwiUk9MRV9VU0VSIl0sImp0aSI6ImRhYmFjZjQyLTc1YmItNDhlOS04OTIyLWRlNmY2Y2EwODk0OSIsImNsaWVudF9pZCI6IjAifQ.o254QNl9MJLW9N4wg0iNwhKkh1NxMlWZ7koxe5J-2AU";
        
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        headMap.put("Authorization", "Bearer" + token);
        try {
            String result = HttpUtils.doGetWithHeader("http://wxkf.5ifapiao.com:8888/fatsapi/admin/user/queryUserInfo", null, headMap);
            if (StringUtils.isNotBlank(result)) {
                R r = JsonUtils.getInstance().parseObject(result, R.class);
                System.out.println(JsonUtils.getInstance().toJsonString(r));
                
                UserEntity userEntity = JsonUtils.getInstance().parseObject(r.get(OrderManagementConstant.DATA).toString(), UserEntity.class);
                
                System.out.println(JsonUtils.getInstance().toJsonString(userEntity));
                
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
