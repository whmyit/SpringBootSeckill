package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票邮寄实体类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:35
 */
@Setter
@Getter
public class SenderEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 邮寄角色 0 寄件人 1 收件人
     */
    private String type;
    /**
     * 寄件人ID
     */
    private String senderId;
    /**
     * 收件人ID
     */
    private String recipientsId;
    /**
     * 姓名
     */
    private String name;
    /**
     * 电话
     */
    private String phone;
    /**
     * 地址
     */
    private String address;
    /**
     * 邮箱
     */
    private String mail;
    /**
     * 邮编
     */
    private String postCode;
    /**
     * 收件人公司名称
     */
    private String recipientsCompanyName;
    /**
     * 购方名称
     */
    private String buyerName;
    /**
     * 发票代码/号码
     */
    private String fpdmhm;
    /**
     * 发票类型
     */
    private String fplx;
    /**
     * 开票时间
     */
    private Date createTime;
    /**
     * 开票类型
     */
    private String kplx;
    /**
     * 快递公司
     */
    private String companyName;
    /**
     * 快递单号
     */
    private String trackNum;
}
