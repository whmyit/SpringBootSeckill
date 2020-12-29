package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 发票明细汇总请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:10
 */
@Setter
@Getter
public class InvoiceItemRequestInfo {
    /**
     * 主键
     */
    private String id;
    
    /**
     * 企业id
     */
    private String companyId;
    
    /**
     * 所属期间
     */
    private String reportPeriod;
    
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 汇总日期
     */
    private String hzrq;
    
    /**
     * 商品编码
     */
    private String spbm;
    
    /**
     * 项目名称
     */
    private String xmmc;
    
    /**
     * 发票号码
     */
    private String invoiceNum;
    
    /**
     * 项目金额
     */
    private String xmje;
    
    /**
     * 税额
     */
    private String se;
    
    /**
     * 开票合计金额
     */
    private String kphjje;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 完成标识
     */
    private String completeFlag;
    
}
