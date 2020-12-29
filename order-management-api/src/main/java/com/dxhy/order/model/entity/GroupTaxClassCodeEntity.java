package com.dxhy.order.model.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 集团税编实体
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:03
 */
@Setter
@Getter
public class GroupTaxClassCodeEntity implements Serializable {

    private String id;
    
    private Long sortId;

    private String xhfNsrsbh;

    private String merchandiseName;

    private String encoding;

    private String taxItems;

    private String briefCode;

    private String specificationModel;

    private String meteringUnit;

    private String unitPrice;

    private String taxClassCode;

    private String taxClassificationName;

    private Date createTime;

    private String groupId;

    private String dataSource;

    private String matchingState;

    private String dataState;

    private String shareState;

    private String deptId;

    /**
    * 税收分类简称
    */
    private String taxClassAbbreviation;

    /**
    * 差异标识
    */
    private String differenceFlag;
    /**
    * 描述
    */
    private String description;
    /**
    * 分组名称
    */
    private String groupName;

}
