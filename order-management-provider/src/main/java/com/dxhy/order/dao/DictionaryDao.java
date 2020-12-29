package com.dxhy.order.dao;

import com.dxhy.order.model.entity.DictionaryEntity;

import java.util.List;

/**
 * 字典表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:57
 */
public interface DictionaryDao {
    
    /**
     * 从字典表查询数据
     *
     * @param type
     * @return
     */
    List<DictionaryEntity> selectDictionaries(String type);
}
