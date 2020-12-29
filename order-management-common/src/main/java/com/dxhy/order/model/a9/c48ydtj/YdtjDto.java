package com.dxhy.order.model.a9.c48ydtj;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: wangyang
 * @date: 2020/7/3 19:14
 * @description
 */
@Getter
@Setter
public class YdtjDto implements Serializable {
    
    private Integer id;
    private String fjh;
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
    private String nsrmc;
    
    /**
     * 开票量
     */
    private String kpl;
    
    /**
     * 库存余数
     */
    private String kcys;
    
    /**
     * 合计金额
     */
    private String hjje;
    
    /**
     * 不含税金额
     */
    private String bhsje;
    
    /**
     * 合计税额
     */
    private String hjse;
}
