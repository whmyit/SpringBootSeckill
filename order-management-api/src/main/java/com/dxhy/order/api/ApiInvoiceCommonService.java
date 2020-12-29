package com.dxhy.order.api;

import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;

import java.util.List;

/**
 * 订单发票通用接口
 *
 * @author ZSC-DXHY
 */
public interface ApiInvoiceCommonService {
    
    /**
     * 调用主键生成器生成主键 generate primary key
     *
     * @return
     */
    String getGenerateShotKey();
    
    /**
     * 根据发票代码号码查询发票信息
     *
     * @param yfpDm
     * @param yfpHm
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectByYfp(String yfpDm, String yfpHm, List<String> shList);
    
    /**
     * 通过订单id查询订单表
     *
     * @param orderInfoId
     * @param shList
     * @return
     */
    OrderInfo selectByOrderInvoiceId(String orderInfoId, List<String> shList);
    
    /**
     * 通过订单id得到订单明细信息
     *
     * @param orderId
     * @param shList
     * @return
     */
    List<OrderItemInfo> selectOrderItemByOrderInfoId(String orderId, List<String> shList);
    
    /**
     * 通过批次号查询所有批次扩展表
     *
     * @param fpqqpch
     * @param shList
     * @return
     */
    List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqpch(String fpqqpch, List<String> shList);
    
    /**
     * selectByFpqqlsh
     *
     * @param kplsh
     * @param shList
     * @return
     */
    InvoiceBatchRequestItem selectInvoiceBatchItemByKplsh(String kplsh, List<String> shList);
    
    /**
     * 根据发票请求流水号获取对应数据的批次号
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqlsh(String fpqqlsh, List<String> shList);
    
    /**
     * 根据开票流水号更新发票状态
     *
     * @param orderInvoiceInfo
     * @param shList
     * @return
     */
    int updateInvoiceStatusByKplsh(OrderInvoiceInfo orderInvoiceInfo, List<String> shList);
    
    /**
     * 根据开票批次号批量更新发票状态
     *
     * @param id
     * @param statusCode
     * @param statusMessage
     * @param shList
     * @return
     */
    int updateBatchStatusById(String id, String statusCode, String statusMessage, List<String> shList);
    
    /**
     * 根据发票请求批次号获取发票批次表数据
     *
     * @param fpqqpch
     * @param shList
     * @return
     */
    InvoiceBatchRequest selectInvoiceBatchRequestByFpqqpch(String fpqqpch, List<String> shList);
    
    /**
     * 根据开票流水号获取发票数据
     *
     * @param kplsh
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectInvoiceInfoByKplsh(String kplsh, List<String> shList);
    
    /**
     * 保存订单信息
     *
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param insertOrderProcessInfoExtList
     * @param updateOrderProcessInfo
     * @param qrcodeInfoList
     * @param originOrderList
     * @param shList
     */
    void saveData(List<OrderInfo> insertOrder, List<List<OrderItemInfo>> insertOrderItem,
                  List<OrderProcessInfo> insertProcessInfo, List<OrderProcessInfoExt> insertOrderProcessInfoExtList,
                  List<OrderProcessInfo> updateOrderProcessInfo, List<OrderQrcodeExtendInfo> qrcodeInfoList, List<OrderOriginExtendInfo> originOrderList, List<String> shList);
    
    /**
     * 订单数据保存数据库
     *
     * @param transitionBatchRequest
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param insertBatchItem
     * @param insertInvoiceInfo
     * @param obr
     * @param updateProcessInfo
     * @param updateInvoiceInfo
     * @param originExtendList
     * @param xhfNsrsbh
     */
    void saveData(List<InvoiceBatchRequest> transitionBatchRequest, List<OrderInfo> insertOrder,
                  List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,
                  List<List<InvoiceBatchRequestItem>> insertBatchItem, List<OrderInvoiceInfo> insertInvoiceInfo,
                  OrderBatchRequest obr, List<OrderProcessInfo> updateProcessInfo, List<OrderInvoiceInfo> updateInvoiceInfo,
                  List<OrderOriginExtendInfo> originExtendList, List<String> xhfNsrsbh);
    
