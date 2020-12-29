package com.dxhy.order.consumer.modules.supplychain.service.impl;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.api.ApiOrderQrcodeExtendService;
import com.dxhy.order.api.ApiPushService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.supplychain.service.SupplyChainService;
import com.dxhy.order.consumer.modules.supplychain.constant.SupplyChainCommonEnum;
import com.dxhy.order.consumer.modules.supplychain.constant.SupplyChainErrorMsgEnum;
import com.dxhy.order.consumer.modules.supplychain.model.SupplyChainBaseResponseBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynCheckResultRequestBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynOrderRequestBean;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 供应链业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 19:40
 */
@Slf4j
@Service
public class SupplyChainServiceImpl implements SupplyChainService{


    private static final String  LOGGER_MSG = "(供应链业务层)";


    @Reference
    ApiInvoiceCommonService apiInvoiceCommonService;

    @Reference
    ApiOrderProcessService apiOrderProcessService;

    @Reference
    ApiOrderQrcodeExtendService apiOrderQrcodeExtendService;

    @Reference
    ApiPushService apiPushService;

    @Resource
    UserInfoService userInfoService;



    @Override
    public SupplyChainBaseResponseBean syncOrder(List<SynOrderRequestBean> requestList) {

        List<OrderInfo> orderList = new ArrayList<>();
        List<OrderProcessInfo> processInfoList = new ArrayList<>();
        List<OrderOriginExtendInfo> originList = new ArrayList<>();



        List<Map<String,String>> resultList = new ArrayList<>();
        for(SynOrderRequestBean request : requestList){

            //TODO 判断订单信息是否存在 ? 根据流水号 还是根据订单号
            //判断订单号是否存在
            Map<String, Object> paramMap = new HashMap<>(2);
            paramMap.put("ddh",request.getPoNo());
            paramMap.put("orderStatus",OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());

            List<String> shList = new ArrayList<>();
            shList.add(requestList.get(0).getXfTaxNo());

            boolean existNoAuditOrder = apiOrderQrcodeExtendService.isExistNoAuditOrder(paramMap, shList);
            if(existNoAuditOrder){
                log.error("{}此税号：{}下订单已存在订单号:{}",LOGGER_MSG,request.getXfTaxNo(),request.getPoNo());
                return buildResponse(SupplyChainErrorMsgEnum.DDH_REPEAT.getKey(),String.format(SupplyChainErrorMsgEnum.DDH_REPEAT.getValue(),request.getPoNo()));
            }

            //组装订单信息
            OrderInfo orderInfo = synOrderToOrder(request);
            orderInfo.setGhfQylx(OrderInfoEnum.GHF_QYLX_01.getKey());

            //补全购方信息
            if (StringUtils.isBlank(orderInfo.getXhfDz()) || StringUtils.isBlank(orderInfo.getXhfDh()) || StringUtils.isBlank(orderInfo.getXhfYh()) ||
                    StringUtils.isBlank(orderInfo.getXhfZh())) {
                DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(orderInfo.getXhfNsrsbh(), orderInfo.getXhfMc());
                if (sysDeptEntity == null) {
                    log.error("{}订单信息校验失败，名称:{},税号:{},销方信息不全!",LOGGER_MSG, orderInfo.getXhfMc(),orderInfo.getXhfNsrsbh());
                    return buildResponse(SupplyChainErrorMsgEnum.DDH_REPEAT.getKey(),String.format(SupplyChainErrorMsgEnum.XHF_NSRSBH_NOTEXIST.getValue(),request.getPoNo()));

                } else {
                    orderInfo.setXhfDz(sysDeptEntity.getTaxpayerAddress());
                    orderInfo.setXhfDh(sysDeptEntity.getTaxpayerPhone());
                    orderInfo.setXhfYh(sysDeptEntity.getTaxpayerBank());
                    orderInfo.setXhfZh(sysDeptEntity.getTaxpayerAccount());
                }
            }

            //组装订单处理表信息
            OrderProcessInfo processInfo = convertProcessInfo(orderInfo);
            orderInfo.setProcessId(processInfo.getId());

            //组装原始订单信息
            OrderOriginExtendInfo originOrder = buildOrderOriginOrderInfo(orderInfo);

            orderList.add(orderInfo);

            processInfoList.add(processInfo);

            originList.add(originOrder);
            Map<String, String> data = new HashMap<>(2);
            data.put("fpqqlsh",processInfo.getFpqqlsh());
            data.put("ddh",processInfo.getDdh());
            resultList.add(data);
        }

        //保存订单信息
        apiInvoiceCommonService.saveDifShData(orderList,null,processInfoList,originList);

        SupplyChainBaseResponseBean supplyChainBaseResponseBean = buildResponse(SupplyChainErrorMsgEnum.SUCCESS);
        supplyChainBaseResponseBean.setData(resultList);

        return supplyChainBaseResponseBean;
    }

