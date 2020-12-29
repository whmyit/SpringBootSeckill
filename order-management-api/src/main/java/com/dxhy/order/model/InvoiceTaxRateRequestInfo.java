package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 发票税率汇总请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:15
 */
@Setter
@Getter
public class InvoiceTaxRateRequestInfo {
    /**
     * 主键
     */
    private String id;
    
    /**
     * 企业id
     */
    private String companyId;
    
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 开票类型
     */
    private String kplx;
    
    /**
     * 所属期间
     */
    private String reportPeriod;
    
    /**
     * 汇总日期
     */
    private String hzrq;
    
    /**
     * 税率
     */
    private String sl;
    
    /**
     * 正数有效发票金额
     */
    private String invoiceAmountPt;
    
    /**
     * 正数有效发票税额
     */
    private String taxAmountPt;
    
    /**
     * 正数有效价税合计
     */
    private String totalAmountPt;
    
    /**
     * 负数有效发票金额
     */
    private String invoiceAmountNt;
    
    /**
     * 负数有效发票税额
     */
    private String taxAmountNt;
    
    /**
     * 负数有效价税合计
     */
    private String totalAmountNt;
    
    /**
     * 正数作废发票金额
     */
    private String invoiceAmountPtVoid;
    
    /**
     * 正数作废发票税额
     */
    private String taxAmountPtVoid;
    
    /**
     * 正数作废价税合计
     */
    private String totalAmountPtVoid;
    
    /**
     * 正数作废价税合计
     */
    private String invoiceAmountNtVoid;
    
    /**
     * 负数作废发票金额
     */
    private String taxAmountNtVoid;
    
    /**
     * 负数作废发票税额
     */
    private String totalAmountNtVoid;
    
    /**
     * 负数作废价税合计
     */
    private Date createTime;
    /**
     * （1未完成，2已完成，3汇总失败）
     */
    private String completeFlag;
    
}
