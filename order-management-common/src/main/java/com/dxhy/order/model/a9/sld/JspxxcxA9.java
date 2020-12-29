package com.dxhy.order.model.a9.sld;

import lombok.Getter;
import lombok.Setter;
/**
 * 金税盘查询
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:14
 */
@Getter
@Setter
public class JspxxcxA9 {
    
    /**
     * 纳税人识别号
     */
    private String nsrsbh;
    
    /**
     * 纳税人名称
     */
    private String nsrmc;
    
    /**
     * 分机号
     */
    private String fjh;
    
    /**
     * 机器编号
     */
    private String jqbh;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 启用时间
     */
    private String qysj;
    
    /**
     * 金税盘状态
     */
    private String jspzt;
    
    /**
     * 锁死日期
     */
    private String ssrq;
    
    /**
     * 上次报税日期
     */
    private String scbsrq;
    
    /**
     * 抄税起始日期
     */
    private String csqsrq;
    
    /**
     * 是否到抄税期
     */
    private String sfdbsq;
    
    /**
     * 是否到锁死期
     */
    private String sfdssq;
    
    /**
     * 单张开票限额
     */
    private String dzkpxe;
    
    /**
     * 离线时限
     */
    private String lxsx;
    
    /**
     * 离线剩余金额
     */
    private String lxsyje;
    
    /**
     * 金税盘时钟
     */
    private String jspsz;
    
    /**
     * 报税资料
     */
    private String bszl;
    
}
