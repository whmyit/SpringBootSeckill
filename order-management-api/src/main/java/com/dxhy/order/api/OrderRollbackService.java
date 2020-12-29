package com.dxhy.order.api;

import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 订单回退接口
 *
 * @author ZSC-DXHY
 */
public interface OrderRollbackService {
    
    /**
     * 订单回退
     *
     * @param idList
     * @return
     */
    R orderRollback(List<Map> idList);

}
