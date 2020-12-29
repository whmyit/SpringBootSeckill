package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单提取码查询响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:21
 */
@Getter
@Setter
public class ORDER_TQM_RSP extends RESPONSE {
	
	private List<COMMON_ORDER_INFO> COMMON_ORDER_INVOICES;
}
