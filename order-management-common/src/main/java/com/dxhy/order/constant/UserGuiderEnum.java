package com.dxhy.order.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户引导枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:46
 */
public enum UserGuiderEnum {


    /**
     * 用户引导操作步骤
     */
    APPLY_INVOICE("APPLY_INVOICE", "发票申领","1"),
    GET_INVOICE("GET_INVOICE", "发票领用","1"),
    SET_UP_TERMINAL("SET_UP_TERMINAL", "开票终端设置","1"),
    DISTRIBUTE_INVOICE("DISTRIBUTE_INVOICE", "发票分发","2"),
    SET_UP_BUSINESS_CODE("SET_UP_BUSINESS_CODE", "商品编码","2"),
    SET_UP_INVOICE_PARAM("SET_UP_INVOICE_PARAM", "开票参数设置","2"),
    PAGE_INVOICE("PAGE_INVOICE", "发票填开","3"),
    QUERY_INVOICE("QUERY_INVOICE", "发票查询","4"),
    TAX_RETURNS("TAX_RETURNS", "抄税报税","4"),
    INVOICE_COLLECT("INVOICE_COLLECT", "发票汇总","4"),

    ;
    /**
     * key
     */
    private final String key;

    /**
     * 值
     */
    private final String value;
    /**
     * 步骤
     */
    private final String step;


    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getStep() {
        return step;
    }



    UserGuiderEnum(String key, String value,String step) {
        this.key = key;
        this.value = value;
        this.step = step;
    }

    public static UserGuiderEnum getCodeValue(String key) {

        for (UserGuiderEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    public static List<String> getValues() {

        List<String> resultList = new ArrayList<>();
        for (UserGuiderEnum item : values()) {
            resultList.add(item.getValue());
        }
        return resultList;
    }



}
