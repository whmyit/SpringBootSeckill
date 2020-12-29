package com.dxhy.order.consumer.modules.supplychain.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 供应链枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:42
 */
public enum SupplyChainCommonEnum {

    /**
     * 订单审核状态
     */
    CHECK_STATUS_0("0", "初始化状态"),
    CHECK_STATUS_1("1", "待审核状态"),
    CHECK_STATUS_2("2", "审核通过"),
    CHECK_STATUS_3("3", "审核驳回"),



    REQUEST_CHECK_STATUS_0("0","同意"),
    REQUEST_CHECK_STATUS_1("1","不同意"),

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

    SupplyChainCommonEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static SupplyChainCommonEnum getCodeValue(String key) {

        for (SupplyChainCommonEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    public static List<String> getValues() {

        List<String> resultList = new ArrayList<>();
        for (SupplyChainCommonEnum item : values()) {
            resultList.add(item.getValue());
        }
        return resultList;
    }

}
