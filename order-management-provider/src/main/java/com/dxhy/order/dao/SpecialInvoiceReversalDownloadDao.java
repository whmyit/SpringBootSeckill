package com.dxhy.order.dao;

import com.dxhy.order.model.entity.SpecialInvoiceReversalDownloadEntity;
import org.apache.ibatis.annotations.Param;
/**
 * 申请单下载数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:42
 */
public interface SpecialInvoiceReversalDownloadDao {
    
    /**
     * 根据sqbxzqqpch查询红字申请单下载记录数
     *
     * @param sqbxzqqpch
     * @return
     */
    int getCountSpecialInvoiceReversalDownload(@Param("sqbxzqqpch") String sqbxzqqpch);
    
    /**
     * 根据sqbxzqqpch查询红字申请单下载
     *
     * @param sqbxzqqpch
     * @return
     */
    SpecialInvoiceReversalDownloadEntity getSpecialInvoiceReversalDownload(@Param("sqbxzqqpch") String sqbxzqqpch);
    
    /**
     * 保存红字申请单下载
     *
     * @param specialInvoiceReversalDownload
     * @return
     */
    int insertSpecialInvoiceReversalDownload(SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload);
    
    /**
     * 更新申请单下载状态
     *
     * @param nsrsbh
     * @param sqbxzqqpch
     * @param sjzt
     */
    void updateDownloadRedInvoiceStatus(@Param("nsrsbh") String nsrsbh, @Param("sqbxzqqpch") String sqbxzqqpch, @Param("sjzt") String sjzt);
}
