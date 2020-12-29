package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 专票申请单明细业务bean
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class SpecialInvoiceReversalItemEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    
    private String specialInvoiceReversalId;
    
    private String code;
    
    private String name;
    
    private String spec;
    
    private String unit;
    
    private String quantity;
    
    private String unitPrice;
    
    private String amount;
    
    private String taxRate;
    
    private String taxAmount;
    
    private String taxFlag;
    
    private String seqNum;
    
    private String isSpecial;
    
    private String specialType;
    
    /**
     * 增值税特殊管理
     */
    private String zeroTaxMark;
    
    private String creatorId;
    
    private String creatorName;
    
    private Date createTime;
    
    private String editorId;
    
    private String editorName;
    
    private Date editTime;
    
}
