package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：PageOrderItemInfo
 * @Description ：发票开票页面对应的model
 * @date ：2018年7月23日 下午3:36:51
 */
@Setter
@Getter
public class PageOrderItemInfo {
    /**
     * 项目名称
     */
    private String xmmc;
    /**
     * 规格型号
     */
    private String ggxh;
    /**
     * 项目单位
     */
    private String xmdw;
    /**
     * 项目数量
     */
    private String xmsl;
    /**
     * 项目单价
     */
    private String xmdj;
    /**
     * 项目金额
     */
    private String xmje;
    /**
     * 税率
     */
    private String sl;
    /**
     * 商品编码
     */
    private String spbm;
    /**
     * 项目税额
     */
    private String xmse;
    /**
     * 含税标志
     */
    private String hsbz;
    /**
     * 发票行性质
     */
    private String fphxz;
    /**
     * 优惠政策标识
     */
    private String yhzcbs;
    /**
     * 增值税特殊管理
     */
    private String zzstsgl;
    /**
     * 零税率标识
     */
    private String lslbs;
    /**
     * kce
     */
    private String kce;

}
