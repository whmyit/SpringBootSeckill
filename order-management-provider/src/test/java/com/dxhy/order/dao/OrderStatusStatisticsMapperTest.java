package com.dxhy.order.dao;

import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author fankunfeng
 * @Date 2019-05-23 15:07:06
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OrderStatusStatisticsMapperTest {
    @Autowired
    OrderStatusStatisticsMapper orderStatusStatisticsMapper;
    @Test
    public void selectOrderStatusInfo() {
        Map map = new HashMap(5);
        map.put("fplx", "0");
        map.put("kpzt", "2");
        map.put("pushStatus", "1");
//        map.put("xhfNsrsbh","1");
        map.put("startTime", "2019-01-01");
        map.put("endTime", "2019-09-01");
        PageHelper.startPage(2, 4);
        List<String> shList = new ArrayList<>();
        List<OrderStatusStatistics> list = orderStatusStatisticsMapper.queryOrderStatusInfo(map, shList);
        PageInfo<OrderStatusStatistics> pageInfo = new PageInfo<OrderStatusStatistics>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
//        List<OrderStatusStatistics> orderStatusStatistics = orderStatusStatisticsMapper.queryOrderStatusInfo(map);
        System.out.println(JsonUtils.getInstance().toJsonString(page));
    }


}
