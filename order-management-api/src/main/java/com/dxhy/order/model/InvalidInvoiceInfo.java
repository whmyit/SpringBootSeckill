package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 发票作废数据库实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class InvalidInvoiceInfo implements Serializable {

    private String id;
    /**
     * 作废批次号
     */
    private String zfpch;

    private String fpdm;

    private String fphm;

    private String sld;

    /**
     * 作废原因
     */
    private String zfyy;

    private String fplx;

    private String zfBz;

    private Date zfsj;

    private Date updateTime;

    private Date createTime;

    private String xhfNsrsbh;
    
    /**
     * 作废类型   0:空白发票作废    1:正数发票作废  3:负数发票作废
     */
    private String zflx;
    
    private String xhfmc;
    
    private String zfr;
    
    private String fgStatus;
    
}
