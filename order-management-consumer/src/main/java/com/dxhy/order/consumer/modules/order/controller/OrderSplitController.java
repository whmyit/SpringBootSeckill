package com.dxhy.order.consumer.modules.order.controller;

import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.PageCommonOrderInfo;
import com.dxhy.order.consumer.modules.order.service.OrderSplitService;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author ：杨士勇
 * @ClassName ：OrderSplitContorller
 * @Description ：订单拆分控制层
 * @date ：2018年7月30日 上午10:06:56
 */

@Api(value = "订单拆分", tags = {"订单模块"})
@RestController
@RequestMapping("/orderSplit")
@Slf4j
public class OrderSplitController {
    
    private static final String LOGGER_MSG = "(订单拆分接口)";
    
    @Resource
    private OrderSplitService orderSplitService;

    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    /**
     * 根据开票项目拆分订单接口,对应前端-单据管理-单据详情-明细行拆分功能
     *
     * @param commonList
     * @return
     */
    @ApiOperation(value = "根据开票项目拆分订单接口", notes = "订单拆分-根据开票项目拆分订单信息")
    @PostMapping("/itemSplit")
    @SysLog(operation = "按项目拆分订单rest接口", operationDesc = "根据开票项目对订单进行拆分", key = "订单拆分")
    public R itemSplit(
            @ApiParam(name = "orderInfo", value = "拆分订单的全信息", required = false) @RequestBody List<PageCommonOrderInfo> commonList) {
        try {
            log.info("{}页面订单信息录入的接口，前端传入的参数commonList:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonList));
            /**
             * 判断订单来源是否为扫码开票
             */
            List<String> orderIds = new ArrayList<>();
            List<String> shList = new ArrayList<>();
            for (PageCommonOrderInfo info : commonList) {
                orderIds.add(info.getOrderInfo().getId());
                shList.add(info.getOrderInfo().getXhfNsrsbh());
            }
        
            shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
            R r = checkSweepCodeMakeOutOnInvoice(orderIds, shList);
            if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                return r;
            }
        
            orderSplitService.saveOrderSplitOrder(commonList);
            return R.ok();
        } catch (OrderReceiveException e) {
            log.warn("{}订单拆分接口保存失败：{}", LOGGER_MSG, e);
            return R.ok().put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE, e.getMessage());
        } catch (Exception e) {
            log.error("{}订单拆分接口异常，异常信息:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    /**
     * 判断合并订单是否为扫码开票
     */
    private R checkSweepCodeMakeOutOnInvoice(List<String> orderIds, List<String> shList) {
        boolean isQrCodeMakeOutOnInvoice = false;
        for (String orderId : orderIds) {
            OrderProcessInfo processInfo = apiOrderProcessService.selectByOrderId(orderId, shList);
            if (!ObjectUtils.isEmpty(processInfo)) {
                if (OrderInfoEnum.ORDER_SOURCE_5.getKey().equals(processInfo.getDdly()) || OrderInfoEnum.ORDER_SOURCE_6.getKey().equals(processInfo.getDdly())) {
                    isQrCodeMakeOutOnInvoice = true;
                    break;
                }
            }
        }
        //订单来源为扫码开票
        if (isQrCodeMakeOutOnInvoice) {
            return R.error("扫码开票的订单不能合并");
        }
        return R.ok();
    }
    
    /**
     * 金额拆分订单,,对应前端-单据管理-单据详情-金额拆分功能
     *
     * @param @param  orderId
     * @param @param  orderItemId
     * @param @return
     * @param @throws Exception
     * @return R
     * @throws
     * @Title :
     * @Description ：根据金额拆分的金额 规则：用户如果输入一个金额，拆分成两个订单，一次类推，最多输入三个金额，保数量 按含税金额拆分
     */
    @ApiOperation(value = "根据金额拆分订单接口", notes = "订单拆分-根据金额拆分订单信息")
    @PostMapping("/priceSplit")
    @SysLog(operation = "按单价拆分订单rest接口", operationDesc = "根据单价对订单进行拆分", key = "订单拆分")
    public R splitByDj(
            @ApiParam(name = "orderId", value = "订单主键id", required = true) @RequestParam("orderId") String orderId,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "jeArray", value = "传入的金额数组", required = false) @RequestParam("jeArray") String jeArray) {
    
        String[] parseJeArray = JsonUtils.getInstance().parseObject(jeArray, String[].class);
        R vo = new R();
        log.info("{}拆分的接口，前端传入的参数orderId:{},orderItemId:{}", LOGGER_MSG, orderId, parseJeArray);
        try {
            if (StringUtils.isBlank(xhfNsrsbh)) {
                log.error("{},请求税号为空!", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
            }
    
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
            /**
             * 判断订单来源是否为扫码开票
             */
            List<String> orderIds = new ArrayList<>();
            orderIds.add(orderId);
            R r = checkSweepCodeMakeOutOnInvoice(orderIds, shList);
            if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                return r;
            }
            /**
             * 拆分订单
             */
            List<CommonOrderInfo> splitOrder = orderSplitService.splitOrder(orderId, shList, parseJeArray, OrderInfoEnum.ORDER_SPLIT_JE_ARRAY.getKey());
            vo.put(OrderManagementConstant.DATA, splitOrder);
        } catch (OrderSplitException e) {
            log.error("{}订单多金额拆分异常:{}", LOGGER_MSG, e);
            vo.put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE, e.getMessage());
            return vo;
        } catch (Exception e) {
            log.error("订单拆分异常：{}", e);
            vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            vo.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            return vo;
        }
        
        return vo;
    }
    
