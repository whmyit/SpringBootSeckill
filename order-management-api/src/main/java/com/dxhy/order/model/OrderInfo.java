package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


/**
 * 订单业务bean
 *
 * @author zsc
 */
@ToString
@Setter
@Getter
public class OrderInfo implements Serializable {

    /**
     * 订单表主键
     */
    private String id;

    /**
     * 订单处理表id
     */
    private String processId;

    /**
     * 发票请求流水号
     */
    private String fpqqlsh;

    /**
     * 订单号
     */
    private String ddh;

    /**
     * 退货单号
     */
    private String thdh;

    /**
     * 订单类型
     */
    private String ddlx;

    /**
     * 电商平台编码
     */
    private String dsptbm;

    /**
     * 纳税人识别号
     */
    private String nsrsbh;

    /**
     * 纳税人名称
     */
    private String nsrmc;

    /**
     * 纳税人电子档案号
     */
    private String nsrdzdah;

    /**
     * 税务机构代码
     */
    private String swjgDm;

    /**
     * 代开标志
     */
    private String dkbz;

    /**
     * 票样代码
     */
    private String pydm;

    /**
     * 开票项目
     */
    private String kpxm;

    /**
     * 编码表版本号
     */
    private String bbmBbh;

    /**
     * 销货方名称
     */
    private String xhfMc;

    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;

    /**
     * 销货方地址
     */
    private String xhfDz;

    /**
     * 销货方电话
     */
    private String xhfDh;

    /**
     * 销货方银行
     */
    private String xhfYh;

    /**
     * 销货方帐号
     */
    private String xhfZh;

    /**
     * 购货方企业类型
     */
    private String ghfQylx;

    /**
     * 购货方省份
     */
    private String ghfSf;
    
    /**
     * 购货方编码
     */
    private String ghfId;

    /**
     * 购货方名称
     */
    private String ghfMc;

    /**
     * 购货方纳税人识别号
     */
    private String ghfNsrsbh;

    /**
     * 购货方地址
     */
    private String ghfDz;

    /**
     * 购货方电话
     */
    private String ghfDh;

    /**
     * 购货方银行
     */
    private String ghfYh;

    /**
     * 购货方帐号
     */
    private String ghfZh;

    /**
     * 购货方手机
     */
    private String ghfSj;

    /**
     * 购货方邮箱
     */
    private String ghfEmail;

    /**
     * 行业代码
     */
    private String hyDm;

    /**
     * 行业名称
     */
    private String hyMc;

    /**
     * 开票人
     */
    private String kpr;

    /**
     * 收款人
     */
    private String skr;

    /**
     * 复核人
     */
    private String fhr;

    /**
     * 订单日期
     */
    private Date ddrq;

    /**
     * 开票类型
     */
    private String kplx;

    /**
     * 发票种类代码
     */
    private String fpzlDm;

    /**
     * 原发票代码
     */
    private String yfpDm;

    /**
     * 原发票号码
     */
    private String yfpHm;

    /**
     * 冲红原因
     */
    private String chyy;

    /**
     * 特殊冲红标志
     */
    private String tschbz;

    /**
     * 操作代码
     */
    private String czdm;

    /**
     * 清单标志
     */
    private String qdBz;

    /**
     * 清单项目名称
     */
    private String qdXmmc;

    /**
     * 开票合计金额
     */
    private String kphjje;

    /**
     * 合计不含税金额
     */
    private String hjbhsje;

    /**
     * 合计税额
     */
    private String hjse;

    /**
     * 门店号
     */
    private String mdh;

    /**
     * 业务类型
     */
    private String ywlx;
    
    /**
     * 业务类型Id
     */
    private String ywlxId;

    /**
     * 备注
     */
    private String bz;

    /**
     * 开票机号
     */
    private String kpjh;

    /**
     * 开票点
     */
    private String sld;

    /**
     * 受理点名称
     */
    private String sldMc;
    
    /**
     * 提取码
     */
    private String tqm;
    
    /**
     * 订单状态(用于订单拆分)
     */
    private String status;

    /**
     * 备用字段1
     */
    private String byzd1;

    /**
     * 备用字段1
     */
    private String byzd2;

    /**
     * 备用字段1
     */
    private String byzd3;

    /**
     * 备用字段1
     */
    private String byzd4;

    /**
     * 备用字段1
     */
    private String byzd5;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
