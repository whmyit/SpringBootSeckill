package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiPartInvoiceService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.OrderInfoMapper;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.OrderItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 部分冲红接口服务
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class ApiPartInvoiceServiceImpl implements ApiPartInvoiceService {

    private static final String LOGGER_MSG = "(部分冲红发票遍历)";
    
    @Resource
    private OrderInfoMapper orderInfoMapper;
    
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;

    /**
     * 根据原发票代码号码获取所有冲红发票明细行数据,用于专票冲红明细列表展示
     *
     * @param fpdm
     * @param fphm
     * @return
     * @throws OrderReceiveException
     */
    @Override
    public List<OrderItemInfo> partInvoiceQueryList(String fpdm, String fphm, List<String> shList) {
        log.debug("{},接收到请求,原发票代码{},原发票号码{}", LOGGER_MSG, fpdm, fphm);
        /**
         * 通过发票代码、号码查出所有红票订单信息，通过订单信息得到发票信息。
         */
        List<OrderInfo> orderList = orderInfoMapper.selectOrderInfoByYfpdmhm(fpdm, fphm, shList);
        if (orderList == null || orderList.size() == 0) {
            log.error("{},对应订单信息不存在,发票代码{},发票号码{}", LOGGER_MSG, fpdm, fphm);
            return null;
        }
        /**
         * 查询出所有冲红发票数据明细,然后合并后返回
         */
        List<OrderItemInfo> list = new ArrayList<>();
        for (OrderInfo orderInfo : orderList) {
            OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
            orderInvoiceInfo1.setOrderInfoId(orderInfo.getId());
            OrderInvoiceInfo selectByPrimaryKey = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
        
            if (selectByPrimaryKey != null) {
                if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(selectByPrimaryKey.getKpzt()) && !OrderInfoEnum.ZFLX_1.getKey().equals(selectByPrimaryKey.getZfBz())) {
                    List<OrderItemInfo> orderItemInfos = orderItemInfoMapper.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
                    list.addAll(orderItemInfos);
                }
            }
        
        }
        return list;
    }
}
