package com.dxhy.order.consumer.modules.order.service;

import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;


/**
 * 订单合并接口
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年8月3日 下午2:46:26
 */
public interface IOrderMergeService {
    
    /**
     * 订单合并
     *
     * @param idList
     * @param isMergeSameOrderItem
     * @return
     */
    R orderMerge(List<Map> idList, String isMergeSameOrderItem);
    
    /**
     * 订单合并数据校验
     *
     * @param grov
     * @param shList
     * @return
     */
    R orderMergeCheck(String[] grov, List<String> shList);
    
}
