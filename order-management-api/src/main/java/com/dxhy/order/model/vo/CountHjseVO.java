package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author fankunfeng
 * @Date 2019-04-13 18:47:31
 * @Describe
 */
@Setter
@Getter
@ToString
public class CountHjseVO {
    /**
     * 时间：年月
     */
    private String time;
    /**
     * 蓝字合计金额
     */
    private String lzse;
    /**
     * 红字合计金额
     */
    private String hzse;
    /**
     * 合计总量
     */
    private String hjse;
    
    /**
     * 月环比
     */
    private String yhb;
}
