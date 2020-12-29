package com.dxhy.order.dao;

import com.dxhy.order.model.EwmGzhConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 二维码配置信息表数据交互层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:01
 */
public interface EwmGzhConfigMapper {
    
    /**
     * 根据主键删除配置
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);
    
    /**
     * 新增
     *
     * @param record
     * @return
     */
    int insert(EwmGzhConfig record);
    
    /**
     * 配置新增
     *
     * @param record
     * @return
     */
    int insertSelective(EwmGzhConfig record);
    
    /**
     * 查询
     *
     * @param id
     * @return
     */
    EwmGzhConfig selectByPrimaryKey(String id);
    
    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(EwmGzhConfig record);
    
    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(EwmGzhConfig record);
    
    /**
     * 查询
     *
     * @param record
     * @return
     */
    EwmGzhConfig selectByEwmGzhConfig(EwmGzhConfig record);
    
    /**
     * 查询列表
     *
     * @param record
     * @param shList
     * @return
     */
    List<EwmGzhConfig> selectListByEwmGzhConfig(@Param("data") EwmGzhConfig record, @Param("shList") List<String> shList);
}
