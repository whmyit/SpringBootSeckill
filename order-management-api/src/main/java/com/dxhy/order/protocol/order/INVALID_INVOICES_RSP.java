package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 发票作废推送数据状态请求报文
 * @Author xueanna
 * @Date 2019/9/2 18:23
 */
@Setter
@Getter
public class INVALID_INVOICES_RSP implements Serializable {

    private static final long serialVersionUID = 1966191302663469413L;
    /**
    * 作废批次号
    */
    private String ZFPCH;
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    private List<INVALID_INVOICE_INFOS> INVALID_INVOICE_INFOS;
}
