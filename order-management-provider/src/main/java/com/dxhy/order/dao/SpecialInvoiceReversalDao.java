package com.dxhy.order.dao;

import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 红字申请单信息数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:40
 */
public interface SpecialInvoiceReversalDao {
    
    
    /**
     * 列表数据查询
     *
     * @param params
     * @return
     */
    List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversals(Map<String, Object> params);
    
    /**
     * 列表数据不含税金额 税额统计
     *
     * @param params
     * @return
     */
    Map<String, Object> selectSpecialInvoiceReversalsCount(Map<String, Object> params);
    
    /**
     * 统一查询接口查询条件
     *
     * @param specialInvoiceReversalEntity
     * @return
     */
    SpecialInvoiceReversalEntity selectSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    
    /**
     * 统一更新接口
     *
     * @param specialInvoiceReversalEntity
     * @return
     */
    int updateByPrimaryKeySelective(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    /**
     * 发票结果同步红字申请单开票状态
     *
     * @param specialInvoiceReversalEntity
     * @return
     */
    int updateInvoiceStatusByXxbbh(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    /**
     * 统一数据插入
     *
     * @param specialInvoiceReversal
     * @return
     */
    int insertSelective(SpecialInvoiceReversalEntity specialInvoiceReversal);
    
    //TODO 逻辑待确定
    
    /**
     * 查询申请单信息
     *
     * @param ids
     * @param excludeSubmitStatuses
     * @return
     */
    List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversalsByIds(@Param("ids") String[] ids, @Param("excludeSubmitStatuses") String[] excludeSubmitStatuses);
    
    /**
     * 根据查询条件查询多条数据
     *
     * @param specialInvoiceReversalEntity
     * @return
     */
    List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversalList(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    //TODO 逻辑待确定
    
    /**
     * 获取申请单信息
     *
     * @return
     */
    List<String> selectSpecialInvoiceReversalTaxpayerCodes();
    
    //todo fangge
    
    /**
     * 查询待上传数据
     *
     * @param sqdscqqlsh
     * @param nsrsbh
     * @return
     */
    SpecialInvoiceReversalEntity selectDscSpecialInvoiceReversalsBySqbscqqlsh(@Param("sqdscqqlsh") String sqdscqqlsh, @Param("nsrsbh") String nsrsbh);
    
    /**
     * 红票申请单上传   状态修改
     *
     * @param specialInvoiceReversalEntity
     */
    void getUploadRedInvoiceStatus(SpecialInvoiceReversalEntity specialInvoiceReversalEntity);
    
    /**
     * 获取申请单信息
     *
     * @param sqbxzqqpch
     * @param nsrsbh
     * @return
     */
    List<SpecialInvoiceReversalEntity> selectSpecialInvoiceReversalsBySqbxzqqpch(@Param("sqbxzqqpch") String sqbxzqqpch, @Param("nsrsbh") String nsrsbh);
    
}
