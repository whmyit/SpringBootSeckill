package com.dxhy.order.protocol.order;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单拆分合并关系协议beanV3
 *
 * @author ZSC-DXHY
 */
@ToString
@Setter
@Getter
public class ORDER_EXTENSION_INFO implements Serializable {
    
    /**
     * 订单请求流水号
     */
    private String DDQQLSH;
    
    /**
     * 订单号
     */
    private String DDH;
    
    /**
     * 订单类型（0:原始订单,1:拆分后订单,2:合并后订单,3:系统冲红订单,4:自动开票订单,5:作废重开订单）
     */
    private String DDLX;
    
    /**
     * 备用字段
     */
    private String BYZD1;
    private String BYZD2;
    private String BYZD3;
    
}
