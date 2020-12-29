package com.dxhy.order.consumer.openapi.service;


import com.dxhy.order.protocol.order.DYNAMIC_CODE_RSP;
import com.dxhy.order.protocol.v4.order.DDZXX;
import com.dxhy.order.protocol.v4.order.EWM_RSP;

/**
 * @Description: 订单对外接口业务层接口V3-二维码相关
 * @author: chengyafu
 * @date: 2018年8月13日 下午4:48:28
 */
public interface IDynamicCodeInterfaceServiceV3 {
	
	/**
	 * 根据提取码获取订单信息
	 *
	 * @param @param  tqmReq
	 * @param @return
	 * @return ORDER_TQM_RSP
	 */
	//ORDER_TQM_RSP getOrderInfoByTqm(ORDER_TQM_REQ tqmReq);
	
	/**
	 * 获取动态码的接口
	 *
	 * @param commonOrder
	 * @return
	 */
	EWM_RSP getDynamicCode(DDZXX commonOrder);
	
	/**
	 * 根据提取码获取动态码数据
	 *
	 * @param tqm
	 * @return
	 */
	DYNAMIC_CODE_RSP getEwmUrlByTqm(String tqm);
	
}
