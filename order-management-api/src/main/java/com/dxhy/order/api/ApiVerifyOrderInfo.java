package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.protocol.v4.order.DDZXX;

import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ApiVerifyOrderInfo
 * @Description : 订单信息校验
 * @date ：2020年2月26日 上午10:51:41
 */

public interface ApiVerifyOrderInfo {
	
	/**
	 * 动态码开票校验
	 *
	 * @param orderInfo
	 * @return
	 */
	Map<String, String> verifyDynamicOrderInfo(DDZXX orderInfo);
	
	/**
	 * 动态码开票校验
	 *
	 * @param commonOrder
	 * @return
	 */
	Map<String, String> verifyDynamicEwmInfo(CommonOrderInfo commonOrder);
	
}
