package com.dxhy.order.model.card;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：InvoiceInsertCardCommon
 * @Description ：
 * @date ：2020年4月8日 下午5:02:17
 */
@Getter
@Setter
public class InsertCardRequest {
	
	private Fpkj fpxx_base;
	
	private List<Fpkjxx> fpkjxx_xmxxs;
	
}
