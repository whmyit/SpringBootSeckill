package com.dxhy.order.consumer.modules.invoice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 订单发票控制层
 *
 * @author ZSC-DXHY
 */
@RestController
@Api(value = "订单发票信息", tags = {"发票模块"})
@RequestMapping(value = "/orderInvoice")
@Slf4j
public class OrderInvoiceController {
    
    private static final String LOGGER_MSG = "(订单发票控制层)";
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;

    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    @Resource
    private InvoiceService invoiceService;
    
    /**
     * 在使用
     * 发票明细
     * 优税 小助手调用
     */
    @PostMapping("/queryInvoiceDetails")
    @ApiOperation(value = "查询发票列表", notes = "订单发票管理-查询发票列表说明")
    @SysLog(operation = "发票信息查询", operationDesc = "发票信息查询", key = "订单发票管理")
    public R queryInvoiceDetails(@RequestBody OrderListQuery orderBatchQuery) {
        log.debug("{}发票明细列表查询参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
        //数据转换
        Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
        if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
        PageUtils pageUtils = apiOrderInvoiceInfoService.selectRedAndInvoiceByMap(paramMap, shList);
    
        log.debug("{}发票明细列表查询，返回参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(pageUtils));
    
        Map<String, Object> numMap = apiOrderInvoiceInfoService.queryCountByMap(paramMap, shList);
    
        log.info("{}发票明细统计返回值{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(numMap));
    
        return R.ok().put("page", pageUtils).put("numMap", numMap);
    
    }

    /**
     * 逻辑删除待开订单
     * TODO 优税小助手调用
     *
     * @param ids 数组
     * @return fankunfeng
     */
    @ApiOperation(value = "逻辑删除订单", notes = "订单发票管理-逻辑删除订单")
    @PostMapping("/updateOrderStatus")
    @SysLog(operation = "订单删除rest接口", operationDesc = "逻辑删除订单单据", key = "订单删除")
    public R updateOrderStatus(
            @ApiParam(name = "ids", value = "订单标志ID") @RequestBody String ids) {
        if (StringUtils.isBlank(ids)) {
            return R.error(OrderInfoContentEnum.UPDATE_ORDER_STATUS_IDS_NULL.getKey(), OrderInfoContentEnum.UPDATE_ORDER_STATUS_IDS_NULL.getMessage());
        }

        StringBuilder errorMsgList = new StringBuilder();
        List<Map> idList = JSON.parseArray(ids, Map.class);
        for (int i = 0; i < idList.size(); i++) {
            Map<String, Object> map = idList.get(i);

            /**
             * 多条数据返回行号
             */
            String preStr = "";
            if (idList.size() > 1) {
                preStr = "第" + (i + 1) + "行:";
            }

            try {
                String id = (String) map.get("id");
                List<String> shList = new ArrayList<>();
                String nsrsbh = (String) map.get("xhfNsrsbh");
                shList.add(nsrsbh);

                //查询待罗辑删除订单是否为待开
                OrderProcessInfo orderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(id, shList);
                log.info("根据ID查询订单结果：{}", JsonUtils.getInstance().toJsonString(orderProcessInfo));
                if (orderProcessInfo == null) {
                    errorMsgList.append(preStr).append(OrderInfoContentEnum.UPDATE_ORDER_STATUS_QUERY_NULL.getMessage()).append("\r\n");
                    continue;
                }
                if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) ||
                        OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) ||
                        OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt()) ||
                        OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt()) ||
                        OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {

                    errorMsgList.append(preStr).append(OrderInfoContentEnum.UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE.getMessage()).append("\r\n");
                    continue;
                } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) ||
                        OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())) {

                    /**
                     * 订单状态为6或者8处理开票失败数据,调用底层最终状态接口,返回2101才可以进行删除
                     */
                    String terminalCode = apiTaxEquipmentService.getTerminalCode(orderProcessInfo.getXhfNsrsbh());
                    if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode) && !OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {

                        CommonInvoiceStatus commonInvoiceStatus = invoiceService.queryInvoiceStatus(orderProcessInfo.getFpqqlsh(), orderProcessInfo.getXhfNsrsbh());

                        //返回结果转换
                        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(commonInvoiceStatus.getStatusCode())) {
                            errorMsgList.append(preStr).append("查询发票状态失败!").append("\r\n");
                            continue;
                        } else {
                            // 2101换流水号,其他都不换,2001时提示开票失败,不允许编辑,不换流水号直接重试.
                            if (!OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
                                errorMsgList.append(preStr).append(OrderInfoContentEnum.UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE.getMessage()).append("\r\n");
                                continue;
                            }
                        }
                    }

                }

                OrderProcessInfo orderProcessInfo1 = new OrderProcessInfo();
                orderProcessInfo1.setId(id);
                orderProcessInfo1.setOrderStatus(ConfigureConstant.STRING_1);
                int success = apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo1, shList);
                if (success < 0) {
                    errorMsgList.append(preStr).append("数据删除失败").append("\r\n");
                    continue;
                }
            } catch (Exception e) {
                log.error("{}数据删除失败:{}", LOGGER_MSG, e);
                errorMsgList.append(preStr).append("数据删除失败").append("\r\n");
                continue;
            }

        }
        if (ObjectUtil.isNotEmpty(errorMsgList)) {
            return R.error(errorMsgList.toString());
        } else {
            return R.ok();
        }


    }
    
    /**
     * 逻辑删除待开订单
     * TODO 优税小助手调用
     *
     * @param ids 数组
     * @return fankunfeng
     */
    @ApiOperation(value = "逻辑删除订单", notes = "订单发票管理-逻辑删除订单")
    @PostMapping("/updateOrderStatusOld")
    @SysLog(operation = "订单删除rest接口", operationDesc = "逻辑删除订单单据", key = "订单删除")
    public R updateOrderStatusOld(
            @ApiParam(name = "ids", value = "订单标志ID", required = false) @RequestBody String ids) {
        if (StringUtils.isBlank(ids)) {
            return R.error(OrderInfoContentEnum.UPDATE_ORDER_STATUS_IDS_NULL.getKey(), OrderInfoContentEnum.UPDATE_ORDER_STATUS_IDS_NULL.getMessage());
        }
    
        List<Map> idList = JSON.parseArray(ids, Map.class);
        for (Map map : idList) {
            String id = (String) map.get("id");
            List<String> shList = new ArrayList<>();
            String nsrsbh = (String) map.get("xhfNsrsbh");
            shList.add(nsrsbh);
        
            //查询待罗辑删除订单是否为待开
            OrderProcessInfo orderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(id, shList);
            log.info("根据ID查询订单结果：{}", JsonUtils.getInstance().toJsonString(orderProcessInfo));
            if (orderProcessInfo == null) {
                return R.error(OrderInfoContentEnum.UPDATE_ORDER_STATUS_QUERY_NULL.getKey(), OrderInfoContentEnum.UPDATE_ORDER_STATUS_QUERY_NULL.getMessage());
            }
            if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {
                return R.error(OrderInfoContentEnum.UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE.getKey(), OrderInfoContentEnum.UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE.getMessage());
            }
            OrderProcessInfo orderProcessInfo1 = new OrderProcessInfo();
            orderProcessInfo1.setId(id);
            orderProcessInfo1.setOrderStatus(ConfigureConstant.STRING_1);
            int i = apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo1, shList);
        }
    
        return R.ok();
    }
    
    //
