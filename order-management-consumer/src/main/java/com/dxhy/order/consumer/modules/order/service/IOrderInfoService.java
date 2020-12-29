package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.consumer.model.OderDetailInfo;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单处理层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:33
 */
public interface IOrderInfoService {
    
    /**
     * 查询订单列表页
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectOrderInfo(Map map, List<String> shList);
    
    /**
     * 根据orderProcessId和发票请求流水号 查询订单信息和订单明细
     *
     * @param orderProcessId
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OderDetailInfo selectOrderDetailByOrderProcessIdAndFpqqlsh(String orderProcessId, String fpqqlsh, List<String> shList);
    
    /**
     * 编辑订单详情
     *
     * @param commonOrderInfo
     * @return
     * @throws OrderReceiveException
     */
    Map updateOrderInfoAndOrderProcessInfo(CommonOrderInfo commonOrderInfo) throws OrderReceiveException;
    
    /**
     * 根据orderId获取订单信息
     *
     * @param orderId
     * @param shList
     * @return
     */
    CommonOrderInfo getOrderInfoByOrderId(String orderId, List<String> shList);
    
    /**
     * 根据纳税人识别号，业务类型来统计金额和票数
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils selectYwlxCount(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据订单号查询已存在的订单个数
     *
     * @param ddh
     * @param xhfNsrsbh
     * @return
     */
    
    int getOrderInfoByDdh(String ddh, String xhfNsrsbh);
    
    /**
     * 业务类型统计，统计各项，总金额，票数这些
     *
     * @param paramMap
     * @param shList
     * @return
     */
    Map<String, String> selectYwlxCountTotal(Map<String, Object> paramMap, List<String> shList);
    
    /**
     * 根据发票代码号码查询订单数据
     *
     * @param fpdm
     * @param fphm
     * @param xhfNsrsbh
     * @return
     */
    Map<String, Object> querySimpleOrderInfoByFpdmAndFphm(String fpdm, String fphm, String xhfNsrsbh);
}
