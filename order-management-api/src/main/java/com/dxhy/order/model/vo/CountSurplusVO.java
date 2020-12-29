package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author fankunfeng
 * @Date 2019-04-13 16:04:45
 * @Describe
 */
@Setter
@Getter
@ToString
public class CountSurplusVO {
    /**
     * 纳税人识别号名称，销货方名称
     */
    private String nsrmc;
    /**
     * 分机号
     */
    private String fjh;
    /**
     * 普票余量
     */
    private String ppyl;
    /**
     * 电票余量
     */
    private String dpyl;
    /**
     * 专票余量
     */
    private String zpyl;
}
