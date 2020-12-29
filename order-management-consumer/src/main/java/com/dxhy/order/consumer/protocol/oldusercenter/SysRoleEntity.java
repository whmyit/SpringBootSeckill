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
public class SysRoleEntity implements Serializable {
    private Long roleId;
    private String roleName;
    private Integer type;
    private String remark;
    private Long deptId;
    private Date createTime;
    private Long pUserId;
}
