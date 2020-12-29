package com.dxhy.order.api;

import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ApiShorMessageSend
 * @Description ：
 * @date ：2020年4月15日 下午5:45:38
 */

public interface ApiShorMessageSend {
	
	/**
	 * 短信发送
	 *
	 * @param invoiceIdArray
	 * @param phone
	 * @return
	 */
	R sendShortMessage(List<Map> invoiceIdArray, String phone);
	
}
