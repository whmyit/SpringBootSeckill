package com.dxhy.order.consumer.handle;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import com.dxhy.order.model.InvoiceBatchRequestItem;
import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.BeanTransitionUtils;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

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
@JobHandler(value = "InvoicePushTask")
@Component
public class InvoicePushTask extends IJobHandler {

    private static final String LOGGER_MSG = "(定时推送开票成功的发票数据)";
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiPushService apiPushService;
    
    @Resource
    private InvalidInvoiceService invalidInvoiceService;
    
    
    @Override
    public ReturnT<String> execute(String param) {
        try {
            log.info("========>{}定时任务开始！！！！", LOGGER_MSG);
            log.info("定时任务开始执行，参数:{}", param);
            Map paramMap = JsonUtils.getInstance().parseObject(param, Map.class);
            
            //获取推送状态参数 如果没有参数 默认开票成功的
            List<String> kpzt = new ArrayList<>();
            if (paramMap.get("kpzt") != null) {
                String zt = String.valueOf(paramMap.get("kpzt"));
                if (StringUtils.isNotBlank(zt)) {
                    String[] split = zt.split(",");
                    for (String sp : split) {
                        kpzt.add(sp);
                    }
                } else {
                    kpzt.add(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                }
            } else {
                kpzt.add(OrderInfoEnum.INVOICE_STATUS_2.getKey());
            }
            paramMap.put("kpztList", kpzt);
            paramMap.remove("kpzt");
    
            //如果有推送状态的参数 根据状态推送 如果没有推送状态的参数 未推送和推送失败的
            List<String> pushStatus = new ArrayList<>();
            if (paramMap.get("pushStatus") != null) {
                String status = String.valueOf(paramMap.get("pushStatus"));
                if (StringUtils.isNotBlank(status)) {
                    pushStatus = JsonUtils.getInstance().parseObject(status, List.class);
                } else {
                    //未推送
                    pushStatus.add(OrderInfoEnum.PUSH_STATUS_0.getKey());
                    //推送失败
                    pushStatus.add(OrderInfoEnum.PUSH_STATUS_2.getKey());
                }
    
            } else {
                //未推送
                pushStatus.add(OrderInfoEnum.PUSH_STATUS_0.getKey());
                //推送失败
                pushStatus.add(OrderInfoEnum.PUSH_STATUS_2.getKey());
            }
            paramMap.put("pushStatusList", pushStatus);
            paramMap.remove("pushStatus");
    
            List<String> fpqqlshList = JsonUtils.getInstance().parseObject(String.valueOf(paramMap.get("fpqqlshs")), List.class);
            paramMap.put("fpqqlshs", fpqqlshList);
    
            //查询开票成功未推送的数量
            Date currentTime = new Date();
            String format = new SimpleDateFormat(ConfigureConstant.DATE_FORMAT_DATE_Y_M_DH_M_S).format(currentTime);
            if (paramMap.get("endTime") == null) {
                paramMap.put("endTime", format);
            }
            if (paramMap.get("startTime") == null) {
                paramMap.put("startTime", DateUtil.beginOfDay(new Date()).toStringDefaultTimeZone());
            }
    
            if (StringUtils.isBlank(paramMap.get("nsrsbh").toString())) {
                log.error("{},请求税号为空!", LOGGER_MSG);
                return FAIL;
            }
    
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(paramMap.get("nsrsbh").toString());
    
            log.info("==================>{},查询开票成功未推送或推送失败的发票入参为：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramMap));
            List<OrderInvoiceInfo> selectInvoiceInfoByPushStatus = apiOrderInvoiceInfoService.selectInvoiceInfoByPushStatus(paramMap, shList);
    
            log.info("==================>{},查询开票成功未推送或推送失败的发票条数：{}", LOGGER_MSG, selectInvoiceInfoByPushStatus.size());
    
            int pushCount = 0;
            //获取推送地址配置 缓存到内存中
            for (OrderInvoiceInfo orderInvoiceInfo : selectInvoiceInfoByPushStatus) {
                InvoiceBatchRequestItem selectByFpqqlsh = apiInvoiceCommonService.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                log.info("{},定时任务推送的开票流水号：{}，纳税人识别号：{}", LOGGER_MSG, orderInvoiceInfo.getKplsh(), orderInvoiceInfo.getXhfNsrsbh());
        
                if (selectByFpqqlsh == null) {
                    log.info("{}发票流水号为：{}在批次invoiceBatchRequestItem表中没有找到！", LOGGER_MSG, orderInvoiceInfo.getKplsh());
                    continue;
                }
        
                InvoicePush invoicePush = BeanTransitionUtils.transitionInvoicePush(orderInvoiceInfo, selectByFpqqlsh.getFpqqpch());
        
                invoicePush.setSTATUSCODE(selectByFpqqlsh.getStatus());
                invoicePush.setSTATUSMSG(selectByFpqqlsh.getMessage());
                Map<String, String> paraMap = new HashMap<>(2);
                paraMap.put("id", orderInvoiceInfo.getOrderInfoId());
                paraMap.put("xhfNsrsbh", orderInvoiceInfo.getXhfNsrsbh());
                List<Map> list = new ArrayList<>();
                list.add(paraMap);
                /**
                 * 添加支持作废状态回推
                 */
                invalidInvoiceService.manualPushInvalidInvoice(list);
        
        
                R r = apiPushService.pushRouting(JsonUtils.getInstance().toJsonString(invoicePush));
                if (ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                    log.info("{}发票推送成功！批次号：{},流水号Kplsh：{}，发票代码：{}，发票号码：{}",
                            LOGGER_MSG, invoicePush.getFPQQPCH(), invoicePush.getFPQQLSH(), invoicePush.getFP_DM(), invoicePush.getFP_HM());
                    pushCount++;
                } else {
                    log.info("发票推送返回状态码：{}", r.get(OrderManagementConstant.CODE));
                    log.info("{}发票推送失败！批次号：{},流水号Kplsh：{}，发票代码：{}，发票号码：{}",
                            LOGGER_MSG, invoicePush.getFPQQPCH(), invoicePush.getFPQQLSH(), invoicePush.getFP_DM(), invoicePush.getFP_HM());
                }
            }
            log.info("此次定时任务共推送发票张数：{}，推送成功数量：{}", selectInvoiceInfoByPushStatus.size(), pushCount);
            log.info("========>{}定时任务结束！！！！", LOGGER_MSG);
        } catch (Exception e) {
            log.error("{}抛出异常：{}", LOGGER_MSG, e);
        }
        return SUCCESS;
    }
    
}
