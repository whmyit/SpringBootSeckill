package com.dxhy.order.consumer.modules.invoice.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.InvoiceDataService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceReciveService;
import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：InvocieReceiveController
 * @Description ：发票开具结果接收接口
 * @date ：2019年6月3日 下午2:54:23
 */
@Api(value = "开票结果接收", tags = {"发票模块"})
@RestController
@RequestMapping("/receive")
@Slf4j
public class InvocieReceiveController {
    
    
    public static final String LOG_MSG = "(发票开具结果接收接口)";
    
    @Reference
    private InvoiceDataService invoiceDataService;
    
    @Resource
    private InvoiceReciveService invoiceReciveService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private RedisService redisService;
    
    /**
     * 接收底层发票推送的接口
     */
    @ApiOperation(value = "发票开具结果接收", notes = "开票结果接收-发票开具结果接收")
    @PostMapping("/invoice")
    public R receiveInvoice(HttpServletRequest request) {
        
        //调用数据处理的接口
        R r;
        try {
            String data = IOUtils.toString(request.getInputStream(), Charsets.UTF_8);
            log.debug("{},接收A9发票推送的接口入参:{}", LOG_MSG, data);
            InvoicePush parseObject = JsonUtils.getInstance().parseObject(data, InvoicePush.class);
    
            /**
             * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
             *
             */
            String cacheKplsh = String.format(Constant.REDIS_KPLSH, parseObject.getFPQQLSH());
            String xhfNsrsbh = redisService.get(parseObject.getFPQQLSH());
            if (StringUtils.isBlank(xhfNsrsbh)) {
                OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
                orderInvoiceInfo1.setKplsh(parseObject.getFPQQLSH());
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfo(orderInvoiceInfo1, null);
                if (orderInvoiceInfo != null && StringUtils.isNotBlank(orderInvoiceInfo.getXhfNsrsbh())) {
        
                    redisService.set(cacheKplsh, orderInvoiceInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                    xhfNsrsbh = orderInvoiceInfo.getXhfNsrsbh();
                }
            }
    
            parseObject.setNSRSBH(xhfNsrsbh);
    
            //异常订单预警推送
            invoiceReciveService.pushExceptionMessageToItax(parseObject);
            r = invoiceDataService.receiveInvoice(parseObject);
            return r;
        } catch (IOException e) {
            log.error("{}接收发票信息异常:{}", LOG_MSG, e);
            return R.error();
        }
    }
    
    /**
     * 发票手动回推企业
     */
    @ApiOperation(value = "手动推送", notes = "开票结果接收-手动推送")
    @PostMapping("/manualPushInvoice")
    @SysLog(operation = "手动推送发票数据", operationDesc = "手动推送发票数据", key = "推送发票")
    public R manualPushInvoice(HttpServletRequest request, @ApiParam(name = "orderInfoIdArray", value = "订单id", required = true) @RequestBody String orderInfoIdArray) {
        R r;
        try {
            if (StringUtils.isBlank(orderInfoIdArray)) {
                return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
            }
            
            List<Map> idList = JSON.parseArray(orderInfoIdArray, Map.class);
            
            r = invoiceDataService.manualPushInvoice(idList);
            return r;
        } catch (Exception e) {
            log.error("{}手动回推发票异常:{}", LOG_MSG, e);
            return R.error();
        }
    }
}
