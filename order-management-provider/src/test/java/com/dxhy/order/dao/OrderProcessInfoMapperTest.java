package com.dxhy.order.dao;

import com.dxhy.order.service.UnifyService;
import com.dxhy.order.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author fankunfeng
 * @Date 2019-05-28 10:59:17
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OrderProcessInfoMapperTest {
    @Autowired
    OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Autowired
    UnifyService unifyService;
    //---------------------基本CRUD省略--------------------------

    //主要是时间的切换和concat
    @Test
    public void selectOrderInfo() {
        Map map = new HashMap<>(5);
        map.put("startTime", "2019-01-01 00:00;00");
        map.put("endTime", "2019-06-01 00:00;00");
        map.put("ddh", "123");
        map.put("ghf_mc", "123");
        List<String> shList = new ArrayList<>();
        shList.add("123412");
        List<Map> maps = orderProcessInfoMapper.queryOrderInfo(map, shList);
        System.out.println(JsonUtils.getInstance().toJsonString(maps));
    
    }
    
    @Test
    public void testYwlx() throws ParseException {
        Map<String, Object> map = new HashMap<>(5);
        String date = "20190101";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        Date parse = sdf.parse(date);
        map.put("endDate", new Date());
        map.put("startDate", parse);
        List<String> shList = new ArrayList<>();
        shList.add("123123");
        List<Map<String, Object>> list = orderProcessInfoMapper.selectYwlxCount(map, shList);
        System.out.print(JsonUtils.getInstance().toJsonString(list));
    
    }

}
