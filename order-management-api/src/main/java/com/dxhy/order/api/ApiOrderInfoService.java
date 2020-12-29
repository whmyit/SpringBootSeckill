package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderProcessInfo;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ApiOrderInfoService
 * @Description ：订单查询，插入，更新，删除操作
 * @date ：2018年7月21日 下午5:37:00
 */
public interface ApiOrderInfoService {
    
    /**
     * 统计订单号数据库中是否已存在
     *
     * @param process
     * @return
     */
    int countByDdh(OrderProcessInfo process);
    
    /**
     * 根据订单号批量查询订单信息  包含明细
     *
     * @param orders 订单号
     * @return OrderInfoAndItemInfo
     */
    List<CommonOrderInfo> queryOrderInfoByOrderIds(List<Map> orders);
    
    /**
     * 根据订单号批量查询订单信息  包含明细
     *
     * @param orders 订单号
     * @param shList 税号列表
     * @return OrderInfoAndItemInfo
     */
    List<CommonOrderInfo> batchQueryOrderInfoByOrderIds(List<String> orders, List<String> shList);
    
    /**
     * 根据orderID获取订单数据
     *
     * @param id
     * @param shList
     * @return
     */
    OrderInfo selectOrderInfoByOrderId(String id, List<String> shList);
    
    /**
     * 补全明细项中数量，单价
     *
     * @param orderInfo
     * @return List<CommonOrderInfo>
     * @author: 陈玉航
     * @date: Created on 2018年8月15日 下午7:56:00
     */
    List<CommonOrderInfo> completionSlAndDj(List<CommonOrderInfo> orderInfo);
    
    /**
     * 根据主键更新订单信息
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderInfoByOrderId(OrderInfo orderInfo, List<String> shList);
    
    /**
     * 插入订单信息
     *
     * @param record
     * @return
     */
    int insertOrderInfo(OrderInfo record);
    
    /**
     * 编辑购方信息,更新
     *
     * @param orderInfo
     * @param shList
     * @return
     */
    int updateOrderInfo(OrderInfo orderInfo, List<String> shList);
    
    /**
     * 根据原发票代码号码 查询原蓝票订单信息
     *
     * @param yFpdm
     * @param yFphm
     * @param shList
     * @return
     */
    List<OrderInfo> queryOrderInfoByYfpdmYfphm(String yFpdm, String yFphm, List<String> shList);

    /**
     * 根据请求流水号查询订单信息
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderInfo queryOrderInfoByFpqqlsh(String fpqqlsh, List<String> shList);
}
