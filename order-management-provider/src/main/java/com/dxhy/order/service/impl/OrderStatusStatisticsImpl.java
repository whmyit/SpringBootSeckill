package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.api.ApiOrderStatusStatisticsService;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.dao.OrderStatusStatisticsMapper;
import com.dxhy.order.model.OrderStatusStatistics;
import com.dxhy.order.model.PageUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
/**
 * 订单统计业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:26
 */
@Slf4j
@Service
public class OrderStatusStatisticsImpl implements ApiOrderStatusStatisticsService {
    @Resource
    private OrderStatusStatisticsMapper orderStatusStatisticsMapper;
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    @Resource
    private ApiOrderInfoService apiOrderInfoService;
    
    @Resource
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;


    @Override
    public PageUtils selectOrderStatusInfo(Map map, List<String> shList) {
        int pageSize = (Integer) map.get("pageSize");
        int currPage = (Integer) map.get("currPage");
        // 这里前端从1开始需要进行-1操作
        // currPage=currPage-1;
        log.info("订单查询，当前页：{},页面条数:{}", currPage, pageSize);
        PageHelper.startPage(currPage, pageSize);
        List<OrderStatusStatistics> list = orderStatusStatisticsMapper.queryOrderStatusInfo(map, shList);
        PageInfo<OrderStatusStatistics> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
        return page;
    }
    
    @Override
    public List<OrderStatusStatistics> exportOrderStatusInfo(Map map, List<String> shList) {
        return orderStatusStatisticsMapper.queryOrderStatusInfo(map, shList);
    }

}
