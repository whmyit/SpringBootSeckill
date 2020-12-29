package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.*;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.JsonUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
/**
 * 订单发票业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 19:39
 */
@Service
@Slf4j
public class OrderInvoiceInfoServiceImpl implements ApiOrderInvoiceInfoService {
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    @Resource
    private ApiOrderItemInfoService apiOrderItemInfoService;
    @Resource
    private ApiOrderInfoService apiOrderInfoService;
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private InvoiceDataServiceImpl invoiceDataServiceImpl;
    @Resource
    private InvoiceBatchRequestMapper invoiceBatchRequestMapper;
    @Resource
    private InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;

    @Override
    public List<InvoiceCount> getCountOfMoreMonth(Date starttime, Date endtime, List<String> shList, String sld, String timeFormatFlag, String timeFlag, String sldFlag, String kplxFlag) {
        log.info("发票统计入参开始时间：{}，结束时间：{},shList:{},sld:{},timeFormatFlag:{},timeFlag:{},sldFlag:{},kplxFlag:{}",
                starttime, endtime, shList, sld, timeFormatFlag, timeFlag, sldFlag, kplxFlag);
        //TODO 下次加上redis
        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoMapper.getCountOfMoreMonth(starttime, endtime, shList, sld, timeFormatFlag, timeFlag, sldFlag, kplxFlag);
        if (countOfMoreMonth == null || countOfMoreMonth.size() == 0) {
            return new ArrayList<>();
        }
        log.info("发票统计查询结果：{}", JsonUtils.getInstance().toJsonString(countOfMoreMonth));
        return countOfMoreMonth;
    }
    
    /**
     * 作用：
     * 1.
     *
     * @param starttime
     * @param endtime
     * @param shList
     * @param timeFlag
     * @param fpzldmFlag
     * @return
     */
    @Override
    public List<InvoiceCount> getMoneyOfMoreMonth(Date starttime, Date endtime, List<String> shList, String timeFlag, String fpzldmFlag) {
        log.info("发票统计入参开始时间：{}，结束时间：{},shList:{},timeFlag:{},fpzldmFlag:{}", starttime, endtime, shList, timeFlag, fpzldmFlag);
        List<InvoiceCount> dataOfThisMonth = orderInvoiceInfoMapper.getDataOfMoreMonth(starttime, endtime, shList, timeFlag, fpzldmFlag);
        log.info("发票统计查询结果：{}", JsonUtils.getInstance().toJsonString(dataOfThisMonth));
        return dataOfThisMonth;
    }
    
    
    @Override
    public OrderInvoiceInfo selectOrderInvoiceInfoByFpdmAndFphm(String fpDm, String fpHm, List<String> shList) {
        return orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(fpDm, fpHm, shList);
    }
    
