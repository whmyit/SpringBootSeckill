package com.dxhy.order.dao;

import com.dxhy.order.model.OrderProcessInfoExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单处理扩展表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:44
 */
public interface OrderProcessInfoExtMapper {
    
    /**
     * 插入订单处理扩展表
     *
     * @param record
     * @return
     */
    int insertOrderProcessExt(OrderProcessInfoExt record);
    
    /**
     * 查询订单扩展信息
     *
     * @param orderProcessId
     * @param shList
     * @return
     */
    List<OrderProcessInfoExt> selectOrderProcessInfoExtByOrderProcessId(@Param("orderProcessId") String orderProcessId, @Param("shList") List<String> shList);
    
    /**
     * 根据处理表id和父级处理表id,和有效状态获取扩展表唯一数据
     *
     * @param orderProcessId
     * @param parentOrderProcessId
     * @param shList
     * @return
     */
    OrderProcessInfoExt selectOrderProcessInfoExtByOrderProcessIdAndParentOrderProcessId(@Param("orderProcessId") String orderProcessId, @Param("parentOrderProcessId") String parentOrderProcessId, @Param("shList") List<String> shList);
    
    /**
     * 查询同父ext
     *
     * @param parentOrderProcessId
     * @param shList
     * @return
     */
    List<OrderProcessInfoExt> selectExtByParentProcessId(@Param("parentId") String parentOrderProcessId, @Param("shList") List<String> shList);
    /**
     * 更新处理扩展表
     *
     * @param record
     * @param shList
     * @return
     */

    int updateByPrimaryKeySelective(@Param("ext")OrderProcessInfoExt record,@Param("shList") List<String> shList);
}
