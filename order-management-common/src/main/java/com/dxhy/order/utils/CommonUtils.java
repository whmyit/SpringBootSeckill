package com.dxhy.order.utils;

import com.dxhy.order.constant.OrderInfoEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 订单号拆分处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-13 20:36
 */
@Slf4j
public class CommonUtils {
	private static final String LOGGER_MSG = "(通用工具类转换)";
	
	/**
	 * 拆分后订单号数据截取工具类
	 */
	public static String dealDdh(String yddh) {
		if (yddh.length() > 20) {
			int index = yddh.indexOf("cf");
			if (index < 0) {
				return yddh;
			}
			String prefixString = yddh.substring(0, index);
			String suffixString = yddh.substring(index);
			if (suffixString.length() >= 20) {
				return suffixString.substring(suffixString.length() - 20);
				
			} else {
				int diff = 20 - suffixString.length();
				prefixString = prefixString.substring(0, diff);
				return prefixString + suffixString;
			}
        
        } else {
			return yddh;
		}
    }
    
	/**
	 * 根据清单标志 和明细行数重置清单标志
	 */
	public static String getQdbz(String qdbz,int itemLegth){
		
		//普通发票
		if(OrderInfoEnum.QDBZ_CODE_0.getKey().equals(qdbz) || OrderInfoEnum.QDBZ_CODE_1.getKey().equals(qdbz)){
			if(itemLegth > 8){
				return OrderInfoEnum.QDBZ_CODE_1.getKey();
			}else{
				return OrderInfoEnum.QDBZ_CODE_0.getKey();
			}
			
		}
		//收购发票
		if(OrderInfoEnum.QDBZ_CODE_2.getKey().equals(qdbz) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(qdbz)){
			if(itemLegth > 8){
				return OrderInfoEnum.QDBZ_CODE_3.getKey();
			} else {
				return OrderInfoEnum.QDBZ_CODE_2.getKey();
			}
		}
		
		//成品油发票
		if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(qdbz)) {
			return OrderInfoEnum.QDBZ_CODE_4.getKey();
		}
		
		return qdbz;
	}
	
	/**
	 * @param fplxdm
	 * @return
	 */
	public static String transFpzldm(String fplxdm) {
		String fpzldm = "";
		if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fplxdm) || OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(fplxdm)) {
			fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey();
		} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fplxdm) || OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(fplxdm)) {
			fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey();
		} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fplxdm) || OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(fplxdm)) {
			fpzldm = OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey();
		}
		log.debug("{}新税控发票种类转换为旧版本发票种类,请求参数为:{},输出参数为:{}", LOGGER_MSG, fplxdm, fpzldm);
		return fpzldm;
	}
	
	/**
	 * 旧版本发票种类转换为新税控发票类型代码
	 *
	 * @param fpzldm
	 * @return
	 */
	public static String transFplxdm(String fpzldm) {
		String fplxdm = "";
		if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(fpzldm)) {
			fplxdm = OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey();
		} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(fpzldm)) {
			fplxdm = OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey();
		} else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(fpzldm)) {
			fplxdm = OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey();
		}
		log.debug("{}旧版本发票种类转换为新税控发票种类,请求参数为:{},输出参数为:{}", LOGGER_MSG, fpzldm, fplxdm);
		return fplxdm;
	}
	
}
