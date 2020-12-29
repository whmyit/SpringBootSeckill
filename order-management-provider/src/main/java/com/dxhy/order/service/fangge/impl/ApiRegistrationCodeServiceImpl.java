package com.dxhy.order.service.fangge.impl;


import com.dxhy.order.api.ApiRegistrationCodeService;
import com.dxhy.order.dao.RegistrationCodeMapper;
import com.dxhy.order.model.RegistrationCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 注册码业务实现层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:37
 */
@Slf4j
@Service
public class ApiRegistrationCodeServiceImpl implements ApiRegistrationCodeService {
    
    
    @Resource
    private RegistrationCodeMapper registrationCodeMapper;
    
    /**
     * 根据纳税人识别号获取注册码
     */
    @Override
    public List<RegistrationCode> getRegistrationCodeByNsrsbh(String nsrsbh) {
        return registrationCodeMapper.getRegistrationCodeByNsrsbh(nsrsbh);
    }
    
    @Override
    public RegistrationCode getRegistrationCodeByNsrsbhAndJqbh(String nsrsbh, String jqbh) {
        return registrationCodeMapper.getRegistrationCodeByNsrsbhAndJqbh(nsrsbh, jqbh);
    }
    
    @Override
    public void saveRegistrationCode(RegistrationCode registrationCode) {
        log.info("保存注册码信息");
        registrationCodeMapper.insertRegistrationCode(registrationCode);
    }
    
    @Override
    public void updateRegistrationCode(RegistrationCode code) {
        registrationCodeMapper.updateRegistrationCodeById(code);
    }
    
    
}
