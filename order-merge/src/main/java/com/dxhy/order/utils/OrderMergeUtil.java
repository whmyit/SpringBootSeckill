package com.dxhy.order.utils;

import com.dxhy.order.constant.*;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName ：OrderMergeServiceImpl1
 * @Description ：订单明细合并  订单合并重构 去除其他依赖 单独的合并
 * @author ：杨士勇
 * @date ：2019年9月20日 下午2:04:11
 *
 *
 */
@Slf4j
public class OrderMergeUtil{

	private final static String LOGGER_MSG = "(订单合并业务实现)";

	/**
	 * 订单合并接口 返回合并后的订单信息
	 *
	 * @param commonOrderInfos
	 * @return
	 * @throws OrderMergeException
	 */
	public static CommonOrderInfo orderMerge(List<CommonOrderInfo> commonOrderInfos, OrderMergeConfig config) throws OrderMergeException{
        log.debug("订单合并接口，入参,订单信息:{},合并配置信息:{}",JsonUtils.getInstance().toJsonString(commonOrderInfos),
        		JsonUtils.getInstance().toJsonString(config));
	    checkParam(commonOrderInfos);
		// 合并订单信息
		CommonOrderInfo commonOrderInfo = mergeOrderInfo(commonOrderInfos,config);
		//格式化单价金额信息
		commonOrderInfo = formatCommonOrder(commonOrderInfo);
		log.info("{} 合并订单信息结束：合并的订单信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));

		return commonOrderInfo;
	}
	
	/**
     * @param @param commonOrderInfos
     * @return void
     * @throws OrderMergeException
     * @throws
     * @Title : checkParam
     * @Description ：订单合并数据校验
     */
	private static void checkParam(List<CommonOrderInfo> commonOrderInfos) throws OrderMergeException {
		
		//合并的订单信息为空
		if(CollectionUtils.isEmpty(commonOrderInfos)){
			log.error("订单信息为空！");
			throw new OrderMergeException(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERINFO_NULL_ERROR);
		}
		
	    int i = 0;
	    String hsbz = "0";
		for(CommonOrderInfo comm : commonOrderInfos){
			
			if(comm == null){
				log.error("订单信息为为空!");
				throw new OrderMergeException(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERINFO_NULL_ERROR);
			}
			//订单主题信息校验
			if(comm.getOrderInfo() == null){
				log.error("订单主体信息为空！");
				throw new OrderMergeException(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERINFO_NULL_ERROR);
			}
			//订单明细信息校验
			if(CollectionUtils.isEmpty(comm.getOrderItemInfo())){
				log.error("订单明细信息为空");
				throw new OrderMergeException(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERITEMINFO_NULL_ERROR);
			}
			int j = 0;
			for(OrderItemInfo orderItem : comm.getOrderItemInfo()){
				if(i == 0 && j == 0){
					hsbz = orderItem.getHsbz();
				}else if(!hsbz.equals(orderItem.getHsbz())){
					log.error("合并的订单只能为全部含税或全部不含税!");
					throw new OrderMergeException(OrderMergeErrorMessageEnum.ORDER_MERGER_ORDERITEMINFO_HSBZ_ERROR);
				}
				j++;
			}
            
            i++;
        }
    }
    
    /**
     * @param config
     *
     * @Title : mergeOrderInfo
     * @Description ：合并订单信息
     * @param @param commonOrderInfos
     * @param @return
     * @return CommonOrderInfo
     * @exception
	 *
	 */
	private static CommonOrderInfo mergeOrderInfo(List<CommonOrderInfo> commonOrderInfos, OrderMergeConfig config) {
		
		CommonOrderInfo resultCommonOrderInfo;
		// 业务类型
		List<String> ywlxList = new ArrayList<>();
		// 复合对象 重新赋值的时候 只能强制new对象分配内存地址否则赋值容易混乱 add by ysy
		OrderInfo orderInfo1 = new OrderInfo();
		BeanUtils.copyProperties(commonOrderInfos.get(0).getOrderInfo(), orderInfo1);
		
		StringBuilder sb = new StringBuilder();
		List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder parentOrderIdStringBuilder = new StringBuilder();
		StringBuilder parentOrderProcessIdStringBuilder = new StringBuilder();
		String gfmcString = "";
		/**
		 * 订单主体信息计算金额
		 *
		 */
		for (CommonOrderInfo commonOrderInfo : commonOrderInfos) {

			OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
			//处理业务类型
			if (!StringUtils.isBlank(orderInfo.getYwlx())) {
				ywlxList.add(orderInfo.getYwlx());
			}
			//处理备注信息
			if (StringUtils.isNotBlank(orderInfo.getBz())) {
				sb.append(orderInfo.getBz()).append(";");
			}
		    //添加订单信息
			orderItemInfoList.addAll(commonOrderInfo.getOrderItemInfo());
			stringBuilder.append(StringUtils.isBlank(orderInfo.getDdh()) ? "" : orderInfo.getDdh());
			
			//购方信息补全,根据购方名称判断,如果购方名称为空则不适用本条记录,如果不为空,并且购方历史为空,则使用该条.
			if (StringUtils.isBlank(gfmcString)) {
				orderInfo1.setGhfMc(orderInfo.getGhfMc());
				orderInfo1.setGhfQylx(orderInfo.getGhfQylx());
				orderInfo1.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
				orderInfo1.setGhfDh(orderInfo.getGhfDh());
				orderInfo1.setGhfDz(orderInfo.getGhfDz());
				orderInfo1.setGhfYh(orderInfo.getGhfYh());
				orderInfo1.setGhfZh(orderInfo.getGhfZh());
			}

			if (parentOrderIdStringBuilder.length() > 0) {
				parentOrderIdStringBuilder.append(":").append(orderInfo.getId());
			} else {
				parentOrderIdStringBuilder.append(orderInfo.getId());
			}
			
			if (parentOrderProcessIdStringBuilder.length() > 0) {
                parentOrderProcessIdStringBuilder.append(":").append(orderInfo.getProcessId());
            } else {
                parentOrderProcessIdStringBuilder.append(orderInfo.getProcessId());
            }
        }

        orderInfo1.setYwlx(mergeYwlxAndMdh(ywlxList));
        String bz = sb.toString();
        orderInfo1.setBz(StringUtil.getBz(bz));
        String xddh = ConfigureConstant.STRING_HB + stringBuilder.toString();
        if(StringUtils.isNotBlank(xddh) && xddh.length() > 50){
			xddh = xddh.substring(0,50);
			orderInfo1.setDdh(xddh);
		}else{
			orderInfo1.setDdh(xddh);
		}

        //orderInfo1.setDdh(StringUtil.getMergeDdh(ConfigureConstant.STRING_HB));
        /**
         * 把相同的明细数组进行同类明细项合并.
         */
        if (ConfigureConstant.STRING_0.equals(config.getIsMergeSameItem())) {
            orderItemInfoList = mergeOrderItemInfo(orderItemInfoList);
        }
        //重新计算订单的金额
        resultCommonOrderInfo = rebuildCommonOrderInfo(orderInfo1, orderItemInfoList);
        resultCommonOrderInfo.setOriginOrderId(parentOrderIdStringBuilder.toString());
        resultCommonOrderInfo.setOriginProcessId(parentOrderProcessIdStringBuilder.toString());
		return resultCommonOrderInfo;
	}

	/**
	 * 合并门店号或者业务类型,如果业务类型不同,设置业务类型或订单号为空
	 *
	 * @return String
	 * @author: 陈玉航
	 * @date: Created on 2018年8月29日 下午3:43:33
	 */
	private static String mergeYwlxAndMdh(List<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		} else {
			String flag = list.get(0);
			for (String str : list) {
				if (!flag.equals(str)) {
					flag = "";
					break;
				}
			}
			return flag;
		}
	}

