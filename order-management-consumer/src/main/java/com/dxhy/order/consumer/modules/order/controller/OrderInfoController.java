package com.dxhy.order.consumer.modules.order.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.OderDetailInfo;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.modules.order.service.IOrderInfoService;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.consumer.utils.PageDataDealUtil;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单控制层
 *
 * @author ZSC-DXHY
 */
@Slf4j
@RestController
@Api(value = "订单信息", tags = {"订单模块"})
@RequestMapping(value = "/order")
public class OrderInfoController {
    
    private static final String LOGGER_MSG = "(订单查询控制层)";
    private static final String PATTERN_JE = "^(([-1-9]\\d*)|([0]))(\\.(\\d){0,2})?$";
    SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
    
    
    @Resource
    private IOrderInfoService orderInfoService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    /**
     * 获取订单发票列表 优税小程序调用
     *
     * @return
     */
    @ApiOperation(value = "查询订单列表", notes = "订单信息管理-查询订单列表")
    @PostMapping("/queryOrderList")
    @SysLog(operation = "查询订单列表rest接口", operationDesc = "查询订单列表", key = "订单列表")
    public R queryOrderList(@RequestBody OrderListQuery orderBatchQuery) {
        
        log.debug("{}发票明细列表查询参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
        
        try {
            
            // 数据校验
            if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())
                    && StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
                /**
                 * 判断金额 是否正确
                 */
                if (Double.parseDouble(orderBatchQuery.getMinKphjje()) > Double
                        .parseDouble(orderBatchQuery.getMaxKphjje())) {
                    log.error("{}开始金额不能大于结束金额", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
                            OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
                }
            }
            
            Pattern pattern = Pattern.compile(PATTERN_JE);
            if (StringUtils.isNotBlank(orderBatchQuery.getMinKphjje())) {
                Matcher minMatch = pattern.matcher(orderBatchQuery.getMinKphjje());
                if (minMatch.matches() == false) {
                    log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
                            OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
                }
            }
            if (StringUtils.isNotBlank(orderBatchQuery.getMaxKphjje())) {
                Matcher maxMatch = pattern.matcher(orderBatchQuery.getMaxKphjje());
                if (maxMatch.matches() == false) {
                    log.error("{}金额格式错误保留两位小数", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getKey(),
                            OrderInfoContentEnum.INVOICE_FPKJ_JE_ERROR.getMessage());
                }
            }
            
            if (StringUtils.isBlank(orderBatchQuery.getStartTime())
                    || StringUtils.isBlank(orderBatchQuery.getEndTime())) {
                
            } else {
                Date starttime = sf2.parse(orderBatchQuery.getStartTime());
                Date endtime = sf2.parse(orderBatchQuery.getEndTime());
                if (starttime.after(endtime)) {
                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(),
                            OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
                }
            }
            // 数据转换
            Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
    
            if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
                log.error("{},请求税号为空!", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
            }
    
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
    
            //查询数据库
            PageUtils page = orderInfoService.selectOrderInfo(paramMap, shList);
    
            return R.ok().put(OrderManagementConstant.DATA, page);
        } catch (ParseException e) {
            log.error("{}列表查询异常:{}", LOGGER_MSG, e);
            return R.error(OrderInfoContentEnum.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取订单详情
     * 优税小程序调用
     * @param orderId
     * @param fpqqlsh
     * @return
     */
    @ApiOperation(value = "查询订单详情", notes = "订单信息管理-查询订单详情")
    @PostMapping("/queryOrderDetail")
    @SysLog(operation = "查询订单详情rest接口", operationDesc = "订单详情数据查询", key = "订单查询")
    public R queryOrderDetail(@ApiParam(name = "orderId", value = "处理表订单ID", required = true) @RequestParam("orderId") String orderId,
                              @ApiParam(name = "fpqqlsh", value = "发票请求流水号", required = true) @RequestParam("fpqqlsh") String fpqqlsh,
                              @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        log.info("{}订单详情查询,订单处理表id{}", LOGGER_MSG, orderId);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        /**
         * 该处的orderId为orderProcessId
         */
        OderDetailInfo commonOrderInfo = orderInfoService.selectOrderDetailByOrderProcessIdAndFpqqlsh(orderId, fpqqlsh, shList);
    
        if (commonOrderInfo == null) {
            log.error("{}订单信息为空", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.PARAM_NULL);
        }
        return R.ok().put(OrderManagementConstant.DATA, commonOrderInfo);
    }
    
    
    /**
     * 订单编辑
     * 优税小助手调用
     *
     * @param data
     * @return
     */
    @ApiOperation(value = "编辑订单", notes = "订单信息管理-编辑订单")
    @PostMapping("/updateOrderInfo")
    @SysLog(operation = "编辑订单rest接口", operationDesc = "订单编辑", key = "订单编辑")
    public R updateOrderInfo(@ApiParam(name = "orderInfo", value = "订单信息", required = true) @RequestBody String data) {
        log.info("{}订单详情编辑,订单信息为:{}", LOGGER_MSG, data);
        CommonOrderInfo commonOrderInfo = JSON.parseObject(data, CommonOrderInfo.class);
        if (commonOrderInfo == null) {
            return R.error(OrderInfoContentEnum.PARAM_NULL);
        }
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        if (orderInfo == null) {
            return R.error(OrderInfoContentEnum.PARAM_NULL);
        }
        List<OrderItemInfo> orderItemInfo = commonOrderInfo.getOrderItemInfo();
        if (orderItemInfo == null || orderItemInfo.size() <= 0) {
            return R.error(OrderInfoContentEnum.HANDLE_ISSUE_202009);
        }

        try {
            Map<String, Object> map = orderInfoService.updateOrderInfoAndOrderProcessInfo(commonOrderInfo);
            R r = new R();
            r.putAll(map);
            return r;
        } catch (OrderReceiveException e) {
            log.error("{}订单编辑异常,异常原因为:{}", LOGGER_MSG, e);
            return R.error(e.getCode(), e.getMessage());
        }
    }
    
    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    @ApiOperation(value = "根据订单id查询订单详细信息", notes = "订单信息管理-根据订单id查询订单信息")
    @PostMapping("/getOrderInfoById")
    @SysLog(operation = "查询订单详情rest接口", operationDesc = "根据ID查询订单详情数据", key = "订单查询")
    public R getOrderInfoById(@ApiParam(name = "orderId", value = "订单主键id", required = true) @RequestParam("orderId") String orderId,
                              @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        CommonOrderInfo commonOrder = orderInfoService.getOrderInfoByOrderId(orderId, shList);
        List<OrderItemInfo> list = commonOrder.getOrderItemInfo();
        PageDataDealUtil.dealOrderItemInfo(list);
        commonOrder.setOrderItemInfo(list);
        return R.ok().put(OrderManagementConstant.DATA, commonOrder);
    }
    
    /**
     * 根据订单号和税号判断是否存在
     *
     * @param ddh
     * @param xhfNsrsbh
     * @return
     */
    @ApiOperation(value = "根据订单号查询订单信息是否存在", notes = "订单信息管理-根据订单号查询订单信息是否存在")
    @PostMapping("/queryOrderByDdh")
    @SysLog(operation = "查询订单rest接口", operationDesc = "根据订单号查询订单详情数据", key = "订单查询")
    public R getOrderByDdh(@ApiParam(name = "ddh", value = "订单号", required = true) @RequestParam("ddh") String ddh,
                           @ApiParam(name = "xhfNsrsbh", value = "订单号", required = true) @RequestParam("xhfNsrsbh") String xhfNsrsbh) {
    
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        if (shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = shList.get(0);
    
    
        int i = orderInfoService.getOrderInfoByDdh(ddh, nsrsbh);
    
        return R.ok().put(Constant.EXIST, i);
    }
    
    
    @ApiOperation(value = "根据发票代码发票号码查询订单信息", notes = "订单信息管理-根据发票代码发票号码查询订单信息")
    @PostMapping("/querySimpleOrderInfo")
    @SysLog(operation = "根据发票代码发票号码查询订单信息rest接口", operationDesc = "根据发票代码发票号码查询订单信息息rest接口", key = "订单查询")
    public R querySimpleOrderInfo(@ApiParam(name = "fpdm", value = "发票代码", required = true) @RequestParam("fpdm") String fpdm,
                                  @ApiParam(name = "fphm", value = "发票号码", required = true) @RequestParam("fphm") String fphm,
                                  @ApiParam(name = "xhfnsrsbh", value = "销货方税号", required = true) @RequestParam("xhfnsrsbh") String xhfnsrsbh) {
        
        log.debug("根据订单号和税号获取订单信息的接口，入参:fpdm:{},fphm:{}", fpdm, fphm);
        Map<String, Object> resultMap = orderInfoService.querySimpleOrderInfoByFpdmAndFphm(fpdm, fphm, xhfnsrsbh);
        return R.ok().put(OrderManagementConstant.DATA, resultMap);
    }
    
}
