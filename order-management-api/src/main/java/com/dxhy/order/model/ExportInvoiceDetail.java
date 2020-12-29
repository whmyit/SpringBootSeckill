package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 发票明细导出类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:14
 */
@Setter
@Getter
public class ExportInvoiceDetail implements Serializable {
    /**
     * 商品信息
     */
    private List<OrderItemInfo> orderItemList;
    
    /**
     * 订单表Id
     */
    private String orderInfoId;
    
    /**
     * 订单号
     */
    private String ddh;
    
    /**
     * 发票代码
     */
    private String fpdm;
    
    /**
     * 发票号码
     */
    private String fphm;
    
    /**
     * 购货方名称
     */
    private String ghfMc;
    
    /**
     * 开票日期
     */
    private Date kprq;
    
    /**
     * 购货方纳税人识别号
     */
    private String ghfNsrsbh;
    
    /**
     * 购货方银行
     */
    private String ghfYh;
    
    /**
     * 购货方帐号
     */
    private String ghfZh;
    
    /**
     * 购货方地址
     */
    private String ghfDz;
    
    /**
     * 销货方地址
     */
    private String xhfDz;
    
    /**
     * 购货方电话
     */
    private String ghfDh;
    
    /**
     * 购货方手机
     */
    private String ghfSj;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 开票合计金额
     */
    private String kphjje;
    
    /**
     * 合计不含税金额
     */
    private String hjbhsje;
    
    /**
     * 合计税额
     */
    private String hjse;
    
    /**
     * 开票人
     */
    private String kpr;
    
    /**
     * 开票类型
     */
    private String kplx;
    
    
    /**
     * 门店号
     */
    private String mdh;
    
    /**
     * 备注
     */
    private String bz;
    
    /**
     * 编码表版本号
     */
    private String bbmBbh;
    
    
}
