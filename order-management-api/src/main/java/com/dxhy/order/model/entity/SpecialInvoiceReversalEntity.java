package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 专票申请单业务主体bean
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class SpecialInvoiceReversalEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String id;

    private String type;

    private String sqdscqqpch;

    private String sqdscqqlsh;

    private String sqdh;

    private String sqsm;

    private String yysbz;

    private String xxblx;

    private String yfpDm;

    private String yfpHm;

    private Date yfpKprq;

    private String invoiceType;

    private String fpzlDm;

    private Date tksj;

    private String nsrsbh;

    private String xhfMc;

    private String xhfNsrsbh;

    private String xhfDz;

    private String xhfDh;

    private String xhfYh;

    private String xhfZh;

    private String ghfMc;

    private String ghfNsrsbh;

    private String ghfDz;

    private String ghfDh;

    private String ghfYh;

    private String ghfZh;

    private String ghfqylx;

    private String hjbhsje;

    private String hjse;

    private String kphjje;

    private String dslbz;

    private String xxbbh;

    private String bmbbbh;

    private String kpzt;

    private String fpdm;

    private String fphm;

    private String sld;

    private String sldMc;

    private String fjh;

    private String chyy;

    private String kpr;

    private String fhr;

    private String skr;

    private String agentName;

    private String statusCode;

    private String statusMessage;

    private String creatorId;

    private String creatorName;

    private String editorId;

    private String editorName;

    private Date createTime;

    private Date updateTime;

    private String dataStatus;

    private String useOldInvoiceData;
    
    private String scfgStatus;
    
    private String xzfgStatus;
    
    /**
     * 前端判断使用
     */
    private String zfbz;
    
    private String jqbh;
    
    /**
     * 原蓝票合计不含税金额
     */
    private String yfphjbhsje;
    /**
     * 原蓝票税额
     */
    private String yfphjse;
    
    private String excluteStatus;
    
}
