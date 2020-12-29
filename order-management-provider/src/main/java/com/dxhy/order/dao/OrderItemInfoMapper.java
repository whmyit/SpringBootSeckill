package com.dxhy.order.dao;

import com.dxhy.order.model.OrderItemInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单明细数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:42
 */
public interface OrderItemInfoMapper {
    
    /**
     * 根据orderId删除明细数据表
     *
     * @param orderId
     * @param shList
     * @return
     */
    int deleteOrderItemInfoByOrderId(@Param("orderId") String orderId, @Param("shList") List<String> shList);
    
    /**
     * 插入订单明细
     *
     * @param record
     * @return
     */
    int insertOrderItemInfo(OrderItemInfo record);
    
    /**
     * 根据订单id查询订单明细
     *
     * @param orderId
     * @param shList
     * @return
     */
    List<OrderItemInfo> selectOrderItemInfoByOrderId(@Param("orderId") String orderId, @Param("shList") List<String> shList);
    
    /**
     * 查询批次明细数据
     *
     * @param orderInfoIdList
     * @param shList
     * @return
     */
    List<OrderItemInfo> selectAllByOrderId(@Param("orderInfoIdList") List<String> orderInfoIdList, @Param("shList") List<String> shList);
    
    /**
     * 批量插入orderItem表
     *
     * @param resultOrderItemList
     * @return
     */
    int insertOrderItemByList(@Param("list") List<OrderItemInfo> resultOrderItemList);
    
}
