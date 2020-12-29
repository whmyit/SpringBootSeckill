package com.dxhy.order.dao;

import com.dxhy.order.model.SpecialInvoiceReversalItem;

import java.util.List;

/**
 * 申请单明细信息数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:43
 */
public interface SpecialInvoiceReversalItemMapper {
    
    /**
     * 删除申请单明细信息
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);
    
    /**
     * 新增申请单数据
     *
     * @param record
     * @return
     */
    int insert(SpecialInvoiceReversalItem record);
    
    /**
     * 新增申请单明细
     *
     * @param record
     * @return
     */
    int insertSelective(SpecialInvoiceReversalItem record);
    
    /**
     * 查询申请单明细
     *
     * @param id
     * @return
     */
    SpecialInvoiceReversalItem selectByPrimaryKey(String id);
    
    /**
     * 查询申请单明细
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SpecialInvoiceReversalItem record);
    
    /**
     * 更新申请单信息
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(SpecialInvoiceReversalItem record);
    
    /**
     * 查询申请单明细信息
     *
     * @param id
     * @return
     */
    List<SpecialInvoiceReversalItem> selectItemListBySpecialInvoiceReversalId(String id);
    
    /**
     * 删除申请单明细
     *
     * @param specialInvoiceReversalId
     * @return
     */
    int deleteSpecialInvoiceReversalItems(String specialInvoiceReversalId);
    
    
}
