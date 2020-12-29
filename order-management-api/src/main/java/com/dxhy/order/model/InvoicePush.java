package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票推送
 *
 * @Author dxy
 * @Date 2018/8/15 14:41
 */
@Setter
@Getter
public class InvoicePush implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 发票请求批次号
     */
    private String FPQQPCH;
    /**
     * 发票请求唯一流水号
     */
    private String FPQQLSH;
    /**
     * 开票唯一流水号
     */
    private String KPLSH;
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 订单号
     */
    private String DDH;
    /**
     * 校验码
     */
    private String JYM;
    /**
     * 防伪码
     */
    private String FWM;
    /**
     * 二维码
     */
    private String EWM;
    /**
     * 发票代码
     */
    private String FP_DM;
    /**
     * 发票号码
     */
    private String FP_HM;
    /**
     * 开票日期
     */
    private Date KPRQ;
    /**
     * 发票类型
     */
    private String FPLX;
    /**
     * 发票类别
     */
    private String FPLB;
    /**
     * 合计金额不含税
     */
    private Double HJBHSJE;
    /**
     * 合计税额
     */
    private Double KPHJSE;
    /**
     * 状态代码
     */
    private String STATUSCODE;
    /**
     * 状态描述
     */
    private String STATUSMSG;
    /**
     * PDF_URL云服务key
     */
    private String PDF_URL;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 受理点ID
     */
    private String SLDID;
    
    /**
     * 受理点名称
     */
    private String SLDMC;
    
    /**
     * 分机号
     */
    private String FJH;

    /**
     * 上传下载表code
     */
    private String SQBSCQQLSH;
    
}
