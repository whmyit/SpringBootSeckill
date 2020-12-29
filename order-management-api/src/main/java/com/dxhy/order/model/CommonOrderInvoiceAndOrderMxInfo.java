package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 订单-发票-明细等数据通用返回
 *
 * @author 张双超
 */
@Setter
@Getter
public class CommonOrderInvoiceAndOrderMxInfo implements Serializable {

    private OrderInvoiceInfo orderInvoiceInfo;

    private List<OrderItemInfo> orderItemList;

    private OrderInfo orderInfo;

}
