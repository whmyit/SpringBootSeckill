package com.dxhy.order.utils;

import com.dxhy.order.constant.*;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单价税分离接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 21:09
 */
@Slf4j
public class PriceTaxSeparationUtil{
	
    private static final String LOGGER_MSG = "(价税分离)";

    /**
     * 价税分离流程
     * 1.去除红票中折扣行和被折扣行相加等于0的订单，判断订单是否是单税率 补全单价和数量
     * 2.根据是否是单税率，区分是单税率价税分离还是多税率价税分离
     * 3.返回价税分离后的数据
     */
	
    public static CommonOrderInfo taxSeparationService(CommonOrderInfo commonOrderInfo,TaxSeparateConfig config) throws OrderSeparationException{
    	
        log.debug("{}价税分离接收数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
        
        //添加红票多税率 带清单发票 明细中没有税率的校验 直接返回
        if(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonOrderInfo.getOrderInfo().getKplx())){
        	if(commonOrderInfo.getOrderItemInfo().size() == 1 && commonOrderInfo.getOrderItemInfo().get(0).getFphxz().equals(OrderInfoEnum.FPHXZ_CODE_6.getKey())
        			&& StringUtils.isBlank(commonOrderInfo.getOrderItemInfo().get(0).getSl())){
        		return commonOrderInfo;
        	}
        	
        }
       
        //去除红票中折扣行和被折扣行相加等于0的订单，判断订单是否是单税率 补全数量和单价
        commonOrderInfo = dealBeforeSeparate(commonOrderInfo);
        
       
        
        //价税分离
        if(commonOrderInfo.isSingleSl()){
        	//单税率价税分离
            commonOrderInfo = separateSingleSl(commonOrderInfo,config);
        }else{
        	//多税率价税分离
            commonOrderInfo = separate(commonOrderInfo,config);
        }
        //价税分离后的数据格式化
        commonOrderInfo = formatCommonOrder(commonOrderInfo);
        return commonOrderInfo;
    }
    
    /**
     * @param @param  commonOrderInfo
     * @param @return
     * @return CommonOrderInfo
     * @throws
     * @Title : formatCommonOrder
     * @Description ：格式化单价和金额
     */
    private static CommonOrderInfo formatCommonOrder(CommonOrderInfo commonOrderInfo) {
    	
    	for(OrderItemInfo itemInfo : commonOrderInfo.getOrderItemInfo()){
    		if(StringUtils.isNotBlank(itemInfo.getXmje())){
			    itemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmje(), ConfigureConstant.INT_2));
		    }
            if(StringUtils.isNotBlank(itemInfo.getSe())) {
	            itemInfo.setSe(DecimalCalculateUtil.decimalFormatToString(itemInfo.getSe(), ConfigureConstant.INT_2));
	
            }
            