    private OrderOriginExtendInfo buildOrderOriginOrderInfo(OrderInfo orderInfo) {

        OrderOriginExtendInfo orderOrginOrder = new OrderOriginExtendInfo();
        orderOrginOrder.setCreateTime(orderInfo.getCreateTime());
        orderOrginOrder.setUpdateTime(orderInfo.getUpdateTime());
        orderOrginOrder.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderOrginOrder.setOrderId(orderInfo.getId());
        orderOrginOrder.setFpqqlsh(orderInfo.getFpqqlsh());
        orderOrginOrder.setOriginFpqqlsh(orderInfo.getFpqqlsh());
        orderOrginOrder.setOriginOrderId(orderInfo.getId());
        orderOrginOrder.setOriginDdh(orderInfo.getDdh());
        orderOrginOrder.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        return orderOrginOrder;
    }

    private SupplyChainBaseResponseBean buildResponse(SupplyChainErrorMsgEnum ddh_repeat) {

        SupplyChainBaseResponseBean response = new SupplyChainBaseResponseBean();
        response.setReturnCode(ddh_repeat.getKey());
        response.setReturnMessage(ddh_repeat.getValue());
        return response;
    }

    private SupplyChainBaseResponseBean buildResponse(String key,String value) {

        SupplyChainBaseResponseBean response = new SupplyChainBaseResponseBean();
        response.setReturnCode(key);
        response.setReturnMessage(value);
        return response;
    }

