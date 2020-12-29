package com.dxhy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiHistoryDataPdfService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiOrderQrcodeExtendService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.*;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.utils.DistributedKeyMaker;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 订单发票通用服务接口
 *
 * @author ZSC-DXHY
 */
@Service
@Slf4j
public class ApiInvoiceCommonServiceImpl implements ApiInvoiceCommonService{
    
    private static final String LOGGER_MSG = "(订单接口通用业务类)";

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    @Resource
    private OrderProcessInfoExtMapper orderProcessInfoExtMapper;
    @Resource
    private InvoiceBatchRequestMapper invoiceBatchRequestMapper;
    @Resource
    private InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;
    @Resource
    private InvalidInvoiceInfoMapper invalidInvoiceInfoMapper;
    @Resource
    private OrderBatchRequestMapper orderBatchRequestMapper;
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    @Resource
    private OrderOriginExtendInfoMapper orderOriginExtendInfoMapper;
    @Resource
    private SpecialInvoiceReversalDao specialInvoiceReversalDao;
    @Resource
    private QuickResponseCodeInfoMapper quickResponseCodeInfoMapper;
    @Resource
    private OrderQrcodeExtendInfoMapper orderQrcodeExtendInfoMapper;
    @Resource
    private ApiHistoryDataPdfService historyDataPdfService;
    @Resource
    private ApiOrderQrcodeExtendService orderQrcodeExtendService;



    private int insertInvoiceBatchRequest(InvoiceBatchRequest invoiceBatchRequest) {
        return invoiceBatchRequestMapper.insertInvoiceBatchRequest(invoiceBatchRequest);
    }
    
    @Override
    public String getGenerateShotKey() {
        return DistributedKeyMaker.generateShotKey();
    }
    
    @Override
    public OrderInvoiceInfo selectByYfp(String yfpDm, String yfpHm, List<String> shList) {
        if (StringUtils.isNotEmpty(yfpDm) && StringUtils.isNotEmpty(yfpHm)) {
            return orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(yfpDm, yfpHm, shList);
        }
        return null;
    }
    
    @Override
    public OrderInfo selectByOrderInvoiceId(String orderInfoId, List<String> shList) {
        return orderInfoMapper.selectOrderInfoByOrderId(orderInfoId, shList);
    }
    
    @Override
    public List<OrderItemInfo> selectOrderItemByOrderInfoId(String id, List<String> shList) {
        return orderItemInfoMapper.selectOrderItemInfoByOrderId(id, shList);
    }
    
    @Override
    public List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqpch(String fpqqpch, List<String> shList) {
        return invoiceBatchRequestItemMapper.selectInvoiceBatchItemByFpqqpch(fpqqpch, shList);
    }
    
    @Override
    public InvoiceBatchRequestItem selectInvoiceBatchItemByKplsh(String kplsh, List<String> shList) {
        return invoiceBatchRequestItemMapper.selectInvoiceBatchItemByKplsh(kplsh, shList);
    }
    
    @Override
    public List<InvoiceBatchRequestItem> selectInvoiceBatchItemByFpqqlsh(String fpqqlsh, List<String> xhfNsrsbh) {
        return invoiceBatchRequestItemMapper.selectInvoiceBatchItemByFpqqlsh(fpqqlsh, xhfNsrsbh);
    }
    
    @Override
    public int updateInvoiceStatusByKplsh(OrderInvoiceInfo orderInvoiceInfo, List<String> shList) {
    
        return orderInvoiceInfoMapper.updateInvoiceStatusByKplsh(orderInvoiceInfo, shList);
    
    }
    
    @Override
    public int updateBatchStatusById(String id, String statusCode, String statusMessage, List<String> shList) {
        InvoiceBatchRequest batchRequest = new InvoiceBatchRequest();
        batchRequest.setId(id);
        batchRequest.setStatus(statusCode);
        batchRequest.setMessage(statusMessage);
        return invoiceBatchRequestMapper.updateInvoiceBatchRequest(batchRequest, shList);
    }
    
    @Override
    public InvoiceBatchRequest selectInvoiceBatchRequestByFpqqpch(String fpqqpch, List<String> shList) {
        return invoiceBatchRequestMapper.selectInvoiceBatchRequestByFpqqpch(fpqqpch, shList);
    }
    
