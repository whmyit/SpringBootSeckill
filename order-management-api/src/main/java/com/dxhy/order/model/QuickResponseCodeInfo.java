package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
/**
 * 静态码
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:31
 */
@Getter
@Setter
public class QuickResponseCodeInfo implements Serializable {
    
    private String id;
    
    private String invoiceTypeCodeId;
    
    private String quickResponseCodeType;
    
    private String xhfMc;
    
    private String xhfNsrsbh;
    
    private String xhfDz;
    
    private String xhfDh;
    
    private String xhfYh;
    
    private String xhfZh;
    
    private String ywlx;
    
    private String ywlxId;
    
    private String sldMc;
    
    private String sld;
    
    private String fjh;
    
    private String kpr;
    
    private String skr;
    
    private String fhr;
    
    private String tqm;
    
    private String ewmzt;
    
    private String quickResponseCodeUrl;
    
    private Date quickResponseCodeValidTime;
    
    private Date createTime;
    
    private Date updateTime;
    
}
