package com.dxhy.order.api;

import com.dxhy.order.model.EwmGzhConfig;

import java.util.List;

/**
 * 公众号配置信息服务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 9:12
 */
public interface ApiEwmGzhConfService {
    /**
     * 获取公众号配置列表
     *
     * @param ewmGzhConfig
     * @param shList
     * @return
     */
    List<EwmGzhConfig> queryEwmGzhConfigList(EwmGzhConfig ewmGzhConfig, List<String> shList);
    
    /**
     * 查询公众号配置详情
     *
     * @param ewmGzhConfig
     * @return
     */
    EwmGzhConfig queryEwmGzhConfInfo(EwmGzhConfig ewmGzhConfig);
    
    /**
     * 更新公众号配置信息
     *
     * @param ewmGzhConfig
     * @return
     */
    int updateEwmGzhConfByPrimaryKey(EwmGzhConfig ewmGzhConfig);
    
    /**
     * 新增公众号配置信息
     *
     * @param ewmGzhConfig
     * @return
     */
    int addEwmGzhConfInfo(EwmGzhConfig ewmGzhConfig);
}
