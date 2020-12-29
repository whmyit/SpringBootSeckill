package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceTaxRatePO;
import com.dxhy.order.model.InvoiceTaxRateRequestInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 税率统计数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:15
 */
public interface InvoiceTaxRateRequestInfoMapper {
    
    /**
     * 新增设汇率统计数据
     *
     * @param record
     * @return
     */
    int insertTaxRateRequestInfo(InvoiceTaxRateRequestInfo record);
    
    /**
     * 更新税率统计
     *
     * @param record
     * @return
     */
    int updateInvoiceTaxRateById(InvoiceTaxRateRequestInfo record);
    
    /**
     * 税率统计
     *
     * @param nsrsbh
     * @param hzrqList
     * @return
     */
    List<InvoiceTaxRatePO> selectTaxRateStatistics(@Param("nsrsbh") String nsrsbh, @Param("hzrqList") List<String> hzrqList);
    
    /**
     * 条件查询汇总数据
     *
     * @param nsrsbh
     * @param hzrq
     * @param sl
     * @param kplx
     * @param fpzldm
     * @return
     */
    InvoiceTaxRateRequestInfo selectTaxRateInfo(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq, @Param("sl") String sl, @Param("kplx") String kplx, @Param("fpzldm") String fpzldm);
    
    /**
     * 获取税率汇总数据，
     *
     * @param nsrsbh
     * @param hzrq
     * @return
     */
    List<Map> selectSummaryTaxRateData(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq);
    
    /**
     * 获取税率汇总状态数据
     *
     * @param nsrsbh
     * @param hzrq
     * @return
     */
    List<Map> getSummaryTaxRateState(@Param("nsrsbh") String nsrsbh, @Param("hzrq") String hzrq);
}
