package com.dxhy.order.dao;

import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.utils.JsonUtils;
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
 * @Date 2019-05-28 11:38:40
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest("ServiceStarter.class")
@WebAppConfiguration
public class OrderInvoiceInfoMapperTest {
    @Autowired
    OrderInvoiceInfoMapper orderInvoiceInfoMapper;
//    @Test
//    public void getCountOfMoreMonth(){
//        String sld = "";
//        String sldFlag = "";
//        //String nsrsbh = "911101082018050516";
//        List<String> list = new ArrayList<String>();
//        list.add("911101082018050516");
//        Date starttime = DateUtilsLocal.getFirstDayOfNMonthAgo(5);
//        Date endtime = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
//        String timeFormatFlag = ConfigureConstant.STRING_0;
//        String kplxFlag = ConfigureConstant.STRING_1;
//        String timeFlag = ConfigureConstant.STRING_1;
//        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoMapper.getCountOfMoreMonth(starttime, endtime, list, sld, timeFormatFlag, timeFlag,sldFlag, kplxFlag);
//        System.out.println(JsonUtils.getInstance().toJsonString(countOfMoreMonth));
//    }
//
//    @Test
//    public void getCountOfMoreMonth2(){
////        String sld = "51";
//        String sld = "";
//        String sldFlag = ConfigureConstant.STRING_1;
//        //String nsrsbh = "911101082018050516";
//        List<String> list = new ArrayList<String>();
//        list.add("911101082018050516");
//        Date starttime = DateUtilsLocal.getFirstDayOfNMonthAgo(5);
//        Date endtime = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
//        String timeFormatFlag = ConfigureConstant.STRING_0;
//        String kplxFlag = null;
//        String timeFlag = null;
//        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoMapper.getCountOfMoreMonth(starttime, endtime, list, sld, timeFormatFlag, timeFlag,sldFlag, kplxFlag);
//        System.out.println(JsonUtils.getInstance().toJsonString(countOfMoreMonth));
//    }

//    @Test
//    public void getDataOfMoreMonth(){
//        Date start = DateUtilsLocal.getFirstDayOfNMonthAgo(4);
//        Date end = new Date();
//        //String nsrsbh = "911101082018050516";
//        List<String> list = new ArrayList<String>();
//        list.add("911101082018050516");
//        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoMapper.getDataOfMoreMonth(start, end, list, "0", "1");
//        System.out.println(JsonUtils.getInstance().toJsonString(countOfMoreMonth));
//    }
//
//    @Test
//    public void queryCountByMap() {
//        Map map = new HashMap<>(5);
//        map.put("startTime", "2019-01-01 00:00;00");
//        map.put("endTime", "2019-06-01 00:00;00");
//        map.put("fpdm", "123");
//        map.put("fphm", "123");
//        map.put("sld", "123");
//        map.put("kplx", "1");
//        map.put("ddh","123");
//        map.put("ghfMc","123");
//        map.put("fplx","21");
//        map.put("kpr","123");
//        map.put("mdh","123");
//        map.put("zfbz","1");
//        map.put("xhfNsrsbh","123");
//        map.put("orderIdArrays","1");
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("0");
//        strings.add("2");
//        strings.add("51");
//        map.put("orderInvocieIdArrays",strings);
//        Map map1 = orderInvoiceInfoMapper.queryCountByMap(map);
//        System.out.println(JsonUtils.getInstance().toJsonString(map1));
//    }
    
    
    @Test
    public void selectInvoiceByOrder() {
        Map map = new HashMap<>(5);
        map.put("startTime", "2019-01-01 00:00;00");
        map.put("endTime", "2019-06-01 00:00;00");
        map.put("dyzt", "1");
        map.put("fpdm", "123");
        map.put("fphm", "123");
        map.put("fpzlDm","21");
        map.put("kplx","1");
        map.put("ddh","123");
        map.put("mdh","123");
        map.put("ghfMc","123");
        map.put("kpr","123");
        map.put("sld", "123");
        map.put("zfbz", "1");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("0");
        strings.add("2");
        strings.add("51");
        map.put("fpzlList", strings);
        map.put("fphmStart", "123");
        map.put("fphmEnd", "123");
        map.put("xhfNsrsbh", "123");
        List<String> shList = new ArrayList<>();
        shList.add("234234");
        List<OrderInvoiceInfo> countOfMoreMonth = orderInvoiceInfoMapper.selectInvoiceByOrder(map, shList);
        System.out.println(JsonUtils.getInstance().toJsonString(countOfMoreMonth));
    }

    @Test
    public void selectRedAndInvoiceBymap(){
        Map map = new HashMap<>(5);
        map.put("startTime","2019-01-01 00:00;00");
        map.put("endTime","2019-06-01 00:00;00");
//        map.put("fpdm","123");
//        map.put("fphm","123");
//        map.put("sld","21");
//        map.put("kplx","1");
//        map.put("ddh","123");
//        map.put("gmfmc","123");
//        map.put("fplx","0");
//        map.put("mdh","123");
//        map.put("kpr","1");
//        map.put("orderIdArrays","1");
//        ArrayList<String> strings = new ArrayList<>();
//        strings.add("0");
//        strings.add("2");
//        strings.add("51");
//        map.put("orderInvocieIdArrays",strings);
//        map.put("xhfNsrsbh","123");
//        map.put("zfbz","1");
//        map.put("xhfmc","123");
//        map.put("minhjje","");
//        map.put("maxhjje","");
//        List<OrderInvoiceDetail> countOfMoreMonth = orderInvoiceInfoMapper.selectRedAndInvoiceBymap(map);
//        System.out.println(JsonUtils.getInstance().toJsonString(countOfMoreMonth));
    }
}
