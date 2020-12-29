package com.dxhy.order.service;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.ServiceStarter;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.model.InvoiceCount;
import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.service.impl.InvoiceDataServiceImpl;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.DistributedKeyMaker;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author fankunfeng
 * @Date 2019-04-01 15:10:28
 * @Describe
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceStarter.class)
@WebAppConfiguration
public class OrderInvoiceInfoTest {
    @Autowired
    OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    @Autowired
    ApiOrderInvoiceInfoService orderInvoiceInfoService;

    @Autowired
    InvoiceDataServiceImpl invoice;

    @Resource
    private IPollinvoiceService iPollinvoiceService;

    @Test
    public void dddd(){
        InvoicePush push = new InvoicePush();
        push.setFPLX("1");
        push.setSTATUSCODE("2000");
        push.setSTATUSMSG("");
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setFpzlDm("51");
        orderInvoiceInfo.setId("56cc7779a5794f57a43ebdcdc6793830");
        orderInvoiceInfo.setOrderInfoId("201905162236121129032806295535617");
        invoice.convertOrderInvoiceInfo(push,orderInvoiceInfo);

    }

    @Test
    public void getCountOfHJ(){
        //String nsrsbh = "911101082018050516";
        List<String> list = new ArrayList<String>();
        list.add("911101082018050516");
        Date starttime = DateUtilsLocal.getFirstDayOfNMonthAgo(5);
        Date endtime = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
        String timeFormatFlag = ConfigureConstant.STRING_0;
        String kplxFlag = ConfigureConstant.STRING_1;
        String timeFlag = ConfigureConstant.STRING_1;
        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoService.getCountOfMoreMonth(starttime, endtime, list, null, timeFormatFlag, timeFlag, null, kplxFlag);
        System.out.println(countOfMoreMonth);
    }

    @Test
    public void getCountOfMoreMonth(){
        Date start = DateUtilsLocal.getFirstDayOfNMonthAgo(5);
        Date end = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
        //String nsrsbh = "911101082018050516";
        List<String> list = new ArrayList<String>();
        list.add("911101082018050516");
        List<InvoiceCount> moneyOfThisMonth = orderInvoiceInfoService.getCountOfMoreMonth(start, end,list,"","0","1","","1");
        System.out.println(JsonUtils.getInstance().toJsonString(moneyOfThisMonth));
    }

    /**
     * 测试按照发票种类代码
     */
    @Test
    public void getMoneyOfMoreMonth() {
        Date start = DateUtilsLocal.getFirstDayOfNMonthAgo(20);
        Date end = new Date();
        //String nsrsbh = "911101082018050516";
        List<String> list = new ArrayList<String>();
        list.add("911101082018050516");
        list.add("150001194112132161");
        List<InvoiceCount> moneyOfMoreMonth = orderInvoiceInfoService.getMoneyOfMoreMonth(start, end, list, "0", "1");
        System.out.println(JsonUtils.getInstance().toJsonString(moneyOfMoreMonth));
    }
//
//    @Test
//    public void queryOrderInvoiceByFpdmhm(){
//        String fpdm = "150003528888";
//        String fphm = "67940995";
//        OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(fpdm, fphm);
//        System.out.println(JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
//        System.out.println(orderInvoiceInfo);
//        System.out.println(orderInvoiceInfo.getSykchje());
//    }
    
    
    @Test
    public void queryCountByMap() {
        String json = "{\"currPage\":1,\"fpdm\":\"\",\"kplx\":\"\",\"fpzh\":\"\",\"gmfmc\":\"\",\"pageSize\":10,\"ddh\":\"\",\"orderStatus\":\"0\",\"sld\":\"\",\"xhfNsrsbh\":[\"123456789654123\",\"150301199811285326\"],\"mdh\":\"\",\"fpqh\":\"\",\"startTime\":\"2019-07-01\",\"endTime\":\"2019-07-20\",\"zfbz\":\"\"}";
        Map parseObject = JSON.parseObject(json, Map.class);
        List<String> shList = new ArrayList<>();
        shList.add("124312312");
        Map<String, Object> queryCountByMap = orderInvoiceInfoMapper.queryCountByMap(parseObject, shList);
        JsonUtils.getInstance().toJsonString(queryCountByMap);
    }
    