    /**
     * 历史订单数据保存到数据库
     *
     * @param transitionBatchRequest
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param insertBatchItem
     * @param insertInvoiceInfo
     * @param obr
     * @param updateProcessInfo
     * @param updateInvoiceInfo
     * @param originExtendList
     * @param pdfFile
     * @param xhfNsrsbh
     */
    void saveHistoryData(List<InvoiceBatchRequest> transitionBatchRequest, List<OrderInfo> insertOrder,
                         List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,
                         List<List<InvoiceBatchRequestItem>> insertBatchItem, List<OrderInvoiceInfo> insertInvoiceInfo,
                         OrderBatchRequest obr, List<OrderProcessInfo> updateProcessInfo, List<OrderInvoiceInfo> updateInvoiceInfo,
                         List<OrderOriginExtendInfo> originExtendList, String pdfFile, List<String> xhfNsrsbh);
    
    /**
     * 发票信息保存
     *
     * @param updateProcessInfo
     * @param insertOrderInvoiceInfo
     * @param insertInvoiceBatchRequest
     * @param insertInvoiceBatchRequestItem
     * @param updateSpecialInvoiceList
     * @param xhfNsrsbh
     * @throws OrderReceiveException
     */
    void invoiceRequestData(List<OrderProcessInfo> updateProcessInfo, List<OrderInvoiceInfo> insertOrderInvoiceInfo,
                            List<InvoiceBatchRequest> insertInvoiceBatchRequest, List<List<InvoiceBatchRequestItem>> insertInvoiceBatchRequestItem,
                            List<SpecialInvoiceReversalEntity> updateSpecialInvoiceList, List<String> xhfNsrsbh) throws OrderReceiveException;
    
    /**
     * 保存合并后的订单信息
     *
     * @param commonOrder
     * @return
     */
    boolean saveMergeOrderInfo(CommonOrderInfo commonOrder);
    
    /**
     * 保存拆分后的订单信息
     *
     * @param resultList
     * @return
     * @throws OrderReceiveException
     */
    List<CommonOrderInfo> saveOrderSplitInfo(List<CommonOrderInfo> resultList) throws OrderReceiveException;
    
    /**
     * 保存订单信息
     *
     * @param insertOrderInfoList
     * @param insertOrderItemList
     * @param insertOrderProcessInfoList
     * @param orderOriginList
     */
    void savePageData(List<OrderInfo> insertOrderInfoList,
                      List<List<OrderItemInfo>> insertOrderItemList, List<OrderProcessInfo> insertOrderProcessInfoList,
                      List<OrderOriginExtendInfo> orderOriginList);
    
    /**
     * 保存动态码信息
     *
     * @param transitionBatchRequest
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param insertBatchItem
     * @param insertInvoiceInfo
     * @param orderBatchRequest
     * @param updateProcessInfo
     * @param updateInvoiceInfo
     * @param orderOriginList
     * @param xhfNsrsbh
     */
    void saveDynamicQrCodeInfo(List<InvoiceBatchRequest> transitionBatchRequest, List<OrderInfo> insertOrder,
                               List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,
                               List<List<InvoiceBatchRequestItem>> insertBatchItem, List<OrderInvoiceInfo> insertInvoiceInfo,
                               OrderBatchRequest orderBatchRequest, List<OrderProcessInfo> updateProcessInfo,
                               List<OrderInvoiceInfo> updateInvoiceInfo, List<OrderOriginExtendInfo> orderOriginList, List<String> xhfNsrsbh);
    
    /**
     * 不同税号数据保存统一事务
     *
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param originOrderList
     */
    void saveDifShData(List<OrderInfo> insertOrder, List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo, List<OrderOriginExtendInfo> originOrderList);
    
    /**
     * 异常订单开票后更新发票表 处理表状态
     *
     * @param updateOrderInvoiceInfos
     * @param updateProcessInfos
     * @param updateSpecialInvoices
     * @param shList
     */
    void batchInvoiceUpdate(List<OrderInvoiceInfo> updateOrderInvoiceInfos, List<OrderProcessInfo> updateProcessInfos, List<SpecialInvoiceReversalEntity> updateSpecialInvoices, List<String> shList);
}
