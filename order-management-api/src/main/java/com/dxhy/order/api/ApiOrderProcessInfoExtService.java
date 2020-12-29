package com.dxhy.order.api;

import com.dxhy.order.model.OrderProcessInfoExt;

import java.util.List;

/**
 * 订单处理表扩展接口
 *
 * @author ZSC-DXHY
 */
public interface ApiOrderProcessInfoExtService {
    
    /**
     * 通过orderProcessId查询
     *
     * @param orderProcessId
     * @param shList
     * @return
     */
    List<OrderProcessInfoExt> selectOrderProcessInfoExtByOrderProcessId(String orderProcessId, List<String> shList);
    
}