    @Override
    public OrderInvoiceInfo selectInvoiceInfoByKplsh(String kplsh, List<String> shList) {
        OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
        orderInvoiceInfo1.setKplsh(kplsh);
        return orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<OrderInfo> insertOrder, List<List<OrderItemInfo>> insertOrderItem,
                         List<OrderProcessInfo> insertProcessInfo, List<OrderProcessInfoExt> insertOrderProcessInfoExtList, List<OrderProcessInfo> updateOrderProcessInfo, List<OrderQrcodeExtendInfo> qrcodeInfoList, List<OrderOriginExtendInfo> originOrderList, List<String> shList) {
        //存订单和订单明细数据
        int insertOrderInfo = insertAutoOrderInfo(insertOrder, insertOrderItem);
        String jsonString = "";
        if (insertOrderInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertOrder);
            log.error("存订单信息到数据库失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        //存处理
        int insertAutoProcessInfo = insertAutoProcessInfo(insertProcessInfo);
        if (insertAutoProcessInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertProcessInfo);
            log.error("存处理表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        //存扩展表
        if (!CollectionUtils.isEmpty(insertOrderProcessInfoExtList)) {
            int insertAutoProcessInfoExt = insertAutoProcessInfoExt(insertOrderProcessInfoExtList, shList);
            if (insertAutoProcessInfoExt <= 0) {
                jsonString = JsonUtils.getInstance().toJsonString(insertAutoProcessInfoExt);
                log.error("存处理扩展表失败，数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        //更新扩展表
        if (!CollectionUtils.isEmpty(updateOrderProcessInfo)){
            int updateOrderProcessInfoByProcessId = updateProcessInfoBatch(updateOrderProcessInfo, shList);
            if (updateOrderProcessInfoByProcessId <= 0) {
                jsonString = JsonUtils.getInstance().toJsonString(insertProcessInfo);
                log.error("更新原订单状态失败,请求数据为:{}", jsonString);
            }
        }
        //存原始订单到最终订单的数据
        if(!CollectionUtils.isEmpty(originOrderList)){
        	int  i = insertOriginOrderInfoBatch(originOrderList);
        	if(i <= 0){
        		log.error("插入原始订单关系表失败");
        	}
        	
        }

        if(!CollectionUtils.isEmpty(qrcodeInfoList)){
            int  i = insertQrcodeInfoBatch(qrcodeInfoList);
            if(i <= 0){
                log.error("插入订单二维码扩展表失败");
            }

        }

    
    }


    /**
     * 不同税号下的数据保存
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param originOrderList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDifShData(List<OrderInfo> insertOrder, List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,List<OrderOriginExtendInfo> originOrderList) {
        //存订单和订单明细数据
        int insertOrderInfo = insertAutoOrderInfo(insertOrder, insertOrderItem);
        String jsonString = "";
        if (insertOrderInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertOrder);
            log.error("存订单信息到数据库失败，数据{},{}", jsonString, LOGGER_MSG);
        }

        //存处理
        int insertAutoProcessInfo = insertAutoProcessInfo(insertProcessInfo);
        if (insertAutoProcessInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertProcessInfo);
            log.error("存处理表失败，数据{},{}", jsonString, LOGGER_MSG);
        }



        //存原始订单到最终订单的数据
        if(!CollectionUtils.isEmpty(originOrderList)){
            int  i = insertOriginOrderInfoBatch(originOrderList);
            if(i <= 0){
                log.error("插入原始订单关系表失败");
            }

        }
    }
    
    /**
     * 异常订单开票后更新发票表 处理表状态
     *
     * @param updateOrderInvoiceInfos
     * @param updateProcessInfos
     * @param updateSpecialInvoices
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInvoiceUpdate(List<OrderInvoiceInfo> updateOrderInvoiceInfos, List<OrderProcessInfo> updateProcessInfos, List<SpecialInvoiceReversalEntity> updateSpecialInvoices, List<String> shList) {
        
        //更新处理表
        if (CollectionUtils.isNotEmpty(updateProcessInfos)) {
            int i = updateProcessInfoBatch(updateProcessInfos, shList);
            if (i != updateProcessInfos.size()) {
                throw new RuntimeException("处理表更新失败");
                
            }
            
        }

        //更新发票表数据
        if (!updateOrderInvoiceInfos.isEmpty()) {
            //更新发票
            int uii = updateInvoiceInfoBatch(updateOrderInvoiceInfos, shList);
            if (uii != updateOrderInvoiceInfos.size()) {
                throw new RuntimeException("发票表更新失败");
            }
        }

        //更新红字申请单状态
        for(SpecialInvoiceReversalEntity specialEntity : updateSpecialInvoices){

            int updateSpecialInvoiceReversal = specialInvoiceReversalDao.updateInvoiceStatusByXxbbh(specialEntity);
            if (updateSpecialInvoiceReversal <= 0) {
                throw new RuntimeException("红字申请单更新失败");
            }
        }


    }

    private int insertQrcodeInfoBatch(List<OrderQrcodeExtendInfo> qrcodeInfoList) {
        int i = 0;
        for (OrderQrcodeExtendInfo qrcodeOrder : qrcodeInfoList) {

            boolean b = orderQrcodeExtendService.saveQrcodeInfo(qrcodeOrder);
            if (!b) {
                log.error("插入原始订单关系表失败,数据:{}", JsonUtils.getInstance().toJsonString(qrcodeOrder));
                return -1;
            }
            i++;
        }
        return i;
    }

    private int insertOriginOrderInfoBatch(List<OrderOriginExtendInfo> originOrderList) {
    	  int i = 0;
          for (OrderOriginExtendInfo originOrder : originOrderList) {
    
              int insert = orderOriginExtendInfoMapper.insertOrderOriginExtend(originOrder);
              if (insert <= 0) {
                  log.error("插入原始订单关系表失败,数据:{}", JsonUtils.getInstance().toJsonString(originOrder));
                  return insert;
              }
              i++;
          }
          return i;
	}

	/**
     * 订单数据保存数据库
     *
     * @param transitionBatchRequest
     * @param insertOrder
     * @param insertOrderItem
     * @param insertProcessInfo
     * @param insertBatchItem
     * @param insertInvoiceInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<InvoiceBatchRequest> transitionBatchRequest
            , List<OrderInfo> insertOrder
            , List<List<OrderItemInfo>> insertOrderItem
            , List<OrderProcessInfo> insertProcessInfo
            , List<List<InvoiceBatchRequestItem>> insertBatchItem
            , List<OrderInvoiceInfo> insertInvoiceInfo
            , OrderBatchRequest obr
            , List<OrderProcessInfo> updateProcessInfo
            , List<OrderInvoiceInfo> updateInvoiceInfo
            , List<OrderOriginExtendInfo> originExtendList
            , List<String> shList) {
        String jsonString = "";
    
        //存订单批次
        int ddh = orderBatchRequestMapper.selectOrderBatchRequestByDdqqpch(obr.getDdqqpch(), shList);
        if (ddh < 1) {
            int inserOrderBatchRequest = orderBatchRequestMapper.insertOrderBatch(obr);
            if (inserOrderBatchRequest <= 0) {
                jsonString = JsonUtils.getInstance().toJsonString(obr);
                log.error("存订单到订单批次表失败,数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        
        insertAutoInvoiceBatch(transitionBatchRequest, insertBatchItem, shList);
        
        //存订单和订单明细数据
        int insertOrderInfo = insertAutoOrderInfo(insertOrder, insertOrderItem);
        if (insertOrderInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertOrder);
            log.error("存订单信息到数据库失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        //存处理
        int insertAutoProcessInfo = insertAutoProcessInfo(insertProcessInfo);
        if (insertAutoProcessInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertProcessInfo);
            log.error("存处理表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        
        //存发票
        int insertAutoOrderInvoiceInfo = insertAutoOrderInvoiceInfo(insertInvoiceInfo, shList);
        jsonString = JsonUtils.getInstance().toJsonString(insertInvoiceInfo);
        log.debug("自动开票存发票表数据:{}", jsonString);
        if (insertAutoOrderInvoiceInfo <= 0) {
            log.error("存发票信息表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        
        //更新process
        if (!updateProcessInfo.isEmpty()) {
            int upb = updateProcessInfoBatch(updateProcessInfo, shList);
            jsonString = JsonUtils.getInstance().toJsonString(updateProcessInfo);
            if (upb <= 0) {
                log.error("更新process表失败，数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        
        if (!updateInvoiceInfo.isEmpty()) {
            //更新发票
            int uii = updateInvoiceInfoBatch(updateInvoiceInfo, shList);
            jsonString = JsonUtils.getInstance().toJsonString(updateInvoiceInfo);
            if (uii <= 0) {
                log.error("更新发票表失败，数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        
        //存原始订单到最终订单的数据
        if(!CollectionUtils.isEmpty(originExtendList)){
        	int  i = insertOriginOrderInfoBatch(originExtendList);
        	if(i <= 0){
        		log.error("插入原始订单关系表失败");
        	}
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveHistoryData(List<InvoiceBatchRequest> transitionBatchRequest, List<OrderInfo> insertOrder,
                                List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,
                                List<List<InvoiceBatchRequestItem>> insertBatchItem, List<OrderInvoiceInfo> insertInvoiceInfo,
                                OrderBatchRequest obr, List<OrderProcessInfo> updateProcessInfo,
                                List<OrderInvoiceInfo> updateInvoiceInfo, List<OrderOriginExtendInfo> originExtendList,
                                String pdfFile, List<String> xhfNsrsbh) {
        this.saveData(transitionBatchRequest, insertOrder, insertOrderItem, insertProcessInfo, insertBatchItem,
                insertInvoiceInfo, obr, updateProcessInfo, updateInvoiceInfo, originExtendList,xhfNsrsbh);
        /*
         * 判断是否为历史导入数据,如果是历史导入数据将pdf文件存储到mongodb服务器，
         * 并更新订单和发票关系表（order_invoice_info）的mongodb_id字段
         */
        historyDataPdfService.save(insertInvoiceInfo.get(0), pdfFile, ConfigureConstant.STRING_SUFFIX_PDF);
    }
    
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoiceRequestData(List<OrderProcessInfo> updateProcessInfo, List<OrderInvoiceInfo> insertOrderInvoiceInfo, List<InvoiceBatchRequest> invoiceBatchRequestList, List<List<InvoiceBatchRequestItem>> insertInvoiceBatchRequestItem,
                                   List<SpecialInvoiceReversalEntity> updateSpecialInvoiceList, List<String> shList) throws OrderReceiveException {
        
        
        /**
         * 入库发票请求批次表
         * 查询请求批次表数据是否存在,如果不存在就插入
         */
        String jsonString = "";
        insertAutoInvoiceBatch(invoiceBatchRequestList, insertInvoiceBatchRequestItem, shList);
        
        //存发票
        int insertAutoOrderInvoiceInfo = insertAutoOrderInvoiceInfo(insertOrderInvoiceInfo, shList);
        jsonString = JsonUtils.getInstance().toJsonString(insertOrderInvoiceInfo);
        log.debug("手动开票存发票表数据:{}", jsonString);
        if (insertAutoOrderInvoiceInfo <= 0) {
            log.error("存发票信息表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        
        //更新process
        if (!updateProcessInfo.isEmpty()) {
            int upb = updateProcessInfoBatch(updateProcessInfo, shList);
            jsonString = JsonUtils.getInstance().toJsonString(updateProcessInfo);
            if (upb <= 0) {
                log.error("更新process表失败，数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        
        //更新红字申请单的开票状态
        for(SpecialInvoiceReversalEntity specialEntity : updateSpecialInvoiceList){
            int updateSpecialInvoiceReversal = specialInvoiceReversalDao.updateInvoiceStatusByXxbbh(specialEntity);
            if (updateSpecialInvoiceReversal <= 0) {
                log.error("红字申请单状态更新失败,红字申请单id:{}", specialEntity.getSqdh());
            }
        }
        //开票时把二维码修改成 已使用状态
        for(OrderProcessInfo opi : updateProcessInfo){

            OrderProcessInfo orderProcessInfo = orderProcessInfoMapper.selectOrderProcessInfoByProcessId(opi.getId(), shList);
            if(OrderInfoEnum.ORDER_SOURCE_5.getKey().equals(orderProcessInfo.getDdly()) || OrderInfoEnum.ORDER_SOURCE_6.getKey().equals(orderProcessInfo.getDdly())){
                OrderQrcodeExtendInfo qrcodeInfo = new OrderQrcodeExtendInfo();
                qrcodeInfo.setOrderInfoId(orderProcessInfo.getOrderInfoId());
                qrcodeInfo.setXhfNsrsbh(orderProcessInfo.getXhfNsrsbh());
                qrcodeInfo.setEwmzt("1");
                qrcodeInfo.setUpdateTime(new Date());
                int i = orderQrcodeExtendInfoMapper.updateByOrderIdSelective(qrcodeInfo, shList);
                if (i <= 0) {
                    log.error("更新订单二维码扩展表失败，id:{}", qrcodeInfo.getOrderInfoId());
                }
            }
        }
        
        
    }
    
    
    private int insertAutoOrderInfo(List<OrderInfo> insertOrder, List<List<OrderItemInfo>> insertOrderItem) {
        int i = 0;
        /**
         * 订单数据和发票数据保证一个原子,进行入库.
         * 订单主体数组长度和订单明细数组长度应该一致.
         */
        for (int j = 0; j < insertOrder.size(); j++) {
            OrderInfo orderInfo = insertOrder.get(j);
            List<String> shList = new ArrayList<>();
            shList.add(orderInfo.getXhfNsrsbh());
            OrderInfo orderInfo1 = orderInfoMapper.selectOrderInfoByDdqqlsh(orderInfo.getFpqqlsh(), shList);
            if (orderInfo1 == null) {
                int insertSelective = orderInfoMapper.insertOrderInfo(orderInfo);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
                log.debug("存订单到数据库处理完成");


                // update by ysy 添加明细的非空校验 供应链的订单没有明细
                if(CollectionUtils.isNotEmpty(insertOrderItem)){
                    for (OrderItemInfo orderItemInfo : insertOrderItem.get(j)) {
                        insertSelective = orderItemInfoMapper.insertOrderItemInfo(orderItemInfo);
                        if (insertSelective <= 0) {
                            String jsonString = JsonUtils.getInstance().toJsonString(insertOrderItem.get(j));
                            log.error("存订单明细到数据库失败，数据{}", jsonString);
                        }
                    }
                    log.debug("存订单明细到数据库处理完成");
                    if (insertSelective <= 0) {
                        return insertSelective;
                    }
                }
                i++;
            }
            
            
        }
        
        return i;
    }
    
    private int insertAutoProcessInfo(List<OrderProcessInfo> insertProcessInfo) {
        int i = 0;
        for (OrderProcessInfo orderProcessInfo : insertProcessInfo) {
            List<String> shList = new ArrayList<>();
            shList.add(orderProcessInfo.getXhfNsrsbh());
            OrderProcessInfo orderProcessInfo1 = orderProcessInfoMapper.selectOrderProcessInfoByDdqqlsh(orderProcessInfo.getFpqqlsh(), shList);
            if (orderProcessInfo1 == null) {
                //添加数据库默认值
                orderProcessInfo.setCheckStatus(StringUtils.isBlank(orderProcessInfo.getCheckStatus()) ?
                        OrderInfoEnum.CHECK_STATUS_0.getKey() : orderProcessInfo.getCheckStatus());
                orderProcessInfo.setEditStatus(OrderInfoEnum.EDIT_STATUS_0.getKey());
                int insertSelective = orderProcessInfoMapper.insertOrderProcessInfo(orderProcessInfo);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
        
            }
            i++;
        }
        return i;
    }
    
    private int insertAutoProcessInfoExt(List<OrderProcessInfoExt> insertProcessInfoExtList, List<String> shList) {
        int i = 0;
        for (OrderProcessInfoExt orderProcessInfoExt : insertProcessInfoExtList) {
            OrderProcessInfoExt orderProcessInfoExt1 = orderProcessInfoExtMapper.selectOrderProcessInfoExtByOrderProcessIdAndParentOrderProcessId(orderProcessInfoExt.getOrderProcessInfoId(), orderProcessInfoExt.getParentOrderProcessId(), shList);
            if (orderProcessInfoExt1 == null) {
                int insertSelective = orderProcessInfoExtMapper.insertOrderProcessExt(orderProcessInfoExt);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
                
            }
            i++;
        }
        return i;
    }
    
    private int insertAutoOrderInvoiceInfo(List<OrderInvoiceInfo> insertInvoiceInfo, List<String> shList) {
        log.info("发票信息保存的接口，参数：{}", JsonUtils.getInstance().toJsonString(insertInvoiceInfo));
        int i = 0;
        for (OrderInvoiceInfo orderInvoiceInfo : insertInvoiceInfo) {
            OrderInvoiceInfo orderInvoiceInfo3 = new OrderInvoiceInfo();
            orderInvoiceInfo3.setFpqqlsh(orderInvoiceInfo.getFpqqlsh());
            OrderInvoiceInfo orderInvoiceInfo1 = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo3, shList);
            if (orderInvoiceInfo1 == null) {
                log.info("发票信息不存在,可以保存数据");
                int insertSelective = orderInvoiceInfoMapper.insertOrderInvoiceInfo(orderInvoiceInfo);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
                
            } else {
                OrderInvoiceInfo orderInvoiceInfo2 = new OrderInvoiceInfo();
                orderInvoiceInfo2.setId(orderInvoiceInfo1.getId());
                orderInvoiceInfo2.setKplsh(orderInvoiceInfo.getKplsh());
                orderInvoiceInfo2.setKpzt(orderInvoiceInfo.getKpzt());
                int insertSelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo2, shList);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
            }
            i++;
        }
        return i;
    }
    
    private int insertAutoInvoiceBatch(List<InvoiceBatchRequest> invoiceBatchRequests, List<List<InvoiceBatchRequestItem>> invoiceBatchRequestItemList, List<String> shList) {
        log.debug("{}发票信息保存的接口,批次信息:{},明细信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoiceBatchRequests), JsonUtils.getInstance().toJsonString(invoiceBatchRequestItemList));
        for (int i = 0; i < invoiceBatchRequests.size(); i++) {
            InvoiceBatchRequest invoiceBatchRequest = invoiceBatchRequests.get(i);
            InvoiceBatchRequest invoiceBatchCount = invoiceBatchRequestMapper.selectInvoiceBatchRequestByFpqqpch(invoiceBatchRequest.getFpqqpch(), shList);
            if (ObjectUtil.isNull(invoiceBatchCount)) {
                int insertSelective = invoiceBatchRequestMapper.insertInvoiceBatchRequest(invoiceBatchRequest);
                if (insertSelective <= 0) {
                    return insertSelective;
                }
            }
            List<InvoiceBatchRequestItem> invoiceBatchRequestItems = selectInvoiceBatchItemByFpqqpch(invoiceBatchRequest.getFpqqpch(), shList);
            if (invoiceBatchRequestItems == null || invoiceBatchRequestItems.size() <= 0) {
                int insertinvoiceBatchRequestItem = invoiceBatchRequestItemMapper.insertInvoiceBatchItemBatch(invoiceBatchRequestItemList.get(i));
                if (insertinvoiceBatchRequestItem <= 0) {
                    return insertinvoiceBatchRequestItem;
                }
            }
        }
    
        return 0;
    }

    public int updateProcessInfoBatch(List<OrderProcessInfo> updateProcessInfo, List<String> shList) {
        int flag = 0;
        for (OrderProcessInfo orderProcessInfo : updateProcessInfo) {
            orderProcessInfo.setUpdateTime(new Date());
            int ubkm = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
            if (ubkm > 0) {
                flag++;
            }
        }
        return flag;
    }
    
    public int updateInvoiceInfoBatch(List<OrderInvoiceInfo> updateInvoiceInfo, List<String> shList) {
        int flag = 0;
        for (OrderInvoiceInfo orderInvoiceInfo : updateInvoiceInfo) {
            orderInvoiceInfo.setUpdateTime(new Date());
            int ubkm = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
            if (ubkm > 0) {
                flag++;
            }
        }
        return flag;
    }

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveMergeOrderInfo(CommonOrderInfo commonOrder) {
		
		if (StringUtils.isNotBlank(commonOrder.getOriginOrderId()) && StringUtils.isNotBlank(commonOrder.getOriginProcessId())) {
            
            List<OrderProcessInfoExt> processExtList = new ArrayList<>();
            List<OrderProcessInfo> processList = new ArrayList<>();
            
            String[] originOrderIdS = commonOrder.getOriginOrderId().split(":");
            String[] originOrderProcessIdS = commonOrder.getOriginProcessId().split(":");
            
            OrderInfo orderInfo = buildOrderInfo(commonOrder.getOrderInfo());
            OrderProcessInfo orderProcessInfo = buildProcessInfo(orderInfo, commonOrder.getProcessInfo());
            orderProcessInfo.setOrderInfoId(orderInfo.getId());
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_2.getKey());
            orderProcessInfo.setDdly(commonOrder.getProcessInfo().getDdly());
            orderInfo.setProcessId(orderProcessInfo.getId());
            List<String> shList = new ArrayList<>();
            shList.add(orderInfo.getXhfNsrsbh());
            
            if (commonOrder.getOrderItemInfo().size() > ConfigureConstant.INT_8) {
                orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_1.getKey());
                if (StringUtils.isBlank(orderInfo.getQdXmmc())) {
                    orderInfo.setQdXmmc(ConfigureConstant.XJZSXHQD);
                }
            } else {
                orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_0.getKey());
            }
            
            for (int i = 0; i < originOrderIdS.length; i++) {
                String originOrderId = originOrderIdS[i];
                String originOrderProcessId = originOrderProcessIdS[i];
                String orderProcessInfoExtId = apiInvoiceCommonService.getGenerateShotKey();
                
                OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
                orderProcessInfoExt.setOrderProcessInfoId(orderProcessInfo.getId());
                orderProcessInfoExt.setParentOrderInfoId(originOrderId);
                orderProcessInfoExt.setParentOrderProcessId(originOrderProcessId);
                orderProcessInfoExt.setId(orderProcessInfoExtId);
                orderProcessInfoExt.setStatus("0");
                orderProcessInfoExt.setXhfNsrsbh(orderProcessInfo.getXhfNsrsbh());
                orderProcessInfoExt.setCreateTime(new Date());
                orderProcessInfoExt.setUpdateTime(new Date());
                processExtList.add(orderProcessInfoExt);
    
                OrderProcessInfo orderProcessInfo1 = new OrderProcessInfo();
                orderProcessInfo1.setId(originOrderProcessId);
                orderProcessInfo1.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
                processList.add(orderProcessInfo1);
            }
            
            //原始订单到最终订单的处理
            List<String> deleteIdList = new ArrayList<>();
            List<OrderOriginExtendInfo> insertList = new ArrayList<>();
            for (String originOrderId : originOrderIdS) {
                OrderOriginExtendInfo originOrder = new OrderOriginExtendInfo();
                originOrder.setOrderId(originOrderId);
                List<OrderOriginExtendInfo> queryOriginOrderByOrder = orderOriginExtendInfoMapper.queryOriginOrderByOrder(originOrder, shList);
                insertList.addAll(queryOriginOrderByOrder);
                deleteIdList.add(originOrderId);
            }
            
            for (OrderItemInfo orderItem : commonOrder.getOrderItemInfo()) {
                orderItem.setOrderInfoId(orderInfo.getId());
                orderItem.setSl(StringUtil.formatSl(orderItem.getSl()));
            	orderItem.setId(apiInvoiceCommonService.getGenerateShotKey());
            	orderItem.setCreateTime(new Date());
            	int insertOrderItemInfo = orderItemInfoMapper.insertOrderItemInfo(orderItem);
            	if(insertOrderItemInfo <= 0){
                	log.error("合并后订单数据保存失败");
                	return false;
            	}
            }
            
            //插入订单表
            int insert = orderInfoMapper.insertOrderInfo(orderInfo);
            if(insert <= 0){
            	log.error("合并后订单数据保存失败");
            	return false;
            }
            //插入处理表
            orderProcessInfo.setCheckStatus(StringUtils.isBlank(orderProcessInfo.getCheckStatus()) ?
                    OrderInfoEnum.CHECK_STATUS_0.getKey() : orderProcessInfo.getCheckStatus());
            orderProcessInfo.setEditStatus(OrderInfoEnum.EDIT_STATUS_0.getKey());

            int insert2 = orderProcessInfoMapper.insertOrderProcessInfo(orderProcessInfo);
            if(insert2 <= 0){
            	log.error("合并后订单数据保存失败");
            	return false;
            }
            //更新原处理表
            int updateProcessInfoBatch = updateProcessInfoBatch(processList, shList);
            if(updateProcessInfoBatch <= 0){
            	log.error("合并后订单数据保存失败");
            	return false;
            }
            //删除原始订单到当前订单的关系
            for(String id : deleteIdList){
            	OrderOriginExtendInfo extendInfo = new OrderOriginExtendInfo();
                extendInfo.setOrderId(id);
                extendInfo.setStatus("1");
                int deleteByOrderId = orderOriginExtendInfoMapper.updateSelectiveByOrderId(extendInfo, shList);
            	if(deleteByOrderId <= 0){
            		log.error("订单合并数据保存失败");
            		return false;
            	}
            }
            
            for(OrderProcessInfoExt ext : processExtList) {
    
                int insert4 = orderProcessInfoExtMapper.insertOrderProcessExt(ext);
                if (insert4 <= 0) {
                    log.error("订单合并数据保存失败");
                    return false;
                }
    
    
            }
            
            //去除重复数据
            insertList = checkAadRemoveOriginExtend(insertList);
            
            //插入新的原始订单到当前订单的关系
            for(OrderOriginExtendInfo origin : insertList) {
                origin.setCreateTime(new Date());
                origin.setUpdateTime(new Date());
                origin.setFpqqlsh(orderInfo.getFpqqlsh());
                origin.setOrderId(orderInfo.getId());
                origin.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
                origin.setId(apiInvoiceCommonService.getGenerateShotKey());
                origin.setStatus("0");
                int insert3 = orderOriginExtendInfoMapper.insertOrderOriginExtend(origin);
                if (insert3 <= 0) {
                    log.error("订单合并数据保存失败");
                    return false;
                }
            }
            return true;
        }
		return false;
	}
    
    /**
     * @param @param  insertList
     * @param @return
     * @return List<OrderOriginExtendInfo>
     * @throws
     * @Title : checkAadRemoveOriginExtend
     * @Description ：过滤重复数据
     */
	
	private List<OrderOriginExtendInfo> checkAadRemoveOriginExtend(List<OrderOriginExtendInfo> insertList) {
        List<OrderOriginExtendInfo> resultList = new ArrayList<>();
        Map<String, String> checkMap = new HashMap<>(5);
        for (OrderOriginExtendInfo originOrder : insertList) {
            String key = originOrder.getOrderId() + originOrder.getOriginOrderId();
            if (!checkMap.containsKey(key)) {
                resultList.add(originOrder);
                checkMap.put(key, "");
            }
        }
        return resultList;
    }
    
    /**
     * 构建订单处理表信息
     *
     * @param orderInfo
     * @param processInfo
     * @return
     */
    private OrderProcessInfo buildProcessInfo(OrderInfo orderInfo, OrderProcessInfo processInfo) {
        String id = apiInvoiceCommonService.getGenerateShotKey();
        Date date = new Date();
        OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
        orderProcessInfo.setOrderInfoId(orderInfo.getId());
        orderProcessInfo.setId(id);
        orderProcessInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        orderProcessInfo.setDdh(orderInfo.getDdh());
        orderProcessInfo.setTqm(orderInfo.getTqm());
        orderProcessInfo.setKphjje(orderInfo.getKphjje());
        orderProcessInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderProcessInfo.setKpse(orderInfo.getHjse());
        orderProcessInfo.setFpzlDm(orderInfo.getFpzlDm());
        orderProcessInfo.setGhfMc(orderInfo.getGhfMc());
        orderProcessInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        orderProcessInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        orderProcessInfo.setXhfMc(orderInfo.getXhfMc());
        orderProcessInfo.setKpxm(orderInfo.getKpxm());
        orderProcessInfo.setDdcjsj(orderInfo.getDdrq());
		orderProcessInfo.setDdlx(processInfo.getDdlx());
        orderProcessInfo.setDdly(processInfo.getDdly());
        orderProcessInfo.setYwlx(orderInfo.getYwlx());
        orderProcessInfo.setYwlxId(orderInfo.getYwlxId());
        orderProcessInfo.setSbyy("");
        orderProcessInfo.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
        orderProcessInfo.setCreateTime(date);
        orderProcessInfo.setUpdateTime(date);
        orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
        return orderProcessInfo;
    }
    
    /**
     * 构建订单信息
     *
     * @param orderInfo
     * @return
     */
    private OrderInfo buildOrderInfo(OrderInfo orderInfo) {
        
        OrderInfo newOrderInfo = new OrderInfo();
        String id = apiInvoiceCommonService.getGenerateShotKey();
        String fpqqlsh = apiInvoiceCommonService.getGenerateShotKey();
        BeanUtils.copyProperties(orderInfo, newOrderInfo);
        Date date = new Date();
        newOrderInfo.setId(id);
        newOrderInfo.setCreateTime(date);
        newOrderInfo.setUpdateTime(date);
        newOrderInfo.setFpqqlsh(fpqqlsh);
        return newOrderInfo;
    }
    
    /**
     * 只支持单条拆分订单插入
     *
     * @param resultList
     * @return
     * @throws OrderReceiveException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CommonOrderInfo> saveOrderSplitInfo(List<CommonOrderInfo> resultList) throws OrderReceiveException {
        
        List<CommonOrderInfo> commList = new ArrayList<>();
        
        OrderOriginExtendInfo orderOriginExtend = new OrderOriginExtendInfo();
        orderOriginExtend.setOrderId(resultList.get(0).getOriginOrderId());
        List<String> shList = new ArrayList<>();
        shList.add(resultList.get(0).getOrderInfo().getXhfNsrsbh());
        List<OrderOriginExtendInfo> queryOriginOrderByOrder = orderOriginExtendInfoMapper.queryOriginOrderByOrder(orderOriginExtend, shList);
        
        OrderProcessInfo selectByOrderId = orderProcessInfoMapper.selectByOrderId(resultList.get(0).getOriginOrderId(), shList);
        selectByOrderId.setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
        
        for (CommonOrderInfo comm : resultList) {
            CommonOrderInfo common = new CommonOrderInfo();
            
            OrderInfo orderInfo = buildOrderInfo(comm.getOrderInfo());
            //重置订单日期
            orderInfo.setDdrq(orderInfo.getCreateTime());
            OrderProcessInfo orderProcessInfo = buildProcessInfo(orderInfo, selectByOrderId);
            

            orderProcessInfo.setOrderInfoId(orderInfo.getId());
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_1.getKey());
            orderInfo.setProcessId(orderProcessInfo.getId());
            
            
            //扩张表
            OrderProcessInfoExt orderExt = new OrderProcessInfoExt();
            orderExt.setCreateTime(new Date());
            orderExt.setUpdateTime(new Date());
            orderExt.setId(apiInvoiceCommonService.getGenerateShotKey());
            orderExt.setOrderProcessInfoId(orderInfo.getProcessId());
            orderExt.setParentOrderInfoId(comm.getOriginOrderId());
            orderExt.setParentOrderProcessId(comm.getOriginProcessId());
            orderExt.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderExt.setStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());

            
            for(OrderItemInfo item : comm.getOrderItemInfo()){
                item.setOrderInfoId(orderInfo.getId());
                if (StringUtils.isBlank(item.getXhfNsrsbh())) {
                    item.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
                }
                item.setSl(StringUtil.formatSl(item.getSl()));
                item.setId(apiInvoiceCommonService.getGenerateShotKey());
                item.setCreateTime(item.getCreateTime() == null ? new Date() : item.getCreateTime());
                int insertOrderItemInfo = orderItemInfoMapper.insertOrderItemInfo(item);
                if (insertOrderItemInfo <= 0) {
                    log.error("拆分后订单数据保存失败");
                    throw new OrderReceiveException("9999", "订单拆分后数据保存失败");
                }
            }
            
            int insert = orderProcessInfoExtMapper.insertOrderProcessExt(orderExt);
            if(insert <= 0){
            	log.error("拆分后订单数据保存失败");
            	throw new OrderReceiveException("9999","订单拆分后数据保存失败");
            }
            
            int insert2 = orderInfoMapper.insertOrderInfo(orderInfo);
            if(insert2 <= 0){
            	log.error("拆分后订单数据保存失败");
            	throw new OrderReceiveException("9999","订单拆分后数据保存失败");
            }

            orderProcessInfo.setCheckStatus(StringUtils.isBlank(orderProcessInfo.getCheckStatus()) ?
                    OrderInfoEnum.CHECK_STATUS_0.getKey() : orderProcessInfo.getCheckStatus());
            orderProcessInfo.setEditStatus(OrderInfoEnum.EDIT_STATUS_0.getKey());
            int insert3 = orderProcessInfoMapper.insertOrderProcessInfo(orderProcessInfo);
            if(insert3 <= 0){
            	log.error("拆分后订单数据保存失败");
            	throw new OrderReceiveException("9999","订单拆分后数据保存失败");
            }
            
            //过滤重复数据
            queryOriginOrderByOrder = checkAadRemoveOriginExtend(queryOriginOrderByOrder);

            for(OrderOriginExtendInfo orderOriginExtendInfo : queryOriginOrderByOrder) {
                OrderOriginExtendInfo origin = new OrderOriginExtendInfo();
                origin.setCreateTime(new Date());
                origin.setUpdateTime(new Date());
                origin.setFpqqlsh(orderInfo.getFpqqlsh());
                origin.setId(apiInvoiceCommonService.getGenerateShotKey());
                origin.setOrderId(orderInfo.getId());
                origin.setOriginOrderId(orderOriginExtendInfo.getOriginOrderId());
                origin.setOriginFpqqlsh(orderOriginExtendInfo.getOriginFpqqlsh());
                origin.setOriginDdh(orderOriginExtendInfo.getOriginDdh());
                origin.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
                origin.setStatus("0");
                int insert4 = orderOriginExtendInfoMapper.insertOrderOriginExtend(origin);
                if (insert4 <= 0) {
                    log.error("拆分后订单数据保存失败");
                    throw new OrderReceiveException("9999", "订单拆分后数据保存失败");
                }
            }
            
            BeanUtils.copyProperties(comm, common);
            common.setOrderInfo(orderInfo);
            common.setOrderItemInfo(comm.getOrderItemInfo());
            commList.add(common);

		}
		
		OrderProcessInfo orderProcessInfo1 = new OrderProcessInfo();
        orderProcessInfo1.setId(resultList.get(0).getOriginProcessId());
        orderProcessInfo1.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
        orderProcessInfo1.setUpdateTime(new Date());
        int updateOrderProcessInfoByProcessId = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo1, shList);
        if(updateOrderProcessInfoByProcessId <= 0){
        	log.error("拆分后的订单更新processInfo失败");
        	throw new OrderReceiveException("9999","订单拆分后数据保存失败");
        }
        
        OrderOriginExtendInfo extendInfo = new OrderOriginExtendInfo();
        extendInfo.setOrderId(resultList.get(0).getOriginOrderId());
        extendInfo.setStatus("1");
        int deleteByOrderId = orderOriginExtendInfoMapper.updateSelectiveByOrderId(extendInfo, shList);
		if(deleteByOrderId <= 0){
        	log.error("拆分后订单数据保存失败");
        	throw new OrderReceiveException("9999","订单拆分后数据保存失败");
		}
		return commList;
	}

	@Override
	public void savePageData(List<OrderInfo> insertOrderInfoList,
			List<List<OrderItemInfo>> insertOrderItemList, List<OrderProcessInfo> insertOrderProcessInfoList,
			List<OrderOriginExtendInfo> orderOriginList) {
		 //存订单和订单明细数据
		
        int insertOrderInfo = insertAutoOrderInfo(insertOrderInfoList, insertOrderItemList);
        String jsonString = "";
        if (insertOrderInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertOrderInfoList);
            log.error("存订单信息到数据库失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        //存处理
        int insertAutoProcessInfo = insertAutoProcessInfo(insertOrderProcessInfoList);
        if (insertAutoProcessInfo <= 0) {
            jsonString = JsonUtils.getInstance().toJsonString(insertOrderProcessInfoList);
            log.error("存处理表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        
        //存原始订单到最终订单的数据
        if(!CollectionUtils.isEmpty(orderOriginList)){
        	int  i = insertOriginOrderInfoBatch(orderOriginList);
        	if(i <= 0){
        		log.error("插入原始订单关系表失败");
        	}
    
        }
	}
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDynamicQrCodeInfo(List<InvoiceBatchRequest> transitionBatchRequest, List<OrderInfo> insertOrder,
                                      List<List<OrderItemInfo>> insertOrderItem, List<OrderProcessInfo> insertProcessInfo,
                                      List<List<InvoiceBatchRequestItem>> insertBatchItem, List<OrderInvoiceInfo> insertInvoiceInfo,
                                      OrderBatchRequest orderBatchRequest, List<OrderProcessInfo> updateProcessInfo,
                                      List<OrderInvoiceInfo> updateInvoiceInfo, List<OrderOriginExtendInfo> orderOriginList, List<String> shList) {
        
        String jsonString = "";
        
        // 存订单批次
        int ddh = orderBatchRequestMapper.selectOrderBatchRequestByDdqqpch(orderBatchRequest.getDdqqpch(), shList);
        if (ddh < 1) {
            int inserOrderBatchRequest = orderBatchRequestMapper.insertOrderBatch(orderBatchRequest);
            if (inserOrderBatchRequest <= 0) {
                jsonString = JsonUtils.getInstance().toJsonString(orderBatchRequest);
                log.error("存订单到订单批次表失败,数据{},{}", jsonString, LOGGER_MSG);
            }
        }
        
        insertAutoInvoiceBatch(transitionBatchRequest, insertBatchItem, shList);
        
        //根据请求流水号 更新动态码订单表
        for (OrderInfo orderInfo : insertOrder) {
			
			OrderInfo updateOrderInfo = new OrderInfo();
			updateOrderInfo.setGhfDh(orderInfo.getGhfDh());
			updateOrderInfo.setGhfDz(orderInfo.getGhfDz());
			updateOrderInfo.setGhfEmail(orderInfo.getGhfEmail());
			updateOrderInfo.setKphjje(orderInfo.getKphjje());
			updateOrderInfo.setHjbhsje(orderInfo.getHjbhsje());
			updateOrderInfo.setHjse(orderInfo.getHjse());
			updateOrderInfo.setGhfId(orderInfo.getGhfId());
			updateOrderInfo.setGhfMc(orderInfo.getGhfMc());
			updateOrderInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
            updateOrderInfo.setGhfQylx(orderInfo.getGhfQylx());
            updateOrderInfo.setSld(orderInfo.getSld());
            updateOrderInfo.setSldMc(orderInfo.getSldMc());
            updateOrderInfo.setId(orderInfo.getId());
            updateOrderInfo.setFpzlDm(orderInfo.getFpzlDm());
            updateOrderInfo.setYwlx(orderInfo.getYwlx());
            
            orderInfoMapper.updateOrderInfoByOrderId(updateOrderInfo, shList);
            
            //删除原有的订单明细
            orderItemInfoMapper.deleteOrderItemInfoByOrderId(orderInfo.getId(), shList);
            
            
        }
		
		//更新订单扩展表
		for(OrderProcessInfo orderProcessInfo : insertProcessInfo){
			
			OrderProcessInfo updateOrderProcessInfo = new OrderProcessInfo();
            updateOrderProcessInfo.setGhfMc(orderProcessInfo.getGhfMc());
            updateOrderProcessInfo.setGhfNsrsbh(orderProcessInfo.getGhfNsrsbh());
            updateOrderProcessInfo.setId(orderProcessInfo.getId());
            updateOrderProcessInfo.setDdzt(orderProcessInfo.getDdzt());
            updateOrderProcessInfo.setKphjje(orderProcessInfo.getKphjje());
            updateOrderProcessInfo.setHjbhsje(orderProcessInfo.getHjbhsje());
            updateOrderProcessInfo.setKpse(orderProcessInfo.getKpse());
            updateOrderProcessInfo.setFpzlDm(orderProcessInfo.getFpzlDm());
            updateOrderProcessInfo.setYwlx(orderProcessInfo.getYwlx());
            updateOrderProcessInfo.setYwlxId(orderProcessInfo.getYwlxId());
            orderProcessInfoMapper.updateOrderProcessInfoByProcessId(updateOrderProcessInfo, shList);
        }
		
		
		//插入明细表
       
        for( int j = 0; j < insertOrderItem.size();j++){
        	for (OrderItemInfo orderItemInfo : insertOrderItem.get(j)) {
                int insertSelective = orderItemInfoMapper.insertOrderItemInfo(orderItemInfo);
                if (insertSelective <= 0) {
                    String jsonString1 = JsonUtils.getInstance().toJsonString(insertOrderItem.get(j));
                    log.error("存订单明细到数据库失败，数据{}", jsonString1);
                }
                j++;
            }
    
    
        }
        
        int insertAutoOrderInvoiceInfo = insertAutoOrderInvoiceInfo(insertInvoiceInfo, shList);
        jsonString = JsonUtils.getInstance().toJsonString(insertInvoiceInfo);
        log.debug("自动开票存发票表数据:{}", jsonString);
        if (insertAutoOrderInvoiceInfo <= 0) {
            log.error("存发票信息表失败，数据{},{}", jsonString, LOGGER_MSG);
        }
        
    }
 
}
