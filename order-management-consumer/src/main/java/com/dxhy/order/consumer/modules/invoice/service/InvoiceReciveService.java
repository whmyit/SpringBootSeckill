package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.model.InvoicePush;

/**
 * 余票预警service
 *
 * @author yuchenguang
 * @ClassName: YpWarningService
 * @Description: 余票预警
 * @date 2018年9月12日 下午12:10:00
 */
public interface InvoiceReciveService {
    
    /**
     * 推动异常订单预警信息给大B
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/8
     * @param invoicePush 发票推送bean
     */
    void pushExceptionMessageToItax(InvoicePush invoicePush);
}
