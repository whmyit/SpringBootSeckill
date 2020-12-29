package com.dxhy.order.api;

import com.dxhy.order.model.OrderItemInfo;

import java.util.List;

/**
 * 订单明细接口
 *
 * @author ZSC-DXHY
 */
public interface ApiOrderItemInfoService {
    
    /**
     * 根据订单id查询订单明细
     *
     * @param orderId
     * @param shList
     * @return
     */
    List<OrderItemInfo> selectOrderItemInfoByOrderId(String orderId, List<String> shList);
    
    
    /**
     * 根据订单id删除订单明细
     *
     * @param orderId
     * @param shList
     * @return
     */
    int deleteOrderItemInfoByOrderId(String orderId, List<String> shList);
    
    /**
     * 插入订单明细
     *
     * @param orderItemInfo
     * @return
     */
    int insertOrderItemInfo(OrderItemInfo orderItemInfo);
    
    /**
     * 批量插入订单明细
     *
     * @param orderItemInfos
     * @return
     */
    int insertOrderItemByList(List<OrderItemInfo> orderItemInfos);
    
}
