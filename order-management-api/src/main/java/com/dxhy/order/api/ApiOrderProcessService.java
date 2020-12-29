package com.dxhy.order.api;

import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单处理表接口
 *
 * @author ZSC-DXHY
 */
public interface ApiOrderProcessService {
    
    /**
     * 根据主键查询orderprocess信息
     *
     * @param id
     * @param shList
     * @return
     */
    OrderProcessInfo selectOrderProcessInfoByProcessId(String id, List<String> shList);
    
    /**
     * 根据发票请求流水号获取发票处理表数据
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderProcessInfo queryOrderProcessInfoByFpqqlsh(String fpqqlsh, List<String> shList);
    
    /**
     * 根据订单请求批次号获取发票处理表数据
     *
     * @param ddqqpch
     * @param shList
     * @return
     */
    List<OrderProcessInfo> selectOrderProcessInfoByDdqqpch(String ddqqpch, List<String> shList);
    
    /**
     * 根据订单状态 业务类型 最小开票金额 订单号 购货方名称 查询订单信息
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectOrderInfo(Map map, List<String> shList);
    
    /**
     * 更新开票状态
     *
     * @param fpqqlsh
     * @param ddzt
     * @param kpzt
     * @param sbyy
     * @param shList
     * @return
     */
    boolean updateKpzt(String fpqqlsh, String ddzt, String kpzt, String sbyy, List<String> shList);
    
    /**
     * 更新orderprocessInfo表
     *
     * @param orderProcessInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfoByProcessId(OrderProcessInfo orderProcessInfo, List<String> shList);
    
    /**
     * 插入orderprocessInfo 数据
     *
     * @param record
     * @return
     */
    int insert(OrderProcessInfo record);
    
    /**
     * 根据list更新订单状态
     *
     * @param list
     * @param key
     * @param shList
     * @return
     */
    int updateOrderDdztByList(List<OrderInfo> list, String key, List<String> shList);
    
    /**
     * 根据销方税号,订单号,发票请求流水号进行查询orderprocess信息
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderProcessInfo> selectOrderProcessByFpqqlshDdhNsrsbh(Map map, List<String> shList);
    
    /**
     * 根据orderProcessId查询最终的子订单
     *
     * @param processId
     * @param shList
     * @return
     */
    List<OrderProcessInfo> findChildList(String processId, List<String> shList);
    
    /**
     * 根据orderProcessId查询原始订单
     *
     * @param orderProcessInfo
     * @param shList
     * @return
     */
    List<OrderProcessInfo> findTopParentList(OrderProcessInfo orderProcessInfo, List<String> shList);
    
    /**
     * 编辑购方信息,更新
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfo(OrderInfo orderInfo, List<String> shList);
    
    /**
     * 根据纳税人识别号，业务类型来统计金额和票数
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils selectYwlxCount(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 业务类型统计，统计各项，总金额，票数这些
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, String> selectYwlxCountTotal(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据订单id查询处理表信息
     *
     * @param orderId
     * @param shList
     * @return
     */
    OrderProcessInfo selectByOrderId(String orderId, List<String> shList);
    
    /**
     * 更新订单处理表信息
     *
     * @param updateList
     * @return
     */
    int updateListOrderProcessInfoByProcessId(List<OrderProcessInfo> updateList);
    
    /**
     * 是否编辑数据
     *
     * @param paramMap
     * @param shList
     * @return
     */
    boolean isExistNoAuditOrder(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据流水号更新订单处理表信息
     *
     * @param updateProcessInfo
     * @param shList
     * @return
     */
    int updateOrderProcessInfoByFpqqlsh(OrderProcessInfo updateProcessInfo, List<String> shList);
}
