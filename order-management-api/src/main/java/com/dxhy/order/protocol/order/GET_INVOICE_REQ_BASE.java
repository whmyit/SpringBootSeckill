package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 底层发票开具结果获取请求协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class GET_INVOICE_REQ_BASE implements Serializable {
    
    /**
     * 发票类型
     */
    private String FPLX;
    
    /**
     * 发票请求流水号
     */
    private String FPQQPCH;
    
}
