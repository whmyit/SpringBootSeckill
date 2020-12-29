package com.dxhy.order.api;

import com.dxhy.order.model.dto.KdniaoQueryReq;
import com.dxhy.order.model.dto.KdniaoRes;

/**
 *
 * @author sunpe
 * @date 2018/9/21
 */
public interface ApiKdniaoTrackApiService {
    /**
     * 查询订单物流轨迹
     *
     * @param req
     * @return
     * @throws Exception
     */
    String getOrderTracesByJson(KdniaoQueryReq req) throws Exception;
    
    /**
     * 查询订单物流轨迹
     * @param req
     * @return
     * @throws Exception
     */
    KdniaoRes getOrderTraces(KdniaoQueryReq req) throws Exception;
}
