package com.dxhy.order.service;

import com.dxhy.order.dao.OrderProcessInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * @Author fankunfeng
 * @Date 2019-04-30 14:52:27
 * @Describe
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class OrderProcessInfoTest {
    @Autowired
    OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Resource
    IRabbitMqSendMessage iRabbitMqSendMessage;
//    @Test
//    public void updateStatus(){
//        String[] str = new String[]{
//                "201904291012431122685110857105411"
//        };
//        List<String> strings = Arrays.asList(str);
//        int i = orderProcessInfoMapper.updateOrderStatusByIdList(strings, "1");
//        System.out.println(i);
//    }

//    @Test
//    public void selectByids(){
//        String[] str = new String[]{
//                "201904291012431122685110857105411"
//        };
//        List<String> strings = Arrays.asList(str);
//        List<OrderProcessInfo> i = orderProcessInfoMapper.selectByPrimaryKeys(strings);
//        System.out.println(JsonUtils.getInstance().toJsonString(i));
//    }
    
    @Test
    public void testmq() {
        iRabbitMqSendMessage.autoSendRabbitMqMessageDelay("1403056P0BUK6872", "order_delay_message", "teadoifjadiofjapdi", "order_fpkj_message1403056P0BUK687", 30000);
    }
}
