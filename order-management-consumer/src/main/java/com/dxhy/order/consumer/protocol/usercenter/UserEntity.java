package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户中心认证：用户信息实体
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8
 */
@Setter
@Getter
public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 盐
     */
    private String salt;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 状态 (0：禁用 1：正常)
     */
    private Integer status;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 组织名称
     */
    private String deptName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 姓名
     */
    private String name;
    /**
     * 最后登陆时间
     */
    private Date lastLoginTime;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 删除标志(0-正常，1-删除)
     */
    private Integer delFlag;
    /**
     * 账号类型(1.主账户  2子账户 3个人用户)
     */
    private Integer userType;
    /**
     * 用户来源(1系统增加 2用户自注册 3 第三方登录)
     */
    private String userSource;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 组织信息集合
     */
    private DeptEntity dept;
    /**
     * 纳税人识别号组织信息集合
     */
    private List<TaxPlayerCodeDept> taxplayercodeDeptList;
    /**
     * 菜单信息集合
     */
    private List<MenuEntity> menus;
}
