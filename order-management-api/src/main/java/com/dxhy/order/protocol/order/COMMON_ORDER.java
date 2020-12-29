package com.dxhy.order.protocol.order;

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
public class COMMON_ORDER implements Serializable {
    
    /**
     * 订单主体信息
     */
    private COMMON_ORDER_HEAD COMMON_ORDER_HEAD;
    
    /**
     * 订单明细信息
     */
    private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
    
}