            if(StringUtils.isNotBlank(itemInfo.getXmdj())) {
	            itemInfo.setXmdj(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmdj(), ConfigureConstant.INT_8));
            }
            
            if(StringUtils.isNotBlank(itemInfo.getXmsl())) {
	            itemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(itemInfo.getXmsl(), ConfigureConstant.INT_8));
            }
    		
    	}
    	
    	if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getKphjje())) {
		    commonOrderInfo.getOrderInfo().setKphjje(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getKphjje(), ConfigureConstant.INT_2));
	    }
    	
        if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getHjbhsje())) {
	        commonOrderInfo.getOrderInfo().setHjbhsje(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getHjbhsje(), ConfigureConstant.INT_2));
        }
        
        if(StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getHjse())) {
	        commonOrderInfo.getOrderInfo().setHjse(DecimalCalculateUtil.decimalFormatToString(commonOrderInfo.getOrderInfo().getHjse(), ConfigureConstant.INT_2));
        }
		
		return commonOrderInfo;
	}

	/**
     * 单税率价税分离流程
     * 1.根据含税标志判断订单是否含税，根据是否含税判断订单是走含税还是不含税价税分离
     * 2.当用户的配置为非平衡误差方式，并且订单数据为非含税时，判断用户配置是否需要 总金额  * 税率 = 税额 如果需要 再次平均分配误差
     * 3.返回价税分离后的数据
     *
     */
	private static CommonOrderInfo separateSingleSl(CommonOrderInfo commonOrderInfo, TaxSeparateConfig config) throws OrderSeparationException {
		//  OrderInfoEnum.HSBZ_0.getKey() 原代码 OrderInfoEnum.HSBZ_0
		if (OrderInfoEnum.HSBZ_0.getKey().equals(commonOrderInfo.getOrderItemInfo().get(0).getHsbz())) {
//			if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getHjbhsje())) {
//				throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_BHSJE_NULL_ERROR);
//			}
			//单税率不含税价税分离
			commonOrderInfo = separateSingleSlBhs(commonOrderInfo, config);
			
			if (StringUtils.isBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce()) && ConfigureConstant.STRING_1.equals(config.getSingleSlSeparateType())) {
				dealSeWc(commonOrderInfo);
			}
		} else {
			//单税率含税价税分离
			if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getKphjje())) {
				throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_HSJE_NULL_ERROR);
			}
			commonOrderInfo = separateSingleSlHs(commonOrderInfo,config);
		}
		return commonOrderInfo;
	}
	
	/**
	 * 不含税价税分离
	 * @throws OrderSeparationException
	 */
	private static CommonOrderInfo separateSingleSlBhs(CommonOrderInfo commonOrderInfo, TaxSeparateConfig config) throws OrderSeparationException {
		//不含税价税分离
		Double hjje = 0.00;
		Double hjse = 0.00;
		BigDecimal excursion = new BigDecimal("0.00");
		BigDecimal sedf = new BigDecimal("0.00");
		int i = 0;
		for (OrderItemInfo orderItem : commonOrderInfo.getOrderItemInfo()) {
			//根据用户配置的价税分离方式决定是否平衡误差方式进行价税分离
		    if("1".equals(config.getDealSeType())){
				orderItem = separateItem(orderItem);
				
				//处理折扣行数据
	        	if(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
	        		orderItem = dealDiscountItem(orderItem,commonOrderInfo.getOrderItemInfo().get(i-1),commonOrderInfo.getOrderInfo().getKplx());
	        	}
	        	//项目名称过滤特殊字符
	        	orderItem.setXmmc(GBKUtil.replaceX(orderItem.getXmmc()));
	            //尾差记录
	            BigDecimal wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
	            orderItem.setWcje(wcje.toString());
	            
	            if(StringUtils.isBlank(orderItem.getKce())){
	            	sedf = sedf.add(new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())));
	            }
		    }else{
		    	Map<String, Object> map = separateItemWithExcursion(orderItem,excursion);
				excursion = (BigDecimal) map.get("excursion");
				orderItem = (OrderItemInfo) map.get("orderItem");
			
				
				if(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
					orderItem = dealDiscountItem(orderItem,commonOrderInfo.getOrderItemInfo().get(i-1),commonOrderInfo.getOrderInfo().getKplx());
	        	}
	        	//项目名称过滤特殊字符
				orderItem.setXmmc(GBKUtil.replaceX(orderItem.getXmmc()));
	            //尾差记录
	            BigDecimal wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
	            orderItem.setWcje(wcje.toString());
	           
	            if(StringUtils.isBlank(orderItem.getKce())){
	            	sedf = sedf.add(new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())));
	            }
		    }
			hjje = MathUtil.add(new BigDecimal(hjje), new BigDecimal(orderItem.getXmje()));
			hjse = MathUtil.add(new BigDecimal(hjse), new BigDecimal(orderItem.getSe()));
			i++;
		}
		
		/**
		 * 累计税额误差计算 当累计税额误差大于1.27时 抛出异常 ，让用户去拆分订单
		 * 扣除额的发票只有单条明细 不做税额误差累计的计算
		 */
		if (sedf.doubleValue() > ConfigureConstant.DOUBLE_PENNY_127) {
			throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_SE_WC_TOTAL);
		}
		// 重新计算合计金额 合计税额 价税合计
		Double jshj = MathUtil.add(new BigDecimal(hjje), new BigDecimal(hjse));
		commonOrderInfo.getOrderInfo().setHjbhsje(hjje.toString());
		commonOrderInfo.getOrderInfo().setHjse(hjse.toString());
		commonOrderInfo.getOrderInfo().setKphjje(jshj.toString());
		return commonOrderInfo;
	}

	/**
	 * 单税率含税价税分离的流程
	 * 1.根据用户的配置 区分用户走 绝对四舍五入的价税分离方式还是误差平衡的价税分离方式
	 * 2.累加价税分离后的金额作为不含税金额，累加价税分离后的税额作为合计税额
	 * @throws OrderSeparationException
	 */
	private static CommonOrderInfo separateSingleSlHs(CommonOrderInfo commonOrderInfo, TaxSeparateConfig config) throws OrderSeparationException {
		//含税价税分离流程
		Double hjje = 0.00;
		Double hjse = 0.00;
		BigDecimal excursion = new BigDecimal("0.00");
		BigDecimal sedf = new BigDecimal("0.00");
		int i = 0;
		for (OrderItemInfo orderItem : commonOrderInfo.getOrderItemInfo()) {
			//根据用户配置的价税分离方式决定是否平衡误差方式进行价税分离
		    if("1".equals(config.getDealSeType())){
				orderItem = separateItem(orderItem);
				
				//处理折扣行数据
	        	if(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
	        		orderItem = dealDiscountItem(orderItem,commonOrderInfo.getOrderItemInfo().get(i-1),commonOrderInfo.getOrderInfo().getKplx());
	        	}
	        	//项目名称过滤特殊字符
	        	orderItem.setXmmc(GBKUtil.replaceX(orderItem.getXmmc()));
	            //尾差记录
	            BigDecimal wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
	            orderItem.setWcje(wcje.toString());
	            
	            if(StringUtils.isBlank(orderItem.getKce())){
	            	sedf = sedf.add(new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())));
	            }
		    }else{
		    	Map<String, Object> map = separateItemWithExcursion(orderItem,excursion);
				excursion = (BigDecimal) map.get("excursion");
				orderItem = (OrderItemInfo) map.get("orderItem");
			
				
				if(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItem.getFphxz())) {
					orderItem = dealDiscountItem(orderItem,commonOrderInfo.getOrderItemInfo().get(i-1),commonOrderInfo.getOrderInfo().getKplx());
	        	}
	        	//项目名称过滤特殊字符
				orderItem.setXmmc(GBKUtil.replaceX(orderItem.getXmmc()));
	            //尾差记录
	            BigDecimal wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
	            orderItem.setWcje(wcje.toString());
	           
	            if(StringUtils.isBlank(orderItem.getKce())){
	            	sedf = sedf.add(new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getSl())).subtract(new BigDecimal(orderItem.getSe())));
	            }
		    }
			hjje = MathUtil.add(new BigDecimal(hjje), new BigDecimal(orderItem.getXmje()));
			hjse = MathUtil.add(new BigDecimal(hjse), new BigDecimal(orderItem.getSe()));
			i++;
		}
		
		/**
		 * 累计税额误差计算 当累计税额误差大于1.27时 抛出异常 ，让用户去拆分订单
		 * 扣除额的发票只有单条明细 不做税额误差累计的计算
		 */
		if (Math.abs(sedf.doubleValue()) > ConfigureConstant.DOUBLE_PENNY_127) {
			throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_SE_WC_TOTAL);
		}
		// 重新计算合计金额 合计税额 价税合计
		Double jshj = MathUtil.add(new BigDecimal(hjje), new BigDecimal(hjse));
		commonOrderInfo.getOrderInfo().setHjbhsje(hjje.toString());
		commonOrderInfo.getOrderInfo().setHjse(hjse.toString());
		commonOrderInfo.getOrderInfo().setKphjje(jshj.toString());
		return commonOrderInfo;
	}

	/**
	 * 价税分离误差平衡方式价税分离
	 *
	 */
	private static Map<String, Object> separateItemWithExcursion(OrderItemInfo orderItemInfo, BigDecimal excursion)
			throws OrderSeparationException {
		
		Map<String, Object> resultMap = new HashMap<>(5);
		
		if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz())) {
			/**
			 * 含税价税分离步骤 a.包含扣除额 1.不含税差额 = （含税金额-扣除额）/（1 + 税率）;不含税金额 = 不含税差额 +
			 * 扣除额 2.税额 = 含税金额 - 不含税金额 3.不含税单价 = 不含税金额/数量 4.重置含税标志为不含税
			 *
			 * b.没有扣除额 1.不含税金额 = 含税金额/（1 + 税率） 2.税额 = 含税金额 - 不含税金额 3.不含税单价 =
			 * 不含税金额/数量 4.重置含税标志为不含税
			 *
			 */
			double bhsje = 0.0;
			double se = 0.0;

			if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
				/**
				 * 扣除额逻辑处理 扣除额只有单条明细不需要平衡误差
				 */
				log.info("价税分离传进扣除额：{}", orderItemInfo.getKce());
				double kce = 0D;
				try {
					kce = Double.parseDouble(orderItemInfo.getKce());
				} catch (Exception e) {
					log.error("协议传输扣除额为:{},不符合规范", orderItemInfo.getKce());
					throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_KCE_FORMAT_ERROR);
				}
				double ce = DecimalCalculateUtil.sub(Double.parseDouble(orderItemInfo.getXmje()), kce);
				double bhsce = DecimalCalculateUtil.div(ce, (1 + Double.parseDouble(orderItemInfo.getSl())));
				// 不含税金额
				bhsje = DecimalCalculateUtil.add(bhsce, kce);
			} else {
				// 不含税金额
				if(OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())){
					if (excursion.doubleValue() > 0) {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, BigDecimal.ROUND_DOWN).doubleValue();
						
					} else if (excursion.doubleValue() == ConfigureConstant.DOUBLE_PENNY_ZERO) {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, RoundingMode.HALF_UP).doubleValue();
					} else {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, BigDecimal.ROUND_UP).doubleValue();
					}
					
				}else{
					if (excursion.doubleValue() > 0) {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, BigDecimal.ROUND_UP).doubleValue();
						
					} else if (excursion.doubleValue() == ConfigureConstant.DOUBLE_PENNY_ZERO) {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, RoundingMode.HALF_UP).doubleValue();
					} else {
						bhsje = new BigDecimal(orderItemInfo.getXmje())
								.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 2, BigDecimal.ROUND_DOWN).doubleValue();
					}
					
				}
				
				// 计算本次四舍五入后的误差
				BigDecimal currentExcursion = new BigDecimal(orderItemInfo.getXmje())
						.divide(BigDecimal.valueOf(1 + Double.parseDouble(orderItemInfo.getSl())), 20, RoundingMode.HALF_UP)
						.subtract(new BigDecimal(bhsje));
				// 累计本次误差
				excursion = excursion.add(currentExcursion);
			}
			// 税额
			se = DecimalCalculateUtil.sub(Double.parseDouble(orderItemInfo.getXmje()), bhsje);
			// 不含税单价
			if (!StringUtils.isBlank(orderItemInfo.getXmdj())) {
				double xmdj = DecimalCalculateUtil.div(bhsje, Double.parseDouble(orderItemInfo.getXmsl()), 8);
				orderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
			}

			orderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
			orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(bhsje, ConfigureConstant.INT_2));
			orderItemInfo.setHsbz(OrderInfoEnum.HSBZ_0.getKey());

		} else {
			
			double se = 0.00;
			if (!StringUtils.isBlank(orderItemInfo.getSe())) {
				/**
				 * 不含税价税分离处理 用户传递不含税金额和税额，对用户传递数据进行计算校验， 用户传递税额如果和计算后数据不一致，日志提示。
				 */
				if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
					se = MathUtil.mul(DecimalCalculateUtil.decimalFormat(
							MathUtil.sub(orderItemInfo.getXmje(), orderItemInfo.getKce()), ConfigureConstant.INT_2),
							orderItemInfo.getSl());
				} else {
					se = MathUtil.mul(new BigDecimal(orderItemInfo.getXmje()),new BigDecimal(orderItemInfo.getSl()));
				}

				if (Math.abs(DecimalCalculateUtil.sub(se,
						Double.parseDouble(orderItemInfo.getSe()))) > ConfigureConstant.DOUBLE_PENNY_SIX) {
					log.error("价税分离，税额校验失败，用户传递数据和计算后数据不一致，大于六分钱,抛出异常");
					throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_SE_WC_ERROR);
				} else {
					se = Double.parseDouble(orderItemInfo.getSe());
				}

			} else {
				/**
				 * 用户如果没有传递税额 计算 税额 = 金额 * 税率  根据上次处理后的误差金额计算税额
				 */
				if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
					//扣除额 只有单条明细 不做误差的平衡处理
					se = MathUtil.mul(DecimalCalculateUtil.decimalFormat(
							MathUtil.sub(orderItemInfo.getXmje(), orderItemInfo.getKce()), ConfigureConstant.INT_2),
							orderItemInfo.getSl());
				} else {
				    //根据误差处理
					se = DecimalCalculateUtil.mul(Double.parseDouble(orderItemInfo.getXmje()),
							Double.parseDouble(orderItemInfo.getSl()));
				}
			}

			// 税额
			orderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
			orderItemInfo.setHsbz(OrderInfoEnum.HSBZ_0.getKey());
		}
		resultMap.put("excursion", excursion);
		resultMap.put("orderItem", orderItemInfo);
		return resultMap;
	}

   /**
    * update by ysy
     * 价税分离流程：
     * 价税分离只根据明细里面的金额做金额的计算，订单中的金额信息会根据明细中的金额信息重新赋值
     * 含税流程：
     * 1.根据订单中的明细的含税标志区分订单是否含税
     * 2.根据含税金额，计算不含税金额，不含税单价和税额
     * 不含税流程:
     * 1.根据不含税金额计算税额
     * 不含税流程中需要考虑用户是否传入税额 如果用户传入税额 以用户传入的税额为准只做数据的校验
    *
     */
	private static CommonOrderInfo separate(CommonOrderInfo commonOrderInfo, TaxSeparateConfig config) throws OrderSeparationException {
		if (OrderInfoEnum.HSBZ_0.equals(commonOrderInfo.getOrderItemInfo().get(0).getHsbz())) {
		    if(StringUtils.isBlank(commonOrderInfo.getOrderInfo().getHjbhsje())){
				throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_BHSJE_NULL_ERROR);
		    }
			//单税率不含税价税分离
			commonOrderInfo = separateSingleSlBhs(commonOrderInfo,config);
		} else {
			if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getKphjje())) {
				throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_HSJE_NULL_ERROR);
			}
			commonOrderInfo = separateSingleSlHs(commonOrderInfo,config);
		}
		return commonOrderInfo;
	}

	/**
	 * 处理折扣行数据
	*/
	private static OrderItemInfo dealDiscountItem(OrderItemInfo discountOrderItemInfo, OrderItemInfo orderItemInfo,
			String kplx) {

		// 被折扣行金额之和
		double bzkje_total = 0.0;
		// 被折扣行金额之和
		double bzkse_total = 0.0;
		// 获取被折扣行的不含税金额
		bzkje_total = Double.parseDouble(orderItemInfo.getXmje());
		// 获取被折扣行的不含税税额
		bzkse_total = Double.parseDouble(orderItemInfo.getSe());
		
		/**
		 * 折扣行, 校验:折扣行金额和被折扣行金额相等,折扣行做一分钱金额调整 1.计算折扣行与被折扣行金额误差,和税额误差
		 * 2.金额无误差,税额存在误差,金额大于一分钱,税额大于一分钱 3.一分钱金额调整,
		 */
		
		double jewc = Math.abs(bzkje_total) - Math.abs(Double.parseDouble(discountOrderItemInfo.getXmje()));
		double sewc = Math.abs(bzkse_total) - Math.abs(Double.parseDouble(discountOrderItemInfo.getSe()));
		// 如果被折扣行累计金额 与 折扣行金额去绝对之后相等并且折扣行的金额和税额大于等于0.01,多折扣行做一分钱调整
		boolean result = (jewc == 0 && sewc > 0 && Math.abs(Double.parseDouble(discountOrderItemInfo.getXmje())) >= 0.01
				&& Math.abs(Double.parseDouble(discountOrderItemInfo.getSe())) >= 0.01)
				|| (jewc == 0 && sewc == 0 && Math.abs(Double.parseDouble(discountOrderItemInfo.getXmje())) == 0.00
				&& Math.abs(Double.parseDouble(discountOrderItemInfo.getSe())) == 0.01);
		if (result) {
			
			if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
				// 如果是蓝票,明细折扣行金额加上一分钱,税额减去一分钱,重新计算单价 合计不含税金额加上一分钱,合计税额减去一分钱
				discountOrderItemInfo.setXmje(
						DecimalCalculateUtil.decimalFormat(MathUtil.add(discountOrderItemInfo.getXmje(), "0.01"), 2));
				if (!StringUtils.isBlank(discountOrderItemInfo.getXmdj())) {
					discountOrderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(
							MathUtil.div(String.valueOf(MathUtil.add(discountOrderItemInfo.getXmje(), "0.01")),
									discountOrderItemInfo.getXmsl(), 8),
							8));
				}
				discountOrderItemInfo.setSe(
						DecimalCalculateUtil.decimalFormat(MathUtil.sub(discountOrderItemInfo.getSe(), "0.01"), 2));
			} else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
				// 如果是红票,明细折扣行金额减去一分钱,税额加上一分钱,重新计算单价 合计不含税金额加上一分钱,合计税额减去一分钱

				discountOrderItemInfo.setXmje(
						DecimalCalculateUtil.decimalFormat(MathUtil.sub(discountOrderItemInfo.getXmje(), "0.01"), 2));
				if (!StringUtils.isBlank(discountOrderItemInfo.getXmdj())) {
					discountOrderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(
							MathUtil.div(String.valueOf(MathUtil.sub(discountOrderItemInfo.getXmje(), "0.01")),
									discountOrderItemInfo.getXmsl(), 8),
							8));
				}
				discountOrderItemInfo.setSe(
						DecimalCalculateUtil.decimalFormat(MathUtil.add(discountOrderItemInfo.getSe(), "0.01"), 2));
				
			}
		}
		return discountOrderItemInfo;
	}

	/**
     * 单税率调整税额 单税率总税额需要保证 等于 合计金额*税率，如果不相等，在明细税额里面做调整
     */
	private static void dealSeWc(CommonOrderInfo commonOrderInfo) {
		
		String se = new BigDecimal(commonOrderInfo.getOrderInfo().getHjbhsje())
				.multiply(new BigDecimal(commonOrderInfo.getOrderItemInfo().get(0).getSl()))
				.setScale(2, RoundingMode.HALF_UP).toString();
        if (!Double.valueOf(se).equals(Double.valueOf(commonOrderInfo.getOrderInfo().getHjse()))) {
			// 税额如果不等于合计金额乘以税率

			// 税额的差值
			double diff = MathUtil.sub(commonOrderInfo.getOrderInfo().getHjse(), se);
			// 每个明细分摊的税额差值
			double eveDiff = Math.abs(new BigDecimal(diff)
					.divide(new BigDecimal(commonOrderInfo.getOrderItemInfo().size()), 2, BigDecimal.ROUND_UP)
					.doubleValue());

			double leaveSe = eveDiff;

			for (OrderItemInfo orderItem : commonOrderInfo.getOrderItemInfo()) {
				// 如果实际税额小于需要平摊的金额 跳过本条明细
				if (Math.abs(Double.parseDouble(orderItem.getSe())) <= eveDiff) {
					continue;
				}
				leaveSe = new BigDecimal(leaveSe).subtract(new BigDecimal(eveDiff))
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				if (leaveSe <= 0) {
					
					double finalSe = new BigDecimal(diff).subtract(new BigDecimal(leaveSe).add(new BigDecimal(eveDiff)))
							.setScale(2, RoundingMode.HALF_UP).doubleValue();
					if (diff > 0) {
						orderItem.setSe(new BigDecimal(orderItem.getSe()).subtract(new BigDecimal(finalSe))
								.setScale(2, RoundingMode.HALF_UP).toString());
					} else {
						orderItem.setSe(new BigDecimal(orderItem.getSe()).add(new BigDecimal(finalSe))
								.setScale(2, RoundingMode.HALF_UP).toString());
					}
					// 重新计算尾差金额
					String wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getXmsl()))
							.setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(orderItem.getSe()))
							.toString();
					orderItem.setWcje(wcje);
					break;
				} else {
					if (diff > 0) {
						orderItem.setSe(new BigDecimal(orderItem.getSe()).subtract(new BigDecimal(eveDiff))
								.setScale(2, RoundingMode.HALF_UP).toString());
					} else {
						orderItem.setSe(new BigDecimal(orderItem.getSe()).add(new BigDecimal(eveDiff))
								.setScale(2, RoundingMode.HALF_UP).toString());
					}
					
					String wcje = new BigDecimal(orderItem.getXmje()).multiply(new BigDecimal(orderItem.getXmsl()))
							.setScale(2, RoundingMode.HALF_UP).subtract(new BigDecimal(orderItem.getSe()))
							.toString();
					orderItem.setWcje(wcje);
				}
			}
	        // 重新计算合计税额
	        resetHjjeAndHjse(commonOrderInfo);
	        if (leaveSe > ConfigureConstant.DOUBLE_PENNY_ZERO) {
		        dealSeWc(commonOrderInfo);
	        }
        }

	}
	
	private static void resetHjjeAndHjse(CommonOrderInfo commonOrder) {
		// 旧的订单号
		NumberFormat num = NumberFormat.getPercentInstance();
		num.setMaximumIntegerDigits(3);
		num.setMaximumFractionDigits(2);
		
		List<OrderItemInfo> orderItemList = commonOrder.getOrderItemInfo();
		BigDecimal hjje = BigDecimal.ZERO;
		BigDecimal hjse = BigDecimal.ZERO;
		for (OrderItemInfo orderItem : orderItemList) {
			hjje = hjje.add(new BigDecimal(orderItem.getXmje())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
			if (StringUtils.isNotBlank(orderItem.getSe())) {
				hjse = hjse.add(new BigDecimal(orderItem.getSe())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
			}
		}
		// 不含税
		BigDecimal jshj = hjje.add(hjse).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP);
		commonOrder.getOrderInfo().setHjbhsje(hjje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
		commonOrder.getOrderInfo().setKphjje(jshj.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
		commonOrder.getOrderInfo().setHjse(hjse.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
		
	}

	/**
     * 订单价税分离前的数据处理
     */
	private static CommonOrderInfo dealBeforeSeparate(CommonOrderInfo commonOrderInfo) {
		List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
		List<OrderItemInfo> newOrderItemInfoList = new ArrayList<>();
		
		String firstSl = StringUtil.formatSl(orderItemInfos.get(0).getSl());
		
		//判断是否单税率
		boolean isSingleSl = true;
		
		if (!CollectionUtils.isEmpty(orderItemInfos)) {
			
			for (int i = 0; i < orderItemInfos.size(); i++) {
				if (StringUtils.isNotBlank(orderItemInfos.get(i).getSl())) {
					String currentSl = StringUtil.formatSl(orderItemInfos.get(i).getSl());
					//如果税率不一致就认为是多税率
					if (!firstSl.equals(currentSl)) {
						isSingleSl = false;
					}
					
				}
                //单价数量补全
        		completeDjAndSl(orderItemInfos.get(i));
            	if(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonOrderInfo.getOrderInfo().getKplx())){
                    //红票商品存在折扣行和被折扣行金额累加等于0的商品,去除该商品.
            		if (OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfos.get(i).getFphxz())) {
                        //折扣行金额 项目金额
                        double zkhje = Math.abs(Double.parseDouble(orderItemInfos.get(i).getXmje()));
                        double bzkhje = Math.abs(Double.parseDouble(orderItemInfos.get(i + 1).getXmje()));
                        double differ = MathUtil.sub(String.valueOf(zkhje), String.valueOf(bzkhje));
			            if ("0.00".equals(DecimalCalculateUtil.decimalFormat(differ, ConfigureConstant.INT_2))) {
				            continue;
			            } else {
				            newOrderItemInfoList.add(orderItemInfos.get(i));
			            }
                    }else if(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfos.get(i).getFphxz())) {
			            double zkhje = Math.abs(Double.parseDouble(orderItemInfos.get(i).getXmje()));
			            double bzkhje = Math.abs(Double.parseDouble(orderItemInfos.get(i - 1).getXmje()));
			            double differ = MathUtil.sub(String.valueOf(zkhje), String.valueOf(bzkhje));
			            if ("0.00".equals(DecimalCalculateUtil.decimalFormat(differ, ConfigureConstant.INT_2))) {
				            continue;
			            } else {
				            newOrderItemInfoList.add(orderItemInfos.get(i));
			            }
		            }else{
                    	newOrderItemInfoList.add(orderItemInfos.get(i));

                    }
            	}else{
            		newOrderItemInfoList.add(orderItemInfos.get(i));
            	}
            }
        }
        commonOrderInfo.setOrderItemInfo(newOrderItemInfoList);
        commonOrderInfo.setSingleSl(isSingleSl);
        return commonOrderInfo;
	}

	/***
	 * 明细价税分离
	 */
	private static OrderItemInfo separateItem(OrderItemInfo orderItemInfo) throws OrderSeparationException {
		// 补全单价数量 对税率进行格式化

		if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz())) {
			/**
			 * 含税价税分离步骤
			 * a.包含扣除额
			 * 1.不含税差额 = （含税金额-扣除额）/（1 + 税率）;不含税金额  = 不含税差额 + 扣除额
			 * 2.税额 = 含税金额 - 不含税金额
			 * 3.不含税单价 = 不含税金额/数量
			 * 4.重置含税标志为不含税
			 *
			 * b.没有扣除额
			 * 1.不含税金额 = 含税金额/（1 + 税率）
			 * 2.税额 = 含税金额 - 不含税金额
			 * 3.不含税单价 = 不含税金额/数量
			 * 4.重置含税标志为不含税
			 *
			 */
			double bhsje = 0.0;
			double se = 0.0;
			
			if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
				/**
				 * 扣除额逻辑处理
				 */
				log.info("价税分离传进扣除额：{}", orderItemInfo.getKce());
				double kce = 0D;
				try {
					kce = Double.parseDouble(orderItemInfo.getKce());
				} catch (Exception e) {
					log.error("协议传输扣除额为:{},不符合规范", orderItemInfo.getKce());
					throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_KCE_FORMAT_ERROR);
				}
				double ce = DecimalCalculateUtil.sub(Double.parseDouble(orderItemInfo.getXmje()), kce);
				double bhsce = DecimalCalculateUtil.div(ce, (1 + Double.parseDouble(orderItemInfo.getSl())));
				// 不含税金额
				bhsje = DecimalCalculateUtil.add(bhsce, kce);
			} else {
				// 不含税金额
				bhsje = DecimalCalculateUtil.div(Double.parseDouble(orderItemInfo.getXmje()),
						(1 + Double.parseDouble(orderItemInfo.getSl())));
			}
			// 税额
			se = DecimalCalculateUtil.sub(Double.parseDouble(orderItemInfo.getXmje()), bhsje);
			// 不含税单价
			if (!StringUtils.isBlank(orderItemInfo.getXmdj())) {
				double xmdj = DecimalCalculateUtil.div(bhsje, Double.parseDouble(orderItemInfo.getXmsl()), 8);
				orderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
			}
			
			orderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
			orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormat(bhsje, ConfigureConstant.INT_2));
			orderItemInfo.setHsbz(OrderInfoEnum.HSBZ_0.getKey());

		} else {
		    /** 不含税价税分离处理
             * 用户传递不含税金额和税额，对用户传递数据进行计算校验，
             * 用户传递税额如果和计算后数据不一致，日志提示。
             */
            double se = 0.00;
            if (!StringUtils.isBlank(orderItemInfo.getSe())) {
            	//带扣除额的税额计算
                if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
            		se = MathUtil.mul(DecimalCalculateUtil.decimalFormat(MathUtil.sub(orderItemInfo.getXmje(), orderItemInfo.getKce()), ConfigureConstant.INT_2), orderItemInfo.getSl());
                }else{
                    se = MathUtil.mul(new BigDecimal(orderItemInfo.getXmje()),new BigDecimal(orderItemInfo.getSl()));
                }
                //如果税额误差大于 0.06抛出异常
                if (Math.abs(DecimalCalculateUtil.sub(se, Double.parseDouble(orderItemInfo.getSe()))) > ConfigureConstant.DOUBLE_PENNY_SIX) {
                    log.error("价税分离，税额校验失败，用户传递数据和计算后数据不一致，大于六分钱,抛出异常");
                    throw new OrderSeparationException(TaxSeparationErrorMessageEnum.TAXSEPARATION_SE_WC_ERROR);
                } else {
                    se = Double.parseDouble(orderItemInfo.getSe());
                }
            } else {
            	if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
            		se = MathUtil.mul(DecimalCalculateUtil.decimalFormat(MathUtil.sub(orderItemInfo.getXmje(), orderItemInfo.getKce()), ConfigureConstant.INT_2), orderItemInfo.getSl());
                }else{
                    se = DecimalCalculateUtil.mul(Double.parseDouble(orderItemInfo.getXmje()), Double.parseDouble(orderItemInfo.getSl()));
                }
            }

            //税额
            orderItemInfo.setSe(DecimalCalculateUtil.decimalFormat(se, ConfigureConstant.INT_2));
            orderItemInfo.setHsbz(OrderInfoEnum.HSBZ_0.getKey());
		}
		return orderItemInfo;
	}

	/**
	 * 补全单价和数量
	 */
	private static OrderItemInfo completeDjAndSl(OrderItemInfo orderItemInfo) {

		orderItemInfo.setSl(StringUtil.formatSl(orderItemInfo.getSl()));

	    if (StringUtils.isBlank(orderItemInfo.getXmsl()) && !StringUtils.isBlank(orderItemInfo.getXmdj())) {
			double xmsl = DecimalCalculateUtil.div(Double.parseDouble(orderItemInfo.getXmje()),
					Double.parseDouble(orderItemInfo.getXmdj()));
			orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormat(xmsl, ConfigureConstant.INT_8));
		} else if (StringUtils.isBlank(orderItemInfo.getXmdj()) && !StringUtils.isBlank(orderItemInfo.getXmsl())) {
			double xmdj = DecimalCalculateUtil.div(Double.parseDouble(orderItemInfo.getXmje()),
					Double.parseDouble(orderItemInfo.getXmsl()));
			orderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormat(xmdj, ConfigureConstant.INT_8));
		}
		return orderItemInfo;
	}
	
}
