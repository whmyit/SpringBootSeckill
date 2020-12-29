//package com.dxhy.order.consumer;
//
//import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
//import com.dxhy.order.exceptions.OrderReceiveException;
//import com.dxhy.order.utils.JsonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//
//@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
//@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
//@WebAppConfiguration
//@Slf4j
//public class InvoiceInvalidTest {
//
//
//    @Autowired
//    private InvalidInvoiceService invalidInvoiceService;
//
//
//    @Test
//    public void test1() throws OrderReceiveException {
//        String[] ids = new String[1];
//        ids[0] = "201906051720101136201034545364993";
//        //ids[1] = "201906051948361136238388718993409";
//        com.dxhy.order.model.R batchValidInvoice = invalidInvoiceService.batchValidInvoice(ids);
//        System.out.println(JsonUtils.getInstance().toJsonString(batchValidInvoice));
//    }
//
//}
