package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 订单全数据 协议bean
 *
 * @author zsc
 * @date 2018年9月19日 15:14:50
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@ToString
@Setter
@Getter
public class DDZXX implements Serializable {
    
    /**
     * 订单主体信息
     */
    private DDTXX DDTXX;
    
    /**
     * 订单明细信息
     */
    private List<DDMXXX> DDMXXX;
    
}
