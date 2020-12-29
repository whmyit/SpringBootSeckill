package com.dxhy.order.consumer.constant;

import lombok.Getter;

/**
 * @Author fankunfeng
 * @Date 2019-02-28 17:24:20
 * @Describe
 */
@Getter
public enum NewExcelEnum{

    /**
     * 订单号
     */
    ORDER_DDH(0, "ddh","订单号"),
    /**
     * 开票类型
     */
    ORDER_KPLX(1, "fpzlDm","发票类型"),
    /**
     * 购货方企业类型
     */
    ORDER_GHFQYLX(2, "ghfqylx","抬头类型"),
    /**
     * 购货方名称
     */
    ORDER_GHFMC(3, "ghf_mc","购方名称"),
    /**
     * 购货方纳税人识别号
     */
    ORDER_GHFNSRSBH(4, "ghf_nsrsbh","购方税号"),
    /**
     * 购货方地址
     */
    ORDER_GHFDZ(5, "ghf_dz","购方地址/购方电话"),
    /**
     * 购货方电话
     */
    ORDER_GHFDH(6, "ghf_dh","购方电话"),
    /**
     * 购货方银行
     */
    ORDER_GHFYH(7, "ghf_yh","开户银行"),
    /**
     * 购货方账号
     */
    ORDER_GHFZH(8, "ghf_zh","银行账号"),
    /**
     * 项目名称
     */
    ORDERITEM_XMMC(9, "xmmc","商品名称"),
    /**
     * 规格型号
     */
    ORDERITEM_GGXH(10, "ggxh","规格型号"),
    /**
     * 项目单位
     */
    ORDERITEM_XMDW(11, "xmdw","单位"),
    /**
     * 项目数量
     */
    ORDERITEM_XMSL(12, "xmsl","数量"),
    /**
     * 项目单价
     */
    ORDERITEM_XMDJ(13, "xmdj","单价"),
    /**
     * 项目金额
     */
    ORDERITEM_XMJE(14, "xmje","金额"),
    /**
     * 含税标志
     */
    ORDERITEM_HSBZ(15, "hsbz","含税标志"),

    // 是否享受税收优惠政策
    // 享受税收优惠政策内容	零税率	企业自编码	备注
    /**
     * 税率
     */
    ORDERITEM_SL(16, "sl","税率"),
    /**
     * 税额
     */
    ORDERITEM_SE(17, "se","税额"),
    /**
     * 编码版本号
     */
    ORDERITEM_BMBBBH(18, "bmbbbh","编码版本号"),
    /**
     * 商品编码(税收分类编码)
     */
    ORDERITEM_SPBM(19, "spbm","税收分类编码"),
    /**
     * 优惠政策标识（是否享受优惠政策）
     */
    ORDERITEM_YHZCBS(20, "yhzcbs","是否享受税收优惠政策*"),
    /**
     * 增值税特殊管理（享受税收优惠政策内容）
     */
    ORDERITEM_ZZSTSGL(21, "zzstsgl","享受税收优惠政策内容"),
    /**
     * 零税率标识
     */
    ORDERITEM_LSLBS(22, "lslbs","零税率"),
    /**
     * 自行编码（企业自编码）
     */
    ORDERITEM_ZXBM(23, "zxbm","企业自编码"),
    /**
     * 备注
     */
    ORDER_BZ(24, "bz","备注"),
	/* *//**
      * 购买方id
      *//*
    ORDERINFO_GMFID(25, "gmfid","购买方id"),*/
 /*   *//**
     * 销货方id
     *//*
    ORDERINFO_XSFID(26, "xhfid","销货方id"),*/
    
    /**
     * 业务类型
     */
    ORDERINFO_YWLX(25, "ywlx","业务类型"),
    ;


    private int key;

    private String value;

    private final String cellName;

    NewExcelEnum(String cellName) {
        this.cellName = cellName;
    }

    NewExcelEnum(int key, String value, String cellName) {
        this.key = key;
        this.value = value;
        this.cellName = cellName;
    }

    public static NewExcelEnum getCodeValue(int key) {

        for (NewExcelEnum item : values()) {
            if (item.getKey() == key) {
                return item;
            }
        }
        return null;
    }
}
