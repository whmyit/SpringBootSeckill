package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.constant.OrderSplitException;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.R;

import java.util.List;

/**
 * 生成待开单据接口
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年7月25日 下午3:29:53
 */
public interface IGenerateReadyOpenOrderService {
    
    /**
     * 冲红流程
     *
     * @param grov
     * @param uId
     * @param deptId
     * @return
     * @throws OrderSeparationException
     */
    R reshRed(CommonOrderInfo grov, String uId, String deptId) throws OrderSeparationException;
    
    /**
     * 商品订单信息补全
     *
     * @param commonOrderInfoList
     * @param userId
     * @throws OrderReceiveException
     */
    void completeOrderInfo(List<CommonOrderInfo> commonOrderInfoList, String userId) throws OrderReceiveException;
    
    /**
     * 生成待开具订单
     *
     * @param pageToFpkjInfo
     * @param uid
     * @return
     */
    R excuSingle(CommonOrderInfo pageToFpkjInfo, String uid);
    
    /**
     * 订单拆分
     *
     * @param value
     * @param terminalCode
     * @param userId
     * @return
     * @throws OrderSplitException
     */
    List<CommonOrderInfo> orderSplit(List<CommonOrderInfo> value, String terminalCode, String userId) throws OrderSplitException;
    
    /**
     * 保存暂存的订单信息
     *
     * @param value
     * @return
     */
    boolean saveOrderInfo(List<CommonOrderInfo> value);
    
    /**
     * 保存合并后的订单信息
     *
     * @param commonOrderInfo
     * @return
     */
    boolean saveOrderMergeInfo(CommonOrderInfo commonOrderInfo);
    
    /**
     * 保存拆分后的订单信息
     *
     * @param resultList
     * @return
     * @throws OrderReceiveException
     */
    List<CommonOrderInfo> saveOrderSplitInfo(List<CommonOrderInfo> resultList) throws OrderReceiveException;
    
    /**
     * 页面订单数据保存
     *
     * @param commonOrderInfoList
     * @return
     */
    List<CommonOrderInfo> savePageOrderInfo(List<CommonOrderInfo> commonOrderInfoList);
    
    /**
     * 补全订单信息
     *
     * @param specialInvoiceList
     * @throws OrderReceiveException
     */
    void completeOrderInfo(List<CommonOrderInfo> specialInvoiceList) throws OrderReceiveException;
    
    
}
