package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.constant.OrderSplitException;
import com.dxhy.order.consumer.model.page.PageCommonOrderInfo;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：OrderSplitService
 * @Description ：订单拆分service
 * @date ：2018年7月30日 上午11:11:55
 */

public interface OrderSplitService {
    
    /**
     * 订单拆分
     *
     * @param commonList
     * @throws OrderReceiveException
     */
    void splitOrder(List<CommonOrderInfo> commonList) throws OrderReceiveException;
    
    /**
     * 订单拆分
     *
     * @param orderId
     * @param shList
     * @param parseJeArray
     * @param key
     * @return
     * @throws OrderSplitException
     */
    List<CommonOrderInfo> splitOrder(String orderId, List<String> shList, String[] parseJeArray, String key) throws OrderSplitException;
    
    /**
     * 保存拆分数据
     *
     * @param commonList
     * @throws OrderReceiveException
     */
    void saveOrderSplitOrder(List<PageCommonOrderInfo> commonList) throws OrderReceiveException;
}
