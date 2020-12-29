package com.dxhy.order.consumer.modules.scaninvoice.service.impl;


import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.modules.order.service.MyinvoiceRequestService;
import com.dxhy.order.consumer.modules.scaninvoice.constant.ScanInvoiceEnum;
import com.dxhy.order.consumer.modules.scaninvoice.model.PageQrcodeOrderInfo;
import com.dxhy.order.consumer.modules.scaninvoice.model.TitleInfo;
import com.dxhy.order.consumer.modules.scaninvoice.service.ScanInvoiceService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.PageDataDealUtil;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import com.itextpdf.text.P;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author ：杨士勇
 * @ClassName ：ScanInvoiceServiceImpl
 * @Description ：
 * @date ：2020年4月1日 上午11:46:03
 */
@Slf4j
@Service
public class ScanInvoiceServiceImpl implements ScanInvoiceService {


    @Reference
    ApiQuickCodeInfoService apiQuickCodeInfoService;

    @Reference
    ApiOrderItemInfoService apiOrderItemInfoService;

    @Resource
    MyinvoiceRequestService myinvoiceRequestService;

    @Reference
    ApiInvoiceCommonService apiInvoiceCommonService;

    @Reference
    ApiOrderInfoService apiOrderInfoService;

    @Reference
    RedisService redisService;

    @Resource
    InvoiceService invoiceService;

    @Resource
    ICommonInterfaceService iCommonInterfaceService;

    @Reference
    ApiInvoiceCommonService apiInvoiceCommonMapperService;

    @Reference
    ApiOrderQrcodeExtendService apiOrderQrcodeExtendService;

    @Reference
    ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;

    @Reference
    ApiInsertCardService apiInsertCardService;

    @Reference
    ApiTaxEquipmentService apiTaxEquipmentService;

    @Reference
    ApiOrderProcessService apiOrderProcessService;


    @Override
    public R queryOrderInfoByTqmAndNsrsbh(String tqm, List<String> shList, String type, String openId) {

        R r = new R();
        TitleInfo titleInfo = new TitleInfo();

        if (OrderInfoEnum.QR_TYPE_1.getKey().equals(type)) {
            OrderQrcodeExtendInfo queryQrCodeDetailByTqm = apiOrderQrcodeExtendService.queryQrCodeDetailByTqm(tqm, shList, type);

            if (queryQrCodeDetailByTqm == null) {
                return r.put(OrderManagementConstant.CODE, "9999").put(OrderManagementConstant.MESSAGE, "提取码不存在");
            }

            // 判断二维码的开具 插卡状态 如果发票开具失败 提示用户发票开具失败 如果是插卡失败 将数据放入队列重新插卡
            if (ConfigureConstant.STRING_1.equals(queryQrCodeDetailByTqm.getEwmzt())) {
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(queryQrCodeDetailByTqm.getFpqqlsh(), shList);
                //开票成功 插卡失败的订单重新放入队列插卡
                if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt()) && ConfigureConstant.STRING_2.equals(queryQrCodeDetailByTqm.getCardStatus())) {
                    InvoicePush invoicePush = new InvoicePush();
                    invoicePush.setFPQQLSH(orderInvoiceInfo.getFpqqlsh());
                    invoicePush.setKPLSH(orderInvoiceInfo.getKplsh());
                    invoicePush.setJQBH(orderInvoiceInfo.getJqbh());
                    invoicePush.setDDH(orderInvoiceInfo.getDdh());
                    invoicePush.setJYM(orderInvoiceInfo.getJym());
                    invoicePush.setFP_DM(orderInvoiceInfo.getFpdm());
                    invoicePush.setFP_HM(orderInvoiceInfo.getFphm());
                    invoicePush.setKPRQ(orderInvoiceInfo.getKprq());
                    invoicePush.setFPLX(orderInvoiceInfo.getKplx());
                    invoicePush.setNSRSBH(orderInvoiceInfo.getXhfNsrsbh());
                    invoicePush.setSLDID(orderInvoiceInfo.getSld());
                    invoicePush.setSLDMC(orderInvoiceInfo.getSldMc());
                    invoicePush.setFJH(orderInvoiceInfo.getFjh());
                    apiInsertCardService.sendToInsertCardQueue(invoicePush);
                }

            } else {
                // 动态码 判断二维码是否已失效
                if (ConfigureConstant.STRING_1.equals(queryQrCodeDetailByTqm.getZfzt())) {

                    return R.error().put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE,
                            "二维码已失效");
                }

