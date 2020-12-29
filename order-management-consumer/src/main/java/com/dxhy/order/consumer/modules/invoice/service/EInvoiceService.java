package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.consumer.model.FileDownLoad;

import java.util.List;

/**
 * 描述信息：电子发票信息service
 *
 * @author 谢元强
 * @date Created on 2018-07-23
 */
public interface EInvoiceService {
    
    
    /**
     * 打印电票pdf
     *
     * @param invoiceCode
     * @param invoiceNo
     * @param shList
     * @return
     */
    List<byte[]> printInvoice(String[] invoiceCode, String[] invoiceNo, String[] shList);
    
    /**
     * 下载 根据发票信息查看发票版式文件信息 并转化为file 数组
     *
     * @param invoiceCode
     * @param invoiceNo
     * @param nsrsbh
     * @return
     */
    List<FileDownLoad> queryInvoicePdfPath(String invoiceCode, String invoiceNo, String nsrsbh);
    
}
