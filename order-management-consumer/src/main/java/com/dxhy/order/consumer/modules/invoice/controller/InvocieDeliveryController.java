package com.dxhy.order.consumer.modules.invoice.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiEmailService;
import com.dxhy.order.api.ApiShorMessageSend;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：InvocieDelivery
 * @Description ：发票交付
 * @date ：2019年3月7日 下午2:36:43
 */
@RestController
@Api(value = "发票交付", tags = {"发票模块"})
@RequestMapping(value = "/email")
@Slf4j
public class InvocieDeliveryController {
    
    private static final String LOGGER_MSG = "(发票交付)";
    
    @Reference
    private ApiEmailService apiEmailService;
    
    @Reference
    private ApiShorMessageSend apiShorMessageSend;
    
    @ApiOperation(value = "发票版式文件交付", notes = "发票交付-发票版式文件交付")
    @PostMapping(value = "/sendPdfEmail")
    @SysLog(operation = "发票版式文件交付接口", operationDesc = "通过邮件交付发票版式文件", key = "发票交付")
    public R queryRedInvoiceList(@ApiParam(name = "invoiceId", value = "发票表主键id和销方税号和邮箱地址", required = false) @RequestBody String invoiceIds) {
        /**
         * 请求参数
         * {
         *   "emailAddress": "32132321@qq.com",
         *   "invoiceId": [{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"}]
         * }
         */
        log.debug("{}发票版式文件交付接口,入参:{}", LOGGER_MSG, invoiceIds);
        if (StringUtils.isBlank(invoiceIds)) {
            return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
        }
        Map mapList = JsonUtils.getInstance().parseObject(invoiceIds, Map.class);
        String eamilAddress = (String) mapList.get("emailAddress");
        List<Map> idList = JSON.parseArray(mapList.get("invoiceId").toString(), Map.class);
        R r = apiEmailService.sendPdfEmail(idList, eamilAddress);
        return r;
    }
    
    @ApiOperation(value = "发票短信交付", notes = "发票交付-发票短信交付")
    @PostMapping("/sendShortMessage")
    @SysLog(operation = "发票短信交付接口", operationDesc = "通过短信交付发票版式文件", key = "发票交付")
    public R sendShortMessage(@ApiParam(name = "invoiceId", value = "发票表主键id和销方税号和手机号", required = false) @RequestBody String invoiceIds) {
        /**
         * {
         *   "phone": "156522121212",
         *   "invoiceId": [{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"}]
         * }
         */
        log.debug("{}短信交付接口,入参invoiceId:{}", LOGGER_MSG, invoiceIds);
        if (StringUtils.isBlank(invoiceIds)) {
            return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
        }
        Map mapList = JsonUtils.getInstance().parseObject(invoiceIds, Map.class);
        String phone = (String) mapList.get("phone");
        List<Map> idList = JSON.parseArray(mapList.get("invoiceId").toString(), Map.class);
        R r = apiShorMessageSend.sendShortMessage(idList, phone);
        return r;
    }
    
    
}
