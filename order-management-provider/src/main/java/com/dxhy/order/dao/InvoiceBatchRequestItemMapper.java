package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceBatchRequestItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票请求底层批次明细数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:24
 */
public interface InvoiceBatchRequestItemMapper {
    
    /**
     * 根据开票流水号更新批次明细数据
     *
     * @param invoiceBatchRequestItem
     * @param shList
     * @return
     */
    int updateInvoiceBatchItemByKplsh(@Param("invoiceBatchItem") InvoiceBatchRequestItem invoiceBatchRequestItem, @Param("shList") List<String> shList);
    
    /**
     * 根据批次号查询发票批次信息
     *
     * @param fpqqpch
     * @param shList
     * @return
     */
    List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqpch(@Param(value = "fpqqpch") String fpqqpch, @Param("shList") List<String> shList);
    
    /**
     * 根据开票流水号获取发票请求明细数据,
     *
     * @param kplsh
     * @param shList
     * @return
     */
    InvoiceBatchRequestItem selectInvoiceBatchItemByKplsh(@Param(value = "kplsh") String kplsh, @Param("shList") List<String> shList);
    
    /**
     * 根据发票请求流水号获取发票请求明细数据,
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqlsh(@Param(value = "fpqqlsh") String fpqqlsh, @Param("shList") List<String> shList);
    
    /**
     * inset 批次关系表
     *
     * @param insertBatchItem
     * @return
     */
    int insertInvoiceBatchItemBatch(@Param("list") List<InvoiceBatchRequestItem> insertBatchItem);
}
