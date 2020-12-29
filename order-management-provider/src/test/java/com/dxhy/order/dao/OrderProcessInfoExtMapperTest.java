package com.dxhy.order.dao;

import com.dxhy.order.model.OrderProcessInfoExt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

/**
 * @Author fankunfeng
 * @Date 2019-05-28 11:00:20
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OrderProcessInfoExtMapperTest {
    @Autowired
    OrderProcessInfoExtMapper orderProcessInfoExtMapper;
    //插入数据
    @Test
    public void insert(){
        OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
        orderProcessInfoExt.setOrderProcessInfoId("123");
        orderProcessInfoExt.setParentOrderInfoId("123");
        orderProcessInfoExt.setParentOrderProcessId("23123");
        orderProcessInfoExt.setUpdateTime(new Date());
        orderProcessInfoExt.setStatus("1");
        orderProcessInfoExt.setCreateTime(new Date());
        orderProcessInfoExt.setId("11111");
        int insert = orderProcessInfoExtMapper.insertOrderProcessExt(orderProcessInfoExt);
        System.out.println(insert);
    }
}
