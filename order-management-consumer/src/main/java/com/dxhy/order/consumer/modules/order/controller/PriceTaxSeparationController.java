package com.dxhy.order.consumer.modules.order.controller;

import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.consumer.modules.order.service.ISeparationService;
import com.dxhy.order.consumer.utils.InterfaceResponseUtils;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 价税分离
 * @author: chengyafu
 * @date: 2018年7月24日 下午6:04:11
 */
@RestController
@RequestMapping(value = "/order")
@Api(value = "价税分离", tags = {"订单模块"})
@Slf4j
public class PriceTaxSeparationController {
    
    private final String LOGGER_MSG = "(价税分离控制层)";
    
    @Resource
    private ISeparationService separationService;
    
    /**
     * todo 未发现引用,
     * 价税分离接口
     *
     * @param req
     * @param order
     * @return
     * @throws OrderSeparationException
     */
    @Deprecated
    @ApiOperation(value = "价税分离接口", notes = "价税分离-价税分离接口")
    @PostMapping(value = "/separationOrder")
    public R separationOrder(HttpServletRequest req,
                             @ApiParam(name = "order", value = "企业订单", required = true) @RequestBody(required = true) CommonOrderInfo order) throws OrderSeparationException {
        
        log.debug("{}得到订单信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(order));
        CommonOrderInfo orderInfoAndItemInfo = separationService.taxSeparationService(order);
        if (orderInfoAndItemInfo != null) {
            log.debug("{}返回价税分离后的订单信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfoAndItemInfo));
            return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.PRICE_TAX_SEPARATION_SUCCESS, orderInfoAndItemInfo);
        }
        return InterfaceResponseUtils.buildErrorInfo(OrderInfoContentEnum.PARAM_NULL);
    }
}
