package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单状态统计业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:34
 */
public interface OrderStatusStatisticsService {
    
    /**
     * 获取订单状态列表
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectOrderStatusInfo(Map map, List<String> shList);
    
    /**
     * 导出订单列表
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderStatusStatistics> exportOrderStatusInfo(Map map, List<String> shList);
    
    /**
     * 更新购方信息
     *
     * @param orderInfo
     * @return
     */
    Map updateGhfInfo(OrderInfo orderInfo);
}
