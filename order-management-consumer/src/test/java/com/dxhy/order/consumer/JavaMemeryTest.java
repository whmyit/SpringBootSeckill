package com.dxhy.order.consumer;

import com.dxhy.order.model.OrderInfo;

import java.util.ArrayList;
import java.util.List;

public class JavaMemeryTest {

    public static void main(String[] args) {
        List<OrderInfo> list = new ArrayList<>();
        OrderInfo oi1 = new OrderInfo();
        oi1.setGhfMc("陈玉航1");
        oi1.setGhfNsrsbh("1231");
        OrderInfo oi2 = new OrderInfo();
        oi2.setGhfMc("陈玉航2");
        oi2.setGhfNsrsbh("1232");
        OrderInfo oi3 = new OrderInfo();
        oi3.setGhfMc("陈玉航3");
        oi3.setGhfNsrsbh("1233");

        list.add(oi1);
        list.add(oi2);
        list.add(oi3);

        int i = 1;
        JavaMemeryTest jt = new JavaMemeryTest();
        jt.memeryTest(list, i);
        System.out.println(i);
        System.out.println(list);
    }

    public void memeryTest(List<OrderInfo> list, int i) {
        list.remove(1);
        list.get(0).setGhfMc("123");
    }
}
