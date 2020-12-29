package com.dxhy.order.api;

import com.dxhy.order.model.vo.QsRequestVo;

import java.util.List;
import java.util.Map;

/**
 * 发票汇总统计 （全税管理平台与销项接口对接）
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 9:25
 */
public interface ApiInvoiceSummaryStatisticsService {
    /**
     * 销项汇总统计
     *
     * @param vo
     * @return
     */
    String getInvoiceSummaryStatistics(QsRequestVo vo);
    
    /**
     * 获取数据的汇总状态
     *
     * @param vo
     * @return
     */
    List<Map> getSummaryState(QsRequestVo vo);
    
    /**
     * 获取汇总数据
     *
     * @param vo
     * @return
     */
    List<Map> getSummaryData(QsRequestVo vo);
}
