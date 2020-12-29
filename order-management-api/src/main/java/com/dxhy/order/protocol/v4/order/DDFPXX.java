package com.dxhy.order.protocol.v4.order;


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
public class DDFPXX extends DDTXX {
    
    /**
     * 开票机号
     */
    private String KPJH;
    /**
     * 开票点
     */
    private String KPZD;
    /**
     * 发票种类代码
     */
    private String FPLXDM;
    
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 发票代码
     */
    private String FPDM;
    /**
     * 发票号码
     */
    private String FPHM;
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
     * 二维码
     */
    private String EWM;
    /**
     * pdf流
     */
    private String PDFZJL;
    
    /**
     * pdfurl
     */
    private String PDFDZ;
    
    
}
