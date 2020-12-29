package com.dxhy.order.dao;

import com.dxhy.order.model.EwmConfigItemInfo;

import java.util.List;
/**
 * 二维码配置明细表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:59
 */
public interface EwmConfigItemInfoMapper {
    
    /**
     * 插入二维码明细信息
     *
     * @param record
     * @return
     */
    int insertEwmConfigItem(EwmConfigItemInfo record);
    
    /**
     * 查询二维码明细信息
     *
     * @param ewmConfigId
     * @return
     */
    List<EwmConfigItemInfo> queryEwmItemInfoByEwmConfigId(String ewmConfigId);
    
    /**
     * 删除二维码信息
     *
     * @param id
     * @return
     */
    int deleteByEwmConfigId(String id);
}
