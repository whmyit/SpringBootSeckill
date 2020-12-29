package com.dxhy.order.constant;

import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @ClassName ：TaxSeparateConfig
 * @Description ：用户价税分离配置
 * @author ：杨士勇
 * @date ：2019年9月2日 下午8:02:26
 *
 *
 */
@Setter
@Getter
public class TaxSeparateConfig {
	
	/**
	 * 价税分离税额计算方式 1.税额遵循绝对的四舍五入的方式 2.税额根据开票的误差金额调整
	 */
	private String dealSeType;
	/**
	 * 单税率价税分离的方式 1.价税分离后的税额进行再次调整 2.税额不进行调整
	 */
	private String singleSlSeparateType;
	
	public TaxSeparateConfig() {
		super();
		this.dealSeType = "1";
		this.singleSlSeparateType = "2";
	}
	
}
