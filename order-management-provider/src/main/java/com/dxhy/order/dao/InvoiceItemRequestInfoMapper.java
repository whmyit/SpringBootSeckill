package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceItemPO;
import com.dxhy.order.model.InvoiceItemRequestInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 按照明细汇总数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:28
 */
public interface InvoiceItemRequestInfoMapper {
    
    /**
     * 插入数据
     *
     * @param record
     * @return
     */
    int insertInvoiceItemRequest(InvoiceItemRequestInfo record);
    
    /**
     * 更新数据
     *
     * @param record
     * @return
     */
    int updateInvoiceItemRequestById(InvoiceItemRequestInfo record);
    
    /**
     * 获取项目汇总状态数据
     *
     * @param nsrsbh
     * @param hzrq
     * @return
     */
    List<Map> getSummaryItemState(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq);
    
    /**
     * 获取项目汇总数据
     *
     * @param nsrsbh
     * @param hzrq
     * @return
     */
    List<Map> selectSummaryItemData(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq);
    
    /**
     * 获取项目汇总统计数据
     *
     * @param nsrsbh
     * @param hzrqList
     * @return
     */
    List<InvoiceItemPO> selectSummaryItemStatistics(@Param("nsrsbh") String nsrsbh, @Param("hzrqList") List<String> hzrqList);
    
    /**
     * 查询项目信息
     *
     * @param nsrsbh
     * @param hzrq
     * @param spbm
     * @return
     */
    InvoiceItemRequestInfo selectItemItem(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq, @Param("spbm") String spbm);
}
