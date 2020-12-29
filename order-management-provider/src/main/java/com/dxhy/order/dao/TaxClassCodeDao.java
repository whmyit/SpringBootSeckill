package com.dxhy.order.dao;

import com.dxhy.order.constant.TableNameConstant;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.elephant.dbcache.annotation.CacheKey;
import com.elephant.dbcache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 税收分类码表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:47
 */
public interface TaxClassCodeDao {
    /**
     * 查询税收分类编码
     *
     * @param map
     * @return
     */
    List<TaxClassCodeEntity> selectTaxClassCode(Map<String, Object> map);
    
    // warning 以下sql添加redis缓存 修改请注意
    
    /**
     * 判断税收分类编码是否存在
     *
     * @param spbm
     * @return
     */
    @Cacheable(key = TableNameConstant.DB, table = TableNameConstant.TAX_CLASS_CODE, operMode = Cacheable.OperMode.SELECT)
    TaxClassCodeEntity queryTaxClassCodeEntityBySpbm(@CacheKey(value = "spbm") String spbm);
    
    
}
