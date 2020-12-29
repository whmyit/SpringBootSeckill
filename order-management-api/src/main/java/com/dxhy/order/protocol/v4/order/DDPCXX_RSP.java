package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.protocol.v4.RESPONSEV4;
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
public class DDPCXX_RSP extends RESPONSEV4 implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
}
