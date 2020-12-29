package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品编码实体类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:12
 */
@Setter
@Getter
public class CommodityCodeEntity implements Serializable {

    /**
     * id
     */
    private String id;
    /**
     * 序号
     */
    private Long sortId;
    
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 商品名称
     */
    private String merchandiseName;
    /**
     * 编码
     */
    private String encoding;
    /**
     * 商品税目
     */
    private String taxItems;
    /**
     * 简码
     */
    private String briefCode;
    /**
     * 税率
     */
    private String taxRate;
    /**
     * 规格型号
     */
    private String specificationModel;
    /**
     * 计量单位
     */
    private String meteringUnit;
    /**
     * 单价
     */
    private String unitPrice;
    /**
     * 含税价标志（0：否，1：是)
     */
    private String taxLogo;
    /**
     * '隐藏标志（0：否，1：是）;
     */
    private String hideTheLogo;
    /**
     * '享受优惠政策（0：否，1：是）',
     */
    private String enjoyPreferentialPolicies;
    /**
     * 税收分类编码
     */
    private String taxClassCode;
    /**
     * 税收分类名称
     */
    private String taxClassificationName;
    /**
     * 优惠政策类型jh：0:出口零税,1：免税，2：不征税 3:普通零税率
     */
    private String preferentialPoliciesType;
    /**
     * 当前登录人id
     */
    private String userId;
    /**
     * 数据创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 更新人
     */
    private String modifyUserId;
    /**
     * 分组id
     */
    private String groupId;
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 分组编码
     */
    private String groupCode;

    /**
    * 数据来源
    */
    private String dataSource;
    /**
     * 数据状态标识
     */
    private String dataState;
    /**
    * 匹配状态
    */
    private String matchingState;
    /**
    * 企业名称
    */
    private String enterpriseName;
    /**
    * 税收简称
    */
    private String taxClassAbbreviation;
    /**
    * 免税类型
    */
    private String taxExemptionType;
    /**
    * 描述
    */
    private String description;
    /**
    * 采集标识
    */
    private String collectIdent;


}
