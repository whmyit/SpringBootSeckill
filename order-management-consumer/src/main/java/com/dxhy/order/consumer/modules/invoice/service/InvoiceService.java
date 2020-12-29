package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;

import java.util.List;
import java.util.Map;

/**
 * 发票操作层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:30
 */
public interface InvoiceService {
    
    /**
     * 根据id查询数据开票
     *
     * @param paperArray
     * @param specialArray
     * @param eleArray
     * @param paramMap
     * @param userId
     * @param shList
     * @return
     */
    R batchInvoiceById(String[] paperArray, String[] specialArray, String[] eleArray, Map<String, PageSld> paramMap, String userId, List<String> shList);
    
    /**
     * 页面更新补全数据后开票
     *
     * @param paramList
     * @param sldMap
     * @param uid
     * @param shList
     * @return
     */
    R updateAndInvoice(List<CommonOrderInfo> paramList, Map<String, PageSld> sldMap, String uid, List<String> shList);
    
    /**
     * 根据订单信息开票
     *
     * @param paramList
     * @param sldMap
     * @param uid
     * @param shList
     * @return
     */
    R batchInvoice(List<CommonOrderInfo> paramList, Map<String, PageSld> sldMap, String uid, List<String> shList);
    
    /**
     * 根据id处理动态码数据
     *
     * @param id
     * @param sldMap
     * @param shList
     * @return
     */
    com.dxhy.order.model.R dynamciInvoiceByOrderId(String id, Map<String, PageSld> sldMap, List<String> shList);
    
    /**
     * 查询发票开具状态
     *
     * @param fpqqlsh
     * @param xhfNsrsbh
     * @return
     */
    CommonInvoiceStatus queryInvoiceStatus(String fpqqlsh, String xhfNsrsbh);
    
}
