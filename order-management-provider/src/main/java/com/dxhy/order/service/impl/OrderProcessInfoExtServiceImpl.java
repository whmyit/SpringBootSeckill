package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOrderProcessInfoExtService;
import com.dxhy.order.dao.OrderProcessInfoExtMapper;
import com.dxhy.order.model.OrderProcessInfoExt;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
/**
 * 订单处理扩展表业务实现层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:25
 */
@Service
public class OrderProcessInfoExtServiceImpl implements ApiOrderProcessInfoExtService {
    @Resource
    private OrderProcessInfoExtMapper orderProcessInfoExtMapper;
    
    @Override
    public List<OrderProcessInfoExt> selectOrderProcessInfoExtByOrderProcessId(String orderProcessId, List<String> shList) {
        return orderProcessInfoExtMapper.selectOrderProcessInfoExtByOrderProcessId(orderProcessId, shList);
    }

}
