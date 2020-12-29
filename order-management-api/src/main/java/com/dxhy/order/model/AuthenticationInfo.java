package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 鉴权用户数据库实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class AuthenticationInfo implements Serializable {

    private String id;

    private String nsrsbh;

    private String secretId;

    private String secretKey;

    private String authStatus;

    private String isConfig;

    private Date createTime;

    private Date updateTime;

    private String xhfMc;

}
