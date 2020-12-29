package com.dxhy.order.dao;

import com.dxhy.order.model.OrderOriginExtendInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单原始订单数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:43
 */
public interface OrderOriginExtendInfoMapper {
    
    /**
     * 插入原始订单
     *
     * @param record
     * @return
     */
    int insertOrderOriginExtend(OrderOriginExtendInfo record);
    
    /**
     * 查询原始订单
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map<String, Object>> queryOriginList(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询票单比对
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map<String, Object>> queryOriginOrderCompare(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询订单发票
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map<String, Object>> queryOriginOrderAndInvoiceInfo(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询原始订单
     *
     * @param param
     * @param shList
     * @return
     */
    List<OrderOriginExtendInfo> queryOriginOrderByOrder(@Param("origin") OrderOriginExtendInfo param, @Param("shList") List<String> shList);
    
    /**
     * 查询订单
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, Object> queryCompareOriginOrderAndInvoiceCounter(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 更新原始订单
     *
     * @param extendInfo
     * @param shList
     * @return
     */
    int updateSelectiveByOrderId(@Param("orgin") OrderOriginExtendInfo extendInfo, @Param("shList") List<String> shList);
}
