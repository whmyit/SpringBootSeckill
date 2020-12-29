package com.dxhy.order.utils;

import com.dxhy.order.constant.*;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @ClassName ：ApiOrderSplitServiceImpl1
 * @Description ：订单拆分接口
 * @author ：杨士勇
 * @date ：2019年9月10日 下午4:07:21
 *
 *
 */

@Slf4j
public class OrderSplitUtil {
	
	private static final String LOG_MSG = "(订单拆分)";

	/**
     * @throws OrderSplitException
     * @Title : orderSplit @Description
     * ：拆分中所有的涉及到总金额的计算均已明细中的金额为准 @param @param commonOrderInfo @param @param
     * orderSplitConfig @param @return @param @throws Exception @return
     * List<CommonOrderInfo> @exception
     */
	public static List<CommonOrderInfo> orderSplit(CommonOrderInfo commonOrderInfo, OrderSplitConfig orderSplitConfig) throws OrderSplitException {
		log.debug("订单拆分接口，入参,订单信息:{},配置参数信息:{}", JsonUtils.getInstance().toJsonString(commonOrderInfo),
				JsonUtils.getInstance().toJsonString(orderSplitConfig));
		
		
		double kphjje = Double.parseDouble(commonOrderInfo.getOrderInfo().getKphjje());
		
		List<CommonOrderInfo> commnList = new ArrayList<>();
		// 拆分订单信息校验
		checkCommonOrderInfo(commonOrderInfo);
		
		// 拆分参数，配置信息校验
		checkParam(orderSplitConfig, commonOrderInfo);
		
		
		//复制对象 不改变入参
		CommonOrderInfo paramCommonOrder = new CommonOrderInfo();
		BeanUtils.copyProperties(commonOrderInfo, paramCommonOrder);
		
		OrderInfo paramOrder = new OrderInfo();
		BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), paramOrder);
		paramCommonOrder.setOrderInfo(paramOrder);
		
		List<OrderItemInfo> orderItemList = new ArrayList<>();
		for (OrderItemInfo orderItemInfo : commonOrderInfo.getOrderItemInfo()) {
			OrderItemInfo paramOrderItem = new OrderItemInfo();
			BeanUtils.copyProperties(orderItemInfo, paramOrderItem);
			orderItemList.add(paramOrderItem);
		}
		paramCommonOrder.setOrderItemInfo(orderItemList);
		
		// 根据拆分方式拆分订单
		if (OrderSplitEnum.ORDER_SPLIT_TYPE_1.getKey().equals(orderSplitConfig.getSplitType())) {
			// 金额超限额拆分
			commnList = orderSplitForOverLimit(paramCommonOrder, orderSplitConfig.getSplitRule(),
					orderSplitConfig.getLimitJe().toString());
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_2.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按金额拆分
			commnList = orderSplitByJeArray(paramCommonOrder, orderSplitConfig.getSplitRule(),
					orderSplitConfig.getJeList());
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_3.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按数量拆分
			commnList = orderSplitBySlArray(paramCommonOrder, orderSplitConfig.getSplitRule(),
					orderSplitConfig.getSlList());
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_4.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按明细行拆分
			commnList = orderSplitByLimitLine(paramCommonOrder, orderSplitConfig.getLimitRang());
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_5.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按明细行拆分
			commnList = orderSplitByLineList(paramCommonOrder, orderSplitConfig.getSplitRule(),
					orderSplitConfig.getLineList());
		}
	    //格式化数量单价
	    BigDecimal cfKphjje = new BigDecimal("0.00");
		for(CommonOrderInfo comm : commnList){
			comm = formatCommonOrder(comm);
			cfKphjje = cfKphjje.add(new BigDecimal(comm.getOrderInfo().getKphjje()));
		}

		if(cfKphjje.doubleValue() != kphjje){
			log.error("拆分后的开票合计金额与拆分前的合计金额不一致!");
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_KPJE_DIFF_ERROR.getKey(),
					OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_KPJE_DIFF_ERROR.getValue());
		}
		
		log.debug("拆分接口出参:{}", JsonUtils.getInstance().toJsonString(commnList));
		return commnList;
    }
    
    /**
     * @Title : orderSplitBySlArray @Description
     * ：按数量数组拆分 @param @param commonOrderInfo @param @param
     * splitRule @param @param jeList @param @return @return
     * List<CommonOrderInfo> @exception
	 *
	 */

	private static List<CommonOrderInfo> orderSplitBySlArray(CommonOrderInfo commonOrderInfo, String splitRule,
			List<Double> slList) {
		// 判断明细是否是折扣行
		boolean isDiscountRang = false;
		if (OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(commonOrderInfo.getOrderItemInfo().get(0).getFphxz())) {
			isDiscountRang = true;
		}
		// 订单数量拆分
		List<CommonOrderInfo> dealSplitSl = dealSplitSl(slList, commonOrderInfo.getOrderInfo(),
				commonOrderInfo.getOrderItemInfo(), isDiscountRang);
		return dealSplitSl;
	}
    
    /**
     * @throws OrderSplitException @throws OrderReceiveException @Title :
     * orderSplitByJeArray @Description ：按金额数组拆分 @param @param
     * commonOrderInfo @param @param splitRule @param @param
     * jeList @param @return @return List<CommonOrderInfo> @exception
	 *
	 */
	private static List<CommonOrderInfo> orderSplitByJeArray(CommonOrderInfo commonOrderInfo, String splitRule,
			List<Double> jeList) throws OrderSplitException {
		List<CommonOrderInfo> commonList = new ArrayList<>();
		// 按照多个金额拆分
		int i = 0;
		for (Double je : jeList) {
			
			List<CommonOrderInfo> resultList;
			
			// 判断是否含税
			// 保证单价数量是正整数
			resultList = dealSplitJeBhs(je.toString(), commonOrderInfo.getOrderInfo(),
					commonOrderInfo.getOrderItemInfo());
			
			if (resultList.size() <= 0) {
				// 金额已拆分完
				commonList.addAll(resultList);
				break;
			} else {
				commonList.addAll(resultList);
				if (i == jeList.size() - 1) {
					if(commonOrderInfo.getOrderItemInfo().size() > 0){
						convertAndAdd(commonOrderInfo.getOrderInfo(), commonOrderInfo.getOrderItemInfo(), commonList);
					}
				}
			}
			i++;
		}
		rebuildCommOrderList(commonList);
		return commonList;
	}

	private static void rebuildCommOrderList(List<CommonOrderInfo> commonList) {
		for(CommonOrderInfo comm : commonList){
			CommonOrderInfo rebuildCommonOrderInfo = rebuildCommonOrderInfo(comm.getOrderInfo(),comm.getOrderItemInfo());
			comm.setOrderInfo(rebuildCommonOrderInfo.getOrderInfo());
		}
	}
    
    /**
     * @throws OrderSplitException @Title :
     * dealSplitJeBhs @Description ：不含税金额拆分
     * (需要处理税额) @param @param string @param @param orderInfo @param @param
     * orderItemInfo @param @return @return List<CommonOrderInfo> @exception
	 *
	 */

	private static List<CommonOrderInfo> dealSplitJeBhs(String je, OrderInfo orderInfo, List<OrderItemInfo> orderItemList)
			throws OrderSplitException {
		List<CommonOrderInfo> resultList = new ArrayList<>();
		
		BigDecimal hjje = new BigDecimal(ConfigureConstant.STRING_000);
		BigDecimal lastHjje = new BigDecimal(ConfigureConstant.STRING_000);
		
		List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
		
		for (int i = 0; i < orderItemList.size(); i++) {
			// 被折扣行
			if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemList.get(i).getFphxz())) {
				hjje = hjje.add(new BigDecimal(orderItemList.get(i).getXmje()))
						.add(new BigDecimal(orderItemList.get(i + 1).getXmje()));
				// 大于
				if (hjje.compareTo(new BigDecimal(je)) > 0) {
					// 如果第n个明细超限额 拆分第n条明细
					Double leaveJe = new BigDecimal(je).subtract(lastHjje).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					if (StringUtils.isNotBlank(orderItemList.get(i).getXmdj())
							&& Double.parseDouble(orderItemList.get(i).getXmdj()) > leaveJe) {
						
						if (i == 0) {
							// 如果第一条的单价大于要拆分的金额，返回拆分失败
							log.error("单价大于要拆分的金额，无法拆分");
							throw new OrderSplitException(
									OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_DJ_OVER_SPLITJE__ERROR);
						} else {
							// 如果无法拆分 前 n个明细作为一张发票
							convertAndAdd(orderInfo, orderItemInfoList, resultList);
							// 将拆分后的明细剔除出
							orderItemList.subList(0, i).clear();
							return resultList;
						}
					} else {

						List<CommonOrderInfo> overLimitSplitByJeArray = overLimitSplitByJeArray(orderInfo,
								orderItemList.get(i), BigDecimal.valueOf(leaveJe), true, orderItemList.get(i + 1));
						// 前i条和拆分后的前一部分作为一个订单
						orderItemInfoList.addAll(overLimitSplitByJeArray.get(0).getOrderItemInfo());
						convertAndAdd(orderInfo, orderItemInfoList, resultList);

						// 剩余的订单接着和下边的订单接着加和
						orderItemList.subList(0, i + 2).clear();
						orderItemList.addAll(0, overLimitSplitByJeArray.get(1).getOrderItemInfo());
						return resultList;
					}
					// 小于
				} else if (hjje.compareTo(new BigDecimal(je)) == 0) {
					orderItemInfoList.add(orderItemList.get(i));
					orderItemInfoList.add(orderItemList.get(i + 1));
					convertAndAdd(orderInfo, orderItemInfoList, resultList);

					orderItemList.subList(0, i + 2).clear();
					return resultList;
					// 等于
				} else {
					orderItemInfoList.add(orderItemList.get(i));
					orderItemInfoList.add(orderItemList.get(i + 1));
					if(i == orderItemList.size() - 2){
						orderItemList.subList(0, i + 2).clear();
						convertAndAdd(orderInfo, orderItemInfoList, resultList);
					    return resultList;
					}
					
					
				}

			} else if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemList.get(i).getFphxz())) {
				// 被折扣行
				continue;
			} else {
				hjje = hjje.add(new BigDecimal(orderItemList.get(i).getXmje()));
				if (hjje.compareTo(new BigDecimal(je)) > 0) {
					Double leaveJe = new BigDecimal(je).subtract(lastHjje).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					if (StringUtils.isNotBlank(orderItemList.get(i).getXmdj())
							&& Double.parseDouble(orderItemList.get(i).getXmdj()) > leaveJe) {
						// 如果第一条的单价大于要拆分的金额，返回拆分失败
						if (i == 0) {
							log.error("单价大于要拆分的金额，无法拆分");
							throw new OrderSplitException(
									OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_DJ_OVER_SPLITJE__ERROR);
							
						} else {
							convertAndAdd(orderInfo, orderItemInfoList, resultList);
							orderItemList.subList(0, i).clear();
							return resultList;
						}
					} else {

						List<CommonOrderInfo> overLimitSplitByJeArray = overLimitSplitByJeArray(orderInfo,
								orderItemList.get(i), BigDecimal.valueOf(leaveJe), false, null);
						// 前i条和拆分后的前一部分作为一个订单
						orderItemInfoList.addAll(overLimitSplitByJeArray.get(0).getOrderItemInfo());
						convertAndAdd(orderInfo, orderItemInfoList, resultList);
						// 剩余的订单接着和下边的订单接着加和
						orderItemList.subList(0, i + 1).clear();
						orderItemList.addAll(0, overLimitSplitByJeArray.get(1).getOrderItemInfo());
						return resultList;
					}

				} else if (hjje.compareTo(new BigDecimal(je)) == 0) {
					orderItemInfoList.add(orderItemList.get(i));
					convertAndAdd(orderInfo, orderItemInfoList, resultList);
					orderItemList.subList(0, i + 1).clear();
					return resultList;
					// 小于
				} else {
					orderItemInfoList.add(orderItemList.get(i));
					if(i == orderItemList.size() - 1){
						orderItemList.subList(0,i + 1).clear();
						convertAndAdd(orderInfo, orderItemInfoList, resultList);
						return resultList;
					}
				}
			}
			lastHjje = hjje;
		}

		return resultList;
	}
    
    /**
     * @throws OrderSplitException @Title :
     * checkCommonOrderInfo @Description :订单信息校验 @param @param
     * commonOrderInfo @return void @exception
	 *
	 */
	private static void checkCommonOrderInfo(CommonOrderInfo commonOrderInfo) throws OrderSplitException {
		

		// 校验订单主题信息是否为空
		if (commonOrderInfo == null) {
			log.error("订单明细信息为空!");
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_NULL_ERROR);

		}
		// 校验订单信息是否为空
		if (commonOrderInfo.getOrderInfo() == null) {
			log.error("订单明细信息为空!");
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_NULL_ERROR);

		}
		// 校验订单明细信息是否为空
		if (CollectionUtils.isEmpty(commonOrderInfo.getOrderItemInfo())) {
			log.error("订单明细信息为空,订单号:{}!", commonOrderInfo.getOrderInfo().getDdh());
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_NULL_ERROR);
		}
		// 订单明细信息校验
		int i = 0;
		BigDecimal hjje = new BigDecimal("0.00");

		String hsbz = commonOrderInfo.getOrderItemInfo().get(0).getHsbz();

		// 如果是不含税拆分 税额只能全部为空或者全部不为空
		boolean isContainSe = false;
		if (StringUtils.isNoneBlank(commonOrderInfo.getOrderItemInfo().get(0).getSe())) {
			isContainSe = true;
		}
		for (OrderItemInfo orderItem : commonOrderInfo.getOrderItemInfo()) {
			// 订单明细金额校验
			if (StringUtils.isBlank(orderItem.getXmje())) {
				log.error("订单号为：{}的订单，订单明细第{}行金额为空!", commonOrderInfo.getOrderInfo().getDdh(), i + 1);
				throw new OrderSplitException(
						OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR.getKey(),
						String.format(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR.getValue(), commonOrderInfo.getOrderInfo().getDdh(), i + 1));
			} else {
				hjje = hjje.add(new BigDecimal(orderItem.getXmje()));
			}
			// 判断明细的含税标志是否一致
			if (!hsbz.equals(orderItem.getHsbz())) {
				log.error("订单明细只能为全部含税或者全部不含税，请查看含税标志是否一致！");
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_HSBZ_ERROR.getKey(),
						String.format(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR.getValue(), commonOrderInfo.getOrderInfo().getDdh(), i + 1));
			}
			// 不含税拆分 税额只能全部为空或者全部不为空
			if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItem.getHsbz())) {
				if (isContainSe) {
					if (StringUtils.isBlank(orderItem.getSe())) {
						log.error("不含税拆分，明细中的税额只能全部为空或者全部都不为空!");
						throw new OrderSplitException(
								OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SE_ERROR.getKey(),
								String.format(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR.getValue(), commonOrderInfo.getOrderInfo().getDdh(), i + 1));
					}
				} else {
					if (StringUtils.isNotBlank(orderItem.getSe())) {
						log.error("不含税拆分，明细中的税额只能全部为空后者全部都不为空!");
						throw new OrderSplitException(
								OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SE_ERROR.getKey(),
								String.format(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR.getValue(), commonOrderInfo.getOrderInfo().getDdh(), i + 1));
					}
				}
			}

			i++;
		}
		// 红字发票不支持拆分
		if (hjje.doubleValue() <= 0.00) {
			log.error("订单总总金额小于0，不支持红字发票拆分!");
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_JE_LESS_ZERO_ERROR.getKey(),
					OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_JE_LESS_ZERO_ERROR.getValue());
		}
		//暂不支持扣除额发票不支持拆分
		if(commonOrderInfo.getOrderItemInfo().size() == 1 && StringUtils.isNotBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce())){
			log.error("暂不支持扣除额发票拆分!");
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_KCE_ILLEGAL.getKey(),
					OrderSplitErrorMessageEnum.ORDER_SPLIT_KCE_ILLEGAL.getValue());
		}

		
	}
    
    /**
     * @throws OrderSplitException @Title :
     * checkParamhg @Description ：拆分方式校验 @param @param
     * orderSplitConfig @return void @exception
	 *
	 */
	private static void checkParam(OrderSplitConfig orderSplitConfig, CommonOrderInfo common) throws OrderSplitException {
		// 拆分方式校验
		if (OrderSplitEnum.ORDER_SPLIT_TYPE_1.getKey().equals(orderSplitConfig.getSplitType())) {
			// 金额超限额拆分
			log.debug("订单拆分方式：{}", OrderSplitEnum.ORDER_SPLIT_TYPE_1.getValue());
			if (orderSplitConfig.getLimitJe() == null || orderSplitConfig.getLimitJe() <= 0.00) {
				log.error("拆分参数校验失败：{}", OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_LIMITJE_ERROR.getValue());
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_LIMITJE_ERROR);
			}

			// if
			// (OrderSplitEnum.ORDER_SPLIT_RULE_1.getKey().equals(orderSplitConfig.getSplitRule()))
			// {
		    //单价大于限额 无法拆分
			for (OrderItemInfo orderItem : common.getOrderItemInfo()) {
				if (StringUtils.isNotBlank(orderItem.getXmdj())
						&& Double.parseDouble(orderItem.getXmdj()) > orderSplitConfig.getLimitJe()) {
					log.error("单价大于限额，无法保证数量是整数!");
					throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_DJ_OVER_JE);
				}
			}

			// }
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_2.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按金额拆分
			log.debug("订单拆分方式：{}", OrderSplitEnum.ORDER_SPLIT_TYPE_2.getValue());
			if (CollectionUtils.isEmpty(orderSplitConfig.getJeList())) {
				log.error("拆分参数校验失败：{}", OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_JE_ERROR.getValue());
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_JE_ERROR);
			} else {
				for (double je : orderSplitConfig.getJeList()) {
					if (je <= 0.00) {
						throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_JE_ERROR);
					}
				}
			}
		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_3.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按数量拆分
			log.debug("订单拆分方式：{}", OrderSplitEnum.ORDER_SPLIT_TYPE_3.getValue());

			if (CollectionUtils.isEmpty(orderSplitConfig.getSlList())) {
				log.error("拆分参数校验失败：{}", OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR.getValue());
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR);
			} else if (common.getOrderItemInfo().size() != 1) {
				if(common.getOrderItemInfo().size() > 1 && OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(common.getOrderItemInfo().get(0).getFphxz())){
					if(common.getOrderItemInfo().size() != 2){
						log.error("数量拆分明细行数只能是1：{}", OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR.getValue());
						throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR);
					}

				}else{
					log.error("数量拆分明细行数只能是1：{}", OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR.getValue());
					throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_SL_ERROR);
				}

			} else {
				int all = 0;
				for (Double sl : orderSplitConfig.getSlList()) {
					if (sl <= 0) {
						log.error("拆分的数量只能为正整数!");
						throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_MXSL_ERROR);
					}
					all += sl;
				}
				
				if (StringUtils.isBlank(common.getOrderItemInfo().get(0).getXmsl())) {
					log.error("数量拆分，明细数量不能为空!");
					throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_MXSL_NULL_ERROR);
				}
				
				if (all > Double.parseDouble(common.getOrderItemInfo().get(0).getXmsl())) {
					log.error("数量拆分，拆分的数量不能超过明细数量!");
					throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_OVER_MXSL_ERROR);
				}
				
			}

		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_4.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按明细行拆分
			log.debug("订单拆分方式：{}", OrderSplitEnum.ORDER_SPLIT_TYPE_4.getValue());
			if (orderSplitConfig.getLimitRang() == null && orderSplitConfig.getLimitRang() <= 0) {
				log.error("按明细行拆分，拆分的明细行不能小于等于0");
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_LIMIT_RANG_ERROR);
			}

		} else if (OrderSplitEnum.ORDER_SPLIT_TYPE_5.getKey().equals(orderSplitConfig.getSplitType())) {
			// 按明细行拆分
			log.debug("订单拆分方式：{}", OrderSplitEnum.ORDER_SPLIT_TYPE_5.getValue());
			if (CollectionUtils.isEmpty(orderSplitConfig.getLineList())) {
				log.error("按明细行拆分，明细行不能为空!");
				throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_RANG_ERROR);

			} else {
				int all = 0;
				for (int line : orderSplitConfig.getLineList()) {
					if (line <= 0) {
						log.error("按明细行拆分，明细行不能小于等于0!");
						throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_RANG_ERROR);
					}
					all += line;
				}
				if (all > common.getOrderItemInfo().size()) {
					log.error("拆分的明细行，大于订单明细行");
					throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_RANG_ERROR);
				}
			}

		} else {
			log.warn("拆分方式错误，拆分方式:{}", orderSplitConfig.getSplitType());
			throw new OrderSplitException(OrderSplitErrorMessageEnum.ORDER_SPLIT_TYPE_ERROR);
		}
	}

	/**
	 * 超限额拆分
	 *
	 * 拆分规则 生成待开票订单时，判断是否超限额。若不超限额，直接开票。
	 * 若超限额，将金额按行数从上到下依次加和，若加至第n（n小于总行数）行时，小于等于发票限额；加至第n+1行时超过限额
	 * 若前n行已无限接近限额，没有再增加一行的可能，则将前n行开为一张发票。
	 * 若前n行加和后，仍与限额有一定的差距（传音侧差距一般不会大于2000元），在数量取整的基础上，拆分第n+1行，
	 * 支持拆分后的第n+1行的一部分与前n行的加和无限接近限额，数量再多拆分1个，就会超限额为止。第n+1行拆分后的剩余金额，
	 * 继续与下一行加和到下一张发票上。 后续明细行处理方式同上
	 *
	 * 超限额拆分方式分为两种 1.保证数量为整数 2.保证金额为限额
	 * @throws OrderSplitException
	 *
	 * @throws Exception
	 *
	 */
	public static List<CommonOrderInfo> orderSplitForOverLimit(CommonOrderInfo commonOrderInfo, String splitType,
			String limitJe) throws OrderSplitException {
		List<CommonOrderInfo> resultList = new ArrayList<>();
		orderSplitOverLimit(commonOrderInfo, splitType, new BigDecimal(limitJe), resultList);
		resetHjjeAndHjse(resultList, false);
		return resultList;
	}
    
    /**
     * @throws OrderSplitException
     * @Title : splitOverLimtBhs @Description ：不含税超限额拆分
     * 扣除额的逻辑还没有加 @param @param commonOrderInfo @param @param
     * splitType @param @param bigDecimal @param @return @return
     * List<CommonOrderInfo> @exception
     *
     */
	/*private static List<CommonOrderInfo> splitOverLimtBhs(CommonOrderInfo commonOrderInfo, String splitType, BigDecimal limit) throws OrderSplitException {

		List<CommonOrderInfo> commonList = new ArrayList<CommonOrderInfo>();
		List<OrderItemInfo> orderInfoList = commonOrderInfo.getOrderItemInfo();
		List<OrderItemInfo> afterOrderInfoList = new ArrayList<OrderItemInfo>();
		Double hjje = 0.00;
		Double lastHjje = 0.00;

		int i = 0;
		for (OrderItemInfo orderItemInfo : orderInfoList) {

			// 累计合计金额 判断累加的明细是否超限额
		    if(i == orderInfoList.size() - 1 ){
				hjje = getHjje(orderItemInfo,null, hjje);
		    }else{
				hjje = getHjje(orderItemInfo, orderInfoList.get(i + 1), hjje);
		    }

			if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz())) {
				// 如果是折扣行的话 跳过
				i++;
				continue;
			}

			if (hjje > limit.doubleValue()) {
				// 如果累加金额大于限额
				if (i == 0) {
					// 单条明细超限额拆分
					List<CommonOrderInfo> resultList = new ArrayList<CommonOrderInfo>();
					if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
						resultList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), orderItemInfo,
								orderInfoList.get(i + 1), limit, splitType);
					} else {
						resultList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), orderItemInfo, null,
								limit, splitType);
					}
					// 判断拆分后的最后一条订单是否等于限额 如果不等于限额继续和下面的金额累加
					String compareJe = "";
					if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())) {
						// 如果是不含税拿拆分后的不含税金额和限额比较
						compareJe = resultList.get(resultList.size() - 1).getOrderInfo().getHjbhsje();
					} else {
						// 如果是含税 拿拆分后的价税合计金额与限额比较
						compareJe = resultList.get(resultList.size() - 1).getOrderInfo().getKphjje();
					}
					if (new BigDecimal(compareJe).compareTo(limit) == 0) {
						commonList.addAll(resultList);
						hjje = 0.00;
						lastHjje = hjje;
						i++;
						continue;
					} else {
						CommonOrderInfo finalCommonOrderInfo = resultList.get(resultList.size() - 1);
						resultList.remove(resultList.size() - 1);
						commonList.addAll(resultList);
						// 拆分后最后一张的金额继续与后边的订单明细累加
						afterOrderInfoList = new ArrayList<OrderItemInfo>();
						afterOrderInfoList.addAll(finalCommonOrderInfo.getOrderItemInfo());
						hjje = Double.valueOf(finalCommonOrderInfo.getOrderInfo().getHjbhsje());
						lastHjje = hjje;
						i++;
						continue;
					}

				} else {
					// 拿限额减去 前几张累计的金额 得到需要从当前明细中拆分出来的金额
					double splitLimit = MathUtil.sub(limit, new BigDecimal(lastHjje));
					OrderInfo orderInfo = new OrderInfo();
					BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
					orderInfo.setHjbhsje(orderItemInfo.getXmje());
					// 将当前订单拆分成两个订单
					if ((StringUtils.isNotBlank(orderItemInfo.getXmdj())
							|| StringUtils.isNotBlank(orderItemInfo.getXmsl()))
							&& OrderInfoEnum.ORDER_SPLIT_OVERLIMIT_SL.getKey().equals(splitType)) {
						// 如果单价数量不为空
						if (Double.parseDouble(orderItemInfo.getXmdj()) > splitLimit
								|| Double.parseDouble(orderItemInfo.getXmsl()) <= 1) {
							// 如果单价大于要拆分的金额 或者项目数量 无法安数量拆分
							CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
							OrderInfo orderInfo1 = new OrderInfo();
							BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo1);
							orderInfo1.setHjbhsje(df.format(lastHjje));
							commonOrderInfo1.setOrderInfo(orderInfo1);
							commonOrderInfo1.setOrderItemInfo(afterOrderInfoList);
							commonList.add(commonOrderInfo1);
							
							// 当前明细继续拆分
							// 计算当天明细的金额是否超过限额 如果但前明细超限额 则继续超限额拆分
							double zje = Double.parseDouble(orderItemInfo.getXmje());
							if (OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFphxz())) {
								zje = MathUtil.add(new BigDecimal(orderItemInfo.getXmje()),
										new BigDecimal(orderInfoList.get(i + 1).getXmje()));
							}
							
							if (zje > limit.doubleValue()) {
								List<CommonOrderInfo> resultList = new ArrayList<CommonOrderInfo>();
								if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
									resultList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(),
											orderItemInfo, orderInfoList.get(i + 1), limit, splitType);
								} else {
									resultList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(),
											orderItemInfo, null, limit, splitType);
								}
								// 判断拆分后的最后一条订单是否等于限额 如果不等于限额继续和下面的金额累加
								String compareJe = "";
								if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())) {
									// 如果是不含税拿拆分后的不含税金额和限额比较
									compareJe = resultList.get(resultList.size() - 1).getOrderInfo().getHjbhsje();
								} else {
									// 如果是含税 拿拆分后的价税合计金额与限额比较
									compareJe = resultList.get(resultList.size() - 1).getOrderInfo().getKphjje();
								}
								if (new BigDecimal(compareJe).compareTo(limit) == 0) {
									commonList.addAll(resultList);
									afterOrderInfoList = new ArrayList<OrderItemInfo>();
									hjje = 0.00;
									lastHjje = hjje;
									i++;
									continue;
								} else {
									CommonOrderInfo finalCommonOrderInfo = resultList.get(resultList.size() - 1);
									resultList.remove(resultList.size() - 1);
									resultList.addAll(resultList);
									// 拆分后最后一张的金额继续与后边的订单明细累加
									afterOrderInfoList = new ArrayList<OrderItemInfo>();
									afterOrderInfoList.addAll(finalCommonOrderInfo.getOrderItemInfo());
									hjje = Double.valueOf(finalCommonOrderInfo.getOrderInfo().getHjbhsje());
									lastHjje = hjje;
									i++;
									continue;
								}
							} else if (zje == limit.doubleValue()) {
								// 刚好等于限额
								if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
									afterOrderInfoList.add(orderItemInfo);
									afterOrderInfoList.add(orderInfoList.get(i + 1));
								} else {
									afterOrderInfoList.add(orderItemInfo);
								}
								CommonOrderInfo commonOrderInfo2 = new CommonOrderInfo();
								OrderInfo orderInfo2 = new OrderInfo();
								BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo2);
								commonOrderInfo2.setOrderInfo(orderInfo2);
								commonOrderInfo2.setOrderItemInfo(afterOrderInfoList);
								commonList.add(commonOrderInfo2);
								afterOrderInfoList = new ArrayList<OrderItemInfo>();
								hjje = 0.00;
								lastHjje = hjje;
								i++;
								continue;
							} else {
								// 与下一张订单加和
								afterOrderInfoList.add(orderItemInfo);
								if (OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFphxz())) {
									afterOrderInfoList.add(orderInfoList.get(i + 1));
								}
								lastHjje = hjje;
								i++;
								continue;

							}
						}
					}

					List<CommonOrderInfo> resultList = new ArrayList<CommonOrderInfo>();

					if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
						resultList = splitItem(commonOrderInfo.getOrderInfo(), orderItemInfo, orderInfoList.get(i + 1),
								new BigDecimal(splitLimit), splitType, true);
					} else {
						resultList = splitItem(commonOrderInfo.getOrderInfo(), orderItemInfo, null,
								new BigDecimal(splitLimit), splitType,false);
					}
					// 拆分的第一条订单与前边的订单加和
					afterOrderInfoList.addAll(resultList.get(0).getOrderItemInfo());
					CommonOrderInfo commonOrderInfo4 = new CommonOrderInfo();
					OrderInfo orderInfo4 = new OrderInfo();
					BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo4);
					orderInfo4.setHjbhsje(df.format(new BigDecimal(lastHjje)
							.add(new BigDecimal(resultList.get(0).getOrderInfo().getHjbhsje()))));
					commonOrderInfo4.setOrderInfo(orderInfo4);
					commonOrderInfo4.setOrderItemInfo(afterOrderInfoList);
					commonList.add(commonOrderInfo4);
					// 处理拆分出来的剩余订单
					
					afterOrderInfoList = new ArrayList<OrderItemInfo>();
					
					double zje = Double.parseDouble(resultList.get(1).getOrderItemInfo().get(0).getXmje());
					if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
						zje = MathUtil.add(new BigDecimal(resultList.get(1).getOrderItemInfo().get(0).getXmje()),
								new BigDecimal(resultList.get(1).getOrderItemInfo().get(1).getXmje()));
					}
					
					//剩余的订单信息
					List<OrderItemInfo> leaveOrderItemInfo = resultList.get(1).getOrderItemInfo();
					
					
					if (zje > limit.doubleValue()) {
						List<CommonOrderInfo> resultList1 = new ArrayList<CommonOrderInfo>();
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
							resultList1 = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), leaveOrderItemInfo.get(0),
									leaveOrderItemInfo.get(1), limit, splitType);
						} else {
							resultList1 = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), leaveOrderItemInfo.get(0), null,
									limit, splitType);
						}
						// 判断拆分后的最后一条订单是否等于限额 如果不等于限额继续和下面的金额累加
						String compareJe = "";
						if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())) {
							// 如果是不含税拿拆分后的不含税金额和限额比较
							compareJe = resultList1.get(resultList1.size() - 1).getOrderInfo().getHjbhsje();
						} else {
							// 如果是含税 拿拆分后的价税合计金额与限额比较
							compareJe = resultList1.get(resultList1.size() - 1).getOrderInfo().getKphjje();
						}
						if (new BigDecimal(compareJe).compareTo(limit) == 0) {
							commonList.addAll(resultList1);
							afterOrderInfoList = new ArrayList<OrderItemInfo>();
							hjje = 0.00;
							lastHjje = hjje;
							i++;
							continue;
						} else {
							CommonOrderInfo finalCommonOrderInfo = resultList1.get(resultList1.size() - 1);
							resultList1.remove(resultList1.size() - 1);
							commonList.addAll(resultList1);
							// 拆分后最后一张的金额继续与后边的订单明细累加
							afterOrderInfoList = new ArrayList<OrderItemInfo>();
							afterOrderInfoList.addAll(finalCommonOrderInfo.getOrderItemInfo());
							hjje = Double.valueOf(finalCommonOrderInfo.getOrderInfo().getHjbhsje());
							lastHjje = hjje;
							i++;
							continue;
						}
					} else if (zje == limit.doubleValue()) {
						// 刚好等于限额
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
							afterOrderInfoList.add(leaveOrderItemInfo.get(0));
							afterOrderInfoList.add(leaveOrderItemInfo.get(1));
						} else {
							afterOrderInfoList.add(leaveOrderItemInfo.get(0));
						}
						CommonOrderInfo commonOrderInfo2 = new CommonOrderInfo();
						OrderInfo orderInfo2 = new OrderInfo();
						BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo2);
						commonOrderInfo2.setOrderInfo(orderInfo2);
						commonOrderInfo2.setOrderItemInfo(afterOrderInfoList);
						commonList.add(commonOrderInfo2);
						afterOrderInfoList = new ArrayList<OrderItemInfo>();
						hjje = 0.00;
						lastHjje = hjje;
						i++;
						continue;
					} else {
						// 与下一张订单加和
						afterOrderInfoList = new ArrayList<OrderItemInfo>();
						afterOrderInfoList.add(leaveOrderItemInfo.get(0));
						if (OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFphxz())) {
							afterOrderInfoList.add(leaveOrderItemInfo.get(1));
						}
						hjje = zje;
						lastHjje = hjje;
						i++;
						continue;

					}
				}
			} else if (hjje.doubleValue() == limit.doubleValue()) {
				if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
					afterOrderInfoList.add(orderItemInfo);
					afterOrderInfoList.add(orderInfoList.get(i + 1));
				} else {
					afterOrderInfoList.add(orderItemInfo);
				}
				CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
				OrderInfo orderInfo1 = new OrderInfo();
				BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo1);
				commonOrderInfo1.setOrderInfo(orderInfo1);
				commonOrderInfo1.setOrderItemInfo(afterOrderInfoList);
				commonList.add(commonOrderInfo1);
				afterOrderInfoList = new ArrayList<OrderItemInfo>();
				hjje = 0.00;
				lastHjje = hjje;
				i++;
				continue;

			} else {
				// 如果没有超过限额继续与下边的订单加和
				if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
					afterOrderInfoList.add(orderItemInfo);
					afterOrderInfoList.add(orderInfoList.get(i + 1));
				} else {
					afterOrderInfoList.add(orderItemInfo);
				}
				i++;
				lastHjje = hjje;
			}
		}
		
		if (afterOrderInfoList.size() > 0) {
			CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
			OrderInfo orderInfo1 = new OrderInfo();
			BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo1);
			orderInfo1.setHjbhsje(df.format(hjje));
			commonOrderInfo1.setOrderInfo(orderInfo1);
			commonOrderInfo1.setOrderItemInfo(afterOrderInfoList);
			commonList.add(commonOrderInfo1);
		}
		return commonList;
	}*/
    
    /**
     * @param @param commonOrderInfo
     * @param @param splitType
     * @param @param limit
     * @param @param resultList
     * @return void
     * @throws OrderSplitException
     * @throws
     * @Title : OrderSplitOverLimit
     * @Description ：超限额拆分重写
     */
    public static void orderSplitOverLimit(CommonOrderInfo commonOrderInfo, String splitType, BigDecimal limit, List<CommonOrderInfo> resultList) throws OrderSplitException {
	
	    List<OrderItemInfo> orderItemList = commonOrderInfo.getOrderItemInfo();
	
	    List<OrderItemInfo> resultOrderItemList = new ArrayList<>();
	
	
	    CommonOrderInfo resultCommonOrder = new CommonOrderInfo();
	    if (orderItemList.size() <= 0) {
		    return;
	    }
		
		int i = 0;
		BigDecimal lastKphjje = new BigDecimal("0.00");
		BigDecimal hjje = new BigDecimal("0.00");
		for (OrderItemInfo orderItem : orderItemList) {

			
			// 累计合计金额 判断累加的明细是否超限额
			if (i == orderItemList.size() - 1) {
				hjje = getHjje(orderItem, null, hjje);
			} else {
				hjje = getHjje(orderItem, orderItemList.get(i + 1), hjje);
			}

			if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
				// 如果是折扣行的话 跳过
				i++;
				continue;
			}

			if (hjje.doubleValue() > limit.doubleValue()){
				if(i == 0) {
					
					List<CommonOrderInfo> commonList = new ArrayList<>();
					if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
						commonList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), orderItem,
								orderItemList.get(i + 1), limit, splitType);
					} else {
						commonList = orderSplitSingleOverLimit(commonOrderInfo.getOrderInfo(), orderItem, null,
								limit, splitType);
					}
					
					//判断拆分的最后一条金额是否超限额
					String compareJe = "";
						// 如果是不含税拿拆分后的不含税金额和限额比较
					compareJe = commonList.get(commonList.size() - 1).getOrderInfo().getHjbhsje();
					if (new BigDecimal(compareJe).compareTo(limit) == 0) {
						
						resultList.addAll(commonList);
						commonOrderInfo.setOrderItemInfo(orderItemList);
						
						orderItemList.remove(0);
						//如果是折扣行 需要移除折扣行
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
							orderItemList.remove(0);
							
						}
						orderSplitOverLimit(commonOrderInfo, splitType, limit, resultList);
						return;
						
					} else{
						CommonOrderInfo finalCommonOrderInfo = commonList.get(commonList.size() - 1);
						commonList.remove(commonList.size() - 1);
						resultList.addAll(commonList);
						orderItemList.remove(0);
						//如果是折扣行 需要移除折扣行
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
							orderItemList.remove(0);
						}
						orderItemList.addAll(0, finalCommonOrderInfo.getOrderItemInfo());
						commonOrderInfo.setOrderItemInfo(orderItemList);
						orderSplitOverLimit(commonOrderInfo, splitType, limit, resultList);
						return;
					}
				}else {
					
					double splitLimit = MathUtil.sub(limit, lastKphjje);
					
					if ((StringUtils.isNotBlank(orderItem.getXmdj())) && OrderInfoEnum.ORDER_SPLIT_OVERLIMIT_SL.getKey().equals(splitType) && Double.parseDouble(orderItem.getXmdj()) > splitLimit) {
						// 如果单价数量不为空
						
						OrderInfo orderInfo = new OrderInfo();
						BeanUtils.copyProperties(commonOrderInfo, resultCommonOrder);
						BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
						resultCommonOrder.setOrderInfo(orderInfo);
						resultCommonOrder.setOrderItemInfo(resultOrderItemList);
						resultList.add(resultCommonOrder);
						
						for (int j = 0; j < i; j++) {
							orderItemList.remove(0);
						}
						
						
						commonOrderInfo.setOrderItemInfo(orderItemList);
						orderSplitOverLimit(commonOrderInfo, splitType, limit, resultList);
						return;
						
					}else {
						
						List<CommonOrderInfo> commonList = new ArrayList<>();
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
							
							commonList = splitItem(commonOrderInfo.getOrderInfo(), orderItem, orderItemList.get(i + 1),
									new BigDecimal(splitLimit), splitType, true);
						} else {
							commonList = splitItem(commonOrderInfo.getOrderInfo(), orderItem, null,
									new BigDecimal(splitLimit), splitType, false);
						}
						
						resultOrderItemList.addAll(commonList.get(0).getOrderItemInfo());
						OrderInfo orderInfo = new OrderInfo();
						BeanUtils.copyProperties(commonOrderInfo, resultCommonOrder);
						BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
						resultCommonOrder.setOrderInfo(orderInfo);
						resultCommonOrder.setOrderItemInfo(resultOrderItemList);
						resultList.add(resultCommonOrder);
						
						for (int j = 0; j <= i; j++) {
							orderItemList.remove(0);
						}
						//如果是折扣行的话，需要移除折扣行
						if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
							orderItemList.remove(0);
						}
						orderItemList.addAll(0, commonList.get(1).getOrderItemInfo());
						commonOrderInfo.setOrderItemInfo(orderItemList);
						orderSplitOverLimit(commonOrderInfo, splitType, limit, resultList);
						return;
					}
					
					
				}
			} else if (hjje.doubleValue() == limit.doubleValue()){
				
				
				resultOrderItemList.add(orderItem);
				if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
					resultOrderItemList.add(orderItemList.get(i + 1));
				}
				OrderInfo orderInfo = new OrderInfo();
				BeanUtils.copyProperties(commonOrderInfo, resultCommonOrder);
				BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
				resultCommonOrder.setOrderInfo(orderInfo);
				resultCommonOrder.setOrderItemInfo(resultOrderItemList);
				resultList.add(resultCommonOrder);
				
				for (int j = 0; j <= i; j++) {
					orderItemList.remove(0);
				}
				//如果是折扣行的话，需要移除折扣行
				if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
					orderItemList.remove(0);
				}
				orderSplitOverLimit(commonOrderInfo, splitType, limit, resultList);
				resultOrderItemList = new ArrayList<>();
				return;
				
			}else{
				
				lastKphjje = hjje;
				resultOrderItemList.add(orderItem);
				if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItem.getFphxz())) {
					resultOrderItemList.add(orderItemList.get(i + 1));
				}
				i++;
			}
		}
		
		
	    //最后剩下的明细信息作为一个单独的订单
		if(resultOrderItemList.size() > 0){
			OrderInfo orderInfo = new OrderInfo();
			BeanUtils.copyProperties(commonOrderInfo, resultCommonOrder);
			BeanUtils.copyProperties(commonOrderInfo.getOrderInfo(), orderInfo);
			resultCommonOrder.setOrderInfo(orderInfo);
			resultCommonOrder.setOrderItemInfo(resultOrderItemList);
			resultList.add(resultCommonOrder);
			
		}
		
	}
    
    
    /**
     * @throws OrderSplitException @Title :
     * orderInfo @param @param orderItemInfo @param @return @return
     * List<CommonOrderInfo> @exception
	 *
	 */

	private static List<CommonOrderInfo> splitItem(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			OrderItemInfo disCountOrderItemInfo, BigDecimal limit, String splitType, boolean isDiscountRang) throws OrderSplitException {
		
		List<CommonOrderInfo> resultList;
		// 如果是被折扣行
		boolean result = (StringUtils.isNotBlank(orderItemInfo.getXmdj()) || StringUtils.isNotBlank(orderItemInfo.getXmsl()))
				&& OrderInfoEnum.ORDER_SPLIT_OVERLIMIT_SL.getKey().equals(splitType);
		if (result) {
			// 如果单价数量不为空
			resultList = overLimitSplitByDjByTrassion(orderInfo, orderItemInfo, splitType, limit, isDiscountRang,
					disCountOrderItemInfo);
		} else {
			// 如果单价和数量为空按金额拆分
			resultList = overLimitSplitByJeByTrassion(orderInfo, orderItemInfo, splitType, limit, isDiscountRang,
					disCountOrderItemInfo);
		}
		
		String kphjje = orderItemInfo.getXmje();
		String xmse = orderItemInfo.getSe();
		BigDecimal hjje = new BigDecimal("0.00");
		BigDecimal hjse = new BigDecimal("0.00");
		
		for (CommonOrderInfo commonOrder : resultList) {
			for (OrderItemInfo orderItem : commonOrder.getOrderItemInfo()) {
				hjje = hjje.add(new BigDecimal(orderItem.getXmje()));
				hjse = hjse.add(new BigDecimal(orderItem.getSe()));
			}
		}
		if (Double.parseDouble(kphjje) != hjje.doubleValue()) {
			log.error("拆分后金额不一致");
			
		}
		if (Double.parseDouble(xmse) != hjse.doubleValue()) {
			log.error("拆分后税额不一致");
			
		}
		return resultList;
	}
    
    /**
     * @throws OrderSplitException
     * @Title : orderSplitSingleOverLimit @Description :
     * 单条明细超限额拆分 @param @param orderInfo @param @param
     * orderItemInfo @param @param orderItemInfo2 @param @param
     * limit @param @param splitType @return void @exception
	 *
	 */
	private static List<CommonOrderInfo> orderSplitSingleOverLimit(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			OrderItemInfo disCountOrderItemInfo, BigDecimal limit, String splitType) throws OrderSplitException {
		List<CommonOrderInfo> resultList;
		// 如果当前明细行是被折扣行
		boolean result = (StringUtils.isNotBlank(orderItemInfo.getXmdj()) || StringUtils.isNotBlank(orderItemInfo.getXmsl()))
				&& OrderInfoEnum.ORDER_SPLIT_OVERLIMIT_SL.getKey().equals(splitType);
		if (result) {
			//
			resultList = overLimitSplitByDj(orderInfo, orderItemInfo, splitType, limit, disCountOrderItemInfo);
			
		} else {
			// 如果没有单价和数量直接按照金额拆分
			resultList = overLimitSplitByJe(orderInfo, orderItemInfo, splitType, limit, disCountOrderItemInfo);
		}
		String kphjje = orderItemInfo.getXmje();
		String xmse = orderItemInfo.getSe();
		BigDecimal hjje = new BigDecimal("0.00");
		BigDecimal hjse = new BigDecimal("0.00");
		
		for (CommonOrderInfo commonOrder : resultList) {
			for (OrderItemInfo orderItem : commonOrder.getOrderItemInfo()) {
				hjje = hjje.add(new BigDecimal(orderItem.getXmje()));
				hjse = hjse.add(new BigDecimal(orderItem.getSe()));
			}
		}
		if (new BigDecimal(kphjje).compareTo(hjje)  != 0) {
			log.error("拆分后金额不一致");
			
		}
		if (new BigDecimal(xmse).compareTo(hjse) != 0) {
			log.error("拆分后税额不一致");
			
		}
		return resultList;
	}
    
    /**
     * @Title : getHjje @Description ：计算合计金额 @param @param
     * orderItemInfo @param @param orderItemInfo2 @param @param
     * hjje @param @return @return Double @exception
	 *
	 */

	private static BigDecimal getHjje(OrderItemInfo orderItemInfo, OrderItemInfo orderItemInfo2, BigDecimal hjje) {

		if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
			// 如果是被折扣行的话 hjje需要与折扣行的金额一起计算
			hjje = hjje.add(new BigDecimal(orderItemInfo.getXmje()))
					.add(new BigDecimal(orderItemInfo2.getXmje()));
		} else if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz())) {
			// 如果是折扣行的话 直接跳过 已经在被折扣行处理过
		} else {
			// 正常商品行 直接累加金额
			hjje = hjje.add(new BigDecimal(orderItemInfo.getXmje()));
		}
		return hjje;
    }
    
    /**
     *
     * @Title : resetHjjeAndHjse
     * @Description ：重新设置合计金额和合计税额
     * @param @param splitOverLimtByTrassion
     * @param @param isResetOrderId
     * @return void
     * @exception
	 *
	 */
	private static void resetHjjeAndHjse(List<CommonOrderInfo> splitOverLimtByTrassion, boolean isResetOrderId) {
		
		NumberFormat num = NumberFormat.getPercentInstance();
		num.setMaximumIntegerDigits(3);
		num.setMaximumFractionDigits(2);
		for (CommonOrderInfo common : splitOverLimtByTrassion) {
			List<OrderItemInfo> orderItemList = common.getOrderItemInfo();
			BigDecimal hjje = BigDecimal.ZERO;
			BigDecimal hjse = BigDecimal.ZERO;



			int i = 0;
			List<OrderItemInfo> resultList = new ArrayList<OrderItemInfo>();
			for (OrderItemInfo orderItem : orderItemList) {
				hjje = hjje.add(new BigDecimal(orderItem.getXmje()))
						.setScale(2, RoundingMode.HALF_UP);
				if (StringUtils.isNotBlank(orderItem.getSe())) {
					hjse = hjse.add(new BigDecimal(orderItem.getSe()))
							.setScale(2, RoundingMode.HALF_UP);
				}
				//去除金额为0的折扣行
				if(OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItem.getFphxz()) && new BigDecimal(orderItem.getXmje()).compareTo(BigDecimal.ZERO) == 0){

					resultList.get(i - 1).setFphxz(OrderInfoEnum.FPHXZ_CODE_0.getKey());

				}else{
					resultList.add(orderItem);
					i++;
				}
			}
			common.setOrderItemInfo(resultList);
			// 不含税
			if (!isResetOrderId) {
				BigDecimal jshj = hjje.add(hjse).setScale(2, RoundingMode.HALF_UP);
				common.getOrderInfo().setHjbhsje(hjje.toPlainString());
				common.getOrderInfo().setKphjje(jshj.toPlainString());
				common.getOrderInfo().setHjse(hjse.toPlainString());
				common.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
				// 含税
			} else {
				common.getOrderInfo().setKphjje(hjje.toPlainString());
				common.getOrderInfo().setHjse("");
				common.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
				
			}
			// 拆分后的订单 添加数据来源 为拆分后的订单
			common.setSjywly(OrderInfoEnum.READY_ORDER_SJLY_0.getKey());

		}
	}

	
	private static CommonOrderInfo rebuildCommonOrderInfo(OrderInfo orderInfo, List<OrderItemInfo> selectByOrderId,
			Double bhsje) {
		CommonOrderInfo splitCommonOrderInfo1 = new CommonOrderInfo();
		OrderInfo splitOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, splitOrderInfo);
		splitOrderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
		splitOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormat(bhsje, ConfigureConstant.INT_2));
		splitCommonOrderInfo1.setOrderInfo(splitOrderInfo);
		splitCommonOrderInfo1.setOrderItemInfo(selectByOrderId);
		return splitCommonOrderInfo1;
	}
	
	/**
	 * @param orderInfo
	 * @param orderItemInfo
	 * @param orderItemInfo2
	 * @param isDiscountRowOverLimit
	 * @return
	 * @throws OrderReceiveException
	 * @description 根据金额拆分
	 */
	public static List<CommonOrderInfo> overLimitSplitByJeByTrassion(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			String splitType, BigDecimal limit, boolean isDiscountRowOverLimit, OrderItemInfo orderItemInfo2) {
		log.debug(
				"{}订单按金额拆分接口,入参:orderInfo:{},orderItemInfo:{},splitType:{},limit:{},isDiscountRowOverLimit:{},"
						+ "orderItem2:{}",
				LOG_MSG, JsonUtils.getInstance().toJsonString(orderInfo),
				JsonUtils.getInstance().toJsonString(orderItemInfo), splitType, limit, isDiscountRowOverLimit,
				JsonUtils.getInstance().toJsonString(orderItemInfo2));
		List<CommonOrderInfo> orderSplit;
		// 是否包含单价和数量 如果包含单价和数量需要对单价和数量进行拆分
		boolean isContainsSlOrDj = false;
		boolean isDiscountContainsSlOrDj = false;
		Double bhsje = 0.00;
		bhsje = Double.valueOf(orderInfo.getKphjje());
		// 如果单价数量不为空 计算单价数量
		if (StringUtils.isNotBlank(orderItemInfo.getXmsl()) || StringUtils.isNotBlank(orderItemInfo.getXmdj())) {
			isContainsSlOrDj = true;
			orderItemInfo = resetDjSl(orderItemInfo);
		}
		// 折扣行超限额拆分
		if (isDiscountRowOverLimit) {
			if (StringUtils.isNotBlank(orderItemInfo2.getXmdj()) || StringUtils.isNotBlank(orderItemInfo2.getXmsl())) {
				isDiscountContainsSlOrDj = true;
				orderItemInfo2 = resetDjSl(orderItemInfo2);
			}
			Double realJe = MathUtil.add(orderItemInfo.getXmje(), orderItemInfo2.getXmje());
			Double zklimit = new BigDecimal(orderItemInfo2.getXmje())
					.multiply(limit.divide(new BigDecimal(realJe), 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			Double commonLimit = MathUtil.sub(String.valueOf(limit), String.valueOf(zklimit));
			Double avgSl = null;
			Double avgDisSl = null;
			if (isContainsSlOrDj) {
				avgSl = MathUtil.div(String.valueOf(commonLimit), String.valueOf(orderItemInfo.getXmdj()), 8);
			}
			if (isDiscountContainsSlOrDj) {
				avgDisSl = MathUtil.div(String.valueOf(zklimit), String.valueOf(orderItemInfo2.getXmdj()), 8);
			}
			int page = (int) MathUtil.div(String.valueOf(realJe), String.valueOf(limit), 0);
			if (limit.multiply(new BigDecimal(page)).compareTo(new BigDecimal(String.valueOf(realJe))) < 0) {
				page++;
			}

			orderSplit = convertCommonOrderInfoDisCountByTrassion(orderInfo, orderItemInfo, orderItemInfo2,
					BigDecimal.valueOf(commonLimit), zklimit, page, avgSl, avgDisSl);

		} else {

			int page = (int) MathUtil.div(String.valueOf(bhsje), String.valueOf(limit), 0);

			// 如果张数乘以限额小于合计开票金额 张数加一
			if (limit.multiply(new BigDecimal(page)).compareTo(new BigDecimal(String.valueOf(bhsje))) < 0) {
				page++;
			}
            BigDecimal avgSl = null;
            if (isContainsSlOrDj) {
                avgSl = limit.divide(new BigDecimal(orderItemInfo.getXmdj()), 8, BigDecimal.ROUND_DOWN);
            }
            orderSplit = convertCommonOrderInfoByTrassion(orderInfo, orderItemInfo, limit, page, avgSl);
            
        }
        return orderSplit;
        
    }
    
    /**
     * @param orderInfo
     * @param orderItemInfo
     * @return
     * @throws OrderSplitException
     * @throws OrderReceiveException
     * @description 根据金额拆分
     */
    public static List<CommonOrderInfo> overLimitSplitByJe(OrderInfo orderInfo, OrderItemInfo orderItemInfo, String splitType,
                                                           BigDecimal limit, OrderItemInfo disCountOrderItemInfo) throws OrderSplitException {
        log.debug("{}订单按金额拆分接口,入参:orderInfo:{},orderItemInfo:{},splitType:{},limit:{},orderItem2:{}", LOG_MSG,
				JsonUtils.getInstance().toJsonString(orderInfo), JsonUtils.getInstance().toJsonString(orderItemInfo),
				splitType, limit, JsonUtils.getInstance().toJsonString(disCountOrderItemInfo));
		List<CommonOrderInfo> orderSplit;
		// 是否包含单价和数量 如果包含单价和数量需要对单价和数量进行拆分
		boolean isContainsSlOrDj = false;
		Double bhsje = 0.00;
		bhsje = Double.valueOf(orderItemInfo.getXmje());
		// 如果单价数量不为空 计算单价数量
		if (StringUtils.isNotBlank(orderItemInfo.getXmsl()) || StringUtils.isNotBlank(orderItemInfo.getXmdj())) {
			isContainsSlOrDj = true;
		}
		// 折扣行超限额拆分
		if (disCountOrderItemInfo != null) {
			
			Double realJe = MathUtil.add(orderItemInfo.getXmje(), disCountOrderItemInfo.getXmje());
			Double zklimit = new BigDecimal(disCountOrderItemInfo.getXmje())
					.multiply(limit.divide(new BigDecimal(realJe), 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			Double commonLimit = MathUtil.sub(String.valueOf(limit), String.valueOf(zklimit));
			Double avgSl = null;
			if (isContainsSlOrDj) {
				avgSl = MathUtil.div(String.valueOf(commonLimit), String.valueOf(orderItemInfo.getXmdj()), 8);
			}
			int page = (int) MathUtil.div(String.valueOf(realJe), limit.toString(), 0);
			if (limit.multiply(new BigDecimal(page)).compareTo(new BigDecimal(String.valueOf(realJe))) < 0) {
				page++;
			}

			orderSplit = convertCommonOrderInfoDisCount(orderInfo, orderItemInfo, disCountOrderItemInfo, commonLimit,
					zklimit, page, avgSl);

		} else {

			int page = (int) MathUtil.div(String.valueOf(bhsje), limit.toString(), 0);

			// 如果张数乘以限额小于合计开票金额 张数加一
			if (limit.multiply(new BigDecimal(page)).compareTo(new BigDecimal(String.valueOf(bhsje))) < 0) {
				page++;
			}
			BigDecimal avgSl = null;
			if (isContainsSlOrDj) {
				avgSl = limit.divide(new BigDecimal(orderItemInfo.getXmdj()), 8, BigDecimal.ROUND_DOWN);
			}
			orderSplit = convertCommonOrderInfo(orderInfo, orderItemInfo, limit, page, avgSl);

		}
		// 拆分后重新计算税额
		dealSeWc(orderSplit);
		//dealFinalDisCount(orderSplit);
		return orderSplit;

	}


	//处理拆分后折扣额为0的订单
	/*private static void dealFinalDisCount(List<CommonOrderInfo> orderSplit) {

    	if(OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderSplit.get(orderSplit.size()).getOrderItemInfo().get(0).getFphxz())){
    		for(CommonOrderInfo common : orderSplit){
    			if(OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(common.getOrderItemInfo().get(1).getFphxz())){
    				if(new BigDecimal(common.getOrderItemInfo().get(1).getXmje()).compareTo(BigDecimal.ZERO) == 0){
						common.getOrderItemInfo().remove(1);
						common.getOrderItemInfo().get(0).setFphxz(OrderInfoEnum.FPHXZ_CODE_0.getKey());
					}
				}
			}

		}
	}*/

	/**
	 * 拆分后再次重组税额
	 *
	 * @param orderSplit
	 */
	private static void dealSeWc(List<CommonOrderInfo> orderSplit) {
		// 判断是否是折扣行
		
		if (OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderSplit.get(0).getOrderItemInfo().get(0).getFphxz())) {
			
			CommonOrderInfo commonOrderInfo = orderSplit.get(orderSplit.size() - 1);
			OrderItemInfo orderItemInfo = commonOrderInfo.getOrderItemInfo().get(0);
			BigDecimal jsse = new BigDecimal(orderItemInfo.getXmje()).multiply(new BigDecimal(orderItemInfo.getSl()))
					.setScale(2, RoundingMode.HALF_UP);
			BigDecimal seCompareResult = jsse.subtract(new BigDecimal(orderItemInfo.getSe()))
					.setScale(2, RoundingMode.HALF_UP);
			
			if (Math.abs(seCompareResult.doubleValue()) > 0.06) {
				log.info("拆分后最后一张发票的税额误差大于0.06 需要重新分配税额，orderItemId:{}", orderItemInfo.getId());
				if (seCompareResult.doubleValue() > 0) {
					
					BigDecimal leaveJe = seCompareResult.subtract(new BigDecimal("0.05"))
							.setScale(2, RoundingMode.HALF_UP);
					BigDecimal syJe = leaveJe;
					
					Double finalse = new BigDecimal(orderItemInfo.getSe()).add(leaveJe)
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					orderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(finalse, ConfigureConstant.INT_2));
					int devide = orderSplit.size() - 1;
					//税额的最小平均误差是0.01
					BigDecimal realAvgSe = leaveJe
							.divide(new BigDecimal(devide), 8, RoundingMode.HALF_UP);
					if (realAvgSe.doubleValue() < 0.01) {
						realAvgSe = new BigDecimal("0.01");
					}
					
					for (int i = 0; i < devide; i++) {
						BigDecimal avgSe = BigDecimal.ZERO;
						BigDecimal currentAvgJe = BigDecimal.ZERO;
						if (realAvgSe.doubleValue() == 0.01) {
							if (devide - 1 - i == 0) {
								currentAvgJe = syJe;
							} else {
								avgSe = new BigDecimal("0.01");
							}
							
						} else {
							if (devide - 1 - i == 0) {
								currentAvgJe = syJe;
							} else {
								currentAvgJe = syJe.divide(new BigDecimal(devide - i), 8, RoundingMode.HALF_UP);
							}
							
							if (currentAvgJe.compareTo(realAvgSe) > 0) {
								avgSe = leaveJe.divide(new BigDecimal(devide), 2, BigDecimal.ROUND_UP);
							} else {
								avgSe = leaveJe.divide(new BigDecimal(devide), 2, BigDecimal.ROUND_DOWN);
							}
						}
						syJe = syJe.subtract(avgSe)
								.setScale(2, RoundingMode.HALF_UP);
						
						if (syJe.doubleValue() < 0) {
							Double se1 = avgSe.add(syJe)
									.setScale(2, RoundingMode.HALF_UP).doubleValue();
							Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
									.subtract(new BigDecimal(se1)).setScale(2, RoundingMode.HALF_UP).doubleValue();
							orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
							break;
						} else {
							
							if (i == devide - 1) {
								
								
								Double se1 = syJe.add(avgSe)
										.setScale(2, RoundingMode.HALF_UP).doubleValue();
								Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
										.subtract(new BigDecimal(se1)).setScale(2, RoundingMode.HALF_UP)
										.doubleValue();
								orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
								
							} else {
								Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
										.subtract(avgSe).setScale(2, RoundingMode.HALF_UP)
										.doubleValue();
								orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
							}
						}
						
					}
				} else {
					
					BigDecimal absJe = seCompareResult.abs();
					BigDecimal leaveJe = absJe.subtract(new BigDecimal("0.05"))
							.setScale(2, RoundingMode.HALF_UP);
					BigDecimal syJe = leaveJe;
					BigDecimal finalse = new BigDecimal(orderItemInfo.getSe()).subtract(leaveJe)
							.setScale(2, RoundingMode.HALF_UP);
					orderItemInfo.setSe(finalse.toPlainString());
					int devide = orderSplit.size() - 1;
					BigDecimal realAvgJe = leaveJe.divide(new BigDecimal(devide), 8, RoundingMode.HALF_UP);
					//税额的最小平均误差是0.01
					if (realAvgJe.doubleValue() < 0.01) {
						realAvgJe = new BigDecimal("0.01");
					}
					for (int i = 0; i < devide; i++) {
						Double avgSe = 0.00;
						BigDecimal currentAvgJe = BigDecimal.ZERO;
						if (realAvgJe.setScale(2, RoundingMode.HALF_UP).doubleValue() == 0.01) {
							if (devide - 1 - i == 0) {
								currentAvgJe = syJe;
							} else {
								avgSe = 0.01;
							}
							
						} else {
							if (devide - 1 - i == 0) {
								currentAvgJe = syJe;
							} else {
								currentAvgJe = syJe.divide(new BigDecimal(devide - i), 8, RoundingMode.HALF_UP);
							}
							
							if (currentAvgJe.compareTo(realAvgJe) > 0) {
								avgSe = leaveJe.divide(new BigDecimal(devide), 2, BigDecimal.ROUND_UP)
										.doubleValue();
							} else {
								avgSe = leaveJe.divide(new BigDecimal(devide), 2, BigDecimal.ROUND_DOWN)
										.doubleValue();
							}
						}
						
						syJe = syJe.subtract(new BigDecimal(avgSe))
								.setScale(2, RoundingMode.HALF_UP);
						if (syJe.doubleValue() < 0) {
							Double se1 = syJe.add(new BigDecimal(avgSe))
									.setScale(2, RoundingMode.HALF_UP).doubleValue();
							Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
									.add(new BigDecimal(se1)).setScale(2, RoundingMode.HALF_UP).doubleValue();
							orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
							break;
						} else {
							if (i == devide - 1) {
								Double se1 = syJe.add(new BigDecimal(avgSe))
										.setScale(2, RoundingMode.HALF_UP).doubleValue();
								Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
										.add(new BigDecimal(se1)).setScale(2, RoundingMode.HALF_UP).doubleValue();
								orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
								
							} else {
								Double realSe = new BigDecimal(orderSplit.get(i).getOrderItemInfo().get(0).getSe())
										.add(new BigDecimal(avgSe)).setScale(2, RoundingMode.HALF_UP).doubleValue();
								orderSplit.get(i).getOrderItemInfo().get(0).setSe(DecimalCalculateUtil.decimalFormat(realSe, ConfigureConstant.INT_2));
							}
						}
					}
					
				}

			}

		}

	}

	/**
	 * @param @param
	 *            orderInfo
	 * @param @param
	 *            orderItemInfo
	 * @param @param
	 *            limit
	 * @param @param
	 *            page
	 * @param @param
	 *            avgSl
	 * @param @return
	 * @return List<CommonOrderInfo>
     * @throws OrderSplitException
     * @throws @Title
     *             : convertCommonOrderInfo
     * @Description ：非折扣行超限额拆分
	 */
	
	private static List<CommonOrderInfo> convertCommonOrderInfo(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			BigDecimal limit, int page, BigDecimal avgSl) throws OrderSplitException {
		List<CommonOrderInfo> orderSplit = new ArrayList<>();
		Double avgse = 0.00;
		if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
			avgse = new BigDecimal(orderItemInfo.getSe())
					.multiply(limit.divide(new BigDecimal(orderItemInfo.getXmje()), 20, RoundingMode.HALF_UP))
					.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		}
		for (int i = 0; i < page; i++) {
			// double realJe = avgPageje;
			CommonOrderInfo newCommonOrderInfo;
			OrderInfo newOrderInfo = orderInfo;
			OrderItemInfo newOrderItemInfo = new OrderItemInfo();
			BeanUtils.copyProperties(orderItemInfo, newOrderItemInfo);
			List<OrderItemInfo> neworderItemInfoList = new ArrayList<>();
			
			if (i == page - 1) {
				// 最后一张的金额等于总金额减去前几张的金额
				// BigDecimal doubleValue = null;
				
				BigDecimal doubleValue = new BigDecimal(orderItemInfo.getXmje())
						.subtract(limit.multiply(new BigDecimal(page - 1))).setScale(2, RoundingMode.HALF_UP);
				if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
					Double leaveSe = new BigDecimal(orderItemInfo.getSe())
							.subtract(new BigDecimal(avgse).multiply(new BigDecimal(page - 1)))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					newOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(leaveSe, ConfigureConstant.INT_2));
				}
				newOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(doubleValue.toString(), ConfigureConstant.INT_2));
				// 最后一行的数量等于前几行的数量相减
				if (avgSl != null) {
					Double xmsl = new BigDecimal(orderItemInfo.getXmsl())
							.subtract(avgSl.multiply(new BigDecimal(page - 1))).setScale(8, RoundingMode.HALF_UP)
							.doubleValue();
					newOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(xmsl, ConfigureConstant.INT_8));
					
					Double subJe = new BigDecimal(newOrderItemInfo.getXmsl())
							.multiply(new BigDecimal(newOrderItemInfo.getXmdj()))
							.subtract(new BigDecimal(newOrderItemInfo.getXmje())).doubleValue();
					
					if (Math.abs(subJe) > 0.01) {
						if (Double.parseDouble(newOrderItemInfo.getXmsl()) == 0.00) {
							throw new OrderSplitException("9999", "订单金额误差大于0.01");
						} else {
							Double xmdj = new BigDecimal(newOrderItemInfo.getXmje())
									.divide(new BigDecimal(newOrderItemInfo.getXmsl()), 8, RoundingMode.HALF_UP)
									.doubleValue();
							newOrderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
						}
						
					}
				}

				// 折扣行 和商品行拆分到一起
				Double finalJe = doubleValue.doubleValue();
				neworderItemInfoList.add(newOrderItemInfo);
				newCommonOrderInfo = rebuildCommonOrderInfo(newOrderInfo, neworderItemInfoList, finalJe);

			} else {
				// 前几张的金额等于限额
				newOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(limit.toString(), ConfigureConstant.INT_2));
				if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
					newOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(avgse, ConfigureConstant.INT_2));
				}
				if (avgSl != null) {
					newOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(avgSl.toString(), ConfigureConstant.INT_8));
				}
				neworderItemInfoList.add(newOrderItemInfo);
				newCommonOrderInfo = rebuildCommonOrderInfo(newOrderInfo, neworderItemInfoList, limit.doubleValue());
			}
			orderSplit.add(newCommonOrderInfo);
		}
		return orderSplit;
	}

	/**
	 * @param orderInfo
	 * @param orderItemInfo
	 * @return
	 * @throws OrderSplitException
	 * @throws OrderReceiveException
	 * @description 根据单价拆分
	 */
	
	public static List<CommonOrderInfo> overLimitSplitByDj(OrderInfo orderInfo, OrderItemInfo orderItemInfo, String splitType,
			BigDecimal limit, OrderItemInfo disCountItemInfo) throws OrderSplitException {
		List<CommonOrderInfo> orderSplit;

		
		// 带折扣行的拆分
		if (disCountItemInfo != null) {
			
			Double realJe = MathUtil.add(orderItemInfo.getXmje(), disCountItemInfo.getXmje());
			// 初算金额
			BigDecimal zklimit = new BigDecimal(disCountItemInfo.getXmje())
					.multiply(limit.divide(new BigDecimal(realJe), 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP);
			
			BigDecimal commonLimit = limit.subtract(zklimit);
			
			BigDecimal divide = commonLimit.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);
			// 重新计算单张的价税合计金额
			commonLimit = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide)
					.setScale(2, RoundingMode.HALF_UP);
			
			// 计算单张折扣行金额
			zklimit = new BigDecimal(disCountItemInfo.getXmje()).multiply(commonLimit
					.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP);
			
			// 计算页数
			Double avgPageJe = commonLimit.add(zklimit).doubleValue();
			
			int page = (int) new BigDecimal(String.valueOf(realJe)).divide(new BigDecimal(String.valueOf(avgPageJe)), 0, BigDecimal.ROUND_DOWN)
					.longValue();
			
			if (new BigDecimal(String.valueOf(avgPageJe)).multiply(new BigDecimal(page))
					.compareTo(new BigDecimal(String.valueOf(realJe))) < 0) {
				page++;
			}
			
			orderSplit = convertCommonOrderInfoDisCount(orderInfo, orderItemInfo, disCountItemInfo, commonLimit.doubleValue(),
					zklimit.doubleValue(), page, divide.doubleValue());
		} else {
			// 非折扣行拆分
			BigDecimal divide = limit.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);

			// 重新计算单张的金额
			double avgPageJe = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide)
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			// 计算页数
			int page = (int) new BigDecimal(orderItemInfo.getXmsl()).divide(divide, 0, BigDecimal.ROUND_DOWN)
					.longValue();

			if (new BigDecimal(String.valueOf(avgPageJe)).multiply(new BigDecimal(page)).setScale(2, RoundingMode.HALF_UP)
					.compareTo(new BigDecimal(orderItemInfo.getXmje())) < 0) {
				page++;
			}
			orderSplit = convertCommonOrderInfo(orderInfo, orderItemInfo, BigDecimal.valueOf(avgPageJe), page, divide);

		}
		//处理税额尾差 大于 6分钱的场景
		dealSeWc(orderSplit);
		return orderSplit;
	}

	/**
	 * @param orderInfo
	 * @param orderItemInfo
	 * @param orderItemInfo2
	 * @param isDiscountRowOverLimit
	 * @return
	 * @throws OrderReceiveException
	 * @description 根据单价拆分
	 */

	public static List<CommonOrderInfo> overLimitSplitByDjByTrassion(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			String splitType, BigDecimal limit, boolean isDiscountRowOverLimit, OrderItemInfo orderItemInfo2) {
		List<CommonOrderInfo> orderSplit;
		boolean isDiscountContainsSl = false;
		
		// 带折扣行的拆分
		if (isDiscountRowOverLimit) {
			if (StringUtils.isNotBlank(orderItemInfo2.getXmdj()) || StringUtils.isNotBlank(orderItemInfo2.getXmsl())) {
				isDiscountContainsSl = true;
				orderItemInfo2 = resetDjSl(orderItemInfo2);
			}
			
			BigDecimal realJe = new BigDecimal(orderItemInfo.getXmje()).add(new BigDecimal(orderItemInfo2.getXmje()));
			// 初算金额
			BigDecimal zklimit = new BigDecimal(orderItemInfo2.getXmje())
					.multiply(limit.divide(realJe, 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP);
			
			BigDecimal commonLimit = limit.subtract(zklimit);
			
			BigDecimal divide = commonLimit
					.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);
			// 重新计算单张的金额
			commonLimit = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide)
					.setScale(2, RoundingMode.HALF_UP);
			
			// 计算页数
			zklimit = new BigDecimal(orderItemInfo2.getXmje()).multiply(commonLimit
					.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP);
			
			BigDecimal avgPageJe = commonLimit.add(zklimit);
			
			int page = (int) realJe.divide(avgPageJe, 0, BigDecimal.ROUND_DOWN).longValue();
			
			if (avgPageJe.multiply(new BigDecimal(page))
					.compareTo(realJe) < 0) {
				page++;
			}
			
			Double avgDisSl = null;
			if (isDiscountContainsSl) {
				avgDisSl = MathUtil.div(String.valueOf(zklimit), String.valueOf(orderItemInfo2.getXmdj()), 8);
			}
			
			orderSplit = convertCommonOrderInfoDisCountByTrassion(orderInfo, orderItemInfo, orderItemInfo2,
					commonLimit, zklimit.doubleValue(), page, divide.doubleValue(), avgDisSl);
			// 非折扣行拆分
		} else {

			BigDecimal divide = limit.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);
			// 重新计算单张的金额
			double avgPageJe = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide)
					.setScale(2, RoundingMode.HALF_UP).doubleValue();

			// 计算页数
			int page = (int) new BigDecimal(orderItemInfo.getXmsl()).divide(divide, 0, BigDecimal.ROUND_DOWN)
					.longValue();

			if (new BigDecimal(String.valueOf(avgPageJe)).multiply(new BigDecimal(page))
					.compareTo(new BigDecimal(orderItemInfo.getXmje())) < 0) {
				page++;
			}

			orderSplit = convertCommonOrderInfoByTrassion(orderInfo, orderItemInfo, BigDecimal.valueOf(avgPageJe), page,
					divide);
		}
		return orderSplit;

	}

	private static List<CommonOrderInfo> convertCommonOrderInfoByTrassion(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			BigDecimal avgPageJe, int page, BigDecimal divide) {
		
		// 将订单拆分成两张发票
		List<CommonOrderInfo> orderSplitList = new ArrayList<>();
		OrderInfo copyOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, copyOrderInfo);
		OrderItemInfo orderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo, orderItemInfo1);
		if (divide != null) {
			orderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormatToString(divide.toString(), ConfigureConstant.INT_8));
		}
		orderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormatToString(avgPageJe.toString(), ConfigureConstant.INT_2));
		// 税额部位空的话拆分税额
		Double se = 0.00;
		if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
			se = new BigDecimal(orderItemInfo.getSe())
					.multiply(avgPageJe.divide(new BigDecimal(orderItemInfo.getXmje()), 20, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
		}
		copyOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormatToString(avgPageJe.toString(), ConfigureConstant.INT_2));
		List<OrderItemInfo> list = new ArrayList<>();
		list.add(orderItemInfo1);
		CommonOrderInfo common = new CommonOrderInfo();
		common.setOrderInfo(copyOrderInfo);
		common.setOrderItemInfo(list);
		orderSplitList.add(common);
		
		orderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo, orderItemInfo1);
		if (divide != null) {
			Double leaveSl = new BigDecimal(orderItemInfo.getXmsl()).subtract(divide)
					.setScale(8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormat(leaveSl, ConfigureConstant.INT_8));
		}
		Double leaveJe = new BigDecimal(orderItemInfo.getXmje()).subtract(avgPageJe)
				.setScale(8, RoundingMode.HALF_UP).doubleValue();
		if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
			Double leaveSe = new BigDecimal(orderItemInfo.getSe()).subtract(new BigDecimal(se))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(leaveSe, ConfigureConstant.INT_2));
		}
		copyOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, copyOrderInfo);
		copyOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormat(leaveJe, ConfigureConstant.INT_2));
		list = new ArrayList<>();
		orderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormat(leaveJe, ConfigureConstant.INT_2));
		
		list.add(orderItemInfo1);
		common = new CommonOrderInfo();
		common.setOrderInfo(copyOrderInfo);
		common.setOrderItemInfo(list);
		orderSplitList.add(common);
		
		return orderSplitList;
	}
	
	private static List<CommonOrderInfo> convertCommonOrderInfoDisCountByTrassion(OrderInfo orderInfo,
			OrderItemInfo orderItemInfo, OrderItemInfo orderItemInfo2, BigDecimal commonLimit, Double zklimit, int page,
			Double divide, Double avgDisSl) {
		// 将订单拆分成两张发票
		List<CommonOrderInfo> orderSplitList = new ArrayList<>();
		OrderInfo copyOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, copyOrderInfo);
		OrderItemInfo orderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo, orderItemInfo1);
		if (divide != null) {
			orderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormatToString(divide.toString(), ConfigureConstant.INT_8));
		}
		orderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormatToString(commonLimit.toString(), ConfigureConstant.INT_2));
		// 根据金额比例分配税额
		Double se = 0.00;
		if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
			se = new BigDecimal(orderItemInfo.getSe())
					.multiply(commonLimit.divide(new BigDecimal(orderItemInfo.getXmje()), 20, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
		}
		OrderItemInfo discountOrderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo2, discountOrderItemInfo1);
		// 如果有数量设置数量
		if (avgDisSl != null) {
			discountOrderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormatToString(avgDisSl.toString(), ConfigureConstant.INT_8));
		}
		discountOrderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormat(zklimit, ConfigureConstant.INT_2));
		Double disCountSe = 0.00;
		if (StringUtils.isNotBlank(orderItemInfo2.getSe())) {
			disCountSe = new BigDecimal(orderItemInfo2.getSe()).multiply(new BigDecimal(zklimit)
					.divide(new BigDecimal(orderItemInfo2.getXmje()), 20, RoundingMode.HALF_UP))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			discountOrderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(disCountSe, ConfigureConstant.INT_2));
		}
		Double relaJe = commonLimit.add(new BigDecimal(zklimit)).setScale(2, RoundingMode.HALF_UP).doubleValue();
		copyOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormat(relaJe, ConfigureConstant.INT_2));
		List<OrderItemInfo> list = new ArrayList<>();
		list.add(orderItemInfo1);
		list.add(discountOrderItemInfo1);
		CommonOrderInfo common = new CommonOrderInfo();
		common.setOrderInfo(copyOrderInfo);
		common.setOrderItemInfo(list);
		orderSplitList.add(common);
		
		orderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo, orderItemInfo1);
		if (divide != null) {
			Double leaveSl = new BigDecimal(orderItemInfo.getXmsl()).subtract(new BigDecimal(divide))
					.setScale(8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormatToString(leaveSl.toString(), ConfigureConstant.INT_8));
		}
		Double leaveJe = new BigDecimal(orderItemInfo.getXmje()).subtract(commonLimit)
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
		if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
			Double leaveSe = new BigDecimal(orderItemInfo.getSe()).subtract(new BigDecimal(se))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(leaveSe, ConfigureConstant.INT_2));
		}
		copyOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, copyOrderInfo);
		list = new ArrayList<>();
		orderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormat(leaveJe, ConfigureConstant.INT_2));
		discountOrderItemInfo1 = new OrderItemInfo();
		BeanUtils.copyProperties(orderItemInfo2, discountOrderItemInfo1);
		if (avgDisSl != null) {
			Double discountLeaveSl = new BigDecimal(Math.abs(Double.parseDouble(orderItemInfo2.getXmsl())))
					.subtract(new BigDecimal(avgDisSl)).setScale(8, RoundingMode.HALF_UP).doubleValue();
			discountOrderItemInfo1.setXmsl(DecimalCalculateUtil.decimalFormat(-discountLeaveSl, ConfigureConstant.INT_8));
		}
		Double discountLeaveJe = new BigDecimal(orderItemInfo2.getXmje()).subtract(new BigDecimal(zklimit))
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
		if (StringUtils.isNotBlank(orderItemInfo2.getSe())) {
			Double discountLeaveSe = new BigDecimal(orderItemInfo2.getSe()).subtract(new BigDecimal(disCountSe))
					.setScale(2, RoundingMode.HALF_UP).doubleValue();
			discountOrderItemInfo1.setSe(DecimalCalculateUtil.decimalFormat(discountLeaveSe, ConfigureConstant.INT_2));
		}
		discountOrderItemInfo1.setXmje(DecimalCalculateUtil.decimalFormat(discountLeaveJe, ConfigureConstant.INT_2));
		
		relaJe = new BigDecimal(leaveJe).subtract(new BigDecimal(discountLeaveJe)).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		copyOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormat(relaJe, ConfigureConstant.INT_2));
		list.add(orderItemInfo1);
		list.add(discountOrderItemInfo1);
		common = new CommonOrderInfo();
		common.setOrderInfo(copyOrderInfo);
		common.setOrderItemInfo(list);
		orderSplitList.add(common);
		
		return orderSplitList;
	}
	
	/**
	 * 重新构建拆分后的订单
	 *
	 * @param orderInfo
	 * @param orderItemInfo
	 * @param orderItemInfo2
	 * @param avgPageje
	 * @param avgDisCountPageje
	 * @param page
	 * @param divide
	 * @return
	 * @throws OrderSplitException
	 */
	
	private static List<CommonOrderInfo> convertCommonOrderInfoDisCount(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			OrderItemInfo orderItemInfo2, Double avgPageje, Double avgDisCountPageje, Integer page, Double divide) throws OrderSplitException {
		List<CommonOrderInfo> orderSplit = new ArrayList<>();
		
		Double avgse = new BigDecimal(orderItemInfo.getSe()).multiply(
				new BigDecimal(String.valueOf(avgPageje)).divide(new BigDecimal(orderItemInfo.getXmje()), 20, RoundingMode.HALF_UP))
				.setScale(2, RoundingMode.HALF_UP).doubleValue();
		Double avgDiscountSe = new BigDecimal(orderItemInfo2.getSe()).multiply(new BigDecimal(String.valueOf(avgDisCountPageje))
				.divide(new BigDecimal(orderItemInfo2.getXmje()), 20, RoundingMode.HALF_UP))
				.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		Double zje = 0.00;
		Double zzke = 0.00;
		for (int i = 0; i < page; i++) {
			// double realJe = avgPageje;
			CommonOrderInfo newCommonOrderInfo;
			OrderInfo newOrderInfo = orderInfo;
			OrderItemInfo newOrderItemInfo = new OrderItemInfo();
			BeanUtils.copyProperties(orderItemInfo, newOrderItemInfo);
			List<OrderItemInfo> neworderItemInfoList = new ArrayList<>();
			
			if (i == page - 1) {
				// 最后一张的金额等于总金额减去前几张的金额
				BigDecimal doubleValue = new BigDecimal(orderItemInfo.getXmje())
						.subtract(new BigDecimal(String.valueOf(avgPageje)).multiply(new BigDecimal(page - 1)))
						.setScale(2, RoundingMode.HALF_UP);
				Double leaveSe = new BigDecimal(orderItemInfo.getSe())
						.subtract(new BigDecimal(String.valueOf(avgse)).multiply(new BigDecimal(page - 1)))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				newOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(doubleValue.toString(), ConfigureConstant.INT_2));
				newOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(leaveSe, ConfigureConstant.INT_2));
				// 最后一行的数量等于前几行的数量相减
				if (divide != null) {
					Double xmsl = new BigDecimal(orderItemInfo.getXmsl())
							.subtract(new BigDecimal(String.valueOf(divide)).multiply(new BigDecimal(page - 1)))
							.setScale(8, RoundingMode.HALF_UP).doubleValue();
					newOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(xmsl, ConfigureConstant.INT_8));
					Double subJe = new BigDecimal(newOrderItemInfo.getXmsl())
							.multiply(new BigDecimal(newOrderItemInfo.getXmdj()))
							.subtract(new BigDecimal(newOrderItemInfo.getXmje())).doubleValue();
					if (Math.abs(subJe) >= 0.01) {
						if (Double.parseDouble(newOrderItemInfo.getXmsl()) == 0.00) {
							throw new OrderSplitException("9999", "订单金额误差大于0.01");
						} else {
							Double xmdj = new BigDecimal(newOrderItemInfo.getXmje())
									.divide(new BigDecimal(newOrderItemInfo.getXmsl()), 8, RoundingMode.HALF_UP)
									.doubleValue();
							newOrderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
						}
						
					}
					
				}
				// 折扣行 和商品行拆分到一起
				Double leaveJe = new BigDecimal(orderItemInfo2.getXmje())
						.subtract(new BigDecimal(String.valueOf(avgDisCountPageje)).multiply(new BigDecimal(page - 1)))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				Double leaveDiscountSe = new BigDecimal(orderItemInfo2.getSe())
						.subtract(new BigDecimal(String.valueOf(avgDiscountSe)).multiply(new BigDecimal(page - 1)))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				
				OrderItemInfo finalOrderItemInfo = new OrderItemInfo();
				
				BeanUtils.copyProperties(orderItemInfo2, finalOrderItemInfo);
				finalOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(leaveJe, ConfigureConstant.INT_2));
				finalOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(leaveDiscountSe, ConfigureConstant.INT_2));
				
				// 最后一张的金额
				Double finalJe = doubleValue.add(new BigDecimal(leaveJe))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				neworderItemInfoList.add(newOrderItemInfo);
				neworderItemInfoList.add(finalOrderItemInfo);
				newCommonOrderInfo = rebuildCommonOrderInfo(newOrderInfo, neworderItemInfoList, finalJe);
				
			} else {
				// 前几张的金额等于限额
				Double realJe = MathUtil.add(String.valueOf(avgPageje), String.valueOf(avgDisCountPageje));
				newOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(avgPageje, ConfigureConstant.INT_2));
				newOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(avgse, ConfigureConstant.INT_2));
				if (divide != null) {
					newOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(divide.toString(), ConfigureConstant.INT_8));
				}
				OrderItemInfo finalOrderItemInfo = new OrderItemInfo();
				BeanUtils.copyProperties(orderItemInfo2, finalOrderItemInfo);
				
				finalOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(avgDisCountPageje, ConfigureConstant.INT_2));
				finalOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(avgDiscountSe, ConfigureConstant.INT_2));
				
				zje = avgPageje + zje;
				zzke = avgDisCountPageje + zzke;
				// 如果还有折扣额 按照折扣额计算实际金额
				neworderItemInfoList.add(newOrderItemInfo);
				neworderItemInfoList.add(finalOrderItemInfo);
				newCommonOrderInfo = rebuildCommonOrderInfo(newOrderInfo, neworderItemInfoList, realJe);
			}
			orderSplit.add(newCommonOrderInfo);
		}
		return orderSplit;
    }
	
	/**
	 * @Title : resetDjSL @Description ：重新计算单价和数量 @param @param
	 * orderItemInfo @param @return @return OrderItemInfo @exception
	 */
	private static OrderItemInfo resetDjSl(OrderItemInfo orderItemInfo) {
		double xmdj = 0.00;
		double xmsl = 0.00;
		
		if (StringUtils.isBlank(orderItemInfo.getXmdj()) && StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
			// 如果单价为空 数量不为空 重新计算单价
			xmdj = new BigDecimal(orderItemInfo.getXmje())
					.divide(new BigDecimal(orderItemInfo.getXmsl()), 8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
		} else if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) && StringUtils.isBlank(orderItemInfo.getXmsl())) {
			// 如果数量为空 单价不为空 重新计算数量
			xmsl = new BigDecimal(orderItemInfo.getXmje())
					.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(xmsl, ConfigureConstant.INT_8));
		}
		return orderItemInfo;

	}

	/**
	 * 按照多个金额数量拆分接口
	 *
	 * @throws OrderReceiveException
	 */
	public static List<CommonOrderInfo> orderSplit(CommonOrderInfo common, String[] parseJeArray, String key)
			throws OrderReceiveException {
		
		OrderInfo selectByPrimaryKey = common.getOrderInfo();
		// 判断是否是红票 目前不支持红票拆分
		if (Double.parseDouble(selectByPrimaryKey.getKphjje()) <= 0) {
			log.info("红票无法拆分{}", LOG_MSG);
			throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_JE_ILLEGALITY.getKey(),
					OrderInfoContentEnum.ORDER_SPLIT_JE_ILLEGALITY.getMessage());
		}
		
		List<OrderItemInfo> selectByOrderId = common.getOrderItemInfo();
		List<CommonOrderInfo> commonList = new ArrayList<>();
		// 按照金额拆分
		if (OrderInfoEnum.ORDER_SPLIT_JE_ARRAY.getKey().equals(key)) {
			BigDecimal enterJe = new BigDecimal(ConfigureConstant.STRING_000);
			for (String je : parseJeArray) {
				double je1 = Double.parseDouble(je);
				if (je1 <= 0) {
					log.info("{}拆分金额小于0，无法拆分", LOG_MSG);
					throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_JE_ILLEGALITY.getKey(),
							OrderInfoContentEnum.ORDER_SPLIT_JE_ILLEGALITY.getMessage());
				}
				enterJe = enterJe.add(new BigDecimal(je));
			}
			
			BigDecimal jsje = enterJe.setScale(2, RoundingMode.HALF_UP);
			BigDecimal hjje = new BigDecimal(selectByPrimaryKey.getKphjje()).setScale(2, RoundingMode.HALF_UP);
			if (parseJeArray.length == 1 && jsje.equals(hjje)) {
				// 拆分金额只有一个，并且拆分金额等于总金额
				throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_JE_SL_ERROR.getKey(),
						OrderInfoContentEnum.ORDER_SPLIT_JE_SL_ERROR.getMessage());
			}
			// 订单拆分金额校验
			if (jsje.compareTo(hjje) > 0) {
				log.info("{}拆分金额大于实际金额，无法拆分", LOG_MSG);
				throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_JE_ERROR.getKey(),
						OrderInfoContentEnum.ORDER_SPLIT_JE_ERROR.getMessage());
			}
			// 订单拆分开始
			int i = 0;
			for (String je : parseJeArray) {
				List<CommonOrderInfo> resultList = new ArrayList<>();
				try {
					resultList = dealSplitJe(je, selectByPrimaryKey, selectByOrderId);
				} catch (OrderSplitException e) {
					e.printStackTrace();
				}
				if (selectByOrderId.size() <= 0) {
					// 金额已拆分完
					commonList.addAll(resultList);
					break;
				} else {
					commonList.addAll(resultList);
					if (i == parseJeArray.length - 1) {
						convertAndAdd(selectByPrimaryKey, selectByOrderId, commonList);
					}
				}
				i++;

			}

		} else if (OrderInfoEnum.ORDER_SPLIT_SL_ARRAY.getKey().equals(key)) {
			// 判断是否是单明细
			boolean isDiscountRang = false;
			if (selectByOrderId.size() != 1) {
				if (selectByOrderId.size() == 2) {
					// 是否是折扣行
					if (!OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(selectByOrderId.get(1).getFphxz())) {
						log.info("{}订单明细不是一条，无法拆分", LOG_MSG);
						throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getKey(),
								OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getMessage());
					} else {
						isDiscountRang = true;
					}
				} else {
					log.info("{}订单明细不是一条，无法拆分", LOG_MSG);
					throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getKey(),
							OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getMessage());
				}
				// 判断是否没有数量后者数量是1
			} else if (StringUtils.isEmpty(selectByOrderId.get(0).getXmsl())
					|| "1".equals(selectByOrderId.get(0).getXmsl())) {
				log.info("{}订单明细五数量或者数量为1，无法拆分", LOG_MSG);
				throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getKey(),
						OrderInfoContentEnum.ORDER_SPLIT_MXSL_ERROR.getMessage());

			}
			BigDecimal enterSl = new BigDecimal(ConfigureConstant.STRING_000);
			
			for (String sl : parseJeArray) {
				double sl1 = Double.parseDouble(sl);
				if (sl1 <= 0) {
					log.info("{}数量为负，不能拆分", LOG_MSG);
				}
				enterSl = enterSl.add(new BigDecimal(sl));
			}
			double jsSl = enterSl.setScale(2, RoundingMode.HALF_UP).doubleValue();
			double xmsl = Double.parseDouble(selectByOrderId.get(0).getXmsl());
			// 订单拆分金额校验
			if (jsSl > xmsl) {
				log.info("要拆分的数量大于实际项目数量");
				throw new OrderReceiveException(OrderInfoContentEnum.ORDER_SPLIT_ZSL_ERROR.getKey(),
						OrderInfoContentEnum.ORDER_SPLIT_ZSL_ERROR.getMessage());
			}
			// 订单拆分开始
			List<CommonOrderInfo> resultList = dealSplitSl(new ArrayList<>(), selectByPrimaryKey,
					selectByOrderId, isDiscountRang);
			commonList.addAll(resultList);
		} else {
			throw new OrderReceiveException(OrderInfoContentEnum.UNKONW_SPLIT_TYPE.getKey(),
					OrderInfoContentEnum.UNKONW_SPLIT_TYPE.getMessage());
		}
		resetHjjeAndHjse(commonList, true);
		return commonList;
	}

	/**
	 * 拆分金额
	 *
	 * @param je
	 * @param orderInfo
	 * @param selectByOrderId
	 * @return
	 * @throws OrderReceiveException
	 */
	private static List<CommonOrderInfo> dealSplitJe(String je, OrderInfo orderInfo, List<OrderItemInfo> selectByOrderId)
			throws OrderSplitException, OrderReceiveException {
		List<CommonOrderInfo> resultList = new ArrayList<>();
		
		BigDecimal hjje = new BigDecimal(ConfigureConstant.STRING_000);
		BigDecimal lastHjje = new BigDecimal(ConfigureConstant.STRING_000);
		List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
		for (int i = 0; i < selectByOrderId.size(); i++) {
			// 被折扣行
			if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(selectByOrderId.get(i).getFphxz())) {
				hjje = hjje.add(new BigDecimal(selectByOrderId.get(i).getXmje()))
						.add(new BigDecimal(selectByOrderId.get(i + 1).getXmje()));
				// 大于
				if (hjje.compareTo(new BigDecimal(je)) > 0) {
					// 如果第n个明细超限额 拆分第n条明细
					Double leaveJe = new BigDecimal(je).subtract(lastHjje).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					if (StringUtils.isNotBlank(selectByOrderId.get(i).getXmdj())
							&& Double.parseDouble(selectByOrderId.get(i).getXmdj()) > leaveJe) {
						
						if (i == 0) {
							// 如果第一条的单价大于要拆分的金额，返回拆分失败
							log.error("单价大于要拆分的金额，无法拆分");
							throw new OrderSplitException(
									OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_DJ_OVER_SPLITJE__ERROR);
						} else {
							// 如果无法拆分 前 n个明细作为一张发票
							convertAndAdd(orderInfo, orderItemInfoList, resultList);
							// 将拆分后的明细剔除出
							selectByOrderId.subList(0, i).clear();
							break;
						}
					} else {

						List<CommonOrderInfo> overLimitSplitByJeArray = overLimitSplitByJeArray(orderInfo,
								selectByOrderId.get(i), BigDecimal.valueOf(leaveJe), true, selectByOrderId.get(i + 1));
						// 前i条和拆分后的前一部分作为一个订单
						orderItemInfoList.addAll(overLimitSplitByJeArray.get(0).getOrderItemInfo());
						convertAndAdd(orderInfo, orderItemInfoList, resultList);

						// 剩余的订单接着和下边的订单接着加和
						selectByOrderId.subList(0, i + 2).clear();
						selectByOrderId.addAll(0, overLimitSplitByJeArray.get(1).getOrderItemInfo());
						break;
					}
					// 小于
				} else if (hjje.compareTo(new BigDecimal(je)) == 0) {
					orderItemInfoList.add(selectByOrderId.get(i));
					orderItemInfoList.add(selectByOrderId.get(i + 1));
					convertAndAdd(orderInfo, orderItemInfoList, resultList);

					selectByOrderId.subList(0, i + 2).clear();
					break;
					// 等于
				} else {
					orderItemInfoList.add(selectByOrderId.get(i));
					orderItemInfoList.add(selectByOrderId.get(i + 1));
				}

			} else if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(selectByOrderId.get(i).getFphxz())) {
				// 被折扣行
				continue;
			} else {
				hjje = hjje.add(new BigDecimal(selectByOrderId.get(i).getXmje()));
				if (hjje.compareTo(new BigDecimal(je)) > 0) {
					Double leaveJe = new BigDecimal(je).subtract(lastHjje).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					if (StringUtils.isNotBlank(selectByOrderId.get(i).getXmdj())
							&& Double.parseDouble(selectByOrderId.get(i).getXmdj()) > leaveJe) {
						// 如果第一条的单价大于要拆分的金额，返回拆分失败
						if (i == 0) {
							log.error("单价大于要拆分的金额，无法拆分");
							throw new OrderSplitException(
									OrderSplitErrorMessageEnum.ORDER_SPLIT_ORDERINFO_DJ_OVER_SPLITJE__ERROR);
							
						} else {
							convertAndAdd(orderInfo, orderItemInfoList, resultList);
							selectByOrderId.subList(0, i).clear();
							break;
						}
					} else {

						List<CommonOrderInfo> overLimitSplitByJeArray = overLimitSplitByJeArray(orderInfo,
								selectByOrderId.get(i), BigDecimal.valueOf(leaveJe), false, null);
						// 前i条和拆分后的前一部分作为一个订单
						orderItemInfoList.addAll(overLimitSplitByJeArray.get(0).getOrderItemInfo());
						convertAndAdd(orderInfo, orderItemInfoList, resultList);
						// 剩余的订单接着和下边的订单接着加和
						selectByOrderId.subList(0, i + 1).clear();
						selectByOrderId.addAll(0, overLimitSplitByJeArray.get(1).getOrderItemInfo());
						break;

					}

				} else if (hjje.compareTo(new BigDecimal(je)) == 0) {
					orderItemInfoList.add(selectByOrderId.get(i));
					convertAndAdd(orderInfo, orderItemInfoList, resultList);
					selectByOrderId.subList(0, i + 1).clear();
					break;
					// 小于
				} else {
					orderItemInfoList.add(selectByOrderId.get(i));
				}
			}
			lastHjje = hjje;
		}

		return resultList;
	}

	/**
	 * 数据组装
	 */
	private static void convertAndAdd(OrderInfo selectByPrimaryKey, List<OrderItemInfo> orderItemInfoList,
			List<CommonOrderInfo> resultList) {
		OrderInfo orderInfo = new OrderInfo();
		BeanUtils.copyProperties(selectByPrimaryKey, orderInfo);
		CommonOrderInfo common = new CommonOrderInfo();
		common.setOrderInfo(orderInfo);
		common.setOrderItemInfo(orderItemInfoList);
		resultList.add(common);
    }
    
    /**
     *
     * @Title : overLimitSplitByJeArray @Description
     * ：不含税金额拆分(不含税需要处理税额) @param @param orderInfo @param @param
     * orderItemInfo @param @param limit @param @param
     * isDiscountRowOverLimit @param @param
     * orderItemInfo2 @param @return @param @throws
     * OrderReceiveException @return List<CommonOrderInfo> @exception
	 *
	 */
	public static List<CommonOrderInfo> overLimitSplitByJeArray(OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			BigDecimal limit, boolean isDiscountRowOverLimit, OrderItemInfo orderItemInfo2) {
		List<CommonOrderInfo> orderSplit = new ArrayList<>();
		boolean isDiscountContainsSl = false;
		if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) || StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
			if (isDiscountRowOverLimit) {
				if (StringUtils.isNotBlank(orderItemInfo2.getXmdj())
						|| StringUtils.isNotBlank(orderItemInfo2.getXmsl())) {
					isDiscountContainsSl = true;
				}
				
				BigDecimal realJe = new BigDecimal(orderItemInfo.getXmje()).add(new BigDecimal(orderItemInfo2.getXmje()));
				// 初算金额
				BigDecimal zklimit = new BigDecimal(orderItemInfo2.getXmje())
						.multiply(limit.divide(realJe, 8, RoundingMode.HALF_UP))
						.setScale(2, RoundingMode.HALF_UP);
				
				BigDecimal commonLimit = limit.subtract(zklimit);
				
				BigDecimal divide = commonLimit.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);
				// 重新计算单张的金额
				commonLimit = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide).setScale(2, RoundingMode.HALF_UP);
				
				// 计算页数
				zklimit = new BigDecimal(orderItemInfo2.getXmje()).multiply(commonLimit
						.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
						.setScale(2, RoundingMode.HALF_UP);
				
				BigDecimal avgPageJe = commonLimit.add(zklimit);
				
				int page = (int) realJe.divide(avgPageJe, 0, BigDecimal.ROUND_DOWN)
						.longValue();
				
				if (avgPageJe.multiply(new BigDecimal(page)).compareTo(realJe) < 0) {
					page++;
				}
				
				Double avgDisSl = null;
				if (isDiscountContainsSl) {
					avgDisSl = MathUtil.div(String.valueOf(zklimit), String.valueOf(orderItemInfo2.getXmdj()), 8);
				}
				orderSplit = convertCommonOrderInfoDisCountByTrassion(orderInfo, orderItemInfo, orderItemInfo2,
						commonLimit, zklimit.doubleValue(), page, divide.doubleValue(), avgDisSl);
				// 非折扣行拆分
			} else {

				BigDecimal divide = limit.divide(new BigDecimal(orderItemInfo.getXmdj()), 0, BigDecimal.ROUND_DOWN);
				// 重新计算单张的金额
				double avgPageJe = new BigDecimal(orderItemInfo.getXmdj()).multiply(divide)
						.setScale(2, RoundingMode.HALF_UP).doubleValue();

				// 计算页数
				int page = (int) new BigDecimal(orderItemInfo.getXmsl()).divide(divide, 0, BigDecimal.ROUND_DOWN)
						.longValue();

				if (new BigDecimal(avgPageJe).multiply(new BigDecimal(page))
						.compareTo(new BigDecimal(orderItemInfo.getXmje())) < 0) {
					page++;
				}
				orderSplit = convertCommonOrderInfoByTrassion(orderInfo, orderItemInfo, BigDecimal.valueOf(avgPageJe),
						page, divide);
			}
		} else {
			if (isDiscountRowOverLimit) {
				Double zklimit = new BigDecimal(orderItemInfo2.getXmje())
						.multiply(limit.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				orderSplit = convertCommonOrderInfoDisCountByTrassion(orderInfo, orderItemInfo, orderItemInfo2, limit,
						zklimit, 0, null, null);
			} else {
				orderSplit = convertCommonOrderInfoByTrassion(orderInfo, orderItemInfo, limit, 0, null);
			}

		}
		
		dealSeWc(orderSplit);
		// 带折扣行的拆分
		return orderSplit;
	}

	/**
	 * @description 数量拆分 区分含税和不含税
	 * @param orderInfo1
	 * @param selectByOrderId
	 * @param isDiscountRang
	 * @return
	 */

	private static List<CommonOrderInfo> dealSplitSl(List<Double> slList, OrderInfo orderInfo1,
			List<OrderItemInfo> selectByOrderId, boolean isDiscountRang) {
		List<CommonOrderInfo> resultList = new ArrayList<>();
		OrderItemInfo orderItemInfo = selectByOrderId.get(0);
		// 重置单价和数量
		orderItemInfo = resetDjSl(orderItemInfo);
		
		if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz())) {
			// 含税拆分
			if (isDiscountRang) {
				resultList = dealSplitSlHs(slList, orderInfo1, orderItemInfo, selectByOrderId.get(1));
			} else {
				resultList = dealSplitSlHs(slList, orderInfo1, orderItemInfo, null);
			}
		} else {
			// 不含税拆分
			if (isDiscountRang) {
				resultList = dealSplitSlBhs(slList, orderInfo1, orderItemInfo, selectByOrderId.get(1));
			} else {
				resultList = dealSplitSlBhs(slList, orderInfo1, orderItemInfo, null);
			}
		}
		// 重新合计金额 合计税额 加水合计
		List<CommonOrderInfo> returnList = new ArrayList<>();
		for (CommonOrderInfo comm : resultList) {
			CommonOrderInfo rebuildCommonOrderInfo = rebuildCommonOrderInfo(comm.getOrderInfo(), comm.getOrderItemInfo());
			returnList.add(rebuildCommonOrderInfo);
		}
		return returnList;
		
	}
    
    /**
     * slList @Title : dealSplitSlBhs @Description ：不含税数量拆分
     * 需要重新分配税额 @param @return void @exception
	 *
	 */
	private static List<CommonOrderInfo> dealSplitSlBhs(List<Double> slList, OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			OrderItemInfo discountOrderItemInfo) {
		// 拆分的结果
		List<CommonOrderInfo> resultList = new ArrayList<>();
		// 拆分完成之后的合计金额
		double hjje = 0.00;
		// 拆分后的合计税额
		double hjse = 0.00;
		// 拆分完成之后的合计折扣金额
		double hjzkje = 0.00;
		// 拆分后折扣合计税额
		double hjzkse = 0.00;
		// 拆分完之后的合计数量
		Double hjsl = 0.00;

		for (Double i : slList) {
			
			hjsl = i + hjsl;
			// 计算明细的金额
			BigDecimal xmje = BigDecimal.ZERO;
			OrderInfo copyOrderInfo = new OrderInfo();
			OrderItemInfo copyOrderItemInfo = new OrderItemInfo();
			// 复制订单和订单明细信息
			List<OrderItemInfo> itemList = new ArrayList<>();
			BeanUtils.copyProperties(orderInfo, copyOrderInfo);
			BeanUtils.copyProperties(orderItemInfo, copyOrderItemInfo);
			// 重置数量
			copyOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(i, ConfigureConstant.INT_8));
			
			if (new BigDecimal(hjsl).compareTo(new BigDecimal(orderItemInfo.getXmsl())) == 0) {
				// 如果拆分的数量刚好等于明细数量
				xmje = new BigDecimal(orderItemInfo.getXmje()).subtract(new BigDecimal(hjje));
				copyOrderItemInfo.setXmje(xmje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
				// 如果最后一张发票的 单价*数量 和 项目金额的误差大于0.01 重置单价
				if (new BigDecimal(copyOrderItemInfo.getXmsl()).multiply(new BigDecimal(copyOrderItemInfo.getXmdj()))
						.setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(copyOrderItemInfo.getXmje()))
						.doubleValue() > 0.01) {
					copyOrderItemInfo.setXmdj(new BigDecimal(copyOrderItemInfo.getXmje())
							.divide(new BigDecimal(copyOrderItemInfo.getXmsl())).setScale(8, RoundingMode.HALF_UP)
							.toString());
				}
				// 如果税额不为空的话重新计算税额
				if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
					double xmse = MathUtil.sub(new BigDecimal(orderItemInfo.getSe()), new BigDecimal(hjse));
					copyOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
					hjse += xmse;
				}
				
				/**
				 * TODO 校验最后一张发票的税额误差是否超过0.06 如果超过再次平均分配税额
				 */
			} else {
				// 按拆分的数量 单价*数量 计算金额
				xmje = new BigDecimal(i).multiply(new BigDecimal(orderItemInfo.getXmdj()));
				copyOrderItemInfo.setXmje(xmje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
				// 如果税额不为空 需要重新计算税额 税额计算方式 税额 = 金额 /合计金额 * 税额
				if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
					double xmse = xmje
							.divide(new BigDecimal(orderItemInfo.getXmje()), 10, RoundingMode.HALF_UP)
							.multiply(new BigDecimal(orderItemInfo.getSe())).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					copyOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
					hjse += xmse;
				}
				
			}
			
			// 累计本次拆分的金额
			hjje = MathUtil.add(new BigDecimal(hjje), xmje);
			itemList.add(copyOrderItemInfo);
			
			// 如果是折扣行 根据金额比例分配折扣额
			if (discountOrderItemInfo != null) {
				
				// 计算需要分配的折扣金额
				double zkje = 0.00;
				OrderItemInfo copyDiscountOrdreItem = new OrderItemInfo();
				BeanUtils.copyProperties(discountOrderItemInfo, copyDiscountOrdreItem);
				if (new BigDecimal(hjsl).compareTo(new BigDecimal(orderItemInfo.getXmsl())) == 0) {
					zkje = MathUtil.sub(new BigDecimal(discountOrderItemInfo.getXmje()), new BigDecimal(hjzkje));
					// 税额不为空的话计算税额
					double xmse = MathUtil.sub(new BigDecimal(discountOrderItemInfo.getSe()), new BigDecimal(hjzkse));
					copyDiscountOrdreItem.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
				} else {
					zkje = new BigDecimal(discountOrderItemInfo.getXmje())
							.multiply(new BigDecimal(copyOrderItemInfo.getXmje())
									.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					double xmse = new BigDecimal(zkje)
							.divide(new BigDecimal(discountOrderItemInfo.getXmje()), 8, RoundingMode.HALF_UP)
							.multiply(new BigDecimal(discountOrderItemInfo.getSe())).setScale(2, RoundingMode.HALF_UP)
							.doubleValue();
					copyDiscountOrdreItem.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
					hjzkse += xmse;
					
				}
				
				/**
				 * TODO 校验最后一张发票的税额误差是否超过0.06 如果超过再次平均分配税额
				 */
				// 组装明细的折扣行
				copyDiscountOrdreItem.setXmje(DecimalCalculateUtil.decimalFormat(zkje, ConfigureConstant.INT_2));
				itemList.add(copyDiscountOrdreItem);
				hjzkje = hjzkje + zkje;
			}
			
			// 组装订单信息
			CommonOrderInfo common = new CommonOrderInfo();
			common.setOrderInfo(orderInfo);
			common.setOrderItemInfo(itemList);
			resultList.add(common);
		}
		// 计算剩余数量
		BigDecimal leaveSl = new BigDecimal(orderItemInfo.getXmsl()).subtract(new BigDecimal(hjsl))
				.setScale(8, RoundingMode.HALF_UP);
		// 拆分后还有剩余的数量
		if (leaveSl.doubleValue() != 0.00) {
			// 计算剩余金额
			BigDecimal leaveJe = new BigDecimal(orderItemInfo.getXmje()).subtract(new BigDecimal(hjje));
			// 复制订单明细信息
			OrderInfo copyOrderInfo = new OrderInfo();
			OrderItemInfo copyOrderItemInfo = new OrderItemInfo();
			List<OrderItemInfo> itemList = new ArrayList<>();
			BeanUtils.copyProperties(orderInfo, copyOrderInfo);
			BeanUtils.copyProperties(orderItemInfo, copyOrderItemInfo);
			copyOrderItemInfo.setXmsl(leaveSl.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toPlainString());
			copyOrderItemInfo.setXmje(leaveJe.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
			// 如果单价*数量 与项目金额的误差大于 0.01的话 重新设置单价
			if (leaveJe.subtract(leaveSl.multiply(new BigDecimal(orderItemInfo.getXmdj()))
					.setScale(2, RoundingMode.HALF_UP)).doubleValue() > 0.01) {
				BigDecimal xmdj = leaveJe.divide(leaveSl, 8, RoundingMode.HALF_UP);
				copyOrderItemInfo.setXmdj(xmdj.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toPlainString());
			}
			if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
				double xmse = MathUtil.sub(new BigDecimal(orderItemInfo.getSe()), new BigDecimal(hjse));
				copyOrderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
			}
			itemList.add(copyOrderItemInfo);
			/**
			 * TODO 校验最后一张发票的税额误差是否超过0.06 如果超过再次平均分配税额
			 */

			if (discountOrderItemInfo != null) {
				// 计算剩余的折扣金额
				Double leaveZkje = new BigDecimal(discountOrderItemInfo.getXmje()).subtract(new BigDecimal(hjzkje))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				OrderItemInfo copyDiscountOrdreItem = new OrderItemInfo();
				BeanUtils.copyProperties(discountOrderItemInfo, copyDiscountOrdreItem);
				copyDiscountOrdreItem.setXmje(DecimalCalculateUtil.decimalFormat(leaveZkje, ConfigureConstant.INT_2));
				
				if (StringUtils.isNotBlank(discountOrderItemInfo.getSe())) {
					double xmse = MathUtil.sub(new BigDecimal(discountOrderItemInfo.getSe()), new BigDecimal(hjzkse));
					copyDiscountOrdreItem.setSe(DecimalCalculateUtil.decimalFormat(xmse, ConfigureConstant.INT_2));
				}
				itemList.add(copyDiscountOrdreItem);
				/**
				 * TODO 校验最后一张发票的税额误差是否超过0.06 如果超过再次平均分配税额
				 */
			}
			
			// 重新构建订单信息
			CommonOrderInfo common = new CommonOrderInfo();
			common.setOrderInfo(orderInfo);
			common.setOrderItemInfo(itemList);
			resultList.add(common);
		}
		dealSeWc(resultList);
		return resultList;
	}

	/**
	 * 基本拆分方式
     */
    
    /**
     * slList @Title : dealSplitSlHs @Description ：含税数量拆分
     * 拆分如果存在误差 误差累计到最后一张发票 如果最后一张发票的单价*数量 误差大于0.01 重置单价 @param @return
     * void @exception
	 *
	 */
	private static List<CommonOrderInfo> dealSplitSlHs(List<Double> slList, OrderInfo orderInfo, OrderItemInfo orderItemInfo,
			OrderItemInfo discountOrderItemInfo) {
		
		// 拆分的结果
		List<CommonOrderInfo> resultList = new ArrayList<>();
		// 拆分完成之后的合计金额
		double jshj = 0.00;
		// 拆分完成之后的合计折扣金额
		double hjzkje = 0.00;
		// 拆分完之后的合计数量
		Double hjsl = 0.00;
		
		for (Double i : slList) {
			
			hjsl = i + hjsl;
			// 计算明细的金额
			Double xmje = 0.00;
			OrderInfo copyOrderInfo = new OrderInfo();
			OrderItemInfo copyOrderItemInfo = new OrderItemInfo();
			// 复制订单和订单明细信息
			List<OrderItemInfo> itemList = new ArrayList<>();
			BeanUtils.copyProperties(orderInfo, copyOrderInfo);
			BeanUtils.copyProperties(orderItemInfo, copyOrderItemInfo);
			// 重置数量
			copyOrderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(i, ConfigureConstant.INT_8));
			
			if (new BigDecimal(hjsl).compareTo(new BigDecimal(orderItemInfo.getXmsl())) == 0) {
				// 如果拆分的数量刚好等于明细数量
				xmje = MathUtil.sub(new BigDecimal(orderItemInfo.getXmje()), new BigDecimal(jshj));
				copyOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(xmje, ConfigureConstant.INT_2));
				// 如果最后一张发票的 单价*数量 和 项目金额的误差大于0.01 重置单价
				if (new BigDecimal(copyOrderItemInfo.getXmsl()).multiply(new BigDecimal(copyOrderItemInfo.getXmdj()))
						.setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(copyOrderItemInfo.getXmje()))
						.doubleValue() > 0.01) {
					copyOrderItemInfo.setXmdj(new BigDecimal(copyOrderItemInfo.getXmje())
							.divide(new BigDecimal(copyOrderItemInfo.getXmsl())).setScale(8, RoundingMode.HALF_UP)
							.toString());
				}
			} else {
				// 按拆分的数量 单价*数量 计算金额
				xmje = MathUtil.mul(new BigDecimal(i), new BigDecimal(orderItemInfo.getXmdj()));
				copyOrderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(xmje, ConfigureConstant.INT_2));
			}
			
			// 累计本次拆分的金额
			jshj = MathUtil.add(new BigDecimal(jshj), new BigDecimal(xmje));
			itemList.add(copyOrderItemInfo);

			// 如果是折扣行 根据金额比例分配折扣额
			if (discountOrderItemInfo != null) {

				// 计算需要分配的折扣金额
				double zkje = 0.00;
				if (new BigDecimal(hjsl).compareTo(new BigDecimal(orderItemInfo.getXmsl())) == 0) {
					zkje = MathUtil.sub(new BigDecimal(discountOrderItemInfo.getXmje()), new BigDecimal(hjzkje));
				} else {
					zkje = new BigDecimal(discountOrderItemInfo.getXmje())
							.multiply(new BigDecimal(copyOrderItemInfo.getXmje())
									.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					
				}
				// 组装明细的折扣行
				OrderItemInfo copyDiscountOrdreItem = new OrderItemInfo();
				BeanUtils.copyProperties(discountOrderItemInfo, copyDiscountOrdreItem);
				copyDiscountOrdreItem.setXmje(DecimalCalculateUtil.decimalFormat(zkje, ConfigureConstant.INT_2));
				itemList.add(copyDiscountOrdreItem);
				hjzkje = hjzkje + zkje;
			}
			
			// 组装订单信息
			CommonOrderInfo common = new CommonOrderInfo();
			common.setOrderInfo(orderInfo);
			common.setOrderItemInfo(itemList);
			resultList.add(common);
		}
		// 计算剩余数量
		BigDecimal leaveSl = new BigDecimal(orderItemInfo.getXmsl()).subtract(new BigDecimal(hjsl))
				.setScale(8, RoundingMode.HALF_UP);
		// 拆分后还有剩余的数量
		if (leaveSl.doubleValue() != 0.00) {
			// 计算剩余金额
			BigDecimal leaveJe = new BigDecimal(orderItemInfo.getXmje()).subtract(new BigDecimal(jshj));
			// 复制订单明细信息
			OrderInfo copyOrderInfo = new OrderInfo();
			OrderItemInfo copyOrderItemInfo = new OrderItemInfo();
			List<OrderItemInfo> itemList = new ArrayList<>();
			BeanUtils.copyProperties(orderInfo, copyOrderInfo);
			BeanUtils.copyProperties(orderItemInfo, copyOrderItemInfo);
			copyOrderItemInfo.setXmsl(leaveSl.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toPlainString());
			copyOrderItemInfo.setXmje(leaveJe.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
			// 如果单价*数量 与项目金额的误差大于 0.01的话 重新设置单价
			if (leaveJe.subtract(leaveSl.multiply(new BigDecimal(orderItemInfo.getXmdj()))
					.setScale(2, RoundingMode.HALF_UP)).doubleValue() > 0.01) {
				BigDecimal xmdj = leaveJe.divide(leaveSl, 8, RoundingMode.HALF_UP);
				copyOrderItemInfo.setXmdj(xmdj.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toPlainString());
			}
			itemList.add(copyOrderItemInfo);
			
			if (discountOrderItemInfo != null) {
				// 计算剩余的折扣金额
				Double leaveZkje = new BigDecimal(discountOrderItemInfo.getXmje()).subtract(new BigDecimal(hjzkje))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				OrderItemInfo copyDiscountOrdreItem = new OrderItemInfo();
				BeanUtils.copyProperties(discountOrderItemInfo, copyDiscountOrdreItem);
				copyDiscountOrdreItem.setXmje(DecimalCalculateUtil.decimalFormat(leaveZkje, ConfigureConstant.INT_2));
				itemList.add(copyDiscountOrdreItem);
			}
			// 重新构建订单信息
			CommonOrderInfo common = new CommonOrderInfo();
			common.setOrderInfo(orderInfo);
			common.setOrderItemInfo(itemList);
			resultList.add(common);
		}
		
		dealSeWc(resultList);
		return resultList;
    
    }
    
    /**
     * @Title : orderSplitByLineList @Description
     * ：根据明细行拆分 @param @param commonOrderInfo @param @param
     * splitRule @param @param lineList @param @return @return
     * List<CommonOrderInfo> @exception
	 *
	 */
	private static List<CommonOrderInfo> orderSplitByLineList(CommonOrderInfo commonOrderInfo, String splitRule,
			List<Integer> lineList) {
		
		List<CommonOrderInfo> resultList = new ArrayList<>();
		// 按明细行数组拆分
		int fromIndex = 0;
		List<OrderItemInfo> list = commonOrderInfo.getOrderItemInfo();
		for (int rang : lineList) {
			List<OrderItemInfo> subList = list.subList(fromIndex, fromIndex + rang);
			list.subList(fromIndex, fromIndex).clear();
			fromIndex += rang;
			CommonOrderInfo rebuildCommonOrderInfo = rebuildCommonOrderInfo(commonOrderInfo.getOrderInfo(), subList);
			resultList.add(rebuildCommonOrderInfo);
		}

		if (list.size() > 0) {
			CommonOrderInfo rebuildCommonOrderInfo = rebuildCommonOrderInfo(commonOrderInfo.getOrderInfo(), list);
			resultList.add(rebuildCommonOrderInfo);
		}
		return resultList;
	}

	/**
	 * 根据明细行限制拆分
	 */
	public static List<CommonOrderInfo> orderSplitByLimitLine(CommonOrderInfo commonOrder, int rangLimit) {
		List<CommonOrderInfo> list = new ArrayList<>();
		// 拆分的明细行数
		int rang = Integer.valueOf(rangLimit);
		
		if (commonOrder.getOrderItemInfo().size() < rang) {
			list.add(commonOrder);
			return list;
		}
		list = recursionSplitItem(commonOrder, list, false, rang);
		return list;
	}

	/**
	 * 明细行递归拆分
	 */
	private static List<CommonOrderInfo> recursionSplitItem(CommonOrderInfo commonOrder, List<CommonOrderInfo> commonList,
			boolean isSplitRange, int limitRang) {
		// List<CommonOrderInfo> commonList = new ArrayList<CommonOrderInfo>();
		if (commonOrder.getOrderItemInfo().size() > limitRang) {
			isSplitRange = true;
			List<OrderItemInfo> originList = commonOrder.getOrderItemInfo();
			int endIndex = limitRang - 1;
			OrderItemInfo maxRangOrdetItem = originList.get(endIndex - 1);
			// 正常商品行
			List<OrderItemInfo> subList = new ArrayList<>();
			
			if (OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(maxRangOrdetItem.getFphxz())) {
				
				subList = originList.subList(0, endIndex);
				originList = originList.subList(endIndex, originList.size());
				
				// 折扣行
			} else if (OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(maxRangOrdetItem.getFphxz())) {
				subList = originList.subList(0, endIndex);
				originList = originList.subList(endIndex, originList.size());

				// 被折扣行
			} else {
				subList = originList.subList(0, endIndex - 1);
				originList = originList.subList(endIndex - 1, originList.size());
			}

			// 重算金额 重新构建订单信息
			CommonOrderInfo subCommon = rebuildCommonOrderInfo(commonOrder.getOrderInfo(), subList);
			commonList.add(subCommon);
			commonOrder.setOrderItemInfo(originList);
			recursionSplitItem(commonOrder, commonList, isSplitRange, limitRang);
		} else {
			// 是否是拆分后的最后一行
			if (isSplitRange) {
				rebuildCommonOrderInfo(commonOrder.getOrderInfo(), commonOrder.getOrderItemInfo());
			}
			commonList.add(commonOrder);
		}

		return commonList;
    }
    
    /**
     *
     * @Title : rebuildCommonOrderInfo @Description
     * ：重新构建订单信息 @param @param orderInfo @param @param
     * subList @param @return @return CommonOrderInfo @exception
	 *
	 */
	private static CommonOrderInfo rebuildCommonOrderInfo(OrderInfo orderInfo, List<OrderItemInfo> subList) {
		// 重新计算金额和税额
		CommonOrderInfo comm = new CommonOrderInfo();
		OrderInfo copyOrderInfo = new OrderInfo();
		BeanUtils.copyProperties(orderInfo, copyOrderInfo);
		Double hjje = 0.00;
		Double hjse = 0.00;
		Double jshj = 0.00;
		for (OrderItemInfo orderItem : subList) {
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
		if (OrderInfoEnum.HSBZ_0.getKey().equals(subList.get(0).getHsbz())) {
			jshj = new BigDecimal(hjje).add(new BigDecimal(hjse)).setScale(2, RoundingMode.HALF_UP).doubleValue();
			copyOrderInfo.setHjbhsje(DecimalCalculateUtil.decimalFormat(hjje, ConfigureConstant.INT_2));
			copyOrderInfo.setKphjje(DecimalCalculateUtil.decimalFormat(jshj, ConfigureConstant.INT_2));
			copyOrderInfo.setHjse(DecimalCalculateUtil.decimalFormat(hjse, ConfigureConstant.INT_2));
		} else {
			copyOrderInfo.setKphjje(DecimalCalculateUtil.decimalFormat(jshj, ConfigureConstant.INT_2));
		}
		comm.setOrderInfo(copyOrderInfo);
		comm.setOrderItemInfo(subList);
		return comm;
    }
    
    /**
     *
     * @Title : formatCommonOrder
     * @Description : 格式化数量和单价
     * @param @param commonOrderInfo
     * @param @return
     * @return CommonOrderInfo
     * @exception
     *
	  */
	 private static CommonOrderInfo formatCommonOrder(CommonOrderInfo commonOrderInfo) {
	    	
		 
		    int i = 1;
	    	for(OrderItemInfo itemInfo : commonOrderInfo.getOrderItemInfo()){
	    		if(StringUtils.isNotBlank(itemInfo.getXmje())){
	    			itemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmje(), ConfigureConstant.INT_2));
			    }
			    if (StringUtils.isNotBlank(itemInfo.getSe())){
	    			itemInfo.setSe(DecimalCalculateUtil.decimalFormatToString(itemInfo.getSe(), ConfigureConstant.INT_2));
				
			    }
			
			
			    if(StringUtils.isNotBlank(itemInfo.getXmdj())){
	            	itemInfo.setXmdj(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmdj(), ConfigureConstant.INT_8));
			    }
			
			    if(StringUtils.isNotBlank(itemInfo.getXmsl())) {
				    itemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmsl(), ConfigureConstant.INT_8));
			    }
			    itemInfo.setSphxh(String.valueOf(i));
	            i++;
	    		
	    	}
	    	
	    	if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getKphjje())){
	    		commonOrderInfo.getOrderInfo().setKphjje(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getKphjje(), ConfigureConstant.INT_2));
		    }
		
		 if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getHjbhsje())){
	        	commonOrderInfo.getOrderInfo().setHjbhsje(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getHjbhsje(), ConfigureConstant.INT_2));
		 }
		
		 if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getHjse())){
	        	commonOrderInfo.getOrderInfo().setHjse(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getHjse(), ConfigureConstant.INT_2));
		 }
		
		 return commonOrderInfo;
	 }
	 
	/* public static void main(String[] args) throws OrderSplitException {
		String str = "{\"orderInfo\": {\"bbmBbh\": \"33.0\",\"byzd1\": \"\",\"byzd2\": \"\",\"byzd3\": \"\",\"byzd4\": \"\",\"byzd5\": \"\",\"bz\": \"223232300000077970090019196\",\"chyy\": \"\",\"createTime\": \"2020-01-06 18:51:48\",\"czdm\": \"10\",\"ddh\": \"0000007797\",\"ddlx\": \"0\",\"ddrq\": \"2020-01-06 18:51:48\",\"dkbz\": \"0\",\"dsptbm\": \"\",\"fhr\": \"3\",\"fpqqlsh\": \"10002020010600015\",\"fpzlDm\": \"0\",\"ghfDh\": \"\",\"ghfDz\": \"上汽收票地址88888888888888888888\",\"ghfEmail\": \"\",\"ghfId\": \"\",\"ghfMc\": \"上海汽车集团股份有限公司\",\"ghfNsrsbh\": \"91310000132260250X\",\"ghfQylx\": \"01\",\"ghfSf\": \"\",\"ghfSj\": \"\",\"ghfYh\": \"安亭支行1 445158750151649999100\",\"ghfZh\": \"445158750151649999100\",\"hjbhsje\": \"118624.89\",\"hjse\": \"15421.24\",\"hyDm\": \"\",\"hyMc\": \"\",\"id\": \"374116387120902145\",\"kphjje\": \"134046.13\",\"kpjh\": \"1\",\"kplx\": \"0\",\"kpr\": \"122\",\"kpxm\": \"领航滑轨端头大塑料饰板，左侧\",\"mdh\": \"\",\"nsrdzdah\": \"140301206111099566\",\"nsrmc\": \"销售方名称测试\",\"nsrsbh\": \"140301206111099566\",\"processId\": \"374116387125096448\",\"qdBz\": \"0\",\"qdXmmc\": \"\",\"skr\": \"2\",\"sld\": \"188\",\"swjgDm\": \"\",\"thdh\": \"\",\"tqm\": \"\",\"tschbz\": \"0\",\"updateTime\": \"2020-01-06 18:51:48\",\"xhfDh\": \"077273338970\",\"xhfDz\": \"销售方地址\",\"xhfId\": \"\",\"xhfMc\": \"销售方名称测试\",\"xhfNsrsbh\": \"140301206111099566\",\"xhfYh\": \"中国人民银行\",\"xhfZh\": \"1234567890123\",\"yfpDm\": \"\",\"yfpHm\": \"\",\"ywlx\": \"\"	},	\"orderItemInfo\": [{\"byzd1\": \"0000007797/1\",\"byzd2\": \"\",\"byzd3\": \"\",\"createTime\": \"2020-01-06 18:51:48\",\"fphxz\": \"2\",\"ggxh\": \"10003400-ASA\",\"hsbz\": \"0\",\"id\": \"374116387133485056\",\"kce\": \"\",\"lslbs\": \"\",\"orderInfoId\": \"374116387120902145\",\"se\": \"15600.00\",\"sl\": \"0.13\",\"spbm\": \"1090310019900000000\",\"sphxh\": \"1\",\"wcje\": \"0.00\",\"xmdj\": \"120000.00\",\"xmdw\": \"个\",\"xmje\": \"120000.00\",\"xmmc\": \"*交通运输设备*领航滑轨端头大塑料饰板，左侧\",\"xmsl\": \"1.00\",\"yhzcbs\": \"0\",\"zxbm\": \"\",\"zzstsgl\": \"\"	}, {\"byzd1\": \"0000007797/1\",\"byzd2\": \"\",\"byzd3\": \"\",\"createTime\": \"2020-01-06 18:51:48\",\"fphxz\": \"1\",\"ggxh\": \"\",\"hsbz\": \"0\",\"id\": \"374116387137679360\",\"kce\": \"\",\"lslbs\": \"\",\"orderInfoId\": \"374116387120902145\",\"se\": \"-258.65\",\"sl\": \"0.13\",\"spbm\": \"1090310019900000000\",\"sphxh\": \"2\",\"wcje\": \"0.00\",\"xmdj\": \"\",\"xmdw\": \"\",\"xmje\": \"-1989.64\",\"xmmc\": \"*交通运输设备*领航滑轨端头大塑料饰板，左侧\",\"xmsl\": \"\",\"yhzcbs\": \"0\",\"zxbm\": \"\",\"zzstsgl\": \"\"	}, {\"byzd1\": \"0000007797/2\",\"byzd2\": \"\",\"byzd3\": \"\",\"createTime\": \"2020-01-06 18:51:48\",\"fphxz\": \"2\",\"ggxh\": \"10099913-ESA\",\"hsbz\": \"0\",\"id\": \"374116387137679361\",\"kce\": \"\",\"lslbs\": \"\",\"orderInfoId\": \"374116387120902145\",\"se\": \"81.24\",\"sl\": \"0.13\",\"spbm\": \"1090310019900000000\",\"sphxh\": \"3\",\"wcje\": \"0.00\",\"xmdj\": \"624.89\",\"xmdw\": \"个\",\"xmje\": \"624.89\",\"xmmc\": \"*交通运输设备*100%后靠背，织物+三头枕\",\"xmsl\": \"1.00\",\"yhzcbs\": \"0\",\"zxbm\": \"\",\"zzstsgl\": \"\"	}, {\"byzd1\": \"0000007797/2\",\"byzd2\": \"\",\"byzd3\": \"\",\"createTime\": \"2020-01-06 18:51:48\",\"fphxz\": \"1\",\"ggxh\": \"\",\"hsbz\": \"0\",\"id\": \"374116387141873664\",\"kce\": \"\",\"lslbs\": \"\",\"orderInfoId\": \"374116387120902145\",\"se\": \"-1.35\",\"sl\": \"0.13\",\"spbm\": \"1090310019900000000\",\"sphxh\": \"4\",\"wcje\": \"0.00\",\"xmdj\": \"\",\"xmdw\": \"\",\"xmje\": \"-10.36\",\"xmmc\": \"*交通运输设备*100%后靠背，织物+三头枕\",\"xmsl\": \"\",\"yhzcbs\": \"0\",\"zxbm\": \"\",\"zzstsgl\": \"\"	}],	\"processInfo\": {\"createTime\": \"2020-01-06 18:51:48\",\"ddcjsj\": \"2020-01-06 18:51:48\",\"ddh\": \"0000007797\",\"ddlx\": \"0\",\"ddly\": \"3\",\"ddqqpch\": \"01002807202001061051425638934333\",\"ddzt\": \"0\",\"fpqqlsh\": \"10002020010600015\",\"fpzlDm\": \"0\",\"ghfMc\": \"上海汽车集团股份有限公司\",\"ghfNsrsbh\": \"91310000132260250X\",\"hjbhsje\": \"118624.89\",\"id\": \"374116387125096448\",\"kpfs\": \"1\",\"kphjje\": \"134046.13\",\"kpse\": \"15421.24\",\"kpxm\": \"领航滑轨端头大塑料饰板，左侧\",\"orderInfoId\": \"374116387120902145\",\"orderStatus\": \"0\",\"sbyy\": \"\",\"updateTime\": \"2020-01-06 18:51:48\",\"xhfMc\": \"销售方名称测试\",\"xhfNsrsbh\": \"140301206111099566\",\"ywlx\": \"\"	},	\"singleSl\": false,	\"termianlCode\": \"002\"}";
		CommonOrderInfo common = JsonUtils.getInstance().parseObject(str, CommonOrderInfo.class);

		OrderSplitConfig config = new OrderSplitConfig();
		config.setSplitType(OrderSplitEnum.ORDER_SPLIT_TYPE_1.getKey());
		config.setSplitRule("0");
		config.setLimitJe(100000.00);
		List<CommonOrderInfo> orderSplit = OrderSplitUtil.orderSplit(common, config);


		System.out.println(JsonUtils.getInstance().toJsonString(orderSplit));

		String str1 = "";



	}*/

	/**
	 * 处理 如果拆分后 税额 金额不符合规范，对金额税额进行再次处理
	 */
	/*private void dealAfterSplitJeOrSeWc(List<CommonOrderInfo> commonList){

		//判断拆分后最后一张的金额是否符合开票规则
		if(CollectionUtils.isNotEmpty(commonList)){

			CommonOrderInfo lastCommon = commonList.get(commonList.size() -1);

			//判断是否是折扣商品
			if(OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(commonList.get(0).getOrderItemInfo().get(0).getFphxz())){

				List<OrderItemInfo> orderItemInfo = lastCommon.getOrderItemInfo();

				OrderItemInfo lastOrderItem  = orderItemInfo.get(orderItemInfo.size() - 1);
				//折扣行处理
				BigDecimal xmje = new BigDecimal(lastOrderItem.getXmje());
				BigDecimal xmse = new BigDecimal(lastOrderItem.getSe());
				if(xmse.compareTo(xmje) >= 0){

				}

				//被折扣行处理
				OrderItemInfo firstItem  = orderItemInfo.get(orderItemInfo.size() - 2);
				BigDecimal zkje = new BigDecimal(firstItem.getXmje());
				BigDecimal zkse = new BigDecimal(firstItem.getSe());
				if(zkse.compareTo(zkje) >= 0){

				}


			}else{

				//非折扣发票处理
				List<OrderItemInfo> orderItemInfo = lastCommon.getOrderItemInfo();
				if(CollectionUtils.isNotEmpty(orderItemInfo)){
					OrderItemInfo lastOrderItem  = orderItemInfo.get(orderItemInfo.size() - 1);

					BigDecimal xmje = new BigDecimal(lastOrderItem.getXmje());
					BigDecimal xmse = new BigDecimal(lastOrderItem.getSe());

					//如果最后拆分的明细的项目税额大于金额
					if(xmse.compareTo(xmje) >= 0){


					}

				}

			}

		}

	}*/

	public static void main(String[] args) throws OrderSplitException {
		String str = "{\n" +
				"\t\"orderInfo\": {\n" +
				"\t\t\"bbmBbh\": \"33.0\",\n" +
				"\t\t\"bz\": \"\",\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:53\",\n" +
				"\t\t\"ddh\": \"52107058292432715811\",\n" +
				"\t\t\"ddlx\": \"0\",\n" +
				"\t\t\"ddrq\": \"2020-04-02 11:40:53\",\n" +
				"\t\t\"dkbz\": \"0\",\n" +
				"\t\t\"fhr\": \"陈爽\",\n" +
				"\t\t\"fpqqlsh\": \"405535691004661761\",\n" +
				"\t\t\"fpzlDm\": \"51\",\n" +
				"\t\t\"ghfDh\": \"\",\n" +
				"\t\t\"ghfDz\": \"\",\n" +
				"\t\t\"ghfEmail\": \"\",\n" +
				"\t\t\"ghfMc\": \"西北民航机场建设集团有限责任公司工会委员会\",\n" +
				"\t\t\"ghfNsrsbh\": \"8161000005692208XP\",\n" +
				"\t\t\"ghfQylx\": \"01\",\n" +
				"\t\t\"ghfSj\": \"\",\n" +
				"\t\t\"ghfYh\": \"\",\n" +
				"\t\t\"ghfZh\": \"\",\n" +
				"\t\t\"hjbhsje\": \"100000.00\",\n" +
				"\t\t\"hjse\": \"0.00\",\n" +
				"\t\t\"id\": \"405535759191592961\",\n" +
				"\t\t\"kphjje\": \"100000.00\",\n" +
				"\t\t\"kpjh\": \"\",\n" +
				"\t\t\"kplx\": \"0\",\n" +
				"\t\t\"kpr\": \"陈冠宇\",\n" +
				"\t\t\"kpxm\": \"佳农 烟台特级红富士苹果 礼盒 15个装 单果重约230g 生鲜水果\",\n" +
				"\t\t\"nsrmc\": \"销项测试有限公司\",\n" +
				"\t\t\"nsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"processId\": \"405535691646390271\",\n" +
				"\t\t\"qdBz\": \"0\",\n" +
				"\t\t\"skr\": \"李佳孟\",\n" +
				"\t\t\"sld\": \"\",\n" +
				"\t\t\"thdh\": \"\",\n" +
				"\t\t\"tschbz\": \"0\",\n" +
				"\t\t\"updateTime\": \"2020-04-02 11:40:53\",\n" +
				"\t\t\"xhfDh\": \"010-82609318\",\n" +
				"\t\t\"xhfDz\": \"北京市昌平区科技园区超前路37号院16号楼2层B0293\",\n" +
				"\t\t\"xhfMc\": \"销项测试有限公司\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"xhfYh\": \"光大银行北京苏州街支行\",\n" +
				"\t\t\"xhfZh\": \"35330188000101150\",\n" +
				"\t\t\"yfpDm\": \"\",\n" +
				"\t\t\"yfpHm\": \"\",\n" +
				"\t\t\"ywlx\": \"\"\n" +
				"\t},\n" +
				"\t\"orderItemInfo\": [{\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"fphxz\": \"0\",\n" +
				"\t\t\"ggxh\": \"\",\n" +
				"\t\t\"hsbz\": \"0\",\n" +
				"\t\t\"id\": \"405535759195787201\",\n" +
				"\t\t\"kce\": \"\",\n" +
				"\t\t\"lslbs\": \"3\",\n" +
				"\t\t\"orderInfoId\": \"405535759191592961\",\n" +
				"\t\t\"se\": \"0.00\",\n" +
				"\t\t\"sl\": \"0.00\",\n" +
				"\t\t\"spbm\": \"1010115010100000000\",\n" +
				"\t\t\"sphxh\": \"1\",\n" +
				"\t\t\"wcje\": \"0.00\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"xmdj\": \"116.62\",\n" +
				"\t\t\"xmdw\": \"\",\n" +
				"\t\t\"xmje\": \"14577.50\",\n" +
				"\t\t\"xmmc\": \"*水果*佳农 烟台特级红富士苹果 礼盒 15个装 单果重约230g 生鲜水果\",\n" +
				"\t\t\"xmsl\": \"125.00\",\n" +
				"\t\t\"yhzcbs\": \"0\",\n" +
				"\t\t\"zzstsgl\": \"\"\n" +
				"\t}, {\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"fphxz\": \"0\",\n" +
				"\t\t\"ggxh\": \"\",\n" +
				"\t\t\"hsbz\": \"0\",\n" +
				"\t\t\"id\": \"405535691646390201\",\n" +
				"\t\t\"kce\": \"\",\n" +
				"\t\t\"lslbs\": \"3\",\n" +
				"\t\t\"orderInfoId\": \"405535759191592961\",\n" +
				"\t\t\"se\": \"0.00\",\n" +
				"\t\t\"sl\": \"0.00\",\n" +
				"\t\t\"spbm\": \"1010402000000000000\",\n" +
				"\t\t\"sphxh\": \"2\",\n" +
				"\t\t\"wcje\": \"0.00\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"xmdj\": \"155.82\",\n" +
				"\t\t\"xmdw\": \"\",\n" +
				"\t\t\"xmje\": \"19477.50\",\n" +
				"\t\t\"xmmc\": \"*海水产品*京东海外直采 马达加斯加老虎虾/黑虎虾（大号）800g\",\n" +
				"\t\t\"xmsl\": \"125.00\",\n" +
				"\t\t\"yhzcbs\": \"0\",\n" +
				"\t\t\"zzstsgl\": \"\"\n" +
				"\t}, {\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"fphxz\": \"0\",\n" +
				"\t\t\"ggxh\": \"\",\n" +
				"\t\t\"hsbz\": \"0\",\n" +
				"\t\t\"id\": \"405535759199981501\",\n" +
				"\t\t\"kce\": \"\",\n" +
				"\t\t\"lslbs\": \"3\",\n" +
				"\t\t\"orderInfoId\": \"405535759191592961\",\n" +
				"\t\t\"se\": \"0.00\",\n" +
				"\t\t\"sl\": \"0.00\",\n" +
				"\t\t\"spbm\": \"1010112020000000000\",\n" +
				"\t\t\"sphxh\": \"3\",\n" +
				"\t\t\"wcje\": \"0.00\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"xmdj\": \"57.62\",\n" +
				"\t\t\"xmdw\": \"\",\n" +
				"\t\t\"xmje\": \"7202.50\",\n" +
				"\t\t\"xmmc\": \"*蔬菜*聚怀斋 焦作温县沙土铁棍山药（精选80/90公分）怀山药 新鲜蔬菜\",\n" +
				"\t\t\"xmsl\": \"125.00\",\n" +
				"\t\t\"yhzcbs\": \"0\",\n" +
				"\t\t\"zzstsgl\": \"\"\n" +
				"\t}, {\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"fphxz\": \"1\",\n" +
				"\t\t\"ggxh\": \"\",\n" +
				"\t\t\"hsbz\": \"0\",\n" +
				"\t\t\"id\": \"405535691654778801\",\n" +
				"\t\t\"kce\": \"\",\n" +
				"\t\t\"lslbs\": \"3\",\n" +
				"\t\t\"orderInfoId\": \"405535759191592961\",\n" +
				"\t\t\"se\": \"0.00\",\n" +
				"\t\t\"sl\": \"0.00\",\n" +
				"\t\t\"spbm\": \"1010401000000000000\",\n" +
				"\t\t\"sphxh\": \"8\",\n" +
				"\t\t\"wcje\": \"0.00\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"xmdj\": \"\",\n" +
				"\t\t\"xmdw\": \"\",\n" +
				"\t\t\"xmje\": \"-902.50\",\n" +
				"\t\t\"xmmc\": \"*海水产品*三都港 深海有机宁德大黄花鱼500g 海鲜水产 生鲜\",\n" +
				"\t\t\"xmsl\": \"\",\n" +
				"\t\t\"yhzcbs\": \"0\",\n" +
				"\t\t\"zzstsgl\": \"\"\n" +
				"\t}],\n" +
				"\t\"processInfo\": {\n" +
				"\t\t\"createTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"ddcjsj\": \"2020-04-02 11:40:53\",\n" +
				"\t\t\"ddh\": \"52107058292432715811\",\n" +
				"\t\t\"ddlx\": \"0\",\n" +
				"\t\t\"ddly\": \"1\",\n" +
				"\t\t\"ddzt\": \"0\",\n" +
				"\t\t\"fpqqlsh\": \"405535691004661761\",\n" +
				"\t\t\"fpzlDm\": \"51\",\n" +
				"\t\t\"ghfMc\": \"西北民航机场建设集团有限责任公司工会委员会\",\n" +
				"\t\t\"ghfNsrsbh\": \"8161000005692208XP\",\n" +
				"\t\t\"hjbhsje\": \"100000.00\",\n" +
				"\t\t\"id\": \"405535691646390271\",\n" +
				"\t\t\"kphjje\": \"100000.00\",\n" +
				"\t\t\"kpse\": \"0.00\",\n" +
				"\t\t\"kpxm\": \"佳农 烟台特级红富士苹果 礼盒 15个装 单果重约230g 生鲜水果\",\n" +
				"\t\t\"orderInfoId\": \"405535759191592961\",\n" +
				"\t\t\"orderStatus\": \"0\",\n" +
				"\t\t\"sbyy\": \"\",\n" +
				"\t\t\"updateTime\": \"2020-04-02 11:40:54\",\n" +
				"\t\t\"xhfMc\": \"销项测试有限公司\",\n" +
				"\t\t\"xhfNsrsbh\": \"15000120561127953X\",\n" +
				"\t\t\"ywlx\": \"\"\n" +
				"\t},\n" +
				"\t\"singleSl\": false,\n" +
				"\t\"terminalCode\": \"001\"\n" +
				"}";
        String conf = "{\"limitJe\":99999.99,\"splitRule\":\"0\",\"splitType\":\"1\"}";
        CommonOrderInfo common = JsonUtils.getInstance().parseObject(str,CommonOrderInfo.class);
        OrderSplitConfig config = JsonUtils.getInstance().parseObject(conf,OrderSplitConfig.class);
        orderSplit(common,config);

		
	}
	
}
