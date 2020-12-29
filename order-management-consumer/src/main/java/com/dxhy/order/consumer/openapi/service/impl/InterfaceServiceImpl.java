package com.dxhy.order.consumer.openapi.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.invoice.protocol.sl.Response;
import com.dxhy.invoice.protocol.sl.cpy.*;
import com.dxhy.invoice.protocol.sl.sld.*;
import com.dxhy.invoice.service.sl.CpyManageService;
import com.dxhy.invoice.service.sl.SldManagerService;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.openapi.service.IInterfaceService;
import com.dxhy.order.consumer.protocol.cpy.*;
import com.dxhy.order.consumer.protocol.sld.*;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.consumer.utils.ReplaceCharacterUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.hp.*;
import com.dxhy.order.model.a9.kp.AllocateInvoicesReq;
import com.dxhy.order.model.a9.kp.CommonInvoice;
import com.dxhy.order.model.a9.kp.CommonInvoicesBatch;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.a9.query.GetAllocateInvoicesStatusRsp;
import com.dxhy.order.model.a9.query.GetAllocatedInvoicesRsp;
import com.dxhy.order.model.a9.query.InvoiceQueryRequest;
import com.dxhy.order.model.a9.query.ResponseCommonInvoice;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.a9.zf.ZfRequest;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.mqdata.FpkjMqData;
import com.dxhy.order.protocol.RESPONSE;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.protocol.v4.invalid.ZFFPXX;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 订单对外接口业务实现类
 *
 * @author: chengyafu
 * @date: 2018年8月9日 下午4:15:27
 */
@SuppressWarnings("AliDeprecation")
@Service
@Slf4j
public class InterfaceServiceImpl implements IInterfaceService {
    
    private static final String NEGATIVE_1 = "-1";
    
    private static final String LOGGER_MSG = "(订单对外接口业务类)";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;
    
    @Reference
    private SldManagerService sldManagerService;
    
    @Reference
    private CpyManageService cpyManageService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Reference
    private ApiBuyerService apiBuyerService;
    
    @Reference(retries = 0)
    private OpenInvoiceService openInvoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiInvalidInvoiceService apiInvalidInvoiceService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Resource
    private IGenerateReadyOpenOrderService iGenerateReadyOpenOrderService;
    
    @Resource
    private InvalidInvoiceService invalidInvoiceService;
    
    @Reference
    private RedisService redisService;
    
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    /**
     * 发票开具接口
     *
     * @param allocateInvoicesReq
     * @return
     */
    @Override
    public R allocateInvoices(AllocateInvoicesReq allocateInvoicesReq) {
        log.debug("{},发票开具数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(allocateInvoicesReq));
        /**
         * todo 后期(v2版本以后)去掉这个R返回都以实体进行返回
         */
        R r = new R();
        
        /**
         * 数据校验
         * 1.限制请求数据不大于300条,超过300条(包括300条)返回错误.
         * 2.校验所有数据判断数据中蓝票不能存在原发票代码号码,红票必须存在原发票代码号码
         *
         */
        
        if (allocateInvoicesReq == null || allocateInvoicesReq.getCOMMON_INVOICE() == null
                || ConfigureConstant.PC_MAX_ITEM_LENGTH <= allocateInvoicesReq.getCOMMON_INVOICE().length) {
            log.error("{}开具发票数量超过1000限额", LOGGER_MSG);
            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getMessage());
        }
        
        if (allocateInvoicesReq.getCOMMON_INVOICES_BATCH().getFPLB().equals(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey())) {
            CommonInvoice[] commonInvoice = allocateInvoicesReq.getCOMMON_INVOICE();
            for (CommonInvoice c : commonInvoice) {
                if (c.getCOMMON_INVOICE_DETAIL().length > ConfigureConstant.INT_1000) {
                    return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getMessage());
                }
            }
        }
        
