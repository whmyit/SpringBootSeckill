package com.dxhy.order.consumer.protocol.oldusercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO:  功能描述
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/18 20:29
 */
@Getter
@Setter
public class SysMenuEntity implements Serializable {
    private Long menuId;
    private Long parentId;
    private String name;
    private String url;
    private String perms;
    private Integer type;
    private String icon;
    private Integer orderNum;
    private String systemSign;
    private Date createTime;
    private Long pUserId;
    
}
