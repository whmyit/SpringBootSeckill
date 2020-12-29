package com.dxhy.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.*;
import com.dxhy.order.dao.*;
import com.dxhy.order.model.*;
import com.dxhy.order.model.email.EmailContent;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.utils.BeanTransitionUtils;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Description: 开票返回数据接收
 * @author: chengyafu
 * @date: 2018年7月24日 下午6:03:10
 */
@Service
@Slf4j
public class InvoiceDataServiceImpl implements InvoiceDataService {
    
    private static final String LOGGER_MSG = "(推送发票结果业务类)";
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    @Resource
    private InvoiceBatchRequestMapper invoiceBatchRequestMapper;
    @Resource
    private InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private SpecialInvoiceReversalDao specialInvoiceReversalDao;
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;
    @Resource
    private SalerWarningService salerWarningService;
    @Resource
    private RedisService redisService;
    @Resource
    private ApiEmailService apiEmailService;
    @Resource
    private PushInfoMapper pushInfoMapper;
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 推送接收接口
     */
    @Override
    public R receiveInvoice(InvoicePush invoicePush) {
        
        log.info("{},发票结果推送数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoicePush));
        if (invoicePush != null) {
            // 开具状态1000待开，1001待调税控，2100赋码成功，2101赋码失败，2000签章成功，2001签章失败
            InvoiceBatchRequest insertInvoiceBatchRequest = new InvoiceBatchRequest();
    
            /**
             * 查询发票批次表数据
             */
            List<String> shList = new ArrayList<>();
            shList.add(invoicePush.getNSRSBH());
            InvoiceBatchRequest oldInvoiceBatchRequest = invoiceBatchRequestMapper.selectInvoiceBatchRequestByFpqqpch(invoicePush.getFPQQPCH(), shList);
            if (oldInvoiceBatchRequest == null) {
                log.error("{}通过批次号{}得到批次表信息为空!", LOGGER_MSG, invoicePush.getFPQQPCH());
                return R.error(OrderInfoContentEnum.INVOICE_DATA_BATCH_NULL);
            }
            log.info("{}通过批次号得到批次表信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oldInvoiceBatchRequest));
            //组装批次表数据
            if (!invoicePush.getSTATUSCODE().equals(oldInvoiceBatchRequest.getStatus())) {
                insertInvoiceBatchRequest.setId(oldInvoiceBatchRequest.getId());
                insertInvoiceBatchRequest.setXhfNsrsbh(oldInvoiceBatchRequest.getXhfNsrsbh());
                insertInvoiceBatchRequest.setMessage(invoicePush.getSTATUSMSG());
                insertInvoiceBatchRequest.setStatus(invoicePush.getSTATUSCODE());
            }else{
            	insertInvoiceBatchRequest = null;
            }
            
            /**
             * 查询发票批次明细表数据
             */
            InvoiceBatchRequestItem oldInvoiceBatchRequestItem = invoiceBatchRequestItemMapper.selectInvoiceBatchItemByKplsh(invoicePush.getFPQQLSH(), shList);
            if (oldInvoiceBatchRequestItem == null) {
                log.error("{}通过开票流水号{}得到批次明细表信息为空!", LOGGER_MSG, invoicePush.getFPQQLSH());
                return R.error(OrderInfoContentEnum.INVOICE_DATA_BATCH_NULL);
            }
            log.info("{}通过批次号得到批次表信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oldInvoiceBatchRequestItem));
    
            //组装发票批次明细信息
            InvoiceBatchRequestItem insertInvoiceBatchRequestItem = new InvoiceBatchRequestItem();
            insertInvoiceBatchRequestItem.setKplsh(invoicePush.getFPQQLSH());
            insertInvoiceBatchRequestItem.setStatus(invoicePush.getSTATUSCODE());
            insertInvoiceBatchRequestItem.setMessage(invoicePush.getSTATUSMSG());
    
    
            /**
             * 查询发票表数据
             */
            OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
            orderInvoiceInfo1.setKplsh(invoicePush.getFPQQLSH());
            OrderInvoiceInfo oldOrderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
    
            if (oldOrderInvoiceInfo == null) {
                log.error("{}发票信息不存在,Kplsh：{}", LOGGER_MSG, invoicePush.getFPQQLSH());
                return R.error(OrderInfoContentEnum.RECEIVE_FAILD);
            }
            log.info("{}根据开票流水号查出发票信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oldOrderInvoiceInfo));
    
    
            /**
             * 开票流水号和发票请求流水号赋值转换 todo
             */
            invoicePush.setFPQQLSH(oldOrderInvoiceInfo.getFpqqlsh());
            invoicePush.setKPLSH(oldOrderInvoiceInfo.getKplsh());
            invoicePush.setNSRSBH(oldOrderInvoiceInfo.getXhfNsrsbh());
            
            // 组装类更新数据库的发票信息
            OrderInvoiceInfo insertOrderInvoiceInfo = convertOrderInvoiceInfo(invoicePush, oldOrderInvoiceInfo);
            log.debug("{} 更新到数据库的发票信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(insertOrderInvoiceInfo));
            
            
            /**
             * 查询处理表
             */
            OrderProcessInfo oldOrderProcessInfo = orderProcessInfoMapper.selectOrderProcessInfoByDdqqlsh(oldOrderInvoiceInfo.getFpqqlsh(), shList);
            
            if (oldOrderProcessInfo == null) {
                log.error("{}通过发票请求流水号{}得到订单处理表信息为空!", LOGGER_MSG, oldOrderInvoiceInfo.getFpqqlsh());
                return R.error(OrderInfoContentEnum.RECEIVE_FAILD);
            }
            log.info("{}根据发票请求流水号查出订单处理表信息:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oldOrderProcessInfo));
            
            OrderProcessInfo insertOrderProcessInfo = new OrderProcessInfo();
            /**
             * 如果订单状态为无效,返回失败,
             */
            if (OrderInfoEnum.ORDER_VALID_STATUS_1.getKey().equals(oldOrderProcessInfo.getOrderStatus())) {
                // TODO: 2019/8/28 后期需要考虑添加报警.
                log.error("{}订单已经被删除,发票请求流水号为:{}", LOGGER_MSG, oldOrderProcessInfo.getFpqqlsh());
                return R.error(OrderInfoContentEnum.RECEIVE_FAILD);
                
            } else {
                
                /**
                 * 获取订单状态
                 */
                String orderStatus = this.dealOrderStatus(invoicePush, oldOrderInvoiceInfo);
                
                //更新orderProcess表
    
                insertOrderProcessInfo.setId(oldOrderInvoiceInfo.getOrderProcessInfoId());
                insertOrderProcessInfo.setSbyy(invoicePush.getSTATUSMSG());
                insertOrderProcessInfo.setDdzt(orderStatus);
    
                // 异常订单发送邮件
                if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderStatus)) {
                    log.debug("发票开具失败，发送异常邮件信息");
                    sendEmailOfInnormalOrder(oldOrderProcessInfo, OrderInfoEnum.ORDER_STATUS_6.getValue(), invoicePush.getSTATUSMSG());
                }
    
                /**
                 * 异常订单允许用户再次进行拆分操作
                 */
                redisService.del(String.format(Constant.REDIS_INVOICE_SPLIT_PREFIX, oldOrderInvoiceInfo.getFpqqlsh()));
    
            }

            boolean pushEmail = false;
            //将发票放入邮箱推送队列
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(oldOrderInvoiceInfo.getFpzlDm()) &&
                    OrderInfoEnum.PUSH_INVOICE_STATUS_2000.getKey().equals(invoicePush.getSTATUSCODE())) {
    
                pushEmail = true;
    
                //数据库发票邮箱发送状态改为发送中
                insertOrderInvoiceInfo.setEmailPushStatus(OrderInfoEnum.EMAIL_PUSH_STATUS_1.getKey());
            }
            
            
            /**
             * 更新订单和发票状态
             */
            try {
                updateInvoiceInfo(insertInvoiceBatchRequest, insertInvoiceBatchRequestItem, insertOrderInvoiceInfo, insertOrderProcessInfo, shList);
                /**
                 * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
                 * 新增发票代码号码与销方税号对应关系
                 *
                 */
                String cacheFpdmHm = String.format(Constant.REDIS_FPDMHM, insertOrderInvoiceInfo.getFpdm() + insertOrderInvoiceInfo.getFphm());
                String xhfNsrsbh = redisService.get(cacheFpdmHm);
                if (StringUtils.isBlank(xhfNsrsbh)) {
                    redisService.set(cacheFpdmHm, insertOrderInvoiceInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                }
    
            } catch (Exception e) {
                log.error("{}订单更新数据库异常,数据回滚,发票请求流水号为:{},详细错误信息为:{}", LOGGER_MSG, oldOrderProcessInfo.getFpqqlsh(), e);
                return R.error(OrderInfoContentEnum.RECEIVE_FAILD);
            }
            //如果是扫码开票 将数据放入插卡队列
            boolean result = OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(oldOrderProcessInfo.getFpzlDm()) && OrderInfoEnum.PUSH_INVOICE_STATUS_2000.getKey().equals(invoicePush.getSTATUSCODE()) &&
                    (OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(oldOrderProcessInfo.getKpfs()) || OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(oldOrderProcessInfo.getKpfs()));
            if (result) {
                iRabbitMqSendMessage.autoSendRabbitMqMessage(oldOrderProcessInfo.getXhfNsrsbh(), NsrQueueEnum.INSERT_CARD_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(invoicePush));
            }
    
            //将需要推送给企业的发票数据放入发票推送队列
            log.info("{},invociePush:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoicePush));
    
            iRabbitMqSendMessage.autoSendRabbitMqMessage(oldOrderProcessInfo.getXhfNsrsbh(), NsrQueueEnum.PUSH_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(invoicePush));
    
            if (pushEmail) {
                //发票邮箱交付请求数据组装
                Map<String, String> map = new HashMap<>(5);
        
                map.put(ConfigureConstant.INVOICE_ID, oldOrderInvoiceInfo.getId());
                map.put(ConfigureConstant.SHLIST, JsonUtils.getInstance().toJsonString(shList));
                log.info("电票开票完成后，发票版式文件邮箱推送，放入队列的参数:{}", JsonUtils.getInstance().toJsonString(map));
                iRabbitMqSendMessage.autoSendRabbitMqMessage(oldOrderProcessInfo.getXhfNsrsbh(), NsrQueueEnum.YXTS_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(map));
        
            }
            // -------------------------结束------------------------
            return R.ok();
        } else {
            log.error("{}接收到的推送信息是空", LOGGER_MSG);
            return R.ok();
        }
    }
    
    /**
     * 发票状态手动回推
     */
    @Override
    public R manualPushInvoice(List<Map> idList) {
    
    
        Set<String> set = new HashSet<>();
        int successCount = 0;
        for (Map map : idList) {
            OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            orderInvoiceInfo1.setId(id);
            OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
            if (orderInvoiceInfo == null) {
                continue;
            }
            //查询当前税号的销方信息是否配置推送地址
            PushInfo queryPushInfo = new PushInfo();
            //推送地址为有效
            queryPushInfo.setStatus("0");
            queryPushInfo.setNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
            //接口类型为发票作废状态推送
            queryPushInfo.setInterfaceType(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_1.getKey());
            List<PushInfo> selectListByPushInfo = pushInfoMapper.selectListByPushInfo(queryPushInfo);
            if (CollectionUtils.isEmpty(selectListByPushInfo)) {
                set.add(orderInvoiceInfo.getXhfNsrsbh());
                continue;
            }
    
            /**
             * 获取批次号,用户电票获取pdf
             */
            List<InvoiceBatchRequestItem> invoiceBatchRequestItem = invoiceBatchRequestItemMapper.selectInvoiceBatchItemByFpqqlsh(orderInvoiceInfo.getFpqqlsh(), shList);
            if(invoiceBatchRequestItem==null){
                return R.error("批量开票明细信息没有查询到");
            }
            InvoicePush invoicePush = BeanTransitionUtils.transitionInvoicePush(orderInvoiceInfo, invoiceBatchRequestItem.get(0).getFpqqpch());
            //将需要推送给企业的发票数据放入发票推送队列
            log.info("{}invociePush:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoicePush));
            
            iRabbitMqSendMessage.autoSendRabbitMqMessage(orderInvoiceInfo.getXhfNsrsbh(), NsrQueueEnum.PUSH_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(invoicePush));
            successCount++;

        }
        
        if(CollectionUtils.isNotEmpty(set)){
        	 String errorMessage = "";
     	    for(String nsrsbh : set) {
                errorMessage = new StringBuilder().append(errorMessage).append(",").append(nsrsbh).toString();
            }
     	    if(errorMessage.startsWith(",")){
       
                errorMessage = errorMessage.substring(1);
            }
     	    errorMessage = "销售方税号:" + errorMessage + "推送地址未配置";
        	if(successCount == 0){
        
                return R.error().put(OrderManagementConstant.MESSAGE, errorMessage);
        		
        	}else{
        		errorMessage = "已推送:" + successCount + "条;" + errorMessage;
        		return R.error().put(OrderManagementConstant.MESSAGE, errorMessage);
        	}
        }else{
        	return R.ok();
        }

        
    }
    
    /**
     * 处理发票回推状态更新
     *
     * @param invoiceBatchRequest
     * @param invoiceBatchRequestItem
     * @param orderInvoiceInfo
     * @param orderProcessInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoiceInfo(InvoiceBatchRequest invoiceBatchRequest, InvoiceBatchRequestItem invoiceBatchRequestItem, OrderInvoiceInfo orderInvoiceInfo, OrderProcessInfo orderProcessInfo, List<String> shList) {
        
        /**
         * 更新数据
         * 1.更新发票请求批次表数据
         * 2.更新发票请求明细表数据
         * 3.更新发票表数据
         * 4.更新订单处理表数据
         *
         *
         */
        
        if (invoiceBatchRequest != null) {
            log.info("{}更新批次表，参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoiceBatchRequest));
            invoiceBatchRequest.setUpdateTime(new Date());
            int updateBatchStatusByFpqpch = invoiceBatchRequestMapper.updateInvoiceBatchRequest(invoiceBatchRequest, shList);
            if (updateBatchStatusByFpqpch == 0) {
                log.error("{}修改发票批次表失败,数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoiceBatchRequest));
            }
        }
    
        if (invoiceBatchRequestItem != null) {
            invoiceBatchRequestItem.setUpdateTime(new Date());
            int batchItem = invoiceBatchRequestItemMapper.updateInvoiceBatchItemByKplsh(invoiceBatchRequestItem, shList);
            if (batchItem == 0) {
                log.error("{}依据底层推送结果,修改批次关系表失败,数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoiceBatchRequestItem));
            }
        }
    
        log.info("{}发票表数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
    
        if (orderInvoiceInfo != null) {
            orderInvoiceInfo.setUpdateTime(new Date());
            int i = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo,shList);
            if (i == 0) {
                log.error("{}修改发票状态失败,数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
            }
        }
    
        if (orderProcessInfo != null) {
        	orderProcessInfo.setUpdateTime(new Date());
            int updateOrderProcessInfoByProcessId = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo,shList);
            if (updateOrderProcessInfoByProcessId <= 0) {
                log.error("{}更新订单状态失败", LOGGER_MSG);
            }
        }
    
    
        /**
         * 如果是红票的话 更新原蓝票的冲红标志和剩余可冲红金额
         */
        if (orderInvoiceInfo != null && OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
            dealRedInvoice(orderInvoiceInfo, orderInvoiceInfo.getKpzt(),shList);
        }
    
        if (orderInvoiceInfo != null && OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx()) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
    
            log.info("{}更新红字信息表数据状态:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
            /**
             * 红字专票的话 更新红字申请单的状态
             */
            SpecialInvoiceReversalEntity specialInvoiceReversal = new SpecialInvoiceReversalEntity();
            specialInvoiceReversal.setXxbbh(orderInvoiceInfo.getHzxxbbh());
            specialInvoiceReversal.setFpdm(orderInvoiceInfo.getFpdm());
            specialInvoiceReversal.setFphm(orderInvoiceInfo.getFphm());
    
            if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt())) {
                log.info("{}更新红字信息表数据状态为开票成功,红字信息表编号为:{}", LOGGER_MSG, orderInvoiceInfo.getHzxxbbh());
                specialInvoiceReversal.setKpzt(OrderInfoEnum.SPECIAL_INVOICE_STATUS_2.getKey());
                int updateSpecialInvoiceReversalByCode = specialInvoiceReversalDao.updateInvoiceStatusByXxbbh(specialInvoiceReversal);
                if (updateSpecialInvoiceReversalByCode <= 0) {
                    log.error("更新红字申请单状态失败,红字信息表编号:{}", specialInvoiceReversal.getXxbbh());
                }
            } else if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(orderInvoiceInfo.getKpzt())) {
                log.info("{}更新红字信息表数据状态为开票失败,红字信息表编号为:{}", LOGGER_MSG, orderInvoiceInfo.getHzxxbbh());
                specialInvoiceReversal.setKpzt(OrderInfoEnum.SPECIAL_INVOICE_STATUS_3.getKey());
                int updateSpecialInvoiceReversalByCode = specialInvoiceReversalDao.updateInvoiceStatusByXxbbh(specialInvoiceReversal);
                if (updateSpecialInvoiceReversalByCode <= 0) {
                    log.error("更新红字申请单状态失败,申请单编号:{}", specialInvoiceReversal.getXxbbh());
                }
            }
        }
    
    
    }
    
    
    /**
     * 更新原蓝票的冲红标志 和剩余可冲红金额
     *
     * @param orderInvoiceInfo
     */
    
    public void dealRedInvoice(OrderInvoiceInfo orderInvoiceInfo, String kpzt, List<String> shList) {
        log.info("更新原蓝票的冲红标志和剩余可冲红金额,{}", JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
        //如果是开红票的话
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
        if (orderInfo == null) {
            
            log.error("{}未查询到原红票订单信息！！！", LOGGER_MSG);
            
        } else {
            
            if (StringUtils.isNotEmpty(orderInfo.getYfpDm()) && StringUtils.isNotEmpty(orderInfo.getYfpHm())) {
                /**
                 * 根据红票订单信息获取原蓝票发票代码号码,查询原蓝票数据
                 */
                OrderInvoiceInfo blueInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(orderInfo.getYfpDm(), orderInfo.getYfpHm(),shList);
            
            
                if (blueInvoiceInfo != null) {
                    //红票冲红只更新发票表的冲红标志
                    updateChbzAndSykchje(orderInfo, blueInvoiceInfo, kpzt,shList);
                }
            } else {
                log.error("查询到原红票订单信息，但是没有代码号码！！！");
            }
        }
    }
    
    /**
     * 更新冲红标志和剩余可冲红金额
     *
     * @param orderInfo
     * @param blueInvoiceInfo
     * @param kpzt
     */
    private void updateChbzAndSykchje(OrderInfo orderInfo, OrderInvoiceInfo blueInvoiceInfo, String kpzt, List<String> shList) {
        
        // 查询原蓝票的发票信息
        
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        //计算剩余可冲红金额
        
        String sykchje = blueInvoiceInfo.getSykchje();
        log.debug("原蓝票剩余可冲红金额:{},价税合计:{}", sykchje, blueInvoiceInfo.getKphjje());
        
        if (StringUtils.isBlank(sykchje)) {
            sykchje = blueInvoiceInfo.getKphjje();
        }
    
        // 剩余可冲红金额等于 上次剩余可冲红金额减去本次红票金额(因为剩余可充红金额为整数,当前发票数据为红票数据,金额为负,所以应该相加)
        sykchje = new BigDecimal(sykchje).add(new BigDecimal(orderInfo.getKphjje()))
                .setScale(2, RoundingMode.HALF_UP).toString();
    
        if (Double.parseDouble(sykchje) < 0) {
            log.error("{}冲红失败:orderId:{}", LOGGER_MSG, orderInfo.getId());
        } else {
            /**
             * 补全冲红标志
             */
            if (StringUtils.isBlank(blueInvoiceInfo.getChBz()) || OrderInfoEnum.RED_INVOICE_0.getKey().equals(blueInvoiceInfo.getChBz())) {
                if (Double.parseDouble(sykchje) <= 0) {
                    blueInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_2.getKey());
                } else {
                    blueInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_5.getKey());
                }
            
            }
            if (OrderInfoEnum.RED_INVOICE_2.getKey().equals(blueInvoiceInfo.getChBz()) || OrderInfoEnum.RED_INVOICE_3.getKey().equals(blueInvoiceInfo.getChBz())) {
                //全部冲红中
                if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
                    orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_1.getKey());
                    orderInvoiceInfo.setSykchje(sykchje);
                } else {
                    orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_3.getKey());
                }
            } else if (OrderInfoEnum.RED_INVOICE_5.getKey().equals(blueInvoiceInfo.getChBz()) || OrderInfoEnum.RED_INVOICE_6.getKey().equals(blueInvoiceInfo.getChBz())) {
                //部分冲红中
                if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
                    orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_4.getKey());
                    if (Double.valueOf(sykchje).equals(BigDecimal.ZERO.doubleValue())) {
                        orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_1.getKey());
                    }
                    orderInvoiceInfo.setSykchje(sykchje);
                } else {
                    orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_6.getKey());
                }
            }
        
            // 更新发票的冲红标志和剩余可冲红金额
            orderInvoiceInfo.setId(blueInvoiceInfo.getId());
            orderInvoiceInfo.setUpdateTime(new Date());
            int updateFlag = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo,shList);
            if (updateFlag <= 0) {
                log.error("{},修改orderInvoiceInfo表剩余可充红金额失败,sykchje:{}", LOGGER_MSG, sykchje);
            }
        }
    
        
    
    }

    /**
     * 组装入库发票数据
     */
    public OrderInvoiceInfo convertOrderInvoiceInfo(InvoicePush invoicePush, OrderInvoiceInfo selectInvoiceInfoByKplsh) {
    
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
    
        /**
         * 1.判断返回的状态码是否为2000，是则直接进行后续
         * 2.判断是否为2100 并且为普票或者专票 是则进入
         * 3.修改开票的冲红标志，开票状态，失败原因，并修改电票的PDF路径
         * 4.如果为红票修改原蓝票的 可冲红金额，以及原蓝票的冲红标志
         * 5.电票2100 修改为开票中，此后是签章，一般是签章没成功
         */
        boolean result = OrderInfoEnum.PUSH_INVOICE_STATUS_2000.getKey().equals(invoicePush.getSTATUSCODE()) ||
                (OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey().equals(invoicePush.getSTATUSCODE())
                        && (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(selectInvoiceInfoByKplsh.getFpzlDm())
                        || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(selectInvoiceInfoByKplsh.getFpzlDm())));
        if (result) {
            orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_0.getKey());
            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(selectInvoiceInfoByKplsh.getFpzlDm())) {
                orderInvoiceInfo.setPdfUrl(invoicePush.getPDF_URL());
            }
            orderInvoiceInfo.setSbyy("");
        }
    
    
        if (OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey().equals(invoicePush.getSTATUSCODE())
                && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(selectInvoiceInfoByKplsh.getFpzlDm())) {
            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_1.getKey());
        }
        // 开具中
        if (OrderInfoEnum.PUSH_INVOICE_STATUS_1000.getKey().equals(invoicePush.getSTATUSCODE())
                || OrderInfoEnum.PUSH_INVOICE_STATUS_1001.getKey().equals(invoicePush.getSTATUSCODE())) {
            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_1.getKey());
        }
        // 开具失败
        if (OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey().equals(invoicePush.getSTATUSCODE())
                || OrderInfoEnum.PUSH_INVOICE_STATUS_2101.getKey().equals(invoicePush.getSTATUSCODE())) {
            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
            orderInvoiceInfo.setSbyy(invoicePush.getSTATUSMSG());
        }
        if (OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey().equals(invoicePush.getSTATUSCODE())
                && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(selectInvoiceInfoByKplsh.getFpzlDm())) {
            orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
            orderInvoiceInfo.setSbyy(invoicePush.getSTATUSMSG());
        }
    
    
        orderInvoiceInfo.setKplsh(invoicePush.getKPLSH());
        orderInvoiceInfo.setDdh(invoicePush.getDDH());
        orderInvoiceInfo.setJym(invoicePush.getJYM());
        orderInvoiceInfo.setEwm(invoicePush.getEWM());
        orderInvoiceInfo.setFwm(invoicePush.getFWM());
        orderInvoiceInfo.setJqbh(invoicePush.getJQBH());
        orderInvoiceInfo.setFpdm(invoicePush.getFP_DM());
        orderInvoiceInfo.setFphm(invoicePush.getFP_HM());
        orderInvoiceInfo.setSld(invoicePush.getSLDID());
        orderInvoiceInfo.setSldMc(invoicePush.getSLDMC());
        orderInvoiceInfo.setFjh(invoicePush.getFJH());
        orderInvoiceInfo.setHjbhsje(selectInvoiceInfoByKplsh.getHjbhsje());
        orderInvoiceInfo.setKpse(selectInvoiceInfoByKplsh.getKpse());
        orderInvoiceInfo.setKprq(invoicePush.getKPRQ());
        orderInvoiceInfo.setOrderInfoId(selectInvoiceInfoByKplsh.getOrderInfoId());
        orderInvoiceInfo.setId(selectInvoiceInfoByKplsh.getId());
        orderInvoiceInfo.setUpdateTime(new Date());
        orderInvoiceInfo.setXhfNsrsbh(selectInvoiceInfoByKplsh.getXhfNsrsbh());
        orderInvoiceInfo.setKplx(selectInvoiceInfoByKplsh.getKplx());
        orderInvoiceInfo.setChyy(selectInvoiceInfoByKplsh.getChyy());
        orderInvoiceInfo.setHzxxbbh(selectInvoiceInfoByKplsh.getHzxxbbh());
        orderInvoiceInfo.setFpzlDm(selectInvoiceInfoByKplsh.getFpzlDm());
        return orderInvoiceInfo;
    }
	
	/**
	 * 发送邮件
	 *
	 */
    private void sendEmailOfInnormalOrder(OrderProcessInfo orderProcessInfo, String statusRemark, String sbyy) {
		log.debug("异常订单邮件发送");
        boolean reslut = redisService.set(ConfigureConstant.SALERWARNING + orderProcessInfo.getFpqqlsh(), orderProcessInfo.getFpqqlsh(), 7200);
		if (reslut) {
            // 构造邮件实体
            log.debug("异常订单邮件发送");
            // 根据纳税人识别号获取预警邮箱
            List<SalerWarning> salerWarningList = salerWarningService.selectSalerWaringByNsrsbh(orderProcessInfo.getXhfNsrsbh(), null);
            if (salerWarningList != null && salerWarningList.size() > 0) {
                for (SalerWarning salerWarning : salerWarningList) {
                    if (StringUtils.isNotEmpty(salerWarning.getWaringEmail()) && OrderInfoEnum.ORDER_WARNING_OPEN.getKey().equals(salerWarning.getWarningFlag())) {
                        // 提取预警邮箱,并构建邮件实体
                        String[] toArr = salerWarning.getWaringEmail().split(";");
                        EmailContent emailContent = new EmailContent();
                        emailContent.setTemplateId(OpenApiConfig.invoiceYiChang);
                        emailContent.setSerialNum(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
                        emailContent.setSubjects(new String[]{orderProcessInfo.getDdh()});
                        emailContent.setContents(new String[]{orderProcessInfo.getDdh(), statusRemark, orderProcessInfo.getKphjje(),
                                DateUtilsLocal.getYMDHMIS(orderProcessInfo.getCreateTime()), getInvoiceRemark(orderProcessInfo.getFpzlDm()), sbyy});
                        emailContent.setTo(toArr);
                        // 发送邮件
                        apiEmailService.sendEmail(JsonUtils.getInstance().toJsonString(emailContent));
                    }
                }
                
                
            }
            
        }
	}

	/**
	 * 获取发票类型的中文说明
	 *
	 * @param fplx
	 * @return
	 */
    private String getInvoiceRemark(String fplx) {
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fplx)) {
            return OrderInfoEnum.ORDER_INVOICE_TYPE_0.getValue();
        }
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fplx)) {
            return OrderInfoEnum.ORDER_INVOICE_TYPE_2.getValue();
        }
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fplx)) {
            return OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue();
        }
        return fplx;
    }
    
    /**
     * 根据返回结果获取当前发票的订单状态
     * @param invoicePush
     * @param orderInvoiceInfo
     * @return
     */
    private String dealOrderStatus(InvoicePush invoicePush, OrderInvoiceInfo orderInvoiceInfo) {
        /**
         * 根据推送结果获取对应订单状态,
         * 1.默认订单状态为开票中,
         * 2.如果开票类型为红票,
         *  开票返回赋码成功和开票成功需要区分对应状态,
         *  如果是电票并且状态为赋码成功,则需要为开票中.
         *  其他情况为红票开票成功
         *  如果是赋码失败或者是开票失败,都为冲红失败
         *  如果是初始化或者是调用税控失败,则为开票中.
         *
         * 3.如果开票类型为蓝票
         *  开票返回赋码成功和开票成功需要区分对应状态,
         *  如果是电票并且状态为赋码成功,则需要为开票中.
         *  其他情况为开票成功
         *  如果是赋码失败或者是开票失败,都为开票失败
         *  如果是初始化或者是调用税控失败,则为开票中.
         */
        String orderStatus = OrderInfoEnum.ORDER_STATUS_4.getKey();
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
        
            if (OrderInfoEnum.PUSH_INVOICE_STATUS_2000.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
            
                // 是51且code是赋码成功的开票状态为开票中
                if (OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey().equals(invoicePush.getSTATUSCODE())
                        && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey()
                        .equals(orderInvoiceInfo.getFpzlDm())) {
                    orderStatus = OrderInfoEnum.ORDER_STATUS_9.getKey();
                } else {
                    orderStatus = OrderInfoEnum.ORDER_STATUS_7.getKey();
                }
    
            } else if (OrderInfoEnum.PUSH_INVOICE_STATUS_2101.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_8.getKey();
    
            } else if (OrderInfoEnum.PUSH_INVOICE_STATUS_1000.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_1001.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_9.getKey();
            }


            //电子发票签章失败
            if (OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey().equals(invoicePush.getSTATUSCODE())
                    && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey()
                    .equals(orderInvoiceInfo.getFpzlDm())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_8.getKey();
            }
    
        } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
            if (OrderInfoEnum.PUSH_INVOICE_STATUS_2000.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
            
                orderStatus = OrderInfoEnum.ORDER_STATUS_5.getKey();
                // 是51且code是赋码成功的开票状态为开票中
                if (OrderInfoEnum.PUSH_INVOICE_STATUS_2100.getKey().equals(invoicePush.getSTATUSCODE())
                        && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey()
                        .equals(orderInvoiceInfo.getFpzlDm())) {
                    orderStatus = OrderInfoEnum.ORDER_STATUS_4.getKey();
                }
            } else if (OrderInfoEnum.PUSH_INVOICE_STATUS_2101.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_6.getKey();
    
            } else if (OrderInfoEnum.PUSH_INVOICE_STATUS_1000.getKey().equals(invoicePush.getSTATUSCODE())
                    || OrderInfoEnum.PUSH_INVOICE_STATUS_1001.getKey()
                    .equals(invoicePush.getSTATUSCODE())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_4.getKey();
            }

            //电子发票签章失败
            if (OrderInfoEnum.PUSH_INVOICE_STATUS_2001.getKey().equals(invoicePush.getSTATUSCODE())
                    && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey()
                    .equals(orderInvoiceInfo.getFpzlDm())) {
                orderStatus = OrderInfoEnum.ORDER_STATUS_6.getKey();
            }
    
        } else {
            log.error("{}其他开票类型,不支持!", LOGGER_MSG);
        }
        return orderStatus;
    }

    
    
}
