package com.dxhy.order.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 专票冲红 红字信息表上传批次 请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:23
 */
@Setter
@Getter
public class RED_INVOICE_FORM_BATCH implements Serializable {
    
    /**
     * 申请表上传请求批次号
     */
    private String SQBSCQQPCH;
    
    /**
     * 申请方纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 开票点ID
     */
    private String SLDID;
    
    /**
     * 开票机号
     */
    private String KPJH;
    
    /**
     * 发票类型
     */
    private String FPLX;
    
    /**
     * 发票类别
     */
    private String FPLB;
    
    /**
     * 申请类别,0为销方申请,1为购方申请
     */
    private String SQLB;
    
    /**
     * 扩展字段
     */
    private String KZZD;
}
