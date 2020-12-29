package com.dxhy.order.consumer.modules.scaninvoice.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：PageQrcodeOrderItemInfo
 * @Description ：
 * @date ：2020年4月14日 上午9:25:15
 */
@Setter
@Getter
public class PageQrcodeOrderItemInfo {
	
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
