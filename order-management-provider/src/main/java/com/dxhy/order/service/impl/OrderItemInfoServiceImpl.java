package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.model.OrderItemInfo;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * 订单明细信息业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:24
 */
@Service
public class OrderItemInfoServiceImpl implements ApiOrderItemInfoService {
    
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;

    @Override
    public List<OrderItemInfo> selectOrderItemInfoByOrderId(String orderId, List<String> shList) {
        return orderItemInfoMapper.selectOrderItemInfoByOrderId(orderId, shList);
    }
    
    @Override
    public int deleteOrderItemInfoByOrderId(String orderId, List<String> shList) {
        return orderItemInfoMapper.deleteOrderItemInfoByOrderId(orderId, shList);
    }

    @Override
    public int insertOrderItemInfo(OrderItemInfo orderItemInfo) {
        return orderItemInfoMapper.insertOrderItemInfo(orderItemInfo);
    }
    
    @Override
    public int insertOrderItemByList(List<OrderItemInfo> orderItemInfos) {
        return orderItemInfoMapper.insertOrderItemByList(orderItemInfos);
    }
    
}
