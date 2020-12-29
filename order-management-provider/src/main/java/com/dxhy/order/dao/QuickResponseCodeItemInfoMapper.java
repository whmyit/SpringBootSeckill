package com.dxhy.order.dao;

import com.dxhy.order.model.QuickResponseCodeItemInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 静态码明细数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:57
 */
public interface QuickResponseCodeItemInfoMapper {
    
    /**
     * 插叙静态码明细数据
     *
     * @param record
     * @return
     */
    int insertSelective(QuickResponseCodeItemInfo record);
    
    /**
     * 查询静态码明细数据
     *
     * @param qrcodeId
     * @param shList
     * @return
     */
    List<QuickResponseCodeItemInfo> selectByQrcodeId(@Param(value = "qrcodeId") String qrcodeId, @Param("shList") List<String> shList);
    
    /**
     * 删除静态码明细数据
     *
     * @param qrId
     * @param shList
     * @return
     */
    int deleteByQrId(@Param(value = "qrId") String qrId, @Param("shList") List<String> shList);
}
