package com.dxhy.order.model.a9.kp;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 调用底层开票接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-13 19:58
 */
@SuppressWarnings("ALL")
@Getter
@Setter
public class AllocateInvoicesReq extends RequestBaseBean {
    
    private CommonInvoicesBatch COMMON_INVOICES_BATCH;
    
    private CommonInvoice[] COMMON_INVOICE;
    
}
