package com.dxhy.order.consumer.protocol.oldusercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO:  功能描述
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/18 20:30
 */
@Getter
@Setter
public class SysDeptEntity implements Serializable {
    private Long deptId;
    private Long parentId;
    private String name;
    private String code;
    private Integer level;
    private String taxpayerCode;
    private String taxpayerName;
    private String taxpayerAddress;
    private String taxpayerPhone;
    private String taxpayerBank;
    private String taxpayerAccount;
    private Integer areaId;
    private Date createTime;
    private Integer deptType;
    private Long createUser;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String deptSname;
    private String enterpriseNumbers;
}
