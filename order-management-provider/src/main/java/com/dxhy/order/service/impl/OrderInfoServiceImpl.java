package com.dxhy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiSpecialInvoiceReversalService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.OrderInfoMapper;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.dao.OrderProcessInfoMapper;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.utils.DecimalCalculateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：OrderInfoServiceImpl
 * @Description ：订单信息实现类
 * @date ：2018年7月21日 下午5:53:27
 */
@Service
public class OrderInfoServiceImpl implements ApiOrderInfoService {
    
    @Resource
    private OrderInfoMapper orderMapper;
    
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;
    
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    
    @Resource
    private ApiSpecialInvoiceReversalService specialInvoiceReversalService;
    
    @Override
    public int countByDdh(OrderProcessInfo process) {
        return orderProcessInfoMapper.countByDdh(process);
    }
    
    /**
     * 根据订单号批量查询订单信息 包含明细
     *
     * @author: 陈玉航
     * @date: Created on 2018年7月25日 下午3:28:59
     */
    @Override
    public List<CommonOrderInfo> queryOrderInfoByOrderIds(List<Map> orders) {
        
        List<CommonOrderInfo> list = new ArrayList<>();
        
        for (Map map : orders) {
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
    
            list.add(getCommonOrderInfo(id, shList));
        }
        
        return list;
    }
    
    /**
     * 根据订单号批量查询订单信息 包含明细
     *
     * @author: 陈玉航
     * @date: Created on 2018年7月25日 下午3:28:59
     */
    @Override
    public List<CommonOrderInfo> batchQueryOrderInfoByOrderIds(List<String> orders, List<String> shList) {
    
        List<CommonOrderInfo> list = new ArrayList<>();
    
        for (String order : orders) {
            list.add(getCommonOrderInfo(order, shList));
        }
    
        return list;
    }
    
    private CommonOrderInfo getCommonOrderInfo(String orderId, List<String> shList) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo orderInfo = orderMapper.selectOrderInfoByOrderId(orderId, shList);
        OrderProcessInfo orderProcessInfo = orderProcessInfoMapper.selectByOrderId(orderId, shList);
        List<OrderItemInfo> orderItemInfoList = orderItemInfoMapper.selectOrderItemInfoByOrderId(orderId, shList);
        if (StringUtils.isNotBlank(orderInfo.getKplx()) && StringUtils.isNotBlank(orderInfo.getFpzlDm()) && OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())
                && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
            orderInvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
            OrderInvoiceInfo orderInvoiceInfo1 = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo, shList);

            // 红字信息表
            if (ObjectUtil.isNotNull(orderInvoiceInfo1) && StringUtils.isNotBlank(orderInvoiceInfo1.getHzxxbbh())) {

                SpecialInvoiceReversalEntity selectSpecialInvoiceReversalByCode = specialInvoiceReversalService.selectSpecialInvoiceReversalBySubmitCode(orderInvoiceInfo1.getHzxxbbh());
                if (ObjectUtil.isNotNull(selectSpecialInvoiceReversalByCode) && StringUtils.isNotBlank(selectSpecialInvoiceReversalByCode.getSqsm())) {
                    if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(selectSpecialInvoiceReversalByCode.getSqsm())) {
                        commonOrderInfo.setFlagbs(ConfigureConstant.STRING_0);
                    }
                }
            }
        }
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setProcessInfo(orderProcessInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfoList);
        return commonOrderInfo;
    }
    
    /**
     * 补全明细项中数量，单价
     *
     * @author: 陈玉航
     * @date: Created on 2018年8月4日 下午3:50:10
     */
    @Override
    public List<CommonOrderInfo> completionSlAndDj(List<CommonOrderInfo> orderInfo) {
        
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            List<OrderItemInfo> orderItemInfo = commonOrderInfo.getOrderItemInfo();
            for (OrderItemInfo orderItem : orderItemInfo) {
                //折扣行不涉及数量单价补全
                if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
                    continue;
                }
                //数量单价都存在不处理
                if (StringUtils.isNotBlank(orderItem.getXmsl()) && StringUtils.isNotBlank(orderItem.getXmdj())) {
                    continue;
                }
    
                //订单数量为空，单价不为空，计算数量，项目金额/项目单价=项目数量，保留小数点后八位
                if (StringUtils.isBlank(orderItem.getXmsl()) && StringUtils.isNotBlank(orderItem.getXmdj())) {
                    orderItem.setXmsl(String.valueOf(DecimalCalculateUtil.div(Double.parseDouble(orderItem.getXmje())
                            , Double.parseDouble(orderItem.getXmdj()), 8)));
                    continue;
                }
                //订单单价为空，数量不为空，计算单价，项目金额/项目数量=项目单价，保留小数点后两位
                if (StringUtils.isNotBlank(orderItem.getXmsl()) && StringUtils.isBlank(orderItem.getXmdj())) {
                    orderItem.setXmdj(DecimalCalculateUtil.decimalFormat(DecimalCalculateUtil.div(Double.parseDouble(orderItem.getXmje()),
                            Double.parseDouble(orderItem.getXmsl()), 8), 8));
                    continue;
                }
            }
        }
        return orderInfo;
    }

    @Override
    public int updateOrderInfoByOrderId(OrderInfo orderInfo, List<String> shList) {
        return orderMapper.updateOrderInfoByOrderId(orderInfo, shList);
    }
    
    @Override
    public int insertOrderInfo(OrderInfo record) {
        return orderMapper.insertOrderInfo(record);
    }
    
    @Override
    public int updateOrderInfo(OrderInfo orderInfo, List<String> shList) {
        return orderMapper.updateOrderInfo(orderInfo, shList);
    }
    
    @Override
    public OrderInfo selectOrderInfoByOrderId(String id, List<String> shList) {
        return orderMapper.selectOrderInfoByOrderId(id, shList);
    }
    
    @Override
    public List<OrderInfo> queryOrderInfoByYfpdmYfphm(String yFpdm, String yFphm, List<String> shList) {
        List<OrderInfo> selectOrderInfoByYfpdmhm = orderMapper.selectOrderInfoByYfpdmhm(yFpdm, yFphm, shList);
        return selectOrderInfoByYfpdmhm;
    }

    /**
     * 根据请求流水号查询订单信息
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    @Override
    public OrderInfo queryOrderInfoByFpqqlsh(String fpqqlsh, List<String> shList) {
        return orderMapper.selectOrderInfoByDdqqlsh(fpqqlsh, shList);
    }

}
