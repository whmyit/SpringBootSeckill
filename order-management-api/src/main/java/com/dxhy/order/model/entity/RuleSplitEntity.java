package com.dxhy.order.model.entity;

import java.io.Serializable;

/**
 * 拆分规则
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:19
 */
public class RuleSplitEntity implements Serializable {
    /**
     * id
     */
    private String id;
    /**
     * 税号
     */
    private String taxpayerCode;
    /**
     * 当前登录人id
     */
    private String  userId;
    /**
     * 创建时间
     */
    private String  createTime;
    /**
     * 拆分规则
     *0 : 保金额 ;  1 : 保数量  ; 2 : 保单价
     */
    private String   ruleSplitType;

    /**
     *修改时间
     */
    private String updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxpayerCode() {
        return taxpayerCode;
    }

    public void setTaxpayerCode(String taxpayerCode) {
        this.taxpayerCode = taxpayerCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRuleSplitType() {
        return ruleSplitType;
    }

    public void setRuleSplitType(String ruleSplitType) {
        this.ruleSplitType = ruleSplitType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
