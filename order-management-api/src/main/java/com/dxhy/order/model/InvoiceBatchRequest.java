package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 批量开票数据库实体类
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class InvoiceBatchRequest implements Serializable {

    private String id;

    private String fpqqpch;

    private String xhfNsrsbh;

    private String sldid;

    private String kpjh;

    private String kplx;

    private String fplb;

    private String status;

    private String message;

    private String kzzd;

    private Date createTime;

    private Date updateTime;

}
