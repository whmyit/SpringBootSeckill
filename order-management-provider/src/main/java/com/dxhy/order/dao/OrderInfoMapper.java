package com.dxhy.order.dao;

import com.dxhy.order.model.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:38
 */
public interface OrderInfoMapper {
    
    /**
     * 插入订单表
     *
     * @param record
     * @return
     */
    int insertOrderInfo(OrderInfo record);
    
    /**
     * 根据订单id获取订单信息
     *
     * @param id
     * @param shList
     * @return
     */
    OrderInfo selectOrderInfoByOrderId(@Param(value = "id") String id, @Param("shList") List<String> shList);
    
    /**
     * 根据发票请求流水号获取订单数据,主要用于查询数据是否存在.
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderInfo selectOrderInfoByDdqqlsh(@Param("fpqqlsh") String fpqqlsh, @Param("shList") List<String> shList);
    
    /**
     * 更新订单表数据
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderInfoByOrderId(@Param("orderInfo") OrderInfo orderInfo, @Param("shList") List<String> shList);
    
    /**
     * 通过发票代码号码查询所有order信息
     *
     * @param fpdm
     * @param fphm
     * @param shList
     * @return
     */
    List<OrderInfo> selectOrderInfoByYfpdmhm(@Param("fpdm") String fpdm, @Param("fphm") String fphm, @Param("shList") List<String> shList);
    
    /**
     * 更新订单数据
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderInfo(@Param("orderInfo") OrderInfo orderInfo, @Param("shList") List<String> shList);
    
}
