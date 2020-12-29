package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
public class COMMON_ORDER_INVOICE {
    
    /**
     * 订单发票信息
     */
    private ORDER_INVOICE_INFO ORDER_INVOICE_INFO;
    
    /**
     * 订单扩展信息
     */
    private List<ORDER_EXTENSION_INFO> ORDER_EXTENSION_INFOS;
    
    /**
     * 发票明细信息
     */
    private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
    
}
