package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 购方信息实体类
 *
 * @author liangyuhuan
 * @date 2018/7/31
 */
@Getter
@Setter
public class BuyerEntity implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 纳税人识别号
     */
    private String taxpayerCode;
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    
    /**
     * 销货方纳税人名称
     */
    private String xhfMc;
    
    /**
     * 购方名称
     */
    private String purchaseName;
    /**
     * 购方地址
     */
    private String address;
    /**
     * 购方电话
     */
    private String phone;
    /**
     * 开户银行
     */
    private String bankOfDeposit;
    /**
     * 银行账号
     */
    private String bankNumber;
    /**
     * 购方联系人邮箱
     */
    private String email;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人
     */
    private String createUserId;
    /**
     * 更新时间
     */
    private Date modifyTime;
    /**
     * 更新人ID
     */
    private String modifyUserId;
    /**
     * 购货方企业类型(01:企业 02：机关事业单位 03：个人 04：其它)
     */
    private String ghfQylx;
    
    /**
     * 唯一编码   后期添加
     */
    private String buyerCode;
    
    /**
     * 是否编辑,仅供前端调用传参使用
     */
    private String isEdit;
}
