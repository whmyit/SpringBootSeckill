package com.dxhy.order.dao;


import com.dxhy.order.model.entity.GroupTaxClassCodeEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description: 集团税编库service
 * @Author:xueanna
 * @Date:2019/9/17
 */
public interface GroupTaxClassCodeMapper {
    
    /**
     * 插入
     *
     * @param record
     * @return
     */
    int insert(GroupTaxClassCodeEntity record);
    
    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    GroupTaxClassCodeEntity selectGroupTaxClassCode(String id);
    
    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(GroupTaxClassCodeEntity record);
    
    /**
     * 更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(GroupTaxClassCodeEntity record);
    
    /**
     * 集团税编库列表
     *
     * @param map
     * @return
     */
    List<Map<String, Object>> queryGroupTaxClassCode(Map<String, Object> map);
    
    /**
     * 集团税编批量共享数据
     *
     * @param taxClassCodeIdArray
     * @param shareStatus
     */
    void updateTaxClassCodeShareStatus(@Param(value = "taxClassCodeIdArray") String[] taxClassCodeIdArray, @Param(value = "shareStatus") String shareStatus);
    
    /**
     * 集团税编批量设置数据为启用
     *
     * @param taxClassCodeIdArray
     * @param dataStatus
     */
    void updateTaxClassCodeDataStatus(@Param(value = "taxClassCodeIdArray") String[] taxClassCodeIdArray, @Param(value = "dataStatus") String dataStatus);
    
    /**
     * 查询
     *
     * @param groupTaxClassCodeId
     * @return
     */
    Map<String, String> selectGroupTaxClassCodeById(@Param(value = "groupTaxClassCodeId") String groupTaxClassCodeId);
    
    /**
     * 查询
     *
     * @param paramMap
     * @return
     */
    List<GroupTaxClassCodeEntity> queryTaxClassCodeBySpbmAndSpmc(Map<String, String> paramMap);
    
    /**
     * 查询
     *
     * @param paramMap
     * @return
     */
    List<GroupTaxClassCodeEntity> queryTaxClassCodeBySpbmOrSpmc(Map<String, String> paramMap);
    
    /**
     * 集团商品名称校验
     *
     * @param map
     * @return
     */
    int selectByName(Map<String, String> map);
    
    /**
     * 集团商品code校验
     *
     * @param map
     * @return
     */
    int selectByCode(Map<String, String> map);
    
    /**
     * 更新
     *
     * @param id
     */
    void updateTaxClassCodeDifferenceFlag(@Param(value = "id") String id);
}
