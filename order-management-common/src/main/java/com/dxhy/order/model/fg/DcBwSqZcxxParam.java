package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 调用第三方申请注册码协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-07-03
 */
@Getter
@Setter
public class DcBwSqZcxxParam implements Serializable {
    
    /**
     * 用户名
     */
    private String uid;
    
    /**
     * 秘钥
     */
    private String secret;
    
    /**
     * 纳税人名称 必填
     */
    private String nsrmc;
    
    /**
     * 纳税人识别号 当rjlx为1、2时必填
     */
    private String nsrsbh;
    
    /**
     * 盘号 当rjlx为1、2时必填
     */
    private String skph;
    
    /**
     * mac地址 当rjlx为3时必填
     */
    private String mac;
    
    /**
     * 税控盘数量 当rjlx为3时必填
     */
    private String skpsl;
    
    /**
     * 申请性质 必填
     * 0测试（有效期为一个月）
     * 1正式（有效期为一年）
     */
    private String je;
    
    /**
     * 软件类型 必填
     * 1 win （组件接口）
     * 2 pos
     * 3盘组（盘组系统）
     */
    private String rjlx;
    
    /**
     * 软件功能 必填
     * 固定值：101
     */
    private String rjgn;
    
    /**
     * 发票类型
     * 必填：按实际类型传送，多个票种用逗号隔开。
     * “004”增值税专用发票
     * “007”增值税普通发票
     * “025”卷式发票
     * “026”电子发票
     * “005”机动车发票
     * “006”二手车销售统一发票
     */
    private String fplx;
    
    /**
     * 变更申请
     * 0 无变更
     * 1 识别号变更
     * 2 盘号变更
     */
    private String bgsq;
    
    /**
     * 丢失申请
     * 0 无
     * 1注册码丢失重传旧码
     */
    private String zcmds;
    
    /**
     * 续费申请
     * 0 无
     * 1 续费再申请
     */
    private String xfsq;
    
    /**
     * 输入注册码起始时间
     * 可空默认为申请日期
     */
    private String inputqssj;
    
    /**
     * 原盘号
     * 当rjlx为1、2且bgsq为2时，必须输入原盘号；
     */
    private String oldskph;
    
    /**
     * 原mac地址
     * 当rjlx为3时且bgsq为2时，必须输入原mac；
     */
    private String oldmac;
    
    /**
     * 备注
     */
    private String bz;
    
    
}
