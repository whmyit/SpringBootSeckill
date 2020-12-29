package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiPartInvoiceService;
import com.dxhy.order.api.ApiRushRedInvoiceRequestInfoService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.modules.invoice.service.C48RedInvoiceService;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import com.dxhy.order.utils.StringConvertUtils;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述信息：纸质发票Service实现类
 *
 * @author 谢元强
 * @date Created on 2018-08-17
 */
@Service
@Slf4j
public class C48RedInvoiceServiceImpl implements C48RedInvoiceService {
    
    private static final String LOGGER_MSG = "纸质部分冲红发票实现类";
    
    @Reference
    private ApiRushRedInvoiceRequestInfoService apiRushRedInvoiceRequestInfoService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiPartInvoiceService apiPartInvoiceService;
	
	/**
	 * 红票信息处理
	 *
	 * @param invoiceCode 发票代码
	 * @param invoiceNo   发票号码
	 * @return
	 */
	@Override
	public Map<String, Object> mergeSpecialInvoiceAndReversal(String invoiceCode, String invoiceNo, List<String> shList) {
		
		/**
		 * 查询蓝票数据
		 *
		 */
		
		try {
			// 查询发票信息
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, shList);
			
			/**
			 * 蓝票金额转换成负数金额显示 蓝票明细金额转换为相反金额
			 */
			if (null != commonOrderInvoiceAndOrderMxInfo && commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo() != null) {
				
				String kpzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKpzt();
				// 判断开票状态是否为开票成功
				if (!OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
					log.info("{}发票代码{} 发票号码{} 的发票开具异常!", LOGGER_MSG, invoiceCode, invoiceNo);
                    return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                            .put(ConfigureConstant.MSG, "该发票开具异常");
                }
                
                /**
                 * 折扣行合并后统一返回含税金额明细行数据 todo,后期优化
                 */
                
                CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
                OrderInfo sourceOrderInfo = commonOrderInvoiceAndOrderMxInfo.getOrderInfo();
                sourceOrderInfo.setHjbhsje("-" + sourceOrderInfo.getHjbhsje());
                sourceOrderInfo.setHjse("-" + sourceOrderInfo.getHjse());
                sourceOrderInfo.setKphjje("-" + sourceOrderInfo.getKphjje());
                sourceOrderInfo.setKplx(OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey());
				commonOrderInfo.setOrderInfo(sourceOrderInfo);
                List<OrderItemInfo> orderItems = commonOrderInvoiceAndOrderMxInfo.getOrderItemList();
                for (OrderItemInfo orderItemInfo : orderItems) {
                    // 折扣行
                    if (OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())) {
                        orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getXmje()).abs().toString(), ConfigureConstant.INT_2));
                        orderItemInfo.setSe(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getSe()).abs().toString(), ConfigureConstant.INT_2));
                        if (!StringUtils.isBlank(orderItemInfo.getXmsl())) {
                            orderItemInfo.setXmsl(StringConvertUtils
                                    .removeLastZero(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getXmsl()).abs().toString(), ConfigureConstant.INT_8)));
                        }
                        if (!StringUtils.isBlank(orderItemInfo.getXmdj())) {
                            orderItemInfo.setXmdj(StringConvertUtils
                                    .removeLastZero(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getXmdj()).abs().toString(), ConfigureConstant.INT_8)));
                        }
                        // 正常行 被折扣行
                    } else {
                        orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getXmje()).negate().toString(), ConfigureConstant.INT_2));
                        orderItemInfo.setSe(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getSe()).negate().toString(), ConfigureConstant.INT_2));
                        if (!StringUtils.isBlank(orderItemInfo.getXmsl())) {
                            orderItemInfo.setXmsl(StringConvertUtils
                                    .removeLastZero(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getXmsl()).negate().toString(), ConfigureConstant.INT_8)));
                        }
        
                    }
                    if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
                        orderItemInfo.setKce(DecimalCalculateUtil.decimalFormatToString(new BigDecimal(orderItemInfo.getKce()).negate().toString(), ConfigureConstant.INT_2));
        
                    }
                }
				commonOrderInfo.setOrderItemInfo(orderItems);

				/**
				 * 数据需要先进行价税分离才可以使用原来的红票合并折扣行代码进行判断
				 */
				TaxSeparateConfig config = new TaxSeparateConfig();
				config.setDealSeType("1");
				config.setSingleSlSeparateType("2");
				commonOrderInfo = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, config);

				/**
				 * 合并折扣行
				 */
				log.info("调用订单系统合并明细行接口 参数" + JsonUtils.getInstance().toJsonString(commonOrderInfo));
				Map<String, Object> volidateOrder = apiRushRedInvoiceRequestInfoService.itemMerge(commonOrderInfo);
				log.info("调用订单系统合并明细行接口 结果" + JsonUtils.getInstance().toJsonString(volidateOrder));
				if (OrderInfoContentEnum.SUCCESS.getKey().equals(volidateOrder.get(OrderManagementConstant.ERRORCODE))) {
					commonOrderInfo = (CommonOrderInfo) volidateOrder.get("data");
					commonOrderInfo.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_3.getKey());

					/**
					 * 修改红票扣除额逻辑处理
					 */
					if (commonOrderInfo.getOrderItemInfo().size() > 0 && StringUtils.isNotBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce())) {
						String kce = commonOrderInfo.getOrderItemInfo().get(0).getKce().replace("-", "");
						commonOrderInfo.getOrderInfo().setBz(commonOrderInfo.getOrderInfo().getBz().replace(kce, "-" + kce));
					}
					commonOrderInvoiceAndOrderMxInfo.setOrderInfo(commonOrderInfo.getOrderInfo());
					commonOrderInvoiceAndOrderMxInfo.setOrderItemList(commonOrderInfo.getOrderItemInfo());
				} else {
                    return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                            .put(ConfigureConstant.MSG, "合并商品折扣行失败");
                }

				/**
				 * 根据是否冲红过展示明细信息
				 */
				orderItems = commonOrderInvoiceAndOrderMxInfo.getOrderItemList();
				BigDecimal sykchje = new BigDecimal(
						commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getSykchje());
				BigDecimal kpje = new BigDecimal(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getKphjje());
				// 判断发票冲红标志
				if (sykchje.abs().compareTo(kpje.abs()) == 0) {
					log.info("发票目前没有成功冲过红  不需要进行明细加减");

					// 成功后返回数据
					return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
							.put(ConfigureConstant.MSG, OrderInfoContentEnum.SUCCESS.getMessage())
							.put(OrderManagementConstant.DATA, commonOrderInvoiceAndOrderMxInfo);
					
				} else {
					log.info("该发票剩余可冲红金额{} 开票金额{}", sykchje, kpje);
					// 可冲红金额判断
					if (sykchje.compareTo(BigDecimal.ZERO) == 0) {
                        log.info("发票可冲红金额为0，发票代码：{},发票号码:{}", invoiceCode, invoiceNo);
                        return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
		                        .put(ConfigureConstant.MSG, "该发票剩余可冲红金额为零，不可以进行冲红");
					}
					// 可冲红金额 与开票金额不相等 说明已经进行过冲红，所以必须查到一冲红明细
					// 获取红票订单明细
					/**
					 * 1.根据发票代码号码获取红票明细数据 2.用原来票明细数据循环和红票数据做对比.
					 * 3.如果当前蓝票明细行和要对比的红票明细行主要商品数据一致
					 * 4.进行判断蓝票明细行金额是否小于红票明细行金额,如果小于则跳过,继续查找能够匹配的商品行
					 * 5.如果当前蓝票明细行数据等于红票明细行金额,则剔除蓝票明细行数据 6.如果
					 */
					List<OrderItemInfo> orderList = apiPartInvoiceService.partInvoiceQueryList(invoiceCode, invoiceNo, shList);
					if (CollectionUtils.isNotEmpty(orderList)) {
                        List<OrderItemInfo> newOrderList = new ArrayList<>();
                        for (OrderItemInfo item : orderList) {
                             newOrderList = removeAlreadyRedItem(orderItems, item);
                        }
						commonOrderInvoiceAndOrderMxInfo.setOrderItemList(newOrderList);
						return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
								.put(ConfigureConstant.MSG, OrderInfoContentEnum.SUCCESS.getMessage())
								.put(OrderManagementConstant.DATA, commonOrderInvoiceAndOrderMxInfo);
						
					} else {
                        log.error("查询该发票已冲红项目明细为空！发票代码：{}，发票号码：{}", invoiceCode, invoiceNo);
                        return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                                .put(ConfigureConstant.MSG, "查询发票已冲红明细失败，不可以进行冲红");
                    }
				}
			} else {
                log.error("查询蓝票信息失败！发票代码：{}，发票号码：{}", invoiceCode, invoiceNo);
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                        .put(ConfigureConstant.MSG, "查询发票信息失败");
            }
		} catch (Exception e) {
            log.error("查询发票信息异常！发票代码：{}，发票号码：{},异常信息:{}", invoiceCode, invoiceNo, e);
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                    .put(ConfigureConstant.MSG, "查询发票信息失败");
        }
	}

	/**
     * @param @param  orderItems
     * @param @param  orderList
     * @param @return
     * @return List<OrderItemInfo>
     * @throws
     * @Title : removeAlreadyRedItem
     * @Description ：通过递归调用去除 已冲红的明细信息
     */
	
	private List<OrderItemInfo> removeAlreadyRedItem(List<OrderItemInfo> orderItems, OrderItemInfo orderItem) {
		int j = 0;
			for (int i = 0; i < orderItems.size(); i++) {
				OrderItemInfo redOrder = orderItems.get(i);
				if (isEquality(redOrder, orderItem)) {
					BigDecimal xmsl = new BigDecimal(0);
					if (!StringUtils.isBlank(redOrder.getXmsl())) {
						xmsl = new BigDecimal(redOrder.getXmsl());
					}
					BigDecimal xmje = new BigDecimal(redOrder.getXmje());
					BigDecimal se = new BigDecimal(redOrder.getSe());

					BigDecimal rexmje = new BigDecimal(orderItem.getXmje());
					BigDecimal rese = new BigDecimal(orderItem.getSe());
					if ((xmje.abs().add(se.abs())).compareTo(rexmje.abs().add(rese.abs())) < 0) {
						continue;
					}
					
					if (!StringUtils.isBlank(redOrder.getXmsl())) {
						BigDecimal rexmsl = new BigDecimal(orderItem.getXmsl());
						xmsl = xmsl.abs().subtract(rexmsl.abs());
					}
					xmje = xmje.abs().subtract(rexmje.abs());
					se = se.abs().subtract(rese.abs());
					
					if (!StringUtils.isBlank(redOrder.getXmsl())) {
						redOrder.setXmsl(StringConvertUtils.removeLastZero("-" + DecimalCalculateUtil.decimalFormatToString(xmsl.abs().toString(), ConfigureConstant.INT_8)));
					}
					
					redOrder.setXmje("-" + DecimalCalculateUtil.decimalFormatToString(xmje.abs().toString(), ConfigureConstant.INT_2));
					redOrder.setSe("-" + DecimalCalculateUtil.decimalFormatToString(se.abs().toString(), ConfigureConstant.INT_2));
					orderItems.remove(i);
					if (BigDecimal.ZERO.doubleValue() != xmje.doubleValue()) {
						orderItems.add(i, redOrder);
					}
					break;
				}
				
			}
		
		return orderItems;
	}

	/**
	 * @Description 判断明细行是否为同一行
	 * @Author xieyuanqiang
	 * @Date 15:42 2018-11-07
	 */
	private boolean isEquality(OrderItemInfo orderItemInfo, OrderItemInfo redOrder) {
		StringBuilder key = new StringBuilder(StringUtil.isEmpty(orderItemInfo.getSpbm()) ? "" : orderItemInfo.getSpbm())
				.append(StringUtil.isEmpty(orderItemInfo.getXmmc()) ? "" : orderItemInfo.getXmmc())
				.append(StringUtil.isEmpty(orderItemInfo.getGgxh()) ? "" : orderItemInfo.getGgxh())
				.append(StringUtil.isEmpty(orderItemInfo.getXmdw()) ? "" : orderItemInfo.getXmdw());
		StringBuilder redkey = new StringBuilder(StringUtil.isEmpty(redOrder.getSpbm()) ? "" : redOrder.getSpbm())
				.append(StringUtil.isEmpty(redOrder.getXmmc()) ? "" : redOrder.getXmmc())
				.append(StringUtil.isEmpty(redOrder.getGgxh()) ? "" : redOrder.getGgxh())
				.append(StringUtil.isEmpty(redOrder.getXmdw()) ? "" : redOrder.getXmdw());
		boolean reslut = key.toString().equals(redkey.toString());
		return reslut;
	}
	
}
