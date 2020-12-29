package com.dxhy.order.consumer.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 订单发票作废接口-协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/22 18:32
 */
@Setter
@Getter
public class DEPRECATE_INVOICES_RSP implements Serializable {
    private String ZFPCH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private com.dxhy.order.model.c48.zf.DEPRECATE_FAILED_INVOICE[] DEPRECATE_FAILED_INVOICE;
}
