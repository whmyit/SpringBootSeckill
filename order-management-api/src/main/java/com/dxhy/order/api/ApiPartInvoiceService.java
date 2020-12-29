package com.dxhy.order.api;

import com.dxhy.order.model.OrderItemInfo;

import java.util.List;

/**
 * 部分冲红接口
 *
 * @author ZSC-DXHY
 */
public interface ApiPartInvoiceService {
    
    /**
     * 遍历部分冲红所有发票明细数据
     *
     * @param fpdm
     * @param fphm
     * @param shList
     * @return
     */
    List<OrderItemInfo> partInvoiceQueryList(String fpdm, String fphm, List<String> shList);
}
