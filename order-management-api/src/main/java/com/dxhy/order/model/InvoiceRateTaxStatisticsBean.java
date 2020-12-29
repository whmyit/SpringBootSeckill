package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 销项发票按税率统计
 * @Author:xueanna
 * @Date:2019/5/31
 */

@Setter
@Getter
public class InvoiceRateTaxStatisticsBean implements Serializable {
    private static final long serialVersionUID = 1L;
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
    private int xhfNsrsbh;
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
     * 年份
     */
    private String year;
    /**
     * 月份
     */
    private String month;
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
     * 正数作废价税合计
     */
    private String zszfjshj;
    
    /**
     * 正数有效价税合计
     */
    private String total_amount_pt;
    /**
     * 负数有效发票金额
     */
    private String invoice_amount_nt;
    /**
     * 负数有效发票税额
     */
    private String tax_amount_nt;
    
    /**
     * 负数有效价税合计
     */
    private String total_amount_nt;
    
    /**
     * 正数作废发票金额
     */
    private String invoice_amount_pt_void;
    /**
     * 正数作废发票税额
     */
    private String tax_amount_pt_void;
    
    /**
     * 正数作废价税合计
     */
    private String total_amount_pt_void;
    /**
     * 负数作废发票金额
     */
    private String invoice_amount_nt_void;
    /**
     * 负数作废发票税额
     */
    private String tax_amount_nt_void;
    
    /**
     * 负数作废价税合计
     */
    private String total_amount_nt_void;
    
    /**
     * 创建时间
     */
    private Date create_time;
    
    /**
     * 是否历史数据
     */
    private String ishistory;
    
}
