package com.dxhy.order.constant;

/**
 * 订单信息枚举类
 *
 * @author ZSC-DXHY
 */
public enum OrderSplitEnum {
    //拆分类型
	/**
	 * 按金额超限额拆分
	 */
    ORDER_SPLIT_TYPE_1("1","按金额超限额拆分"),
    /**
	 * 按金额拆分
	 */
    ORDER_SPLIT_TYPE_2("2","按金额拆分"),
    /**
	 * 按数量拆分
	 */
    ORDER_SPLIT_TYPE_3("3","按数量拆分"),
    /**
	 * 按限制的明细行拆分
	 */
    ORDER_SPLIT_TYPE_4("4","按限制的明细行拆分"),
    /**
	 * 按明细行拆分
	 */
    ORDER_SPLIT_TYPE_5("5","按明细行拆分"),
    
    //拆分规则
    /**
	 * 拆分后保证数量不变
	 */
    ORDER_SPLIT_RULE_1("1","拆分后保证数量不变"),
    /**
	 * 拆分后保证单价不变
	 */
    ORDER_SPLIT_RULE_2("2","拆分后保证单价不变"),
    /**
	 * 拆分后保证总数量，总金额，单价不变，平衡金额误差
	 */
    ORDER_SPLIT_RULE_3("3","拆分后保证总数量，总金额，单价不变，平衡金额误差"),
    ;
    /**
     * key
     */
    private final String key;

    /**
     * 值
     */
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    OrderSplitEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static OrderSplitEnum getCodeValue(String key) {

        for (OrderSplitEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
