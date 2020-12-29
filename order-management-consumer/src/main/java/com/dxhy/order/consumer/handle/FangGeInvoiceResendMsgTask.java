package com.dxhy.order.consumer.handle;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.openapi.service.FangGeInterfaceService;
import com.dxhy.order.model.*;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.utils.JsonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 方格开票中的数据重新发送消息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:50
 */
@Slf4j
@JobHandler(value = "fangGeInvoiceResendMsgTask")
@Component
public class FangGeInvoiceResendMsgTask extends IJobHandler {
    
    private static final String LOGGER_MSG = "(开票中的数据重新发送消息)";
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    @Resource
    private FangGeInterfaceService fangGeInterfaceService;
    
    @Override
    public ReturnT<String> execute(String param) {
        try {
            /**
             * 查询税控设备是方格的数据进行处理
             */
            List<TaxEquipmentInfo> taxEquipmentInfos = apiTaxEquipmentService.queryTaxEquipmentList(new TaxEquipmentInfo(), null);
            taxEquipmentInfos = taxEquipmentInfos.stream().filter(taxEquipmentInfo -> OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(taxEquipmentInfo.getSksbCode()) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(taxEquipmentInfo.getSksbCode()) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(taxEquipmentInfo.getSksbCode())).collect(Collectors.toList());
            List<String> shList = new ArrayList<>();
            for (TaxEquipmentInfo taxEquipmentInfo : taxEquipmentInfos) {
                shList.add(taxEquipmentInfo.getXhfNsrsbh());
            }
            log.debug("========>{}定时任务开始！！！！参数:{}", LOGGER_MSG, param);
            if (StringUtils.isEmpty(param)) {
                log.warn("请输入参数，要重发多少分钟之前的开票信息");
                return FAIL;
            }
            Calendar beforeTime = Calendar.getInstance();
            int minute = Integer.parseInt(param);
            // minute分钟之前的时间
            beforeTime.add(Calendar.MINUTE, -minute);
            Date date = beforeTime.getTime();
            String dateStr = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
            //todo fangge
            Map<String, Object> paraMap = new HashMap<>(10);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_START_TIME, dateStr);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_END_TIME, DateTime.now().toStringDefaultTimeZone());
            List<String> ddztList = Arrays.asList(OrderInfoEnum.ORDER_STATUS_4.getKey(), OrderInfoEnum.ORDER_STATUS_9.getKey());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_DDZT, ddztList);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_ORDER_STATUS, OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
    
            List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(paraMap, shList);
            log.debug("===========》需要开票的数据：{}", JsonUtils.getInstance().toJsonString(orderProcessInfos));
            if (CollectionUtils.isEmpty(orderProcessInfos)) {
                return SUCCESS;
            }
            for (OrderProcessInfo info : orderProcessInfos) {
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(info.getFpqqlsh(), shList);
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(orderInvoiceInfo.getXhfNsrsbh(), orderInvoiceInfo.getJqbh());
                if (StringUtils.isEmpty(registCodeStr)) {
                    log.warn("===========》没有查询到该税盘的注册信息，税号:{},机器编号:{}", orderInvoiceInfo.getXhfNsrsbh(), orderInvoiceInfo.getJqbh());
                    continue;
                }
                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
                orderProcessInfo.setId(orderInvoiceInfo.getOrderProcessInfoId());
                /**
                 *  方格存放开票信息到消息队列
                 *  需要判断方格开票状态,如果是签章失败需要重新走签章
                 */
                if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(orderInvoiceInfo.getKpzt()) && StringUtils.isNotEmpty(orderInvoiceInfo.getFpdm())
                        && StringUtils.isNotEmpty(orderInvoiceInfo.getFphm()) && StringUtils.isEmpty(orderInvoiceInfo.getPdfUrl())) {
        
                    String pdfid = "";
                    //如果是电票开具调底层接口
                    if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
                        OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
                        List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
    
                        Result genPdf = fangGeInterfaceService.genPdf(orderInvoiceInfo, orderInfo, orderItemInfos, OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey());
                        log.info("调用底层获取签章结果,出参{}", genPdf);
                        if (Boolean.TRUE.equals(genPdf.get(ConfigureConstant.PDF_GEN_R))) {
                            pdfid = String.valueOf(genPdf.get(ConfigureConstant.PDF_GEN_O));
                            orderInvoiceInfo.setPdfUrl(pdfid);
                            orderInvoiceInfo.setUpdateTime(new Date());
                        } else {
                            log.info("调用底层接口，获取签章失败");
                            //更新订单处理表为开票失败
                            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_6.getKey());
                            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
                            orderProcessInfo.setSbyy("签章失败");
                            //开票失败时修改获取数据状态为失败，重新拉取数据
                            orderProcessInfo.setFgStatus(ConfigureConstant.STRING_0);
                
                        }
                    }
                    //更新订单发票数据
                    apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
                    //更新订单处理表
                    apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
        
                } else {
    
                    /**
                     * 存放开票信息到redis队列
                     */
                    PushPayload pushPayload = new PushPayload();
                    //接口发票开具
                    pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);
                    pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                    pushPayload.setJQBH(registrationCode.getJqbh());
                    pushPayload.setZCM(registrationCode.getZcm());
                    pushPayload.setDDQQLSH(info.getFpqqlsh());
                    apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                    if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(info.getDdzt())) {
    
    
                        //存放redis成功，修改开票状态为开票中
                        boolean isSuccess = apiOrderProcessService.updateKpzt(info.getFpqqlsh(), OrderInfoEnum.ORDER_STATUS_4.getKey(), OrderInfoEnum.INVOICE_STATUS_1.getKey(), "", shList);
                        if (isSuccess) {
                            log.debug("===========》开票中状态发票数据更新成功，流水号为：[{}]", info.getDdzt());
                        }
                    }
        
                }
    
    
            }
        } catch (Exception e) {
            log.error("{}抛出异常：{}", LOGGER_MSG, e);
        }
        return SUCCESS;
    }
    
}
