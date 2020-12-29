package com.dxhy.order.consumer.modules.order.service.impl;

import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.api.ApiOrderStatusStatisticsService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.order.service.OrderStatusStatisticsService;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 订单状态统计业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:18
 */
@Service
public class OrderStatusStatisticsImpl implements OrderStatusStatisticsService {

    @Reference
    private ApiOrderStatusStatisticsService apiOrderStatusStatisticsService;
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Override
    public PageUtils selectOrderStatusInfo(Map map, List<String> shList) {
        return apiOrderStatusStatisticsService.selectOrderStatusInfo(map, shList);
    }
    
    @Override
    public List<OrderStatusStatistics> exportOrderStatusInfo(Map map, List<String> shList) {
        return apiOrderStatusStatisticsService.exportOrderStatusInfo(map, shList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map updateGhfInfo(OrderInfo orderInfo) {
        Map map = new HashMap<>(5);
        List<String> shList = new ArrayList<>();
        shList.add(orderInfo.getXhfNsrsbh());
        int updateOrderInfoResult = apiOrderInfoService.updateOrderInfo(orderInfo, shList);
        int updateOrderProcessInfoResult = apiOrderProcessService.updateOrderProcessInfo(orderInfo, shList);
    
        if (updateOrderInfoResult > 0 && updateOrderProcessInfoResult > 0) {
            map.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
            return map;
        } else {
            map.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            return map;
        }
    }
}
