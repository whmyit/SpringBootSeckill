package com.dxhy.order.consumer.handle;

import com.dxhy.order.api.ApiInvoiceSummaryStatisticsService;
import com.dxhy.order.model.vo.QsRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Description
 * @Author xueanna
 * @Date 2019/5/31 18:21
 */
@Slf4j
public class SummaryThread extends Thread {
    private final ExecutorService executor = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("summary-schedule-pool-%d").daemon(true).build());
    
    public void fun(QsRequestVo vo, ApiInvoiceSummaryStatisticsService service) throws Exception {
        executor.submit(() -> {
            try {
                log.info("开始汇总数据");
                //数据汇总
                service.getInvoiceSummaryStatistics(vo);
//                                 Thread.sleep(2*1000);
            } catch (Exception e) {
                log.info("汇总失败", e);
            }
        });
    
    }

}
