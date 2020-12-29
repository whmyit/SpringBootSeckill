package com.dxhy.order.api;

import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;

import java.util.Map;

/**
 * 校验红字申请单接口
 *
 * @author ZSC-DXHY
 */
public interface IValidateInterfaceSpecialInvoice {
    
    /**
     * 校验红字申请单上传接口
     *
     * @param hzsqdscReq
     * @return
     */
    Map<String, String> checkSpecialInvoiceUpload(HZSQDSC_REQ hzsqdscReq);
    
    /**
     * 校验红字申请单下载接口
     *
     * @param hzsqdxzReq
     * @return
     */
    Map<String, String> checkSpecialInvoiceDownload(HZSQDXZ_REQ hzsqdxzReq);
    
    
}
