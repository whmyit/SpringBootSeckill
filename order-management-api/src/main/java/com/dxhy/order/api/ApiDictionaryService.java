package com.dxhy.order.api;

import com.dxhy.order.model.entity.DictionaryEntity;

import java.util.List;

/**
 * 字典表查询
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 9:10
 */
public interface ApiDictionaryService {
    
    /**
     * 查询字典表信息
     *
     * @param type
     * @return
     */
    List<DictionaryEntity> queryDictionaries(String type);
}