                Date validDate = queryQrCodeDetailByTqm.getQuickResponseCodeValidTime();
                if (new Date().after(validDate)) {

                    return R.error().put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE,
                            "二维码已失效");
                }
            }

            List<Map<String, String>> resultList = new ArrayList<>();

            if (StringUtils.isBlank(queryQrCodeDetailByTqm.getFpzlDm())) {
                Map<String, Object> paramMap = new HashMap<>(5);
                paramMap.put("xhfNsrsbh", queryQrCodeDetailByTqm.getXhfNsrsbh());
                EwmConfigInfo queryEwmConfigInfo = apiQuickCodeInfoService.queryEwmConfigInfo(paramMap);
                if (queryEwmConfigInfo != null) {
                    List<EwmConfigItemInfo> queryEwmConfigItemInfoById = apiQuickCodeInfoService
                            .queryEwmConfigItemInfoById(queryEwmConfigInfo.getId());

                    if (CollectionUtils.isNotEmpty(queryEwmConfigItemInfoById)) {
                        if (CollectionUtils.isNotEmpty(queryEwmConfigItemInfoById)) {
                            for (EwmConfigItemInfo ewmConfigItemInfo : queryEwmConfigItemInfoById) {
                                Map<String, String> resultMap = getResultMap(ewmConfigItemInfo.getFpzldm());
                                resultList.add(resultMap);
                            }
                        }
                    } else {
                        Map<String, String> resultMap = new HashMap<>(5);
                        resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
                        resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue());
                        resultList.add(resultMap);
                    }
                } else {
                    Map<String, String> resultMap = new HashMap<>(5);
                    resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
                    resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue());
                    resultList.add(resultMap);
                }

            } else {
                Map<String, String> resultMap = getResultMap(queryQrCodeDetailByTqm.getFpzlDm());
                resultList.add(resultMap);
            }
            List<OrderItemInfo> selectOrderItemInfoByOrderId = apiOrderItemInfoService
                    .selectOrderItemInfoByOrderId(queryQrCodeDetailByTqm.getOrderInfoId(), shList);

            OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(queryQrCodeDetailByTqm.getOrderInfoId(), shList);
            //判断生成的二维码是否存在购方信息
            if (StringUtils.isNotEmpty(orderInfo.getGhfMc()) || StringUtils.isNotEmpty(orderInfo.getGhfNsrsbh())
                    || StringUtils.isNotEmpty(orderInfo.getGhfSj())
                    || StringUtils.isNotEmpty(orderInfo.getGhfEmail())
                    || StringUtils.isNotEmpty(orderInfo.getGhfDz())
                    || StringUtils.isNotEmpty(orderInfo.getGhfDh())
                    || StringUtils.isNotEmpty(orderInfo.getGhfYh())
                    || StringUtils.isNotEmpty(orderInfo.getGhfZh())) {
                titleInfo.setGfmc(orderInfo.getGhfMc());
                titleInfo.setGfsh(orderInfo.getGhfNsrsbh());
                titleInfo.setGfsj(orderInfo.getGhfSj());
                titleInfo.setGfyx(orderInfo.getGhfEmail());
                titleInfo.setGfdz(orderInfo.getGhfDz());
                titleInfo.setGfdh(orderInfo.getGhfDh());
                titleInfo.setGfyh(orderInfo.getGhfYh());
                titleInfo.setGfzh(orderInfo.getGhfZh());
            } else {
                String s = redisService.get(String.format(Constant.REDIS_EWM_TITLE, openId));
                if (StringUtils.isNotBlank(s)) {
                    titleInfo = JsonUtils.getInstance().parseObject(s, TitleInfo.class);
                }
            }

            if (StringUtils.isNotBlank(orderInfo.getBz())) {
                titleInfo.setBz(orderInfo.getBz());
            }

            r.put("orderInfo", queryQrCodeDetailByTqm);
            r.put("orderItemList", selectOrderItemInfoByOrderId);
            r.put("fpzls", resultList);
            r.put("titleInfo", titleInfo);
        } else {
            QuickResponseCodeInfo queryQrCodeDetailByTqm = apiQuickCodeInfoService.queryQrCodeDetailByTqm(tqm, shList,
                    type);
            if (queryQrCodeDetailByTqm != null) {
    
                if (OrderInfoEnum.QUICK_RESPONSE_CODE_STATUS_1.getKey().equals(queryQrCodeDetailByTqm.getEwmzt())) {
                    log.warn("静态码已删除,tqm:{},shList:{}", tqm, shList);
                    return r.put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE, "提取码不存在");
                }
                // 查询用户的发票种类代码配置
                List<Map<String, String>> resultList = new ArrayList<>();

                List<InvoiceTypeCodeExt> queryInvoiceTypeByQrcodeId = apiQuickCodeInfoService
                        .queryInvoiceTypeByQrcodeId(queryQrCodeDetailByTqm.getId(), shList);
                if (CollectionUtils.isNotEmpty(queryInvoiceTypeByQrcodeId)) {

                    for (InvoiceTypeCodeExt invoiceTypeCodeExt : queryInvoiceTypeByQrcodeId) {
                        Map<String, String> resultMap = getResultMap(invoiceTypeCodeExt.getFpzlDm());
                        resultList.add(resultMap);
                    }
                }


                List<QuickResponseCodeItemInfo> queryQrCodeItemListByQrcodeId = apiQuickCodeInfoService
                        .queryQrCodeItemListByQrcodeId(queryQrCodeDetailByTqm.getId(), shList);

                //获取缓存的抬头信息
                String s = redisService.get(String.format(Constant.REDIS_EWM_TITLE, openId));
                if (StringUtils.isNotBlank(s)) {
                    titleInfo = JsonUtils.getInstance().parseObject(s, TitleInfo.class);
                }

                r.put("orderInfo", queryQrCodeDetailByTqm);
                r.put("orderItemList", queryQrCodeItemListByQrcodeId);
                r.put("fpzls", resultList);
                r.put("titleInfo", titleInfo);
            } else {
                return r.put(OrderManagementConstant.CODE, "9999").put(OrderManagementConstant.MESSAGE, "提取码不存在");
            }
        }
        return r;
    }


    /**
     * @param @param fpzldm
     * @return void
     * @throws
     * @Title : getResultMap
     * @Description ：
     */

    private Map<String, String> getResultMap(String fpzldm) {

        Map<String, String> resultMap = new HashMap<>(5);
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm)) {
            resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
            resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_51.getValue());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm)) {
            resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
            resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_0.getValue());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm)) {
            resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
            resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_2.getValue());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey().equals(fpzldm)) {
            resultMap.put("key", OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey());
            resultMap.put("value", OrderInfoEnum.ORDER_INVOICE_TYPE_41.getValue());
        }
        return resultMap;
    }


    @Override
    public R updateOrderInfo(PageQrcodeOrderInfo pageQrcodeOrderInfo) {

        List<String> shList = new ArrayList<>();
        shList.add(pageQrcodeOrderInfo.getXhfNsrsbh());
        R r = R.ok();
        if (OrderInfoEnum.QR_TYPE_0.getKey().equals(pageQrcodeOrderInfo.getType())) {



        } else {

            OrderQrcodeExtendInfo queryQrCodeDetailByTqm = apiOrderQrcodeExtendService.queryQrCodeDetailByTqm(pageQrcodeOrderInfo.getTqm(), shList, pageQrcodeOrderInfo.getType());
            String orderId = queryQrCodeDetailByTqm.getOrderInfoId();

            //更新购方信息 订单金额
            CommonOrderInfo pageToFpkjInfo = PageDataDealUtil.pageToFpkjInfo(pageQrcodeOrderInfo);
            pageToFpkjInfo.getOrderInfo().setId(orderId);
            boolean b = apiQuickCodeInfoService.updateGhfInfo(pageToFpkjInfo, shList);
            if (!b) {
                return R.error().put(OrderManagementConstant.MESSAGE, "扫码开票异常!");
            }
            r.put("authOrderId", orderId);

        }
        //redis缓存用户上次的抬头信息
        TitleInfo titleInfo = new TitleInfo();
        titleInfo.setGfmc(pageQrcodeOrderInfo.getGhfMc());
        titleInfo.setGfsh(pageQrcodeOrderInfo.getGhfNsrsbh());
        titleInfo.setGfdz(pageQrcodeOrderInfo.getGhfDz());
        titleInfo.setGfdh(pageQrcodeOrderInfo.getGhfDh());
        titleInfo.setGfyh(pageQrcodeOrderInfo.getGhfYh());
        titleInfo.setGfzh(pageQrcodeOrderInfo.getGhfZh());
        titleInfo.setGfsj(pageQrcodeOrderInfo.getGhfSj());
        titleInfo.setGfyx(pageQrcodeOrderInfo.getGhfEmail());
        titleInfo.setGhfqylx(pageQrcodeOrderInfo.getGhfqylx());

        redisService.set(String.format(Constant.REDIS_EWM_TITLE, pageQrcodeOrderInfo.getOpenId()), JsonUtils.getInstance().toJsonString(titleInfo));
        return r;
    }


    @Override
    public R authOrderInvoice(String succOrderId) {
        String uuid = UUID.randomUUID().toString();
        //todo 授权这里需要判断下怎么做分库分表
        try {
            //add by ysy 此处添加redis同步锁 过滤重复数据

            if (!redisService.setNx(String.format(Constant.REDIS_EWM_SYN_LOCK,succOrderId), uuid)) {
                return R.error().put(OrderManagementConstant.MESSAGE, "重复推送");
            } else {
                redisService.expire(String.format(Constant.REDIS_EWM_SYN_LOCK,succOrderId), 300);
            }

            if (succOrderId.startsWith(ScanInvoiceEnum.INVOICE_SCAN_TYPE_01.getKey())) {
                log.info("接受微信授权推送，静态码开票，订单号:{}", succOrderId);
                String string = redisService.get(String.format(Constant.REDIS_EWM_STATIC, succOrderId));
                if (StringUtils.isNotBlank(string)) {
                    PageQrcodeOrderInfo parseObject = JsonUtils.getInstance().parseObject(string, PageQrcodeOrderInfo.class);
                    CommonOrderInfo pageToFpkjInfo = PageDataDealUtil.pageToFpkjInfo(parseObject);
                    List<String> shList = new ArrayList<>();
                    shList.add(parseObject.getXhfNsrsbh());

                    //判断数据是否已存在 已存在的数据不在处理
                    OrderQrcodeExtendInfo query = apiOrderQrcodeExtendService.queryQrCodeDetailByAuthOrderId(succOrderId,shList);
                    if(query != null) {
                        log.info("微信授权事件重复推送!,授权订单号:{}",succOrderId);

                    }else{
                        //数据价税分离
                        try {
                            pageToFpkjInfo = PriceTaxSeparationUtil.taxSeparationService(pageToFpkjInfo, new TaxSeparateConfig());
                        } catch (OrderSeparationException e) {
                            log.error("静态码开票数据价税分离失败,异常原因：{}", e);
                        }

                        OrderProcessInfo processInfo = new OrderProcessInfo();
                        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
                        iCommonInterfaceService.buildInsertOrderData(pageToFpkjInfo.getOrderInfo(), pageToFpkjInfo.getOrderItemInfo(), processInfo, orderInvoiceInfo);

                        //设置开票方式
                        processInfo.setKpfs(OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey());
                        processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_5.getKey());
                        processInfo.setYwlx(pageToFpkjInfo.getOrderInfo().getYwlx());
                        processInfo.setYwlxId(pageToFpkjInfo.getOrderInfo().getYwlxId());

                        //创建原始订单信息
                        OrderOriginExtendInfo orderOrginOrder = buildOriginOrder(pageToFpkjInfo);


                        OrderQrcodeExtendInfo orderQrcodeExtendInfo = buildOrderQrcodeInfo(pageToFpkjInfo);
                        orderQrcodeExtendInfo.setOpenId(parseObject.getOpenId());
                        orderQrcodeExtendInfo.setUnionId(parseObject.getUnionId());
                        orderQrcodeExtendInfo.setAuthOrderId(succOrderId);
                        orderQrcodeExtendInfo.setQuickResponseCodeType(OrderInfoEnum.QR_TYPE_0.getKey());
                        orderQrcodeExtendInfo.setOrderInfoId(pageToFpkjInfo.getOrderInfo().getId());

                        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
                        List<OrderInfo> orderInfoList = new ArrayList<>();
                        List<OrderProcessInfo> orderProcessInfoList = new ArrayList<>();
                        List<List<OrderItemInfo>> orderItemInfoList = new ArrayList<>();
                        List<OrderQrcodeExtendInfo> qrcodeInfoList = new ArrayList<>();

                        orderOriginList.add(orderOrginOrder);
                        orderInfoList.add(pageToFpkjInfo.getOrderInfo());
                        orderItemInfoList.add(pageToFpkjInfo.getOrderItemInfo());
                        orderProcessInfoList.add(processInfo);
                        qrcodeInfoList.add(orderQrcodeExtendInfo);
                        apiInvoiceCommonMapperService.saveData(orderInfoList, orderItemInfoList, orderProcessInfoList, null,
                                null,qrcodeInfoList,orderOriginList, shList);

                    }

                } else {
                    log.error("静态码开票,订单信息不存在,授权订单号:{}", succOrderId);

                }

            } else if (succOrderId.startsWith(ScanInvoiceEnum.INVOICE_SCAN_TYPE_02.getKey())) {
                log.info("接受微信授权推送，动态码开票，订单号:{}", succOrderId);
                /**
                 * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
                 *
                 */
                List<String> shList = null;
                String cacheAuthId = String.format(Constant.REDIS_AUTHID, succOrderId);
                String xhfNsrsbh = redisService.get(cacheAuthId);
                if (StringUtils.isBlank(xhfNsrsbh)) {
                    OrderQrcodeExtendInfo quickResponse = apiOrderQrcodeExtendService.queryQrCodeDetailByAuthOrderId(succOrderId, null);
                    if (quickResponse != null && StringUtils.isNotBlank(quickResponse.getXhfNsrsbh())) {

                        redisService.set(cacheAuthId, quickResponse.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        xhfNsrsbh = quickResponse.getXhfNsrsbh();
                    }
                }
                shList = NsrsbhUtils.transShListByNsrsbh(xhfNsrsbh);
                //根据授权id查询订单信息
                OrderQrcodeExtendInfo quickResponse = apiOrderQrcodeExtendService.queryQrCodeDetailByAuthOrderId(succOrderId, shList);
                if (quickResponse == null) {
                    log.error("动态码开票，订单信息不存在，授权订单号:{}", succOrderId);

                } else {
                    //已作废
                    if (StringUtils.isNotBlank(quickResponse.getZfzt()) && ConfigureConstant.STRING_1.equals(quickResponse.getZfzt())) {

                        //二维码已作废
                        log.error("二维码已作废，tqm:{}", quickResponse.getTqm());
                        return R.error().put(OrderManagementConstant.MESSAGE, "二维码已作废!");
                    }


                    OrderInfo selectOrderInfoByOrderId = apiOrderInfoService.selectOrderInfoByOrderId(quickResponse.getOrderInfoId(), shList);

                    Map<String, Object> paramMap = new HashMap<>(5);
                    paramMap.put("xhfNsrsbh", selectOrderInfoByOrderId.getXhfNsrsbh());

                    Map<String, PageSld> sldMap = new HashMap<>(5);
                    EwmConfigInfo queryEwmConfigInfo = apiQuickCodeInfoService.queryEwmConfigInfo(paramMap);
                    if (queryEwmConfigInfo != null) {
                        List<EwmConfigItemInfo> queryEwmConfigItemInfoById = apiQuickCodeInfoService.queryEwmConfigItemInfoById(queryEwmConfigInfo.getId());
                        if (CollectionUtils.isNotEmpty(queryEwmConfigItemInfoById)) {
                            for (EwmConfigItemInfo itemInfo : queryEwmConfigItemInfoById) {

                                if (selectOrderInfoByOrderId.getFpzlDm().equals(itemInfo.getFpzldm())) {
                                    PageSld sld = new PageSld();
                                    sld.setSldid(itemInfo.getSld());
                                    sld.setSldmc(itemInfo.getSldMc());
                                    sldMap.put("_" + selectOrderInfoByOrderId.getFpzlDm(), sld);
                                }
                            }
                        }
                    }

                    String terminalCode =apiTaxEquipmentService.getTerminalCode(selectOrderInfoByOrderId.getXhfNsrsbh());

                    if (!OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(selectOrderInfoByOrderId.getFpzlDm())) {
                        if (sldMap.isEmpty()) {
                            //没有设置受理点 轮询受理点
                            R result = iCommonInterfaceService.dealWithSldStartV3("", selectOrderInfoByOrderId.getFpzlDm(), selectOrderInfoByOrderId.getXhfNsrsbh(), selectOrderInfoByOrderId.getQdBz(), terminalCode);
                            log.debug("受理点查询成功!");
                            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
                                return R.error().put(OrderManagementConstant.MESSAGE, "无可用受理点");
                            } else {
                                PageSld sld = new PageSld();
                                sld.setSldid(String.valueOf(result.get("sldid")));
                                sld.setSldmc(String.valueOf(result.get("sldmc")));
                                sldMap.put("_" + selectOrderInfoByOrderId.getFpzlDm(), sld);
                            }

                        }
                    } else {
                        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                            R result = iCommonInterfaceService.dealWithSldStartV3("", selectOrderInfoByOrderId.getFpzlDm(), selectOrderInfoByOrderId.getXhfNsrsbh(), selectOrderInfoByOrderId.getQdBz(), terminalCode);
                            log.debug("受理点查询成功!");
                            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
                                return R.error().put(OrderManagementConstant.MESSAGE, "无可用受理点");
                            } else {
                                PageSld sld = new PageSld();
                                sld.setSldid(String.valueOf(result.get("sldid")));
                                sld.setSldmc(String.valueOf(result.get("sldmc")));
                                paramMap.put("_" + selectOrderInfoByOrderId.getFpzlDm(), sld);
                            }
                        }
                    }
                    R dynamciInvoiceByOrderId = invoiceService.dynamciInvoiceByOrderId(selectOrderInfoByOrderId.getId(), sldMap, shList);
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(dynamciInvoiceByOrderId.get(OrderManagementConstant.CODE))) {
                        log.error("动态码开票，发票开具失败，失败原因:{}", dynamciInvoiceByOrderId.get(OrderManagementConstant.MESSAGE));
                    }

                }
            }
        } catch (Exception e) {
            log.error("接收微信授权推送异常:{}", e);
            return R.error();
        } finally {

            if(uuid.equals(redisService.get(String.format(Constant.REDIS_EWM_SYN_LOCK,succOrderId)))){
                redisService.del(String.format(Constant.REDIS_EWM_SYN_LOCK,succOrderId));

            }

        }
        return R.ok();
    }

    private OrderQrcodeExtendInfo buildOrderQrcodeInfo(CommonOrderInfo pageToFpkjInfo) {
    
    
        Date now = new Date();
        OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
        orderQrcodeExtendInfo.setCreateTime(now);
        orderQrcodeExtendInfo.setFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderQrcodeExtendInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderQrcodeExtendInfo.setQuickResponseCodeType(OrderInfoEnum.QR_TYPE_1.getKey());
        orderQrcodeExtendInfo.setTqm(pageToFpkjInfo.getOrderInfo().getTqm());
        orderQrcodeExtendInfo.setUpdateTime(now);
        orderQrcodeExtendInfo.setXhfMc(pageToFpkjInfo.getOrderInfo().getXhfMc());
        orderQrcodeExtendInfo.setXhfNsrsbh(pageToFpkjInfo.getOrderInfo().getXhfNsrsbh());
        orderQrcodeExtendInfo.setKphjje(pageToFpkjInfo.getOrderInfo().getKphjje());
        orderQrcodeExtendInfo.setDdh(pageToFpkjInfo.getOrderInfo().getDdh());
        orderQrcodeExtendInfo.setEwmzt("0");
    
        orderQrcodeExtendInfo.setFpzlDm(pageToFpkjInfo.getOrderInfo().getFpzlDm());
        orderQrcodeExtendInfo.setZfzt("0");
        orderQrcodeExtendInfo.setCardStatus("0");
        orderQrcodeExtendInfo.setDataStatus("0");
        return orderQrcodeExtendInfo;
    
    
    }

    private OrderOriginExtendInfo buildOriginOrder(CommonOrderInfo pageToFpkjInfo) {

        OrderOriginExtendInfo orderOrginOrder = new OrderOriginExtendInfo();
        orderOrginOrder.setCreateTime(new Date());
        orderOrginOrder.setUpdateTime(new Date());
        orderOrginOrder.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
        orderOrginOrder.setOrderId(pageToFpkjInfo.getOrderInfo().getId());
        orderOrginOrder.setFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderOrginOrder.setOriginFpqqlsh(pageToFpkjInfo.getOrderInfo().getFpqqlsh());
        orderOrginOrder.setOriginOrderId(pageToFpkjInfo.getOrderInfo().getId());
        orderOrginOrder.setOriginDdh(pageToFpkjInfo.getOrderInfo().getDdh());
        orderOrginOrder.setXhfNsrsbh(pageToFpkjInfo.getOrderInfo().getXhfNsrsbh());
        return orderOrginOrder;
    }

    @Override
    public R updateFaildOrder(String failOrderId) {
        if (failOrderId.startsWith(Constant.REDIS_EWM_STATIC)) {
            log.info("接受微信授权推送，静态码开票，订单号:{}", failOrderId);

        } else if (failOrderId.startsWith(Constant.REDIS_EWM_DYNAMIC)) {
            log.info("接受微信授权推送，动态码开票，订单号:{}", failOrderId);

        }

        return R.ok();

    }
    
    @Override
    public R getEwmGzhConfig(String tqm, String nsrsbh, String type) {
        
        return R.ok().put(OrderManagementConstant.DATA, apiQuickCodeInfoService.queryGzhEwmConfig(nsrsbh));
        
    }

    @Override
    public R getAuthUrlAndUpdateOrderInfo(PageQrcodeOrderInfo pageQrcodeOrderInfo) {

        if (OrderInfoEnum.QR_TYPE_1.getKey().equals(pageQrcodeOrderInfo.getType())) {
            List<String> shList = new ArrayList<>();
            shList.add(pageQrcodeOrderInfo.getXhfNsrsbh());
            OrderQrcodeExtendInfo queryQrCodeDetailByTqm = apiOrderQrcodeExtendService.queryQrCodeDetailByTqm(pageQrcodeOrderInfo.getTqm(), shList, pageQrcodeOrderInfo.getType());

            if (queryQrCodeDetailByTqm == null) {
                return R.error().put(OrderManagementConstant.MESSAGE, "二维码信息不存在!");
            }
            String authoOrderId = "";
            if (StringUtils.isBlank(queryQrCodeDetailByTqm.getAuthOrderId())) {

                authoOrderId = ScanInvoiceEnum.INVOICE_SCAN_TYPE_02.getKey() + apiInvoiceCommonService.getGenerateShotKey();

                /**
                 * todo 满足mycat临时使用的缓存,后期优化
                 * 添加授权id与销方税号对应关系
                 *
                 */
                String cacheAuthId = String.format(Constant.REDIS_AUTHID, authoOrderId);
                if (StringUtils.isBlank(redisService.get(cacheAuthId))) {
                    redisService.set(cacheAuthId, pageQrcodeOrderInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                }
                //更新购方信息
            } else {

                //已经受过权 判断是否是本人操作 如果是本人操作 返回授权url 如果非本人操作直接跳转发票已领取页面
                if(StringUtils.isNotBlank(queryQrCodeDetailByTqm.getOpenId()) && !queryQrCodeDetailByTqm.getOpenId().equals(pageQrcodeOrderInfo.getOpenId())){
                    //授权过并且非本人操作

                    return R.error().put(OrderManagementConstant.CODE,ScanInvoiceEnum.HAVEN_GENERATE.getKey())
                            .put(OrderManagementConstant.MESSAGE,ScanInvoiceEnum.HAVEN_GENERATE.getValue());

                }else{
                    authoOrderId = queryQrCodeDetailByTqm.getAuthOrderId();

                }

            }
            //获取授权url
            Map<String, Object> authUrlFromWxService = myinvoiceRequestService.getAuthUrlFromWxService(authoOrderId, pageQrcodeOrderInfo.getKphjje(), "",
                    String.valueOf(System.currentTimeMillis() / 1000), pageQrcodeOrderInfo.getAppid());
            if (!ConfigureConstant.STRING_0.equals(String.valueOf(authUrlFromWxService.get(ConfigureConstant.STRING_ERRCODE)))) {
                log.error("获取授权url失败!");
                return R.error().put(OrderManagementConstant.MESSAGE, "获取授权url失败!");
            }

            //如果是首次获取授权url 更新授权订单号 和 购方信息
            if(StringUtils.isBlank(queryQrCodeDetailByTqm.getAuthOrderId())){


                CommonOrderInfo pageToFpkjInfo = PageDataDealUtil.pageToFpkjInfo(pageQrcodeOrderInfo);

                pageToFpkjInfo.getOrderInfo().setId(queryQrCodeDetailByTqm.getOrderInfoId());

                OrderQrcodeExtendInfo qrcodeInfo = new OrderQrcodeExtendInfo();
                qrcodeInfo.setId(queryQrCodeDetailByTqm.getId());
                qrcodeInfo.setAuthOrderId(authoOrderId);
                qrcodeInfo.setOpenId(pageQrcodeOrderInfo.getOpenId());
                qrcodeInfo.setUnionId(pageQrcodeOrderInfo.getUnionId());
                qrcodeInfo.setXhfNsrsbh(pageQrcodeOrderInfo.getXhfNsrsbh());
                boolean updateEwmDetailInfo = apiOrderQrcodeExtendService.updateEwmDetailInfo(qrcodeInfo, shList,pageToFpkjInfo);
                if(!updateEwmDetailInfo){
                    return R.error().put(OrderManagementConstant.MESSAGE, "更新购方信息失败!");

                }

                //更新购方信息到订单中
                pageToFpkjInfo.getOrderInfo().setId(queryQrCodeDetailByTqm.getOrderInfoId());
                boolean b = apiQuickCodeInfoService.updateGhfInfo(pageToFpkjInfo, shList);
                if (!b) {
                    return R.error().put(OrderManagementConstant.MESSAGE, "扫码开票异常!");
                }
                //缓存抬头信息
                TitleInfo titleInfo = new TitleInfo();
                titleInfo.setGfmc(pageQrcodeOrderInfo.getGhfMc());
                titleInfo.setGfsh(pageQrcodeOrderInfo.getGhfNsrsbh());
                titleInfo.setGfdz(pageQrcodeOrderInfo.getGhfDz());
                titleInfo.setGfdh(pageQrcodeOrderInfo.getGhfDh());
                titleInfo.setGfyh(pageQrcodeOrderInfo.getGhfYh());
                titleInfo.setGfzh(pageQrcodeOrderInfo.getGhfZh());
                titleInfo.setGfsj(pageQrcodeOrderInfo.getGhfSj());
                titleInfo.setGfyx(pageQrcodeOrderInfo.getGhfEmail());
                titleInfo.setGhfqylx(pageQrcodeOrderInfo.getGhfqylx());
                redisService.set(String.format(Constant.REDIS_EWM_TITLE, pageQrcodeOrderInfo.getOpenId()), JsonUtils.getInstance().toJsonString(titleInfo));
            }

            return R.ok().put("data", authUrlFromWxService);

        } else {
            // 静态码订单缓存到redis
            String authoOrderId = ScanInvoiceEnum.INVOICE_SCAN_TYPE_01.getKey() + apiInvoiceCommonService.getGenerateShotKey();
            Map<String, Object> authUrlFromWxService = myinvoiceRequestService.getAuthUrlFromWxService(authoOrderId, pageQrcodeOrderInfo.getKphjje(), "",
                    String.valueOf(System.currentTimeMillis() / 1000), pageQrcodeOrderInfo.getAppid());
            if (!ConfigureConstant.STRING_0.equals(String.valueOf(authUrlFromWxService.get(ConfigureConstant.STRING_ERRCODE)))) {
                log.error("获取授权url失败!");
                return R.error().put(OrderManagementConstant.MESSAGE, "获取授权url失败!");
            }
    
    
            log.info("静态码开票存储的key值：{}", authoOrderId);
            redisService.set(String.format(Constant.REDIS_EWM_STATIC, authoOrderId), JsonUtils.getInstance().toJsonString(pageQrcodeOrderInfo), Constant.REDIS_EXPIRE_TIME_DEFAULT);

            /**
             * todo 满足mycat临时使用的缓存,后期优化
             * 添加授权id与销方税号对应关系
             *
             */
            String cacheAuthId = String.format(Constant.REDIS_AUTHID, authoOrderId);
            if (StringUtils.isBlank(redisService.get(cacheAuthId))) {
                redisService.set(cacheAuthId, pageQrcodeOrderInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
            }
            //缓存抬头信息
            TitleInfo titleInfo = new TitleInfo();
            titleInfo.setGfmc(pageQrcodeOrderInfo.getGhfMc());
            titleInfo.setGfsh(pageQrcodeOrderInfo.getGhfNsrsbh());
            titleInfo.setGfdz(pageQrcodeOrderInfo.getGhfDz());
            titleInfo.setGfdh(pageQrcodeOrderInfo.getGhfDh());
            titleInfo.setGfyh(pageQrcodeOrderInfo.getGhfYh());
            titleInfo.setGfzh(pageQrcodeOrderInfo.getGhfZh());
            titleInfo.setGfsj(pageQrcodeOrderInfo.getGhfSj());
            titleInfo.setGfyx(pageQrcodeOrderInfo.getGhfEmail());
            titleInfo.setGhfqylx(pageQrcodeOrderInfo.getGhfqylx());
            redisService.set(String.format(Constant.REDIS_EWM_TITLE, pageQrcodeOrderInfo.getOpenId()), JsonUtils.getInstance().toJsonString(titleInfo));

            return R.ok().put("data", authUrlFromWxService);

        }
    }

    @Override
    public R queryOrderInfoByTqm(String tqm, List<String> shList) {

        OrderQrcodeExtendInfo orderQrcodeExtendInfo = apiOrderQrcodeExtendService.queryQrCodeDetailByTqm(tqm, shList, OrderInfoEnum.QR_TYPE_1.getKey());

        //校验二维码是否失效
        R r = checkQrcodeIsValid(orderQrcodeExtendInfo);
        if(!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))){
            return r;
        }

        List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderQrcodeExtendInfo.getOrderInfoId(), shList);
        return R.ok().put("orderInfo",orderQrcodeExtendInfo).put("orderItemList",orderItemInfos);
    }

    @Override
    public R getMergeOrderAuthUrl(List<String> lshList, List<String> shList) {


        for(String fpqqlsh : lshList){

            OrderQrcodeExtendInfo orderQrcodeExtendInfo = apiOrderQrcodeExtendService.queryQrCodeDetailByDdqqlshAndNsrsbh(fpqqlsh, shList);


            //校验发票是否有效
            R r = checkQrcodeIsValid(orderQrcodeExtendInfo);
            if(!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))){
                return r;
            }

        }
        return R.ok();
    }


    private R checkQrcodeIsValid(OrderQrcodeExtendInfo orderQrcodeExtendInfo) {

        if(orderQrcodeExtendInfo == null) {

            log.warn("提取码不存在!");
            return R.error().put(OrderManagementConstant.MESSAGE, "提取码不存在!");
        }else if(ConfigureConstant.STRING_1.equals(orderQrcodeExtendInfo.getEwmzt())){

            log.warn("二维码已使用");
            return R.error().put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE,
                    "二维码已失效");
        }else if(ConfigureConstant.STRING_1.equals(orderQrcodeExtendInfo.getZfzt())){

            return R.error().put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE,
                    "二维码已失效");
        }

        Date validDate = orderQrcodeExtendInfo.getQuickResponseCodeValidTime();
        if (new Date().after(validDate)) {

            return R.error().put(OrderManagementConstant.CODE, "1099").put(OrderManagementConstant.MESSAGE,
                    "二维码已失效");
        }
        return R.ok();

    }




}
