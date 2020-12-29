package com.dxhy.order.api;

import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 接收发票接口
 *
 * @author ZSC-DXHY
 */
public interface InvoiceDataService {
    
    /**
     * 接收发票
     *
     * @param invoicePush
     * @return
     */
    R receiveInvoice(InvoicePush invoicePush);
    
    /**
     * 发票手动回推
     *
     * @param orderInfoIds
     * @return
     */
    R manualPushInvoice(List<Map> orderInfoIds);
    
}
