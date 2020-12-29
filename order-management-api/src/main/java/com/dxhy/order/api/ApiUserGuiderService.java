package com.dxhy.order.api;

import com.dxhy.order.model.UserGuiderInfo;

import java.util.List;

/**
 * 用于引导业务处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:45
 */
public interface ApiUserGuiderService {
    
    
    /**
     * 查询用户引导配置列表
     *
     * @param userGuiderInfo
     * @return
     */
    List<UserGuiderInfo> queryUserGuiderList(UserGuiderInfo userGuiderInfo);
    
    /**
     * 更新用户引导
     *
     * @param userGuiderInfo
     * @return
     */
    boolean updateUserGuider(UserGuiderInfo userGuiderInfo);
}
