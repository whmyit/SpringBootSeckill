package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票预警业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:34
 */
@Getter
@Setter
public class SalerWarning implements Serializable {
    private String id;
    
    private String xhfNsrsbh;
    
    private String waringEmail;
    
    private String warningFlag;
    
    private String deptId;
    
    private String createId;
    
    private String createUser;
    
    private Date createTime;
    
    private Date updateTime;
    
}
