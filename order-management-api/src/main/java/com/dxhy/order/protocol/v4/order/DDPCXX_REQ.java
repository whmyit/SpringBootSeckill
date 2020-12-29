package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 发票对外请求协议beanV4版本,统一对外输出
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class DDPCXX_REQ implements Serializable {
    
    /**
     * 订单批次信息
     */
    private DDPCXX DDPCXX;
    
    /**
     * 多订单信息
     */
    private List<DDZXX> DDZXX;
    
}
