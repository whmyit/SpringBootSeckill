package com.dxhy.order.constant;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @ClassName ：OrderMergeConfig
 * @Description ：订单合并配置
 * @author ：杨士勇
 * @date ：2019年9月20日 下午1:59:59
 *
 *
 */

@Setter
@Getter
public class OrderMergeConfig {
	
	
	/**
	 * 是否合并同类明细项 0 合并 1 不合并
	 */
	private String isMergeSameItem;
	
	/**
	 * 构造函数
	 */
	public OrderMergeConfig() {
		super();
		this.isMergeSameItem = "1";
	}

}
