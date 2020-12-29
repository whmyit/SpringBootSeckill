package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.InvalidInvoiceInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：InvalidInvoiceService
 * @Description ：发票作废接口
 * @date ：2018年8月1日 下午5:17:09
 */

public interface InvalidInvoiceService {
    
    /**
     * 发票作废接口
     *
     * @param receviePoint
     * @param invoiceType
     * @param invoiceCode
     * @param invoiceNum
     * @param kpjh
     * @param nsrsbh
     * @param xhfmc
     * @return
     * @throws OrderReceiveException
     */
    R validInvoice(String receviePoint, String invoiceType, String invoiceCode, String invoiceNum, String kpjh, String nsrsbh, String xhfmc) throws OrderReceiveException;
    
    /**
     * 发票作废列表查询接口
     *
     * @param paramMap
     * @param xhfNsrsbh
     * @return
     */
    PageUtils queryByInvalidInvoice(Map paramMap, List<String> xhfNsrsbh);
    
    /**
     * 批量作废发票的接口 只做批量插入数据库
     *
     * @param orderIdArrays
     * @return
     * @throws OrderReceiveException
     */
    R batchValidInvoice(List<Map> orderIdArrays) throws OrderReceiveException;
    
    /**
     * 空白发票作废完成时候更新状态
     *
     * @param fpdm
     * @param fphm
     * @param zfzt
     * @param sldid
     * @param nsrsbh
     * @param fpzldm
     * @return
     */
    R voidInvalidInvoiceActiveX(String fpdm, String fphm, String zfzt, String sldid, String nsrsbh, String fpzldm);
    
    /**
     * 手动推送作废发票数据状态
     *
     * @param orderIdArrays
     * @return
     */
    R manualPushInvalidInvoice(List<Map> orderIdArrays);
    
    /**
     * 空白发票作废
     *
     * @param paramMap
     * @param xhfNsrsbh
     * @return
     */
    PageUtils queryKbInvoiceList(Map<String, Object> paramMap, List<String> xhfNsrsbh);
    
    /**
     * 处理作废成功数据
     *
     * @param invalidInvoiceInfo
     * @param orderInvoiceInfo
     * @param shList
     * @throws OrderReceiveException
     */
    void processSuccessInvalid(InvalidInvoiceInfo invalidInvoiceInfo, OrderInvoiceInfo orderInvoiceInfo, List<String> shList) throws OrderReceiveException;
    
    /**
     * 处理方格作废成功数据
     *
     * @param invalid
     * @param orderInvoiceInfo
     * @param shList
     * @throws OrderReceiveException
     */
    void fgProcessSuccessInvalid(InvalidInvoiceInfo invalid, OrderInvoiceInfo orderInvoiceInfo, List<String> shList) throws OrderReceiveException;
    
    /**
     * 更新剩余可冲红金额
     *
     * @param orderInvoiceInfo
     * @param shList
     */
    void updateSykchJe(OrderInvoiceInfo orderInvoiceInfo, List<String> shList);
}
