package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 发票对外返回协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class COMMON_ORDER_RSP extends RESPONSE implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
}