	/**
	 * 把明细行列表数组中的重复项进行合并.
	 *
	 * @return List<OrderItemInfo>
	 * @author: 陈玉航
	 * @date: Created on 2018年8月6日 上午11:25:52
	 */
	private static List<OrderItemInfo> mergeOrderItemInfo(List<OrderItemInfo> orderItemInfos) {
		log.info("{} 合并明细项开始", LOGGER_MSG);
		Map<String, List<OrderItemInfo>> map = new HashMap<>(10);

		/**
		 * 循环数组中的数据,把同类明细项放在相同的数组中,作为原数据存在.
		 */
		for (OrderItemInfo orderItemInfo : orderItemInfos) {
			
			resetDjSL(orderItemInfo);
			/**
			 * 如果是折扣行使用折扣行的key进行区分 如果是其他使用关键字进行区分
			 *
			 * 判断map中是否存在关键字,如果存在取值然后追加数据,如果不存在新建数组,存放当前数据.
			 */
			StringBuilder key = new StringBuilder();
			if (OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())) {
				key.append(ConfigureConstant.STRING_ZKH).append(orderItemInfo.getXmmc());
			} else {
				key.append(orderItemInfo.getXmmc()).append(orderItemInfo.getGgxh()).append(orderItemInfo.getSl())
						.append(orderItemInfo.getXmdj());
			}

			if (map.containsKey(key.toString())) {
				map.get(key.toString()).add(orderItemInfo);
			} else {
				List<OrderItemInfo> list = new ArrayList<>();
				list.add(orderItemInfo);
				map.put(key.toString(), list);
			}
		}

