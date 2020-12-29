package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 红字申请单下载
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class SpecialInvoiceReversalDownloadEntity implements Serializable {
    
    private String id;
    private String sqbxzqqpch;
    private String nsrsbh;
    private String sldid;
    private String kpjh;
    private String fpzldm;
    private String tkrqQ;
    private String tkrqZ;
    private String gmfNsrsbh;
    private String xsfNsrsbh;
    private String xxbbh;
    private String xxbfw;
    private String pageno;
    private String pagesize;
    private String downStatus;
    private Date createTime;
    
    
}
