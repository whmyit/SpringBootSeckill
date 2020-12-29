package com.dxhy.order.consumer.modules.fiscal.service.bwactivexs;

import com.dxhy.order.model.bwactivexs.dy.DydListRequest;
import com.dxhy.order.model.bwactivexs.dy.DydListResponse;
import com.dxhy.order.model.bwactivexs.server.SkServerRequest;
import com.dxhy.order.model.bwactivexs.server.SkServerResponse;

/**
 * 百旺受理点业务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:39
 */
public interface SldManagerServiceBw {
    
    /**
     * 获取受理点信息
     *
     * @param request
     * @return
     */
    SkServerResponse queryServerInfo(SkServerRequest request);
    
    /**
     * 获取打印点信息
     *
     * @param request
     * @return
     */
    DydListResponse queryDydXxList(DydListRequest request);
    
}
