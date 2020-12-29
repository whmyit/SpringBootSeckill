package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 红字申请单明细信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:40
 */
@Getter
@Setter
public class SpecialInvoiceReversalItem implements Serializable{

    private String id;

    private String specialInvoiceReversalId;

    private String spbm;

    private String zxbm;

    private String xmmc;

    private String ggxh;

    private String xmdw;

    private String xmsl;

    private String xmdj;

    private String kce;

    private String xmje;

    private String sl;

    private String se;

    private String hsbz;

    private String fphxz;

    private String sphxh;

    private String lslbs;

    private String yhzcbs;

    private String zzstsgl;

    private String wcje;

    private Date createTime;

}
