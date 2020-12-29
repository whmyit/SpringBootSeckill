package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author fankunfeng
 * @Date 2019-04-13 14:29:56
 * @Describe
 */
@Setter
@Getter
@ToString
public class CountBySldVO {
    /**
     * 受理点
     */
    private String sld;
    /**
     * 分机号
     */
    private String fjh;
    /**
     * 受理点名称
     */
    private String sldMc;
    /**
     * 税盘名称
     */
    private String spmc;
    /**
     * 开票量
     */
    private String count;
    /**
     * 价税合计
     */
    private String jshj;
    /**
     * 合计税额
     */
    private String hjse;
    
    /**
     * 合计不含税金额
     */
    private String hjbhje;
    
    /**
     * 销方名称
     */
    private String nsrmc;
    
    /**
     * 销方税号
     */
    private String nsrsbh;
}
