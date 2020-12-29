package com.dxhy.order.dao;

import com.dxhy.order.model.PushInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 推送表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:53
 */
public interface PushInfoMapper {
    
    /**
     * 根据条件查询
     *
     * @param pushInfo
     * @return
     */
    PushInfo selectByPushInfo(PushInfo pushInfo);
    
    /**
     * 根据税号查询多个推送地址
     *
     * @param pushInfo
     * @return
     */
    List<PushInfo> selectListByPushInfo(PushInfo pushInfo);
    
    /**
     * 查询推送表数据
     *
     * @param pushInfo
     * @param shList
     * @return
     */
    List<PushInfo> queryPushInfoList(@Param("data") PushInfo pushInfo, @Param("shList") List<String> shList);
    
    /**
     * 查询企业信息
     *
     * @param pushInfo
     * @param shList
     * @return
     */
    List<PushInfo> queryPushInfoListByMap(@Param("data") Map pushInfo, @Param("shList") List<String> shList);
    
    
    /**
     * 插入推送表数据
     *
     * @param record
     * @return
     */
    int insertSelective(PushInfo record);
    
    /**
     * 更新推送表数据
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(PushInfo record);
    
    
    /**
     * 更新推送信息
     *
     * @param updatePushInfo
     * @return
     */
    int updateAuthStatusByShAndInterfaceType(PushInfo updatePushInfo);
}
