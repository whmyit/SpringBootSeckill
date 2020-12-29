package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class InvoicePrintInfo implements Serializable {
    
    private String id;
    
    private String fpid;
    
    private String xhfNsrsbh;
    
    private String fpzldm;
    
    private String fpdm;
    
    private String fphm;
    
    private String spotKey;
    
    private String printStatus;
    
    private String printMsg;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String fpdypch;
    
    private String dylx;
    
    private String dyjmc;
    
    private String fgStatus;
    
    private String zpy;
    
    private String spy;
    
    private String fpqqlsh;
    
}