    @Override
    public OrderInvoiceInfo selectOrderInvoiceInfo(OrderInvoiceInfo orderInvoiceInfo, List<String> shList) {
        return orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo, shList);
    }
    
    
    @Override
    public OrderInvoiceInfo selectOrderInvoiceInfoByFpqqlsh(String fpqqlsh, List<String> shList) {
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setFpqqlsh(fpqqlsh);
        return orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo, shList);
        
    }

    @Override
    public PageUtils selectInvoiceByOrder(Map map, List<String> shList) {
        log.info("查询纸电票传入参数为{}", JsonUtils.getInstance().toJsonString(map));
        int pageSize = (Integer) map.get("pageSize");
        int currPage = (Integer) map.get("currPage");
        //这里前端从1开始需要进行-1操作
        //		currPage=currPage-1;
        PageHelper.startPage(currPage, pageSize);
        List<OrderInvoiceInfo> list = orderInvoiceInfoMapper.selectInvoiceByOrder(map, shList);
        PageInfo<OrderInvoiceInfo> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }
    
    @Override
    public CommonOrderInvoiceAndOrderMxInfo selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(String fpDm, String fpHm, List<String> shList) {
        CommonOrderInvoiceAndOrderMxInfo co = new CommonOrderInvoiceAndOrderMxInfo();
        OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
        orderInvoiceInfo1.setFpdm(fpDm);
        orderInvoiceInfo1.setFphm(fpHm);
        OrderInvoiceInfo oderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
        if (oderInvoiceInfo != null) {
            OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(oderInvoiceInfo.getOrderInfoId(), shList);
            List<OrderItemInfo> list = apiOrderItemInfoService.selectOrderItemInfoByOrderId(oderInvoiceInfo.getOrderInfoId(), shList);
            co.setOrderInvoiceInfo(oderInvoiceInfo);
            co.setOrderItemList(list);
            co.setOrderInfo(orderInfo);
            return co;
        }
        return null;
    }
    
    /**
     * 根据订单id获取订单数据
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderInvoiceInfo selectInvoiceListByOrderId(String orderId, List<String> shList) {
        OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
        orderInvoiceInfo1.setOrderInfoId(orderId);
        return orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
    }
    
    
    @Override
    public PageUtils selectRedAndInvoiceByMap(Map map, List<String> shList) {
        log.info("MAP===={}", JsonUtils.getInstance().toJsonString(map));
        int pageSize = Integer.parseInt(map.get("pageSize").toString());
        int currPage = Integer.parseInt(map.get("currPage").toString());
        //这里前端从1开始需要进行-1操作
        //		currPage=currPage-1;
        PageHelper.startPage(currPage, pageSize);
        List<OrderInvoiceDetail> list = orderInvoiceInfoMapper.selectRedAndInvoiceBymap(map, shList);
        log.info("发票明细出参为{}", list == null ? 0 : list.size());
        PageInfo<OrderInvoiceDetail> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }

    @Override
    public int updateDyztById(String id, String dyzt, String invoiceType, List<String> shList) {
        log.debug("发票打印状态修改,入参:invoiceId{},dyzt:{},invoiceType{}", id, dyzt, invoiceType);
        if (StringUtils.isNotBlank(dyzt) && StringUtils.isNotBlank(id) && StringUtils.isNotBlank(invoiceType)) {
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(invoiceType)) {
                OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
                orderInvoiceInfo.setId(id);
                orderInvoiceInfo.setDyzt(dyzt);
                return orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
            }
        }
        return 0;
        
    }
    
    @Override
    public int updateOrderInvoiceInfoByInvoiceId(OrderInvoiceInfo orderInvoiceInfo, List<String> shList) {
        return orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByList(List<OrderInvoiceInfo> insertList) {
        int i = 0;
        for (OrderInvoiceInfo orderInvoiceInfo : insertList) {
            int insertSelective = orderInvoiceInfoMapper.insertOrderInvoiceInfo(orderInvoiceInfo);
            if (insertSelective <= 0) {
                return insertSelective;
            }
            i++;
        }
        return i;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrderInvoiceInfoByList(List<OrderInvoiceInfo> updateList, List<String> shList) {
        int i = 0;
        for (OrderInvoiceInfo orderInvoiceInfo : updateList) {
            int updateByList = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
            if (updateByList <= 0) {
                return updateByList;
            }
            i++;
        }
        return i;
    }
    
    @Override
    public Map<String, Object> queryCountByMap(Map<String, Object> map, List<String> shList) {
        return orderInvoiceInfoMapper.queryCountByMap(map, shList);
    }
    
    @Override
    public void dealRedInvoice(OrderInvoiceInfo orderInvoiceInfo, String kpzt, List<String> shList) {
        invoiceDataServiceImpl.dealRedInvoice(orderInvoiceInfo, kpzt, shList);
    }
    
    /**
     * 分页导出明细数据
     */
    @Override
    public PageUtils exportAllInvoiceDetailByPage(Map map, List<String> shList) {
        
        int currPage = Integer.parseInt(String.valueOf(map.get("currentPage")));
        int pageSize = Integer.parseInt(String.valueOf(map.get("pageSize")));
        
        log.info("MAP===={}", JsonUtils.getInstance().toJsonString(map));
        Long startTime = Calendar.getInstance().getTimeInMillis();
        if (map.size() <= 0) {
            log.error("====map为空");
            return null;
        }
    
        PageHelper.startPage(currPage, pageSize);
    
        log.info("发票明细excel导出入参：{}", JsonUtils.getInstance().toJsonString(map));
        List<OrderInvoiceDetail> orderInvoiceDetails = orderInvoiceInfoMapper.selectRedAndInvoiceBymap(map, shList);
        PageInfo<OrderInvoiceDetail> pageInfo = new PageInfo<>(orderInvoiceDetails);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
    
        //获取明细
        List<String> orderInfoIdList = new ArrayList<>();
        for (OrderInvoiceDetail orderInvoiceDetail : orderInvoiceDetails) {
            orderInfoIdList.add(orderInvoiceDetail.getOrder_info_id());
        }
    
        List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(orderInfoIdList)) {
            orderItemInfoList = orderItemInfoMapper.selectAllByOrderId(orderInfoIdList, shList);
        }
        //明细信息和发票信息组合
        Long startTime1 = Calendar.getInstance().getTimeInMillis();
        Map<String, List<OrderItemInfo>> paramMap = new HashMap<>(5);
        for (OrderItemInfo orderItem : orderItemInfoList) {
            if (paramMap.get(orderItem.getOrderInfoId()) == null) {
                List<OrderItemInfo> list = new ArrayList<>();
                list.add(orderItem);
                paramMap.put(orderItem.getOrderInfoId(), list);
            } else {
                List<OrderItemInfo> list = paramMap.get(orderItem.getOrderInfoId());
                list.add(orderItem);
                paramMap.put(orderItem.getOrderInfoId(), list);
            }
		}
		//数据信息转换
		for(OrderInvoiceDetail detail : orderInvoiceDetails){
			detail.setOrderItemList(paramMap.get(detail.getOrder_info_id()));
			//发票种类代码展示信息转换
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(detail.getFpzlDm())) {
            	detail.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue());
            } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(detail.getFpzlDm())){
            	detail.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getValue());
            } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(detail.getFpzlDm())) {
                detail.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getValue());
            } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey().equals(detail.getFpzlDm())) {
                detail.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_41.getValue());
            }
            //作废标志展示信息转换
            if(OrderInfoEnum.INVALID_INVOICE_0.getKey().equals(detail.getZfbz())) {
                detail.setZfbz("未作废");
            }
            if(OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(detail.getZfbz())){
            	detail.setZfbz("已作废");
            }
            //转换开票类型
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(detail.getKplx())) {
                detail.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getValue());
            } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(detail.getKplx())) {
                detail.setKplx(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getValue());
            }
        }
        
        Long endTime1 = Calendar.getInstance().getTimeInMillis();
        log.info("发票明细数据组装耗时：{}", endTime1 - startTime1);
        Long endTime = Calendar.getInstance().getTimeInMillis();
        log.info("发票明细导出用时{},条数{}", endTime - startTime, orderInvoiceDetails.size());
        return page;
    }
    
    @Override
    public List<OrderInvoiceInfo> selectInvoiceInfoByPushStatus(Map paramMap, List<String> shList) {
        return orderInvoiceInfoMapper.selectInvoiceInfoByPushStatus(paramMap, shList);
    }
    
    @Override
    public PageUtils getMoneyOfMoreMonth(Date start, Date end, List<String> shList, String timeFlag,
                                         String fpzldmFlag, String pageSize, String currPage) {
        log.info("发票统计入参开始时间：{}，结束时间：{},nsrsbh:{},timeFlag:{},fpzldmFlag:{}", start, end,
                JsonUtils.getInstance().toJsonString(shList), timeFlag, fpzldmFlag);
        int size = Integer.parseInt(pageSize);
        int curr = Integer.parseInt(currPage);
        // 这里前端从1开始需要进行-1操作
        // currPage=currPage-1;
        PageHelper.startPage(curr, size);
        List<InvoiceCount> dataOfThisMonth = orderInvoiceInfoMapper.getDataOfMoreMonth(start, end, shList, timeFlag,
                fpzldmFlag);
        PageInfo<InvoiceCount> pageInfo = new PageInfo<>(dataOfThisMonth);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
        log.info("发票统计查询结果：{}", JsonUtils.getInstance().toJsonString(dataOfThisMonth));
        return page;
    }
    
    @Override
    public void updateMongoDbIdByFpdmAndFphm(String fpqqlsh, String mongodbId, List<String> shList) {
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setFpqqlsh(fpqqlsh);
        orderInvoiceInfo.setMongodbId(mongodbId);
        orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
    }
    
    @Override
    public OrderInvoiceInfo findMongoDbIdByFpdmAndFphm(String fpdm, String fphm, List<String> shList) {
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setFpdm(fpdm);
        orderInvoiceInfo.setFphm(fphm);
        return orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo, shList);
    }


}

