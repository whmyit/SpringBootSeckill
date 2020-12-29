package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票头信息
 * todo V1和V2使用,后期不改更新维护
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@Deprecated
public class COMMON_INVOICE_HEAD implements Serializable {
    
    private static final long serialVersionUID = 3254089553815062635L;
    /**
     * 发票请求唯一流水号
     */
    private String FPQQLSH;
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 纳税人名称
     */
    private String NSRMC;
    /**
     * 开票类型
     */
    private String KPLX;
    /**
     * 编码表版本号
     */
    private String BMB_BBH;
    /**
     * 销售方纳税人识别号
     */
    private String XSF_NSRSBH;
    /**
     * 销售方名称
     */
    private String XSF_MC;
    /**
     * 销售方地址
     */
    private String XSF_DZ;
    /**
     * 销售方电话
     */
    private String XSF_DH;
    /**
     * 销售方银行账号
     */
    private String XSF_YHZH;
    /**
     * 购买方纳税人识别号
     */
    private String GMF_NSRSBH;
    /**
     * 购买方名称
     */
    private String GMF_MC;
    /**
     * 购买方地址
     */
    private String GMF_DZ;
    /**
     * 购买方企业类型
     */
    private String GMF_QYLX;
    /**
     * 购买方省份
     */
    private String GMF_SF;
    /**
     * 购买方固定电话
     */
    private String GMF_GDDH;
    /**
     * 购买方手机
     */
    private String GMF_SJ;
    /**
     * 购买方微信
     */
    private String GMF_WX;
    /**
     * 购买方邮箱
     */
    private String GMF_EMAIL;
    /**
     * 购买方银行账号
     */
    private String GMF_YHZH;
    /**
     * 开票人
     */
    private String KPR;
    /**
     * 收款人
     */
    private String SKR;
    /**
     * 复核人
     */
    private String FHR;
    /**
     * 原发票代码
     */
    private String YFP_DM;
    /**
     * 原发票号码
     */
    private String YFP_HM;
    /**
     * 清单标志
     */
    private String QD_BZ;
    /**
     * 清单发票项目名称
     */
    private String QDXMMC;
    /**
     * 价税合计
     */
    private String JSHJ;
    /**
     * 合计金额
     */
    private String HJJE;
    /**
     * 合计税额
     */
    private String HJSE;
    /**
     * 备注
     */
    private String BZ;
    /**
     * 票样代码
     */
    private String PYDM;
    /**
     * 冲红原因
     */
    private String CHYY;
    /**
     * 特殊冲红标志
     */
    private String TSCHBZ;
    /**
     * 开票机号
     */
    private String KPJH;
    /**
     * 受理点
     */
    private String SLD;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
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
    /**
     * 备用字段4
     */
    private String BYZD4;
    /**
     * 备用字段5
     */
    private String BYZD5;
    
}
