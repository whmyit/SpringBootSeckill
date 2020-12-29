package com.dxhy.order.consumer.modules.supplychain.controller;

import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.supplychain.service.SupplyChainService;
import com.dxhy.order.consumer.modules.supplychain.constant.SupplyChainCommonEnum;
import com.dxhy.order.consumer.modules.supplychain.constant.SupplyChainErrorMsgEnum;
import com.dxhy.order.consumer.modules.supplychain.model.SupplyChainBaseResponseBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynCheckResultRequestBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynOrderRequestBean;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应链控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:42
 */
@Api(value = "供应链订单接收", tags = {"订单供应链模块"})
@RestController
@RequestMapping("/supplyChain")
@Slf4j
public class SupplyChainController {

    private static final String LOGGER_MSG = "(订单供应链模块)";


    @Resource
    SupplyChainService supplyChainService;

    /**
     * 提供给进项将订单信息推送给销项
     * @param content
     * @return
     */
    @PostMapping("/syncOrder")
    @ApiOperation(value = "订单同步-供应链", notes = "订单同步-供应链")
    @SysLog(operation = "订单同步-供应链", operationDesc = "订单同步-供应链", key = "供应链模块")
    public SupplyChainBaseResponseBean syncOrder(@RequestBody String content) {

        SupplyChainBaseResponseBean response = new SupplyChainBaseResponseBean();
        log.debug("{}供应链订单接收，入参:{}",LOGGER_MSG,content);
        //数据校验
        try {
            List<SynOrderRequestBean> synOrderRequestBeans = JSONObject.parseArray(content, SynOrderRequestBean.class);
            for(SynOrderRequestBean request : synOrderRequestBeans){
                Map<String,String> resultMap = checkRequest(request);
                if(!OrderInfoContentEnum.SUCCESS.getKey().equals(resultMap.get(OrderManagementConstant.CODE))){
                    response.setReturnCode(resultMap.get(OrderManagementConstant.CODE));
                    response.setReturnMessage(resultMap.get(OrderManagementConstant.MESSAGE));
                    return response;
                }
            }


            response = supplyChainService.syncOrder(synOrderRequestBeans);
            return response;
        } catch (Exception e) {
            log.error("{}同步订单信息异常,异常信息:{}",LOGGER_MSG,e);
            response.setReturnCode(SupplyChainErrorMsgEnum.UNKONW_ERROR.getKey());
            response.setReturnMessage(SupplyChainErrorMsgEnum.UNKONW_ERROR.getValue());
            return response;
        }

    }


    private Map checkRequest(SynOrderRequestBean request) {

        Map<String, String> resultMap = new HashMap(2);
        resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
        resultMap.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());


        if(StringUtils.isBlank(request.getPoNo())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号不能为空!");
            return resultMap;

        }

