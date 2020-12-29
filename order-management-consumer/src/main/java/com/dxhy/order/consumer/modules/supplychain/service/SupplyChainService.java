package com.dxhy.order.consumer.modules.supplychain.service;


import com.dxhy.order.consumer.modules.supplychain.model.SupplyChainBaseResponseBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynCheckResultRequestBean;
import com.dxhy.order.consumer.modules.supplychain.model.SynOrderRequestBean;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 进项供应链业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:46
 */
public interface SupplyChainService {
    
    
    /**
     * 数据同步
     *
     * @param request
     * @return
     */
    SupplyChainBaseResponseBean syncOrder(List<SynOrderRequestBean> request);
    
    /**
     * 同步结果处理
     *
     * @param request
     * @return
     */
    SupplyChainBaseResponseBean syncChecResult(List<SynCheckResultRequestBean> request);
    
    /**
     * 推送订单信息
     *
     * @param paramMap
     * @return
     */
    R pushCompleteOrder(Map<String, String> paramMap);
}
