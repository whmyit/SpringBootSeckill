package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceBatchRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票批次请求底层数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:26
 */
public interface InvoiceBatchRequestMapper {
    
    /**
     * 插入发票批次请求表
     *
     * @param record
     * @return
     */
    int insertInvoiceBatchRequest(InvoiceBatchRequest record);
    
    /**
     * 更新发票批次表
     *
     * @param record
     * @param shList
     * @return
     */
    int updateInvoiceBatchRequest(@Param("invoiceBatchRequest") InvoiceBatchRequest record, @Param("shList") List<String> shList);
    
    /**
     * 查询发票批次表
     *
     * @param fpqqpch
     * @param shList
     * @return
     */
    InvoiceBatchRequest selectInvoiceBatchRequestByFpqqpch(@Param(value = "fpqqpch") String fpqqpch, @Param("shList") List<String> shList);
    
}
