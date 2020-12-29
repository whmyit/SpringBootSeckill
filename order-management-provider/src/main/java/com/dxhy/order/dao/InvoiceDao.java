package com.dxhy.order.dao;

import com.dxhy.order.model.entity.DrawerInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 开票人信息数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:27
 */
public interface InvoiceDao {
    
    /**
     * 更新开票人信息
     *
     * @param drawerInfoEntity
     * @param shList
     * @return
     */
    int updateDrawer(@Param("drawer") DrawerInfoEntity drawerInfoEntity, @Param("shList") List<String> shList);
    
    /**
     * 新增开票人信息
     *
     * @param drawerInfoEntity
     * @return
     */
    int insertDrawer(DrawerInfoEntity drawerInfoEntity);
    
    /**
     * 开票人信息查询
     *
     * @param shList
     * @param userId
     * @return
     */
    DrawerInfoEntity queryDrawerInfo(@Param("shList") List<String> shList, @Param("userId") String userId);
    
}
