package com.dxhy.order.service;

import com.dxhy.order.ServiceStarter;
import com.dxhy.order.api.ApiInvoiceSummaryStatisticsService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.dao.InvoiceTaxRateRequestInfoMapper;
import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.vo.QsRequestVo;
import com.dxhy.order.utils.JsonUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceStarter.class)
@WebAppConfiguration
public class OrderInvoiceServiceTest {
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Resource
    private ApiInvoiceSummaryStatisticsService apiInvoiceSummaryStatisticsService;
    @Autowired
    private InvoiceTaxRateRequestInfoMapper invoiceTaxRateRequestInfoMapper;
    @Resource
    private ApiPushService apiPushService;
    @Test
    public void selectInvoiceByOrderTest() {
        Map map = new HashMap<>(5);
        map.put("startTime", "2018-09-01");
        map.put("endTime", "2018-09-12");
        
        map.put("fpdm", "1");
        map.put("fphm", "1");
        List list = new ArrayList<>();
        list.add("2");
//        list.add("51");
        map.put("fpzlList", list);
//		map.put("fphmStart","10000");
//		map.put("fphmStart","2000000");
        map.put("pageSize", 10);
        map.put("currPage", 1);
        map.put("xhfNsrsbh", "911101082018050516");
        List<String> shList = new ArrayList<>();
        shList.add("911101082018050516");
        PageUtils selectInvoiceByOrder = apiOrderInvoiceInfoService.selectInvoiceByOrder(map, shList);
        System.out.println("==========" + JsonUtils.getInstance().toJsonString(selectInvoiceByOrder));
    }
    
    /**
     * 汇总测试
     */
    @Test
    public void selectTaxRateStat() {
        String str = "{\"informType\":\"2\",\"param\":" +
                "[{\"taxNumber\":\"150001194112132161\",\"billingDate\":\"2019Q1\"}" +
                "]}";
        QsRequestVo vo = JsonUtils.getInstance().fromJson(str, QsRequestVo.class);
        apiInvoiceSummaryStatisticsService.getInvoiceSummaryStatistics(vo);
    }


    /**
     * 发票推送客户webservice接口
     */
    @Test
    public void fpts() {
        InvoicePush push = new InvoicePush();
        push.setFPQQLSH("468572767064903680");
        push.setNSRSBH("140301206111099566");
        push.setFP_DM("5000191650");
        push.setFP_HM("87058067");
        String s = JsonUtils.getInstance().toJsonString( push );
        //发票开具
//        R r = apiPushService.pushRouting(s);


        //红字信息表撤销推送
        apiPushService.pushHZXXBtatus("5001012010015624","140301206111099566","123456789012345","0");
    }
}
