package com.dxhy.order.dao;

import com.dxhy.order.model.entity.InvoiceWarningInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 余票预警表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:32
 */
public interface InvoiceWarningInfoMapper {
    
    /**
     * 新增发票预警数据
     *
     * @param record
     * @return
     */
    int insertInvoiceWarning(InvoiceWarningInfo record);
    
    /**
     * 更新发票预警数据
     *
     * @param invoiceWarningInfo
     * @param shList
     * @return
     */
    int updateYpWarnInfo(@Param("invoiceWarning") InvoiceWarningInfo invoiceWarningInfo, @Param("shList") List<String> shList);
    
    /**
     * 余票预警查询
     *
     * @param shList             纳税人识别号
     * @param invoiceWarningInfo
     * @return
     */
    List<InvoiceWarningInfo> selectYpWarning(@Param("invoiceWarning") InvoiceWarningInfo invoiceWarningInfo, @Param("shList") List<String> shList);
    
}
