package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.model.OderDetailInfo;
import com.dxhy.order.consumer.model.PageOrderExt;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.modules.order.service.IOrderInfoService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.PageDataDealUtil;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
/**
 * 订单信息业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:24
 */
@Slf4j
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    private static final String LOGGER_MSG = "(订单查询相关service)";
    
    @Reference
    private ApiOrderProcessInfoExtService apiOrderProcessInfoExtService;
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    @Reference
    private ApiOrderProcessService apiOrderProcessService;

    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;

    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;

    @Resource
    private ICommonInterfaceService commonInterfaceService;

    @Resource
    private InvoiceService invoiceService;

    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    @Override
    public PageUtils selectOrderInfo(Map map, List<String> shList) {
        return apiOrderProcessService.selectOrderInfo(map, shList);
    }

    @Override
    public OderDetailInfo selectOrderDetailByOrderProcessIdAndFpqqlsh(String orderProcessId, String fpqqlsh, List<String> shList) {
        /**
         *
         * 1.根据processId 查询 处理订单信息
         * 2.根据processId 查询扩展表的父类订单id
         * 3.根据父订单id 查询原始订单号
         * 4.根据订单号查询订单明细
         * 5.组装返回
         */
    
        OderDetailInfo oderDetailInfo = new OderDetailInfo();

        /**
         *  购货方企业类型 统一修改为03
         */
    
        if (OrderInfoEnum.GHF_QYLX_04.getKey().equals(oderDetailInfo.getGhfQylx())) {
            oderDetailInfo.setGhfQylx(OrderInfoEnum.GHF_QYLX_03.getKey());
        }
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
    
        OrderProcessInfo orderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(orderProcessId, shList);
    
        log.info("{}根据Processid查询OrderProcess id为：{}", LOGGER_MSG, orderProcessId);
        if (orderProcessInfo == null) {
            log.error("{}查询OrderProcessid为空，id为：{}", LOGGER_MSG, orderProcessId);
            return null;
        }
        /**
         * 1.根据订单处理表中数据判断是否是已开具,如果是已开具需要调用开具接口返回已开具发票信息
         */
        if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt())) {
    
            orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(fpqqlsh, shList);
    
            log.debug("{}根据流水号:{},查询订单发票数据,返回信息:{}", LOGGER_MSG, fpqqlsh, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
        }
    
    
        OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
    
        log.info("{}根据Processid查询原始订单{}", LOGGER_MSG, orderProcessInfo.getOrderInfoId());
        
        if (orderInfo == null) {
            log.error("{}查询原始订单为空，id为：{}", LOGGER_MSG, orderProcessInfo.getOrderInfoId());
            return null;
        }
    
        /**
         * 1.根据当前订单id获取对应父订单扩展数据.
         * 2.循环遍历父订单数据,
         * 3.判断父订单数据长度,如果为多条正常返回数据
         * 4.如果为一条需要判断订单号是否相同,如果相同则说明是编辑订单不进行展示原始订单数据,如果不相同,继续遍历获取父订单数据,
         */
    
        List<PageOrderExt> pageOrderExts = new ArrayList<>();
    
        getOrderProcessInfoExtInfo(orderProcessInfo, pageOrderExts, shList);
    
        List<OrderItemInfo> orderItemList = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
    
        log.debug("{}根据订单id:{},查询订单明细:{}", LOGGER_MSG, orderProcessInfo.getOrderInfoId(), JsonUtils.getInstance().toJsonString(orderItemList));
    
        oderDetailInfo = this.convertOderDetailInfo(orderProcessInfo, orderItemList, orderInfo, pageOrderExts, orderInvoiceInfo);
    
        oderDetailInfo.setOrderItemInfo(orderItemList);
        /**
         * 格式化单价，数量和金额
         */
        formatCommonOrder(oderDetailInfo);
    
        log.debug("{}返回前端订单详情为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oderDetailInfo));
    
        return oderDetailInfo;
    }
    
    
    public void getOrderProcessInfoExtInfo(OrderProcessInfo orderProcessInfo, List<PageOrderExt> pageOrderExts, List<String> shList) {
        
        /**
         * 判断当前订单是拆分后订单还是合并后订单
         * 如果查询到的数据大于2,说明是合并后订单,
         */
        
        List<OrderProcessInfoExt> orderProcessInfoExtList = apiOrderProcessInfoExtService.selectOrderProcessInfoExtByOrderProcessId(orderProcessInfo.getId(), shList);
        log.debug("{}根据Processid查询父类订单号:{},返回信息:{}", LOGGER_MSG, orderProcessInfo.getId(), JsonUtils.getInstance().toJsonString(orderProcessInfoExtList));
        
        if (orderProcessInfoExtList != null && orderProcessInfoExtList.size() > 0) {
            for (OrderProcessInfoExt processInfoExt : orderProcessInfoExtList) {
        
                PageOrderExt pageOrderExt = new PageOrderExt();
                OrderProcessInfoExt orderProcessInfoExt = processInfoExt;
                if (orderProcessInfoExt != null) {
                    OrderProcessInfo parentOrderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(orderProcessInfoExt.getParentOrderProcessId(), shList);
                    log.debug("{}根据父类订单号:{},获取订单返回信息:{}", LOGGER_MSG, processInfoExt.getParentOrderInfoId(), JsonUtils.getInstance().toJsonString(parentOrderProcessInfo));
                    if (parentOrderProcessInfo != null) {
                
                        if (orderProcessInfoExt.getOrderProcessInfoId().equals(orderProcessInfoExt.getParentOrderProcessId())) {
                            log.info("{}扩展表数据处理表id和父处理表id一致,不进行处理.", LOGGER_MSG);
                    
                    
                        } else {
                    
                            pageOrderExt.setDdh(parentOrderProcessInfo.getDdh());
                            pageOrderExt.setDdzt(parentOrderProcessInfo.getDdzt());
                            pageOrderExt.setFpqqlsh(parentOrderProcessInfo.getFpqqlsh());
                            pageOrderExt.setOrderId(parentOrderProcessInfo.getId());
                            pageOrderExts.add(pageOrderExt);
                    
                    
                        }
                    }
                }
            }

        }


    }


    /**
     * 订单详情数据组装
     *
     * @param orderProcessInfo
     * @param list
     * @param orderInfo
     * @param pageOrderExts
     * @return
     */
    private OderDetailInfo convertOderDetailInfo(OrderProcessInfo orderProcessInfo, List<OrderItemInfo> list, OrderInfo orderInfo, List<PageOrderExt> pageOrderExts, OrderInvoiceInfo orderInvoiceInfo) {
        OderDetailInfo oderDetailInfo = new OderDetailInfo();
        oderDetailInfo.setKplx(orderInfo.getKplx());
        oderDetailInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        oderDetailInfo.setDdh(orderProcessInfo.getDdh());
        oderDetailInfo.setDdcjsj(orderProcessInfo.getDdcjsj());
        oderDetailInfo.setDdzt(orderProcessInfo.getDdzt());
        oderDetailInfo.setDdly(orderProcessInfo.getDdly());
        oderDetailInfo.setFpzlDm(orderProcessInfo.getFpzlDm());
        oderDetailInfo.setGhfSj(orderInfo.getGhfSj());
        oderDetailInfo.setGhfQylx(orderInfo.getGhfQylx());
        oderDetailInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        oderDetailInfo.setGhfMc(orderProcessInfo.getGhfMc());
        oderDetailInfo.setGhfDz(orderInfo.getGhfDz());
        oderDetailInfo.setGhfDh(orderInfo.getGhfDh());
        oderDetailInfo.setGhfYh(orderInfo.getGhfYh());
        oderDetailInfo.setGhfZh(orderInfo.getGhfZh());


//        添加销方信息
        oderDetailInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        oderDetailInfo.setXhfMc(orderProcessInfo.getXhfMc());
        oderDetailInfo.setXhfDz(orderInfo.getXhfDz());
        oderDetailInfo.setXhfDh(orderInfo.getXhfDh());
        oderDetailInfo.setXhfYh(orderInfo.getXhfYh());
        oderDetailInfo.setXhfZh(orderInfo.getXhfZh());
        
        oderDetailInfo.setKphjje(orderProcessInfo.getKphjje());
        oderDetailInfo.setHjbshje(orderProcessInfo.getHjbhsje());
        oderDetailInfo.setSe(orderProcessInfo.getKpse());
        oderDetailInfo.setKpxm(orderProcessInfo.getKpxm());
        oderDetailInfo.setMdh(orderInfo.getMdh());
        oderDetailInfo.setOrderId(orderInfo.getId());
        oderDetailInfo.setOrderItemInfo(list);
        oderDetailInfo.setPageOrderExts(pageOrderExts);
        oderDetailInfo.setOrderProcessId(orderProcessInfo.getId());
        oderDetailInfo.setSbyy(orderProcessInfo.getSbyy());
        oderDetailInfo.setYwlx(orderProcessInfo.getYwlx());
        oderDetailInfo.setYwlxId(orderProcessInfo.getYwlxId());
        oderDetailInfo.setGhfEmail(orderInfo.getGhfEmail());
        oderDetailInfo.setFpdm(orderInvoiceInfo.getFpdm());
        oderDetailInfo.setFphm(orderInvoiceInfo.getFphm());
        oderDetailInfo.setJym(orderInvoiceInfo.getJym());
        oderDetailInfo.setKprq(DateUtil.format(orderInvoiceInfo.getKprq(),"yyyy-MM-dd HH:mm:ss"));
        oderDetailInfo.setKplsh(orderInvoiceInfo.getKplsh());
        oderDetailInfo.setOrderStatus(orderProcessInfo.getOrderStatus());
        oderDetailInfo.setKpr(orderInfo.getKpr());
        oderDetailInfo.setFhr(orderInfo.getFhr());
        oderDetailInfo.setSkr(orderInfo.getSkr());
        oderDetailInfo.setChyy(orderInfo.getChyy());
        oderDetailInfo.setBz(orderInfo.getBz());
        oderDetailInfo.setQdbz(orderInfo.getQdBz());
        oderDetailInfo.setCheckStatus(orderProcessInfo.getCheckStatus());
        oderDetailInfo.setCheckTime(DateUtil.format(orderProcessInfo.getCheckTime(),"yyyy-MM-dd HH:mm:ss"));
        return oderDetailInfo;
    }
    

    @Override
    public Map updateOrderInfoAndOrderProcessInfo(CommonOrderInfo commonOrderInfo) throws OrderReceiveException {
        /**
         * 1.查询订单表数据,看下数据是否存在,不存在返回失败
         * 2.组装订单信息
         * 3.查询订单处理表数据,看下数据是否存在,不存在返回失败
         * 4.组装订单处理表信息
         * 5.组装订单明细信息
         * 6.更新订单表,订单处理表,删除订单明细表数据,插入新的订单明细数据
         */
        Map<String, String> map = new HashMap<>(5);
        map.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
    
        CommonOrderInfo commonOrderInfoResult;
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        List<String> shList = new ArrayList<>();
        shList.add(orderInfo.getXhfNsrsbh());
    
        OrderInfo oldOrderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderInfo.getId(), shList);
        if (oldOrderInfo == null) {
            log.error("{}根据原订单id:{}查询订单数据失败,数据为空!", LOGGER_MSG, orderInfo.getId());
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
        }
    
        coverOrderInfo(oldOrderInfo, orderInfo);
    
    
        log.info("{}处理订单明细数据", LOGGER_MSG);
    
        List<OrderItemInfo> orderItemInfoList = coverOrderItemInfo(oldOrderInfo, commonOrderInfo.getOrderItemInfo());

        String qdbz = CommonUtils.getQdbz(oldOrderInfo.getQdBz(), orderItemInfoList.size());
        oldOrderInfo.setQdBz(qdbz);
        if("1".equals(qdbz) && StringUtils.isBlank(orderInfo.getQdXmmc())){
            oldOrderInfo.setQdXmmc(ConfigureConstant.XJXHQD);
        }else if("0".equals(qdbz) && StringUtils.isNotBlank(orderInfo.getQdXmmc())){
            oldOrderInfo.setQdXmmc("");
        }

        String terminalCode = apiTaxEquipmentService.getTerminalCode(oldOrderInfo.getXhfNsrsbh());
        /**
         * 补全商品信息
         */
        commonInterfaceService.dealOrderItem(orderItemInfoList, oldOrderInfo.getXhfNsrsbh(), oldOrderInfo.getQdBz(), terminalCode);

        /**
         * 订单处理表数据处理
         */
        OrderProcessInfo oldOrderProcessInfo = apiOrderProcessService.selectOrderProcessInfoByProcessId(oldOrderInfo.getProcessId(), shList);
        log.info("{}根据ProcessId查询，id:{}", LOGGER_MSG, oldOrderInfo.getProcessId());
        if (oldOrderProcessInfo == null) {
            log.error("{}根据id未找到订单信息，id:{}", LOGGER_MSG, oldOrderInfo.getProcessId());
            throw new OrderReceiveException(OrderInfoContentEnum.PARAM_NULL);
        }
    
    
        /**
         * 订单发票表数据处理
         */
        OrderInvoiceInfo oldOrderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(oldOrderInfo.getFpqqlsh(), shList);
        log.info("{}根据发票请求流水号查询，流水号:{}", LOGGER_MSG, oldOrderInfo.getFpqqlsh());
    
    
        /**
         * 异常订单逻辑处理 (2101:赋码失败;2100:赋码成功;2001:签章失败;2000:签章成功;4001:未知异常),2101换流水号,其他都不换,2001时提示开票失败,不允许编辑,不换流水号直接重试)
         */
        boolean result = ConfigureConstant.STRING_1.equals(commonOrderInfo.getIsExceptionEdit()) && (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(oldOrderProcessInfo.getDdzt())
                || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(oldOrderProcessInfo.getDdzt()));
        if (result) {
    
    
            oldOrderProcessInfo.setEditStatus(OrderInfoEnum.EDIT_STATUS_1.getKey());
        }
        //更新审核时间
        boolean result2 = OrderInfoEnum.ORDER_STATUS_6.getKey().equals(oldOrderProcessInfo.getDdzt())
                || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(oldOrderProcessInfo.getDdzt()) && OrderInfoEnum.ORDER_SOURCE_5.getKey().equals(oldOrderProcessInfo.getDdly());
        if (result2) {
        
            oldOrderProcessInfo.setCheckTime(new Date());
        }
    
        /**
         * 编辑后数据做下税分离,做订单校验,通过后再进行保存入库
         */
        try {
            commonOrderInfo.setOrderInfo(oldOrderInfo);
            commonOrderInfo.setOrderItemInfo(orderItemInfoList);
            commonOrderInfoResult = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, new TaxSeparateConfig());
    
            /**
             * 税控设备类型添加到订单主信息中
             */
            commonOrderInfo.setTerminalCode(terminalCode);
    
            map = validateOrderInfo.checkOrderInvoice(commonOrderInfoResult);
            if (!ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.ERRORCODE))) {
                log.info("{} 订单校验失败：订单号：{}，失败原因：{}，错误代码：{}", LOGGER_MSG,
                        commonOrderInfoResult.getOrderInfo().getDdh(), map.get(OrderManagementConstant.ERRORMESSAGE), map.get(OrderManagementConstant.ERRORCODE));
                throw new OrderReceiveException(map.get(OrderManagementConstant.ERRORCODE), map.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
        } catch (OrderSeparationException e) {
            throw new OrderReceiveException(e.getCode(),e.getMessage());
        }
    
        if (commonOrderInfoResult != null) {
            coverOrderProcessInfo(oldOrderProcessInfo, commonOrderInfoResult.getOrderInfo());
    
            log.info("{}需要更新OrderProcess信息为{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(oldOrderProcessInfo));
    
    
            if (oldOrderInvoiceInfo != null) {
                coverOrderInvoiceInfo(oldOrderInvoiceInfo, commonOrderInfoResult.getOrderInfo());
            }
            /**
             * 更新数据,此处添加事物
             */
            updateDbOrderInfos(oldOrderInvoiceInfo, oldOrderProcessInfo, commonOrderInfoResult.getOrderInfo(), commonOrderInfoResult.getOrderItemInfo(), shList);
    
            map.put(Constant.ORDERINFOID, oldOrderProcessInfo.getOrderInfoId());
            map.put(Constant.ORDERID, oldOrderProcessInfo.getId());
            map.put(Constant.FPQQLSH, oldOrderProcessInfo.getFpqqlsh());
        } else {
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
        }

        
        return map;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void updateDbOrderInfos(OrderInvoiceInfo orderInvoiceInfo, OrderProcessInfo orderProcessInfo, OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos, List<String> shList) throws OrderReceiveException {
        
        //更新订单处理表数据
        orderProcessInfo.setUpdateTime(new Date());
        int insertRow = apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
        
        if (insertRow <= 0) {
            log.error("{}插入OrderProcess失败", LOGGER_MSG);
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
        }
        
        if (orderInvoiceInfo != null) {
            //更新订单发票表数据
            orderInvoiceInfo.setUpdateTime(new Date());
            int insertInvoiceRow = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
            
            if (insertInvoiceRow <= 0) {
                log.error("{}插入OrderInvoice失败", LOGGER_MSG);
                throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
            }
        }
        
        
        //更新订单表数据
        orderInfo.setUpdateTime(new Date());
        int orderInfoCount = apiOrderInfoService.updateOrderInfoByOrderId(orderInfo, shList);
        
        if (orderInfoCount <= 0) {
            log.error("{}插入OrderInfo失败", LOGGER_MSG);
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
        }
        
        
        //删除明细表数据
        int deleteOrderItemInfoByOrderId = apiOrderItemInfoService.deleteOrderItemInfoByOrderId(orderInfo.getId(), shList);
        
        if (deleteOrderItemInfoByOrderId < 0) {
            log.error("{}根据OrderId删除orderItemInfo表失败!", LOGGER_MSG);
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
            
        }
        
        //插入明细表数据
        int insertItem = apiOrderItemInfoService.insertOrderItemByList(orderItemInfos);
        if (insertItem < 0) {
            log.error("{}插入订单明细表失败!", LOGGER_MSG);
            throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD);
        }
    }
    
    private List<OrderItemInfo> coverOrderItemInfo(OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos) {
        List<OrderItemInfo> orderItemInfoList = new ArrayList<>();
        for (int i = 0; i < orderItemInfos.size(); i++) {
            OrderItemInfo orderItemInfo = orderItemInfos.get(i);
            orderItemInfo.setCreateTime(new Date());
            orderItemInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
            orderItemInfo.setOrderInfoId(orderInfo.getId());
            orderItemInfo.setCreateTime(new Date());
            orderItemInfo.setSphxh(String.valueOf(i + 1));
            orderItemInfo.setSpbm(StringUtils.isBlank(orderItemInfo.getSpbm()) ? orderItemInfo.getSpbm() : StringUtil.fillZero(orderItemInfo.getSpbm(), 19));
            orderItemInfo.setSl(StringUtil.formatSl(orderItemInfo.getSl()));
            orderItemInfo.setGgxh(StringUtils.isBlank(orderItemInfo.getGgxh()) ? "" : orderItemInfo.getGgxh());
            orderItemInfo.setLslbs(StringUtils.isBlank(orderItemInfo.getLslbs()) ? "" : orderItemInfo.getLslbs());
            orderItemInfo.setZzstsgl(StringUtils.isBlank(orderItemInfo.getZzstsgl()) ? "" : orderItemInfo.getZzstsgl());
            orderItemInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            
            
            /**
             * 成品油发票,如果单价和数量都为空时,需要把价税分离后的单价,数量和单位赋值为空
             */
            if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz()) && StringUtils.isBlank(orderItemInfos.get(i).getXmdj()) && StringUtils.isBlank(orderItemInfos.get(i).getXmsl())) {
                orderItemInfo.setXmdj(null);
                orderItemInfo.setXmsl(null);
                orderItemInfo.setXmdw(null);
            }
            
            orderItemInfoList.add(orderItemInfo);
        }
        return orderItemInfoList;
    }
    
    /**
     * 订单编辑-订单对象赋值,以数据库中对象为基础,前端返回数据进行赋值.
     *
     * @param dbOrderInfo
     * @param orderInfo
     */
    private void coverOrderInfo(OrderInfo dbOrderInfo, OrderInfo orderInfo) {
        dbOrderInfo.setDdh(orderInfo.getDdh());
        dbOrderInfo.setGhfQylx(orderInfo.getGhfQylx());
        dbOrderInfo.setGhfDz(orderInfo.getGhfDz());
        dbOrderInfo.setGhfMc(orderInfo.getGhfMc());
        dbOrderInfo.setGhfDh(orderInfo.getGhfDh());
        dbOrderInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        dbOrderInfo.setGhfYh(orderInfo.getGhfYh());
        dbOrderInfo.setGhfEmail(orderInfo.getGhfEmail());
        dbOrderInfo.setGhfZh(orderInfo.getGhfZh());
        dbOrderInfo.setGhfSj(orderInfo.getGhfSj());
        /**
         * 销方地址电话,银行帐号允许编辑
         */
        dbOrderInfo.setXhfDz(orderInfo.getXhfDz());
        dbOrderInfo.setXhfDh(orderInfo.getXhfDh());
        dbOrderInfo.setXhfYh(orderInfo.getXhfYh());
        dbOrderInfo.setXhfZh(orderInfo.getXhfZh());
        dbOrderInfo.setKpr(orderInfo.getKpr());
        dbOrderInfo.setFhr(orderInfo.getFhr());
        dbOrderInfo.setSkr(orderInfo.getSkr());
        dbOrderInfo.setYwlx(orderInfo.getYwlx());
        dbOrderInfo.setYwlxId(orderInfo.getYwlxId());
        dbOrderInfo.setBz(orderInfo.getBz());
        dbOrderInfo.setKphjje(orderInfo.getKphjje());
        dbOrderInfo.setUpdateTime(new Date());
        dbOrderInfo.setFpzlDm(orderInfo.getFpzlDm());
    }
    
    /**
     * 订单表数据赋值给订单明细表数据
     *
     * @param orderProcessInfo
     * @param orderInfo
     */
    private void coverOrderProcessInfo(OrderProcessInfo orderProcessInfo, OrderInfo orderInfo) {
        orderProcessInfo.setGhfMc(orderInfo.getGhfMc());
        orderProcessInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        orderProcessInfo.setKphjje(orderInfo.getKphjje());
        orderProcessInfo.setDdh(orderInfo.getDdh());
        orderProcessInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderProcessInfo.setKphjje(orderInfo.getKphjje());
        orderProcessInfo.setKpse(orderInfo.getHjse());
        orderProcessInfo.setYwlx(orderInfo.getYwlx());
        orderProcessInfo.setYwlxId(orderInfo.getYwlxId());
        orderProcessInfo.setKpxm(orderInfo.getKpxm());
        orderProcessInfo.setUpdateTime(new Date());
        orderProcessInfo.setFpzlDm(orderInfo.getFpzlDm());
    }
    
    /**
     * 订单表数据赋值给订单发票表数据
     *
     * @param orderInvoiceInfo
     * @param orderInfo
     */
    private void coverOrderInvoiceInfo(OrderInvoiceInfo orderInvoiceInfo, OrderInfo orderInfo) {
        orderInvoiceInfo.setGhfMc(orderInfo.getGhfMc());
        orderInvoiceInfo.setKphjje(orderInfo.getKphjje());
        if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            orderInvoiceInfo.setSykchje(ConfigureConstant.STRING_0);
        } else {
            
            orderInvoiceInfo.setSykchje(orderInfo.getKphjje());
        }
        orderInvoiceInfo.setDdh(orderInfo.getDdh());
        orderInvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderInvoiceInfo.setKpse(orderInfo.getHjse());
        orderInvoiceInfo.setUpdateTime(new Date());
        orderInvoiceInfo.setFpzlDm(orderInfo.getFpzlDm());
    }
    
    public OrderProcessInfoExt covertOrderProcessInfoExt(OrderProcessInfo orderProcessInfo, OrderProcessInfo parentOrderProcessInfo) {
        OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
        orderProcessInfoExt.setCreateTime(new Date());
        orderProcessInfoExt.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderProcessInfoExt.setOrderProcessInfoId(orderProcessInfo.getId());
        orderProcessInfoExt.setXhfNsrsbh(orderProcessInfo.getXhfNsrsbh());
        if (StringUtils.isNotBlank(orderProcessInfo.getDdzt())) {
//			if("1".equals(orderProcessInfo.getDdzt())||"2".equals(orderProcessInfo.getDdzt())) {
            orderProcessInfoExt.setParentOrderInfoId(parentOrderProcessInfo.getOrderInfoId());
            orderProcessInfoExt.setParentOrderProcessId(parentOrderProcessInfo.getId());
//			}
        }
        orderProcessInfoExt.setStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
        orderProcessInfoExt.setUpdateTime(new Date());
        return orderProcessInfoExt;
    }
    
    
    @Override
    public CommonOrderInfo getOrderInfoByOrderId(String orderId, List<String> shList) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderId, shList);
        List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderId, shList);
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);
        return commonOrderInfo;
    }
    
    @Override
    public int getOrderInfoByDdh(String ddh, String xhfNsrsbh) {
        OrderProcessInfo process = new OrderProcessInfo();
        process.setDdh(ddh);
        process.setXhfNsrsbh(xhfNsrsbh);
        return apiOrderInfoService.countByDdh(process);
    }
    
    @Override
    public PageUtils selectYwlxCount(Map<String, Object> paramMap, List<String> shList) {
        return apiOrderProcessService.selectYwlxCount(paramMap, shList);
    }
    
    @Override
    public Map<String, String> selectYwlxCountTotal(Map<String, Object> paramMap, List<String> shList) {
        return apiOrderProcessService.selectYwlxCountTotal(paramMap, shList);
    }
    
    
    @Override
    public Map<String, Object> querySimpleOrderInfoByFpdmAndFphm(String fpdm, String fphm, String xhfNsrsbh) {
        Map<String, Object> resultMap = new HashMap<>(5);
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        //根据发票代码发票号码查询订单id
        OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(fpdm, fphm, shList);
        if (orderInvoiceInfo == null) {
            return null;
        }
        //查询process信息
        OrderProcessInfo orderProcessInfo = apiOrderProcessService.selectByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
    
    
        resultMap.put("ddcjsj", orderProcessInfo.getDdcjsj());
        resultMap.put("ddzt",orderProcessInfo.getDdzt());
        resultMap.put("ddly",orderProcessInfo.getDdly());
        resultMap.put("ywlx",orderProcessInfo.getYwlx());
        resultMap.put("ddh",orderProcessInfo.getDdh());
        resultMap.put("fpzldm",orderProcessInfo.getFpzlDm());

        //查询原始订单
        List<PageOrderExt> pageOrderExts = new ArrayList<>();
        getOrderProcessInfoExtInfo(orderProcessInfo, pageOrderExts, shList);
        if(CollectionUtils.isNotEmpty(pageOrderExts)){
            resultMap.put("pageOrderExts",pageOrderExts);
        }
        return resultMap;
    }
    
    
    /**
     * @param @param  oderDetailInfo
     * @param @return
     * @throws
     * @Title : formatCommonOrder
     * @Description ：格式化金额 数量
     */
    private void formatCommonOrder(OderDetailInfo oderDetailInfo) {
        
        PageDataDealUtil.dealOrderItemInfo(oderDetailInfo.getOrderItemInfo());
        
        if (StringUtils.isNotBlank(oderDetailInfo.getKphjje())) {
            oderDetailInfo.setKphjje(DecimalCalculateUtil.decimalFormatToString(oderDetailInfo.getKphjje(), ConfigureConstant.INT_2));
        }
        
        if (StringUtils.isNotBlank(oderDetailInfo.getHjbshje())) {
            oderDetailInfo.setHjbshje(DecimalCalculateUtil.decimalFormatToString(oderDetailInfo.getHjbshje(), ConfigureConstant.INT_2));
        }
        
        if (StringUtils.isNotBlank(oderDetailInfo.getSe())) {
            oderDetailInfo.setSe(DecimalCalculateUtil.decimalFormatToString(oderDetailInfo.getSe(), ConfigureConstant.INT_2));
        }
    
    }


}
