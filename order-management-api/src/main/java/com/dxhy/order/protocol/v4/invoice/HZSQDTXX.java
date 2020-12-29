package com.dxhy.order.protocol.v4.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字信息表主体信息协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:26
 */
@Setter
@Getter
public class HZSQDTXX implements Serializable {
    
    /**
     * 申请表上传请求流水号
     */
    private String SQBSCQQLSH;
    
    /**
     * 信息表类型
     */
    private String XXBLX;
    
    /**
     * 原蓝字发票代码
     */
    private String YFPDM;
    
    /**
     * 原蓝字发票号码
     */
    private String YFPHM;
    
    /**
     * 营业税标志
     */
    private String YYSBZ;
    
    /**
     * 原蓝字发票开票日期
     */
    private String YFPKPRQ;
    
    /**
     * 填开时间
     */
    private String TKSJ;
    
    /**
     * 销售方纳税人识别
     */
    private String XHFSBH;
    
    /**
     * 销售方纳税人名称
     */
    private String XHFMC;
    
    /**
     * 购买方纳税人识别号
     */
    private String GMFSBH;
    
    /**
     * 购买方纳税人名称
     */
    private String GMFMC;
    
    /**
     * 合计金额(带负号,不含税)
     */
    private String HJJE;
    
    /**
     * 合计税额(带负号)
     */
    private String HJSE;
    
    /**
     * 申请说明
     */
    private String SQSM;
    
    /**
     * 商品编码版本号
     */
    private String BMBBBH;
    
    /**
     * 扩展字段1
     */
    private String KZZD1;
    
    /**
     * 扩展字段2
     */
    private String KZZD2;
}
