package com.dxhy.order.dao;

import com.dxhy.order.model.UserGuiderInfo;

import java.util.List;

/**
 * 用户引导数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:49
 */
public interface UserGuiderInfoMapper {
    
    /**
     * 添加用户引导
     *
     * @param record
     * @return
     */
    int insertSelective(UserGuiderInfo record);
    
    /**
     * 更新用户引导
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(UserGuiderInfo record);
    
    /**
     * 查询用户引导
     *
     * @param queryUserGuiderParam
     * @return
     */
    List<UserGuiderInfo> queryUserGuiderList(UserGuiderInfo queryUserGuiderParam);
    
}