        CommonInvoice[] commonInvoice1 = allocateInvoicesReq.getCOMMON_INVOICE();
        for (CommonInvoice commonInvoice : commonInvoice1) {
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getKPLX()) && !StringUtils.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getYFP_DM()) && !StringUtils.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getYFP_HM())) {
                log.error("{}开票类型为蓝票时,不能有原发票代码,发票号码", LOGGER_MSG);
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_BLUE_DMHM.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_BLUE_DMHM.getMessage());
            }
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getKPLX()) && StringUtils.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getYFP_DM()) && StringUtils.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getYFP_HM())) {
                log.error("{}开票类型为红票时,必须有原发票代码、号码", LOGGER_MSG);
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_RED_DMHM.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_RED_DMHM.getMessage());
            }
            if (OrderInfoEnum.GHF_QYLX_01.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getGMF_QYLX())
                    && StringUtils.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getGMF_NSRSBH())) {
                log.error("{}购货方税号不能为空", LOGGER_MSG);
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.GENERATE_READY_ORDER_GFXX_NULL_ERROR.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.GENERATE_READY_ORDER_GFXX_NULL_ERROR.getMessage());
            }
            String gmfNsrsbh = commonInvoice.getCOMMON_INVOICE_HEAD().getGMF_NSRSBH();
            if (StringUtils.isNotBlank(gmfNsrsbh)) {
                if (gmfNsrsbh.contains(" ")) {
                    log.error("{}购买方税号不能有空格", LOGGER_MSG);
                    return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.CHECK_ISS7PRI_107164.getKey()).
                            put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.CHECK_ISS7PRI_107164.getMessage());
                }
            }
            
        }
        
        /**
         * 逻辑处理
         * 1.最外层接口协议对象转换成业务对象
         * 2.循环处理
         *  协议对象转换为业务对象
         *  价税分离
         *  数据校验
         *  入库前数据准备(补全id等)
         *  业务对象转换为底层交互协议对象
         * 3.请求底层开票接口
         * 4.根据结果进行数据入库(批次主表,批次明细表,订单表,订单处理表,订单明细表,订单发票表)
         */
        
        /**
         * 外层批次对象转换
         */
        CommonInvoicesBatch commonInvoicesBatch = allocateInvoicesReq.getCOMMON_INVOICES_BATCH();
        List<String> shList = new ArrayList<>();
        shList.add(commonInvoicesBatch.getNSRSBH());
        if (StringUtils.isNotBlank(commonInvoicesBatch.getFPQQPCH())) {
            InvoiceBatchRequest selectBatchByFpqqpch = apiInvoiceCommonMapperService.selectInvoiceBatchRequestByFpqqpch(commonInvoicesBatch.getFPQQPCH(), shList);
            if (selectBatchByFpqqpch != null) {
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_702001.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_702001.getMessage());
            }
        }
        
        //订单批次号转换
        OrderBatchRequest orderBatchRequest = BeanTransitionUtils.transitionOrderBatchRequest(commonInvoicesBatch);
        orderBatchRequest.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
    
    
        //发票类别
        String fplb = commonInvoicesBatch.getFPLB();
        //受理点id
        String sldid;
        //开票机号
        String kpjh;
        
        
        /**
         * 获取sldid ，如果sldid= -1 调用sldidUtils.sldIdFormat(COMMON_INVOICES_BATCH common_invoices_batch)为sldid和kpjh赋值,
         * 如果不为-1 则保持原来处理逻辑不变，直接为sldid和kpjh赋值
         */
        if (NEGATIVE_1.equals(commonInvoicesBatch.getSLDID())) {
            Map result = iCommonInterfaceService.dealWithSldStartV3("", fplb, commonInvoicesBatch.getNSRSBH(), commonInvoice1[0].getCOMMON_INVOICE_HEAD().getQD_BZ(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            if (OrderInfoContentEnum.SUCCESS.getKey().equals(result.get(OrderManagementConstant.CODE))) {
                sldid = String.valueOf(result.get("sldid"));
                kpjh = String.valueOf(result.get("kpjh"));
            } else {
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getKey()).put(OrderManagementConstant.MESSAGE, result.get(OrderManagementConstant.MESSAGE));
            }
            
        } else {
            sldid = commonInvoicesBatch.getSLDID();
            kpjh = commonInvoicesBatch.getKPJH();
        }
        //重新赋值
        commonInvoicesBatch.setSLDID(sldid);
        commonInvoicesBatch.setKPJH(kpjh);
        
        List<OrderInfo> insertOrder = new ArrayList<>();
        List<List<OrderItemInfo>> insertOrderItem = new ArrayList<>();
        List<OrderProcessInfo> insertProcessInfo = new ArrayList<>();
        List<List<InvoiceBatchRequestItem>> insertBatchItem = new ArrayList<>();
        List<OrderInvoiceInfo> insertInvoiceInfo = new ArrayList<>();
        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
        List<FpkjMqData> fpkjMqDataList = new ArrayList<>();
        List<InvoiceBatchRequest> invoiceBatchRequestList = new ArrayList<>();
        Boolean isContainCpy = false;
        for (int i = 0; i < commonInvoice1.length; i++) {
            String invoiceRequestId = apiInvoiceCommonMapperService.getGenerateShotKey();
            InvoiceBatchRequest transitionBatchRequest = BeanTransitionUtils.transitionAutoBatchRequest(commonInvoicesBatch);
            transitionBatchRequest.setId(invoiceRequestId);
            transitionBatchRequest.setFpqqpch(apiInvoiceCommonMapperService.getGenerateShotKey());
            
            CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
            /**
             * 订单信息转换
             */
            CommonOrderInfo commonOrderInfo = BeanTransitionUtils.transitionCommonOrderInfo(commonInvoice1[i]);
            
            OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(commonInvoice1[i].getCOMMON_INVOICE_HEAD().getFPQQLSH(), shList);
    
            log.debug("{}查询数据库处理表校验数据是否已经存在,结果为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderProcessInfo));
            //common_invoice[0].getCOMMON_INVOICE_HEAD().get
            if (orderProcessInfo != null) {
                /**
                 * 订单已经存在不继续执行,返回成功.
                 */
                log.error("{}请求流水号在数据库中已存在!批次号为:{},流水号为:{}", LOGGER_MSG, commonInvoicesBatch.getFPQQPCH(), commonInvoice1[i].getCOMMON_INVOICE_HEAD().getFPQQLSH());
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_702001.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_702001.getMessage());
            }
            
            /**
             * 价税分离 TODO 价税分离方式修改
             */
            TaxSeparateConfig config = new TaxSeparateConfig();
            config.setDealSeType(ConfigureConstant.STRING_1);
            config.setSingleSlSeparateType("2");
            CommonOrderInfo taxSeparationService;
			try {
				taxSeparationService = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo,config);
			} catch (OrderSeparationException e) {
				log.error("{}价税分离未通过,查看provider获取异常数据,{}", LOGGER_MSG,e.getMessage());
                return r.put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE, e.getMessage());
			}
    
            OrderInfo orderInfo = taxSeparationService.getOrderInfo();
            List<OrderItemInfo> orderItemInfos = taxSeparationService.getOrderItemInfo();
    
            /**
             * 补全对象转换工具类中不能补充的数据
             */
            orderInfo.setFpzlDm(fplb);
            orderInfo.setKpjh(kpjh);
            orderInfo.setSld(sldid);
    
            /**
             * 税控设备类型添加到订单主信息中,旧版本接口只有C48
             */
            commonOrderInfo.setTerminalCode(OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
    
            /**
             * 对订单请求数据进行校验,校验失败直接返回
             */
            Map<String, String> checkInvParam = validateOrderInfo.checkOrderInvoice(taxSeparationService);
            if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
                log.error("{}数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
                return r.put(OrderManagementConstant.CODE, checkInvParam.get(OrderManagementConstant.ERRORCODE)).put(OrderManagementConstant.MESSAGE, checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
    
            //===========
            /**
             * 保存或更新购方信息
             */
            BuyerEntity buyerEntity = new BuyerEntity();
            //纳税人识别号
            buyerEntity.setPurchaseName(orderInfo.getGhfMc());
            //购货方名称
            buyerEntity.setTaxpayerCode(orderInfo.getGhfNsrsbh());
            //购货方地址
            buyerEntity.setAddress(orderInfo.getGhfDz());
            //购货方电话
            buyerEntity.setPhone(orderInfo.getGhfDh());
            //开户银行
            buyerEntity.setBankOfDeposit(orderInfo.getGhfYh());
            //购货方账号
            buyerEntity.setBankNumber(orderInfo.getGhfZh());
            //购货方邮箱
            buyerEntity.setEmail(orderInfo.getGhfEmail());
            //购货方企业类型
            buyerEntity.setGhfQylx(orderInfo.getGhfQylx());
            //销货方纳税人识别号
            buyerEntity.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            //销货方纳税人名称
            buyerEntity.setXhfMc(orderInfo.getXhfMc());
            com.dxhy.order.model.R mapResult = apiBuyerService.saveOrUpdateBuyerInfo(buyerEntity);
    
            if (ConfigureConstant.STRING_9999.equals(mapResult.get(OrderManagementConstant.CODE))) {
                log.info("{}保存或更新购方信息结果:{}", LOGGER_MSG, mapResult.get(OrderManagementConstant.MESSAGE));
            }
    
            /**
             *  补全商品简称
             */
    
            try {
                iCommonInterfaceService.dealOrderItem(orderItemInfos, orderInfo.getXhfNsrsbh(), orderInfo.getQdBz(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            } catch (OrderReceiveException e) {
                r.put(OrderManagementConstant.CODE, e.getCode());
                r.put(OrderManagementConstant.MESSAGE, e.getMessage());
                return r;
            }
            
            /**
             * 订单入库前数据补全
             */
            String orderId = apiInvoiceCommonMapperService.getGenerateShotKey();
            String processId = apiInvoiceCommonMapperService.getGenerateShotKey();
            String fpqqlsh = apiInvoiceCommonMapperService.getGenerateShotKey();
            String invoiceId = apiInvoiceCommonMapperService.getGenerateShotKey();
            String invoiceRequestItemId = apiInvoiceCommonMapperService.getGenerateShotKey();
            DecimalFormat df = new DecimalFormat("000");
            String format = df.format(1);
            String kplsh = transitionBatchRequest.getFpqqpch() + format;
            
            orderInfo.setId(orderId);
            if (StringUtils.isBlank(orderInfo.getFpqqlsh())) {
                orderInfo.setFpqqlsh(fpqqlsh);
            }
        
            orderInfo.setProcessId(processId);
        
            insertOrder.add(orderInfo);
            /**
             * 订单明细入库前数据补全
             */
            for (OrderItemInfo orderItemInfo : orderItemInfos) {
                orderItemInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
                orderItemInfo.setOrderInfoId(orderInfo.getId());
            }
            insertOrderItem.add(orderItemInfos);
            /**
             * 订单处理表入库前数据补全
             */
            OrderProcessInfo processInfo = new OrderProcessInfo();
            BeanTransitionUtils.transitionAutoProcessInfo(processInfo, orderInfo);
            processInfo.setId(processId);
            processInfo.setOrderInfoId(orderId);
            processInfo.setDdqqpch(orderBatchRequest.getDdqqpch());
            processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_4.getKey());
            
            insertProcessInfo.add(processInfo);
            
            /**
             * 订单发票表入库前数据补全
             */
            OrderInvoiceInfo invoiceInfo = new OrderInvoiceInfo();
            BeanTransitionUtils.transitionOrderInvoiceInfo(invoiceInfo, orderInfo);
            invoiceInfo.setId(invoiceId);
            invoiceInfo.setOrderInfoId(orderId);
            invoiceInfo.setOrderProcessInfoId(processId);
            invoiceInfo.setKplsh(kplsh);
            
            OrderOriginExtendInfo orderOrginOrder = new OrderOriginExtendInfo();
            orderOrginOrder.setCreateTime(new Date());
            orderOrginOrder.setUpdateTime(new Date());
            orderOrginOrder.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            orderOrginOrder.setOrderId(orderInfo.getId());
            orderOrginOrder.setFpqqlsh(orderInfo.getFpqqlsh());
            orderOrginOrder.setOriginFpqqlsh(orderInfo.getFpqqlsh());
            orderOrginOrder.setOriginOrderId(orderInfo.getId());
            orderOrginOrder.setOriginDdh(orderInfo.getDdh());
            orderOrginOrder.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderOriginList.add(orderOrginOrder);
            
            /**
             * 清单标志赋值
             */
            if (!OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz())) {
                BeanTransitionUtils.getOrderInvoiceInfoQdBz(OrderInfoEnum.TAX_EQUIPMENT_C48.getKey(), orderInfo, orderItemInfos);
    
            } else {
                if (!NEGATIVE_1.equals(commonInvoicesBatch.getSLDID()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(invoiceInfo.getFpzlDm())) {
                    isContainCpy = true;
                }
                invoiceInfo.setQdbz(orderInfo.getQdBz());
            }
            insertInvoiceInfo.add(invoiceInfo);
    
            invoiceBatchRequestList.add(transitionBatchRequest);
            List<InvoiceBatchRequestItem> invoiceBatchRequestItems = new ArrayList<>();
            /**
             * 发票开具请求明细表入库前数据补全
             */
            InvoiceBatchRequestItem invoiceBatchRequestItem = new InvoiceBatchRequestItem();
            invoiceBatchRequestItem.setId(invoiceRequestItemId);
            invoiceBatchRequestItem.setInvoiceBatchId(invoiceRequestId);
            invoiceBatchRequestItem.setFpqqpch(transitionBatchRequest.getFpqqpch());
            invoiceBatchRequestItem.setFpqqlsh(orderInfo.getFpqqlsh());
            invoiceBatchRequestItem.setKplsh(kplsh);
            invoiceBatchRequestItem.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            invoiceBatchRequestItem.setCreateTime(new Date());
            invoiceBatchRequestItem.setUpdateTime(new Date());
            invoiceBatchRequestItems.add(invoiceBatchRequestItem);
            insertBatchItem.add(invoiceBatchRequestItems);
    
            /**
             * 数据放入开票队列
             */
            FpkjMqData fpkjMqData = new FpkjMqData();
            fpkjMqData.setFpqqlsh(orderInfo.getFpqqlsh());
            fpkjMqData.setFpqqpch(transitionBatchRequest.getFpqqpch());
            fpkjMqData.setKplsh(kplsh);
            fpkjMqData.setNsrsbh(orderInfo.getXhfNsrsbh());
            fpkjMqData.setTerminalCode(OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            fpkjMqDataList.add(fpkjMqData);
    
        }
        //如果包含成品油的发票 判断下受理点是否成品油受理点
        if (isContainCpy) {
            Set<SearchSld> sldList = new HashSet<>();
            String terminalCode = OrderInfoEnum.TAX_EQUIPMENT_C48.getKey();
            String invoiceType = insertInvoiceInfo.get(0).getFpzlDm();
            String url = OpenApiConfig.querySldList;
            if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                url = OpenApiConfig.queryKpdXxBw;
            } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                url = OpenApiConfig.queryNsrXnsbxx;
                /**
                 * 如果是新税控转换发票种类代码
                 */
                invoiceType = CommonUtils.transFplxdm(invoiceType);
            }
            SldSearchResponse specialSld;
            HttpInvoiceRequestUtil.getSldList(sldList, url, invoiceType, OrderInfoEnum.OIL_TYPE_1.getKey(), insertInvoiceInfo.get(0).getXhfNsrsbh(), null, null, OrderInfoEnum.TAX_EQUIPMENT_HEAD_C48.getKey());
    
            HttpInvoiceRequestUtil.getSldList(sldList, url, invoiceType, OrderInfoEnum.OIL_TYPE_2.getKey(), insertInvoiceInfo.get(0).getXhfNsrsbh(), null, null, OrderInfoEnum.TAX_EQUIPMENT_HEAD_C48.getKey());
    
    
            boolean isCpySld = false;
            for (SearchSld sld : sldList) {
                if (commonInvoicesBatch.getSLDID().equals(sld.getSldId())) {
                    isCpySld = true;
                }
        
            }
            if (!isCpySld) {
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.ORDER__SLD_NOT_CPY_ERROR.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.ORDER__SLD_NOT_CPY_ERROR.getMessage());
            }
    
            // 成品油的获取成品油的受力点
    
        }
    
        //队列的优先级目前全部设置成0
        apiInvoiceCommonMapperService.saveData(invoiceBatchRequestList, insertOrder, insertOrderItem, insertProcessInfo, insertBatchItem, insertInvoiceInfo, orderBatchRequest, new ArrayList<>(), new ArrayList<>(), orderOriginList, shList);
        for (FpkjMqData fpkjMqData : fpkjMqDataList) {
            String jsonString = JsonUtils.getInstance().toJsonString(fpkjMqData);
            log.debug("{}放入mq的数据:{}", LOGGER_MSG, jsonString);
            com.dxhy.order.model.R openAnInvocie = openInvoiceService.openAnInvoice(jsonString, fpkjMqData.getNsrsbh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(openAnInvocie.get(OrderManagementConstant.CODE)))) {
                r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey());
                r.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
                r.put("fpqqpch", commonInvoicesBatch.getFPQQPCH());
                return r;
            }
        }
        r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getKey());
        r.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getMessage());
        r.put("fpqqpch", commonInvoicesBatch.getFPQQPCH());
        return r;
    
    
    }
    
    
    /**
     * 发票状态查询接口
     *
     * @param jsonString
     * @return
     */
    @Override
    public GetAllocateInvoicesStatusRsp invoiceStatus(String jsonString) {
        
        GetAllocateInvoicesStatusRsp getAllocateInvoicesStatusRsp = new GetAllocateInvoicesStatusRsp();
        log.debug("{},发票状态查询数据：{}", LOGGER_MSG, jsonString);
        R r = new R();
        if (StringUtils.isBlank(jsonString)) {
            log.error("{}查询开票执行状态参数为空", LOGGER_MSG);
            getAllocateInvoicesStatusRsp.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getKey());
            getAllocateInvoicesStatusRsp.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getMessage());
            return getAllocateInvoicesStatusRsp;
        }
        
        getAllocateInvoicesStatusRsp = HttpInvoiceRequestUtil.queryInvoiceStatus(OpenApiConfig.invoiceStatusQuery, jsonString, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
        try {
            /**
             * todo 底层不愿意新增税号返回,业务系统自己维护,支持mycat操作
             * 本次新增mycat查询,根据销方税号做分片规则,所以需要底层返回销方税号,如果返回为空不进行操作
             */
            if (StringUtils.isBlank(getAllocateInvoicesStatusRsp.getNSRSBH()) && StringUtils.isNotBlank(getAllocateInvoicesStatusRsp.getFPQQPCH())) {
                /**
                 * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
                 *
                 */
                String cacheFpqqpch = String.format(Constant.REDIS_FPQQPCH, getAllocateInvoicesStatusRsp.getFPQQPCH());
                String xhfNsrsbh = redisService.get(cacheFpqqpch);
                if (StringUtils.isBlank(xhfNsrsbh)) {
                    InvoiceBatchRequest invoiceBatchRequest = apiInvoiceCommonMapperService.selectInvoiceBatchRequestByFpqqpch(getAllocateInvoicesStatusRsp.getFPQQPCH(), null);
                    if (invoiceBatchRequest != null && StringUtils.isNotBlank(invoiceBatchRequest.getXhfNsrsbh())) {
                
                        redisService.set(cacheFpqqpch, invoiceBatchRequest.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        xhfNsrsbh = invoiceBatchRequest.getXhfNsrsbh();
                    }
                }
        
                getAllocateInvoicesStatusRsp.setNSRSBH(xhfNsrsbh);
            }
            if (StringUtils.isNotBlank(getAllocateInvoicesStatusRsp.getNSRSBH())) {
                List<String> shList = new ArrayList<>();
                shList.add(getAllocateInvoicesStatusRsp.getNSRSBH());
                String jsonString2 = JsonUtils.getInstance().toJsonString(getAllocateInvoicesStatusRsp);
                log.debug("{},发票状态查询返回数据:{}", LOGGER_MSG, jsonString2);
        
                /**
                 * 通过批次号 查出批次关系表所有与发票关联的
                 */
                List<InvoiceBatchRequestItem> batchItem = apiInvoiceCommonMapperService.selectInvoiceBatchItemByFpqqpch(getAllocateInvoicesStatusRsp.getFPQQPCH(), shList);
        
                if (batchItem.size() < 1) {
                    log.error("此批次号不存在，批次号：{},{}", getAllocateInvoicesStatusRsp.getFPQQPCH(), LOGGER_MSG);
                    getAllocateInvoicesStatusRsp.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_QUERY_ERROR.getKey());
                    getAllocateInvoicesStatusRsp.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_QUERY_ERROR.getMessage());
                    return getAllocateInvoicesStatusRsp;
                }
        
                /**
                 * 更改批次表状态
                 */
                int a = apiInvoiceCommonMapperService.updateBatchStatusById(batchItem.get(0).getInvoiceBatchId(), getAllocateInvoicesStatusRsp.getSTATUS_CODE(), getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), shList);
                if (a == 0) {
                    log.error("{},更改批次表状态失败，发票请求批次号{}", LOGGER_MSG, getAllocateInvoicesStatusRsp.getFPQQPCH());
                }
                // TODO: 2018/10/26 后期需要查询本地数据库,然后根据本地结果判断是否需要调用底层接口,后续数据操作可以考虑使用一个for循环进行遍历赋值.
                //更改发票状态 2
                if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020000.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
                    OrderInvoiceInfo invoiceInfo = null;
                    for (InvoiceBatchRequestItem invoiceBatchRequestItem : batchItem) {
                        invoiceInfo = new OrderInvoiceInfo();
                        //得到与单个发票对应的开票流水号
                        invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                        invoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                        //修改发票表
                        int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                        if (i == 0) {
                            log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                        }
        
                    }
                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020001.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
                    OrderInvoiceInfo invoiceInfo = null;
                    for (InvoiceBatchRequestItem invoiceBatchRequestItem : batchItem) {
                        if (invoiceBatchRequestItem.getKplsh().equals(getAllocateInvoicesStatusRsp.getINVOICES_FAILED().getFPQQLSH())) {
                            invoiceInfo = new OrderInvoiceInfo();
                            invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                            invoiceInfo.setKpzt("3");
                            invoiceInfo.setSbyy(getAllocateInvoicesStatusRsp.getINVOICES_FAILED().getSTATUS_MESSAGE());
                            int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                            if (i == 0) {
                                log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                            }
                            continue;
                        }
                        invoiceInfo = new OrderInvoiceInfo();
                        invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                        invoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                        invoiceInfo.setSbyy("null");
                        int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                        if (i == 0) {
                            log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                        }
                    }
                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020002.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
                    OrderInvoiceInfo invoiceInfo = null;
                    for (InvoiceBatchRequestItem invoiceBatchRequestItem : batchItem) {
                        invoiceInfo = new OrderInvoiceInfo();
                        invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                        invoiceInfo.setKpzt("3");
                        invoiceInfo.setSbyy(getAllocateInvoicesStatusRsp.getINVOICES_FAILED().getSTATUS_MESSAGE());
                        int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                        if (i == 0) {
                            log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                        }
                    }
                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_021002.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE()) || OrderInfoContentEnum.INVOICE_ERROR_CODE_031002.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
                    OrderInvoiceInfo invoiceInfo = null;
                    for (InvoiceBatchRequestItem invoiceBatchRequestItem : batchItem) {
                        if (invoiceBatchRequestItem.getKplsh().equals(getAllocateInvoicesStatusRsp.getINVOICES_FAILED().getFPQQLSH())) {
                            invoiceInfo = new OrderInvoiceInfo();
                            invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                            invoiceInfo.setKpzt("3");
                            invoiceInfo.setSbyy(getAllocateInvoicesStatusRsp.getINVOICES_FAILED().getSTATUS_MESSAGE());
                            int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                            if (i == 0) {
                                log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                            }
                            continue;
                        }
                        invoiceInfo = new OrderInvoiceInfo();
                        invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                        invoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                        invoiceInfo.setSbyy("null");
                        int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                        if (i == 0) {
                            log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                        }
                    }
                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020111.getKey().equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
                    OrderInvoiceInfo invoiceInfo = null;
                    for (InvoiceBatchRequestItem invoiceBatchRequestItem : batchItem) {
                        invoiceInfo = new OrderInvoiceInfo();
                        invoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
                        invoiceInfo.setKpzt(ConfigureConstant.STRING_1);
                        invoiceInfo.setSbyy("null");
                        int i = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
                        if (i == 0) {
                            log.info("修改发票状态失败,状态信息{},开票流水号kplsh{},{}", getAllocateInvoicesStatusRsp.getSTATUS_MESSAGE(), invoiceBatchRequestItem.getKplsh(), LOGGER_MSG);
                        }
                    }
                }
            }
    
        } catch (Exception e) {
            log.error("{},e:{}", LOGGER_MSG, e);
        }
        if (ConfigureConstant.STRING_0000.equals(getAllocateInvoicesStatusRsp.getSTATUS_CODE())) {
            getAllocateInvoicesStatusRsp.setSTATUS_CODE(ConfigureConstant.STRING_0);
        }
        return getAllocateInvoicesStatusRsp;
    }
    
    /**
     * 发票结果查询接口业务处理
     *
     * @param jsonString
     * @return
     */
    @Override
    public GetAllocatedInvoicesRsp getAllocatedInvoices(String jsonString) {
        log.debug("{},发票结果查询数据:{}", LOGGER_MSG, jsonString);
        InvoiceQueryRequest invoiceQueryRequest = JsonUtils.getInstance().parseObject(jsonString, InvoiceQueryRequest.class);
    
        GetAllocatedInvoicesRsp getAllocatedInvoicesRsp = new GetAllocatedInvoicesRsp();
        try {
        
        
            getAllocatedInvoicesRsp.setFPQQPCH(invoiceQueryRequest.getFPQQPCH());
            /**
             * 校验查询的字段是否为空
             */
            getAllocatedInvoicesRsp = checkAllocatedInvoicesRequest(invoiceQueryRequest);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(getAllocatedInvoicesRsp.getStatusCode())) {
                return getAllocatedInvoicesRsp;
            }
    
    
            /**
             * todo 底层不愿意新增税号返回,业务系统自己维护,支持mycat操作
             * 本次新增mycat查询,根据销方税号做分片规则,所以需要底层返回销方税号,如果返回为空不进行操作
             */
            if (StringUtils.isBlank(invoiceQueryRequest.getNSRSBH()) && StringUtils.isNotBlank(invoiceQueryRequest.getFPQQPCH())) {
                /**
                 * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
                 *
                 */
                String cacheFpqqpch = String.format(Constant.REDIS_FPQQPCH, invoiceQueryRequest.getFPQQPCH());
                String xhfNsrsbh = redisService.get(cacheFpqqpch);
                if (StringUtils.isBlank(xhfNsrsbh)) {
                    List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessInfoByDdqqpch(invoiceQueryRequest.getFPQQPCH(), null);
                    if (orderProcessInfos != null && orderProcessInfos.size() > 0 && StringUtils.isNotBlank(orderProcessInfos.get(0).getXhfNsrsbh())) {
    
                        redisService.set(cacheFpqqpch, orderProcessInfos.get(0).getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        xhfNsrsbh = orderProcessInfos.get(0).getXhfNsrsbh();
                    }
                }
        
                invoiceQueryRequest.setNSRSBH(xhfNsrsbh);
            }
            List<String> shList = new ArrayList<>();
            shList.add(invoiceQueryRequest.getNSRSBH());
            /**
             * 1.根据订单请求批次号获取订单处理表中的数据
             *  如果查询不到数据,就返回失败
             * 2.查询到数据后,根据数据判断,返回开票成功的数据给客户
             */
    
            List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessInfoByDdqqpch(invoiceQueryRequest.getFPQQPCH(), shList);
            if (orderProcessInfos == null || orderProcessInfos.size() <= 0) {
                log.error("{}发票开具结果数据获取，请求批次号不存在!", LOGGER_MSG);
                getAllocatedInvoicesRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_204001_V3.getKey());
                getAllocatedInvoicesRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_204001_V3.getMessage());
                return getAllocatedInvoicesRsp;
            }
            //2.根据查询出来的数据,循环处理.
            List<ResponseCommonInvoice> fpzxxes = new ArrayList<>();
            int successCount = 0;
            int invoiceingCount = 0;
            for (OrderProcessInfo orderProcessInfo : orderProcessInfos) {
                ResponseCommonInvoice fpzxx = new ResponseCommonInvoice();
                //根据处理表的发票请求流水号获取发票表数据.
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderProcessInfo.getFpqqlsh(), shList);
                if (orderInvoiceInfo == null) {
            
                    continue;
            
                } else {
                    //如果发票代码号码不等于空,并且发票状态不是开票中,则返回数据,
                    if (StringUtils.isNotBlank(orderInvoiceInfo.getFpdm()) && StringUtils.isNotBlank(orderInvoiceInfo.getFphm()) && !OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(orderInvoiceInfo.getKpzt())) {
                        fpzxx = BeanTransitionUtils.transitionCommonInvoiceInfoV2(orderInvoiceInfo);
                
                    } else {
    
                        continue;
                    }
                }
                /**
                 * 赋值数据对应的订单状态
                 */
                OrderInfoContentEnum invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021000_V3;
                if (StringUtils.isNotBlank(orderProcessInfo.getDdzt())) {
                    if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt())) {
                        invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021000_V3;
                        successCount++;
                    } else if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt())) {
                        invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021001_V3;
                        invoiceingCount++;
                    } else if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {
                        invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021002_V3;
                        invoiceingCount++;
                    } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())) {
                        invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021999_V3;
                    } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(orderProcessInfo.getDdzt())) {
                        invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021003_V3;
                    }
                }
                fpzxxes.add(fpzxx);
            }
            if (successCount == orderProcessInfos.size()) {
                getAllocatedInvoicesRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_020000_V3.getKey());
                getAllocatedInvoicesRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_020000_V3.getMessage());
            } else if (successCount == 0) {
                getAllocatedInvoicesRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_020002_V3.getKey());
                getAllocatedInvoicesRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_020002_V3.getMessage());
                if (invoiceingCount > 0) {
                    getAllocatedInvoicesRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_020111_V3.getKey());
                    getAllocatedInvoicesRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_020111_V3.getMessage());
                }
            } else if (successCount < orderProcessInfos.size()) {
                getAllocatedInvoicesRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_020001_V3.getKey());
                getAllocatedInvoicesRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_020001_V3.getMessage());
            }
            if (fpzxxes.size() > 0) {
        
                getAllocatedInvoicesRsp.setRESPONSE_COMMON_INVOICE(fpzxxes);
            }

