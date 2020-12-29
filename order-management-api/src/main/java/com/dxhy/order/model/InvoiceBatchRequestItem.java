package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 发票批量开票明细数据库实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class InvoiceBatchRequestItem implements Serializable {
    
    private String id;
    
    private String invoiceBatchId;
    
    private String fpqqpch;
    
    private String fpqqlsh;
    
    private String kplsh;
    
    private String xhfNsrsbh;
    
    private String status;
    
    private String message;
    
    private Date createTime;
    
    private Date updateTime;
    
}