    /**
     * 组装扩张表信息
     * @param orderInfo
     * @return
     */
    private OrderProcessInfo convertProcessInfo(OrderInfo orderInfo) {

        OrderProcessInfo processInfo = new OrderProcessInfo();
        processInfo.setCreateTime(orderInfo.getCreateTime());
        processInfo.setUpdateTime(orderInfo.getUpdateTime());
        processInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        processInfo.setDdh(orderInfo.getDdh());
        processInfo.setKphjje(orderInfo.getKphjje());
        processInfo.setFpzlDm(orderInfo.getFpzlDm());
        processInfo.setDdcjsj(processInfo.getCreateTime());
        processInfo.setDdlx(orderInfo.getDdlx());
        processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
        processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_8.getKey());
        processInfo.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());

        processInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        processInfo.setYwlxId(orderInfo.getYwlxId());
        processInfo.setGhfMc(orderInfo.getGhfMc());
        processInfo.setYwlx(orderInfo.getYwlx());
        processInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        processInfo.setHjbhsje(orderInfo.getHjbhsje());
        processInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        processInfo.setKpse(orderInfo.getHjse());
        processInfo.setKpxm(orderInfo.getKpxm());
        processInfo.setTqm(orderInfo.getTqm());
        processInfo.setXhfMc(orderInfo.getXhfMc());
        processInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        processInfo.setOrderInfoId(orderInfo.getId());
        processInfo.setCheckStatus(SupplyChainCommonEnum.CHECK_STATUS_0.getKey());
        return processInfo;
    }

    /**
     * bean转换
     * @param request
     * @return
     */
    private OrderInfo synOrderToOrder(SynOrderRequestBean request) {
        OrderInfo orderInfo = new OrderInfo();

        Date now = new Date();
        orderInfo.setKphjje(request.getAmount());
        orderInfo.setGhfMc(request.getGfName());
        orderInfo.setGhfNsrsbh(request.getGfTaxNo());
        orderInfo.setDdh(request.getPoNo());
        orderInfo.setXhfMc(request.getXfName());
        orderInfo.setXhfNsrsbh(request.getXfTaxNo());
        orderInfo.setNsrsbh(request.getXfTaxNo());
        orderInfo.setNsrmc(request.getXfName());
        //补全不存在的数据
        orderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
        orderInfo.setFpqqlsh(apiInvoiceCommonService.getGenerateShotKey());
        orderInfo.setDdrq(now);
        orderInfo.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey());
        orderInfo.setUpdateTime(now);
        orderInfo.setCreateTime(now);
        orderInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_0.getKey());
        orderInfo.setBbmBbh(SystemConfig.bmbbbh);
        orderInfo.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey());
        orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());

        return orderInfo;

    }

    @Override
    public SupplyChainBaseResponseBean syncChecResult(List<SynCheckResultRequestBean> requestList) {


        List<OrderProcessInfo> updateList = new ArrayList<>();
        for(SynCheckResultRequestBean request : requestList){
            List<String> shList = new ArrayList<String>();
            shList.add(request.getXfTaxno());

            //查询数据是否存在
            OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(request.getBatchNo(), shList);
            if(orderProcessInfo == null){
                log.error("{} 请求流水号:{} 的数据不存在!",request.getBatchNo());
                return buildResponse(SupplyChainErrorMsgEnum.ORDER_INFO_NOT_EXIST.getKey(),String.format(SupplyChainErrorMsgEnum.ORDER_INFO_NOT_EXIST.getValue(),request.getBatchNo()));
            }

            //更新订单审核状态
            OrderProcessInfo updateProcessInfo = new OrderProcessInfo();
            updateProcessInfo.setId(orderProcessInfo.getId());
            updateProcessInfo.setCheckTime(new Date());
            updateProcessInfo.setXhfNsrsbh(request.getXfTaxno());
            if(SupplyChainCommonEnum.REQUEST_CHECK_STATUS_0.getKey().equals(request.getStatus())){
                //同意
                updateProcessInfo.setCheckStatus(SupplyChainCommonEnum.CHECK_STATUS_2.getKey());
            }else if(SupplyChainCommonEnum.REQUEST_CHECK_STATUS_1.getKey().equals(request.getStatus())){
                //不同意
                updateProcessInfo.setCheckStatus(SupplyChainCommonEnum.CHECK_STATUS_3.getKey());
                updateProcessInfo.setSbyy(request.getMessage());
            }
            updateList.add(updateProcessInfo);

        }

        int i = apiOrderProcessService.updateListOrderProcessInfoByProcessId(updateList);
        if(i < updateList.size()){
            return buildResponse(SupplyChainErrorMsgEnum.UPDATE_CHECK_STATUS_FALILD);
        }
        return buildResponse(SupplyChainErrorMsgEnum.SUCCESS);
    }


    /**
     *
     * @param paramMap
     * @return
     */
    @Override
    public R pushCompleteOrder(Map<String,String> paramMap) {


        SupplyChainBaseResponseBean responseBean = new SupplyChainBaseResponseBean();

        String fpqqlsh = paramMap.get("fpqqlsh");
        String xhfNsrsbh = paramMap.get("xhfNsrsbh");
        //发票推送队列添加推送 补全的订单信息
        InvoicePush pushInfo = new InvoicePush();
        pushInfo.setNSRSBH(xhfNsrsbh);
        pushInfo.setFPQQLSH(fpqqlsh);
        R r = apiPushService.pushCompleteOrder(pushInfo, OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_0.getKey());

        return r;

       /* if(!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))){
            responseBean.setReturnCode(String.valueOf(r.get(OrderManagementConstant.CODE)));
            responseBean.setReturnMessage(String.valueOf(r.get(OrderManagementConstant.MESSAGE)));
            return responseBean;
        }else{
            responseBean.setReturnCode(OrderInfoContentEnum.SUCCESS.getKey());
            responseBean.setReturnMessage(OrderInfoContentEnum.SUCCESS.getMessage());
            return responseBean;
        }*/
    }
}
