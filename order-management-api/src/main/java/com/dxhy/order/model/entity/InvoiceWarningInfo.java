package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class InvoiceWarningInfo implements Serializable {
    
    /**
     * 预警表id
     */
    private String id;
    
    /**
     * 销方税号
     */
    private String xhfNsrsbh;
    
    /**
     * 销方名称
     */
    private String xhfMc;
    
    /**
     * 设备编号
     */
    private String sbbh;
    
    /**
     * 设备名称
     */
    private String sbMc;
    
    /**
     * 发票种类代码
     */
    private String fpzlDm;
    
    /**
     * 预警份数
     */
    private String yjfs;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String eMail;
    
    /**
     * 用户id
     */
    private String userId;
    
    /**
     * 预警次数
     */
    private String yjcs;
    
    /**
     * 是否预警
     */
    private String sfyj;
    
    /**
     * 部门id
     */
    private String deptId;
    
    /**
     * 删除状态
     */
    private String deleteStatus;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
}
