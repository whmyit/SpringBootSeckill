package com.dxhy.order.consumer.handle;

import com.dxhy.order.api.ApiEmailService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.api.ApiYpWarningService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.itaxmsg.service.IItaxMsgService;
import com.dxhy.order.model.a9.sld.SldKcByFjhResponseExtend;
import com.dxhy.order.model.a9.sld.SldKcRequest;
import com.dxhy.order.model.a9.sld.SldKcmxByFjh;
import com.dxhy.order.model.entity.InvoiceWarningInfo;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description 余票预警定时任务
 * @Author xieyuanqiang
 * @Date 11:09 2018-09-19
 */
@Slf4j
@Component
@JobHandler(value = "/ypyjTask")
public class YpyjTask extends IJobHandler {
    @Reference
    private ApiYpWarningService ypWarningService;
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    @Resource
    private IItaxMsgService IItaxMsgService;
    @Reference
    private ApiEmailService apiEmailService;
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    @Resource
    private UnifyService unifyService;
    
    @Override
    public ReturnT<String> execute(String s) {
        log.info("ypyjTask 余票预警 定时任务开始执行");
    
        // 获取开票信息
        List<InvoiceWarningInfo> data = ypWarningService.selectYpWarning(new InvoiceWarningInfo(), null);
        String messageHead = "截至" + DateUtils.format(new Date(), "yyyy年MM月dd日 HH:mm:ss");
        if (CollectionUtils.isNotEmpty(data)) {
            for (InvoiceWarningInfo ypWarningEntity : data) {
                try {
                    if (StringUtils.isBlank(ypWarningEntity.getXhfNsrsbh())) {
                        continue;
                    }
                    // 查询税控设备
                    String terminalCode = apiTaxEquipmentService.getTerminalCode(ypWarningEntity.getXhfNsrsbh());
                    List<String> shList = new ArrayList<>();
                    shList.add(ypWarningEntity.getXhfNsrsbh());
    
                    SldKcRequest kccxRequest = new SldKcRequest();
                    kccxRequest.setNsrsbh(ypWarningEntity.getXhfNsrsbh());
                    if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                        kccxRequest.setJqbh(ypWarningEntity.getSbbh());
                    } else {
                        kccxRequest.setFjh(ypWarningEntity.getSbbh());
                    }
    
    
                    SldKcByFjhResponseExtend sldKcByFjhResponseExtend = HttpInvoiceRequestUtil.queryKcxxByFjh(OpenApiConfig.querykcxxByFjh, kccxRequest, terminalCode);
                    if (sldKcByFjhResponseExtend != null && CollectionUtils.isNotEmpty(sldKcByFjhResponseExtend.getKcmxes())) {
                        List<SldKcmxByFjh> kcmxs = sldKcByFjhResponseExtend.getKcmxes();
                        Map<String, Integer> map = new HashMap<>(10);
                        if (CollectionUtils.isNotEmpty(kcmxs)) {
                            for (SldKcmxByFjh k : kcmxs) {
                                if (!"41".equals(k.getFpzlDm())) {
                                    if (map.containsKey(k.getFpzlDm())) {
                                        int x = map.get(k.getFpzlDm()) + Integer.parseInt(k.getFpfs());
                                        map.put(k.getFpzlDm(), x);
                                    } else {
                                        map.put(k.getFpzlDm(), Integer.valueOf(k.getFpfs()));
                                    }
                                }
                            }
                            map.forEach((fpzldm, fpfs) -> {
                                List<InvoiceWarningInfo> result = ypWarningService.selectYpWarning(ypWarningEntity, shList);
                                result.forEach(invoiceWarningInfo -> {
                                    if (StringUtils.equals(fpzldm, invoiceWarningInfo.getFpzlDm())) {
                                        int emailNum = Integer.parseInt(invoiceWarningInfo.getYjcs());
                                        if (new BigDecimal(fpfs.toString()).compareTo(new BigDecimal(invoiceWarningInfo.getYjfs())) < 0) {
                                        
                                            log.info("开始预警 税盘号{} 发票类型{} 发票份数{} 预警份数{}", invoiceWarningInfo.getSbbh(), invoiceWarningInfo.getFpzlDm(),
                                                    fpfs.toString(), invoiceWarningInfo.getYjfs());
                                            String mc = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm) ? "增值税专用发票"
                                                    : (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm) ? "增值税普通发票"
                                                    : (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm) ? "增值税电子普通发票" : "其他类型"));
                                            String message = "";
                                        
                                            message = String.format(Constant.ERROR_MESSAGE_INVOICE, invoiceWarningInfo.getXhfNsrsbh(), invoiceWarningInfo.getSbbh(), fpfs.toString(), mc, invoiceWarningInfo.getYjfs()) + "\n" + message;
                                        
                                            log.info("向i-tax系统推送消息开始了。。。");
                                            if (emailNum < 1) {
                                                IItaxMsgService.sessMessageToTax("余票预警", message, "2",
                                                        invoiceWarningInfo.getUserId(), invoiceWarningInfo.getDeptId());
                                            }
                                            if (OrderInfoEnum.ORDER_WARNING_OPEN.getKey().equals(invoiceWarningInfo.getSfyj())) {
                                            
                                                log.info("邮件预警次数{}", emailNum);
                                                if (emailNum < 1) {
                                                    apiEmailService.sendInvoiceWarningInfoEmail(invoiceWarningInfo, fpfs.toString(), mc);
                                                    emailNum = 1;
                                                }
                                            }
                                        
                                        }
                                        invoiceWarningInfo.setYjcs(String.valueOf(emailNum));
                                        log.info("更新发票预警信息。。。");
                                        int reslut = ypWarningService.updateYpWarnInfo(invoiceWarningInfo, shList);
                                        log.info("更新发票预警信息结果    {}", reslut > 0);
                                    }
                                });
                            });
                        }
                    } else {
                        log.info("ypyjTask 余票预警 调用开票系统查询税盘库存接口 结果为空");
                    }
                } catch (Exception e) {
                    log.error("ypyjTask 余票预警 业务处理异常 异常信息:{}", e);
                
                }
            }
        } else {
            log.info("ypyjTask 余票预警 查询税盘号 结果为空");
        }
        return new ReturnT<>(200, "业务处理成功");
    
    
    }
    
}
