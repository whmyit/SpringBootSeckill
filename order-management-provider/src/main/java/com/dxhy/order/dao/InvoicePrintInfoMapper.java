package com.dxhy.order.dao;

import com.dxhy.order.model.InvoicePrintInfo;
import com.dxhy.order.model.dto.FgInvoicePrintDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 发票打印表数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:29
 */
public interface InvoicePrintInfoMapper {
    
    /**
     * 新怎发票打印数据
     *
     * @param record
     * @return
     */
    int insertSelective(InvoicePrintInfo record);
    
    /**
     * 查询待打印发票数据
     *
     * @param dypch
     * @param nsrsbh
     * @return
     */
    FgInvoicePrintDto getPrintInvoices(@Param("dypch") String dypch, @Param("xhfNsrsbh") String nsrsbh);
    
    /**
     * 修改待打印数据状态
     *
     * @param dypch
     * @param nsrsbh
     * @param sjzt
     */
    void updatePrintInvoicesStatus(@Param("dypch") String dypch, @Param("xhfNsrsbh") String nsrsbh, @Param("sjzt") String sjzt);
    
    /**
     * 打印完成修改数据库状态
     *
     * @param invoicePrintInfo
     * @param shList
     */
    void updateFgPrintInvoice(@Param("invoicePrintInfo") InvoicePrintInfo invoicePrintInfo, @Param("shList") List<String> shList);
    
    /**
     * 查询打印列表数据
     *
     * @param fpqqlsh
     * @param nsrsbh
     * @return
     */
    List<InvoicePrintInfo> getPrintInvoicesList(@Param("fpqqlsh") String fpqqlsh, @Param("xhf_nsrsbh") String nsrsbh);
}
