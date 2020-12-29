package com.dxhy.order.api;

import java.util.List;
/**
 * 订单发票批次业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:36
 */
public interface ApiOrderBatchRequestService {
    
    
    /**
     * 根据发票请求批次号查询发票批次
     *
     * @param fpqqpch
     * @param shList
     * @return OrderBatchRequest
     * @author: 陈玉航
     * @date: Created on 2019年1月3日 下午8:18:31
     */
    int selectOrderBatchRequestByDdqqpch(String fpqqpch, List<String> shList);
}