        if(StringUtils.isBlank(request.getAmount())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo()+ ",订单金额不能为空!");
            return resultMap;
        }
        if(StringUtils.isBlank(request.getGfName())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "购方名称不能为空!");
            return resultMap;

        }
        if(StringUtils.isBlank(request.getGfTaxNo())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "购方税号不能为空!");
            return resultMap;

        }

        if(StringUtils.isBlank(request.getXfName())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "销方名称不能为空!");
            return resultMap;

        }
        if(StringUtils.isBlank(request.getXfTaxNo())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "销方税号不能为空!");
            return resultMap;

        }
        return resultMap;
    }



    /**
     * 提供给进项将发票的审核结果通知到销项
     * @param content
     * @return
     */
    @ApiOperation(value = "审核结果同步-供应链", notes = "审核结果同步-供应链")
    @PostMapping("/syncChecResult")
    @SysLog(operation = "审核结果同步-供应链", operationDesc = "审核结果同步-供应链", key = "供应链模块")
    public SupplyChainBaseResponseBean syncChecResult(@RequestBody String content) {

        log.debug("{}审核结果接收，入参:{}",LOGGER_MSG,content);
        SupplyChainBaseResponseBean response = new SupplyChainBaseResponseBean();

        try {

            List<SynCheckResultRequestBean> synOrderRequestBeanList = JSONObject.parseArray(content, SynCheckResultRequestBean.class);

            for(SynCheckResultRequestBean request : synOrderRequestBeanList){
                Map<String,String> resultMap = checkSynCheckReultRequest(request);

                if(!OrderInfoContentEnum.SUCCESS.getKey().equals(resultMap.get(OrderManagementConstant.CODE))){
                    response.setReturnCode(resultMap.get(OrderManagementConstant.CODE));
                    response.setReturnMessage(resultMap.get(OrderManagementConstant.MESSAGE));
                    return response;
                }
            }

            response = supplyChainService.syncChecResult(synOrderRequestBeanList);
            log.debug("{}审核结果接收，出参:{}",LOGGER_MSG,JsonUtils.getInstance().toJsonString(response));
            return response;
        } catch (Exception e) {
            log.error("{}同步审核结果异常,异常信息:{}",LOGGER_MSG,e);
            response.setReturnCode(SupplyChainErrorMsgEnum.UNKONW_ERROR.getKey());
            response.setReturnMessage(SupplyChainErrorMsgEnum.UNKONW_ERROR.getValue());
            return response;
        }

    }

    private Map checkSynCheckReultRequest(SynCheckResultRequestBean request) {
    
        Map<String, String> resultMap = new HashMap(2);
        resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
        resultMap.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());

        if(StringUtils.isBlank(request.getPoNo())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号不能为空!");
            return resultMap;

        }
        if(StringUtils.isBlank(request.getXfTaxno())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "销方税号不能为空!");
            return resultMap;
        }
        if(StringUtils.isBlank(request.getBatchNo())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "流水号不能为空!");
            return resultMap;

        }
        if(StringUtils.isBlank(request.getStatus())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "审核结果不能为空!");
            return resultMap;

        }
        if(!SupplyChainCommonEnum.CHECK_STATUS_0.getKey().equals(request.getStatus()) && !SupplyChainCommonEnum.CHECK_STATUS_1.getKey().equals(request.getStatus())){
            resultMap.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE, "订单号为:" + request.getPoNo() + "审核结果只能为0 或者 1!");
            return resultMap;

        }
        return resultMap;
    }


    /**
     * 将补全的订单信息推送给进项
     * @param fpqqlsh
     * @param xhfNsrsbh
     * @return
     */
    @ApiOperation(value = "推送补全的订单信息到进项-供应链", notes = "推送补全的订单信息到进项-供应链")
    @PostMapping("/pushCompleteOrder")
    @SysLog(operation = "推送补全的订单信息到进项-供应链", operationDesc = "推送补全的订单信息到进项-供应链", key = "供应链模块")
    public R pushCompleteOrder(
            @ApiParam(name = "发票请求流水号", value = "fpqqlsh", required = true) @RequestParam(value = "fpqqlsh", required = true) String fpqqlsh,
            @ApiParam(name = "销货方纳税人识别号", value = "xhfNsrsbh", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        SupplyChainBaseResponseBean response = new SupplyChainBaseResponseBean();

        try {
            log.debug("{}推送补全的订单信息到进项，入参,fpqqlsh:{},xhfNsrsbh:{}",LOGGER_MSG,fpqqlsh,xhfNsrsbh);
            Map<String, String> paramMap = new HashMap<>(2);
            paramMap.put("fpqqlsh",fpqqlsh);
            paramMap.put("xhfNsrsbh",xhfNsrsbh);
            R r = supplyChainService.pushCompleteOrder(paramMap);
            log.debug("{}推送补全的订单信息到进项,出参：{}",JsonUtils.getInstance().toJsonString(r));
            return r;

        } catch (Exception e) {
            log.error("{}推送补全的订单到进项异常,异常信息:{}",LOGGER_MSG,e);
            return R.error().put(OrderManagementConstant.CODE,SupplyChainErrorMsgEnum.UNKONW_ERROR.getKey())
                    .put(OrderManagementConstant.MESSAGE,SupplyChainErrorMsgEnum.UNKONW_ERROR.getValue());
        }

    }




}
