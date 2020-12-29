package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用户中心认证：角色实体
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8 19:25
 */
@Getter
@Setter
public class RoleEntity implements Serializable {

    /**
     * 角色id
     */
    private Integer roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 创建组织
     */
    private String deptName;
    /**
     * 角色类型
     */
    private Integer type;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改日期
     */
    private String updateTime;
    /**
     * 角色说明
     */
    private String describe;
}
