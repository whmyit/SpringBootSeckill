package com.dxhy.order.service;

import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;

/**
 * 统一方法调用业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:49
 */
public interface UnifyService {
    
    /**
     * 获取C48金税盘信息
     *
     * @param sldJspxxRequest
     * @param terminalCode
     * @return
     */
    SldJspxxResponse selectSldJspxx(SldJspxxRequest sldJspxxRequest, String terminalCode);
    
}
