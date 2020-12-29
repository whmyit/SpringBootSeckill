package com.dxhy.order.consumer.modules.invoice.service;

import java.util.List;
import java.util.Map;

/**
 * 描述信息： 纸质发票Service
 *
 * @author 谢元强
 * @date Created on 2018-08-17
 */
public interface C48RedInvoiceService {
    /**
     * 发票信息查询及合并
     *
     * @param invoiceCode
     * @param invoiceNo
     * @param shList
     * @return
     */
    Map<String, Object> mergeSpecialInvoiceAndReversal(String invoiceCode, String invoiceNo, List<String> shList);
}
