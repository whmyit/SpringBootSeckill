package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;

import java.util.List;
import java.util.Map;

/**
 * 红字发票处理接口
 *
 * @author ZSC-DXHY
 */
public interface ApiRushRedInvoiceRequestInfoService {
    
    /**
     * 合并红票折扣行
     *
     * @param xmmx
     * @return
     */
    List<OrderItemInfo> redInvoiceMerge(List<OrderItemInfo> xmmx);
    
    /**
     * 红票折扣行合并后处理尾差
     *
     * @param protocolProjectBean
     * @param protocalFpt
     * @return
     */
    List<OrderItemInfo> processRedInvoiceDetailForCheckListSumAmount(List<OrderItemInfo> protocolProjectBean, final OrderInfo protocalFpt);
    
    /**
     * 查询合并折扣行
     *
     * @param commonOrderInfo
     * @return
     */
    Map<String, Object> itemMerge(CommonOrderInfo commonOrderInfo);
    
}
