package com.dxhy.order.consumer.openapi.service;

import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;

import java.util.Map;

/**
 * 税控设备同步信息校验
 *
 * @author ZSC-DXHY
 */
public interface IValidateTaxEquipmentInfo {
    /**
     * 税控设备信息同步校验
     *
     * @param sksbxxtbReq
     * @return
     */
    Map<String, String> checkSyncTaxEquipmentInfo(SKSBXXTB_REQ sksbxxtbReq);
    
}
