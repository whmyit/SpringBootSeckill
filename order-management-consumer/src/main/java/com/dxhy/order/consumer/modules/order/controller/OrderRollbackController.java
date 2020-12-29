package com.dxhy.order.consumer.modules.order.controller;


import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.OrderRollbackService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单回退
 * @author: chengyafu
 * @date: 2018年7月31日 下午2:01:14
 */
@RestController
@RequestMapping(value = "/order")
@Api(value = "订单回退", tags = {"订单模块"})
@Slf4j
public class OrderRollbackController {
    
    private static final String ORDER_ROLLBACK_CONTROLLER = "（订单回退接口）";
    
    @Reference(timeout = 6000000)
    private OrderRollbackService orderRollbackService;
    
    /**
     * 订单回退
     *
     * @param request
     * @param orderId
     * @return
     */
    @ApiOperation(value = "订单回退", notes = "订单回退-订单回退接口")
    @PostMapping(path = "/rollback")
    @SysLog(operation = "订单回退rest接口", operationDesc = "订单回退到上一个订单", key = "订单回退")
    public R orderRollback(HttpServletRequest request,
                           @ApiParam(name = "orderId", value = "订单和销货方税号", required = true) @RequestBody String orderId) {
        log.debug("{}收到回退请求,数据:{}", ORDER_ROLLBACK_CONTROLLER, orderId);
        if (orderId != null) {
            List<Map> idList = JSON.parseArray(orderId, Map.class);
        
            if (idList != null && idList.size() > 0) {
                R commonRspVo = orderRollbackService.orderRollback(idList);
                log.info("订单回退返回数据:{},{}", commonRspVo, ORDER_ROLLBACK_CONTROLLER);
                return commonRspVo;
            }
        
        }
        return R.setCodeAndMsg(OrderInfoContentEnum.PARAM_NULL, null);
    }
}
