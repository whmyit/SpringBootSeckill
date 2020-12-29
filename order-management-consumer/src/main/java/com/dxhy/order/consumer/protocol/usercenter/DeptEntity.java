package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户中心认证：组织实体
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8 18:22
 */
@Setter
@Getter
public class DeptEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 组织唯一标识ID
     */
    private String deptId;
    /**
     * 上级组织ID
     */
    private String parentId;
    /**
     * 组织名称
     */
    private String name;
    /**
     * 组织简称
     */
    private String deptSname;
    /**
     * 树形编码
     */
    private String code;
    /**
     * 组织层级
     */
    private Integer level;
    /**
     * 纳税人识别号
     */
    private String taxpayerCode;
    /**
     * 纳税人注册省份
     */
    private String taxpayerProvince;
    /**
     * 纳税人注册市区
     */
    private String taxpayerCity;
    /**
     * 纳税人注册区/县
     */
    private String taxpayerCounty;
    /**
     * 纳税人地址
     */
    private String taxpayerAddress;
    /**
     * 纳税人电话
     */
    private String taxpayerPhone;
    /**
     * 纳税人开户行
     */
    private String taxpayerBank;
    /**
     * 纳税人账号
     */
    private String taxpayerAccount;
    /**
     * 纳税人类型(1:一般纳税人 2:小规模纳税人)
     */
    private Integer taxpayerType;
    /**
     * 纳税人所属行业
     */
    private String taxpayerIndustry;
    /**
     * 会计准则
     */
    private String accountingPrinciple;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 组织类型(1.总公司 2.子公司 3.分支机构 4.部门 5.虚拟机构 6.独立公司)
     */
    private Integer deptType;
    /**
     * 创建人名称
     */
    private Long createUser;
    /**
     * 最后更新人名称
     */
    private Long updateUser;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 联系人邮箱
     */
    private String contactEmail;
    /**
     * 企业编码
     */
    private String enterpriseNumbers;
    /**
     * 企业授权码
     */
    private String authorizationCode;
    /**
     * 渠道来源ID
     */
    private String sourceId;
    /**
     * 排序
     */
    private Integer orderNum;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否删除
     */
    private String delFlag;
    /**
     * 数据来源
     */
    private String dataSource;
    /**
     * 菜单ID
     */
    private Long menuId;
    /**
     * 套餐ID
     */
    private Long setMealId;
    /**
     * 套餐数量
     */
    private Integer einNumber;
}
