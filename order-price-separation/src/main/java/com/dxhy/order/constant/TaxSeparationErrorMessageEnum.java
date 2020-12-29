package com.dxhy.order.constant;

/**
 * @author ：杨士勇
 * @ClassName ：TaxSeparationErrorMessageEnum
 * @Description ：价税分离错误信息枚举类
 * @date ：2019年9月3日 上午10:00:58
 */

public enum TaxSeparationErrorMessageEnum {
    
    /**
     * 价税分离异常类
     */
    TAXSEPARATION_SE_WC_TOTAL("1000", "税额累计误差大于1.27"),
    
    TAXSEPARATION_KCE_FORMAT_ERROR("1001", "扣除额格式错误"),
    
    TAXSEPARATION_SE_WC_ERROR("1002", "单条明细税额误差大于0.06"),
    
    TAXSEPARATION_BHSJE_NULL_ERROR("1003", "不含税订单，不含税金额不能为空"),
    
    TAXSEPARATION_HSJE_NULL_ERROR("1004", "含税订单，含税金额不能为空"),
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

    TaxSeparationErrorMessageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static TaxSeparationErrorMessageEnum getCodeValue(String key) {

        for (TaxSeparationErrorMessageEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
