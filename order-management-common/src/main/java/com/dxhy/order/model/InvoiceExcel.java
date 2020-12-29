package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ：杨士勇
 * @ClassName ：InvoiceExcel
 * @Description ：excel导出对象
 * @date ：2018年12月8日 下午4:46:11
 */
@Setter
@Getter
public class InvoiceExcel implements Serializable {
    /**
     * 申请单号
     * 批次表获取
     */
    private String sqdh;
    /**
     * 订单号
     */
    private String ddh;
    /**
     * 发票代码
     * 通过发票表获取
     */
    private String fpdm;
    /**
     * 发票号码
     * 通过发票表获取
     */
    private String fphm;
    /**
     * 销售方名称
     */
    private String xhfMc;
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 购买方名称
     */
    private String ghfMc;
    /**
     * 购货方纳税人识别号
     */
    private String ghfNsrsbh;
    /**
     * 银行账号
     */
    private String ghfYh;
    /**
     * 地址电话
     */
    private String ghfDz;
    /**
     * 发票类型 电子发票 专票 纸票
     * 需要转换
     */
    private String fpzlDm;
    /**
     * 开票日期
     * 通过发票表获取
     */
    private String kprq;
    /**
     * 价税合计金额
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
     * 开票人
     * 通过发票表获取
     */
    private String kpr;
    /**
     * 开票类型 标识蓝票和红票
     * 需要转换
     */
    private String kplx;
    /**
     * 发票备注
     */
    private String bz;
    /**
     * 作废标志 是否作废
     * 发票表获取 需要转换
     */
    private String zfbz;
    /**
     * 冲红标志 是已冲红
     * 发票表获取需要转换
     */
    private String chbz;
    /**
     * 清单标志 表示发票明细是否有八条明细
     * 发票表获取 需要转换
     */
    private String qdbz;
    /**
     * 订单类型 分为个人和企销
     * 需要转换  批次表获取需要转换
     */
    private String ddqylx;
    /**
     * 第一行商品名称 开票项目
     */
    private String kpxm;
}
