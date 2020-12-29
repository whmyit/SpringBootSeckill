package com.dxhy.order.dao;

import com.dxhy.order.model.QuickResponseCodeInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 静态码主表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:56
 */
public interface QuickResponseCodeInfoMapper {
    
    /**
     * 插入静态码
     *
     * @param record
     * @return
     */
    int insertSelective(QuickResponseCodeInfo record);
    
    /**
     * 查询静态码数据
     *
     * @param id
     * @param shList
     * @return
     */
    QuickResponseCodeInfo selectQuickResponseCodeById(@Param(value = "id") String id, @Param("shList") List<String> shList);
    
    /**
     * 更新静态码
     *
     * @param record
     * @param shList
     * @return
     */
    int updateQrCodeInfo(@Param("qrCode") QuickResponseCodeInfo record, @Param("shList") List<String> shList);
    
    /**
     * 查询静态码数据
     *
     * @param map
     * @param shList
     * @return
     */
    List<Map> selectQrCodeList(@Param("map") Map map, @Param("shList") List<String> shList);
    
    /**
     * 查询静态码数据
     *
     * @param tqm
     * @param shList
     * @param type
     * @return
     */
    QuickResponseCodeInfo queryQrCodeDetailByTqm(@Param(value = "tqm") String tqm, @Param("shList") List<String> shList, @Param(value = "type") String type);
    
}
