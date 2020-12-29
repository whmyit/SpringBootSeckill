package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 发票对外请求协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class COMMON_ORDER_REQ implements Serializable {
    
    /**
     * 订单批次信息
     */
    private COMMON_ORDER_BATCH COMMON_ORDER_BATCH;
    
    /**
     * 多订单信息
     */
    private List<COMMON_ORDER> COMMON_ORDERS;
    
}
