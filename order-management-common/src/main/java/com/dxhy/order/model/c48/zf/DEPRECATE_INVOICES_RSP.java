package com.dxhy.order.model.c48.zf;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 作废返回数据C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:25
 */
@Setter
@Getter
public class DEPRECATE_INVOICES_RSP implements Serializable {
    private String ZFPCH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private DEPRECATE_FAILED_INVOICE[] deprecate_failed_invoice;
}