    @Test
    public void fpkj(){
        String str = "{\n" +
                "    \"COMMON_INVOICE\":[\n" +
                "        {\n" +
                "            \"COMMON_INVOICE_DETAIL\":[\n" +
                "                {\n" +
                "                    \"BYZD1\":\"\",\n" +
                "                    \"BYZD2\":\"\",\n" +
                "                    \"BYZD3\":\"\",\n" +
                "                    \"DW\":\"L\",\n" +
                "                    \"FPHXZ\":\"0\",\n" +
                "                    \"GGXH\":\"\",\n" +
                "                    \"HSBZ\":\"0\",\n" +
                "                    \"LSLBS\":\"\",\n" +
                "                    \"SE\":\"82.57\",\n" +
                "                    \"SL\":\"0.09\",\n" +
                "                    \"SPBM\":\"1010101010000000000\",\n" +
                "                    \"XMBM\":\"\",\n" +
                "                    \"XMDJ\":\"91.74311927\",\n" +
                "                    \"XMJE\":\"917.43\",\n" +
                "                    \"XMMC\":\"*谷物*商品TEST-ZLC\",\n" +
                "                    \"XMSL\":\"10.00\",\n" +
                "                    \"XMXH\":1,\n" +
                "                    \"YHZCBS\":\"0\",\n" +
                "                    \"ZXBM\":\"\",\n" +
                "                    \"ZZSTSGL\":\"\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"COMMON_INVOICE_HEAD\":{\n" +
                "                \"BMB_BBH\":\"33.0\",\n" +
                "                \"BYZD1\":\"\",\n" +
                "                \"BYZD2\":\"\",\n" +
                "                \"BYZD3\":\"\",\n" +
                "                \"BYZD4\":\"\",\n" +
                "                \"BYZD5\":\"\",\n" +
                "                \"BZ\":\"\",\n" +
                "                \"CHYY\":\"\",\n" +
                "                \"FHR\":\"\",\n" +
                "                \"FPQQLSH\":\"123\",\n" +
                "                \"GMF_DZ\":\"北京\",\n" +
                "                \"GMF_EMAIL\":\"\",\n" +
                "                \"GMF_GDDH\":\"01098726354\",\n" +
                "                \"GMF_MC\":\"购买方名称\",\n" +
                "                \"GMF_NSRSBH\":\"11010120181016031\",\n" +
                "                \"GMF_QYLX\":\"01\",\n" +
                "                \"GMF_SF\":\"\",\n" +
                "                \"GMF_SJ\":\"\",\n" +
                "                \"GMF_WX\":\"\",\n" +
                "                \"GMF_YHZH\":\"天地银行89383838\",\n" +
                "                \"HJJE\":\"917.43\",\n" +
                "                \"HJSE\":\"82.57\",\n" +
                "                \"JSHJ\":\"1000.00\",\n" +
                "                \"KPLX\":\"0\",\n" +
                "                \"KPR\":\"ZLC\",\n" +
                "                \"NSRMC\":\"大象慧云信息技术有限公司重庆分公司\",\n" +
                "                \"NSRSBH\":\"91500108MA004CPN95\",\n" +
                "                \"PYDM\":\"\",\n" +
                "                \"QDXMMC\":\"\",\n" +
                "                \"QD_BZ\":\"0\",\n" +
                "                \"SKR\":\"\",\n" +
                "                \"TSCHBZ\":\"0\",\n" +
                "                \"XSF_DH\":\"33355\",\n" +
                "                \"XSF_DZ\":\"华海路\",\n" +
                "                \"XSF_MC\":\"大象慧云信息技术有限公司重庆分公司\",\n" +
                "                \"XSF_NSRSBH\":\"91500108MA004CPN95\",\n" +
                "                \"XSF_YHZH\":\"郑州银行673652\",\n" +
                "                \"YFP_DM\":\"\",\n" +
                "                \"YFP_HM\":\"\"\n" +
                "            },\n" +
                "            \"COMMON_INVOICE_ORDER\":{\n" +
                "                \"DDH\":\"62751248603927872039\",\n" +
                "                \"THDH\":\"\",\n" +
                "                \"DDDATE\":\"\"\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"COMMON_INVOICES_BATCH\":{\n" +
                "        \"CPYFP\":false,\n" +
                "        \"FPLB\":\"51\",\n" +
                "        \"FPLX\":\"2\",\n" +
                "        \"FPQQPCH\":\"123\",\n" +
                "        \"KPJH\":\"\",\n" +
                "        \"KZZD\":\"\",\n" +
                "        \"NSRSBH\":\"91500108MA004CPN95\",\n" +
                "        \"SLDID\":\"-1\"\n" +
                "    },\n" +
                "    \"tERMINALCODE\":\"009\"\n" +
                "}";
        //iPollinvoiceService.pollInvoice(str);
        String s = DistributedKeyMaker.generateShotKey();
        System.out.println(s);
    }

//    @Autowired
//    ApiEmailService apiEmailService;
//    @Test
//    public void emailCheck(){
//        List<String> list = new ArrayList();
//        list.add("411081290688974848");
//        apiEmailService.sendPdfEmail(list,"1439655204@qq.com");
//    }
}
