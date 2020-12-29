package com.dxhy.order.dao;

import com.dxhy.order.model.RegistrationCode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 注册码数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:58
 */
public interface RegistrationCodeMapper {
    
    /**
     * 新增注册码数据
     *
     * @param record
     * @return
     */
    int insertRegistrationCode(RegistrationCode record);
    
    /**
     * 更新注册码数据
     *
     * @param record
     * @return
     */
    int updateRegistrationCodeById(RegistrationCode record);
    
    /**
     * 根据纳税人识别号获取注册信息
     *
     * @param xhfNsrsbh
     * @return
     */
    List<RegistrationCode> getRegistrationCodeByNsrsbh(@Param("xhfNsrsbh") String xhfNsrsbh);
    
    /**
     * 根据纳税人识别号和机器编号查询
     *
     * @param xhfNsrsbh
     * @param jqbh
     * @return
     */
    RegistrationCode getRegistrationCodeByNsrsbhAndJqbh(@Param("xhfNsrsbh") String xhfNsrsbh, @Param("jqbh") String jqbh);
}
