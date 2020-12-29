package com.dxhy.order.api;


import com.dxhy.order.model.CommonOrderInvoiceAndOrderMxInfo;
import com.dxhy.order.model.InvoiceCount;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.PageUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单发票表接口
 *
 * @author ZSC-DXHY
 */
public interface ApiOrderInvoiceInfoService {
    
    /**
     * 按照月份统计，分开统计每月
     *
     * @param starttime
     * @param endtime
     * @param nsrsbh
     * @param timeFlag
     * @param fpzldmFlag
     * @return
     */
    List<InvoiceCount> getMoneyOfMoreMonth(Date starttime, Date endtime, List<String> nsrsbh,
                                           String timeFlag, String fpzldmFlag);
    
    /**
     * 按照月份统计，分开统计每月,可以根据开票类型
     *
     * @param starttime
     * @param endtime
     * @param nsrsbh
     * @param sld
     * @param timeFormat
     * @param timeFlag
     * @param sldFlag
     * @param kplxFlag
     * @return
     */
    List<InvoiceCount> getCountOfMoreMonth(Date starttime, Date endtime, List<String> nsrsbh, String sld,
                                           String timeFormat, String timeFlag, String sldFlag, String kplxFlag);
    
    /**
     * 根据发票号码代码查询
     *
     * @param fpDm
     * @param fpHm
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectOrderInvoiceInfoByFpdmAndFphm(String fpDm, String fpHm, List<String> shList);
    
    /**
     * 根据主键查询发票
     *
     * @param orderInvoiceInfo
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectOrderInvoiceInfo(OrderInvoiceInfo orderInvoiceInfo, List<String> shList);
    
    /**
     * 根据发票请求流水号查询发票表信息
     *
     * @param fpqqlsh
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectOrderInvoiceInfoByFpqqlsh(String fpqqlsh, List<String> shList);
    
    /**
     * 根据OrderProccessId和发票号码代码 开票员和开票类型查询发票
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectInvoiceByOrder(Map map, List<String> shList);
    
    
    /**
     * 根据发票号码代码查询发票和明细
     *
     * @param fpDm
     * @param fpHm
     * @param shList
     * @return
     */
    CommonOrderInvoiceAndOrderMxInfo selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(String fpDm, String fpHm, List<String> shList);
    
    /**
     * 根据orderId查询发票信息
     *
     * @param orderId
     * @param shList
     * @return
     */
    OrderInvoiceInfo selectInvoiceListByOrderId(String orderId, List<String> shList);
    
    /**
     * 查询所有发票
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils selectRedAndInvoiceByMap(Map map, List<String> shList);
    
    /**
     * invoiceType 0 是正常发票 1 是作废发票
     *
     * @param id
     * @param dyzt
     * @param invoiceType
     * @param shList
     * @return
     */
    int updateDyztById(String id, String dyzt, String invoiceType, List<String> shList);
    
    /**
     * 根据条件分页查询
     *
     * @param map
     * @param shList
     * @return
     */
    PageUtils exportAllInvoiceDetailByPage(Map map, List<String> shList);
    
    
    /**
     * 根据发票id更新发票信息
     *
     * @param orderInvoiceInfo
     * @param shList
     * @return
     */
    int updateOrderInvoiceInfoByInvoiceId(OrderInvoiceInfo orderInvoiceInfo, List<String> shList);
    
    /**
     * 根据list插入orderInvoiceInfo
     *
     * @param insertList
     * @return
     */
    int insertByList(List<OrderInvoiceInfo> insertList);
    
    /**
     * 根据list更新orderInvoiceInfo
     *
     * @param updateList
     * @param shList
     * @return
     */
    int updateOrderInvoiceInfoByList(List<OrderInvoiceInfo> updateList, List<String> shList);
    
    /**
     * 根据条件统计数量金额
     *
     * @param map
     * @param shList
     * @return Map
     */
    Map<String, Object> queryCountByMap(Map<String, Object> map, List<String> shList);
    
    /**
     * 处理红字发票数据
     *
     * @param orderInvoiceInfo
     * @param kpzt
     * @param shList
     */
    void dealRedInvoice(OrderInvoiceInfo orderInvoiceInfo, String kpzt, List<String> shList);
    
    /**
     * 查询发票数据
     *
     * @param paramMap
     * @param shList
     * @return
     */
    List<OrderInvoiceInfo> selectInvoiceInfoByPushStatus(Map paramMap, List<String> shList);
    
    /**
     * 分页月度统计环比
     *
     * @param start
     * @param end
     * @param list
     * @param timeFlag
     * @param fpzldmFlag
     * @param pageSize
     * @param currPage
     * @return
     */
    PageUtils getMoneyOfMoreMonth(Date start, Date end, List<String> list, String timeFlag, String fpzldmFlag, String pageSize,
                                  String currPage);
    
    /**
     * 根据发票代码和发票号码更新'订单和发票关系表'的mongodb_id
     *
     * @param fpqqlsh
     * @param mongodbId
     * @param shList
     */
    void updateMongoDbIdByFpdmAndFphm(String fpqqlsh, String mongodbId, List<String> shList);
    
    /**
     * 根据发票代码和发票号码查询mongodb_id
     *
     * @param fpdm 发票代码
     * @param fphm 发票号码
     * @param shList
     * @return java.lang.String
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/12
     */
    OrderInvoiceInfo findMongoDbIdByFpdmAndFphm(String fpdm, String fphm, List<String> shList);
}

