package com.dxhy.order.consumer.openapi.service;


import com.dxhy.order.consumer.protocol.cpy.*;
import com.dxhy.order.consumer.protocol.sld.*;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.hp.HpInvocieRequest;
import com.dxhy.order.model.a9.kp.AllocateInvoicesReq;
import com.dxhy.order.model.a9.query.GetAllocateInvoicesStatusRsp;
import com.dxhy.order.model.a9.query.GetAllocatedInvoicesRsp;
import com.dxhy.order.protocol.RESPONSE;
import com.dxhy.order.protocol.order.ORDER_REQUEST;
import com.dxhy.order.protocol.order.ORDER_RESPONSE;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;

import java.util.List;

/**
 * @Description: 订单对外接口业务层接口
 * @author: chengyafu
 * @date: 2018年8月13日 下午4:48:28
 */
public interface IInterfaceService {
    
    /**
     * 开具发票
     *
     * @param company
     * @return
     */
    R allocateInvoices(AllocateInvoicesReq company);
    
    /**
     * 发票状态查询
     *
     * @param jsonString
     * @return
     */
    GetAllocateInvoicesStatusRsp invoiceStatus(String jsonString);
    
    /**
     * 发票结果获取
     *
     * @param jsonString
     * @return
     */
    GetAllocatedInvoicesRsp getAllocatedInvoices(String jsonString);
    
    /**
     * 已开发票作废
     * invoiceInvalid
     *
     * @param zfxxReq
     * @return
     */
    ZFXX_RSP invoiceInvalid(ZFXX_REQ zfxxReq);
    
    /**
     * 企业数据导入
     * importOrders
     *
     * @param commonInvoices
     * @return
     */
    @SuppressWarnings("AliDeprecation")
    RESPONSE importOrders(List<com.dxhy.order.consumer.protocol.order.COMMON_INVOICE> commonInvoices);
    
    /**
     * 受理点列表查询接口
     *
     * @param sldInvoiceRollploRequest
     * @return
     */
    SLDKCMX_RESPONSE queryinvoicerollplolist(SLD_INVOICEROLLPLO_REQUEST sldInvoiceRollploRequest);

    /**
     * 受理点上票接口
     *
     * @param slduprequest
     * @return
     */
    RESPONSE accessPointUpInvoice(SLDUP_REQUEST slduprequest);
    
    /**
     * 受理点下票接口
     *
     * @param slddownRequest
     * @return
     */
    RESPONSE accessPointDownInvoice(SLDDOWN_REQUEST slddownRequest);

    /**
     * 受理点列表接口
     *
     * @param sldSearchRequest
     * @return
     */
    RESPONSE querySld(SLD_SEARCH_REQUEST sldSearchRequest);
    
    /**
     * 根据订单号获取订单数据以及发票数据接口
     *
     * @param orderRequest
     * @return
     */
    ORDER_RESPONSE getOrderInfoAndInvoiceInfo(ORDER_REQUEST orderRequest);
    
    /**
     * 专票冲红申请单
     *
     * @param hzfpsqbscsReq
     * @return
     */
    R specialInvoiceRushRed(String hzfpsqbscsReq);
    
    /**
     * 红字发票申请下载
     *
     * @param hpInvocieRequest
     * @return
     */
    R downSpecialInvoice(HpInvocieRequest hpInvocieRequest);
    
    /**
     * 成品油库存局端可下载库存查询
     *
     * @param cpyJdkcRequest
     * @return
     */
    CPY_JDKC_RESPONSE queryCpyJdKc(CPY_JDKC_REQUEST cpyJdkcRequest);
    
    /**
     * 成品油已下载库存查询
     *
     * @param cpyYxzkcRequest
     * @return
     */
    CPY_YXZKC_RESPONSE queryCpyYxzKc(CPY_YXZKC_REQUEST cpyYxzkcRequest);
    
    /**
     * 成品油库存下载
     *
     * @param downloadCpykcRequest
     * @return
     */
    DOWNLOAD_CPYKC_RESPONSE downloadCpyKc(DOWNLOAD_CPYKC_REQUEST downloadCpykcRequest);
    
    /**
     * 成品油库存退回
     *
     * @param backCpykcRequest
     * @return
     */
    BACK_CPYKC_RESPONSE backCpyKc(BACK_CPYKC_REQUEST backCpykcRequest);
    
    /**
     * 成品油库存同步
     *
     * @param syncCpykcRequest
     * @return
     */
    SYNC_CPYKC_RESPONSE syncCpyKc(SYNC_CPYKC_REQUEST syncCpykcRequest);
}
