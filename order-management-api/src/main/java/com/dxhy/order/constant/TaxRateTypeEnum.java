package com.dxhy.order.constant;

/**
 * 全税对接税率汇总
 * 税率枚举类
 *
 * @author ZSC-DXHY
 */
public enum TaxRateTypeEnum {
    
    /**
     * 清单标志0-普通发票;1-普通发票(清单);2-收购发票;3-收购发票(清单);4-成品油发票
     */
    TAX_TATE_1("0.16", "0.16税率"),
    TAX_TATE_2("0.17", "0.17税率"),
    TAX_TATE_3("0.13", "0.13税率"),
    TAX_TATE_4("0.09", "0.09税率"),
    TAX_TATE_5("0.10", "0.10税率"),
    TAX_TATE_6("0.11", "0.11税率"),
    TAX_TATE_7("0.06", "0.06税率"),
    TAX_TATE_8("0.05", "0.05税率"),
    TAX_TATE_9("0.04", "0.04税率"),
    TAX_TATE_10("0.03", "0.03税率"),
    TAX_TATE_11("免税", "免税"),
    TAX_TATE_12("不征税", "不征税");
    
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
    
    TaxRateTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static TaxRateTypeEnum getCodeValue(String key) {
        
        for (TaxRateTypeEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
