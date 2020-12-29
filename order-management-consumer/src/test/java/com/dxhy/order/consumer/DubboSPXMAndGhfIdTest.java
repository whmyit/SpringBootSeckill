package com.dxhy.order.consumer;

import com.dxhy.order.api.ApiBuyerService;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @Author fankunfeng
 * @Date 2019-05-28 17:46:46
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
@WebAppConfiguration
public class DubboSPXMAndGhfIdTest {
    @Reference
    private ApiBuyerService apiBuyerService;
    
    @Test
    public void queryBuyerInfoByxhfNsrsbhAndBuyerCode() {
        //{"address":"","createTime":"2018-11-19 00:00:00","id":"105162954764197888","modifyTime":"2018-11-19 00:00:00","phone":"","remarks":""}
        String nsrsbh = "150001205110278555";
        String ghfid = "123456789";
        BuyerEntity buyerEntity = apiBuyerService.queryBuyerInfoByxhfNsrsbhAndBuyerCode(nsrsbh, ghfid);
        System.out.println(JsonUtils.getInstance().toJsonString(buyerEntity));
    }
    
}
