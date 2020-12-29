package com.dxhy.order.dao;

import com.dxhy.order.model.InvoiceCount;
import com.dxhy.order.model.OrderInvoiceDetail;
import com.dxhy.order.model.OrderInvoiceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单发票表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:39
 */
public interface OrderInvoiceInfoMapper {
    
    /**
     * 插入订单发票数据
     *
     * @param record
     * @return
     */
    int insertOrderInvoiceInfo(OrderInvoiceInfo record);
    
    /**
     * 根据开票流水号更新发票状态
     *
     * @param record
     * @param shList
     * @return
     */
    int updateInvoiceStatusByKplsh(@Param("orderInvoiceInfo") OrderInvoiceInfo record, @Param("shList") List<String> shList);
    
    /**
     * 根据id更新发票数据
     *
     * @param record
     * @param shList
     * @return
     */
    int updateOrderInvoiceInfoByInvoiceId(@Param("orderInvoiceInfo") OrderInvoiceInfo record, @Param("shList") List<String> shList);
    
    /**
     * 根据发票代码号码查询数据
     *
     * @param fpdm
     * @param fphm
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectOrderInvoiceInfoByFpdmAndFphm(@Param("fpdm") String fpdm, @Param("fphm") String fphm, @Param("shList") List<String> shList);
    
    /**
     * 查询发票表
     *
     * @param orderInvoiceInfo
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectOrderInvoiceInfo(@Param("orderInvoiceInfo") OrderInvoiceInfo orderInvoiceInfo, @Param("shList") List<String> shList);
    
    /**
     * 查询发票表
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderInvoiceInfo> selectInvoiceByOrder(@Param("map") Map map, @Param("shList") List<String> shList);
    
    /**
     * 根据条件查询发票和红票
     *
     * @param map
     * @param shList
     * @return
     */
    List<OrderInvoiceDetail> selectRedAndInvoiceBymap(@Param("map") Map map, @Param("shList") List<String> shList);
    
    /**
     * 根据条件统计总金额数量
     *
     * @param map
     * @param shList
     * @return
     */
    Map<String, Object> queryCountByMap(@Param("map") Map<String, Object> map, @Param("shList") List<String> shList);
    
    /**
     * 根据开票状态 推送状态 和时间查询发票
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<OrderInvoiceInfo> selectInvoiceInfoByPushStatus(@Param("paramMap") Map paramMap, @Param("shList") List<String> shList);
    
    /**
     * 根据推送状态查询发票
     *
     * @param kpzt
     * @param pushStatus
     * @param startTime
     * @param endTime
     * @param fpzldm
     * @param shList
     * @return
     */
    List<OrderInvoiceInfo> selectInvoiceInfoByEmailPushStatus(@Param("kpztList") List<String> kpzt, @Param("emailPushStatus") List<String> pushStatus, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("fpzldm") String fpzldm, @Param("shList") List<String> shList);
    
    /**
     * 统计开票量，以及三种金额
     *
     * @param startTime
     * @param endTime
     * @param shList
     * @param flagTime
     * @param flagFpzldm
     * @return
     */
    List<InvoiceCount> getDataOfMoreMonth(@Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                          @Param("shList") List<String> shList, @Param("flagTime") String flagTime,
                                          @Param("flagFPZLDM") String flagFpzldm);
    
    
    /**
     * 统计开票量
     *
     * @param startTime
     * @param endTime
     * @param shList
     * @param sld
     * @param timeFormatFlag
     * @param timeFlag
     * @param sldFlag
     * @param kplxFlag
     * @return
     */
    List<InvoiceCount> getCountOfMoreMonth(@Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                           @Param("shList") List<String> shList, @Param("sld") String sld,
                                           @Param("timeFormatFlag") String timeFormatFlag, @Param("timeFlag") String timeFlag,
                                           @Param("sldFlag") String sldFlag, @Param("kplxFlag") String kplxFlag);
    
}
