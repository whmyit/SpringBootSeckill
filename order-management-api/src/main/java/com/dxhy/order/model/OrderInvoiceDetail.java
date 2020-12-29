package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-06-04 17:56:16
 * @Describe
 */
@Setter
@Getter
public class OrderInvoiceDetail implements Serializable {
    /**
     * 商品信息
     */
    private List<OrderItemInfo> orderItemList;
    private Date kprq;
    private String ch_bz;
    private String ddh;
    private String ddly;
    private String fpdm;
    private String fphm;
    private String fpqqlsh;
    private String fpzlDm;
    private String ghfNsrsbh;
    private String ghf_mc;
    private String ghf_sj;
    private String hjbhsje;
    private String id;
    private String zfbz;
    private String xhfmc;
    private String xhfNsrsbh;
    private String sldmc;
    private String sld;
    private String qdbz;
    private String pdf_url;
    private String order_info_id;
    private String mdh;
    private String kpse;
    private String kpr;
    private String kplx;
    private String kphjje;
    private String ghfDz;
    private String ghfDh;
    private String ghfYh;
    private String ghfZh;
    private String ghfEmail;
    private String bz;
    private String bmbbbh;
    private String orderProcessId;
    private String pushStatus;
    private String xhfDz;
    private String xhfDh;
    private String xhfZh;
    private String xhfYh;
    private String ywlx;
    private String kpxm;
    private String ddzt;
    private String updateTime;
    private String kpzt;
    private String checkTime;
    private String createTime;
}
