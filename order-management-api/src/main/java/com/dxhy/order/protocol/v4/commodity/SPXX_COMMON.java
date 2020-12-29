package com.dxhy.order.protocol.v4.commodity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 商品信息协议bean：公共字段
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/20
 */
@Getter
@Setter
public class SPXX_COMMON implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 销货方纳税人识别号
     */
    private String XHFSBH;

    /**
     * 销货方纳税人名称
     */
    private String XHFMC;

    /**
     * 商品对应的ID
     */
    private String SPID;

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
     * 单价
     */
    private String DJ;

    /**
     * 含税标志
     */
    private String HSBZ;

    /**
     * 税率
     */
    private String SL;
}
