package com.dxhy.order.dao;

import com.dxhy.order.model.entity.GroupCommodity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分组税编数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:03
 */
public interface GroupCommodityDao {
    
    /**
     * 添加
     *
     * @param groupCommodity
     * @return
     */
    int insertGroup(GroupCommodity groupCommodity);
    
    /**
     * 分组
     *
     * @param groupCommodity
     * @param shList
     * @return
     */
    int updateGroup(@Param("groupCommodity") GroupCommodity groupCommodity, @Param("shList") List<String> shList);
    
    /**
     * 检验groupName
     *
     * @param groupName    关联税号
     * @param taxpayerCode 关联税号
     * @return
     */
    int selectGroupByName(@Param("groupName") String groupName, @Param("taxpayerCode") String taxpayerCode);
    
    /**
     * 删除
     *
     * @param id
     * @param shList
     * @return
     */
    int deleteGroupById(@Param("id") String id, @Param("shList") List<String> shList);
    
    /**
     * 根据名称和用户id获取分组id
     *
     * @param groupName
     * @param userId
     * @return
     */
    String selectGroupIdByNameAndUserId(@Param("groupName") String groupName, @Param("userId") String userId);
    
    /**
     * 查询分组
     *
     * @param groupName
     * @param userId
     * @return
     */
    int selectGroupByNameAndUserId(@Param("groupName") String groupName, @Param("userId") String userId);
    
    /**
     * 查询分组
     *
     * @param groupCode
     * @param userId
     * @return
     */
    int selectGroupByCodeAndUserId(@Param("groupCode") String groupCode, @Param("userId") String userId);
    
    /**
     * 查询分组
     *
     * @param userId
     * @return
     */
    List<GroupCommodity> selectGroupListByUserId(@Param("userId") String userId);
}
