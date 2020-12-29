package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户引导
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:46
 */
@Getter
@Setter
public class UserGuiderInfo implements Serializable {
    
    private String id;
    
    private String operate;
    
    private String operateGroup;
    
    private String operateDescription;
    
    private String step;
    
    private String videoUrl;
    
    private String status;
    
    private String uid;
    
    private String xhfNsrsbh;
    
    private Date createTime;
    
    private Date updateTime;
    
}
