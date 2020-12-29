package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票开具请求信息项目信息
 * todo V1和V2使用,后期不改更新维护
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@Deprecated
public class COMMON_INVOICE_DETAIL implements Serializable {
    
    /**
     * 项目序号
     */
    private String XMXH;
    /**
     * 发票行性质
     */
    private String FPHXZ;
    /**
     * 商品编码
     */
    private String SPBM;
    /**
     * 自行编码
     */
    private String ZXBM;
    /**
     * 优惠政策标识
     */
    private String YHZCBS;
    /**
     * 零税率标识
     */
    private String LSLBS;
    /**
     * 增值税特殊管理
     */
    private String ZZSTSGL;
    /**
     * 项目名称
     */
    private String XMMC;
    /**
     * 规格型号
     */
    private String GGXH;
    /**
     * 单位
     */
    private String DW;
    /**
     * 项目数量
     */
    private String XMSL;
    /**
     * 项目单价
     */
    private String XMDJ;
    /**
     * 项目金额
     */
    private String XMJE;
    /**
     * 项目编码
     */
    private String XMBM;
    /**
     * 含税标志
     */
    private String HSBZ;
    /**
     * 税率
     */
    private String SL;
    /**
     * 税额
     */
    private String SE;
    /**
     * 备用字段1
     */
    private String BYZD1;
    /**
     * 备用字段2
     */
    private String BYZD2;
    /**
     * 备用字段3
     */
    private String BYZD3;
    
}
