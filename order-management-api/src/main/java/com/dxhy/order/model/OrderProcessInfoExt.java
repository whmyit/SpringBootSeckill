package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单处理扩展业务bean
 *
 * @author zsc
 */
@Setter
@Getter
public class OrderProcessInfoExt implements Serializable {
    /**
     * 订单处理扩展表id
     */
    private String id;

    /**
     * 订单处理表id
     */
    private String orderProcessInfoId;
    
    /**
     * 对应父订单表id
     */
    private String parentOrderInfoId;
    
    /**
     * 对应父订单处理表id
     */
    private String parentOrderProcessId;
    
    /**
     * 销货方税号
     */
    private String xhfNsrsbh;
    
    /**
     * 处理扩展表状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