//    /**
//     * 复制开票功能,发票数据全复制,重新开票接口 X
//     * 1.功能为复制开票，统一处理，只修改批次信息，流水信息，其他都一致
//     * 2.支持普票专票，修改为还支持电票
//     * ==>此处不改，重写
//     *
//     * @param deptId
//     * @return
//     */
//    @ApiOperation(value = "重新开票", notes = "订单发票管理-重新开具发票的接口")
//    @PostMapping("/remakeInvoice")
//    @SysLog(operation = "重新开票rest接口", operationDesc = "重新开具发票接口", key = "发票重开")
//    public R remakeInvoice(
//            @ApiParam(name = "orderIdArray", value = "订单id和销方税号", required = false) @RequestParam(value = "orderIdArray", required = false) String orderIdArray,
//            @ApiParam(name = "sld", value = "受理点", required = false) @RequestParam(value = "sld", required = false) String sld,
//            @ApiParam(name = "sldmc", value = "受理点名称", required = false) @RequestParam(value = "sldmc", required = false) String sldmc,
//            @ApiParam(name = "deptId", value = "组织机构代码", required = false) @RequestParam(value = "deptId", required = false) String deptId) {
//        R r;
//        log.info("{},重开发票入参 --sld:{},sldmc:{},", LOGGER_MSG, sld, sldmc);
//        List<CommonOrderInfo> orderList = new ArrayList<>();
//        try {
//
//            if (StringUtils.isBlank(orderIdArray)) {
//                return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
//            }
//
//            List<Map> idList = JSON.parseArray(orderIdArray, Map.class);
//
//            /**
//             *
//             *    1.此处复制三张表，分别为批次表，发票订单表，处理表
//             */
//            orderList = orderService.makeOrinfoByOrderArray(idList, sld, sldmc);
//            /**
//             *     发票开具
//             */
//            r = makeOutAnInvoiceService.makeOutAnInovice(orderList, sld, sldmc);
//            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
//                if (orderList != null && orderList.size() > 0) {
//                    for (int i = 0; i < orderList.size(); i++) {
//                        List<String> shList = new ArrayList<>();
//                        shList.add(orderList.get(i).getOrderInfo().getXhfNsrsbh());
//                        OrderProcessInfo o = new OrderProcessInfo();
//                        o.setId(orderList.get(i).getOrderInfo().getProcessId());
//                        o.setDdzt(ConfigureConstant.STRING_6);
//                        o.setSbyy(r.get("msg").toString());
//                        apiOrderProcessService.updateOrderProcessInfoByProcessId(o, shList);
//                    }
//                }
//            }
//            return r;
//        } catch (Exception e) {
//            if (orderList != null && orderList.size() > 0) {
//                for (int i = 0; i < orderList.size(); i++) {
//                    List<String> shList = new ArrayList<>();
//                    shList.add(orderList.get(i).getOrderInfo().getXhfNsrsbh());
//                    OrderProcessInfo o = new OrderProcessInfo();
//                    o.setId(orderList.get(i).getOrderInfo().getProcessId());
//                    o.setDdzt(ConfigureConstant.STRING_6);
//                    o.setSbyy("系统异常,请联系管理员");
//                    apiOrderProcessService.updateOrderProcessInfoByProcessId(o, shList);
//                }
//            }
//            log.error("{}发票重新开具接口，异常e:{}", LOGGER_MSG, e);
//            return R.error().put(OrderManagementConstant.MESSAGE, "未知异常，请联系管理员！");
//        }
//    }
//
//    @ApiOperation(value = "查询纸票列表", notes = "订单发票管理-查询纸票列表")
//    @PostMapping("/queryPaperInvoiceList")
//    @SysLog(operation = "发票信息列表查询", operationDesc = "发票信息列表查询", key = "订单发票管理")
//    public R queryPaperInvoiceList(HttpServletRequest request,
//                                   @ApiParam(name = "gmfMc", value = "购买方名称", required = false) @RequestParam("gmfMc") String gmfMc,
//                                   @ApiParam(name = "fpzlDm", value = "发票种类代码", required = false) @RequestParam("fpzlDm") String fpzlDm,
//                                   @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam("startTime") String startTime,
//                                   @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam("endTime") String endTime,
//                                   @ApiParam(name = "deptId", value = "组织机构id", required = true) @RequestParam("deptId") String deptId,
//                                   @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = true) @RequestParam("xhfNsrsbh") String xhfNsrsbh,
//                                   @ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam("pageSize") String pageSize,
//                                   @ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam("currPage") String currPage) {
//        log.debug("{},发票查询列表入参 --ghfMc:{},fpzlDm:{},startTime:{},endTime:{},", LOGGER_MSG, gmfMc, fpzlDm, startTime, endTime);
//        Map paramMap = new HashMap<>(10);
//        if (StringUtils.isBlank(xhfNsrsbh)) {
//            log.error("{},请求税号为空!", LOGGER_MSG);
//            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
//        }
//
//        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
//        try {
//
//
//            if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
//                return R.error(OrderInfoContentEnum.PARAM_NULL.getKey(), OrderInfoContentEnum.PARAM_NULL.getMessage());
//            } else {
//                Date starttime = new SimpleDateFormat(ConfigureConstant.DATE_FORMAT_DATE).parse(startTime);
//                Date endtime = new SimpleDateFormat(ConfigureConstant.DATE_FORMAT_DATE).parse(endTime);
//                if (starttime.after(endtime)) {
//                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
//                    return R.error(OrderInfoContentEnum.ORDER_TIME_ERROR.getKey(), OrderInfoContentEnum.ORDER_TIME_ERROR.getMessage());
//                }
//                paramMap.put("startTime", startTime);
//                paramMap.put("endTime", endTime);
//
//            }
//        } catch (ParseException e) {
//            log.error("{}时间转换异常", LOGGER_MSG);
//            return R.error(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
//        }
//        if (StringUtils.isNotBlank(gmfMc)) {
//            paramMap.put("ghfMc", gmfMc);
//        }
//        if (StringUtils.isNotBlank(fpzlDm)) {
//            List<String> fpzlList = JsonUtils.getInstance().parseObject(fpzlDm, List.class);
//            paramMap.put("fpzlList", fpzlList);
//        }
//        if (StringUtils.isNotBlank(pageSize)) {
//            paramMap.put("pageSize", Integer.parseInt(pageSize));
//        }
//        if (StringUtils.isNotBlank(currPage)) {
//            paramMap.put("currPage", Integer.parseInt(currPage));
//        }
//        PageUtils page = apiOrderInvoiceInfoService.selectInvoiceByOrder(paramMap, shList);
//        return R.ok().put("data", page);
//    }
    
    
    @ApiOperation(value = "发票信息明细查询", notes = "订单发票管理-查询发票明细列表")
    @SysLog(operation = "发票信息明细查询", operationDesc = "发票信息明细查询", key = "订单发票管理")
    @PostMapping("/queryInvoiceDetail")
    public R queryInvoiceDetail(@ApiParam(name = "orderId", value = "订单主键id", required = true) @RequestParam(value = "orderId", required = true) String orderId
            , @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        //查询订单详情
        OrderInvoiceInfo queryOrderInvoiceInfo = apiOrderInvoiceInfoService.selectInvoiceListByOrderId(orderId, shList);
    
        return R.ok().put(OrderManagementConstant.DATA, queryOrderInvoiceInfo);
    }
}
