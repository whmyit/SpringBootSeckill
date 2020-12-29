package com.dxhy.order.dao;

import com.dxhy.order.model.OrderBatchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单批次数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:37
 */
@Mapper
public interface OrderBatchRequestMapper {
    
    /**
     * 插入订单批次数据
     *
     * @param record
     * @return
     */
    int insertOrderBatch(OrderBatchRequest record);
    
    /**
     * 更新订单批次数据
     *
     * @param orderBatchRequest
     * @param shList
     * @return
     */
    int updateByPrimaryKeySelective(@Param("orderBatchRequest") OrderBatchRequest orderBatchRequest, @Param("shList") List<String> shList);
    
    /**
     * 查询订单批次数据
     *
     * @param fpqqpch
     * @param shList
     * @return
     */
    int selectOrderBatchRequestByDdqqpch(@Param("ddqqpch") String fpqqpch, @Param("shList") List<String> shList);
    
}
