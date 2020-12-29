package com.dxhy.order.api;

import com.dxhy.order.model.OrderOriginExtendInfo;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 原始订单业务接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:37
 */
public interface ApiOriginOrderExtendService {
    
    
    /**
     * 查询原始订单列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryOriginList(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 查询订单发票比对列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryOriginOrderCompare(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据原始订单查询发票信息
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map<String, Object>> queryOriginOrderAndInvoiceInfo(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 查询票单比对总计金额
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, Object> queryCompareOriginOrderAndInvoiceCounter(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据条件查询原始订单关系表
     *
     * @param orderOriginExtendInfo
     * @param shList
     * @return
     */
    List<OrderOriginExtendInfo> queryOriginOrderByOrder(OrderOriginExtendInfo orderOriginExtendInfo, List<String> shList);
    
}
