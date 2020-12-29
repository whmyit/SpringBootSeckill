//package com.dxhy.order.service;
//
//
//import com.dxhy.order.ServiceStarter;
//import com.dxhy.order.api.ValidateOrderInfo;
//import com.dxhy.order.constant.OrderInfoContentEnum;
//import com.dxhy.order.constant.OrderManagementConstant;
//import com.dxhy.order.constant.OrderSplitConfig;
//import com.dxhy.order.dao.OrderInfoMapper;
//import com.dxhy.order.dao.OrderItemInfoMapper;
//import com.dxhy.order.model.CommonOrderInfo;
//import com.dxhy.order.model.OrderInfo;
//import com.dxhy.order.model.OrderItemInfo;
//import com.dxhy.order.utils.OrderSplitUtil;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author ：杨士勇
// * @ClassName ：OrderSplitServiceTest
// * @Description ：订单拆分测试
// * @date ：2018年10月22日 下午5:10:37
// */
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ServiceStarter.class)
//@WebAppConfiguration
//@Slf4j
//public class OrderSplitServiceTest {
//
//    @Autowired
//    private OrderInfoMapper orderInfoMapper;
//
//    @Autowired
//    private OrderItemInfoMapper orderItemInfoMapper;
//
//    @Autowired
//    private ValidateOrderInfo validateOrderInfo;
//
//
//
//    //不合并同类明细项含税合并
//	@Test
//	public void testSplitSlHs() throws Exception {
//
//		OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId("405535759191592960");
//		List<OrderItemInfo> selectByOrderId = orderItemInfoMapper
//				.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
//		CommonOrderInfo common = new CommonOrderInfo();
//		common.setOrderInfo(selectByPrimaryKey);
//		common.setOrderItemInfo(selectByOrderId);
//		OrderSplitConfig splitConfig = new OrderSplitConfig();
//		splitConfig.setLimitJe(99999.99);
//		splitConfig.setSplitType("1");
//		splitConfig.setSplitRule("0");
//		List<CommonOrderInfo> orderSplit = OrderSplitUtil.orderSplit(common, splitConfig);
//
//		for(CommonOrderInfo com : orderSplit){
//			Map<String, String> checkInvoiceData = validateOrderInfo.checkInvoiceData(com);
//			if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkInvoiceData.get(OrderManagementConstant.ERRORCODE))){
//	        	 log.error("合并后的订单校验失败:{}",checkInvoiceData.get(OrderManagementConstant.ERRORMESSAGE));
//
//	         }
//		}
//
//		String str = "{\"DDH\":\"81819989066096384495\",\"EWM\":\"iVBORw0KGgoAAAANSUhEUgAAAJQAAACUCAIAAAD6XpeDAAAC90lEQVR42u3aS47bMBAFQN3/0pNtNgGG4ntsOi4tDdkQu2ioP3yewvXz1/Wvz39zrf7m6rOtfndnjc+nXPDgwYP3JXg/G1c7QO0NsfrdnY2VijM8ePDgwcvgtV/aWw9aSGTam6kRZ3jw4MGDdxfeyUBMJVPw4MGDBw9esrnchoQHDx48eN+Ld8PgdKqJ3G4of8xUAR48ePD+Y7xGA9fnh4IODx4kePDiiUA7kWm8zNvPNhZbePDgwYP3er3txu5O4dkorhvAJw87wYMHDx68zFmbxvCzDd9OKE4OnCv/Qnjw4MH7crzUIlMHiqYK6tsOWf3qHnjw4MGD9/qeRpF7W/JycmOdbKzDgwcPHrz3eI1CuDEsbSQgJwv22OaGBw8ePHjNeeHTeKCp32kkSic3Hzx48ODBW2tMt5uwjeK0cU9jg6ZQ4cGDBw9eBi/VqL0tWDvJy82/Aw8ePHjw8niVAzOFAWYbeyvQjcY0PHjw4MF7jdc4JLMT9NT9jcZxozG9nOjBgwcPHrx0L/Q5mchMfd5eezsJggcPHjx4+YI9VXimmtGpHToVdHjw4MGDdw5vqihuD2ZTxe9HNqbhwYMHD95SknIzZHse2S7SK1MFePDgwYP3ehjbCGgbcge1nbBUNhk8ePDgwYvg3bzIBvDUJqg0puHBgwcPXqRJPbX4nUCcvH+q2Q0PHjx48OYPIKUWPBaUwlC30uCGBw8ePHiv53lTQ9eTG6i9Ieqo8ODBgwcvgtcokNvFfqMRfHJzLCdK8ODBgwfvbX15NOloJAU7107CMvXM8ODBgwcv/xK+4ZDSbUGfSrJiHRZ48ODB+0K8qZd/44V/w5C2vV548ODBg5fBaycm7eQi9cwN+PZQFx48ePDgvcdrJCYnE5+Tg+Wp9cKDBw8evLvw2gPbFFhqDroTn60EBx48ePDgXYV3QxJxstBuJ1bw4MGDBy+D1z5o1IBvbMRUfOrrggcPHjx4iXXFGqypQjiVdDQGuScbBfDgwYMHb+n6A4QQuSRXMxC+AAAAAElFTkSuQmCC\",\"FPLB\":\"\",\"FPLX\":\"0\",\"FPQQLSH\":\"cateaii1t63kqi2je001\",\"FPQQPCH\":\"cateaii1t63kqi2je\",\"FP_DM\":\"011002680026\",\"FP_HM\":\"00010009\",\"FWM\":\"004<234>++3+20*/*113-892007*5883+578723+9154>57+0+29+38+4>65***63/->68*>540*757232*-24+46-2316954801/<44196/*<89\",\"HJBHSJE\":10.09,\"JQBH\":\"237000120420\",\"JYM\":\"09040543445295568474\",\"KPHJSE\":0.91,\"KPRQ\":\"2020-04-10 17:58:28\",\"PDF_URL\":\"null\",\"STATUSCODE\":\"2001\",\"STATUSMSG\":\"9992-签章失败：com.aisino.certreq.SignException: status: 297 message: 0X129证书已过期\"}";
//
//	}
//    //不合并同类明细项不含税合并
//   /* @Test
//    public void testSplitBhs() throws Exception {
//
//        String[] strList = new String[]{"201907081427261148116360644132864"};
//         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
//         double zje = 0.00;
//         for(String str : strList){
//         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
//             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
//             CommonOrderInfo common = new CommonOrderInfo();
//             common.setOrderInfo(selectByPrimaryKey);
//             common.setOrderItemInfo(selectByOrderId);
//             common = priceTaxSeparationService.taxSeparationService(common, new TaxSeparateConfig());
//             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
//             paramList.add(common);
//         }
//
//        CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList, new OrderMergeConfig());
//
//        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
//        	 log.error("合并的价税合计金额错误");
//         }
//
//        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
//         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
//        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
//
//         }
//
//    }
//
//    //合并同类明细项含税合并
//    @Test
//    public void testSplitJeHs() throws Exception {
//    	 String[] strList = new String[]{"201907081427261148116360644132864"};
//         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
//         double zje = 0.00;
//         for(String str : strList){
//         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
//             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
//             CommonOrderInfo common = new CommonOrderInfo();
//             common.setOrderInfo(selectByPrimaryKey);
//             common.setOrderItemInfo(selectByOrderId);
//             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
//             paramList.add(common);
//         }
//         OrderMergeConfig config = new OrderMergeConfig();
//         config.setIsMergeSameItem("0");
//         CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList, config);
//
//        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
//        	 log.error("合并的价税合计金额错误");
//         }
//
//        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
//         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
//        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
//
//         }
//
//    }
//
//    //合并同类明细项不含税合并
//    @Test
//    public void testSplitJeBhs() throws Exception {
//
//        String[] strList = new String[]{"201907081427261148116360644132864"};
//         List<CommonOrderInfo> paramList = new ArrayList<CommonOrderInfo>();
//         double zje = 0.00;
//         for(String str : strList){
//         	OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId( str);
//             List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId(selectByPrimaryKey.getId());
//             CommonOrderInfo common = new CommonOrderInfo();
//             common.setOrderInfo(selectByPrimaryKey);
//             common.setOrderItemInfo(selectByOrderId);
//             common = priceTaxSeparationService.taxSeparationService(common, new TaxSeparateConfig());
//             zje = MathUtil.add(new BigDecimal(common.getOrderInfo().getKphjje()), new BigDecimal(zje));
//             paramList.add(common);
//         }
//         OrderMergeConfig config = new OrderMergeConfig();
//         config.setIsMergeSameItem("0");
//         CommonOrderInfo orderMerge = apiOrderMergeService.orderMerge(paramList,config);
//
//        if(zje != Double.valueOf(orderMerge.getOrderInfo().getKphjje())){
//        	 log.error("合并的价税合计金额错误");
//         }
//
//        Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(orderMerge);
//         if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkOrderInvoice.get(OrderManaementConstant.ERRORCODE))){
//        	 log.error("合并后的订单校验失败:{}",checkOrderInvoice.get(OrderManaementConstant.ERRORMESSAGE));
//
//         }
//
//    }*/
//}
