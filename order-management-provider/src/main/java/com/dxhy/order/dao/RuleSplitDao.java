package com.dxhy.order.dao;

import com.dxhy.order.model.entity.RuleSplitEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 拆分规则数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:19
 */
public interface RuleSplitDao {
    /**
     * 查询
     * @param taxpayerCode
     * @param userId
     * @return
     */
    RuleSplitEntity selectRuleSplit(@Param("taxpayerCode") String taxpayerCode, @Param("userId") String userId);

    /**
     * 添加
     * @param ruleSplitEntity
     * @return
     */
    int insert(RuleSplitEntity ruleSplitEntity);

    /**
     * 保存
     * @param ruleSplitEntity
     * @return
     */
    int update(RuleSplitEntity ruleSplitEntity);

}
