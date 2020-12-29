package com.dxhy.order.dao;

import com.dxhy.order.model.InvalidInvoiceInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author fankunfeng
 * @Date 2019-05-23 20:09:26
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class InvalidInvoiceInfoMapperTest {
    @Resource
    InvalidInvoiceInfoMapper invalidInvoiceInfoMapper;
    @Test
    public void insert(){
        InvalidInvoiceInfo info = new InvalidInvoiceInfo();
        info.setId("123");
        info.setZfyy("测试");
        info.setZfsj(new Date());
        info.setZfpch("zzzcs123");
        info.setZfBz("1");
        info.setXhfNsrsbh("sdasd12313");
        info.setUpdateTime(new Date());
        info.setSld("95");
        info.setFplx("1");
        info.setFphm("123123");
        info.setFpdm("12321");
        info.setCreateTime(new Date());
        int insert = invalidInvoiceInfoMapper.insertValidInvoice(info);
        System.out.println(insert);
    }
}
