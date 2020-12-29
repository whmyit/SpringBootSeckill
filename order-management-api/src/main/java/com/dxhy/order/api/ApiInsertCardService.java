package com.dxhy.order.api;

import com.dxhy.order.model.InvoicePush;

/**
 * 插卡业务service
 *
 * @author ：杨士勇
 * @ClassName ：ApiInserdCardService
 * @Description ：
 * @date ：2020年4月8日 下午4:18:13
 */


public interface ApiInsertCardService {
	
	/**
	 * 添加到微信卡包
	 *
	 * @param message
	 */
	void insertCard(String message);
	
	/**
	 * 添加到mq队列中
	 *
	 * @param invoicePush
	 */
	void sendToInsertCardQueue(InvoicePush invoicePush);
	
}
