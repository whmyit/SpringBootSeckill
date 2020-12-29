package com.dxhy.order.api;

import com.dxhy.order.model.TaxEquipmentInfo;

import java.util.List;

/**
 * 税控设备管理业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 10:44
 */
public interface ApiTaxEquipmentService {
    
    /**
     * 更新税控设备
     *
     * @param taxEquipmentInfo
     * @return
     */
    int updateTaxEquipment(TaxEquipmentInfo taxEquipmentInfo);
    
    /**
     * 新增税控设备
     *
     * @param taxEquipmentInfo
     * @return
     */
    int addTaxEquipment(TaxEquipmentInfo taxEquipmentInfo);
    
    /**
     * 查询税控设备列表
     *
     * @param taxEquipmentInfo
     * @param shList
     * @return
     */
    List<TaxEquipmentInfo> queryTaxEquipmentList(TaxEquipmentInfo taxEquipmentInfo, List<String> shList);
    
    /**
     * 根据税号获取对应税号的终端设备号,如果查询不到数据返回为默认值C48
     *
     * @param nsrsbh
     * @return
     */
    String getTerminalCode(String nsrsbh);
    
}
