package com.dxhy.order.dao;

import com.dxhy.order.model.SalerWarning;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 余票预警数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 13:36
 */
public interface SalerWarningMapper {
    
    /**
     * 新增预警信息
     *
     * @param record
     * @return
     */
    int insertSelective(SalerWarning record);
    
    /**
     * 查询预警信息
     *
     * @param xhfNsrsbh
     * @param createId
     * @return
     */
    List<SalerWarning> selectSalerWaringByNsrsbh(@Param(value = "xhfNsrsbh") String xhfNsrsbh, @Param(value = "createId") String createId);
    
    /**
     * 更新预警信息
     *
     * @param record
     * @return
     */
    int updateByTaxCode(SalerWarning record);
}
