package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票快递
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:51
 */
@Setter
@Getter
public class FpExpress implements Serializable {
    /**
     * 主键
     */
    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 组织id
     */
    private String orgId;
    /**
     * 寄件人姓名
     */
    private String senderName;
    /**
     * 寄件人地址
     */
    private String senderAddress;
    /**
     * 寄件人电话
     */
    private String senderPhone;
    /**
     * 寄件人邮箱
     */
    private String senderMail;
    /**
     * 寄件人邮编
     */
    private String senderPostCode;
    /**
     * 收件人姓名
     */
    private String recipientsName;
    /**
     * 收件人地址
     */
    private String recipientsAddress;
    /**
     * 收件人电话
     */
    private String recipientsPhone;
    /**
     * 收件人邮箱
     */
    private String recipientsMail;
    /**
     * 收件人邮编
     */
    private String recipientsPostCode;
    /**
     * 快递公司名称
     */
    private String expressCompanyName;
    /**
     * 快递公司编码
     */
    private String expressCompanyCode;
    /**
     * 快递单号
     */
    private String expressNumber;
    /**
     * 购方名称
     */
    private String buyerName;
    /**
     * 快递状态
     */
    private String expressState;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 快递的物品信息:多个物品逗号分隔
     */
    private String expressItems;
    
    @Override
    public String toString() {
        return "FpExpress{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", orgId='" + orgId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", senderPhone='" + senderPhone + '\'' +
                ", senderMail='" + senderMail + '\'' +
                ", senderPostCode='" + senderPostCode + '\'' +
                ", recipientsName='" + recipientsName + '\'' +
                ", recipientsAddress='" + recipientsAddress + '\'' +
                ", recipientsPhone='" + recipientsPhone + '\'' +
                ", recipientsMail='" + recipientsMail + '\'' +
                ", recipientsPostCode='" + recipientsPostCode + '\'' +
                ", expressCompanyName='" + expressCompanyName + '\'' +
                ", expressCompanyCode='" + expressCompanyCode + '\'' +
                ", expressNumber='" + expressNumber + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", expressState='" + expressState + '\'' +
                ", createTime=" + createTime +
                ", expressItems='" + expressItems + '\'' +
                '}';
    }
}
