package com.dxhy.order.dao;

import com.dxhy.order.model.InvalidInvoiceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 发票作废数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:22
 */
public interface InvalidInvoiceInfoMapper {
    /**
     * 作废插入
     *
     * @param record
     * @return
     */
    int insertValidInvoice(InvalidInvoiceInfo record);
    
    /**
     * 作废列表查询
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<Map> selectByMap(@Param("map") Map paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询作废发票数据
     *
     * @param record
     * @param shList
     * @return
     */
    InvalidInvoiceInfo selectByInvalidInvoiceInfo(@Param("invalid") InvalidInvoiceInfo record, @Param("shList") List<String> shList);
    
    /**
     * 查询作废列表
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<InvalidInvoiceInfo> queryKbInvoiceList(@Param("map") Map<String, Object> paramMap, @Param("shList") List<String> shList);
    
    /**
     * 查询待作废的发票信息
     *
     * @param zfpch
     * @param nsrsbh
     * @return
     */
    List<InvalidInvoiceInfo> selectInvalidInvoiceInfo(@Param("zfpch") String zfpch, @Param("shList") List<String> nsrsbh);
    
    /**
     * 更新方格作废信息
     *
     * @param record
     * @param shList
     * @return
     */
    int updateFgInvalidInvoice(@Param("invalid") InvalidInvoiceInfo record, @Param("shList") List<String> shList);
}
