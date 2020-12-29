package com.dxhy.order.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 税编枚举
 * @author xueanna
 */
public enum TaxClassCodeEnum {
    /**
     * 免税类型（税编表）
     */
    MSLX_0("0", "正常税率"),
    MSLX_1("1", "出口免税率或其他免税优惠政策"),
    MSLX_2("2", "不征增值税"),
    MSLX_3("3", "普通零税率"),
    /**
     * 数据来源
     */
    DATA_SOURCE_0("0", "集团共享"),
    DATA_SOURCE_1("1", "手工创建"),
    DATA_SOURCE_2("2", "模板导入"),
    DATA_SOURCE_3("3", "采集下级"),
    /**
     * 匹配状态
     */
    MATCHING_STATE_0("0", "已匹配"),
    MATCHING_STATE_1("1", "未匹配"),
    /**
     * 数据状态
     */
    DATA_STATE_0("0", "启用"),
    DATA_STATE_1("1", "停用"),
    DATA_STATE_2("2", "删除"),
    /**
     * 共享状态
     */
    SHARE_STATE_0("0", "允许共享"),
    SHARE_STATE_1("1", "待核实"),

    /**
    * 差异标识
    */
    DIFFERENCE_FLAG_0("0","差异标识，该类型的数据为后插入的差异数据"),
    DIFFERENCE_FLAG_1("1","原库里的数据差异标识"),
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

    TaxClassCodeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static TaxClassCodeEnum getCodeValue(String key) {
        
        for (TaxClassCodeEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
    
    public static List<String> getValues() {
        
        List<String> resultList = new ArrayList<>();
        for (TaxClassCodeEnum item : values()) {
            resultList.add(item.getValue());
        }
        return resultList;
    }
    
    
}
