package com.dxhy.order.consumer.userinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/2/7 12:10
 */
public class testUserInfo {
    
    public static void main(String[] args) {
    
        /**
         * 测试用户信息,根据税号和名称获取用户信息
         */
        testDept("15000120561127953X", "销项测试有限公司");
    
        String xmje = DecimalCalculateUtil.mul("5.84434279", "38.11026788");
    
        if (Math.abs(new BigDecimal(xmje).setScale(ConfigureConstant.INT_2, BigDecimal.ROUND_HALF_UP).doubleValue() - new BigDecimal("222.74").setScale(ConfigureConstant.INT_2, BigDecimal.ROUND_UP).doubleValue()) > 0.01) {
            System.out.println(1111);
        }
    }
    
    
    public static void testDept(String taxpayerCode, String taxpayerName) {
        Map<String, String> paraMap = new HashMap<>(2);
        paraMap.put(Constant.TAXPAYERCODE, taxpayerCode);
        paraMap.put(Constant.TAXPAYERNAME, taxpayerName);
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        try {
            String result = HttpUtils.doPostWithHeader("http://test.5ifapiao.com:8888/fatsapi/uadmin/dept/queryDeptByNameAndCode", JsonUtils.getInstance().toJsonString(paraMap), headMap);
            if (StringUtils.isNotBlank(result)) {
                JSONObject jsonObject = JSON.parseObject(result);
                if (!jsonObject.isEmpty() && ConfigureConstant.STRING_0000.equals(jsonObject.get(OrderManagementConstant.CODE))) {
                    System.out.println(JsonUtils.getInstance().toJsonString(JsonUtils.getInstance().parseObject(jsonObject.getString(OrderManagementConstant.DATA), DeptEntity.class)));
                }
                
                System.out.println(result);
                
            }
        } catch (Exception e) {
            System.out.println(JsonUtils.getInstance().toJsonString(e));
        }
    }
}
