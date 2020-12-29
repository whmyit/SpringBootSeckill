package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceTypeCodeExt;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票种类数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:31
 */
public interface InvoiceTypeCodeExtMapper {
    
    /**
     * 新增发票种类数据
     *
     * @param record
     * @return
     */
    int insertInvoiceTypeCodeExt(InvoiceTypeCodeExt record);
    
    /**
     * 查询发票种类数据
     *
     * @param qrcodeId
     * @param shList
     * @return
     */
    List<InvoiceTypeCodeExt> selectByQrcodeId(@Param("qrcodeId") String qrcodeId, @Param("shList") List<String> shList);
    
    /**
     * 删除发票种类数据
     *
     * @param qrId
     * @param shList
     * @return
     */
    int deleteByQrId(@Param(value = "qrId") String qrId, @Param("shList") List<String> shList);
}
