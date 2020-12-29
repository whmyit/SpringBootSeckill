package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class InvoiceTypeCodeExt implements Serializable {
    
    private String id;
    
    private String invoiceTypeCodeId;
    
    private String fpzlDm;
    
    private String fpzlDmMc;
    
    private Date createTime;
    
    private String xhfNsrsbh;
    
}
