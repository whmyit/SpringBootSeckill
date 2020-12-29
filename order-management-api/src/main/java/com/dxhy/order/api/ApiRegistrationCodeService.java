package com.dxhy.order.api;

import com.dxhy.order.model.RegistrationCode;

import java.util.List;

/**
 * @Description: 获取注册码
 * @Author:xueanna
 * @Date:2019/6/26
 */
public interface ApiRegistrationCodeService {
    
    /**
     * 根据税号查询注册二维码信息
     *
     * @param nsrsbh
     * @return
     */
    List<RegistrationCode> getRegistrationCodeByNsrsbh(String nsrsbh);
    
    /**
     * 根据税号和机器编号获取注册信息
     *
     * @param nsrsbh
     * @param jqbh
     * @return
     */
    RegistrationCode getRegistrationCodeByNsrsbhAndJqbh(String nsrsbh, String jqbh);
    
    /**
     * 保存
     *
     * @param registrationCode
     */
    void saveRegistrationCode(RegistrationCode registrationCode);
    
    /**
     * 更新注册信息
     *
     * @param code
     */
    void updateRegistrationCode(RegistrationCode code);
}
