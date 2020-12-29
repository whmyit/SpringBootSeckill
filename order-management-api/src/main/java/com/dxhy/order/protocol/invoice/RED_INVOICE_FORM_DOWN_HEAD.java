package com.dxhy.order.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 专票冲红 红字信息表 下载信息 协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:26
 */
@Setter
@Getter
public class RED_INVOICE_FORM_DOWN_HEAD implements Serializable {
    
    /**
     * 申请单号
     */
    private String SQDH;
    
    /**
     * 信息表编号
     */
    private String XXBBH;
    
    /**
     * 状态代码
     */
    private String STATUS_CODE;
    
    /**
     * 状态信息
     */
    private String STATUS_MESSAGE;
    
    /**
     * 原蓝字发票代码
     */
    private String YFP_DM;
    
    /**
     * 原蓝字发票号码
     */
    private String YFP_HM;
    
    /**
     * 发票类型
     */
    private String FPLX;
    
    /**
     * 发票类别
     */
    private String FPLB;
    
    /**
     * 多税率标志:0一票一税率，1一票多税率
     */
    private String DSLBZ;
    
    /**
     * 填开时间
     */
    private String TKSJ;
    
    /**
     * 销售方纳税人识别号
     */
    private String XSF_NSRSBH;
    
    /**
     * 销售方纳税人名称
     */
    private String XSF_MC;
    
    /**
     * 购买方纳税人识别号
     */
    private String GMF_NSRSBH;
    
    /**
     * 购买方纳税人名称
     */
    private String GMF_MC;
    
    /**
     * 合计金额(带负号,不含税)
     */
    private String HJJE;
    
    /**
     * 合计税额(带负号)
     */
    private String HJSE;
    
    /**
     * 十位数字表示的申请说明
     */
    private String SQSM;
    
    /**
     * 商品编码版本号
     */
    private String BMB_BBH;
    
    /**
     * 营业税标志
     */
    private String YYSBZ;
}
