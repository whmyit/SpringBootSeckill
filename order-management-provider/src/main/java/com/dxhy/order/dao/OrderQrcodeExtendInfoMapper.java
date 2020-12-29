package com.dxhy.order.dao;

import com.dxhy.order.model.OrderQrcodeExtendInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 动态码数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:47
 */
public interface OrderQrcodeExtendInfoMapper {
    
    /**
     * 添加动态码数据
     *
     * @param record
     * @return
     */
    int insertQrCodeInfo(OrderQrcodeExtendInfo record);
    
    /**
     * 查询动态码数据
     *
     * @param id
     * @param shList
     * @return
     */
    OrderQrcodeExtendInfo selectByPrimaryKey(@Param(value = "id") String id, @Param("shList") List<String> shList);
    
    /**
     * 更新动态码数据
     *
     * @param record
     * @param shList
     * @return
     */
    int updateByPrimaryKeySelective(@Param("qrCodeExt") OrderQrcodeExtendInfo record, @Param("shList") List<String> shList);
    
    /**
     * 查询动态码数据
     *
     * @param record
     * @param shList
     * @return
     */
    OrderQrcodeExtendInfo selectByOrderQrcodeExtendInfo(@Param("qrCodeExt") OrderQrcodeExtendInfo record, @Param("shList") List<String> shList);
    
    /**
     * 定时任务查询异常扫开数据,
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<OrderQrcodeExtendInfo> selectOrderQrcodeExtendInfoForTask(@Param("paraMap") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询动态码列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map> selectDynamicQrCodeList(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询动态码信息
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    Map<String, Object> queryEwmDetailByFpqqlsh(@Param(value = "fpqqlsh") String fpqqlsh, @Param("shList") List<String> shList);
    
    /**
     * 查询动态码明细
     *
     * @param qrcodeId
     * @param shList
     * @return
     */
    Map<String, Object> queryQrcodeAndInvoiceDetail(@Param(value = "qrcodeId") String qrcodeId, @Param("shList") List<String> shList);
    
    /**
     * 更新动态码信息
     *
     * @param qrcodeInfo
     * @param shList
     * @return
     */
    int updateByOrderIdSelective(@Param(value = "qrCodeExt") OrderQrcodeExtendInfo qrcodeInfo, @Param("shList") List<String> shList);
}