		/**
		 * 使用大列表数据进行循环.从原数据中取值,
		 */
		List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
		for (OrderItemInfo orderItemInfo : orderItemInfos) {
			OrderItemInfo newOrderItemInfo = new OrderItemInfo();
			/**
			 * 如果是折扣行不进行处理,只处理非折扣行, 如果是非折扣行,获取对应原数据中的数据, 如果是被折扣行,获取对应折扣行的数据.
			 */
			if (OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())) {
				continue;
			}
			/**
			 * 判断原数据是否已经为空,如果不为空执行以下操作.
			 */
			if (!map.isEmpty()) {
				/**
				 * 处理同名称的折扣行和被折扣行数据.
				 */
				StringBuilder key = new StringBuilder();
				StringBuilder zkhKey = new StringBuilder();
				key.append(orderItemInfo.getXmmc()).append(orderItemInfo.getGgxh()).append(orderItemInfo.getSl())
						.append(orderItemInfo.getXmdj());
				if (map.containsKey(key.toString())) {
					List<OrderItemInfo> list = map.get(key.toString());
					newOrderItemInfo = completMXH(list, 0);
					orderItemInfoList.add(newOrderItemInfo);
					map.remove(key.toString());
				}
				/**
				 * 处理折扣行数据
				 */
				zkhKey.append(ConfigureConstant.STRING_ZKH).append(orderItemInfo.getXmmc());
				if (map.containsKey(zkhKey.toString())) {
					List<OrderItemInfo> zklist = map.get(zkhKey.toString());
					newOrderItemInfo = completMXH(zklist, 1);
					orderItemInfoList.add(newOrderItemInfo);
					map.remove(zkhKey.toString());
				}
			}

		}
		log.info("{} 合并明细项结束：{}", LOGGER_MSG, orderItemInfoList);
		return orderItemInfoList;
	}

	/**
	 * 把同类明细相同的合并为同一个明细数据. i主要用于区分,是处理折扣行还是被折扣行数据,
	 *
	 * @param orderItemInfos
	 * @param i
	 * @return
	 */
	private static OrderItemInfo completMXH(List<OrderItemInfo> orderItemInfos, int i) {
		OrderItemInfo orderItemInfo2 = orderItemInfos.get(0);
		

		/**
		 * 进入这里的数据都是,明细行相同的数据,默认使用第一条数据作为主要数据, 金额累加,数量累加.
		 */
		//判断是否含税
        if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfos.get(0).getHsbz())) {
			//含税
			BigDecimal xmsl = BigDecimal.ZERO;
			BigDecimal xmje = BigDecimal.ZERO;
			String fphxz = orderItemInfo2.getFphxz();
			for (OrderItemInfo orderItemInfo : orderItemInfos) {
				if (i == 0) {
					if (StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
						xmsl = xmsl.add(new BigDecimal(orderItemInfo.getXmsl()));
					}
				}
				if (orderItemInfo.getFphxz().equals(OrderInfoEnum.FPHXZ_CODE_2.getKey())) {
					fphxz = orderItemInfo.getFphxz();
				}
				xmje = xmje.add(new BigDecimal(orderItemInfo.getXmje()));
			}
			if (i == 0) {
				if (!xmsl.equals(BigDecimal.ZERO)) {
					orderItemInfo2.setXmsl(xmsl.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toString());
				}
			}
			orderItemInfo2.setFphxz(fphxz);
			orderItemInfo2.setXmje(xmje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toString());
			/**
			 * 判断单价是否和计算后一致,如果不一致需要重新计算.
			 */
			if (!xmsl.equals(BigDecimal.ZERO)) {
				orderItemInfo2.setXmdj(xmje.divide(xmsl, ConfigureConstant.INT_8, RoundingMode.HALF_UP).toString());
			}
			
		}else{
			//不含税
			BigDecimal xmsl = BigDecimal.ZERO;
			BigDecimal xmje = BigDecimal.ZERO;
			BigDecimal xmse = BigDecimal.ZERO;
			String fphxz = orderItemInfo2.getFphxz();
			for (OrderItemInfo orderItemInfo : orderItemInfos) {
				if (i == 0) {
					if (StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
						xmsl = xmsl.add(new BigDecimal(orderItemInfo.getXmsl()));
					}
				}
				
				if(StringUtils.isNotBlank(orderItemInfo.getSe())){
					xmse = xmse.add(new BigDecimal(orderItemInfo.getSe()));
				}
				
				if (orderItemInfo.getFphxz().equals(OrderInfoEnum.FPHXZ_CODE_2.getKey())) {
					fphxz = orderItemInfo.getFphxz();
				}
				xmje = xmje.add(new BigDecimal(orderItemInfo.getXmje()));
			}
			if (i == 0) {
				if (!xmsl.equals(BigDecimal.ZERO)) {
					orderItemInfo2.setXmsl(xmsl.setScale(ConfigureConstant.INT_8, RoundingMode.HALF_UP).toString());
				}
			}
	        orderItemInfo2.setFphxz(fphxz);
	        orderItemInfo2.setXmje(xmje.setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toString());
	        /**
	         * 判断单价是否和计算后一致,如果不一致需要重新计算.
	         */
	        if (!xmsl.equals(BigDecimal.ZERO)) {
		        orderItemInfo2.setXmdj(xmje.divide(xmsl, ConfigureConstant.INT_8, RoundingMode.HALF_UP).toString());
	        }
	
	        if (xmse.doubleValue() != ConfigureConstant.DOUBLE_PENNY_ZERO) {
		        orderItemInfo2.setSe(xmse.toString());
	        }
	
        }
		
		return orderItemInfo2;
    }
    
    
    /**
     *
     * @Title : rebuildCommonOrderInfo
     * @Description ：重新计算合计金额和合计税额 价税合计
     * @param @param orderInfo
     * @param @param subList
     * @param @return
     * @return CommonOrderInfo
     * @exception
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
     * @Title : resetDjSL
     * @Description ：重算单价和数量
     * @param @param orderItemInfo
     * @param @return
     * @return OrderItemInfo
     * @exception
	 *
	 */
	private static OrderItemInfo resetDjSL(OrderItemInfo orderItemInfo) {
		Double xmdj = 0.00;
		Double xmsl = 0.00;

		if (StringUtils.isBlank(orderItemInfo.getXmdj()) && StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
			// 如果单价为空 数量不为空 重新计算单价
			xmdj = new BigDecimal(orderItemInfo.getXmje())
					.divide(new BigDecimal(orderItemInfo.getXmsl()), 8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmdj), ConfigureConstant.INT_8));
		} else if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) && StringUtils.isBlank(orderItemInfo.getXmsl())) {
			// 如果数量为空 单价不为空 重新计算数量
			xmsl = new BigDecimal(orderItemInfo.getXmje())
					.divide(new BigDecimal(orderItemInfo.getXmje()), 8, RoundingMode.HALF_UP).doubleValue();
			orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(String.valueOf(xmsl), ConfigureConstant.INT_8));
		}
		return orderItemInfo;
    
    }
    
    /**
     *
     * @Title : formatCommonOrder
     * @Description ：格式化单价金额的信息
     * @param @param commonOrderInfo
     * @param @return
     * @return CommonOrderInfo
     * @exception
	 *
	 */
	 private static CommonOrderInfo formatCommonOrder(CommonOrderInfo commonOrderInfo) {
	    	
	    	for(OrderItemInfo itemInfo : commonOrderInfo.getOrderItemInfo()){
	    		if(StringUtils.isNotBlank(itemInfo.getXmje())) {
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

	public static void main(String[] args) {
		String str = "abddre";
		System.out.println(str.substring(0,1));
	}

}
