package com.dxhy.order.consumer.modules.invoice.service;


import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.vo.*;

import java.util.Date;
import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-04-11 16:12:17
 * @Describe 统计发票开具数据
 */
public interface InvoiceCountService {
    /**
     * 按照时间统计
     *
     * @param start
     * @param end
     * @param list
     * @param timeFlag
     * @param pageSize
     * @param currPage
     * @return
     */
	PageUtils getCountByTime(Date start, Date end, List<String> list, String timeFlag, String pageSize,
			String currPage);
    
    /**
     * 按照受理点统计
     * @param starttime
     * @param endtime
     * @param nsrsbh
     * @param sld
     * @return
     */
    List<CountBySldVO> getCountOfInvoiceBySld(Date starttime, Date endtime, List<String> nsrsbh, String sld);
    
    /**
     * 查询最近六个月的开票量
     * @param nsrsbh
     * @return
     */
    List<InvoiceCountByTimeVO> getSixMonthOfInvoice(List<String> nsrsbh);


    /**
     * 查询最近六个月的合计金额
     * @param nsrsbh
     * @return
     */
    List<CountHjjeVO> getSixMonthOfInvoiceHjje(List<String> nsrsbh);
    
    /**
     * 查询最近六个月的合计税额
     *
     * @param nsrsbh
     * @return
     */
    List<CountHjseVO> getSixMonthOfInvoiceHjse(List<String> nsrsbh);
    
    /**
     * 发票余量统计
     *
     * @param countToB
     * @param terminalCode
     * @param nsrmc
     * @return
     */
    R getFpyl(CountToB countToB, String terminalCode, String nsrmc);
    
}
