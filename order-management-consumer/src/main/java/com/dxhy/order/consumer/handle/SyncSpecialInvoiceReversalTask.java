package com.dxhy.order.consumer.handle;

import com.alibaba.fastjson.JSONArray;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiSpecialInvoiceReversalService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.modules.invoice.service.SpecialInvoiceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时同步红字申请单定时任务
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Component
@JobHandler(value = "/syncSpecialInvoiceReversal")
public class SyncSpecialInvoiceReversalTask extends IJobHandler {
    
    private static final String LOGGER_MSG = "(定时同步红字申请单数据)";
    
    @Resource
    private SpecialInvoiceService specialInvoiceService;
    
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Override
    public ReturnT<String> execute(String s) {
        long startTime = System.currentTimeMillis();
        
        String batchNo = apiInvoiceCommonService.getGenerateShotKey();
        
        int successCount = 0, failCount = 0;
        
        List<String> taxpayerCodes = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalTaxpayerCodes();
        log.info("{}同步红色申请单定时任务开始，批次号:{}，总企业税号数：{}", LOGGER_MSG, batchNo, taxpayerCodes.size());
        for (String taxpayerCode : taxpayerCodes) {
            JSONArray errorMsgArray = specialInvoiceService.syncSpecialInvoiceReversal("000000000000", taxpayerCode, ConfigureConstant.STRING_1, OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey(), ConfigureConstant.STRING_1, "admin");
            if (null != errorMsgArray && errorMsgArray.size() > 0) {
                failCount++;
                log.error("{}同步红色申请单定时任务，同步失败。批次号：{},企业税号:{},错误信息:{}", LOGGER_MSG, batchNo, taxpayerCode, errorMsgArray.toJSONString());
            } else {
                successCount++;
            }
        }
        
        long endTime = System.currentTimeMillis();
        log.info("{}同步红色申请单定时任务结束，批次号:{},耗时:{},总企业税号数:{},成功企业税号数:{},失败企业税号数:{}", LOGGER_MSG, batchNo, endTime - startTime, taxpayerCodes.size(), successCount, failCount);
        
        return SUCCESS;
    }
}
