package com.dxhy.order.api;


import com.dxhy.order.model.AuthenticationInfo;
import com.dxhy.order.model.PushInfo;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;


/**
 * 鉴权表统一接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-13 19:59
 */
public interface ApiAuthenticationService {
    
    /**
     * 查询鉴权配置信息列表
     *
     * @param authenticationInfo
     * @param shList
     * @return
     */
    List<AuthenticationInfo> queryAuthenInfoList(AuthenticationInfo authenticationInfo, List<String> shList);
    
    /**
     * 更新鉴权信息
     *
     * @param authenticationInfo
     * @return
     */
    int updateAuthenInfoByPrimaryKey(AuthenticationInfo authenticationInfo);
    
    /**
     * 新增企业建全信息
     *
     * @param authenticationInfo
     * @return
     */
    int addAuthenInfo(AuthenticationInfo authenticationInfo);
    
    /**
     * 单条数据查询
     *
     * @param authenticationInfo
     * @return
     */
    AuthenticationInfo queryAuthenInfo(AuthenticationInfo authenticationInfo);
    
    /**
     * 查询企业信息配置
     *
     * @param paramMap
     * @return
     */
    R queryEnterpreiseConfigInfo(Map<String, String> paramMap);
    
    /**
     * 保存企业信息
     *
     * @param authInfo
     * @param pushInfoList
     * @return
     */
    R saveEnterpriseCofnigInfo(AuthenticationInfo authInfo, List<PushInfo> pushInfoList);
}
