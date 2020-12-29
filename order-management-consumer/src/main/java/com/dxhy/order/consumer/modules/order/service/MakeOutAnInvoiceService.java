package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInvoiceInfoRequest;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 开票数据操作
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:36
 */
public interface MakeOutAnInvoiceService {
    
    /**
     * 开票接口数据传送
     *
     * @param commonOrderInfo
     * @param sldMap
     * @return
     */
    R makeOutAnInovice(List<CommonOrderInfo> commonOrderInfo, Map<String, PageSld> sldMap);
    
    /**
     * 开票完成更新发票信息
     *
     * @param invoiceInfo
     * @param shList
     * @return
     */
    R updateOrderInvoiceInfo(OrderInvoiceInfoRequest invoiceInfo, List<String> shList);
    
}
