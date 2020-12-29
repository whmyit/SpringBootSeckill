package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.model.page.PageCommonOrderInfo;
import com.dxhy.order.consumer.model.page.PageOrderItemInfo;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.OrderSplitService;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.utils.CommonUtils;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.OrderSplitUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：OrderSplitServiceImpl
 * @Description ：订单拆分实现类
 * @date ：2018年7月30日 上午11:12:15
 */
@Service
@Slf4j
public class OrderSplitServiceImpl implements OrderSplitService {
    
    private static final String LOGGER_MSG = "(订单拆分实现类)";
    
    private static final String CF = "cf";
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0000");
    private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;

    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Resource
    private IGenerateReadyOpenOrderService iGenerateReadyOpenOrderService;
    
    @Override
    public void splitOrder(List<CommonOrderInfo> commonList) throws OrderReceiveException {
    	
    	/**
    	 *  以后添加订单拆分后保存的方法
    	 */
        saveOrderSplitInfo(commonList, ConfigureConstant.STRING_1);
    }

    /**
     * 根据多个金额和数量拆分 TODO 金额拆分重构
     *
     * @throws OrderSplitException
     * @throws Exception
     */
    @Override
    public List<CommonOrderInfo> splitOrder(String orderId, List<String> shList, String[] parseJeArray, String key) throws OrderSplitException {
    
        //获取订单信息
        OrderInfo selectOrderInfoByOrderId = apiOrderInfoService.selectOrderInfoByOrderId(orderId, shList);
        //获取订单明细信息
        List<OrderItemInfo> selectOrderItemInfoByOrderId = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderId, shList);
    
        CommonOrderInfo comm = new CommonOrderInfo();
    
        comm.setOrderInfo(selectOrderInfoByOrderId);
        comm.setOrderItemInfo(selectOrderItemInfoByOrderId);
    
