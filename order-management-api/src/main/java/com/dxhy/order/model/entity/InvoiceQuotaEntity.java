package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票限额表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:13
 */
@Getter
@Setter
public class InvoiceQuotaEntity  implements Serializable {
    
    /**
     * 主键
     */
    private String   id;
    /**
     *发票限额
     */
    private String   invoiceAmount;
    /**
     * 发票种类 51 电子发票开票限额 ； 2 普通发票开票限额；0 专用发票开票限额
     */
    private String   invoiceType;
    /**
     * 税号
     */
    private String   taxpayerCode;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *当前登录人id
     */
    private String   userId;

}
