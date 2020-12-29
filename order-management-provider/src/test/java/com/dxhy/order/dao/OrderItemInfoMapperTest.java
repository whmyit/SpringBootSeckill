//package com.dxhy.order.dao;
//
//import com.dxhy.order.model.OrderItemInfo;
//import com.dxhy.order.utils.JsonUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.List;
//
///**
// * @Author fankunfeng
// * @Date 2019-05-28 11:33:17
// * @Describe
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//@WebAppConfiguration
//public class OrderItemInfoMapperTest {
//    @Autowired
//    OrderItemInfoMapper orderItemInfoMapper;
//
//    //to_number（）函数测试
//    @Test
//    public void selectByOrderId(){
//        List<OrderItemInfo> list = orderItemInfoMapper.selectOrderItemInfoByOrderId("12");
//        System.out.println(JsonUtils.getInstance().toJsonString(list));
//
//    }
//}
