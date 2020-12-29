package com.dxhy.order.api;

import com.dxhy.order.model.R;

/**
 * @author ：杨士勇
 * @ClassName ：OpenInvoiceService
 * @Description ：将发票开具的数据放到mq中
 * @date ：2019年3月11日 下午4:11:03
 */

public interface OpenInvoiceService {
    
    /**
     * 发票开具队列存放数据
     *
     * @param content
     * @param nsrsbh
     * @return
     */
    R openAnInvoice(String content, String nsrsbh);
    
}
