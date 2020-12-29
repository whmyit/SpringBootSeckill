package com.dxhy.order.constant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 *
 * @ClassName ：TaxSeparateConfig
 * @Description ：拆分配置
 * @author ：杨士勇
 * @date ：2019年9月2日 下午8:02:26
 *
 *
 */
@Setter
@Getter
public class OrderSplitConfig {
	
	/**
	 * 订单拆分配置  1：拆分金额 超限额拆分  2：拆分金额 普通拆分 3：拆分数量 4：拆分明细行 超明细行拆分 5：拆分明细行 行数拆分
	 */
	private String splitType;
	/**
	 * 拆分规则 0：保证金额是拆分的金额 1: 保证数量整数
	 */
	private String splitRule;
	/**
	 * 误差修正方式
	 * 1：拆分的金额和税额遵循绝对的四舍五入 最后的误差平衡到单价中
	 * 2：拆分的金额和税额遵循绝对的四舍五入 最后的误差平衡到数量中
	 * 3：拆分的金额和税额不遵循绝对的四舍五入
	 */
	private String errorCorrectionType;
	/**
	 * 超限额拆分方式时必传 限额
	 */
	private Double limitJe;
	/**
	 * 明细行数超限后拆分
	 */
	private Integer limitRang;
	/**
	 * 要拆分的金额数组
	 */
	private List<Double> jeList;
	/**
	 * 要拆分的数量数组
	 */
	private List<Double> slList;
	/**
	 * 要拆分的明细行
	 */
	private List<Integer> lineList;
}
