package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发票全数据返回协议beanV3
 *
 * @author zsc
 * @date 2018年9月19日 15:14:50
 */
@ToString
@Setter
@Getter
public class DDFPZXX implements Serializable {
    
    /**
     * 订单发票信息
     */
    private DDFPXX DDFPXX;
    
    /**
     * 订单扩展信息
     */
    private List<DDKZXX> DDKZXX;
    
    /**
     * 发票明细信息
     */
    private List<DDMXXX> DDMXXX;
    
}
