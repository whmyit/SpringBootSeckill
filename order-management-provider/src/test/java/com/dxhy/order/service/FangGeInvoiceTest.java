package com.dxhy.order.service;

import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.config.MqttPushClient;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.protocol.fangge.FG_REGIS_TAXDISK_REQ;
import com.dxhy.order.utils.HmacSHA1Util;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.nio.charset.StandardCharsets;

/**
 * @Description: 方格接口对接-测试
 * @Author:xueanna
 * @Date:2019/5/29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public class FangGeInvoiceTest {
    @Autowired
    private RedisService apiRedisService;
    @Autowired
    private MqttPushClient mqttPushClient;
    @Reference
    private ICommonDisposeService commonDisposeService;
    @Autowired
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Before
    public void setup() {
    
    }
    
    @Test
    public void name() {
    }
    
    @Test
    public void test1() {
        apiFangGeInterfaceService.pushMqttMsg("422010201709012004", "661616316992");
        
    }
    
    @Test
    public void test() {
        String pushTopic = String.format(Constant.FG_MQTT_TOPIC_PUB_FANGGE, "15000120561127953X", "661616315914");
        //发布消息
        //mqttPushClient.publish("topictest","{\"hello:world;\"}");
        
        mqttPushClient.subscribe("topictest");
    }
    
    /**
     * redis里面获取信息
     */
    @Test
    public void pushMsg() throws Exception {
        
        //发布消息
        String taxDiskInfo = apiRedisService.get("fa_tax_disk_info");
        if (StringUtils.isNotEmpty(taxDiskInfo)) {
            FG_REGIS_TAXDISK_REQ req = JsonUtils.getInstance().parseObject(taxDiskInfo, FG_REGIS_TAXDISK_REQ.class);
            //redis获取纳税人识别号和机器编号
            String pushTopic = String.format(Constant.FG_MQTT_TOPIC_PUB_FANGGE, req.getNSRSBH(), req.getJQBH());
            
            PushPayload pushPayload = new PushPayload();
            pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);//发票开具
            pushPayload.setNSRSBH(req.getNSRSBH());
            pushPayload.setJQBH(req.getJQBH());
            String s = JsonUtils.getInstance().toJsonString(pushPayload);
            //发布消息
            mqttPushClient.publish(pushTopic, s);
        }
    }
    
    
    public static void main(String[] args) {
       /* String str = "{\"INTERFACETYPE\":\"1\",\"JQBH\":\"499000101350\",\"NSRSBH\":\"11010120181016031\"}";
        PushPayload pushPayload = JsonUtils.getInstance().parseObject(str, PushPayload.class);
        System.out.println(pushPayload);*/
        try {
            String secretKey = "27a06832a2214a4fa3b7105e4a72d370";
            String content =
                    "POST10.1.28.32:8081/order-api/invoice/fangge/v1/registTaxDisk" +
                            "?Nonce=00707" +
                            "&SecretId=289efb7512e54146273b982456b03f42ea93" +
                            "&Timestamp=20190709114058" +
                            "&content=eyJOU1JTQkgiOiI1MDAwMTAwMDY2NjY1NDMyMSIsIk5TUk1DIjoisbG-qbDX1MbT0M_euavLviIsIlpDTFgiOiIxIiwiSlFCTSI6IjQ5OTAwMDEzODQ5NCJ9" +
                            "&encryptCode=0" +
                            "&zipCode=0";
            byte[] bytes = HmacSHA1Util.hmacsha1(content.getBytes(StandardCharsets.UTF_8), secretKey.getBytes(StandardCharsets.UTF_8));
            
            String signStr = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(bytes);
            System.out.println(signStr);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    
}
