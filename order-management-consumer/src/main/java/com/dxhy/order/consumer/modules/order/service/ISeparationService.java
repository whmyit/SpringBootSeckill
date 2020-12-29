package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.model.CommonOrderInfo;

/**
 * 价税分离数据操作
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:35
 */
public interface ISeparationService {
    
    /**
     * 价税分离
     *
     * @param order
     * @return
     * @throws OrderSeparationException
     */
    CommonOrderInfo taxSeparationService(CommonOrderInfo order) throws OrderSeparationException;
    
}