//            GETALLOCATED_INVOICES_RSP parseObject = JsonUtils.getInstance().parseObject(invoicingResult.get(ConfigurerInfo.FPKJRESULT).toString(), GETALLOCATED_INVOICES_RSP.class);

//            /**
//             * todo 底层不愿意新增税号返回,业务系统自己维护,支持mycat操作
//             * 本次新增mycat查询,根据销方税号做分片规则,所以需要底层返回销方税号,如果返回为空不进行操作
//             */
//            if (StringUtils.isBlank(getAllocatedInvoicesRsp.getNSRSBH()) && StringUtils.isNotBlank(getAllocatedInvoicesRsp.getFPQQPCH())) {
//                /**
//                 * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
//                 *
//                 */
//                String cacheFpqqpch = String.format(Constant.REDIS_FPQQPCH, getAllocatedInvoicesRsp.getFPQQPCH());
//                String xhfNsrsbh = redisService.get(cacheFpqqpch);
//                if (StringUtils.isBlank(xhfNsrsbh)) {
//                    InvoiceBatchRequest invoiceBatchRequest = apiInvoiceCommonMapperService.selectInvoiceBatchRequestByFpqqpch(getAllocatedInvoicesRsp.getFPQQPCH(), null);
//                    if (invoiceBatchRequest != null && StringUtils.isNotBlank(invoiceBatchRequest.getXhfNsrsbh())) {
//
//                        redisService.set(cacheFpqqpch, invoiceBatchRequest.getXhfNsrsbh());
//                        xhfNsrsbh = invoiceBatchRequest.getXhfNsrsbh();
//                    }
//                }
//
//                getAllocatedInvoicesRsp.setNSRSBH(xhfNsrsbh);
//            }
//            if (StringUtils.isNotBlank(getAllocatedInvoicesRsp.getNSRSBH())) {
//                /**
//                 * 更改批次表状态
//                 */
//                List<String> shList = new ArrayList<>();
//                shList.add(getAllocatedInvoicesRsp.getNSRSBH());
//                InvoiceBatchRequest selectBatchByFpqqpch = apiInvoiceCommonMapperService.selectInvoiceBatchRequestByFpqqpch(getAllocatedInvoicesRsp.getFPQQPCH(), shList);
//                int a = apiInvoiceCommonMapperService.updateBatchStatusById(selectBatchByFpqqpch.getId(), getAllocatedInvoicesRsp.getStatusCode(), getAllocatedInvoicesRsp.getStatusMessage(), shList);
//                if (a <= 0) {
//                    log.error("{},更改批次表状态失败，发票请求批次号{}", LOGGER_MSG, getAllocatedInvoicesRsp.getFPQQPCH());
//                }
//                //"020000", "发票全部开具成功"
//                //"020001", "发票部分开具成功"
//                /**
//                 * 更改发票表状态
//                 */
//                // TODO: 2018/10/27 需要更改发票表,订单处理表,请求批次明细表等表的数据状态,同时需要考虑红票,如果红票需要修改对应蓝票剩余可充红金额,失败数据返回第一张数据,因此需要特殊考虑
//                OrderInvoiceInfo invoiceInfo = null;
//                List<ResponseCommonInvoice> fpkjResult = getAllocatedInvoicesRsp.getRESPONSE_COMMON_INVOICE();
//                if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020000.getKey().equals(getAllocatedInvoicesRsp.getStatusCode())) {
//                    for (ResponseCommonInvoice responseCommonInvoice : fpkjResult) {
//                        invoiceInfo = new OrderInvoiceInfo();
//                        invoiceInfo.setKplsh(responseCommonInvoice.getFPQQLSH());
//                        invoiceInfo.setFpdm(responseCommonInvoice.getFP_DM());
//                        invoiceInfo.setFphm(responseCommonInvoice.getFP_HM());
//                        invoiceInfo.setJym(responseCommonInvoice.getJYM());
//
//                        invoiceInfo.setFwm(responseCommonInvoice.getFWM());
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date parse = simpleDateFormat.parse(responseCommonInvoice.getKPRQ());
//                        invoiceInfo.setKprq(parse);
//                        invoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
//                        invoiceInfo.setJqbh(responseCommonInvoice.getJQBH());
//                        int updateInvoiceStatusByKplsh = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
//                        if (updateInvoiceStatusByKplsh == 0) {
//                            log.error("开具发票成功,结果存数据库失败,发票请求流水号{},{}", responseCommonInvoice.getFPQQLSH(), LOGGER_MSG);
//                        }
//                        //更改处理表状态
//                        OrderInvoiceInfo selectInvoiceInfoByKplsh = apiInvoiceCommonMapperService.selectInvoiceInfoByKplsh(responseCommonInvoice.getFPQQLSH(), shList);
//                        int process = apiInvoiceCommonMapperService.updateProcessInfoDdztByOrderInfoId(null, "5", selectInvoiceInfoByKplsh.getOrderProcessInfoId(), shList);
//                        if (process <= 0) {
//                            log.error("修改处理表状态失败,订单号{},{}", selectInvoiceInfoByKplsh.getDdh(), LOGGER_MSG);
//                        }
//                    }
//
//                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020001.getKey().equals(getAllocatedInvoicesRsp.getStatusCode())) {
//                    for (ResponseCommonInvoice response_COMMON_INVOICE2 : fpkjResult) {
//                        invoiceInfo = new OrderInvoiceInfo();
//                        invoiceInfo.setKplsh(response_COMMON_INVOICE2.getFPQQLSH());
//                        invoiceInfo.setFpdm(response_COMMON_INVOICE2.getFP_DM());
//                        invoiceInfo.setFphm(response_COMMON_INVOICE2.getFP_HM());
//                        invoiceInfo.setJym(response_COMMON_INVOICE2.getJYM());
//                        invoiceInfo.setFwm(response_COMMON_INVOICE2.getFWM());
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date parse = simpleDateFormat.parse(response_COMMON_INVOICE2.getKPRQ());
//                        invoiceInfo.setKprq(parse);
//                        invoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
//                        invoiceInfo.setJqbh(response_COMMON_INVOICE2.getJQBH());
//                        int updateInvoiceStatusByKplsh = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
//                        if (updateInvoiceStatusByKplsh == 0) {
//                            log.error("开具发票成功结果存数据库失败，发票请求流水号{},{}", response_COMMON_INVOICE2.getFPQQLSH(), LOGGER_MSG);
//                        }
//                        //更改处理表状态
//                        OrderInvoiceInfo selectInvoiceInfoByKplsh = apiInvoiceCommonMapperService.selectInvoiceInfoByKplsh(response_COMMON_INVOICE2.getFPQQLSH(), shList);
//                        int process = apiInvoiceCommonMapperService.updateProcessInfoDdztByOrderInfoId(null, "5", selectInvoiceInfoByKplsh.getOrderProcessInfoId(), shList);
//                        if (process <= 0) {
//                            log.error("修改处理表状态失败,订单号{},{}", selectInvoiceInfoByKplsh.getDdh(), LOGGER_MSG);
//                        }
//                    }
//                    // 失败
//                } else if (OrderInfoContentEnum.INVOICE_ERROR_CODE_020002.getKey().equals(getAllocatedInvoicesRsp.getStatusCode())) {
//                    for (ResponseCommonInvoice response_COMMON_INVOICE2 : fpkjResult) {
//                        invoiceInfo = new OrderInvoiceInfo();
//                        invoiceInfo.setKplsh(response_COMMON_INVOICE2.getFPQQLSH());
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        Date parse = simpleDateFormat.parse(response_COMMON_INVOICE2.getKPRQ());
//                        invoiceInfo.setKprq(parse);
//                        invoiceInfo.setKpzt("3");
//                        invoiceInfo.setJqbh(response_COMMON_INVOICE2.getJQBH());
//                        int updateInvoiceStatusByKplsh = apiInvoiceCommonMapperService.updateInvoiceStatusByKplsh(invoiceInfo, shList);
//                        if (updateInvoiceStatusByKplsh == 0) {
//                            log.error("发票开具失败结果存数据库失败，发票请求流水号{},{}", response_COMMON_INVOICE2.getFPQQLSH(), LOGGER_MSG);
//                        }
//                        //更改处理表状态
//                        OrderInvoiceInfo selectInvoiceInfoByKplsh = apiInvoiceCommonMapperService.selectInvoiceInfoByKplsh(response_COMMON_INVOICE2.getFPQQLSH(), shList);
//                        int process = apiInvoiceCommonMapperService.updateProcessInfoDdztByOrderInfoId(null, "6", selectInvoiceInfoByKplsh.getOrderProcessInfoId(), shList);
//                        if (process <= 0) {
//                            log.error("修改处理表状态失败,订单号{},{}", selectInvoiceInfoByKplsh.getDdh(), LOGGER_MSG);
//                        }
//                    }
//                }
//            }
    
        } catch (Exception e) {
            log.error("{},e:{}", LOGGER_MSG, e);
        }
    
        if (ConfigureConstant.STRING_0000.equals(getAllocatedInvoicesRsp.getStatusCode())) {
            getAllocatedInvoicesRsp.setStatusCode(ConfigureConstant.STRING_0);
        }
        return getAllocatedInvoicesRsp;
    }
    
    private GetAllocatedInvoicesRsp checkAllocatedInvoicesRequest(InvoiceQueryRequest invoiceQueryRequest) {
        GetAllocatedInvoicesRsp getInvoiceRsp = new GetAllocatedInvoicesRsp();
        getInvoiceRsp.setFPQQPCH(invoiceQueryRequest.getFPQQPCH());
        if (StringUtils.isBlank(invoiceQueryRequest.getFPQQPCH())) {
            log.error("{}发票开具结果数据获取，请求批次号不能为空", LOGGER_MSG);
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104002_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104002_V3.getMessage());
            return getInvoiceRsp;
        } else if (invoiceQueryRequest.getFPQQPCH().length() > ConfigureConstant.INT_40) {
            log.error("{}发票开具结果数据获取，请求批次号长度不匹配", LOGGER_MSG);
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104003_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104003_V3.getMessage());
            return getInvoiceRsp;
        }
        if (StringUtils.isBlank(invoiceQueryRequest.getFPLX())) {
            log.error("{}发票开具结果数据获取，发票类型不能为空", LOGGER_MSG);
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104004_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104004_V3.getMessage());
            return getInvoiceRsp;
        } else if (invoiceQueryRequest.getFPLX().length() > 1) {
            log.error("{}发票开具结果数据获取，发票类型长度不匹配", LOGGER_MSG);
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getMessage());
            return getInvoiceRsp;
        }
        //订单请求发票类型合法性
        if (!ConfigureConstant.STRING_1.equals(invoiceQueryRequest.getFPLX())
                && !ConfigureConstant.STRING_2.equals(invoiceQueryRequest.getFPLX())) {
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getMessage());
            return getInvoiceRsp;
        }
        /**
         * 订单请求纳税人识别号
         */
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_108034, OrderInfoContentEnum.CHECK_ISS7PRI_107006, OrderInfoContentEnum.CHECK_ISS7PRI_107163, invoiceQueryRequest.getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104009_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104009_V3.getMessage());
            return getInvoiceRsp;
        } else if (StringUtils.isBlank(invoiceQueryRequest.getNSRSBH())) {
            log.error("{}发票开具结果数据获取，税号不能为空", LOGGER_MSG);
            getInvoiceRsp.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_104010_V3.getKey());
            getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.INVOICE_ERROR_CODE_104010_V3.getMessage());
            return getInvoiceRsp;
        }
        
        
        getInvoiceRsp.setStatusCode(OrderInfoContentEnum.SUCCESS.getKey());
        getInvoiceRsp.setStatusMessage(OrderInfoContentEnum.SUCCESS.getMessage());
        return getInvoiceRsp;
    }
    
    
    /**
     * 发票作废接口业务处理
     *
     * @param zfxxReq
     * @return
     */
    @Override
    public ZFXX_RSP invoiceInvalid(ZFXX_REQ zfxxReq) {
        String jsonString = JsonUtils.getInstance().toJsonString(zfxxReq);
        log.debug("{},已开发票作废数据:{}", LOGGER_MSG, jsonString);
        ZFXX_RSP zfxxRsp = new ZFXX_RSP();
        zfxxRsp.setZFPCH(zfxxReq.getZFPCH());
        Map<String, String> checkInvParam = validateOrderInfo.checkInvalidInvoice(zfxxReq);
        if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}作废数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
            zfxxRsp.setZTDM(checkInvParam.get(OrderManagementConstant.ERRORCODE));
            zfxxRsp.setZTXX(checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
            return zfxxRsp;
        }
    
        //目前只支持单张发票作废
        if (!zfxxReq.getFPQH().equals(zfxxReq.getFPZH())) {
            log.error("{}作废数据校验未通过，目前只支持单张作废,未通过数据:{}", LOGGER_MSG, jsonString);
            zfxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_108032.getKey());
            zfxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_108032.getMessage());
            return zfxxRsp;
        }
        
        /**
         * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
         *
         */
        String xhfNsrsbh = zfxxReq.getXHFSBH();
        
        if (StringUtils.isBlank(xhfNsrsbh)) {
            String cacheFpdmHm = String.format(Constant.REDIS_FPDMHM, zfxxReq.getFPDM() + zfxxReq.getFPQH());
            xhfNsrsbh = redisService.get(cacheFpdmHm);
            if (StringUtils.isBlank(xhfNsrsbh)) {
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(zfxxReq.getFPDM(), zfxxReq.getFPQH(), null);
                if (orderInvoiceInfo != null && StringUtils.isNotBlank(orderInvoiceInfo.getXhfNsrsbh())) {
    
                    redisService.set(cacheFpdmHm, orderInvoiceInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                    xhfNsrsbh = orderInvoiceInfo.getXhfNsrsbh();
                }
            }
    
        }
    
        zfxxReq.setXHFSBH(xhfNsrsbh);
        List<String> shList = new ArrayList<>();
        shList.add(zfxxReq.getXHFSBH());
    
        OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(zfxxReq.getFPDM(), zfxxReq.getFPQH(), shList);
    
        if (orderInvoiceInfo == null) {
            log.error("{}作废数据校验未通过，查询数据库发票数据为空,未通过数据:{}", LOGGER_MSG, jsonString);
            zfxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_108033.getKey());
            zfxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_108033.getMessage());
            return zfxxRsp;
        }
    
    
        if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoiceInfo.getZfBz())) {
            //发票已经作废，组装返回结果数据
            zfxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey());
            zfxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getMessage());
            List<ZFFPXX> zffpxxList = new ArrayList<>();
            ZFFPXX zffpxx = new ZFFPXX();
            zffpxx.setFPDM(orderInvoiceInfo.getFpdm());
            zffpxx.setFPHM(orderInvoiceInfo.getFphm());
            zffpxx.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey());
            zffpxx.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getMessage());
            zffpxxList.add(zffpxx);
            zfxxRsp.setZFFPXX(zffpxxList);
            return zfxxRsp;
        }
        /**
         * 判断当前发票是否被冲红,如果已经冲红提示冲红发票不能作废
         */
        if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoiceInfo.getChBz())) {
            log.error("{}作废数据校验未通过，已冲红发票不允许作废,未通过数据:{}", LOGGER_MSG, jsonString);
            zfxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_108038.getKey());
            zfxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_108038.getMessage());
            return zfxxRsp;
        }
    
        String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInvoiceInfo.getXhfNsrsbh());
    
    
        //TODO 百望和航信的作废接口有区分 所以以后需要修改接口文档
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
            /**
             * 校验作废批次号是否重复
             */
            List<InvalidInvoiceInfo> invalidInvoiceInfos = apiInvalidInvoiceService.selectInvalidInvoiceInfo(zfxxReq.getZFPCH(), shList);
            if (ObjectUtil.isNotEmpty(invalidInvoiceInfos)) {
                log.error("{}作废数据校验未通过，作废批次重复,未通过数据:{}", LOGGER_MSG, jsonString);
                zfxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_108037.getKey());
                zfxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_108037.getMessage());
                return zfxxRsp;
            }
    
            /**
             * 根据原发票代码号码查询发票数据,判断是不是红票数据
             */
            //保存作废表
            InvalidInvoiceInfo invalid = new InvalidInvoiceInfo();
    
            invalid.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            invalid.setZfpch(zfxxReq.getZFPCH());
            invalid.setFpdm(orderInvoiceInfo.getFpdm());
            invalid.setFphm(orderInvoiceInfo.getFphm());
            invalid.setSld(orderInvoiceInfo.getSld());
            invalid.setFplx(orderInvoiceInfo.getFpzlDm());
            invalid.setZfyy(zfxxReq.getZFYY());
            invalid.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
            invalid.setZfsj(new Date());
            invalid.setCreateTime(new Date());
            invalid.setUpdateTime(new Date());
            invalid.setZfr("System");
            //正数发票作废
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
                invalid.setZflx(OrderInfoEnum.ZFLX_1.getKey());
            } else {
                invalid.setZflx(OrderInfoEnum.ZFLX_2.getKey());
            }
            invalid.setXhfNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
            invalid.setXhfmc(orderInvoiceInfo.getXhfMc());
    
            log.info("保存作废数据到作废表");
            try {
                invalidInvoiceService.fgProcessSuccessInvalid(invalid, orderInvoiceInfo, shList);
            } catch (OrderReceiveException e) {
                log.error("{}作废异常,未通过数据:{}", LOGGER_MSG, e);
                zfxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_108037.getKey());
                zfxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_108037.getMessage());
                return zfxxRsp;
            }
        
            /**
             * 通知方格获取待作废消息
             */
            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(orderInvoiceInfo.getXhfNsrsbh(), StringUtils.isEmpty(orderInvoiceInfo.getJqbh()) ? orderInvoiceInfo.getSld() : orderInvoiceInfo.getJqbh());
            if (StringUtils.isNotEmpty(registCodeStr)) {
                RegistrationCode registCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                /**
                 * 存放作废信息到redis队列
                 */
                PushPayload pushPayload = new PushPayload();
                //接口发票作废（已开发票作废）
                pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_4);
                pushPayload.setNSRSBH(registCode.getXhfNsrsbh());
                pushPayload.setJQBH(registCode.getJqbh());
                pushPayload.setZCM(registCode.getZcm());
                pushPayload.setZFPCH(zfxxReq.getZFPCH());
                apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
            }
    
            //拼装返回值
    
            zfxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_040003.getKey());
            zfxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_040003.getMessage());
            List<ZFFPXX> zffpxxList = new ArrayList<>();
            ZFFPXX zffpxx = new ZFFPXX();
            zffpxx.setFPDM(orderInvoiceInfo.getFpdm());
            zffpxx.setFPHM(orderInvoiceInfo.getFphm());
            zffpxx.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_040003.getKey());
            zffpxx.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_040003.getMessage());
            zffpxxList.add(zffpxx);
            zfxxRsp.setZFFPXX(zffpxxList);
        } else {
            ZfRequest request = convertToZfRequest(zfxxReq);
            request.setSLDID(orderInvoiceInfo.getSld());
            request.setZFR("System");
            if (StringUtils.isBlank(request.getNSRSBH())) {
                request.setNSRSBH(orderInvoiceInfo.getXhfNsrsbh());
            }
            com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP ykfpzf = HttpInvoiceRequestUtil.zfInvoice(OpenApiConfig.ykfpzf, request, terminalCode);
            log.debug("发票作废接口返回数据:{}", JsonUtils.getInstance().toJsonString(ykfpzf));
            zfxxRsp = ReplaceCharacterUtils.transzfxxRsp(ykfpzf, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm());
            try {
                /**
                 * 查看底层代码发现,不论成功失败都会返回数据.
                 */
            
                //"040000", "发票全部作废成功"
                //"040001", "发票部分作废成功"
                //"040002", "发票全部作废失败"
                // TODO: 2018/10/27 for循环放在外面,内部根据状态进行判断取值 作废需要插入到作废表中 作废需要更新orderInvoice表的状态
    
                /**
                 * 判断外层为成功情况,不论外层作废成功或者是失败,都需要记录数据库
                 * 不用判断底层返回的外层状态,只需要判断底层返回的数组是否为空,如果为空,则都是失败,如果不为空则根据返回数据内层状态进行判断,
                 * 如果状态不是0000则说明作废失败,
                 */
    
    
                if (zfxxRsp.getZFFPXX() != null && zfxxRsp.getZFFPXX().size() > 0) {
                    List<ZFFPXX> deprecateFailedInvoice = zfxxRsp.getZFFPXX();
        
                    /**
                     * 根据原发票代码号码查询发票数据,判断是不是红票数据
                     */
                    for (ZFFPXX deprecateFailedInvoice1 : deprecateFailedInvoice) {
            
                        if (OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey().equals(deprecateFailedInvoice1.getZTDM())) {
                
                            InvalidInvoiceInfo invalid = new InvalidInvoiceInfo();
                            invalid.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
                            invalid.setZfpch(zfxxReq.getZFPCH());
                            invalid.setFpdm(deprecateFailedInvoice1.getFPDM());
                            invalid.setFphm(deprecateFailedInvoice1.getFPHM());
                            invalid.setSld(orderInvoiceInfo.getSld());
                            invalid.setFplx(orderInvoiceInfo.getFpzlDm());
                            invalid.setZfyy(zfxxReq.getZFYY());
                            invalid.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
                            invalid.setZfsj(new Date());
                            invalid.setCreateTime(new Date());
                            invalid.setUpdateTime(new Date());
                
                            //正数发票作废
                            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
                                invalid.setZflx(OrderInfoEnum.ZFLX_1.getKey());
                            } else {
                                invalid.setZflx(OrderInfoEnum.ZFLX_2.getKey());
                            }
                            invalid.setXhfNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
                            invalid.setXhfmc(orderInvoiceInfo.getXhfMc());
                        
                        
                            invalidInvoiceService.processSuccessInvalid(invalid, orderInvoiceInfo, shList);
                        
                        
                        }
                    
                    }
                
                }
            } catch (Exception e) {
                log.error("{},作废接口异常:{}", LOGGER_MSG, e);
            }
        }
    
        return zfxxRsp;
    }
    
    /**
     * bean
     */
    private ZfRequest convertToZfRequest(ZFXX_REQ zfxx_req) {
        ZfRequest request = new ZfRequest();
        request.setFP_DM(zfxx_req.getFPDM());
        request.setFP_QH(zfxx_req.getFPQH());
        request.setFP_ZH(zfxx_req.getFPZH());
        request.setZFLX(zfxx_req.getZFLX());
        request.setZFPCH(zfxx_req.getZFPCH());
        request.setZFYY(zfxx_req.getZFYY());
        request.setNSRSBH(zfxx_req.getXHFSBH());
        return request;
    }
    
    
    /**
     * 订单导入业务处理
     *
     * @param commonInvoices
     * @return
     */
    @Override
    public RESPONSE importOrders(List<com.dxhy.order.consumer.protocol.order.COMMON_INVOICE> commonInvoices) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(commonInvoices);
        log.debug("{},企业数据导入数据:{}", LOGGER_MSG, jsonString2);
    
        RESPONSE importOrdersRsp = new RESPONSE();
        List<CommonOrderInfo> requestOrderList = new ArrayList<>();
        CommonOrderInfo commonOrderInfo;
        /**
         * 业务逻辑处理
         */
        try {
            if (commonInvoices == null || commonInvoices.size() <= 0) {
                log.error("{}企业订单导入数据为空", LOGGER_MSG);
                importOrdersRsp.setSTATUS_CODE(OrderInfoContentEnum.IMPORT_ORDERS_PARAM_NULL.getKey());
                importOrdersRsp.setSTATUS_MESSAGE(OrderInfoContentEnum.IMPORT_ORDERS_PARAM_NULL.getMessage());
                return importOrdersRsp;
            }
    
            /**
             * 数据转换
             */
            /**
             * 数据校验
             */
            for (com.dxhy.order.consumer.protocol.order.COMMON_INVOICE commonInvoice : commonInvoices) {
        
                /**
                 * 数据转换
                 */
        
                commonOrderInfo = BeanTransitionUtils.transitionCommonOrderInfoForImportOrders(commonInvoice);
        
                /**
                 * 数据校验
                 */
    
                Map<String, String> volidateOrder = validateOrderInfo.volidateOrder(commonOrderInfo);
                log.info("{}订单校验接口，返回信息：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(volidateOrder));
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(volidateOrder.get(OrderManagementConstant.ERRORCODE))) {
                    log.error("{}企业订单上传接口，订单数据校验失败:{}", LOGGER_MSG, volidateOrder.get(OrderManagementConstant.ERRORMESSAGE));
                    importOrdersRsp.setSTATUS_CODE(volidateOrder.get(OrderManagementConstant.ERRORCODE));
                    importOrdersRsp.setSTATUS_MESSAGE(volidateOrder.get(OrderManagementConstant.ERRORMESSAGE));
                    return importOrdersRsp;
                }
    
                OrderProcessInfo processInfo = new OrderProcessInfo();
                processInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
                processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_2.getKey());
                processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
                commonOrderInfo.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
                commonOrderInfo.setProcessInfo(processInfo);
                requestOrderList.add(commonOrderInfo);
            }
    
            /**
             * 订单信息入库
             */
    
            iGenerateReadyOpenOrderService.saveOrderInfo(requestOrderList);
    
            importOrdersRsp.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
            importOrdersRsp.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
    
        } catch (Exception e) {
            log.error("{}订单导入接口异常:{}", LOGGER_MSG, e);
            importOrdersRsp.setSTATUS_CODE(OrderInfoContentEnum.IMPORT_ORDERS_ERROR.getKey());
            importOrdersRsp.setSTATUS_MESSAGE(OrderInfoContentEnum.IMPORT_ORDERS_ERROR.getMessage());
        }
        String jsonString = JsonUtils.getInstance().toJsonString(importOrdersRsp);
        log.debug("{},企业订单导入请求状态返回数据:{}", LOGGER_MSG, jsonString);
        return importOrdersRsp;
    }
    
    /**
     * 受理点上下票列表管理接口业务处理
     *
     * @param sldInvoiceRollploRequest
     * @return
     */
    @Override
    public SLDKCMX_RESPONSE queryinvoicerollplolist(SLD_INVOICEROLLPLO_REQUEST sldInvoiceRollploRequest) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(sldInvoiceRollploRequest);
        log.debug("{},受理点上下票列表管理接口数据:{}", LOGGER_MSG, jsonString2);
        SLDKCMX_RESPONSE sldkcmxResponse = new SLDKCMX_RESPONSE();
        if (sldManagerService == null) {
            log.error("{}调用研二受理点上下票管理接口失败", LOGGER_MSG);
            sldkcmxResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getKey());
            sldkcmxResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getMessage());
            return sldkcmxResponse;
        }
        /**
         * 协议bean转换
         */
        SldInvoiceRollPloRequest sldInvoiceRollPloRequest = BeanTransitionUtils.transSldInvoiceReq(sldInvoiceRollploRequest);
    
        log.debug("{}受理点上下票列表管理接口请求数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(sldInvoiceRollPloRequest));
        SldKcmxResponse sldKcmxResponse = sldManagerService.queryInvoiceRollPloList(sldInvoiceRollPloRequest);
        log.debug("{}受理点上下票列表管理接口返回数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(sldKcmxResponse));
        if (sldKcmxResponse == null || sldKcmxResponse.getKclb().size() <= 0) {
            log.error("{}受理点上下票列表管理接口失败或返回数据为空", LOGGER_MSG);
            sldkcmxResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getKey());
            sldkcmxResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getMessage());
            return sldkcmxResponse;
        }
    
        /**
         * 协议bean转换
         */
        sldkcmxResponse = BeanTransitionUtils.transSldInvoiceRsp(sldKcmxResponse);
    
        String jsonString = JsonUtils.getInstance().toJsonString(sldkcmxResponse);
        log.debug("{}受理点上下票列表管理接口最终返回数据:{}", jsonString, LOGGER_MSG);
        return sldkcmxResponse;
    }
    
    /**
     * 受理点上票接口业务逻辑处理
     *
     * @param sldupRequest
     * @return
     */
    @Override
    public RESPONSE accessPointUpInvoice(SLDUP_REQUEST sldupRequest) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(sldupRequest);
        log.debug("{},受理点上票接口数据:{}", LOGGER_MSG, jsonString2);
        RESPONSE response = new RESPONSE();
        if (sldManagerService == null) {
            log.error("{}调用研二上票接口失败", LOGGER_MSG);
            response.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getKey());
            response.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getMessage());
            return response;
        }
        /**
         * 协议bean转换
         */
        SldUpRequest sldUpRequest = BeanTransitionUtils.transSldUpInvoiceReq(sldupRequest);
    
        log.debug("{}获取受理点上票接口请求数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(sldUpRequest));
        Response response1 = sldManagerService.accessPointUpInvoice(sldUpRequest);
        log.debug("{}获取受理点上票接口返回数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(response1));
    
        /**
         * 协议bean转换
         */
        response = BeanTransitionUtils.transSldInvoiceCommonRsp(response1);
    
        String jsonString = JsonUtils.getInstance().toJsonString(response);
        log.debug("{}获取受理点上票接口最终返回数据:{}", jsonString, LOGGER_MSG);
        return response;
    }
    
    
    /**
     * 受理点下票接口业务逻辑处理
     *
     * @param slddownRequest
     * @return
     */
    @Override
    public RESPONSE accessPointDownInvoice(SLDDOWN_REQUEST slddownRequest) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(slddownRequest);
        log.debug("{},受理点下票接口数据:{}", LOGGER_MSG, jsonString2);
        RESPONSE response = new RESPONSE();
        if (sldManagerService == null) {
            log.error("{}调用研二下票接口失败", LOGGER_MSG);
            response.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getKey());
            response.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_AUTO_DATA_INTERFACE_FAILED.getMessage());
            return response;
        }
        /**
         * 协议bean转换
         */
        SldDownRequest sldDownRequest = BeanTransitionUtils.transSldDownInvoiceReq(slddownRequest);
    
        log.debug("{}获取受理点下票接口请求数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(sldDownRequest));
        Response response1 = sldManagerService.accessPointDownInvoice(sldDownRequest);
        log.debug("{}获取受理点下票接口返回数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(response1));
    
        /**
         * 协议bean转换
         */
        response = BeanTransitionUtils.transSldInvoiceCommonRsp(response1);
    
        String jsonString = JsonUtils.getInstance().toJsonString(response);
        log.debug("{}获取受理点下票接口最终返回数据:{}", jsonString, LOGGER_MSG);
        return response;
    }
    
    
    /**
     * 受理点下票接口业务逻辑处理
     *
     * @param sldSearchRequest
     * @return
     */
    @Override
    public RESPONSE querySld(SLD_SEARCH_REQUEST sldSearchRequest) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(sldSearchRequest);
        log.debug("{},受理点列表接口数据:{}", LOGGER_MSG, jsonString2);
        RESPONSE response = new RESPONSE();
    
        /**
         * 协议bean转换
         */
        Set<SearchSld> searchSldSet = new HashSet<>();
        String terminalCode = OrderInfoEnum.TAX_EQUIPMENT_C48.getKey();
        String url = OpenApiConfig.querySldList;
        if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            url = OpenApiConfig.queryKpdXxBw;
        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
            url = OpenApiConfig.queryNsrXnsbxx;
        }
        HttpInvoiceRequestUtil.getSldList(searchSldSet, url, sldSearchRequest.getFPZLDM(), "", sldSearchRequest.getNSRSBH(), null, null, terminalCode);
    
        /**
         * 协议bean转换
         */
        response = BeanTransitionUtils.transSldSearchRsp(searchSldSet);
    
        String jsonString = JsonUtils.getInstance().toJsonString(response);
        log.debug("{}获取受理点下票接口最终返回数据:{}", jsonString, LOGGER_MSG);
        return response;
    }
    
    /**
     * 根据订单号获取订单数据接口业务逻辑处理
     *
     * @param orderRequest
     * @return
     */
    @Override
    public ORDER_RESPONSE getOrderInfoAndInvoiceInfo(ORDER_REQUEST orderRequest) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(orderRequest);
        log.debug("{},根据订单号获取订单数据以及发票数据接口数据:{}", LOGGER_MSG, jsonString2);
    
        ORDER_RESPONSE orderResponse = new ORDER_RESPONSE();
        orderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
    
        try {
            if (orderRequest == null) {
                orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return orderResponse;
            } else if (StringUtils.isBlank(orderRequest.getNSRSBH())) {
                orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH.getKey());
                orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据销方税号为空", LOGGER_MSG);
                return orderResponse;
            } else if (StringUtils.isBlank(orderRequest.getDDH())) {
                orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_DDH.getKey());
                orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_DDH.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据订单号为空", LOGGER_MSG);
                return orderResponse;
            }
            
            /**
             * 根据请求数据获取对应的订单信息.
             * 1.循环数据列表
             * 2.补全头信息
             * 3.根据订单状态判断是否需要查询发票表数据进行补全
             * 4.补全明细信息
             */
            List<String> shList = new ArrayList<>();
            shList.add(orderRequest.getNSRSBH());
            Map<String, Object> paraMap = new HashMap<>(5);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_FPQQLSH, orderRequest.getFPQQLSH());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_DDH, orderRequest.getDDH());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_TQM, orderRequest.getTQM());
            
            List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(paraMap, shList);
            List<OrderProcessInfo> finalList = new ArrayList<>(orderProcessInfos);
            for (OrderProcessInfo orderProcessInfo : orderProcessInfos) {
                
                List<OrderProcessInfo> findChildList = apiOrderProcessService.findChildList(orderProcessInfo.getId(), shList);
                finalList.addAll(findChildList);
            }
            
            if (finalList.size() <= 0) {
                orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                log.error("{},根据订单号:{}获取订单数据以及发票数据接口,查询订单处理表数据为空", LOGGER_MSG, orderRequest.getDDH());
                return orderResponse;
            }
        
            COMMON_ORDER_INVOCIE[] commonOrderInvocies = new COMMON_ORDER_INVOCIE[finalList.size()];
            /**
             * 遍历数据
             */
            for (int i = 0; i < finalList.size(); i++) {
                COMMON_ORDER_INVOCIE commonOrderInvocie = new COMMON_ORDER_INVOCIE();
                /**
                 * 根据orderid查询order信息表数据.
                 */
                OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
                OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(finalList.get(i).getOrderInfoId(), shList);
                String orderStatus = finalList.get(i).getDdzt();
                if (orderInfo == null) {
                    orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                    orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                    log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求id为:{}查询订单表数据为空", LOGGER_MSG, orderRequest.getDDH(), finalList.get(i).getOrderInfoId());
                    return orderResponse;
                }
                if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderStatus)) {
                    orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(finalList.get(i).getFpqqlsh(), shList);
                    if (orderInvoiceInfo == null) {
                        orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                        orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                        log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求id为:{}查询订单发票表数据为空", LOGGER_MSG, orderRequest.getDDH(), finalList.get(i).getId());
                        return orderResponse;
                    }
                }
    
    
                /**
                 * 数据组装
                 * 订单状态  0 未开具 1 开具成功 2 开具失败
                 */
                ORDER_INVOICE_HEAD orderInvoiceHead = com.dxhy.order.utils.BeanTransitionUtils.transitionORDER_INVOICE_HEAD(orderInfo, orderInvoiceInfo);
                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderStatus)
                        || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderStatus) ||
                        OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderStatus)) {
                    orderInvoiceHead.setSTATUS(OrderInfoEnum.INTERFACE_GETORDERANDINVOICE_STATUS_0.getKey());
        
                } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderStatus)) {
                    orderInvoiceHead.setSTATUS(OrderInfoEnum.INTERFACE_GETORDERANDINVOICE_STATUS_1.getKey());
                } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderStatus) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderStatus)) {
                    orderInvoiceHead.setSTATUS(OrderInfoEnum.INTERFACE_GETORDERANDINVOICE_STATUS_2.getKey());
                }
    
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceHead.getFPZLDM()) && StringUtils.isNotEmpty(orderInvoiceHead.getFP_DM()) && StringUtils.isNotEmpty(orderInvoiceHead.getFP_HM())) {
                    /**
                     * 根据流水号从批次表中查询批次号.
                     * 根据发票代码号码调用接口获取pdf字节流
                     */
                    InvoiceBatchRequestItem invoiceBatchRequestItem = apiInvoiceCommonMapperService.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                    if (invoiceBatchRequestItem == null) {
                        orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                        orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                        log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求id为:{}查询批量开票数据为空", LOGGER_MSG, orderRequest.getDDH(), orderInvoiceHead.getFPQQLSH());
                        return orderResponse;
                    }
                    GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(invoiceBatchRequestItem.getFpqqpch(), orderInvoiceHead.getXSF_NSRSBH(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey(), orderInvoiceHead.getFP_DM(), orderInvoiceHead.getFP_HM(), orderInvoiceInfo.getPdfUrl());
                    GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
    
    
                    if (pdf == null || OrderInfoContentEnum.INVOICE_ERROR_CODE_114004.getKey().equals(pdf.getSTATUS_CODE()) || pdf.getResponse_EINVOICE_PDF().size() <= 0) {
    
                        orderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_QUERY_ERROR.getKey());
                        orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_QUERY_ERROR.getMessage());
                        log.error("{},根据订单号:{}获取订单数据以及发票数据接口,调用pdf获取接口返回数据为空", LOGGER_MSG, orderRequest.getDDH());
                        return orderResponse;
                    }
                    orderInvoiceHead.setPDF_FILE(pdf.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
                } else {
                    /**
                     *  不返回pdf文件
                     */
                    orderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
                    orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        
                }
    
                /**
                 * 组装明细信息
                 */
                List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
                if (orderItemInfos == null || orderItemInfos.size() <= 0) {
                    orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL.getKey());
                    orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL.getMessage());
                    log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求查询参数为:{}明细数据为空", LOGGER_MSG, orderRequest.getDDH(), orderInfo.getId());
                    return orderResponse;
                }
                List<ORDER_INVOICE_ITEM> orderInvoiceItems = com.dxhy.order.utils.BeanTransitionUtils.transitionORDER_INVOICE_ITEM(orderItemInfos);
    
                commonOrderInvocie.setORDER_INVOICE_HEAD(orderInvoiceHead);
                commonOrderInvocie.setORDER_INVOICE_ITEMS(orderInvoiceItems);
                commonOrderInvocies[i] = commonOrderInvocie;
            }
        
            orderResponse.setCOMMON_ORDER_INVOCIES(commonOrderInvocies);
        
        } catch (Exception e) {
            orderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_ERROR.getKey());
            orderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_ERROR.getMessage());
            log.error("{},根据订单号:{}获取订单数据以及发票数据接口异常,异常原因为:{}", LOGGER_MSG, orderRequest.getDDH(), e);
        }
    
        String jsonString = JsonUtils.getInstance().toJsonString(orderResponse);
        log.debug("{},企业订单导入请求状态返回数据:{}", LOGGER_MSG, jsonString);
        return orderResponse;
    }
    
    /**
     * 红字发票申请单上传接口
     *
     * @param hzfpsqbscsReq
     * @return
     */
    @Override
    public R specialInvoiceRushRed(String hzfpsqbscsReq) {
        R r = new R();
        log.debug("{},接收红字发票申请单:{}", LOGGER_MSG, hzfpsqbscsReq);
        HzfpsqbsReq hzfpsqbsReq = new HzfpsqbsReq();
        JSONObject po = JSON.parseObject(hzfpsqbscsReq);
        //转换batch
        JSONObject jsonObject = po.getJSONObject("HZFPSQBSCS_BATCH");
        String jsonString = JSONObject.toJSONString(jsonObject);
        HzfpsqbscBatch batch = JSONObject.parseObject(jsonString, HzfpsqbscBatch.class);
        hzfpsqbsReq.setHZFPSQBSCSBATCH(batch);
        List<String> shList = new ArrayList<>();
        shList.add(batch.getNSRSBH());
        //转换数组
        JSONArray jsonArray = po.getJSONArray("HZFPSQBSC");
        String jsonString1 = JSONObject.toJSONString(jsonArray);
        List<Hzfpsqbsc> parseArray = JSONObject.parseArray(jsonString1, Hzfpsqbsc.class);
        Hzfpsqbsc[] hzfpsqbsc = new Hzfpsqbsc[parseArray.size()];
        Hzfpsqbsc hh = new Hzfpsqbsc();
        for (int i = 0; i < parseArray.size(); i++) {
            HzfpsqbsHead hzfpsqbsHead = parseArray.get(i).getHZFPSQBSCHEAD();
    
            //如果根据发票代码发票号码能查到发票 不全原发票开票日期字段
            if (StringUtils.isBlank(hzfpsqbsHead.getYFP_KPRQ())) {
                log.warn("专票冲红企业传递的原开票日期为空,红字申请单编号:{}", hzfpsqbsHead.getSQBSCQQLSH());
                if (StringUtils.isNotBlank(hzfpsqbsHead.getYFP_DM()) && StringUtils.isNotBlank(hzfpsqbsHead.getYFP_HM())) {
                    OrderInvoiceInfo selectOrderInvoiceInfoByFpdmAndFphm = apiOrderInvoiceInfoService
                            .selectOrderInvoiceInfoByFpdmAndFphm(hzfpsqbsHead.getYFP_DM(),
                                    hzfpsqbsHead.getYFP_HM(), shList);
                    if (selectOrderInvoiceInfoByFpdmAndFphm != null) {
                        log.info("根据发票代码号码查到的发票信息不为空，补全原发票开票日期:{}", hzfpsqbsHead.getSQBSCQQLSH());
                        hzfpsqbsHead.setYFP_KPRQ(DateUtil.format(selectOrderInvoiceInfoByFpdmAndFphm.getKprq(), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
    
            hh.setHZFPSQBSCHEAD(hzfpsqbsHead);
            hzfpsqbsc[i] = hh;
            String jsonString2 = JSONObject.toJSONString(parseArray.get(i).getHZFPSQBSCDETAILIST());
            List<HzfpsqbsDetail> parseArray2 = JSONObject.parseArray(jsonString2, HzfpsqbsDetail.class);
            if (parseArray2 != null) {
                HzfpsqbsDetail[] detail = new HzfpsqbsDetail[parseArray2.size()];
                for (int j = 0; j < parseArray2.size(); j++) {
    
                    detail[j] = parseArray2.get(j);
                    if (StringUtils.isBlank(parseArray2.get(j).getGGXH())) {
                        detail[j].setGGXH("");
                    }
                }
                hzfpsqbsc[i].setHZFPSQBSCDETAILIST(detail);
            }
        }
        hzfpsqbsReq.setHZFPSQBSCLIST(hzfpsqbsc);
    
        log.debug("{},转换后数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(hzfpsqbsReq));
        if (StringUtils.isBlank(hzfpsqbscsReq)) {
            log.info("{}接收红字发票申请单为空", LOGGER_MSG);
            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getMessage());
        }
    
        HpUploadResponse redInvoiceUpload = HttpInvoiceRequestUtil.redInvoiceUpload(OpenApiConfig.redInvoiceUpload, hzfpsqbsReq, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
    
    
        String jsonString2 = JsonUtils.getInstance().toJsonString(BeanTransitionUtils.convertHzfpQbs(redInvoiceUpload));
        log.debug("红字发票申请接口研二返回数据:{},{}", jsonString2, LOGGER_MSG);
        r.put(OrderManagementConstant.DATA, redInvoiceUpload);
        return r;
    }
    
    
    /**
     * 红字发票申请单审核结果下载
     *
     * @param hpInvocieRequest
     * @return
     */
    @Override
    public R downSpecialInvoice(HpInvocieRequest hpInvocieRequest) {
        R r = new R();
        if (StringUtils.isBlank(hpInvocieRequest.getTKRQ_Q()) || StringUtils.isBlank(hpInvocieRequest.getTKRQ_Z())) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.DATE, -5);
            hpInvocieRequest.setTKRQ_Q(DateUtils.getYYYYMMDDFormatStr(currentCalendar.getTime()));
            hpInvocieRequest.setTKRQ_Z(DateUtils.getYYYYMMDDFormatStr(new Date()));
        }
    
        String jsonString = JsonUtils.getInstance().toJsonString(hpInvocieRequest);
        log.debug("红字发票申请下载数据:{},{}", jsonString, LOGGER_MSG);
        if (StringUtils.isBlank(jsonString)) {
            log.error("红字发票申请下载数据为空{},{}", jsonString, LOGGER_MSG);
            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_DATA_NULL.getMessage());
        }
    
    
        HpResponseBean redInvoiceDown = HttpInvoiceRequestUtil.redInvoiceDown(OpenApiConfig.redInvoiceDown, hpInvocieRequest, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
    
    
        String jsonString2 = JsonUtils.getInstance().toJsonString(BeanTransitionUtils.convertHzResponse(redInvoiceDown));
        log.debug("红字发票下载研二返回数据:{},{}", jsonString2, LOGGER_MSG);
        r.put(OrderManagementConstant.DATA, redInvoiceDown);
        return r;
    }
    
    
    /**
     * 成品油局端可下载库存
     *
     * @param cpyJdkcRequest
     * @return
     */
    @Override
    public CPY_JDKC_RESPONSE queryCpyJdKc(CPY_JDKC_REQUEST cpyJdkcRequest) {
        CpyJdKcRequest cpyJdKcRequest = BeanTransitionUtils.transitionJdkcRequest(cpyJdkcRequest);
        log.info("{}查询成品油局端可下载库存入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(cpyJdkcRequest));
        CpyJdKcResponse queryCpyJdKc = cpyManageService.queryCpyJdKc(cpyJdKcRequest);
        log.info("{}查询成品油局端可下载库存出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(queryCpyJdKc));
        /**
         * 数据转换
         */
        
        return BeanTransitionUtils.transtionCpyJdKcResponse(queryCpyJdKc);
    }
    
    /**
     * 成品油已下载库存
     *
     * @param cpyYxzkcRequest
     * @return
     */
    @Override
    public CPY_YXZKC_RESPONSE queryCpyYxzKc(CPY_YXZKC_REQUEST cpyYxzkcRequest) {
        CpyYxzKcRequest cpyYxzKcRequest = BeanTransitionUtils.transitionYxzkcRequest(cpyYxzkcRequest);
        log.info("{}成品油已下载库存,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(cpyYxzKcRequest));
        CpyYxzKcResponse queryCpyYxzKc = cpyManageService.queryCpyYxzKc(cpyYxzKcRequest);
        log.info("{}成品油已下载库存,出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(queryCpyYxzKc));
        return BeanTransitionUtils.transitionCpyYxzKcResponse(queryCpyYxzKc);
    }
    
    /**
     * 成品油库存下载
     *
     * @param downloadCpykcRequest
     * @return
     */
    @Override
    public DOWNLOAD_CPYKC_RESPONSE downloadCpyKc(DOWNLOAD_CPYKC_REQUEST downloadCpykcRequest) {
        DownloadCpyKcRequest downloadCpyKcRequest = BeanTransitionUtils.transitionCpykcRequest(downloadCpykcRequest);
        log.info("{}成品油库存下载接口，入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(downloadCpyKcRequest));
        DownloadCpyKcResponse downloadCpyKc = cpyManageService.downloadCpyKc(downloadCpyKcRequest);
        log.info("{}成品油库存下载接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(downloadCpyKc));
        return BeanTransitionUtils.transitionDownloadCpyKcResponse(downloadCpyKc);
    }
    
    /**
     * 成品油库存退回
     *
     * @param backCpykcRequest
     * @return
     */
    @Override
    public BACK_CPYKC_RESPONSE backCpyKc(BACK_CPYKC_REQUEST backCpykcRequest) {
        BackCpyKcRequest backCpyKcRequest = BeanTransitionUtils.transitionCpykchtRequest(backCpykcRequest);
        log.info("{}成品油库存退回接口，入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(backCpyKcRequest));
        BackCpyKcResponse backCpyKc = cpyManageService.backCpyKc(backCpyKcRequest);
        log.info("{}成品油库存退回接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(backCpyKc));
        
        return BeanTransitionUtils.transitionBackCpyKcResponse(backCpyKc);
    }
    
    /**
     * 成品油库存同步
     *
     * @param syncCpykcRequest
     * @return
     */
    @Override
    public SYNC_CPYKC_RESPONSE syncCpyKc(SYNC_CPYKC_REQUEST syncCpykcRequest) {
        SyncCpyKcRequest syncCpyKcRequest = BeanTransitionUtils.transitionCpykctbRequest(syncCpykcRequest);
        log.info("{}成品油库存同步接口，入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(syncCpyKcRequest));
        SyncCpyKcResponse syncCpyKc = cpyManageService.syncCpyKc(syncCpyKcRequest);
        log.info("{}成品油库存同步接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(syncCpyKc));
        return BeanTransitionUtils.transitionSyncCpyKcResponse(syncCpyKc);
    }
    
    
}
