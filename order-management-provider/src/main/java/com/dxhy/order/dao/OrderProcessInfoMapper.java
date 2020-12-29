package com.dxhy.order.dao;

import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderProcessInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单处理表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:45
 */
@Mapper
public interface OrderProcessInfoMapper {
    
    /**
     * 插入订单处理表
     *
     * @param record
     * @return
     */
    int insertOrderProcessInfo(OrderProcessInfo record);
    
    /**
     * 根据订单处理表id获取订单处理表数据
     *
     * @param id
     * @param shList
     * @return
     */
    OrderProcessInfo selectOrderProcessInfoByProcessId(@Param("id") String id, @Param("shList") List<String> shList);
    
    /**
     * ddh在订单中的个数
     *
     * @param process
     * @return
     */
    int countByDdh(OrderProcessInfo process);
    
    /**
     * 根据流水号查询处理表数据
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderProcessInfo queryOrderProcessInfoByFpqqlsh(@Param("fpqqlsh") String fpqqlsh, @Param("shList") List<String> shList);
    
    /**
     * 根据批次号查询订单处理表数据
     *
     * @param ddqqpch
     * @param shList
     * @return
     */
    List<OrderProcessInfo> selectOrderProcessInfoByDdqqpch(@Param("ddqqpch") String ddqqpch, @Param("shList") List<String> shList);
    
    /**
     * 根据发票请求流水号(订单请求流水号)获取订单处理表数据,用于查询数据是否存在
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderProcessInfo selectOrderProcessInfoByDdqqlsh(@Param("fpqqlsh") String fpqqlsh, @Param("shList") List<String> shList);
    
    /**
     * 更新处理表数据
     *
     * @param orderProcessInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfoByProcessId(@Param("processInfo") OrderProcessInfo orderProcessInfo, @Param("shList") List<String> shList);
    
    /**
     * 根据条件查询顶单列表
     *
     * @param map
     * @param shList
     * @return
     */
    List<Map> queryOrderInfo(@Param("map") Map map, @Param("shList") List<String> shList);
    
    /**
     * 通过orderId查询处理表
     *
     * @param orderId
     * @param shList
     * @return
     */
    OrderProcessInfo selectByOrderId(@Param("orderId") String orderId, @Param("shList") List<String> shList);
    
    /**
     * 多维度查询订单处理表
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderProcessInfo> selectOrderProcessByFpqqlshDdhNsrsbh(@Param("orderMap") Map map, @Param("shList") List<String> shList);
    
    /**
     * 更新订单处理表
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfo(@Param("orderInfo") OrderInfo orderInfo, @Param("shList") List<String> shList);
    
    /**
     * 根据纳税人识别号，业务类型来统计金额和票数
     *
     * @param map
     * @param shList
     * @return
     */
    List<Map<String, Object>> selectYwlxCount(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 务类型统计，统计各项，总金额，票数这些
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, String> selectYwlxCountTotal(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 判断是否存在未审核的订单
     *
     * @param paramMap
     * @param shList
     * @return
     */
    String isExistNoAuditOrder(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 更新订单处理表
     *
     * @param updateProcessInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfoByFpqqlsh(@Param("processInfo") OrderProcessInfo updateProcessInfo, @Param("shList") List<String> shList);
}
