package com.dxhy.order.dao;


import com.dxhy.order.model.entity.FpExpress;

import java.util.List;
import java.util.Map;

/**
 * 发票快递数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:10
 */
public interface FpExpressDao {
    
    /**
     * 新增发票快递信息
     *
     * @param record
     * @return
     */
    int insertFpExpress(FpExpress record);

//    int insertSelective(FpExpress record);
    
    /**
     * 更新快递信息
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(FpExpress record);
    
    /**
     * 更新快递信息
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithBlobs(FpExpress record);
    
    /**
     * 更新快递信息
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(FpExpress record);
    
    /**
     * 查询快递列表
     *
     * @param map
     * @return
     */
    List<FpExpress> queryList(Map<String, Object> map);
    
    /**
     * 查询快递
     *
     * @return
     */
    List<FpExpress> queryWqs();
    
    /**
     * 查询快递列表
     *
     * @param map
     * @return
     */
    List<FpExpress> expressCompanyList(Map<String, Object> map);
}
