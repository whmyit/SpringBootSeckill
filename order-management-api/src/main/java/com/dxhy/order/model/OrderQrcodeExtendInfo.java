package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
/**
 * 动态码表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:25
 */
@Getter
@Setter
public class OrderQrcodeExtendInfo implements Serializable {
    
    private String id;
    
    private String orderInfoId;
    
    private String authOrderId;
    
    private String openId;
    
    private String unionId;
    
    private String quickResponseCodeType;
    
    private String fpzlDm;
    
    private String fpqqlsh;
    
    private String tqm;
    
    private String ddh;
    
    private String kphjje;
    
    private String xhfMc;
    
    private String xhfNsrsbh;
    
    private String zfzt;
    
    private String cardStatus;
    
    private String ewmzt;
    
    private String quickResponseCodeUrl;
    
    private Date quickResponseCodeValidTime;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String dataStatus;
    
}
