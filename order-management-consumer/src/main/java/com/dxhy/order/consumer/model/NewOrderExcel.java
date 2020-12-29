package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-02-28 15:23:44
 * @Describe
 */
@Setter
@Getter
public class NewOrderExcel {
    /**
     * 订单号
     */
    private String ddh;
    /**
     * 发票种类代码（发票类型）
     */
    private String fpzlDm;
    /**
     * 购货方企业类型
     */
    private String ghf_qylx;
    /**
     * 购货方名称
     */
    private String ghf_mc;
    /**
     * 购货方纳税人识别号
     */
    private String ghf_nsrsbh;
    /**
     * 购货方地址
     */
    private String ghf_dz;
    /**
     * 购货方电话
     */
    private String ghf_dh;
    /**
     * 购货方银行
     */
    private String ghf_yh;
    /**
     * 购货方账号
     */
    private String ghf_zh;
    /**
     * 购货方邮箱
     */
    private String ghf_yx;
    /**
     * 购货方id
     */
    private String ghf_id;
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
     * 是否含税
     */
    private String hsbz;
    /**
     * 商品编码
     */
    private String spbm;
    /**
     * 税率
     */
    private String sl;
    /**
     * 税额
     */
    private String se;
    /**
     * 编码表版本号
     */
    private String bmbbbh;
    /**
     * 优惠政策标识（是否享受优惠政策）
     */
    private String yhzcbs;
    /**
     * 增值税特殊管理（享受税收优惠政策内容）
     */
    private String zzstsgl;
    /**
     * 零税率标识
     */
    private String lslbs;
    /**
     * 自行编码（企业自编码）
     */
    private String zxbm;
    /**
     * 备注
     */
    private String bz;
    /**
     * excel行坐标
     */
    private String rowIndex;
    /**
     * excel列坐标
     */
    private String columnIndex;
    /**
     * 销售方id
     */
    private String xhfid;
    /**
     * 业务类型
     */
    private String ywlx;
    /**
     * 业务类型id
     */
    private String ywlxid;
    /**
     * 是否是成品油
     */
    private boolean isCpy;
}
