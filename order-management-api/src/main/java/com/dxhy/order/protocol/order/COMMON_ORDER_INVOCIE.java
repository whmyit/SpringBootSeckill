package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 订单发票全数据返回协议bean
 *
 * @author zsc
 * @date 2018年9月19日 15:14:50
 */
@ToString
@Setter
@Getter
public class COMMON_ORDER_INVOCIE implements Serializable {
    
    private ORDER_INVOICE_HEAD ORDER_INVOICE_HEAD;
    private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
    
}
