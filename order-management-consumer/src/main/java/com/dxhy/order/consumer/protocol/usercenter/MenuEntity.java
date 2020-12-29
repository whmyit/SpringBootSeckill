package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户中心认证：菜单实体
 *
 * @author: <a href="tivenninesongs@163.com;</a>
 * @createDate: Created in 2020/4/8 19:32
 */
@Getter
@Setter
public class MenuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 菜单id
     */
    private Integer menuId;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 菜单权限标识
     */
    private String permission;
    /**
     * 前端URL
     */
    private String path;
    /**
     * 请求链接
     */
    private String url;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 父菜单ID
     */
    private Integer parentId;
    /**
     * 图标
     */
    private String icon;
    /**
     * VUE页面
     */
    private String component;
    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 菜单类型(0菜单 1按钮)
     */
    private String type;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;
}
