package com.dxhy.order.dao;

import com.dxhy.order.model.OrderStatusStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单统计数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:49
 */
public interface OrderStatusStatisticsMapper {
    /**
     * 根据条件查询订单列表
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderStatusStatistics> queryOrderStatusInfo(@Param("map") Map map, @Param("shList") List<String> shList);
    
    
}
