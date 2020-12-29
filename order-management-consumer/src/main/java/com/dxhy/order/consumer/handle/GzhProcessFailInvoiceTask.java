package com.dxhy.order.consumer.handle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.api.ApiOrderQrcodeExtendService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.modules.order.service.MyinvoiceRequestService;
import com.dxhy.order.consumer.modules.scaninvoice.service.ScanInvoiceService;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.OrderQrcodeExtendInfo;
import com.dxhy.order.model.R;
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
import java.util.*;


/**
 * 公众号处理开具失败的发票数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-19 9:34
 */
@Slf4j
@JobHandler(value = "gzhProcessFailInvoiceTask")
@Component
public class GzhProcessFailInvoiceTask extends IJobHandler {
    
    private static final String LOGGER_MSG = "(公众号处理开具失败数据)";
    
    @Resource
    private MyinvoiceRequestService myinvoiceRequestService;
    
    @Reference
    private ApiOrderQrcodeExtendService apiOrderQrcodeExtendService;
    
    @Resource
    private ScanInvoiceService scanInvoiceService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Override
    public ReturnT<String> execute(String param) {
        try {
            /**
             *
             * 1.调用数据库查询未回推授权消息的数据
             * 2.如果存在数据,直接请求开票接口进行开票
             */
            log.info("{}定时任务开始执行，参数:{}", LOGGER_MSG, param);
            Map<String, Object> paramMap = new HashMap<>(10);
            if (StringUtils.isNotBlank(param)) {
                paramMap = JsonUtils.getInstance().parseObject(param, Map.class);
            }
            List<String> shList = null;
            
            boolean result = ObjectUtil.isNull(paramMap) || (ObjectUtil.isNotNull(paramMap) && ObjectUtil.isNull(paramMap.get("startTime")) && ObjectUtil.isNull(paramMap.get("endTime")));
            if (result) {
                /**
                 * 判断定时任务查询时间范围,如果为空,默认查询结束时间为当前时间前5个小时,开始时间为结束时间一天前的数据
                 */
                
                Date currentTime = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentTime);
                calendar.add(Calendar.HOUR_OF_DAY, -5);
                Date endTime = calendar.getTime();
                calendar.add(Calendar.DATE, -1);
                Date startTime = calendar.getTime();
                paramMap.put("startTime", DateUtil.formatDateTime(startTime));
                paramMap.put("endTime", DateUtil.formatDateTime(endTime));
            } else {
                if (ObjectUtil.isNotNull(paramMap.get("nsrsbh"))) {
                    shList = JsonUtils.getInstance().jsonToList(paramMap.get("nsrsbh").toString(), List.class);
                }
            }
        
            /**
             * 查询数据库数据
             */
            log.debug("{}查询公众号已授权未开票请求数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramMap));
            List<OrderQrcodeExtendInfo> orderQrcodeExtendInfoList = apiOrderQrcodeExtendService.selectOrderQrcodeExtendInfoForTask(paramMap, shList);
            if (ObjectUtil.isNotEmpty(orderQrcodeExtendInfoList)) {
                for (OrderQrcodeExtendInfo orderQrcodeExtendInfo : orderQrcodeExtendInfoList) {
                    log.debug("{}处理公众号已授权未开票数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderQrcodeExtendInfo));
                    try {
                    
                    
                        /**
                         * 判断当前数据的状态,如果是未开票,进行开票操作,
                         */
                        Map<String, Object> authStatus = myinvoiceRequestService.getAuthStatus(orderQrcodeExtendInfo.getAuthOrderId(), "");
                    
                        if (ObjectUtil.isNotNull(authStatus)) {
                            //返回鉴权结果,根据鉴权成功的判断,返回为0,标识鉴权成功,
                            if (ObjectUtil.isNotNull(authStatus.get(ConfigureConstant.STRING_ERRCODE)) && ConfigureConstant.STRING_0.equals(authStatus.get(ConfigureConstant.STRING_ERRCODE).toString())) {
    
                                /**
                                 * 根据invoice_status进行判断,
                                 * invoice send表示插卡成功
                                 * auth success表示授权成功
                                 *
                                 */
                                if (ObjectUtil.isNotNull(authStatus.get("invoice_status")) && "auth success".equals(authStatus.get("invoice_status").toString())) {
        
                                    R r = new R();
                                    /**
                                     * 查询订单表,判断状态是开票中的数据
                                     */
                                    if (StringUtils.isNotBlank(orderQrcodeExtendInfo.getFpqqlsh())) {
                                        OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(orderQrcodeExtendInfo.getFpqqlsh(), NsrsbhUtils.transShListByNsrsbh(orderQrcodeExtendInfo.getXhfNsrsbh()));
                                        boolean result1 = ObjectUtil.isNotNull(orderProcessInfo) && !OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) && !OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt()) && !OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) && !OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt());
                                        if (result1) {
                                            //鉴权成功,迟迟未收到消息,需要调用开票接口继续开票
                                            r = scanInvoiceService.authOrderInvoice(orderQrcodeExtendInfo.getAuthOrderId());
                                        }
        
                                    } else {
                                        //鉴权成功,迟迟未收到消息,需要调用开票接口继续开票
                                        r = scanInvoiceService.authOrderInvoice(orderQrcodeExtendInfo.getAuthOrderId());
                                    }
    
                                    log.debug("{}开票申请调用结果为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(r));
                                }
                            
                            
                            }
                        
                        }
                    } catch (Exception e) {
                        log.error("{}处理公众号已授权未开票数据失败,异常信息为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(e));
                    }
                }
            }
        } catch (Exception e) {
            log.error("{}处理公众号已授权未开票数据失败,异常信息为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(e));
        }
    
        return SUCCESS;
    }
    
}
