package com.dxhy.order.consumer.protocol.oldusercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * TODO:  功能描述
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/18 20:27
 */
@Getter
@Setter
public class SsoUser implements Serializable {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String mobile;
    private Integer status;
    private Date createTime;
    private String number;
    private String salt;
    private String name;
    private Long currentDeptId;
    private SysDeptEntity dept;
    private SysDeptEntity parentDept;
    private List<SysRoleEntity> roles;
    private List<SysMenuEntity> menus;
    private List<Long> dataPerm;
    private List<SysDeptEntity> taxplayercodeDeptList;
    private Map<String, String> plugininfo;
    private String version;
    private int expireMinite;
    private long expireFreshTime;
    private int loginCount;
    private Date lastLoginTime;
    private int boradAuth;
    private Integer userSource;
}
