package com.dxhy.order.consumer.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开具发票结果获取-响应协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/23 10:01
 */
@Setter
@Getter
@Deprecated
public class GETALLOCATED_INVOICES_RSP implements Serializable {
    private String FPQQPCH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private RESPONSE_COMMON_INVOICE[] RESPONSE_COMMON_INVOICE;
}
