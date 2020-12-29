package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票作废列表
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年12月20日 下午5:26:33
 */

@Getter
@Setter
public class InvalidInvoiceEntity implements Serializable {
    
    /**
     * 订单流程id
     */
    private String orderProcessId;
    
    /**
     * 订单id
     */
    private String orderInfoId;
    
    /**
     * 发票id
     */
    private String orderInvoiceId;
    
    /**
     * 开票流水号
     */
    private String kplsh;
    
    /**
     * 订单号
     */
    private String ddh;
    
    /**
     * 申请单号
     */
    private String sqdh;
    
    /**
     * 发票代码
     */
    private String fpdm;
    
    /**
     * 发票号码
     */
    private String fphm;
    
    /**
     * 购方名称
     */
    private String gfmc;
    
    /**
     * 销方名称
     */
    private String xfmc;
    
    /**
     * 开票时间
     */
    private String kpsj;
    
    /**
     * 合计金额
     */
    private String hjje;
    
    /**
     * 开票类型
     */
    private String kplx;
    
    /**
     * 开票人
     */
    private String kpr;
    
    /**
     * 作废标志(0:已作废;1:未作废)
     */
    private String zfbz;
    
    /**
     * kprq
     */
    private String kprq;
    
    /**
     * 作废类型(0:空白发票作废;1:作废表作废;2:发票数据作废)
     */
    private String zflx;
    
    /**
     * fjh
     */
    private String fjh;
    
    /**
     * xfsh
     */
    private String xfsh;
    
}
