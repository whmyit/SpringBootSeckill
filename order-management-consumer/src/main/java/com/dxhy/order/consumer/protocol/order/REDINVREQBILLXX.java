package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * todo V1或者是V2版本,后续不再更新迭代.
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/5/15 16:53
 */
@Getter
@Setter
@Deprecated
public class REDINVREQBILLXX implements Serializable {
    
    private String SQDH;
    private String XXBBH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private String YFP_DM;
    private String YFP_HM;
    private String FPLX;
    private String FPLB;
    private String DSLBZ;
    private String TKSJ;
    private String XSF_NSRSBH;
    private String XSF_MC;
    private String GMF_NSRSBH;
    private String GMF_MC;
    private String HJJE;
    private String HJSE;
    private String SQSM;
    private String BMB_BBH;
    private String YYSBZ;
    private COMMON_INVOICE_DETAIL[] COMMONINVDETAILS;
}
