package com.dxhy.order.model.a9.kp;

import lombok.Getter;
import lombok.Setter;

/**
 * 新税控查询返回对象
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-21 8:51
 */
@Getter
@Setter
public class InvoiceQueryResponseNewTax {

    private String ZTDM;

    private String ZTXX;

    private CommonInvoiceStatusNewTax COMMON_INVOICESTATUS;
}
