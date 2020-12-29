package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单明细业务bean
 *
 * @author zsc
 */
@Setter
@Getter
public class OrderItemInfo implements Serializable {
    /**
     * 订单明细表id
     */
    private String id;

    /**
     * 订单主表id
     */
    private String orderInfoId;

    /**
     * 商品行序号
     */
    private String sphxh;

    /**
     * 项目名称
     */
    private String xmmc;

    /**
     * 项目单位
     */
    private String xmdw;

    /**
     * 规格型号
     */
    private String ggxh;

    /**
     * 项目数量
     */
    private String xmsl;

    /**
     * 含税标志
     */
    private String hsbz;

    /**
     * 发票行性质
     */
    private String fphxz;

    /**
     * 项目单价
     */
    private String xmdj;

    /**
     * 商品编码
     */
    private String spbm;

    /**
     * 自行编码
     */
    private String zxbm;

    /**
     * 优惠政策标识
     */
    private String yhzcbs;

    /**
     * 零税率标识
     */
    private String lslbs;

    /**
     * 增值税特殊管理
     */
    private String zzstsgl;

    /**
     * 扣除额
     */
    private String kce;

    /**
     * 项目金额
     */
    private String xmje;

    /**
     * 税率
     */
    private String sl;
    
    /**
     * 税额
     */
    private String se;
    
    /**
     * 尾差金额
     */
    private String wcje;
    
    /**
     * 销方税号
     */
    private String xhfNsrsbh;
    
    /**
     * 备用字段1
     */
    private String byzd1;
    
    /**
     * 备用字段2
     */
    private String byzd2;

    /**
     * 备用字段3
     */
    private String byzd3;

    /**
     * 备用字段4
     */
    private String byzd4;

    /**
     * 备用字段5
     */
    private String byzd5;

    /**
     * 创建时间
     */
    private Date createTime;


}
