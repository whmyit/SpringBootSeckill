package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：DYNAMIC_COMMON_ORDER
 * @Description ：
 * @date ：2020年2月21日 下午3:10:57
 */

@Setter
@Getter
public class DYNAMIC_COMMON_ORDER implements Serializable {

	private DYNAMIC_ORDER_INFO COMMON_ORDER_HEAD;
	private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
	
	
}
