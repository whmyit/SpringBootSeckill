package com.dxhy.order.api;

import com.dxhy.order.model.SalerWarning;

import java.util.List;

/**
 * 销方预警
 *
 * @author lizy
 */
public interface SalerWarningService {
    /**
     * 新增
     *
     * @param record
     */
    void addSalerWarning(SalerWarning record);
    
    /**
     * 更新
     *
     * @param record
     */
    void update(SalerWarning record);
    
    /**
     * 查询
     *
     * @param nsrsbh
     * @param createId
     * @return
     */
    List<SalerWarning> selectSalerWaringByNsrsbh(String nsrsbh, String createId);
    
}
