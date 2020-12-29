package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.openapi.service.FangGeInterfaceService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceRequest;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceResponseExtend;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.mqdata.FpkjMqData;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 开票接口业务层处理类
 *
 * @author ZSC-DXHY
 */
@Service
@Slf4j
public class MakeOutAnInvoiceServiceImpl implements MakeOutAnInvoiceService {
    
    private static final String LOGGER_MSG = "(开票接口业务类)";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference(retries = 0)
    private OpenInvoiceService openInvoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Resource
    private UnifyService unifyService;
    
    @Reference
    private InvoiceDataService invoiceDataService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Resource
    private FangGeInterfaceService fangGeInterfaceService;

    @Reference
    private ApiRushRedInvoiceRequestInfoService apiRushRedInvoiceRequestInfoService;

    /**
     * 调用开票接口
     *
     * @param commonOrderInfos
     * @param sldMap
     * @return
     */
    @Override
    public R makeOutAnInovice(List<CommonOrderInfo> commonOrderInfos, Map<String, PageSld> sldMap) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(commonOrderInfos);
        log.info("{}，接收到开票请求,数据:{},受理点信息:{}", LOGGER_MSG, jsonString2, JsonUtils.getInstance().toJsonString(sldMap));
        R r = new R();
        
        
        String ppSldId = "";
        String ppSldMc = "";
        String ppFjh = "";
        String zpSldId = "";
        String zpSldMc = "";
        String zpFjh = "";
        /**
         * 1.循环遍历受理点获取受理点对应的开票机号
         * 2.循环每条数据去请求底层进行开票
         */
        if (ObjectUtil.isNotEmpty(sldMap)) {
            for (String key : sldMap.keySet()) {
                String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
                if (("_" + OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey()).equals(key)) {
                    PageSld pageSld = sldMap.get(key);
                    if(pageSld != null && StringUtils.isNotBlank(pageSld.getSldid())){
                        QueryNextInvoiceRequest queryNextInvoiceRequest = new QueryNextInvoiceRequest();
                        queryNextInvoiceRequest.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
                        queryNextInvoiceRequest.setNsrsbh(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
                        queryNextInvoiceRequest.setSldId(pageSld.getSldid());
                        QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = HttpInvoiceRequestUtil.queryNextInvoice(OpenApiConfig.queryNextInvoice, queryNextInvoiceRequest, terminalCode);
                        zpFjh = queryNextInvoiceResponseExtend.getFjh();
                        zpSldId = pageSld.getSldid();
                        zpSldMc = pageSld.getSldmc();
                    }

                } else if (("_" + OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey()).equals(key)) {
                    PageSld pageSld = sldMap.get(key);
                    if(pageSld != null && StringUtils.isNotBlank(pageSld.getSldid())) {
                        QueryNextInvoiceRequest queryNextInvoiceRequest = new QueryNextInvoiceRequest();
                        queryNextInvoiceRequest.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
                        queryNextInvoiceRequest.setNsrsbh(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
                        queryNextInvoiceRequest.setSldId(pageSld.getSldid());
                        QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = HttpInvoiceRequestUtil.queryNextInvoice(OpenApiConfig.queryNextInvoice, queryNextInvoiceRequest, terminalCode);
                        ppFjh = queryNextInvoiceResponseExtend.getFjh();
                        ppSldId = pageSld.getSldid();
                        ppSldMc = pageSld.getSldmc();
                    }
                }
            }
        }
        
        List<String> shList = new ArrayList<>();
        shList.add(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
        /**
         * 数据校验
         * 1.判断数据是否为空,
         * 2.判断数据是否超过接口上限1000条,
         *  2.保证一批数据请求时数据为同一个销方税号
         *  4.保证一批数据请求时数据为同一个开票机号
         *  5.保证一批数据请求时数据为同一个发票类型
         *  6.保证一批数据请求时数据为同一个发票类别
         * 发票明细数据校验
         *
         *
         */
        
        if (commonOrderInfos.size() <= ConfigureConstant.INT_0) {
            log.error("{}传递的开票数据为空,或者是长度小于0 ", LOGGER_MSG);
            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getMessage());
        }
    
        if (commonOrderInfos.size() >= ConfigureConstant.PC_MAX_ITEM_LENGTH) {
            log.error("{}已超过1000张要开票数据", LOGGER_MSG);
            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_NUMBER.getMessage());
        }
    

    
        /**
         * 校验套餐余量
         */
        R result = iCommonInterfaceService.mealAllowance(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(result.get(OrderManagementConstant.CODE))) {
            log.error("{} 套餐余量查询：{}", LOGGER_MSG, result.get(OrderManagementConstant.MESSAGE));
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999)
                    .put(OrderManagementConstant.MESSAGE, result.get(OrderManagementConstant.MESSAGE));
        }
    
