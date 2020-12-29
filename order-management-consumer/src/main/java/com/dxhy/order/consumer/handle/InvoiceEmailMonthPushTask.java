package com.dxhy.order.consumer.handle;

import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 任务Handler示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@Slf4j
@JobHandler(value = "InvoiceEmailMonthPushTask")
@Component
public class InvoiceEmailMonthPushTask extends IJobHandler {

    private static final String LOGGER_MSG = "(定时推送开票成功的发票数据)";

    @Reference
    ApiPushService apiPushService;


    @Override
    public ReturnT<String> execute(String param){
        try {
            log.info("========>{}定时任务开始！！！！", LOGGER_MSG);
            Map paramMap = JsonUtils.getInstance().parseObject(param, Map.class);
            if (StringUtils.isBlank(paramMap.get("nsrsbh").toString())) {
                log.error("{},请求税号为空!", LOGGER_MSG);
                return FAIL;
            }
    
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(paramMap.get("nsrsbh").toString());
            apiPushService.pushInvoiceEmailMonthTask(shList);
            log.info("========>{}定时任务结束！！！！", LOGGER_MSG);
        }catch (Exception e){
            log.error("{}抛出异常：{}",LOGGER_MSG,e);
        }
        return SUCCESS;
    }
}
