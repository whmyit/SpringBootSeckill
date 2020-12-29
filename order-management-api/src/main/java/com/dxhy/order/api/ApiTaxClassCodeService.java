package com.dxhy.order.api;

import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;

import java.util.Map;

/**
 * 税收分类编码接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:37
 */
public interface ApiTaxClassCodeService {
    /**
     * 查询税收分类编码
     *
     * @param map
     * @return
     */
    PageUtils queryTaxClassCode(Map<String, Object> map);

    /**
     * 查询税收分类编码是否存在
     * @param taxClassCode
     * @return
     */
    TaxClassCodeEntity queryTaxClassCodeEntity(String taxClassCode);
    
    /**
     * 查询成品油编码
     *
     * @param spbm
     * @return
     */
    OilEntity queryOilBySpbm(String spbm);
}
