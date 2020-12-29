package com.dxhy.order.consumer.openapi.service;

import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.fangge.*;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_RSP;

import java.util.List;

/**
 * 方格接口调用
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:09
 */
public interface FangGeInterfaceService {
    
    /**
     * 获取待开发票订单数据
     *
     * @param paramMap
     * @return
     */
    FG_ORDER_RESPONSE getInvoices(FG_GET_INVOICE_REQ paramMap);
    
    
    /**
     * 更新方格获取数据订单状态
     *
     * @param param
     * @return
     */
    FG_ORDER_RESPONSE getInvoiceStatus(List<FG_COMMON_ORDER_STATUS> param);
    
    /**
     * 更新开票订单数据
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updateInvoices(List<FG_COMMON_INVOICE_INFO> paramContent);
    
    /**
     * 获取红字申请单上传订单数据
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getUploadRedInvoice(FG_GET_INVOICE_UPLOAD_REQ paramContent);
    
    /**
     * 更新红字申请单上传状态
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getUploadRedInvoiceStatus(FG_COMMON_RED_INVOICE_UPLOAD_STATUS paramContent);
    
    /**
     * 红字申请单数据上传
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updateUploadRedInvoice(RED_INVOICE_FORM_RSP paramContent);
    
    /**
     * 获取红字申请单下载订单数据
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getDownloadRedInvoice(FG_GET_INVOICE_DOWNLOAD_REQ paramContent);
    
    /**
     * 更新红字申请单下载状态
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updateDownloadRedInvoiceStatus(FG_RED_INVOICE_DOWNLOAD_STATUS_REQ paramContent);
    
    /**
     * 获取作废发票数据
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getDeprecateInvoices(FG_GET_INVOICE_ZF_REQ paramContent);
    
    /**
     * 更新发票作废状态
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getDeprecateInvoicesStatus(FG_GET_INVOICE_INVALID_STATUS_REQ paramContent);
    
    /**
     * 发票作废完成返回结果
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updateDeprecateInvoices(FG_INVALID_INVOICE_FINISH_REQ paramContent);
    
    /**
     * 获取待打印的发票数据
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getPrintInvoices(FG_INVOICE_PRING_REQ paramContent);
    
    /**
     * 更新发票打印状态
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE getPrintInvoicesStatus(FG_INVOICE_PRING_STATUS_REQ paramContent);
    
    /**
     * 打印完成更新状态
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updatePrintInvoices(FG_INVOICE_PRING_FINISH_REQ paramContent);
    
    /**
     * 注册税盘
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE registTaxDisk(String paramContent);
    
    /**
     * 税盘信息同步
     *
     * @param paramContent
     * @return
     */
    FG_ORDER_RESPONSE updateTaxDiskInfo(String paramContent);
    
    /**
     * 更新红字申请单下载表
     *
     * @param paramContent
     * @param operatorId
     * @param operatorName
     * @return
     */
    FG_ORDER_RESPONSE updateDownloadRedInvoice(FG_RED_INVOICE_DOWNLOAD_REQ paramContent, String operatorId, String operatorName);
    
    /**
     * 方格生成pdf
     *
     * @param orderInvoiceInfo
     * @param orderInfo
     * @param orderItemInfos
     * @param terminalCode
     * @return
     */
    Result genPdf(OrderInvoiceInfo orderInvoiceInfo, OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos, String terminalCode);
}
