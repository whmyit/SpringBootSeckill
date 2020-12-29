package com.dxhy.order.consumer.openapi.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.openapi.service.IValidateTaxEquipmentInfo;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;
import com.dxhy.order.utils.CheckParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 税控设备校验
 *
 * @author ZSC-DXHY
 */
@Service
@Slf4j
public class ValidateTaxEquipmentInfoImpl implements IValidateTaxEquipmentInfo {
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    /**
     * 税控设备信息同步校验
     *
     * @param sksbxxtbReq
     * @return
     */
    @Override
    public Map<String, String> checkSyncTaxEquipmentInfo(SKSBXXTB_REQ sksbxxtbReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        String successCode = OrderInfoContentEnum.SUCCESS.getKey();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);
        
        //入参对象是否为null
        if (ObjectUtil.isEmpty(sksbxxtbReq)) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193001);
        }
        
        if (ObjectUtil.isEmpty(sksbxxtbReq)) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193001);
        }
        
        //销货方纳税人识别号
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193002,
                OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193003,
                OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193004,
                sksbxxtbReq.getXHFSBH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        //销货方纳税人名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193005,
                sksbxxtbReq.getXHFMC());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 税控设备代码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193006,
                sksbxxtbReq.getSKSBDM());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (StringUtils.isNotBlank(sksbxxtbReq.getSKSBDM())) {
            if (!(OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(sksbxxtbReq.getSKSBDM())
                    || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(sksbxxtbReq.getSKSBDM()))) {
                return validateInterfaceOrder.generateErrorMap("", "",
                        OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193007);
            }
            
        }
        
        /**
         * 税控设备型号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193008,
                sksbxxtbReq.getSKSBXH());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 关联时间
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193009,
                sksbxxtbReq.getGLSJ());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 操作类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193010,
                sksbxxtbReq.getCZLX());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //操作类型
        if (!StringUtils.equals(OrderInfoEnum.INTERFACE_CZLX_0.getKey(), sksbxxtbReq.getCZLX())
                && !StringUtils.equals(OrderInfoEnum.INTERFACE_CZLX_1.getKey(), sksbxxtbReq.getCZLX())
                && !StringUtils.equals(OrderInfoEnum.INTERFACE_CZLX_2.getKey(), sksbxxtbReq.getCZLX())) {
            return validateInterfaceOrder.generateErrorMap("", "",
                    OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193012);
        }
    
        /**
         * 备注
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193011,
                sksbxxtbReq.getBZ());
        if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        
        return checkResultMap;
        
    }
}
