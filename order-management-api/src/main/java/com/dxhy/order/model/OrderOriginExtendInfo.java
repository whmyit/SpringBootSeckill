package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单原始订单信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:24
 */
@Setter
@Getter
public class OrderOriginExtendInfo implements Serializable {
    
    private String id;
    
    private String orderId;
    
    private String fpqqlsh;
    
    private String originOrderId;
    
    private String originFpqqlsh;
    
    private String originDdh;
    
    private String xhfNsrsbh;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String status;
}