        /**
         * 根据税号查询税控设备
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
    
        /**
         * 对订单请求数据进行校验,校验失败直接返回
         */
        for (CommonOrderInfo commonOrderInfo : commonOrderInfos) {
        
        
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(commonOrderInfo.getOrderInfo().getKplx()) && !StringUtils.isBlank(commonOrderInfo.getOrderInfo().getYfpDm()) && !StringUtils.isBlank(commonOrderInfo.getOrderInfo().getYfpHm())) {
                log.error("{}开票类型为蓝票时,不能有原发票代码,发票号码", LOGGER_MSG);
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_BLUE_DMHM.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_BLUE_DMHM.getMessage());
            }
            /**
             * 专票冲红时带着flagbs,需要特殊处理
             */
            if (StringUtils.isBlank(commonOrderInfo.getFlagbs())) {
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonOrderInfo.getOrderInfo().getKplx()) && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getYfpDm()) && StringUtils.isBlank(commonOrderInfo.getOrderInfo().getYfpHm())) {
                    log.error("{}开票类型为红票时,必须有原发票代码、号码", LOGGER_MSG);
                    return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_RED_DMHM.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_RED_DMHM.getMessage());
                }
            }
        
            /**
             * 税控设备类型添加到订单主信息中
             */
            commonOrderInfo.setTerminalCode(terminalCode);
        
            /**
             * 对订单请求数据进行校验,校验失败直接返回
             */
            commonOrderInfo.setKpfs(OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey());
            Map<String, String> checkInvParam = validateOrderInfo.checkOrderInvoice(commonOrderInfo);
            if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
                log.error("{}数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
                return r.put(OrderManagementConstant.CODE, checkInvParam.get(OrderManagementConstant.ERRORCODE)).put(OrderManagementConstant.MESSAGE, checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
            }

            /**
             * 如果是红票需要做折扣行合并操作
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonOrderInfo.getOrderInfo().getKplx())) {
                Map<String, Object> mergeResult = apiRushRedInvoiceRequestInfoService.itemMerge(commonOrderInfo);
                if (OrderInfoContentEnum.SUCCESS.getKey().equals(mergeResult.get(OrderManagementConstant.ERRORCODE))) {
                    CommonOrderInfo commonOrderInfo1 = (CommonOrderInfo) mergeResult.get(OrderManagementConstant.DATA);
                    commonOrderInfo.setOrderItemInfo(commonOrderInfo1.getOrderItemInfo());
                } else {
                    log.error("{}合并商品折扣行失败，未通过数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
                    r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getMessage());
                    return buildMakeOutInvoiceResult(r, commonOrderInfo.getOrderInfo().getFpzlDm(), false);
                }
            }

        }
    
    
        for (int m = 0; m < commonOrderInfos.size(); m++) {
            String sld = "";
            String sldMc = "";
            String fjh = "";
    
            List<OrderInvoiceInfo> orderInvoiceInfoList = new ArrayList<>();
            List<OrderProcessInfo> updateProcessInfo = new ArrayList<>();
            List<InvoiceBatchRequest> insertBatchRequest = new ArrayList<>();
            List<List<InvoiceBatchRequestItem>> insertBatchItem = new ArrayList<>();
            List<SpecialInvoiceReversalEntity> updateSpecialInvoiceList = new ArrayList<>();
            OrderInvoiceInfo formerBlueInvoiceStatus = new OrderInvoiceInfo();
    
    
            /**
             * 此处判断受理点map是否为空,如果不为空,说明是前端传递的受理点,需要对数据进行赋值
             */
            if (ObjectUtil.isNotEmpty(sldMap) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(commonOrderInfos.get(m).getOrderInfo().getFpzlDm())) {
        
                sld = zpSldId;
                sldMc = zpSldMc;
                fjh = zpFjh;
            }
            if (ObjectUtil.isNotEmpty(sldMap) && OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(commonOrderInfos.get(m).getOrderInfo().getFpzlDm())) {
        
                sld = ppSldId;
                sldMc = ppSldMc;
                fjh = ppFjh;
            }
            String registrationCodeStr = "";
    
            /**
             * 方格开票点赋值
             */
            if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                //redis获取里面获取注册的税盘信息
                registrationCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh(), sld);
                if (StringUtils.isBlank(registrationCodeStr)) {
                    log.error("redis中没有获取到注册码信息，税号为：{},机器编号为：{}", commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh(), sld);
                    return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_FG_SLD_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_FG_SLD_NULL.getMessage());
                } else {
                    RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registrationCodeStr, RegistrationCode.class);
                    sld = String.valueOf(registrationCode.getJqbh());
                    commonOrderInfos.get(m).getOrderInfo().setKpjh(String.valueOf(registrationCode.getJqbh()));
                    sldMc = String.valueOf(registrationCode.getJqbh());
                    fjh = String.valueOf(registrationCode.getJqbh());
                }
            }
    
            //根据process表的订单状态判断是否是异常订单重开
            OrderProcessInfo processInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(commonOrderInfos.get(m).getOrderInfo().getFpqqlsh(), shList);
            if (processInfo == null) {
                log.error("根据流水号查到的订单处理表数据为空！fpqqlsh：{}", commonOrderInfos.get(m).getOrderInfo().getFpqqlsh());
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getMessage());
            }
    
            /**
             * 发票请求批次号和发票请求流水号规则说明
             * 1.没有编辑过的异常订单,重新开具的发票请求流水号和发票请求批次号使用原来的请求流水号
             * 2.编辑过的异常订单，重新开具的发票请求流水号和批次号重新生成
             * 3.正常发票的开具流水号随机生成
             */
    
            String fpqqpch = "";
            String kplsh = "";
            boolean result1 = ConfigureConstant.STRING_0.equals(processInfo.getEditStatus()) && (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(processInfo.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(processInfo.getDdzt()));
            if (result1) {
                OrderInvoiceInfo selectByOrderProcessId = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(commonOrderInfos.get(m).getOrderInfo().getFpqqlsh(), shList);
                if (selectByOrderProcessId != null) {
            
                    fpqqpch = selectByOrderProcessId.getKplsh().substring(0, selectByOrderProcessId.getKplsh().length() - 3);
                    CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
                    kplsh = selectByOrderProcessId.getKplsh();
                } else {
                    //生成批次号和流水号
                    /**
                     * 如果是百望的电票 发票批次号最多17为 发票请求流水号最多20位
                     */
                    if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)
                            || OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                        fpqqpch = RandomUtil.randomString(17);
                    } else {
                        fpqqpch = apiInvoiceCommonMapperService.getGenerateShotKey();
                    }

                    DecimalFormat df = new DecimalFormat("000");
                    String format = df.format(1);
                    kplsh = fpqqpch + format;
                }
            }else{
                //生成批次号和流水号
                /**
                 * 如果是百望的电票 发票批次号最多17为 发票请求流水号最多20位
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)
                        || OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                    fpqqpch = RandomUtil.randomString(17);
                } else {
                    fpqqpch = apiInvoiceCommonMapperService.getGenerateShotKey();
                }

                DecimalFormat df = new DecimalFormat("000");
                String format = df.format(1);
                kplsh = fpqqpch + format;
            }


            String invoiceBatchRequestId = apiInvoiceCommonMapperService.getGenerateShotKey();
            /**
             * 业务数据库批次数据组装
             */
            InvoiceBatchRequest invoiceBatchRequest = BeanTransitionUtils.getInvoiceBatchRequest(commonOrderInfos.get(0).getOrderInfo());
            invoiceBatchRequest.setId(invoiceBatchRequestId);
            invoiceBatchRequest.setSldid(sld);
            invoiceBatchRequest.setFpqqpch(fpqqpch);
            // 已抵扣情况，batch中纳税人识别号和head的纳税人识别号保持一致
            if (StringUtils.isNotBlank(commonOrderInfos.get(0).getFlagbs())) {
                invoiceBatchRequest.setXhfNsrsbh(commonOrderInfos.get(0).getOrderInfo().getXhfNsrsbh());
            }
            insertBatchRequest.add(invoiceBatchRequest);
            log.debug("{},循环次数{}", LOGGER_MSG, m + 1);
            OrderInfo orderInfo = commonOrderInfos.get(m).getOrderInfo();
        
        
            /**
             * 内层数据校验
             * 1.校验数据是否为空
             *
             */
            if (orderInfo == null) {
                log.error("{}传递的开票数据为空,", LOGGER_MSG);
                return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_AUTO_PARAM_NULL.getMessage());
            }
    
    
            /**
             * 手工设置受理点名称和受理点
             */
            orderInfo.setSld(sld);
            orderInfo.setSldMc(sldMc);
            orderInfo.setKpjh(fjh);
            List<OrderItemInfo> orderItemInfo = commonOrderInfos.get(m).getOrderItemInfo();
    
            /**
             * 业务数据组装
             */
    
            String orderInvoiceInfoId = apiInvoiceCommonMapperService.getGenerateShotKey();
            String invoiceBatchRequestItemId = apiInvoiceCommonMapperService.getGenerateShotKey();


            /**
             * 业务数据库批次明细数据组装
             */
            List<InvoiceBatchRequestItem> invoiceBatchRequestItems = new ArrayList<>();
            InvoiceBatchRequestItem invoiceBatchRequestItem = new InvoiceBatchRequestItem();
            invoiceBatchRequestItem.setId(invoiceBatchRequestItemId);
            invoiceBatchRequestItem.setInvoiceBatchId(invoiceBatchRequestId);
            invoiceBatchRequestItem.setFpqqpch(invoiceBatchRequest.getFpqqpch());
            invoiceBatchRequestItem.setFpqqlsh(orderInfo.getFpqqlsh());
            invoiceBatchRequestItem.setKplsh(kplsh);
            invoiceBatchRequestItem.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            invoiceBatchRequestItem.setCreateTime(new Date());
            invoiceBatchRequestItem.setUpdateTime(new Date());
            invoiceBatchRequestItems.add(invoiceBatchRequestItem);
            insertBatchItem.add(invoiceBatchRequestItems);
        
            /**
             * 清单标志赋值
             */
            BeanTransitionUtils.getOrderInvoiceInfoQdBz(terminalCode, orderInfo, orderItemInfo);
        
            /**
             * 订单发票数据表组装数据
             */
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
            BeanTransitionUtils.transitionOrderInvoiceInfo(orderInvoiceInfo, orderInfo);
            orderInvoiceInfo.setKplsh(kplsh);
           
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                if (StringUtils.isBlank(orderInvoiceInfo.getHzxxbbh())) {
                    orderInvoiceInfo.setHzxxbbh(commonOrderInfos.get(m).getHzfpxxbbh());
                }
                
                // 开票的时候把红字申请单状态更新成开票中
                SpecialInvoiceReversalEntity updateSpecialInvoice = new SpecialInvoiceReversalEntity();
                updateSpecialInvoice.setXxbbh(orderInvoiceInfo.getHzxxbbh());
                updateSpecialInvoice.setKpzt(OrderInfoEnum.SPECIAL_INVOICE_STATUS_1.getKey());
                updateSpecialInvoiceList.add(updateSpecialInvoice);
                
            }
    
            //红票可冲红金额为0，蓝票为原价税合计
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
                orderInvoiceInfo.setSykchje("0");
            } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
                orderInvoiceInfo.setSykchje(orderInfo.getKphjje());
            }
            orderInvoiceInfo.setId(orderInvoiceInfoId);
            orderInvoiceInfoList.add(orderInvoiceInfo);
    
            OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
            orderProcessInfo.setId(orderInfo.getProcessId());
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_4.getKey());
            orderProcessInfo.setUpdateTime(new Date());
    
            //静态码开票时间为审核时间
            if (OrderInfoEnum.ORDER_SOURCE_5.getKey().equals(processInfo.getDdly())) {
                orderProcessInfo.setCheckTime(new Date());
            }
            //异常订单重新请求开局后修改编辑状态未编辑状态
            if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(processInfo.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(processInfo.getDdzt())) {
                orderProcessInfo.setEditStatus(ConfigureConstant.STRING_0);
            }
    
            updateProcessInfo.add(orderProcessInfo);
        
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                /**
                 * 可以通过原发票代码号码得到蓝字发票信息,使用原蓝票订单号作为退回单号
                 */
                OrderInvoiceInfo selectInvoiceInfoByFpdhm = apiInvoiceCommonMapperService.selectByYfp(orderInfo.getYfpDm(), orderInfo.getYfpHm(), shList);
            
            
                log.debug("{}根据原蓝票代码号码查询到的orderInvoice：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(selectInvoiceInfoByFpdhm));
                // 如果原始订单查不到数据，使用之前数据的订单号即可
                if (selectInvoiceInfoByFpdhm != null) {
                    //发票冲红后更新原蓝票的冲红标志
                    Double valueOf = Double.valueOf(orderInfo.getKphjje());
                    double hkphjje = Math.abs(valueOf);
                    Double valueOf2 = Double.valueOf(selectInvoiceInfoByFpdhm.getKphjje());
                    double lkphjje = Math.abs(valueOf2);
                    formerBlueInvoiceStatus.setId(selectInvoiceInfoByFpdhm.getId());
                    if (BigDecimal.valueOf(lkphjje).equals(BigDecimal.valueOf(hkphjje))) {
                        formerBlueInvoiceStatus.setChBz(OrderInfoEnum.RED_INVOICE_2.getKey());
                    }
                    if (lkphjje > hkphjje) {
                        //通过原始蓝票得到原始order
                        Double valueOf3 = Double.valueOf(selectInvoiceInfoByFpdhm.getSykchje());
                        if (valueOf3 >= hkphjje) {
                            formerBlueInvoiceStatus.setChBz(OrderInfoEnum.RED_INVOICE_5.getKey());
                        }
                        if (valueOf3 < hkphjje) {
                            log.info("{},部分冲红金额大于剩余可冲红金额", LOGGER_MSG);
                            return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SYKCHJE_LESS.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SYKCHJE_LESS.getMessage());
                        }
                    }
                    formerBlueInvoiceStatus.setKplsh(selectInvoiceInfoByFpdhm.getKplsh());
                }
            }

            OrderInvoiceInfo selectByOrderProcessId = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderInfo.getFpqqlsh(), shList);
            /**
             * 读取数据库数据,判断是否已经存在开票数据,如果存在则进行更新,不存在就重新生成id
             */
            if (selectByOrderProcessId != null) {
                if (OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(selectByOrderProcessId.getKpzt()) ||
                        (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(selectByOrderProcessId.getKpzt()))) {
                    return r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_STATUS_EROR.getKey())
                            .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_STATUS_EROR.getMessage());
                }
            
            }
        
            /**
             * 所有的更新操作统一事务处理 首先保存数据库之后再调用开票接口
             */
        
            try {
                apiInvoiceCommonMapperService.invoiceRequestData(updateProcessInfo, orderInvoiceInfoList, insertBatchRequest, insertBatchItem, updateSpecialInvoiceList, shList);
            
                if (StringUtils.isNotBlank(formerBlueInvoiceStatus.getKplsh()) && StringUtils.isNotBlank(formerBlueInvoiceStatus.getChBz())) {
                    /**
                     * 根据id更新蓝票的冲红标志
                     */
                    formerBlueInvoiceStatus.setUpdateTime(new Date());
                
                    apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(formerBlueInvoiceStatus, shList);
                }
            } catch (OrderReceiveException e) {
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                        .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            }
    
    
            /**
             * 数据放入开票队列
             */
            FpkjMqData fpkjMqData = new FpkjMqData();
            fpkjMqData.setFpqqlsh(orderInfo.getFpqqlsh());
            fpkjMqData.setFpqqpch(fpqqpch);
            fpkjMqData.setKplsh(kplsh);
            fpkjMqData.setNsrsbh(orderInfo.getXhfNsrsbh());
            fpkjMqData.setTerminalCode(terminalCode);
    
    
            //方格开票特殊处理
            if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) ||
                    OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                for (List<InvoiceBatchRequestItem> invoiceBatchRequestItem1 : insertBatchItem) {
                    for (InvoiceBatchRequestItem batchRequestItem : invoiceBatchRequestItem1) {
                        /**
                         *  方格存放开票信息到消息队列
                         *  需要判断方格开票状态,如果是签章失败需要重新走签章
                         */
                        if (OrderInfoEnum.INVOICE_STATUS_3.getKey().equals(orderInvoiceInfo.getKpzt()) && StringUtils.isNotEmpty(orderInvoiceInfo.getFpdm())
                                && StringUtils.isNotEmpty(orderInvoiceInfo.getFphm()) && StringUtils.isEmpty(orderInvoiceInfo.getPdfUrl())) {
        
                            String pdfid = "";
                            //如果是电票开具调底层接口
                            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
                                List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
                                Result genPdf = fangGeInterfaceService.genPdf(orderInvoiceInfo, orderInfo, orderItemInfos, terminalCode);
                                log.info("调用底层获取签章结果,出参{}", genPdf);
                                if (Boolean.TRUE.equals(genPdf.get(ConfigureConstant.PDF_GEN_R))) {
                                    pdfid = String.valueOf(genPdf.get(ConfigureConstant.PDF_GEN_O));
                                    orderInvoiceInfo.setPdfUrl(pdfid);
                                    orderInvoiceInfo.setUpdateTime(new Date());
                                } else {
                                    log.info("调用底层接口，获取签章失败");
                                    //更新订单处理表为开票失败
                                    orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_6.getKey());
                                    orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
                                    orderProcessInfo.setSbyy("签章失败");
                                    //开票失败时修改获取数据状态为失败，重新拉取数据
                                    orderProcessInfo.setFgStatus(ConfigureConstant.STRING_0);
                
                                }
                            }
                            //更新订单发票数据
                            apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
                            //更新订单处理表
                            apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
        
                        } else {
                            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(invoiceBatchRequest.getXhfNsrsbh(), sld);
                            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
    
                            /**
                             * 存放开票信息到redis队列
                             */
                            PushPayload pushPayload = new PushPayload();
                            //发票开具
                            pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);
                            pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                            pushPayload.setJQBH(registrationCode.getJqbh());
                            pushPayload.setZCM(registrationCode.getZcm());
                            pushPayload.setDDQQLSH(batchRequestItem.getFpqqlsh());
                            apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                        }
                    }
                }
            }else{
                String jsonString = JsonUtils.getInstance().toJsonString(fpkjMqData);
                log.info("开票数据信息{}", jsonString);
                openInvoiceService.openAnInvoice(jsonString, orderInfo.getXhfNsrsbh());
            }
        }
    
    
        return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
    }
    
    /**
     * activex 专用  开票完成更新发票信息
     */
    @Override
    public R updateOrderInvoiceInfo(OrderInvoiceInfoRequest invoiceInfo, List<String> shList) {
        //更新处理表
        OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(invoiceInfo.getDdqqlsh(), shList);
        if (ObjectUtils.isEmpty(orderProcessInfo)) {
            return R.error("订单处理信息为空");
        }
        OrderInvoiceInfo info = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderProcessInfo.getFpqqlsh(), shList);
        OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(invoiceInfo.getOrderInfoid(), shList);
        OrderInvoiceInfo orderinvoiceInfo = new OrderInvoiceInfo();
        BeanTransitionUtils.transitionOrderInvoiceInfo(orderinvoiceInfo, orderInfo);
        List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
        List<OrderInvoiceInfo> list = new ArrayList<>();
        if (ConfigureConstant.STRING_0.equals(invoiceInfo.getKpzt())) {
            //开票成功
            /**
             * 发票处理表
             */
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_5.getKey());
            if (ObjectUtils.isEmpty(info)) {
                //判断发票信息是否存在，不存在，添加
                /**
                 * 发票表
                 */
                orderinvoiceInfo.setJqbh(invoiceInfo.getJqbh());
                orderinvoiceInfo.setSld(invoiceInfo.getSld());
                orderinvoiceInfo.setSldMc(invoiceInfo.getSldMc());
                orderinvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
                orderinvoiceInfo.setDdh(invoiceInfo.getDdh());
                orderinvoiceInfo.setJym(invoiceInfo.getJym());
                orderinvoiceInfo.setFwm(invoiceInfo.getFwm());
                orderinvoiceInfo.setEwm(invoiceInfo.getEwm());
                orderinvoiceInfo.setFpdm(invoiceInfo.getFpdm());
                orderinvoiceInfo.setFphm(invoiceInfo.getFphm());
                orderinvoiceInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
                orderinvoiceInfo.setOrderProcessInfoId(invoiceInfo.getProcessId());
                orderinvoiceInfo.setOrderInfoId(invoiceInfo.getOrderInfoid());
                Date date = DateUtils.stringToDate(invoiceInfo.getKprq(), DateUtils.DATE_TIME_PATTERN_NOSPLIT);
                orderinvoiceInfo.setKprq(date);
                orderinvoiceInfo.setFpzlDm(orderProcessInfo.getFpzlDm());
                orderinvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
                orderinvoiceInfo.setKphjje(orderInfo.getKphjje());
                orderinvoiceInfo.setKpse(orderInfo.getHjse());
                orderinvoiceInfo.setKplx(orderInfo.getKplx());

                String generateShotKey = apiInvoiceCommonMapperService.getGenerateShotKey();
                DecimalFormat df = new DecimalFormat("000");
                String format = df.format(1);
                String kplsh = generateShotKey + format;
                orderinvoiceInfo.setKplsh(kplsh);
            
                orderinvoiceInfo.setPushStatus(ConfigureConstant.STRING_0);
                orderinvoiceInfo.setCreateTime(new Date());
                orderinvoiceInfo.setUpdateTime(new Date());
                orderinvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                /**
                 * 清单标志赋值
                 */
                BeanTransitionUtils.getOrderInvoiceInfoQdBz(OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey(), orderInfo, orderItemInfos);
                //红票可冲红金额为0，蓝票为原价税合计
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderinvoiceInfo.getKplx())) {
                    orderinvoiceInfo.setSykchje("0");
                } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderinvoiceInfo.getKplx())) {
                    orderinvoiceInfo.setSykchje(orderInfo.getKphjje());
                }
                //如果是红票的话 更新原蓝票的冲红标志和剩余可冲红金额
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderinvoiceInfo.getKplx())) {
                    //红票
                    apiOrderInvoiceInfoService.dealRedInvoice(orderinvoiceInfo, orderinvoiceInfo.getKpzt(), shList);
                }
                list.add(orderinvoiceInfo);
            } else {//更新
                info.setJym(invoiceInfo.getJym());
                info.setFwm(invoiceInfo.getFwm());
                info.setEwm(invoiceInfo.getEwm());
                info.setFpdm(invoiceInfo.getFpdm());
                info.setFphm(invoiceInfo.getFphm());
                info.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());
                apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(info, shList);
            }
        } else {
            //开票失败
            /**
             * 更新订单处理表
             */
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_6.getKey());
            orderProcessInfo.setSbyy(invoiceInfo.getSbyy());
            /**
             * 更新发票表
             */
            if (ObjectUtils.isEmpty(info)) {
                //发票信息不存在，添加
                orderinvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
                /**
                 * 发票表
                 */
                orderinvoiceInfo.setJqbh(invoiceInfo.getJqbh());
                orderinvoiceInfo.setSld(invoiceInfo.getSld());
                orderinvoiceInfo.setSldMc(invoiceInfo.getSldMc());
                orderinvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
                orderinvoiceInfo.setDdh(invoiceInfo.getDdh());
                orderinvoiceInfo.setJym(invoiceInfo.getJym());
                orderinvoiceInfo.setFwm(invoiceInfo.getFwm());
                orderinvoiceInfo.setEwm(invoiceInfo.getEwm());
                orderinvoiceInfo.setFpdm(invoiceInfo.getFpdm());
                orderinvoiceInfo.setFphm(invoiceInfo.getFphm());
                orderinvoiceInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
                orderinvoiceInfo.setOrderProcessInfoId(invoiceInfo.getProcessId());
                orderinvoiceInfo.setOrderInfoId(invoiceInfo.getOrderInfoid());
                Date date = DateUtils.stringToDate(invoiceInfo.getKprq(), DateUtils.DATE_TIME_PATTERN_NOSPLIT);
                orderinvoiceInfo.setKprq(date);
                orderinvoiceInfo.setFpzlDm(orderProcessInfo.getFpzlDm());
                orderinvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
                orderinvoiceInfo.setKphjje(orderInfo.getKphjje());
                orderinvoiceInfo.setKpse(orderInfo.getHjse());
                orderinvoiceInfo.setKplx(orderInfo.getKplx());

                String generateShotKey = apiInvoiceCommonMapperService.getGenerateShotKey();
                DecimalFormat df = new DecimalFormat("000");
                String format = df.format(1);
                String kplsh = generateShotKey + format;
                orderinvoiceInfo.setKplsh(kplsh);
    
                orderinvoiceInfo.setPushStatus(ConfigureConstant.STRING_0);
                orderinvoiceInfo.setCreateTime(new Date());
                orderinvoiceInfo.setUpdateTime(new Date());
                /**
                 * 清单标志赋值
                 */
                BeanTransitionUtils.getOrderInvoiceInfoQdBz(OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey(), orderInfo, orderItemInfos);
                list.add(orderinvoiceInfo);
            }else{//修改
                info.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
                info.setUpdateTime(new Date());
                apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(info, shList);
            }

        }

        //更新订单发票数据
        if(CollectionUtils.isNotEmpty(list)){
            int i = apiOrderInvoiceInfoService.insertByList(list);
            if(i<0){
                return R.error("更新订单发票数据失败");
            }
        }
        int i1 = apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
        if(i1<0){
            return R.error("更新发票处理表失败");
        }

        return R.ok();
    }
    public R buildMakeOutInvoiceResult(R r, String fpzldm, boolean success) {
        if (success) {
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm)) {
                r.put("dpSuccessCount", Integer.parseInt(r.get("dpSuccessCount").toString()) + 1);
                r.put("dpFailCount", Integer.parseInt(r.get("dpFailCount").toString()) - 1);
            } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm)) {
                r.put("ppSuccessCount", Integer.parseInt(r.get("ppSuccessCount").toString()) + 1);
                r.put("ppFailCount", Integer.parseInt(r.get("ppFailCount").toString()) - 1);
            } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm)) {
                r.put("zpSuccessCount", Integer.parseInt(r.get("zpSuccessCount").toString()) + 1);
                r.put("zpFailCount", Integer.parseInt(r.get("zpFailCount").toString()) - 1);
            }
        }
        return r;
    }
}


