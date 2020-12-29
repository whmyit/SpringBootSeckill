//package com.dxhy.order.service.impl;
//
//import com.dxhy.order.ServiceStarter;
//import com.dxhy.order.dao.OrderInfoMapper;
//import com.dxhy.order.model.OrderInfo;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ServiceStarter.class)
//@WebAppConfiguration
//public class TableDaoTest {
//    @Autowired
//    OrderInfoMapper orderInfoMapper;
//
//    @Test
//    public void test() {
//        OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId("201810111020001050209370937360385");
//        System.out.println(selectByPrimaryKey);
//        selectByPrimaryKey.setId(selectByPrimaryKey.getId() + "-");
//        System.out.println(selectByPrimaryKey);
//    }
//}
