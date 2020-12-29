package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 发票开具结果获取请求协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class GET_INVOICE_REQ implements Serializable {
    
    /**
     * 发票类型
     */
    private String FPLX;
    
    /**
     * 是否返回失败数据
     */
    private String RETURNFAIL;
    
    /**
     * 销方纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    
}