    /**
     * 数量拆分订单,,对应前端-单据管理-单据详情-数量拆分功能
     *
     * @param @param  orderId
     * @param @param  orderItemId
     * @param @return
     * @param @throws Exception
     * @return R
     * @throws
     * @Title : splitBySL
     * @Description ：根据数量拆分 输入一个数量 拆分成两个订单，以此类推，最多输入三个数量
     */
    @ApiOperation(value = "根据数量拆分订单接口", notes = "订单拆分-根据数量拆分订单信息")
    @PostMapping("/amountSplit")
    @SysLog(operation = "按数量拆分订单rest接口", operationDesc = "根据数量对订单进行拆分", key = "订单拆分")
    public R splitBySl(
            @ApiParam(name = "orderId", value = "订单主键id", required = true) @RequestParam("orderId") String orderId,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "slArray", value = "数量数组", required = false) @RequestParam("slArray") String slArray) {
        log.info("{}页面订单信息录入的接口，前端传入的参数orderId:{},orderItemId:{}", LOGGER_MSG, orderId, JsonUtils.getInstance().toJsonString(slArray));
        String[] parseSlArray = JsonUtils.getInstance().parseObject(slArray, String[].class);
        R vo = new R();
        try {
            if (StringUtils.isBlank(xhfNsrsbh)) {
                log.error("{},请求税号为空!", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
            }
    
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
            List<CommonOrderInfo> splitOrder = orderSplitService.splitOrder(orderId, shList, parseSlArray, OrderInfoEnum.ORDER_SPLIT_SL_ARRAY.getKey());
            vo.put(OrderManagementConstant.DATA, splitOrder);
        } catch (OrderSplitException e) {
            log.error("订单按数量拆分异常:{}", LOGGER_MSG, e);
            vo.put(OrderManagementConstant.CODE, e.getCode());
            vo.put(OrderManagementConstant.MESSAGE, e.getMessage());
            return vo;
        } catch (Exception e) {
            log.error("订单拆分异常：{}", e);
            vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            vo.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            return vo;
        }
        
        return vo;
    }
    
    /**
     * 拆分后数据保存功能,对应前端
     *
     * @param @param  orderId
     * @param @param  orderItemId
     * @param @return
     * @param @throws Exception
     * @return R
     * @throws
     * @Description ：订单拆分后数据的保存接口
     */
    @ApiOperation(value = "订单拆分后的数据保存接口", notes = "订单拆分-订单拆分后的数据保存接口")
    @PostMapping("/saveOrderInfo")
    @SysLog(operation = "保存拆分订单rest接口", operationDesc = "对订单进行拆分后数据保存", key = "订单拆分")
    public R saveOrderInfo(
            @ApiParam(name = "orderInfo", value = "拆分订单的全信息", required = false) @RequestBody List<CommonOrderInfo> commonList) throws Exception {
        log.info("{}页面订单信息录入的接口，commonOrder:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonList));
        try {
            log.info("{}页面订单信息录入的接口，前端传入的参数commonList:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonList));
            orderSplitService.splitOrder(commonList);
            return R.ok();
        } catch (OrderReceiveException e) {
            log.error("订单按数量拆分异常:{}", LOGGER_MSG, e);
        
            return R.error().put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE, e.getMessage());
        } catch (Exception e) {
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
        
        }
    }
    
    
}