        List<CommonOrderInfo> orderSplit;
        if (OrderInfoEnum.ORDER_SPLIT_JE_ARRAY.getKey().equals(key)) {
        
            OrderSplitConfig config = new OrderSplitConfig();
            config.setSplitType(OrderSplitEnum.ORDER_SPLIT_TYPE_2.getKey());
            config.setSplitRule(OrderSplitEnum.ORDER_SPLIT_RULE_1.getKey());
    
            List<Double> list = new ArrayList<>();
            for (String je : parseJeArray) {
                list.add(Double.valueOf(je));
            }
    		config.setJeList(list);
    		
    		orderSplit = OrderSplitUtil.orderSplit(comm, config);
    	}else {
    
            OrderSplitConfig config = new OrderSplitConfig();
            config.setSplitType(OrderSplitEnum.ORDER_SPLIT_TYPE_3.getKey());
            config.setSplitRule(OrderSplitEnum.ORDER_SPLIT_RULE_1.getKey());
    
            List<Double> list = new ArrayList<>();
            for (String je : parseJeArray) {
                Double valueOf;
                try {
                    valueOf = Double.valueOf(je);
                } catch (NumberFormatException e) {
                    log.error("拆分输入的数量解析异常，异常信息：{}", e);
                    throw new OrderSplitException(ConfigureConstant.STRING_9999, "输入的数量必须是正整数");
                }
                list.add(valueOf);
            }
    		config.setSlList(list);
    		orderSplit = OrderSplitUtil.orderSplit(comm, config);
    	}
    	//重新设置拆分后的订单号
    	int i = 1;
    	for(CommonOrderInfo com : orderSplit) {
            StringBuilder sb = new StringBuilder();
            String cfDdh = sb.append(com.getOrderInfo().getDdh()).append(CF).append(DECIMAL_FORMAT.format(i)).toString();
            com.getOrderInfo().setDdh(CommonUtils.dealDdh(cfDdh));
            i++;
        }
        return orderSplit;
        
    }
    
    /**
     * 保存明细拆分后的订单的接口
     *
     * @throws OrderReceiveException
     */
    @Override
    public void saveOrderSplitOrder(List<PageCommonOrderInfo> commonList) throws OrderReceiveException {
        List<CommonOrderInfo> list = new ArrayList<>();
        String oldDdh = commonList.get(0).getOrderInfo().getDdh();
		int i = 1;
		NumberFormat num = NumberFormat.getPercentInstance();
		num.setMaximumIntegerDigits(3);
		num.setMaximumFractionDigits(2);
        for (PageCommonOrderInfo pageCommonOrderInfo : commonList) {
            CommonOrderInfo commonOrder = new CommonOrderInfo();
            OrderInfo orderInfo = new OrderInfo();
            List<OrderItemInfo> orderItemList = new ArrayList<>();
            for (PageOrderItemInfo pageOrderItemInfo : pageCommonOrderInfo.getOrderItemInfo()) {
                OrderItemInfo orderItemInfo = new OrderItemInfo();
                BeanUtils.copyProperties(pageOrderItemInfo, orderItemInfo);
                orderItemList.add(orderItemInfo);
    
            }
            orderInfo.setId(pageCommonOrderInfo.getOrderInfo().getId());
            orderInfo.setProcessId(pageCommonOrderInfo.getOrderInfo().getProcessId());
            orderInfo.setKphjje(pageCommonOrderInfo.getOrderInfo().getKphjje());
            orderInfo.setStatus(pageCommonOrderInfo.getOrderInfo().getStatus());
    
    
            String qdbz = CommonUtils.getQdbz(orderInfo.getQdBz(), orderItemList.size());
            if ("1".equals(qdbz) && StringUtils.isBlank(orderInfo.getQdXmmc())) {
                orderInfo.setQdXmmc(ConfigureConstant.XJXHQD);
            } else if ("0".equals(qdbz) && StringUtils.isNotBlank(orderInfo.getQdXmmc())) {
                orderInfo.setQdXmmc("");
        
            }
            orderInfo.setQdBz(qdbz);
    
            StringBuilder sb = new StringBuilder();
            String cfDdh = sb.append(oldDdh).append(CF).append(DECIMAL_FORMAT.format(i)).toString();
            orderInfo.setDdh(CommonUtils.dealDdh(cfDdh));
            orderInfo.setXhfNsrsbh(pageCommonOrderInfo.getOrderInfo().getXhfNsrsbh());
            commonOrder.setOrderItemInfo(orderItemList);
            commonOrder.setOrderInfo(orderInfo);
            list.add(commonOrder);
            i++;
        }
        
        //重算金额
        rebuildCommonOrderInfo(list);
        /**
         * 以后添加订单拆分后保存的方法
         */
        saveOrderSplitInfo(list, ConfigureConstant.STRING_0);
    }
    
    
    public void saveOrderSplitInfo(List<CommonOrderInfo> commonList, String type) throws OrderReceiveException {
    
        String parentOrderId = commonList.get(0).getOrderInfo().getId();
        List<String> shList = new ArrayList<>();
        shList.add(commonList.get(0).getOrderInfo().getXhfNsrsbh());
        OrderInfo orderInfo1 = apiOrderInfoService.selectOrderInfoByOrderId(parentOrderId, shList);
        String parentProcessId = orderInfo1.getProcessId();
    
        List<CommonOrderInfo> resultList = new ArrayList<>();
        for (CommonOrderInfo commonOrderInfo : commonList) {
            /**
             * 添加校验,防止重复拆分,先从数据库开始判断,后期添加redis
             * 根据订单处理表id获取处理表原始订单数据,看下订单删除状态是否为已删除,如果已删除返回异常.
             */
            OrderProcessInfo orderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(commonOrderInfo.getOrderInfo().getProcessId(), shList);
            if (ObjectUtil.isNotNull(orderProcessInfo) && ConfigureConstant.STRING_1.equals(orderProcessInfo.getOrderStatus())) {
                log.error("{}该数据已经被删除,请求数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonList));
                throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_ERROR1);
            }
            OrderInfo orderInfo = new OrderInfo();
            if (ConfigureConstant.STRING_0.equals(type)) {
                BeanUtils.copyProperties(orderInfo1, orderInfo);
                orderInfo.setDdh(commonOrderInfo.getOrderInfo().getDdh());
                orderInfo.setKphjje(commonOrderInfo.getOrderInfo().getKphjje());
                orderInfo.setStatus(commonOrderInfo.getOrderInfo().getStatus());
                orderInfo.setHjbhsje(commonOrderInfo.getOrderInfo().getHjbhsje());
                orderInfo.setHjse(commonOrderInfo.getOrderInfo().getHjse());
            } else {
                BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
            }
            orderInfo.setFpqqlsh("");
    
            CommonOrderInfo resultCommon = new CommonOrderInfo();
            if (orderInfo.getStatus() != null && ConfigureConstant.STRING_0.equals(orderInfo.getStatus())) {
                continue;
            }
    
            //重置清单标志
            String qdbz = CommonUtils.getQdbz(commonOrderInfo.getOrderInfo().getQdBz(), commonOrderInfo.getOrderItemInfo().size());
            commonOrderInfo.getOrderInfo().setQdBz(qdbz);
            if("1".equals(qdbz) && StringUtils.isBlank(orderInfo.getQdXmmc())){
                orderInfo.setQdXmmc(ConfigureConstant.XJXHQD);
            }else if("0".equals(qdbz) && StringUtils.isNotBlank(orderInfo.getQdXmmc())){
                orderInfo.setQdXmmc("");

            }
            orderInfo.setKphjje(FORMAT.format(Double.valueOf(orderInfo.getKphjje())));
            orderInfo.setYwlxId(orderInfo1.getYwlxId());
            orderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
            resultCommon.setOrderInfo(orderInfo);
            resultCommon.setOrderItemInfo(commonOrderInfo.getOrderItemInfo());
            resultCommon.setOriginOrderId(parentOrderId);
            resultCommon.setOriginProcessId(parentProcessId);
            resultList.add(resultCommon);
        }
    
        iGenerateReadyOpenOrderService.saveOrderSplitInfo(resultList);
    
    }
    
    private void rebuildCommonOrderInfo(List<CommonOrderInfo> comms) {
    
        // 重新计算金额和税额
    	for(CommonOrderInfo comm : comms){
    		Double hjje = 0.00;
    		Double hjse = 0.00;
    		Double jshj = 0.00;
    		for (OrderItemInfo orderItem : comm.getOrderItemInfo()) {
    			if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItem.getHsbz())) {
                    hjje = new BigDecimal(hjje).add(new BigDecimal(orderItem.getXmje()))
                            .setScale(2, RoundingMode.HALF_UP).doubleValue();
                    hjse = new BigDecimal(hjse).add(new BigDecimal(orderItem.getSe())).setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                } else {
                    jshj = new BigDecimal(jshj).add(new BigDecimal(orderItem.getXmje()))
                            .setScale(2, RoundingMode.HALF_UP).doubleValue();

    			}
    		}
    		if (OrderInfoEnum.HSBZ_0.getKey().equals(comm.getOrderItemInfo().get(0).getHsbz())) {
                jshj = new BigDecimal(hjje).add(new BigDecimal(hjse)).setScale(2, RoundingMode.HALF_UP).doubleValue();
                comm.getOrderInfo().setHjbhsje(FORMAT.format(hjje));
                comm.getOrderInfo().setKphjje(FORMAT.format(jshj));
                comm.getOrderInfo().setHjse(FORMAT.format(hjse));
            } else {
                comm.getOrderInfo().setKphjje(FORMAT.format(jshj));
            }
    		
    	}
		
	}
	


}
