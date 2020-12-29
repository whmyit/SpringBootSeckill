package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 推送数据库实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class PushInfo implements Serializable {
    
    private String id;
    
    private String nsrsbh;

    private String xhfMc;
    
    private String pushUrl;
    
    private String encryptCode;
    
    private String zipCode;
    
    private String status;
    
    private String byzd1;
    
    private String byzd2;
    
    private Date createTime;
    
    private Date modifyTime;
    /**
     * 版本标识
     */
    private String versionIdent;

    /**
    * 接口类型
    */
    private String interfaceType;
    /**
    * 协议类型
    */
    private String protocolType;
    
}
