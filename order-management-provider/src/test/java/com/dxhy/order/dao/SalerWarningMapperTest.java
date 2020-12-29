package com.dxhy.order.dao;

import com.dxhy.order.api.ApiYpWarningService;
import com.dxhy.order.model.SalerWarning;
import com.dxhy.order.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-05-23 13:36:15
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest("ServiceStarter.class")
@WebAppConfiguration
public class SalerWarningMapperTest {
    @Autowired
    SalerWarningMapper salerWarningMapper;
    @Resource
    ApiYpWarningService apiYpWarningService;
    @Test
    public void selectByTaxCode(){
        //税号
        List<SalerWarning> salerWarning = salerWarningMapper.selectSalerWaringByNsrsbh("911101082018050516", null);
        System.out.println(JsonUtils.getInstance().toJsonString(salerWarning));
    }

    @Test
    public void insertSelective(){
        //税号
        SalerWarning insert = new SalerWarning();
        insert.setId("1112");

        int i = salerWarningMapper.insertSelective(insert);
        System.out.println(i);
    }

    @Test
    public void updateByTaxCode(){
        //税号
        SalerWarning insert = new SalerWarning();
        insert.setId("1111");
        insert.setXhfNsrsbh("150001196104213444");
        insert.setUpdateTime(new Date());
        int i = salerWarningMapper.updateByTaxCode(insert);
        System.out.println(i);
    }
    
}
