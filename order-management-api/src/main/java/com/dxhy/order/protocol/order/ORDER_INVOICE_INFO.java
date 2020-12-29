package com.dxhy.order.protocol.order;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单发票全数据协议beanV3
 *
 * @author ZSC-DXHY
 */
@ToString
@Setter
@Getter
public class ORDER_INVOICE_INFO extends COMMON_ORDER_HEAD {
    
    /**
     * 开票机号
     */
    private String KPJH;
    /**
     * 开票点
     */
    private String SLD;
    /**
     * 发票种类代码
     */
    private String FPZLDM;
    
    /**
     * 机器编号
     */
    private String JQBH;
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
    private String KPRQ;
    /**
     * 校验码
     */
    private String JYM;
    /**
     * 防伪码
     */
    private String FWM;
    /**
     * pdf流
     */
    private String PDF_FILE;
    /**
     * pdfUrl
     */
    private String PDF_URL;
    
}
