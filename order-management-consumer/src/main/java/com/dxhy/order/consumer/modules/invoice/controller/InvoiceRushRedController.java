package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceRushRedService;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 发票冲红
 * @author: chengyafu
 * @date: 2018年7月25日 上午10:26:02
 */
@RestController
@Api(value = "电票冲红", tags = {"发票模块"})
@RequestMapping(value = "/invoice")
@Slf4j
public class InvoiceRushRedController {
    
    private static final String LOGGER_MSG = "(订单冲红接口)";
    @Resource
    private InvoiceRushRedService invoiceRushRedService;
    
    /**
     * 电票冲红
     *
     * @param request
     * @param fpdm
     * @param fphm
     * @param chyy
     * @return
     * @throws OrderSeparationException
     */
    @ApiOperation(value = "发票冲红", notes = "发票冲红")
    @PostMapping("/rushRed")
    @SysLog(operation = "发票冲红rest接口", operationDesc = "发票冲红", key = "发票冲红")
    public R eleRush(HttpServletRequest request,
                     @ApiParam(name = "fpdm", value = "发票代码", required = true) @RequestParam(required = true, value = "fpdm") String fpdm,
                     @ApiParam(name = "fphm", value = "发票号码", required = true) @RequestParam(required = true, value = "fphm") String fphm,
                     @ApiParam(name = "chyy", value = "冲红原因", required = false) @RequestParam(required = false, value = "chyy") String chyy,
                     @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh
    ) throws OrderSeparationException {
    
        if ((StringUtils.isBlank(fphm)) && (StringUtils.isBlank(fpdm)) && (StringUtils.isBlank(chyy))) {
            R r = new R();
            log.error("{}接收冲红发票信息为空", LOGGER_MSG);
            return r.put(OrderInfoContentEnum.INVOICE_RUSH_RED_NULL.getKey(), OrderInfoContentEnum.INVOICE_RUSH_RED_NULL.getMessage());
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        return invoiceRushRedService.eleRush(fpdm, fphm, chyy, shList);
    }
    
}
