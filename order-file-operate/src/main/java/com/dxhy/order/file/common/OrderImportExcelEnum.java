package com.dxhy.order.file.common;

import lombok.Getter;

/**
 * 表格导入枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:23
 */
@Getter
public enum OrderImportExcelEnum{

    /**
     * 订单号
     */
    ORDER_DDH("订单号*", "ddh"),
    /**
     * 开票类型
     */
    ORDER_KPLX("发票类型*","fpzlDm"),
    /**
     * 购货方企业类型
     */
    ORDER_GHFQYLX("抬头类型*","ghf_qylx"),
    /**
     * 购货方名称
     */
    ORDER_GHFMC("购方名称","ghf_mc"),
    /**
     * 购货方纳税人识别号
     */
    ORDER_GHFNSRSBH("购方税号", "ghf_nsrsbh"),
    /**
     * 购货方地址
     */
    ORDER_GHFDZ("购方地址", "ghf_dz"),
    /**
     * 购货方电话
     */
    ORDER_GHFDH("购方电话", "ghf_dh"),
    /**
     * 购货方银行
     */
    ORDER_GHFYH("开户银行", "ghf_yh"),
    /**
     * 购货方账号
     */
    ORDER_GHFZH("银行账号", "ghf_zh"),
    /**
     * 项目名称
     */
    ORDERITEM_XMMC("商品名称*", "xmmc"),
    /**
     * 规格型号
     */
    ORDERITEM_GGXH("规格型号", "ggxh"),
    /**
     * 项目单位
     */
    ORDERITEM_XMDW("单位", "xmdw"),
    /**
     * 项目数量
     */
    ORDERITEM_XMSL("数量", "xmsl"),
    /**
     * 项目单价
     */
    ORDERITEM_XMDJ("单价", "xmdj"),
    /**
     * 项目金额
     */
    ORDERITEM_XMJE("金额*", "xmje"),
    /**
     * 含税标志
     */
    ORDERITEM_HSBZ("含税标志*", "hsbz"),

    // 是否享受税收优惠政策
    // 享受税收优惠政策内容	零税率	企业自编码	备注
    /**
     * 税率
     */
    ORDERITEM_SL("税率", "sl"),
    /**
     * 税额
     */
    ORDERITEM_SE("税额", "se"),
    /**
     * 编码版本号
     */
    ORDERITEM_BMBBBH("编码版本号", "bmbbbh"),
    /**
     * 商品编码(税收分类编码)
     */
    ORDERITEM_SPBM("税收分类编码", "spbm"),
    /**
     * 优惠政策标识（是否享受优惠政策）
     */
    ORDERITEM_YHZCBS("是否享受税收优惠政策*", "yhzcbs"),
    /**
     * 增值税特殊管理（享受税收优惠政策内容）
     */
    ORDERITEM_ZZSTSGL("享受税收优惠政策内容", "zzstsgl"),
    /**
     * 零税率标识
     */
    ORDERITEM_LSLBS("零税率", "lslbs"),
    /**
     * 自行编码（企业自编码）
     */
    ORDERITEM_ZXBM("企业自编码", "zxbm"),
    /**
     * 备注
     */
    ORDER_BZ("备注", "bz"),

    /**
     * 业务类型
     */
    ORDERINFO_YWLX("业务类型", "ywlx"),
    /**
     * 购方id
     */
    ORDERINFO_GFID("购方编码", "ghf_id"),
    /**
     * 邮箱
     */
    ORDERINFO_GFYX("邮箱","ghf_yx"),

    ;


    private final String key;
    
    private final String value;


    OrderImportExcelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public static OrderImportExcelEnum getCodeValue(int key) {

        for (OrderImportExcelEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
