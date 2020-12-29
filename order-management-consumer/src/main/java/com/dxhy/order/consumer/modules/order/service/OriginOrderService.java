package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.model.PageUtils;

import javax.servlet.ServletOutputStream;
import java.util.List;
import java.util.Map;

/**
 * 原始订单接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:27
 */
public interface OriginOrderService {
    
    /**
     * 原始订单列表查询
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryOriginList(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 票单比对列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils executeCompareOriginOrderAndInvoice(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 票单比对详情
     *
     * @param originOrderId
     * @param shList
     * @return
     */
    Map<String, Object> queryOriginOrderDetail(String originOrderId, List<String> shList);
    
    /**
     * 票单比对数据导出
     *
     * @param paramMap
     * @param outputStream
     * @param shList
     */
    void exportCompareOriginOrderAndInvoice(Map<String, Object> paramMap, ServletOutputStream outputStream, List<String> shList);
    
    /**
     * 查询列表金额统计
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, String> queryCompareOriginOrderAndInvoiceCounter(Map<String, Object> paramMap, List<String> shList);
    
}
