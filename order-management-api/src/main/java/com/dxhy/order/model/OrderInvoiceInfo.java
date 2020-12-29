package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单与发票对应关系业务bean
 *
 * @author zsc
 */
@Setter
@Getter
public class OrderInvoiceInfo implements Serializable {

    /**
     * 订单发票表主键
     */
    private String id;

    /**
     * 订单表id
     */
    private String orderInfoId;

    /**
     * 订单处理表id
     */
    private String orderProcessInfoId;

    /**
     * 发票请求流水号
     */
    private String fpqqlsh;

    /**
     * 开票流水号
     */
    private String kplsh;

    /**
     * 订单号
     */
    private String ddh;

    /**
     * 开票合计金额
     */
    private String kphjje;

    /**
     * 开票类型
     */
    private String kplx;

    /**
     * 开票日期
     */
    private Date kprq;

    /**
     * 发票代码
     */
    private String fpdm;

    /**
     * 发票号码
     */
    private String fphm;

    /**
     * 校验码
     */
    private String jym;

    /**
     * 失败原因
     */
    private String sbyy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 门店编号
     */
    private String mdh;

    /**
     * 购货方名称
     */
    private String ghfMc;

    /**
     * 购货方手机
     */
    private String ghfSj;

    /**
     * 合计不含税金额
     */
    private String hjbhsje;

    /**
     * 开票税额
     */
    private String kpse;

    /**
     * 冲红标志
     */
    private String chBz;
    /**
     * 可冲红金额
     * 2019-04-01添加
     */
    private String sykchje;
    /**
     * 冲红时间
     */
    private Date chsj;

    /**
     * 发票种类代码
     */
    private String fpzlDm;

    /**
     * 开票状态
     */
    private String kpzt;

    /**
     * 开票人
     */
    private String kpr;

    /**
     * 防伪码
     */
    private String fwm;

    /**
     * 二维码
     */
    private String ewm;

    /**
     * 机器编号
     */
    private String jqbh;

    /**
     * pdf——url
     */
    private String pdfUrl;

    /**
     * 认证状态
     */
    private String rzZt;
    /**
     * 受理点
     */
    private String sld;
    /**
     * 作废标志
     */
    private String zfBz;
    /**
     * 冲红原因
     */
    private String chyy;
    /**
     * 作废原因
     */
    private String zfyy;
    /**
     * 作废时间
     */
    private Date zfsj;
    /**
     * 打印状态
     */
    private String dyzt;
    /**
     * 销货方纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 销货方纳税人名称
     */
    private String xhfMc;
    /**
     * 推送状态
     */
    private String pushStatus;
    /**
     * 受理点名称
     */
    private String sldMc;
    /**
     * 清单标志
     */
    private String qdbz;
    /**
     * 红字信息表编号
     */
    private String hzxxbbh;
    /**
     * 邮件推送状态
     */
    private String emailPushStatus;
    /**
     * 短信发送状态
     */
    private String shortMsgPushStatus;
    /**
     * 分机号
     */
    private String fjh;
    
    /**
     * mongoDb库Id
     */
    private String mongodbId;
}
