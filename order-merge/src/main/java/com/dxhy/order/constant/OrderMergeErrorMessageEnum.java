package com.dxhy.order.constant;

/**
 * @author ：杨士勇
 * @ClassName ：OrderMergeErrorMessageEnum
 * @Description ：订单合并错误信息枚举
 * @date ：2019年9月20日 下午2:22:14
 */

public enum OrderMergeErrorMessageEnum {
    
    /**
     * 订单合并异常枚举值
     */
    ORDER_MERGER_ORDERINFO_NULL_ERROR("1000", "订单合并，订单信息为空!"),
    ORDER_MERGER_ORDERITEMINFO_NULL_ERROR("1001", "订单合并，订单明细信息为空!"),
    ORDER_MERGER_ORDERITEMINFO_HSBZ_ERROR("1002", "合并的订单只能为全部含税或全部不含税!"),
    ORDER_MERGER_XHFNSRSBH_COMPLEX_ERROR("1003", "订单合并,销售方信息不一致"),
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

    OrderMergeErrorMessageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static OrderMergeErrorMessageEnum getCodeValue(String key) {

        for (OrderMergeErrorMessageEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
