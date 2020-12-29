package com.dxhy.order.consumer;

import com.dxhy.order.api.ApiInvoiceSummaryStatisticsService;
import com.dxhy.order.consumer.modules.invoice.controller.InvoiceSummaryStatisticsController;
import com.dxhy.order.model.R;
import com.dxhy.order.model.vo.QsRequestVo;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
* @Description: 销项接口对接—发票汇总统计
* @Author:xueanna
* @Date:2019/5/29
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public class InvoiceStatTest {

    @Autowired
    private InvoiceSummaryStatisticsController invoiceSummaryStatisticsController;

    @Reference
    private ApiInvoiceSummaryStatisticsService apiInvoiceSummaryStatisticsService;


    @Before
    public void setup() {

    }

    @Test
    public void name() {
    }

    @Test
    public void invoiceStatTest() throws Exception {
        log.info("----------汇总统计接口开始测试-----------------------");
        String str = "{\"informType\":\"1\",\"param\":" +
                "[{\"taxNumber\":\"150001194112132161\",\"billingDate\":\"201904\"}" +
                "]}";
        QsRequestVo vo = JsonUtils.getInstance().fromJson(str, QsRequestVo.class);
        //汇总通知
        // R r = invoiceSummaryStatisticsController.invoiceSummaryNotice(str);
        //获取汇总状态
        //  R r = invoiceSummaryStatisticsController.queryInvoiceSummaryStatus(str);
        //获取汇总数据
        R r = invoiceSummaryStatisticsController.queryInvoiceSummaryData(str);
        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(r));

    }

}
