package com.dxhy.order.consumer.openapi.service;


import com.dxhy.order.protocol.v4.order.DDPCXX_REQ;
import com.dxhy.order.protocol.v4.order.DDPCXX_RSP;

/**
 * @Description: 订单对外接口业务层接口V3-发票开具接口
 * @author: chengyafu
 * @date: 2018年8月13日 下午4:48:28
 */
public interface IAllocateInvoiceInterfaceServiceV3 {
    
    /**
     * 开具发票V3版本
     *
     * @param ddpcxxReq
     * @param secretId
     * @param kpjh
     * @return
     */
    DDPCXX_RSP allocateInvoicesV3(DDPCXX_REQ ddpcxxReq, String secretId, String kpjh,String protocol_type);
    
    
}
