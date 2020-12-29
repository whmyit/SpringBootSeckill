package com.dxhy.order.dao;

import com.dxhy.order.model.EwmConfigInfo;

import java.util.Map;

/**
 * 二维码配置信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:58
 */
public interface EwmConfigInfoMapper {
    
    /**
     * 插入数据
     *
     * @param record
     * @return
     */
    int insertSelective(EwmConfigInfo record);
    
    /**
     * 更新数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(EwmConfigInfo record);
    
    /**
     * 查询信息
     *
     * @param paramMap
     * @return
     */
    EwmConfigInfo queryEwmConfigInfo(Map<String, Object> paramMap);
    
}
