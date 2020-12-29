package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 发票实体类
 * todo V1和V2使用,后期不改更新维护
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@Deprecated
public class COMMON_INVOICE implements Serializable {
    
    private static final long serialVersionUID = -2069880055078999694L;
    /**
     * 发票头信息
     */
    private COMMON_INVOICE_HEAD COMMON_INVOICE_HEAD;
    /**
     * 项目信息
     */
    private COMMON_INVOICE_DETAIL[] COMMON_INVOICE_DETAIL;
    /**
     * 订单信息
     */
    private COMMON_INVOICE_ORDER COMMON_INVOICE_ORDER;
    
}
