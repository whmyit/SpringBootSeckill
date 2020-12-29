package com.dxhy.order.consumer.modules.invoice.controller;


import com.dxhy.order.api.ApiInvoiceSummaryStatisticsService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.handle.SummaryThread;
import com.dxhy.order.model.R;
import com.dxhy.order.model.vo.QsRequestVo;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * @Description: 发票汇总统计接口 (目前只提供给全税使用)
 * @Author:xueanna
 * @Date: 2019/5/29
 */
@RestController
@RequestMapping(value = "/invoiceSummary")
@Api(value = "发票汇总", tags = {"发票模块"})
@Slf4j
public class InvoiceSummaryStatisticsController {
    
    private static final String INVOICE_SUMMARY = "（发票汇总接口）";
    @Reference
    private ApiInvoiceSummaryStatisticsService apiInvoiceSummaryStatisticsService;
    
    @ApiOperation(value = "发票汇总通知", notes = "发票汇总-发票汇总数据接口")
    @PostMapping(path = "/invoiceSummaryNotice")
    @SysLog(operation = "发票汇总通知rest接口", operationDesc = "汇总发票通知接口", key = "发票汇总")
    public R invoiceSummaryNotice(@ApiParam(name = "jsonparam", value = "请求参数", required = true) @RequestBody String jsonparam) {
        log.info("{}收到发票汇总通知请求,数据{}", INVOICE_SUMMARY, jsonparam);
        try {
            QsRequestVo vo = JsonUtils.getInstance().fromJson(jsonparam, QsRequestVo.class);
            //参数校验
            if (!OrderInfoEnum.INVOICE_TAXRATE_SUMMARY.getKey().equals(vo.getInformType()) && !OrderInfoEnum.INVOICE_ITEM_SUMMARY.getKey().equals(vo.getInformType())) {
                R d = new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                        .put(OrderManagementConstant.ALL_MESSAGE, "请检查参数是否正确");
                return d;
            }
            SummaryThread thread = new SummaryThread();
            thread.fun(vo, apiInvoiceSummaryStatisticsService);
        } catch (Exception e) {
            log.error("{}异常:{}", INVOICE_SUMMARY, e);
            return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                    .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
        }
        return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey())
                .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getMessage());
    }
    
    @ApiOperation(value = "查询发票汇总状态接口", notes = "发票汇总-查询发票汇总状态接口")
    @PostMapping(path = "/queryInvoiceSummaryStatus")
    @SysLog(operation = "发票汇总状态rest接口", operationDesc = "汇总发票状态查看接口", key = "发票汇总")
    public R queryInvoiceSummaryStatus(@ApiParam(name = "jsonparam", value = "请求参数", required = true) @RequestBody String jsonparam) {
        log.info("{}收到查询发票汇总状态请求,数据{}", INVOICE_SUMMARY, jsonparam);
        QsRequestVo vo;
        List<Map> list;
        try {
            vo = JsonUtils.getInstance().fromJson(jsonparam, QsRequestVo.class);
            //参数校验
            if (!OrderInfoEnum.INVOICE_TAXRATE_SUMMARY.getKey().equals(vo.getInformType()) && !OrderInfoEnum.INVOICE_ITEM_SUMMARY.getKey().equals(vo.getInformType())) {
                return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                        .put(OrderManagementConstant.ALL_MESSAGE, "请检查参数是否正确");
            } else {
                list = apiInvoiceSummaryStatisticsService.getSummaryState(vo);
            }
        } catch (Exception e) {
            log.error("{}异常:{}", INVOICE_SUMMARY, e);
            return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                    .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
        }
        return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey())
                .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getMessage())
                .put("informType", vo.getInformType()).put("param", list);
    }
    
    @ApiOperation(value = "查询发票汇总数据接口", notes = "发票汇总-查询发票汇总数据接口")
    @PostMapping(path = "/queryInvoiceSummaryData")
    @SysLog(operation = "发票汇总数据rest接口", operationDesc = "汇总发票获取数据接口", key = "发票汇总")
    public R queryInvoiceSummaryData(@ApiParam(name = "jsonparam", value = "请求参数", required = true) @RequestBody String jsonparam) {
        log.info("{}收到查询发票汇总数据请求,数据{}", INVOICE_SUMMARY, jsonparam);
        QsRequestVo vo;
        List<Map> list;
        try {
            vo = JsonUtils.getInstance().fromJson(jsonparam, QsRequestVo.class);
            //参数校验
            if (!OrderInfoEnum.INVOICE_TAXRATE_SUMMARY.getKey().equals(vo.getInformType()) && !OrderInfoEnum.INVOICE_ITEM_SUMMARY.getKey().equals(vo.getInformType())) {
                return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                        .put(OrderManagementConstant.ALL_MESSAGE, "请检查参数是否正确");
            } else {
                list = apiInvoiceSummaryStatisticsService.getSummaryData(vo);
            }
        } catch (Exception e) {
            log.error("{}异常:{}", INVOICE_SUMMARY, e);
            return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey())
                    .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
        }
        return new R(true).put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey())
                .put(OrderManagementConstant.ALL_MESSAGE, OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getMessage())
                .put("informType", vo.getInformType()).put("data", list);
    }
}
