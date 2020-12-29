package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 税控设备类型
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:44
 */
@Getter
@Setter
public class TaxEquipmentInfo implements Serializable {
    
    private String id;
    
    private String xhfNsrsbh;
    
    private String xhfMc;
    
    private String groupId;
    
    private String groupName;
    
    private String sksbCode;
    
    private String sksbName;
    
    private String sksbType;
    
    private Date linkTime;
    
    /**
     * 省份代码
     */
    private String sfdm;
    
    /**
     * 省份名称
     */
    private String sfmc;
    
    private String bz;
    
    private String createUserId;
    
    private String updateUserId;
    
    /**
     * 删除标志(0:未删除;1:已删除)
     */
    private String deleted;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String pageSize;
    
    private String currentPage;
    
}
