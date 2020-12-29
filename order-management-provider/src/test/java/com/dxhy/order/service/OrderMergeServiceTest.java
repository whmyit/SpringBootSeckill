/*package com.dxhy.order.service;

import com.dxhy.order.ServiceStarter;
import com.dxhy.order.api.ApiOrderMergeService;
import com.dxhy.order.api.PriceTaxSeparationService;
import com.dxhy.order.api.ValidateOrderInfo;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManaementConstant;
import com.dxhy.order.constant.OrderMergeConfig;
import com.dxhy.order.constant.TaxSeparateConfig;
import com.dxhy.order.dao.OrderInfoMapper;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*//**
 * @author ：杨士勇
 * @ClassName ：OrderSplitServiceTest
 * @Description ：订单拆分测试
 * @date ：2018年10月22日 下午5:10:37
 *//*

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceStarter.class)
@WebAppConfiguration
@Slf4j
public class OrderMergeServiceTest {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemInfoMapper orderItemInfoMapper;

    @Autowired
    private ValidateOrderInfo validateOrderInfo;

    @Autowired
    private PriceTaxSeparationService priceTaxSeparationService;

    @Autowired
    private ApiOrderMergeService apiOrderMergeService;
    
    
    //不合并同类明细项含税合并
    @Test
    public void testSplitSlHs() throws Exception {
    
        String[] strList = new String[]{"201907081427261148116360644132864"};
         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
         double zje = 0.00;
         for(String str : strList){
         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
             CommonOrderInfo common = new CommonOrderInfo();
             common.setOrderInfo(selectByPrimaryKey);
             common.setOrderItemInfo(selectByOrderId);
             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
             paramList.add(common);
         }
    
        CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList, new OrderMergeConfig());
    
        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
        	 log.error("合并的价税合计金额错误");
         }
    
        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
    
         }
    
    
    }
    //不合并同类明细项不含税合并
    @Test
    public void testSplitBhs() throws Exception {
    
        String[] strList = new String[]{"201907081427261148116360644132864"};
         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
         double zje = 0.00;
         for(String str : strList){
         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
             CommonOrderInfo common = new CommonOrderInfo();
             common.setOrderInfo(selectByPrimaryKey);
             common.setOrderItemInfo(selectByOrderId);
             common = priceTaxSeparationService.taxSeparationService(common, new TaxSeparateConfig());
             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
             paramList.add(common);
         }
    
        CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList, new OrderMergeConfig());
    
        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
        	 log.error("合并的价税合计金额错误");
         }
    
        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
    
         }

    }
    
    //合并同类明细项含税合并
    @Test
    public void testSplitJeHs() throws Exception {
    	 String[] strList = new String[]{"201907081427261148116360644132864"};
         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
         double zje = 0.00;
         for(String str : strList){
         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
             CommonOrderInfo common = new CommonOrderInfo();
             common.setOrderInfo(selectByPrimaryKey);
             common.setOrderItemInfo(selectByOrderId);
             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
             paramList.add(common);
         }
         OrderMergeConfig config = new OrderMergeConfig();
         config.setIsMergeSameItem("0");
         CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList, config);
    
        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
        	 log.error("合并的价税合计金额错误");
         }
    
        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
    
         }

    }
    
    //合并同类明细项不含税合并
    @Test
    public void testSplitJeBhs() throws Exception {
    
        String[] strList = new String[]{"201907081427261148116360644132864"};
         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
         double zje = 0.00;
         for(String str : strList){
         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
             CommonOrderInfo common = new CommonOrderInfo();
             common.setOrderInfo(selectByPrimaryKey);
             common.setOrderItemInfo(selectByOrderId);
             common = priceTaxSeparationService.taxSeparationService(common, new TaxSeparateConfig());
             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
             paramList.add(common);
         }
         OrderMergeConfig config = new OrderMergeConfig();
         config.setIsMergeSameItem("0");
         CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList,config);
    
        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
        	 log.error("合并的价税合计金额错误");
         }
    
        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
    
         }

    }
}
*/