package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author fankunfeng
 * @Date 2019-04-13 18:44:16
 * @Describe
 */
@Setter
@Getter
@ToString
public class CountHjjeVO {
    /**
     * 时间：年月
     */
    private String time;
    /**
     * 蓝字合计金额
     */
    private String lzje;
    /**
     * 红字合计金额
     */
    private String hzje;
    /**
     * 合计总量
     */
    private String hjje;
    
    /**
     * 金额月环比
     */
    private String yhb;
}
