package com.dxhy.order.api;

import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单统计表接口
 *
 * @author ZSC-DXHY
 */
public interface ApiOrderStatusStatisticsService {
    
    /**
     * 根据发票类型 发票开具状态 发票推送状态 查询订单统计信息
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectOrderStatusInfo(Map map, List<String> shList);
    
    /**
     * 根据发票类型 发票开具状态 发票推送状态 查询导出订单统计信息
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderStatusStatistics> exportOrderStatusInfo(Map map, List<String> shList);
    
    
}
