package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 申请单发票实体类
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年12月17日 上午10:48:20
 */
@Getter
@Setter
public class InvoiceSQDInfoEntity implements Serializable {
    
    /**
     * 申请单id
     */
    private String orderBatchId;
    
    /**
     * 申请单号
     */
    private String sqdh;
    
    /**
     * 申请单作废标志
     */
    private String sqdzfbz;
    
    /**
     * 申请单作废原因
     */
    private String sqdzfyy;
    
    /**
     * 申请单作废日期
     */
    private String sqdzfrq;
    
    /**
     * 受理点id
     */
    private String sldid;
    
    /**
     * 开票机号
     */
    private String kpjh;
    
    /**
     * 发票代码
     */
    private String fpdm;
    
    /**
     * 发票号码
     */
    private String fphm;
    
    /**
     * 发票起号
     */
    private String fpqh;
    
    /**
     * 发票止号
     */
    private String fpzh;
    
    /**
     * 销货方名称
     */
    private String xfmc;
    
    /**
     * 购方名称
     */
    private String gfmc;
    
    /**
     * 开票时间
     */
    private String kpsj;
    
    /**
     * 开票金额
     */
    private String kpje;
    
    /**
     * 开票类型
     */
    private String kplx;
    
    /**
     * 回推状态
     */
    private String htzt;
    
    /**
     * 订单类型
     */
    private String ddlx;
    
    /**
     * 开票人
     */
    private String kpr;
    
    /**
     * 作废标志
     */
    private String zfbz;
    
    /**
     * 作废原因
     */
    private String zfyy;
    
    /**
     * 作废时间
     */
    private String zfsj;
    
    /**
     * 作废时间
     */
    private String createTime;
    /**
     * 销方税号
     */
    private String xhfNsrsbh;
}
