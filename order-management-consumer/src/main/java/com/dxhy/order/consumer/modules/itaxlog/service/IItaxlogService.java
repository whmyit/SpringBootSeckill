package com.dxhy.order.consumer.modules.itaxlog.service;

import com.dxhy.order.consumer.model.SysLogEntity;

/**
 * 大B日志集成服务
 *
 * @author ZSC-DXHY
 */
public interface IItaxlogService {
    
    /**
     * 保存到日志中心
     *
     * @param sysLogEntity
     */
    void saveItaxLog(SysLogEntity sysLogEntity);
    
}
