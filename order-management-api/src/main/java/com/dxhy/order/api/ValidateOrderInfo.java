package com.dxhy.order.api;

import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.entity.SpecialExcelImport;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;

import java.util.List;
import java.util.Map;

/**
 * 校验订单接口
 *
 * @author ZSC-DXHY
 */
public interface ValidateOrderInfo {

    /**
     * 订单校验接口
     *
     * @param commonOrderInfo
     * @return
     * @throws Exception
     */
    Map<String, String> volidateOrder(CommonOrderInfo commonOrderInfo);

    /**
     * 校验订单发票接口(非空和长度校验加上数据校验)
     *
     * @param commonOrderInfo
     * @return
     */
    Map<String,String> checkOrderInvoice(CommonOrderInfo commonOrderInfo);
    
    /**
     * 校验接口数据合法性(不包含非空校验)
     *
     * @param commonOrderInfo
     * @return
     */
    Map<String, String> checkInvoiceData(CommonOrderInfo commonOrderInfo);
    
    /**
     * 专票表格导入校验
     *
     * @param specialExcelImportList
     * @return
     */
    Map<String, Object> checkSpecialExcelImport(List<SpecialExcelImport> specialExcelImportList);
    
    
    /**
     * 校验作废接口
     *
     * @param zfxxReq
     * @return
     */
    Map<String, String> checkInvalidInvoice(ZFXX_REQ zfxxReq);
    
}
