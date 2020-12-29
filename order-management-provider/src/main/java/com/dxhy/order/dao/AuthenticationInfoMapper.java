package com.dxhy.order.dao;

import com.dxhy.order.model.AuthenticationInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 鉴权表业务层处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:47
 */
public interface AuthenticationInfoMapper {
    /**
     * 查询所有有效状态的secretId
     *
     * @param authStatus
     * @return
     */
    List<AuthenticationInfo> selectAuthticationAll(@Param("authStatus") String authStatus);
    
    /**
     * 新增
     *
     * @param record
     * @return
     */
    int insert(AuthenticationInfo record);
    
    /**
     * 根据主键新增
     *
     * @param record
     * @return
     */
    int insertSelective(AuthenticationInfo record);
    
    /**
     * 根据主键查询
     *
     * @param id
     * @return
     */
    AuthenticationInfo selectByPrimaryKey(String id);
    
    /**
     * 批量更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(AuthenticationInfo record);
    
    /**
     * 根据主键更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(AuthenticationInfo record);
    
    /**
     * 查询鉴权列表
     *
     * @param authenticationInfo
     * @param shList
     * @return
     */
    List<AuthenticationInfo> queryAuthenInfoList(@Param("data") AuthenticationInfo authenticationInfo, @Param("shList") List<String> shList);
    
    /**
     * 查询鉴权信息
     *
     * @param authenticationInfo
     * @return
     */
    AuthenticationInfo queryAuthenInfo(AuthenticationInfo authenticationInfo);
}
