package com.dxhy.order.consumer.modules.invoice.constant;

import lombok.Getter;


/**
 * 红字申请单导入枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:20
 */
@Getter
public enum SpecialInvoiceImportExcelEnum {

    /**
     * 订单号
     */
    ORDER_DDH("申请单唯一编号*", "sqdwybh"),
    /**
     * 开票类型
     */
    ORDER_KPLX("成品油专用发票类型","cypzyfplx"),
    /**
     * 购货方企业类型
     */
    ORDER_GHFQYLX("原发票代码*","yfpDm"),
    /**
     * 购货方名称
     */
    ORDER_GHFMC("原发票号码*","yfpHm"),
    /**
     * 购货方纳税人识别号
     */
    ORDER_GHFNSRSBH("原蓝票日期*", "ylprq"),
    /**
     * 购货方地址
     */
    ORDER_GHFDZ("销方名称*", "xhfMc"),
    /**
     * 购货方电话
     */
    ORDER_GHFDH("销方税号*", "xhfSh"),
    /**
     * 购货方银行
     */
    ORDER_GHFYH("商品名称*", "spMc"),
    /**
     * 购货方账号
     */
    ORDER_GHFZH("税收分类编码", "spBm"),
    /**
     * 规格型号
     */
    ORDERITEM_GGXH("税率", "sLv"),
    /**
     * 项目单位
     */
    ORDERITEM_XMDW("规格型号", "ggXh"),
    /**
     * 项目数量
     */
    ORDERITEM_XMSL("计量单位", "xmDw"),
    /**
     * 项目单价
     */
    ORDERITEM_XMDJ("单价", "xmDj"),
    /**
     * 项目金额
     */
    ORDERITEM_XMJE("数量", "xmSl"),
    /**
     * 含税标志
     */
    ORDERITEM_HSBZ("金额*", "xmje"),

    // 是否享受税收优惠政策
    // 享受税收优惠政策内容	零税率	企业自编码	备注
    /**
     * 税率
     */
    ORDERITEM_SL("税额*", "xmSe"),
    /**
     * 税额
     */
    ORDERITEM_SE("享受优惠政策", "yhzcbs"),
    /**
     * 编码版本号
     */
    ORDERITEM_BMBBBH("优惠政策类型", "zzstsgl"),

    /**
     * 购方名称
     */
    ORDERITEM_GFMC("购方名称*", "ghfMc"),

    /**
     * 购方税号
     */
    ORDERITEM_GFSH("购方税号*","ghfSh"),


    ;


    private final String key;
    
    private final String value;


    SpecialInvoiceImportExcelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public static SpecialInvoiceImportExcelEnum getCodeValue(int key) {

        for (SpecialInvoiceImportExcelEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
