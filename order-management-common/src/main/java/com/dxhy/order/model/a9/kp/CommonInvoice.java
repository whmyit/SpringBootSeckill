package com.dxhy.order.model.a9.kp;

import lombok.Getter;
import lombok.Setter;
/**
 * 统一开票接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:40
 */
@Getter
@Setter
public class CommonInvoice {
    
    
    private CommonInvoiceOrder COMMON_INVOICE_ORDER;
    
    private CommonInvoiceDetail[] COMMON_INVOICE_DETAIL;
    
    private CommonInvoiceHead COMMON_INVOICE_HEAD;
    
    
}
