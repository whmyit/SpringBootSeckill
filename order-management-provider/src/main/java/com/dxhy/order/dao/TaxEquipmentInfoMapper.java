package com.dxhy.order.dao;

import com.dxhy.order.model.TaxEquipmentInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 税控设备数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:47
 */
public interface TaxEquipmentInfoMapper {
    
    /**
     * 新增税控设备
     *
     * @param taxEquipmentInfo
     * @return
     */
    int insertTaxEquipment(TaxEquipmentInfo taxEquipmentInfo);
    
    /**
     * 更新税控设备
     *
     * @param taxEquipmentInfo
     * @return
     */
    int updateTaxEquipment(TaxEquipmentInfo taxEquipmentInfo);
    
    /**
     * 查询税控设备
     *
     * @param taxEquipmentInfo
     * @param shList
     * @return
     */
    List<TaxEquipmentInfo> queryTaxEquipmentList(@Param("data") TaxEquipmentInfo taxEquipmentInfo, @Param("shList") List<String> shList);
    
    /**
     * 根据税号查询税控设备信息
     *
     * @param nsrsbh
     * @return
     */
    TaxEquipmentInfo selectByNsrsbh(String nsrsbh);
    
}
