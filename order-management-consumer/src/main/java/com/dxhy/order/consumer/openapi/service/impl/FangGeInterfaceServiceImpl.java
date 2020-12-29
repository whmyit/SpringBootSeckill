package com.dxhy.order.consumer.openapi.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.NsrQueueEnum;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import com.dxhy.order.consumer.openapi.service.FangGeInterfaceService;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.dto.FgInvoicePrintDto;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.DictionaryEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalDownloadEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.fg.InvoiceDetailQueueEntity;
import com.dxhy.order.model.fg.InvoiceQueueEntity;
import com.dxhy.order.model.fg.SqZcxxParam;
import com.dxhy.order.model.fg.TbSpxxParam;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.fangge.*;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_RSP;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_UPLOAD_RESPONSE;
import com.dxhy.order.protocol.order.INVALID_INVOICES_RSP;
import com.dxhy.order.protocol.order.INVALID_INVOICE_INFOS;
import com.dxhy.order.protocol.order.ORDER_INVOICE_ITEM;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 方格业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-25 9:22
 */
@Service
@Slf4j
public class FangGeInterfaceServiceImpl implements FangGeInterfaceService {
    private static final String LOGGER_MSG = "(方格订单接口业务类)";
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    @Reference
    private ApiRegistrationCodeService apiRegistrationCodeService;
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    @Reference
    private ApiInvalidInvoiceService apiInvalidInvoiceService;
    @Reference
    private InvoiceDataService invoiceDataService;
    @Reference
    private ICommonDisposeService commonDisposeService;
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    @Reference
    private ApiPushService apiPushService;
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    @Resource
    private InvalidInvoiceService invalidInvoiceService;
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    @Reference
    private ApiDictionaryService apiDictionaryService;
    
    @Reference
    private ApiHistoryDataPdfService apiHistoryDataPdfService;
    
    private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    /**
     * 待开发票数据接口
     */
    @Override
    public FG_ORDER_RESPONSE getInvoices(FG_GET_INVOICE_REQ req) {
        List<FG_COMMON_ORDER> list = new ArrayList<FG_COMMON_ORDER>();
        
        FG_ORDER_RESPONSE<List<FG_COMMON_ORDER>> fgOrderResponse = new FG_ORDER_RESPONSE<List<FG_COMMON_ORDER>>();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = "";
        String jqbh = "";
        try {
            //判断参数
            if (req == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            } else if (StringUtils.isBlank(req.getNSRSBH())) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据销方税号为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            List<String> shList = NsrsbhUtils.transShListByNsrsbh(req.getNSRSBH());
            //条件查询订单处理表
            Map<String, Object> paraMap = new HashMap<>(10);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_FPQQLSH, req.getDDQQLSH());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_START_TIME, req.getKSSJ());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_END_TIME, req.getJSSJ());
            List<String> ddztList = Arrays.asList(OrderInfoEnum.ORDER_STATUS_0.getKey(), OrderInfoEnum.ORDER_STATUS_1.getKey(), OrderInfoEnum.ORDER_STATUS_2.getKey(), OrderInfoEnum.ORDER_STATUS_3.getKey(), OrderInfoEnum.ORDER_STATUS_4.getKey(), OrderInfoEnum.ORDER_STATUS_9.getKey(), OrderInfoEnum.ORDER_STATUS_10.getKey());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_DDZT, ddztList);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_ORDER_STATUS, OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
            
            List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(paraMap, shList);
            if (orderProcessInfos == null || orderProcessInfos.size() <= 0) {
                //处理redis里mqtt消息
                handleRedisMsg(nsrsbh, jqbh);
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                log.error("{},根据纳税人识别号:{}，订单请求流水号:{},开始时间:{},结束时间:{},获取订单数据以及发票数据接口,查询订单处理表数据为空", LOGGER_MSG, req.getNSRSBH(), req.getDDQQLSH(), req.getKSSJ(), req.getJSSJ());
                return fgOrderResponse;
            }
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
            for (OrderProcessInfo info : orderProcessInfos) {
                
                FG_COMMON_ORDER fgCommonOrder = new FG_COMMON_ORDER();
                //查询订单数据
                OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(info.getOrderInfoId(), shList);
                if (orderInfo == null) {
                    //处理redis里mqtt消息
                    handleRedisMsg(nsrsbh, jqbh);
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                    log.error("{},获取订单数据,请求id为:{}查询订单表数据为空", LOGGER_MSG, info.getOrderInfoId());
                    return fgOrderResponse;
                }
                
                nsrsbh = orderInfo.getXhfNsrsbh();
                jqbh = orderInfo.getSld();
                
                //查询订单发票关系表数据
                orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(info.getFpqqlsh(), shList);
                if (ObjectUtils.isEmpty(orderInvoiceInfo)) {
                    //处理redis里mqtt消息为可取
                    handleRedisMsg(nsrsbh, jqbh);
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                    log.error("{},获取查询发票数据为空，发票请求流水号为：{}", LOGGER_MSG, req.getDDQQLSH());
                    return fgOrderResponse;
                }
                /**
                 * 查询明细信息
                 */
                List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
                /**
                 * 数据组装
                 * 订单状态  0 未开具 1 开具成功 2 开具失败
                 */
                FG_COMMON_ORDER_HEAD fgCommonOrderHead = BeanTransitionUtils.transitionFG_ORDER_INVOICE_HEAD(orderInfo, orderInvoiceInfo, orderItemInfos);
                String terminalCode = apiTaxEquipmentService.getTerminalCode(req.getNSRSBH());
                //redis获取注册码
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(req.getNSRSBH(), fgCommonOrderHead.getJQBH());
                if (StringUtils.isNotEmpty(registCodeStr)) {
                    RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                    fgCommonOrderHead.setZCM(registrationCode == null ? "" : registrationCode.getZcm());
                }
    
                if (orderItemInfos == null || orderItemInfos.size() <= 0) {
                    //处理redis里mqtt消息
                    handleRedisMsg(nsrsbh, jqbh);
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL.getMessage());
                    log.error("{},获取订单数据以及发票数据接口,请求查询参数为:{}明细数据为空", LOGGER_MSG, orderInfo.getId());
                    return fgOrderResponse;
                }
                List<FG_ORDER_INVOICE_ITEM> orderInvoiceItems = BeanTransitionUtils.transitionFGORDER_INVOICE_ITEM(orderItemInfos, orderInfo, fgCommonOrderHead);
    
                FgUkeySf fgukeysf = new FgUkeySf();
                try {
                    fgukeysf = getFgukeysf(req.getNSRSBH(), ConfigureConstant.STRING_FGOFDDOWNLOAD_URL);
                } catch (OrderReceiveException e) {
                    fgOrderResponse.setSTATUS_CODE(e.getCode());
                    fgOrderResponse.setSTATUS_MESSAGE(e.getMessage());
                    log.error("{},开票接口读取数据异常,异常信息为:{}", LOGGER_MSG, e);
                    return fgOrderResponse;
                }
                fgCommonOrderHead.setJDIP(fgukeysf.getSjdz());
                fgCommonOrderHead.setJDDK(fgukeysf.getSjdk());
    
                //开票项目 默认为商品行第一行名称
                fgCommonOrderHead.setKPXM(orderItemInfos.get(0).getXmmc());
                /**
                 * “00”不是
                 * “01”农产品销售
                 * “02”农产品收购
                 */
                if (OrderInfoEnum.QDBZ_CODE_2.getKey().equals(orderInfo.getQdBz()) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())) {
                    fgCommonOrderHead.setTSPZ(OrderInfoEnum.FANGGE_TSPZ_02.getKey());
                } else if (OrderInfoEnum.QDBZ_CODE_0.getKey().equals(orderInfo.getQdBz()) || OrderInfoEnum.QDBZ_CODE_1.getKey().equals(orderInfo.getQdBz())) {
                    fgCommonOrderHead.setTSPZ(OrderInfoEnum.FANGGE_TSPZ_00.getKey());
                }
                
                /**
                 * 含税税率标识
                 * 0是普通征收，1是减按计增，2是差额征收
                 */
                if (StringUtils.isNotBlank(orderItemInfos.get(0).getKce())) {
                    fgCommonOrderHead.setHSSLBS(OrderInfoEnum.FANGGE_HSSLBS_2.getKey());
                }
                
                /**
                 * 清单标志处理,如果是收购票,2改为0,3改为1,
                 */
                if (OrderInfoEnum.QDBZ_CODE_2.getKey().equals(fgCommonOrderHead.getQD_BZ())) {
                    fgCommonOrderHead.setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());
                } else if (OrderInfoEnum.QDBZ_CODE_3.getKey().equals(fgCommonOrderHead.getQD_BZ())) {
                    fgCommonOrderHead.setQD_BZ(OrderInfoEnum.QDBZ_CODE_1.getKey());
                }
                /**
                 * 如果是红票,清单标志需要传递0
                 */
                if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                    if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(fgCommonOrderHead.getQD_BZ())) {
                        fgCommonOrderHead.setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());
                    }
                }
                /**
                 * 如果是电票,清单标志需要传递0
                 */
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
                    if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(fgCommonOrderHead.getQD_BZ())) {
                        fgCommonOrderHead.setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());
                    }
                }
                
                /**
                 * 百旺综合税率判断,以下是百旺接口规则
                 *
                 * 负数票：
                 * 对应蓝票相同税率时“综合税率填写sl”;
                 * 对应蓝票不同税率时“综合税率99.01”
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) && ConfigureConstant.STRING_1.equals(fgCommonOrderHead.getSFDSL())) {
                    fgCommonOrderHead.setZHSL(ConfigureConstant.STRING_99_01);
                }
                
                fgCommonOrder.setCOMMON_ORDER_HEAD(fgCommonOrderHead);
                fgCommonOrder.setORDER_INVOICE_ITEMS(orderInvoiceItems);
                
                list.add(fgCommonOrder);
            }
            fgOrderResponse.setData(list);
        } catch (Exception e) {
            //处理redis里mqtt消息
            handleRedisMsg(nsrsbh, jqbh);
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 更新订单数据状态
     */
    @Override
    public FG_ORDER_RESPONSE getInvoiceStatus(List<FG_COMMON_ORDER_STATUS> req) {
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (req == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            for (FG_COMMON_ORDER_STATUS status : req) {
                if (!ConfigureConstant.STRING_2.equals(status.getSJZT())) {
                    //订单请求唯一流水号重复
                    OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
                    orderProcessInfo.setFpqqlsh(status.getDDQQLSH());
                    if (StringUtils.isBlank(status.getSJZT()) || !ConfigureConstant.STRING_0.equals(status.getSJZT()) || !ConfigureConstant.STRING_1.equals(status.getSJZT())) {
                        log.error("{};接口待开订单数据状态接口方格返回状态有误,SJZT-->{}", LOGGER_MSG, status.getSJZT());
                        continue;
                    }
                    orderProcessInfo.setFgStatus(status.getSJZT());
                    orderProcessInfo.setXhfNsrsbh(status.getNSRSBH());
                    //更新订单状态 方格接口
                    apiOrderProcessService.updateOrderProcessInfoByFpqqlsh(orderProcessInfo, NsrsbhUtils.transShListByNsrsbh(status.getNSRSBH()));
                }
            }
            
            
            log.info("{},订单状态更新成功", LOGGER_MSG);
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        
        
        return fgOrderResponse;
    }
    
    /**
     * 更新开票订单数据
     */
    @Override
    public FG_ORDER_RESPONSE updateInvoices(List<FG_COMMON_INVOICE_INFO> infoList) {
        log.info("开票完成更新开票数据，参数：{}", JsonUtils.getInstance().toJsonString(infoList));
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = "", jqbh = "";
        Boolean returnFalg = Boolean.FALSE;
        try {
            for (FG_COMMON_INVOICE_INFO info : infoList) {
                if (StringUtils.isEmpty(info.getJQBH())) {
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE("机器编号不能为空");
                    log.error("{},根据订单处理记录表为空", LOGGER_MSG);
                    return fgOrderResponse;
                }
                String terminalCode = apiTaxEquipmentService.getTerminalCode(info.getNSRSBH());
                List<String> shList = NsrsbhUtils.transShListByNsrsbh(info.getNSRSBH());
    
                /**
                 * 校验方格UKey,并且是电票时,需要返回ofd文件流
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode) && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(info.getFPZLDM()) && StringUtils.isEmpty(info.getOFDWJL())) {
        
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR1.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR1.getMessage());
                    log.error("{},推送开票结果ofd文件流不能为空", LOGGER_MSG);
                    return fgOrderResponse;
                }
    
                /**
                 * 根据订单请求流水号查询
                 */
                OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(info.getDDQQLSH(), shList);
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(info.getDDQQLSH(), shList);
                nsrsbh = orderInvoiceInfo.getXhfNsrsbh();
                jqbh = info.getJQBH();
                if (ObjectUtils.isEmpty(orderProcessInfo)) {
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getMessage());
                    log.error("{},根据订单处理记录表为空", LOGGER_MSG);
                    return fgOrderResponse;
                }
                if (ConfigureConstant.STRING_0.equals(info.getSTATUSCODE())) {
                    //开票成功
                    OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
                    List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
                    orderInvoiceInfo.setDdh(info.getDDH());
                    orderInvoiceInfo.setJym(info.getJYM());
                    orderInvoiceInfo.setFwm(info.getFWM());
                    orderInvoiceInfo.setEwm(info.getEWM());
                    orderInvoiceInfo.setFpdm(info.getFP_DM());
                    orderInvoiceInfo.setFphm(info.getFP_HM());
                    Date kprq = null;
                    if (StringUtils.isNotBlank(info.getKPRQ()) && info.getKPRQ().length() < 9) {
                        kprq = DateUtils.stringToDate(info.getKPRQ(), DateUtilsLocal.YYYYMMDD);
                    } else if (StringUtils.isNotBlank(info.getKPRQ()) && info.getKPRQ().length() >= 9) {
                        kprq = DateUtilsLocal.getMissDataFormat(info.getKPRQ());
                    }
                    orderInvoiceInfo.setKprq(kprq);
                    orderInvoiceInfo.setFpzlDm(orderInfo.getFpzlDm());
                    orderInvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
                    orderInvoiceInfo.setKphjje(orderInfo.getKphjje());
                    orderInvoiceInfo.setJqbh(info.getJQBH());
                    orderInvoiceInfo.setKpse(orderInfo.getHjse());
                    orderInvoiceInfo.setKpzt(ConfigureConstant.STRING_2);
                    orderInvoiceInfo.setUpdateTime(new Date());
                    // process 表更新开票成功
                    orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_5.getKey());
                    
                    String pdfid = "";
                    //如果是电票开具调底层接口
                    if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceInfo.getFpzlDm())) {
                        if (!OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                            Result result = this.genPdf(orderInvoiceInfo, orderInfo, orderItemInfos, terminalCode);
                            log.info("调用底层获取签章结果,出参{}", result);
                            if (Boolean.TRUE.equals(result.get(ConfigureConstant.PDF_GEN_R))) {
                                pdfid = String.valueOf(result.get(ConfigureConstant.PDF_GEN_O));
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
            
                                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey());
                                fgOrderResponse.setSTATUS_MESSAGE(String.valueOf(result.get(ConfigureConstant.MSG)));
                                returnFalg = Boolean.TRUE;
                            }
                        } else {
                            //方格UKey电票,需要把ofd文件流存储到mongodb中.
                            apiHistoryDataPdfService.save(orderInvoiceInfo, info.getOFDWJL(), ConfigureConstant.STRING_SUFFIX_OFD);
                        }
    
    
                    }
                    //更新订单发票数据
                    apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
                    //更新订单处理表
                    apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
                    if (returnFalg) {
                        return fgOrderResponse;
                    }
                } else if (ConfigureConstant.STRING_1.equals(info.getSTATUSCODE()) || ConfigureConstant.STRING_2.equals(info.getSTATUSCODE())) {
                    //开票失败和未插盘的情况
                    log.error("开票完信息：{}", info.getSTATUSMSG());
                    //更新订单处理表为开票失败
                    orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_6.getKey());
                    orderProcessInfo.setSbyy(info.getSTATUSMSG());
                    //开票失败时修改获取数据状态为失败，重新拉取数据
                    orderProcessInfo.setFgStatus(ConfigureConstant.STRING_1);
                    apiOrderProcessService.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
                    
                    orderInvoiceInfo.setKpzt(ConfigureConstant.STRING_3);
                    orderInvoiceInfo.setSbyy(info.getSTATUSMSG());
                    orderInvoiceInfo.setUpdateTime(new Date());
                    //更新订单发票数据
                    apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
                    
                    fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey());
                    fgOrderResponse.setSTATUS_MESSAGE(info.getSTATUSMSG());
                }
                //如果是红票的话 更新原蓝票的冲红标志和剩余可冲红金额
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
                    //红票
                    apiFangGeInterfaceService.dealRedInvoice(info, orderInvoiceInfo);
                }
                
                //发票数据放入发票推送队列
                InvoiceBatchRequestItem invoiceBatchRequestItem = apiInvoiceCommonService.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                InvoicePush invoicePush = com.dxhy.order.utils.BeanTransitionUtils.transitionInvoicePush(orderInvoiceInfo, invoiceBatchRequestItem.getFpqqpch());
                log.info("测试开票时间-回推放入队列：{},发票请求流水号：{}", DateUtil.now(), orderInvoiceInfo.getFpqqlsh());
                log.info("{},invociePush:{}", LOGGER_MSG + ": 开票回推信息", JsonUtils.getInstance().toJsonString(invoicePush));
                apiPushService.putDataInQueue(invoicePush, orderInvoiceInfo.getXhfNsrsbh(), NsrQueueEnum.PUSH_MESSAGE.getValue());
            }
            
            //处理redis里mqtt消息
            handleRedisMsg(nsrsbh, jqbh);
            
        } catch (Exception e) {
            //处理redis里mqtt消息
            handleRedisMsg(nsrsbh, jqbh);
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        
        return fgOrderResponse;
    }
    
    @Override
    public Result genPdf(OrderInvoiceInfo orderInvoiceInfo, OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos, String terminalCode) {
        
        InvoiceQueueEntity entity = new InvoiceQueueEntity();
        entity.setInvoiceType(ConfigureConstant.STRING_2);
        entity.setTaxpayerIdentifyNo(orderInvoiceInfo.getXhfNsrsbh());
        entity.setBillingType(Long.parseLong(orderInvoiceInfo.getKplx()));
        entity.setInvoiceRequestSerialNo(orderInvoiceInfo.getFpqqlsh());
        
        //发票冲红加备注  对应正数发票代码:150004528888号码:62900115
        if (ConfigureConstant.STRING_0.equals(orderInfo.getKplx())) {
            //蓝票
            entity.setMemo(orderInfo.getBz());
        } else {//红票
            String bz = "对应正数发票代码:" + orderInfo.getYfpDm() + "号码:" + orderInfo.getYfpHm();
            entity.setMemo(orderInfo.getBz() + bz);
        }
        entity.setOldInvoiceCode(orderInfo.getYfpDm());
        entity.setOldInvoiceNo(orderInfo.getYfpHm());
        entity.setBuyerMobile(orderInfo.getGhfSj());
        entity.setBuyerFixedPhone(orderInfo.getGhfDh());
        entity.setBuyerEmail(orderInfo.getGhfEmail());
        entity.setBuyerName(orderInfo.getGhfMc());
        entity.setBuyerTaxpayerIdentifyNo(orderInfo.getGhfNsrsbh());
        entity.setBuyerAddress(orderInfo.getGhfDz());
        entity.setBuyerEnterpriseTypeCode(orderInfo.getGhfQylx());
        entity.setSellerTaxpayerIdentifyNo(orderInfo.getXhfNsrsbh());
        entity.setSellerName(orderInfo.getXhfMc());
        entity.setIndustryName(orderInfo.getHyMc());
        entity.setTaxpayer(orderInfo.getNsrmc());
        entity.setOperatorNo(orderInfo.getCzdm());
        entity.setInfoClientBankAccount(StringUtils.isEmpty(orderInfo.getGhfYh()) ? "" : orderInfo.getGhfYh() + (StringUtils.isEmpty(orderInfo.getGhfZh()) ? "" : orderInfo.getGhfZh()));
        
        entity.setInfoClientAddressPhone(StringUtils.isEmpty(orderInfo.getGhfDz()) ? "" : orderInfo.getGhfDz() + (StringUtils.isEmpty(orderInfo.getGhfDh()) ? "" : orderInfo.getGhfDh()));
        
        entity.setInfoSellerBankAccount(StringUtils.isEmpty(orderInfo.getXhfYh()) ? "" : orderInfo.getXhfYh() + (StringUtils.isEmpty(orderInfo.getXhfZh()) ? "" : orderInfo.getXhfZh()));
        entity.setInfoSellerAddressPhone(StringUtils.isEmpty(orderInfo.getXhfDz()) ? "" : orderInfo.getXhfDz() + (StringUtils.isEmpty(orderInfo.getXhfDh()) ? "" : orderInfo.getXhfDh()));
        entity.setBillingStaff(orderInfo.getKpr());
        entity.setInfoChecker(orderInfo.getFhr());
        //销货清单没有
        entity.setInfoListName("");
        entity.setInfoMonth(DateUtils.format(orderInfo.getDdrq()));
        
        //合计税额
        entity.setInfoTaxAmount(Double.parseDouble(orderInfo.getHjse()));
        //开票合计金额
        entity.setBillingAmount(Double.parseDouble(orderInfo.getKphjje()));
        // 合计不含税金额
        entity.setInfoAmount(Double.parseDouble(orderInfo.getHjbhsje()));
        // entity.setInfoAmount();   12.1
        entity.setGoodsListFlag(orderInfo.getQdBz());
        //返回编码没有
        entity.setRetCode("");
        //防伪密文对应的是防伪码吗
        entity.setCiphertext(orderInvoiceInfo.getFwm());
        entity.setCheckCode(orderInvoiceInfo.getJym());
        //数字签名
        entity.setInfoInvoicer("");
        //收款员
        entity.setCashier(orderInfo.getSkr());
        entity.setCodeTableVersion(orderInfo.getBbmBbh());
        entity.setInvoiceKindCode(orderInfo.getFpzlDm());
        entity.setListFlag(orderInfo.getQdBz());
        entity.setListItemname(orderInfo.getQdXmmc());
        entity.setInvoiceCode(orderInvoiceInfo.getFpdm());
        entity.setInvoiceNo(orderInvoiceInfo.getFphm());
        if (ObjectUtil.isNotEmpty(orderInvoiceInfo.getKprq())) {
            
            entity.setBillingDate(orderInvoiceInfo.getKprq());
        }
        entity.setTwoDimensionCode(orderInvoiceInfo.getEwm());
        //机器编号
        entity.setMachineNumber(orderInvoiceInfo.getJqbh());
        //分机号
        entity.setExtensionNumber("");
        entity.setInvoiceSerialNo(orderInvoiceInfo.getFpdm() + orderInvoiceInfo.getFphm());
        //收购标志
        entity.setTakeoversMark("");
        //代开标志
        entity.setAgentInvoiceFlag("");
        // 处理业务错误描述
        entity.setReturnMessage("");
        //队列错误处理次数
        entity.setQueueErrorCount(0);
        //特殊冲红标志
        entity.setTschbz("");
        
        List<InvoiceDetailQueueEntity> list = new ArrayList<InvoiceDetailQueueEntity>();
        for (OrderItemInfo itemInfo : orderItemInfos) {
            InvoiceDetailQueueEntity invoiceDetailQueueEntity = new InvoiceDetailQueueEntity();
            invoiceDetailQueueEntity.setItemIndex(Long.parseLong(itemInfo.getSphxh()));
            invoiceDetailQueueEntity.setItemName(itemInfo.getXmmc());
            invoiceDetailQueueEntity.setUnitName(itemInfo.getXmdw());
            invoiceDetailQueueEntity.setSpecificationModel(itemInfo.getGgxh());
            invoiceDetailQueueEntity.setItemCount(StringUtils.isEmpty(itemInfo.getXmsl()) ? null : Double.parseDouble(itemInfo.getXmsl()));
            invoiceDetailQueueEntity.setItemUnitCost(StringUtils.isEmpty(itemInfo.getXmdj()) ? null : Double.parseDouble(itemInfo.getXmdj()));
            invoiceDetailQueueEntity.setItemAmount(StringUtils.isEmpty(itemInfo.getXmje()) ? null : Double.parseDouble(itemInfo.getXmje()));
            //项目编码
            invoiceDetailQueueEntity.setItemCode("");
            invoiceDetailQueueEntity.setListPriceKind(itemInfo.getHsbz());
            invoiceDetailQueueEntity.setListTaxAmount(StringUtils.isEmpty(itemInfo.getSe()) ? null : Double.parseDouble(itemInfo.getSe()));
            invoiceDetailQueueEntity.setInfoTaxRate(itemInfo.getSl());
            //税目
            invoiceDetailQueueEntity.setListTaxItem("");
            invoiceDetailQueueEntity.setInvoiceLineProperty(itemInfo.getFphxz());
            invoiceDetailQueueEntity.setCommodityCode(itemInfo.getSpbm());
            invoiceDetailQueueEntity.setVoluntarilyCode(itemInfo.getZxbm());
            invoiceDetailQueueEntity.setIncentiveFlag(itemInfo.getYhzcbs());
            invoiceDetailQueueEntity.setZeroTaxrateFlag(itemInfo.getLslbs());
            invoiceDetailQueueEntity.setAddtaxManager(itemInfo.getZzstsgl());
            invoiceDetailQueueEntity.setDeductionAmount(StringUtils.isEmpty(itemInfo.getKce()) ? null : Double.parseDouble(itemInfo.getKce()));
            list.add(invoiceDetailQueueEntity);
        }
        entity.setDetailQueueEntityList(list);
        log.info("电票开具调用底层接口，入参为{}", JsonUtils.getInstance().toJsonString(entity));
        com.dxhy.order.model.protocol.Result result1 = HttpInvoiceRequestUtilFg.genPdf(OpenApiConfig.genPdfFg, entity, terminalCode);
        return result1;
    }
    
    /**
     * 方格接口  获取红字申请的待上传订单数据
     */
    @Override
    public FG_ORDER_RESPONSE getUploadRedInvoice(FG_GET_INVOICE_UPLOAD_REQ param) {
        log.debug("[{}红票申请单上传获取数据接口，参数为[{}]", LOGGER_MSG, JsonUtils.getInstance().toJsonString(param));
        FG_ORDER_RESPONSE<FG_RED_INVOICE_FORM_REQ> fgOrderResponse = new FG_ORDER_RESPONSE<FG_RED_INVOICE_FORM_REQ>();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        
        FG_RED_INVOICE_FORM_REQ req = new FG_RED_INVOICE_FORM_REQ();
        List<FG_RED_INVOICE_FORM_UPLOAD> fgRedInvoiceFormUploads = new ArrayList<>();
        FG_RED_INVOICE_FORM_UPLOAD fgRedInvoiceFormUpload = new FG_RED_INVOICE_FORM_UPLOAD();
        List<ORDER_INVOICE_ITEM> orderInvoiceItems = new ArrayList<>();
        //条件查询待上传的红票申请单
        SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.selectDscSpecialInvoiceReversalsBySqbscqqlsh(param.getSQBSCQQPCH(), param.getNSRSBH());
        if (ObjectUtils.isEmpty(specialInvoiceReversal)) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL.getMessage());
            log.error("{},红票申请单待上传数据为空", LOGGER_MSG);
            return fgOrderResponse;
        }
        FgUkeySf fgukeysf = new FgUkeySf();
        try {
            fgukeysf = getFgukeysf(specialInvoiceReversal.getNsrsbh(), ConfigureConstant.STRING_FGUPLOAD_URL);
        } catch (OrderReceiveException e) {
            fgOrderResponse.setSTATUS_CODE(e.getCode());
            fgOrderResponse.setSTATUS_MESSAGE(e.getMessage());
            log.error("{},红票申请单待上传数据异常,异常信息为:{}", LOGGER_MSG, e);
            return fgOrderResponse;
        }
    
        FG_RED_INVOICE_FORM_BATCH reqBatch = new FG_RED_INVOICE_FORM_BATCH();
        reqBatch.setSQBSCQQPCH(specialInvoiceReversal.getSqdscqqpch());
        reqBatch.setKPJH(StringUtils.isNotBlank(specialInvoiceReversal.getFjh()) ? specialInvoiceReversal.getFjh() : "");
        reqBatch.setFPZLDM(specialInvoiceReversal.getFpzlDm());
        String callType = specialInvoiceReversal.getSqsm().substring(0, 1);
        reqBatch.setSQLB(callType);
    
        reqBatch.setNSRSBH("1".equals(callType) ? specialInvoiceReversal.getGhfNsrsbh() : specialInvoiceReversal.getXhfNsrsbh());
        //获取机器编号
        reqBatch.setJQBH(specialInvoiceReversal.getSld());
        //redis获取注册码
        String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(reqBatch.getNSRSBH(), specialInvoiceReversal.getSld());
        if (StringUtils.isNotEmpty(registCodeStr)) {
            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
            reqBatch.setZCM(registrationCode == null ? "" : registrationCode.getZcm());
        }
        reqBatch.setKZZD("");
        req.setRED_INVOICE_FORM_BATCH(reqBatch);
    
        FG_RED_INVOICE_FORM_HEAD fgRedInvoiceFormHead = new FG_RED_INVOICE_FORM_HEAD();
        fgRedInvoiceFormHead.setJDIP(fgukeysf.getSjdz());
        fgRedInvoiceFormHead.setJDDK(fgukeysf.getSjdk());
        fgRedInvoiceFormHead.setSQBSCQQLSH(specialInvoiceReversal.getSqdscqqlsh());
        fgRedInvoiceFormHead.setYFP_DM(specialInvoiceReversal.getYfpDm());
        fgRedInvoiceFormHead.setYFP_HM(specialInvoiceReversal.getYfpHm());
    
        // 根据申请单类型 判断是否需要传原发票开票日期 购方已抵扣的发票不需要传原发票开票日期  原票开票日期格式化为yyyyMM
        if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoiceReversal.getSqsm())) {
            // 购方申请已抵扣
            fgRedInvoiceFormHead.setYFP_KPRQ(specialInvoiceReversal.getYfpKprq() == null ? "" : DateUtil.format(specialInvoiceReversal.getYfpKprq(), "yyyy-MM"));
        } else if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey()
                .equals(specialInvoiceReversal.getSqsm())) {
            // 购方申请未抵扣
            fgRedInvoiceFormHead.setYFP_KPRQ(specialInvoiceReversal.getYfpKprq() == null ? "" : DateUtil.format(specialInvoiceReversal.getYfpKprq(), "yyyy-MM"));
        } else if (OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialInvoiceReversal.getSqsm())) {
            // 销方申请
            fgRedInvoiceFormHead.setYFP_KPRQ(specialInvoiceReversal.getYfpKprq() == null ? "" : DateUtil.format(specialInvoiceReversal.getYfpKprq(), "yyyy-MM"));
        }
        fgRedInvoiceFormHead.setXSF_NSRSBH(specialInvoiceReversal.getXhfNsrsbh());
        fgRedInvoiceFormHead.setXSF_MC(specialInvoiceReversal.getXhfMc());
        fgRedInvoiceFormHead.setGMF_NSRSBH(specialInvoiceReversal.getGhfNsrsbh());
        fgRedInvoiceFormHead.setGMF_MC(specialInvoiceReversal.getGhfMc());
        fgRedInvoiceFormHead.setHJJE(specialInvoiceReversal.getHjbhsje());
        fgRedInvoiceFormHead.setHJSE(specialInvoiceReversal.getHjse());
        fgRedInvoiceFormHead.setSQSM(specialInvoiceReversal.getSqsm());
        fgRedInvoiceFormHead.setBMB_BBH("1.0");
        fgRedInvoiceFormHead.setXXBLX("0");
        //填开时间为 yyyy-MM-dd HH:mm:ss ,返回方格转换为yyyyMMdd
        fgRedInvoiceFormHead.setTKSJ(DateUtils.format(specialInvoiceReversal.getTksj(), DateUtils.DATE_PATTERN_NOSPLIT));
        fgRedInvoiceFormHead.setYYSBZ("0000000000");
        
        fgRedInvoiceFormHead.setSL(specialInvoiceReversal.getDslbz());
        //税种类别0:营业税;1:增值税(默认)
        fgRedInvoiceFormHead.setSZLB("1");
        //0:一票一税率,1:一票多税率
        fgRedInvoiceFormHead.setDSLBS("0");
        //0:正常发票 1:减按计征2:差额征收
        fgRedInvoiceFormHead.setSLBS("0");
        fgRedInvoiceFormUpload.setRED_INVOICE_FORM_HEAD(fgRedInvoiceFormHead);
        
        
        List<SpecialInvoiceReversalItem> list = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(specialInvoiceReversal.getId());
        for (SpecialInvoiceReversalItem e : list) {
            ORDER_INVOICE_ITEM orderInvoiceItem = new ORDER_INVOICE_ITEM();
            orderInvoiceItem.setXMXH(e.getSphxh());
            String fphxz = "0";
            if (ConfigureConstant.XJZSXHQD.equals(e.getXmmc()) && list.size() == 1) {
                fphxz = "6";
            }
            orderInvoiceItem.setFPHXZ(fphxz);
            orderInvoiceItem.setSPBM(StringUtils.isNotBlank(e.getSpbm()) ? e.getSpbm() : "");
            orderInvoiceItem.setZXBM("");
            orderInvoiceItem.setYHZCBS(e.getYhzcbs());
            orderInvoiceItem.setLSLBS(e.getLslbs());
            String zzstsgl = "";
            if (StringUtils.isNotBlank(e.getZzstsgl())) {
                if ("1".equals(e.getLslbs())) {
                    zzstsgl = "免税";
                } else if ("2".equals(e.getLslbs())) {
                    zzstsgl = "不征税";
                }
            }
            orderInvoiceItem.setZZSTSGL(zzstsgl);
            orderInvoiceItem.setXMMC(e.getXmmc());
            orderInvoiceItem.setGGXH(StringUtils.isNotBlank(e.getGgxh()) ? e.getGgxh() : "");
            orderInvoiceItem.setDW(StringUtils.isNotBlank(e.getXmdw()) ? e.getXmdw() : "");
            orderInvoiceItem.setXMSL(StringUtils.isNotBlank(e.getXmsl()) ? e.getXmsl() : "");
            orderInvoiceItem.setXMDJ(StringUtils.isNotBlank(e.getXmdj()) ? e.getXmdj() : "");
            orderInvoiceItem.setXMJE(e.getXmje());
            orderInvoiceItem.setHSBZ(e.getHsbz());
            orderInvoiceItem.setSL(e.getSl());
            orderInvoiceItem.setSE(StringUtils.isNotBlank(e.getSe()) ? e.getSe() : "0.00");
            orderInvoiceItems.add(orderInvoiceItem);
        }
        fgRedInvoiceFormUpload.setORDER_INVOICE_ITEMS(orderInvoiceItems);
        fgRedInvoiceFormUploads.add(fgRedInvoiceFormUpload);
        req.setRED_INVOICE_FORM_UPLOADS(fgRedInvoiceFormUploads);
        fgOrderResponse.setData(req);
        return fgOrderResponse;
    }
    
    /**
     * 获取方格UKey税局地址
     *
     * @param nsrsbh
     * @param type
     * @return
     * @throws OrderReceiveException
     */
    public FgUkeySf getFgukeysf(String nsrsbh, String type) throws OrderReceiveException {
        /**
         * 如果是本地UKey需要传递税局的ip和端口,
         * 读取到红字信息表数据,然后判断是销方申请还是购方申请,根据对应税号获取该企业配置的税局地址
         */
        String sjdz = "";
        String sjdk = "";
        
        List<TaxEquipmentInfo> taxEquipmentInfos = apiTaxEquipmentService.queryTaxEquipmentList(new TaxEquipmentInfo(), NsrsbhUtils.transShListByNsrsbh(nsrsbh));
        if (ObjectUtil.isEmpty(taxEquipmentInfos)) {
            throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL1);
        }
        String terminalCode = taxEquipmentInfos.get(0).getSksbCode();
        if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            
            String sfdm = taxEquipmentInfos.get(0).getSfdm();
            if (StringUtils.isEmpty(sfdm)) {
                throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL2);
            } else {
                //根据省份代码去字典表查询数据
                List<DictionaryEntity> dictionaryEntities = apiDictionaryService.queryDictionaries(type);
                if (ObjectUtil.isEmpty(dictionaryEntities)) {
                    throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL3);
                } else {
                    String sjUrl = "";
                    for (DictionaryEntity dictionaryEntity : dictionaryEntities) {
                        if (sfdm.equals(dictionaryEntity.getCode())) {
                            sjUrl = dictionaryEntity.getValue();
                        }
                    }
                    if (StringUtils.isEmpty(sjUrl)) {
                        throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL3);
                    } else {
                        /**
                         * 根据域名解析对应的ip地址
                         */
                        sjdz = sjUrl.split(ConfigureConstant.STRING_COLON)[0];
                        try {
                            InetAddress addresses = InetAddress.getByName(sjdz);
                            sjdz = addresses.getHostAddress();
                        } catch (UnknownHostException e) {
                            throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL4);
                        }
                        sjdk = sjUrl.split(ConfigureConstant.STRING_COLON)[1];
                    }
                }
            }
            
            if (StringUtils.isEmpty(sjdz) || StringUtils.isEmpty(sjdk)) {
                
                throw new OrderReceiveException(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_NULL2);
            }
        }
        FgUkeySf fgukeysf = new FgUkeySf();
        fgukeysf.setSjdz(sjdz);
        fgukeysf.setSjdk(sjdk);
        return fgukeysf;
    }
    
    /**
     * 更新方格红票申请单上传数据状态
     */
    @Override
    public FG_ORDER_RESPONSE getUploadRedInvoiceStatus(FG_COMMON_RED_INVOICE_UPLOAD_STATUS paramContent) {
        log.debug("[{}红票申请单上传修改状态接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            List<FG_COMMON_RED_INVOICE_UPLOAD> list = paramContent.getSQDQQSJ();
            for (FG_COMMON_RED_INVOICE_UPLOAD upload : list) {
                SpecialInvoiceReversalEntity specialInvoiceReversalEntity = new SpecialInvoiceReversalEntity();
                specialInvoiceReversalEntity.setSqdscqqlsh(upload.getSQBSCQQLSH());
                specialInvoiceReversalEntity.setScfgStatus(upload.getSJZT());
                specialInvoiceReversalEntity.setSqdscqqpch(paramContent.getSQBSCQQPCH());
                apiSpecialInvoiceReversalService.getUploadRedInvoiceStatus(specialInvoiceReversalEntity);
            }
            
            log.info("{},红票申请单上传状态更新成功", LOGGER_MSG);
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 红票申请单上传接口
     */
    @Override
    public FG_ORDER_RESPONSE updateUploadRedInvoice(RED_INVOICE_FORM_RSP paramContent) {
        log.debug("[{}]红票申请单上传完成接口，参数为[{}]", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramContent));
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = "", jqbh = "";
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},红票申请单上传完成接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            String fpdm = "", fphm = "";
            /**
             * 方格为单张开票,因此申请单一批只能为一个.
             * 批次和流水号一致
             */
            List<RED_INVOICE_FORM_UPLOAD_RESPONSE> red = paramContent.getRED_INVOICE_FORM_UPLOAD_RESPONSES();
            if (ConfigureConstant.STRING_0.equals(paramContent.getSTATUS_CODE())) {
                //上传完成
                if (red != null) {
                    for (RED_INVOICE_FORM_UPLOAD_RESPONSE r : red) {
                        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(r.getSQBSCQQLSH());
                        if (ConfigureConstant.STRING_0.equals(r.getSTATUS_CODE())) {
                            //上传成功
                            specialInvoiceReversalEntity.setXxbbh(r.getXXBBH());
                            specialInvoiceReversalEntity.setStatusCode("TZD0001");
                        } else {
                            //上传失败
                            specialInvoiceReversalEntity.setStatusCode("TZD9998");
                        }
                        specialInvoiceReversalEntity.setStatusMessage(r.getSTATUS_MESSAGE());
                        nsrsbh = specialInvoiceReversalEntity.getXhfNsrsbh();
                        fpdm = specialInvoiceReversalEntity.getYfpDm();
                        fphm = specialInvoiceReversalEntity.getYfpHm();
                        apiFangGeInterfaceService.updateUploadRedInvoice(specialInvoiceReversalEntity);
                        
                        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(nsrsbh);
                        
                        OrderInvoiceInfo info = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(fpdm, fphm, shList);
                        if (!ObjectUtils.isEmpty(info)) {
                            jqbh = info.getJqbh();
                        }
                        
                        if (StringUtils.isNotBlank(nsrsbh) && StringUtils.isNotBlank(jqbh)) {
                            handleRedisMsg(nsrsbh, jqbh);
                        }
                    }
                }
            } else if (ConfigureConstant.STRING_1.equals(paramContent.getSTATUS_CODE()) || ConfigureConstant.STRING_2.equals(paramContent.getSTATUS_CODE())) {
                //上传失败或者未插盘
                /**
                 * 修改上传数据状态为待上传（2），可重新拉取数据
                 */
                if (red != null) {
                    for (RED_INVOICE_FORM_UPLOAD_RESPONSE r : red) {
                        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(r.getSQBSCQQLSH());
                        if (ObjectUtils.isEmpty(specialInvoiceReversalEntity)) {
                            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
                            fgOrderResponse.setSTATUS_MESSAGE("没有查询到红字信息表，申请表上传请求流水号为：" + r.getSQBSCQQLSH());
                        } else {
                            specialInvoiceReversalEntity.setScfgStatus(ConfigureConstant.STRING_2);
                            specialInvoiceReversalEntity.setStatusCode("TZD9998");
                            
                            fpdm = specialInvoiceReversalEntity.getFpdm();
                            fphm = specialInvoiceReversalEntity.getFphm();
                            nsrsbh = specialInvoiceReversalEntity.getNsrsbh();
                            jqbh = specialInvoiceReversalEntity.getSld();
                            
                            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
                            if (StringUtils.isNotEmpty(paramContent.getSTATUS_MESSAGE())) {
                                fgOrderResponse.setSTATUS_MESSAGE(paramContent.getSTATUS_MESSAGE());
                                specialInvoiceReversalEntity.setStatusMessage(paramContent.getSTATUS_MESSAGE());
                            } else {
                                fgOrderResponse.setSTATUS_MESSAGE(StringUtils.isNotEmpty(red.get(0).getSTATUS_MESSAGE()) ? red.get(0).getSTATUS_MESSAGE() : "上传失败");
                                specialInvoiceReversalEntity.setStatusMessage(StringUtils.isNotEmpty(red.get(0).getSTATUS_MESSAGE()) ? red.get(0).getSTATUS_MESSAGE() : "上传失败");
                            }
                            apiFangGeInterfaceService.updateUploadRedInvoice(specialInvoiceReversalEntity);
                        }
                        
                        if (StringUtils.isNotBlank(nsrsbh) && StringUtils.isNotBlank(jqbh)) {
                            handleRedisMsg(nsrsbh, jqbh);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (StringUtils.isNotBlank(nsrsbh) && StringUtils.isNotBlank(jqbh)) {
                handleRedisMsg(nsrsbh, jqbh);
            }
            
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 获取红字申请单下载订单数据
     */
    @Override
    public FG_ORDER_RESPONSE getDownloadRedInvoice(FG_GET_INVOICE_DOWNLOAD_REQ paramContent) {
        log.error("[{}],红票申请单获取下载数据接口，参数为{}", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE<FG_COMMON_RED_INVOICE_DOWNLOAD> fgCommonRedInvoiceDownloadfgOrderResponse = new FG_ORDER_RESPONSE<FG_COMMON_RED_INVOICE_DOWNLOAD>();
        fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
    
        FG_COMMON_RED_INVOICE_DOWNLOAD fgCommonRedInvoiceDownload = new FG_COMMON_RED_INVOICE_DOWNLOAD();
    
        SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload = apiSpecialInvoiceReversalService.getSpecialInvoiceReversalDownload(paramContent.getSQBXZQQPCH());
        if (ObjectUtils.isEmpty(specialInvoiceReversalDownload)) {
            fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_DOWNLOAD_NULL.getKey());
            fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_SPECIAL_DOWNLOAD_NULL.getMessage());
            log.error("{},红票申请单待下载数据为空", LOGGER_MSG);
            return fgCommonRedInvoiceDownloadfgOrderResponse;
        }
    
        FgUkeySf fgukeysf = new FgUkeySf();
        try {
            fgukeysf = getFgukeysf(specialInvoiceReversalDownload.getNsrsbh(), ConfigureConstant.STRING_FGUPLOAD_URL);
        } catch (OrderReceiveException e) {
            fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_CODE(e.getCode());
            fgCommonRedInvoiceDownloadfgOrderResponse.setSTATUS_MESSAGE(e.getMessage());
            log.error("{},红票申请单待下载数据异常,异常信息为:{}", LOGGER_MSG, e);
            return fgCommonRedInvoiceDownloadfgOrderResponse;
        }
    
        fgCommonRedInvoiceDownload.setSQBXZQQPCH(specialInvoiceReversalDownload.getSqbxzqqpch());
        fgCommonRedInvoiceDownload.setNSRSBH(paramContent.getNSRSBH());
        fgCommonRedInvoiceDownload.setJDIP(fgukeysf.getSjdz());
        fgCommonRedInvoiceDownload.setJDDK(fgukeysf.getSjdk());
        fgCommonRedInvoiceDownload.setKPJH(specialInvoiceReversalDownload.getKpjh());
        fgCommonRedInvoiceDownload.setJQBH(specialInvoiceReversalDownload.getSldid());
        fgCommonRedInvoiceDownload.setFPZLDM(specialInvoiceReversalDownload.getFpzldm());
        fgCommonRedInvoiceDownload.setTKRQ_Q(specialInvoiceReversalDownload.getTkrqQ());
        fgCommonRedInvoiceDownload.setTKRQ_Z(specialInvoiceReversalDownload.getTkrqZ());
        fgCommonRedInvoiceDownload.setGMF_NSRSBH(specialInvoiceReversalDownload.getGmfNsrsbh());
        fgCommonRedInvoiceDownload.setXSF_NSRSBH(specialInvoiceReversalDownload.getXsfNsrsbh());
        fgCommonRedInvoiceDownload.setXXBBH(specialInvoiceReversalDownload.getXxbbh());
        fgCommonRedInvoiceDownload.setXXBFW(specialInvoiceReversalDownload.getXxbfw());
        fgCommonRedInvoiceDownload.setPageNo(specialInvoiceReversalDownload.getPageno());
        fgCommonRedInvoiceDownload.setPageSize(specialInvoiceReversalDownload.getPagesize());
        //redis获取注册码
        String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(paramContent.getNSRSBH(), specialInvoiceReversalDownload.getSldid());
        if (StringUtils.isNotEmpty(registCodeStr)) {
            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
            fgCommonRedInvoiceDownload.setZCM(registrationCode == null ? "" : registrationCode.getZcm());
        }
        fgCommonRedInvoiceDownloadfgOrderResponse.setData(fgCommonRedInvoiceDownload);
        return fgCommonRedInvoiceDownloadfgOrderResponse;
    }
    
    
    /**
     * 红票申请单下载  修改状态
     */
    @Override
    public FG_ORDER_RESPONSE updateDownloadRedInvoiceStatus(FG_RED_INVOICE_DOWNLOAD_STATUS_REQ paramContent) {
        log.debug("[{}红票申请单下载修改状态接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            
            apiSpecialInvoiceReversalService.updateDownloadRedInvoiceStatus(paramContent.getNSRSBH(), paramContent.getSQBXZQQPCH(), paramContent.getSJZT());
            log.info("{},红票申请单下载状态更新成功", LOGGER_MSG);
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 红票申请单下载 下载完成修改状态
     */
    @Override
    public FG_ORDER_RESPONSE updateDownloadRedInvoice(FG_RED_INVOICE_DOWNLOAD_REQ paramContent, String operatorId, String operatorName) {
        log.debug("[{}红票申请单下载完成接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = "", jqbh = "";
        try {
            //判断参数
            if (paramContent == null || paramContent.getRED_INVOICE_FORM_DOWNLOADS() == null || paramContent.getRED_INVOICE_FORM_DOWNLOADS().size() < 1 || paramContent.getRED_INVOICE_FORM_DOWNLOADS().get(0).getRED_INVOICE_FORM_DOWN_HEAD() == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("{},红票申请单下载完成接口,请求数据为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            SpecialInvoiceReversalDownloadEntity specialInvoiceReversalDownload = apiSpecialInvoiceReversalService.getSpecialInvoiceReversalDownload(paramContent.getSQBXZQQPCH());
            if (specialInvoiceReversalDownload == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL.getMessage());
                log.error("{},根据批次号没有查询到下载数据,批次号为：{}", LOGGER_MSG, paramContent.getSQBXZQQPCH());
                return fgOrderResponse;
            }
            nsrsbh = specialInvoiceReversalDownload.getNsrsbh();
            jqbh = specialInvoiceReversalDownload.getSldid();
            List<FG_RED_INVOICE_FORM_DOWNLOAD> red = paramContent.getRED_INVOICE_FORM_DOWNLOADS();
            //下载成功
            if (ConfigureConstant.STRING_0.equals(paramContent.getSTATUS_CODE())) {
                if (red != null) {
                    boolean isPush;
                    for (FG_RED_INVOICE_FORM_DOWNLOAD r : red) {
                        isPush = false;
                        FG_RED_INVOICE_FORM_DOWN_HEAD head = r.getRED_INVOICE_FORM_DOWN_HEAD();
                        /**
                         * 方格使用内层的申请单号作为数据
                         */
                        SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(head.getSQDH());
                        if (ObjectUtils.isEmpty(specialInvoiceReversal)) {
                            //数据库不存在保存数据
                            isPush = true;
                            String type = "0";
                            if ("0000000090".equals(head.getYYSBZ())) {
                                type = "3";
                            }
                            specialInvoiceReversal.setType(type);
                            specialInvoiceReversal.setSqdh(head.getSQDH());
                            specialInvoiceReversal.setSqdscqqlsh(head.getSQDH());
                            specialInvoiceReversal.setSqdscqqpch(head.getSQDH());
                            specialInvoiceReversal.setSqsm(convertReason(head.getSQSM()));
                            specialInvoiceReversal.setYfpDm(("0000000000".equals(head.getYFP_DM()) ? ""
                                    : head.getYFP_DM()));
                            specialInvoiceReversal.setYfpHm(("00000000".equals(head.getYFP_HM())
                                    ? "" : head.getYFP_HM()));
                            specialInvoiceReversal.setInvoiceType(OrderInfoEnum.INVOICE_TYPE_1.getKey());
                            specialInvoiceReversal.setFpzlDm(specialInvoiceReversalDownload.getFpzldm());
                            specialInvoiceReversal.setTksj(
                                    DateUtils.stringToDate(head.getTKSJ(),
                                            DateUtils.DATE_TIME_PATTERN));
                            specialInvoiceReversal.setXhfMc(head.getXSF_MC());
                            specialInvoiceReversal.setXhfNsrsbh(head.getXSF_NSRSBH());
                            specialInvoiceReversal.setGhfMc(head.getGMF_MC());
                            specialInvoiceReversal.setGhfNsrsbh(head.getGMF_NSRSBH());
                            specialInvoiceReversal.setGhfqylx("01");
                            specialInvoiceReversal.setHjbhsje(head.getHJJE());
                            specialInvoiceReversal.setHjse(head.getHJSE());
                            specialInvoiceReversal.setKphjje(new BigDecimal(head.getHJJE())
                                    .add(new BigDecimal(head.getHJSE())).toString());
                            specialInvoiceReversal.setXxbbh(head.getXXBBH());
                            specialInvoiceReversal.setStatusCode(head.getSTATUS_CODE());
                            specialInvoiceReversal.setStatusMessage(head.getSTATUS_MESSAGE());
                            specialInvoiceReversal.setNsrsbh(nsrsbh);
                            specialInvoiceReversal.setCreatorId(operatorId);
                            specialInvoiceReversal.setCreatorName(operatorName);
                            specialInvoiceReversal.setEditorId(operatorId);
                            specialInvoiceReversal.setEditorName(operatorName);
                            specialInvoiceReversal.setScfgStatus(ConfigureConstant.STRING_1);
                            specialInvoiceReversal.setId(apiInvoiceCommonService.getGenerateShotKey());
                            boolean isSuccess = addSpecialInvoiceReversal(specialInvoiceReversal);
                            if (isSuccess) {
                                String specialInvoiceReversalId = specialInvoiceReversal.getId();
                                List<ORDER_INVOICE_ITEM> orderInvoiceItems = r.getORDER_INVOICE_ITEMS();
                                String taxRate = "";
                                for (int k = 0; k < orderInvoiceItems.size(); k++) {
                                    ORDER_INVOICE_ITEM invoiceDetail = orderInvoiceItems.get(k);
                                    
                                    SpecialInvoiceReversalItem specialInvoiceReversalItem = new SpecialInvoiceReversalItem();
                                    specialInvoiceReversalItem
                                            .setSpecialInvoiceReversalId(specialInvoiceReversalId);
                                    specialInvoiceReversalItem.setSpbm(invoiceDetail.getSPBM());
                                    specialInvoiceReversalItem.setXmmc(invoiceDetail.getXMMC());
                                    specialInvoiceReversalItem.setGgxh(invoiceDetail.getGGXH());
                                    specialInvoiceReversalItem.setXmdw(invoiceDetail.getDW());
                                    specialInvoiceReversalItem
                                            .setXmsl(String.valueOf(invoiceDetail.getXMSL()));
                                    specialInvoiceReversalItem
                                            .setXmdj(String.valueOf(invoiceDetail.getXMDJ()));
                                    specialInvoiceReversalItem
                                            .setXmje(String.valueOf(invoiceDetail.getXMJE()));
                                    specialInvoiceReversalItem
                                            .setSl(String.valueOf(invoiceDetail.getSL()));
                                    specialInvoiceReversalItem
                                            .setSe(String.valueOf(invoiceDetail.getSE()));
                                    specialInvoiceReversalItem.setHsbz(invoiceDetail.getHSBZ());
                                    specialInvoiceReversalItem
                                            .setYhzcbs(StringUtils.isNotBlank(invoiceDetail.getYHZCBS())
                                                    ? invoiceDetail.getYHZCBS()
                                                    : OrderInfoEnum.YHZCBS_0.getKey());
                                    specialInvoiceReversalItem
                                            .setLslbs(StringUtils.isNotBlank(invoiceDetail.getLSLBS())
                                                    ? invoiceDetail.getLSLBS() : "");
                                    specialInvoiceReversalItem.setSphxh(String.valueOf(k + 1));
                                    isSuccess = isSuccess
                                            & addSpecialInvoiceReversalItem(specialInvoiceReversalItem);
                                    if (isSuccess) {
                                        log.info("红字信息保存成功");
                                    }
                                    if (k == 0) {
                                        taxRate = String.valueOf(invoiceDetail.getSL());
                                    } else if (!taxRate.equals(invoiceDetail.getSL())) {
                                        taxRate = "多税率";
                                    }
                                }
                                /**
                                 * 更新红字申请单下载表状态为成功
                                 */
                                apiSpecialInvoiceReversalService.updateDownloadRedInvoiceStatus(specialInvoiceReversal.getNsrsbh(), specialInvoiceReversal.getSqdscqqlsh(), OrderInfoEnum.SPECIAL_INVOICE_DOWNLOAD_TYPE_2.getKey());
                                if (isSuccess) {
                                    specialInvoiceReversal = new SpecialInvoiceReversalEntity();
                                    specialInvoiceReversal.setId(specialInvoiceReversalId);
                                    specialInvoiceReversal.setDslbz(taxRate);
                                    isSuccess = editSpecialInvoiceReversal(specialInvoiceReversal);
                                    if (isSuccess) {
                                        log.info("红字申请单更新税率成功");
                                    }
                                    
                                }
                            }
                        } else {
                            //数据库存在
                            if (ConfigureConstant.STRING_0.equals(head.getSTATUS_CODE())) {
                                //审核成功
                                specialInvoiceReversal.setStatusCode("TZD0000");
                                if (StringUtils.isNotEmpty(r.getRED_INVOICE_FORM_DOWN_HEAD().getXXBBH())) {
                                    specialInvoiceReversal.setXxbbh(r.getRED_INVOICE_FORM_DOWN_HEAD().getXXBBH());
                                }
                            } else {
                                //审核失败
                                isPush = true;
                                specialInvoiceReversal.setStatusCode("TZD9999");
                            }
                            specialInvoiceReversal.setStatusMessage(head.getSTATUS_MESSAGE());
                            specialInvoiceReversal.setCreatorId(operatorId);
                            specialInvoiceReversal.setCreatorName(operatorName);
                            specialInvoiceReversal.setEditorId(operatorId);
                            specialInvoiceReversal.setEditorName(operatorName);
                            apiFangGeInterfaceService.updateUploadRedInvoice(specialInvoiceReversal);
                            /**
                             * 更新红字申请单下载表状态为成功
                             */
                            apiSpecialInvoiceReversalService.updateDownloadRedInvoiceStatus(specialInvoiceReversal.getNsrsbh(), specialInvoiceReversal.getSqdscqqlsh(), OrderInfoEnum.SPECIAL_INVOICE_DOWNLOAD_TYPE_2.getKey());
                            
                        }
                        
                        //下载回推
                        //数据放到放入下载推送队列
                        if (isPush) {
                            InvoicePush invoicePush = new InvoicePush();
                            invoicePush.setNSRSBH(nsrsbh);
                            invoicePush.setSQBSCQQLSH(head.getSQDH());
                            log.info("{},invociePush:{}", LOGGER_MSG + ": 下载回推信息", JsonUtils.getInstance().toJsonString(invoicePush));
                            apiPushService.putDataInQueue(invoicePush, nsrsbh, NsrQueueEnum.UPLOAD_DOWNLOAD_MESSAGE.getValue());
                        }
                    }
                }
                //处理redis里mqtt消息
                handleRedisMsg(nsrsbh, jqbh);
            } else if (ConfigureConstant.STRING_1.equals(paramContent.getSTATUS_CODE()) || ConfigureConstant.STRING_2.equals(paramContent.getSTATUS_CODE())) {
                //未插盘或者下载失败
                log.info("红票申请单下载失败，申请单流水号是：{}", paramContent.getSQBXZQQPCH());
                SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(paramContent.getSQBXZQQPCH());
                //修改数据状态为待下载
                specialInvoiceReversalEntity.setXzfgStatus(ConfigureConstant.STRING_2);
                specialInvoiceReversalEntity.setStatusCode("TZD9999");
                specialInvoiceReversalEntity.setStatusMessage("审核失败");
                apiFangGeInterfaceService.updateUploadRedInvoice(specialInvoiceReversalEntity);
                /**
                 * 更新红字申请单下载表状态为成功
                 */
                apiSpecialInvoiceReversalService.updateDownloadRedInvoiceStatus(specialInvoiceReversalEntity.getNsrsbh(), specialInvoiceReversalEntity.getSqdscqqlsh(), OrderInfoEnum.SPECIAL_INVOICE_DOWNLOAD_TYPE_2.getKey());
                
                /**
                 * 处理redis里面的mqtt消息
                 */
                handleRedisMsg(nsrsbh, jqbh);
                
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(paramContent.getSTATUS_MESSAGE());
            }
            
            
        } catch (Exception e) {
            //处理redis里面的mqtt消息
            handleRedisMsg(nsrsbh, jqbh);
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    public Boolean addSpecialInvoiceReversalItem(SpecialInvoiceReversalItem specialInvoiceReversalItem) {
        Boolean isSuccess = false;
        
        if (StringUtils.isBlank(specialInvoiceReversalItem.getYhzcbs())) {
            specialInvoiceReversalItem.setYhzcbs(OrderInfoEnum.YHZCBS_0.getKey());
        }
        specialInvoiceReversalItem.setId(apiInvoiceCommonService.getGenerateShotKey());
        specialInvoiceReversalItem.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
        
        int addResult = apiSpecialInvoiceReversalService.insertSpecialInvoiceReversalItem(specialInvoiceReversalItem);
        if (addResult > 0) {
            isSuccess = true;
        }
        return isSuccess;
    }
    
    
    public Boolean editSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
        Boolean isSuccess = false;
        
        specialInvoiceReversal.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
        Integer editResult = apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversal);
        if (editResult > 0) {
            isSuccess = true;
        }
        return isSuccess;
    }
    
    public Boolean addSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal) {
        Boolean isSuccess = false;
        
        if (StringUtils.isBlank(specialInvoiceReversal.getId())) {
            specialInvoiceReversal.setId(apiInvoiceCommonService.getGenerateShotKey());
        }
        if (StringUtils.isBlank(specialInvoiceReversal.getStatusCode())) {
            specialInvoiceReversal.setStatusCode("TZD0500");
        }
        specialInvoiceReversal.setKpzt(ConfigureConstant.STRING_0);
        specialInvoiceReversal.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
        specialInvoiceReversal.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
        
        int addResult = apiSpecialInvoiceReversalService.insertSpecialInvoiceReversal(specialInvoiceReversal);
        if (addResult > 0) {
            isSuccess = true;
        }
        return isSuccess;
    }
    
    private String convertReason(String reason) {
        String result = reason;
        switch (reason) {
            case "Y":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey();
                break;
            case "N1":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
                break;
            case "N2":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
                break;
            case "N3":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
                break;
            case "N4":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey();
                break;
            case "N5":
                result = OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + reason);
        }
        
        return result;
    }
    
    /**
     * 获取待作废发票数据
     */
    @Override
    public FG_ORDER_RESPONSE getDeprecateInvoices(FG_GET_INVOICE_ZF_REQ paramContent) {
        log.debug("[{}发票作废接口，参数为[{}]", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramContent));
        FG_ORDER_RESPONSE<FG_INVALID_INVOICE_RSP> fgInvalidInvoiceRspfgOrderResponse = new FG_ORDER_RESPONSE<FG_INVALID_INVOICE_RSP>();
        fgInvalidInvoiceRspfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgInvalidInvoiceRspfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        
        FG_INVALID_INVOICE_RSP fgInvalidInvoiceRsp = new FG_INVALID_INVOICE_RSP();
        
        //查询注册码
        fgInvalidInvoiceRsp.setZFPCH(paramContent.getZFPCH());
        fgInvalidInvoiceRsp.setNSRSBH(paramContent.getNSRSBH());
        List<FG_INVALID_INVOICE_INFOS> list = new ArrayList<FG_INVALID_INVOICE_INFOS>();
        try {
            //判断参数
            if (paramContent == null) {
                fgInvalidInvoiceRspfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgInvalidInvoiceRspfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]作废数据参数为空，参数[{}]", LOGGER_MSG, paramContent);
                return fgInvalidInvoiceRspfgOrderResponse;
            }
            //查询待作废的数据
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(paramContent.getNSRSBH());
            List<InvalidInvoiceInfo> invalidInvoiceInfo = apiInvalidInvoiceService.selectInvalidInvoiceInfo(paramContent.getZFPCH(), shList);
            if (ObjectUtils.isEmpty(invalidInvoiceInfo)) {
                fgInvalidInvoiceRspfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.FG_INVOICE_VALID_ENPTY.getKey());
                fgInvalidInvoiceRspfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.FG_INVOICE_VALID_ENPTY.getMessage());
                log.error("[{}]没有作废发票", LOGGER_MSG, paramContent);
                return fgInvalidInvoiceRspfgOrderResponse;
            }
            String jqbh = null;
            for (InvalidInvoiceInfo invalidInvoiceInfo1 : invalidInvoiceInfo) {
                FG_INVALID_INVOICE_INFOS infos = new FG_INVALID_INVOICE_INFOS();
                infos.setFP_DM(invalidInvoiceInfo1.getFpdm());
                infos.setFP_HM(invalidInvoiceInfo1.getFphm());
                infos.setQMCS("");
                infos.setZFR(invalidInvoiceInfo1.getZfr());
                /**
                 * 方格底层支持持作废类型,0空白,1已开
                 */
                if (StringUtils.isNotBlank(invalidInvoiceInfo1.getZflx()) && !OrderInfoEnum.ZFLX_0.getKey().equals(invalidInvoiceInfo1.getZflx())) {
                    infos.setZFLX(OrderInfoEnum.INVOICE_VALID_ZFLX_1.getKey());
                } else {
                    infos.setZFLX(OrderInfoEnum.INVOICE_VALID_ZFLX_0.getKey());
                }
                infos.setFPZLDM(invalidInvoiceInfo1.getFplx());
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(invalidInvoiceInfo1.getFpdm(), invalidInvoiceInfo1.getFphm(), NsrsbhUtils.transShListByNsrsbh(invalidInvoiceInfo1.getXhfNsrsbh()));
                if (!ObjectUtils.isEmpty(orderInvoiceInfo)) {
                    jqbh = orderInvoiceInfo.getJqbh();
                    infos.setHJJE(orderInvoiceInfo.getHjbhsje());
                } else {
                    jqbh = invalidInvoiceInfo1.getSld();
                }
                list.add(infos);
            }
            if (StringUtils.isNotBlank(jqbh)) {
                fgInvalidInvoiceRsp.setJQBH(jqbh);
                //redis获取注册码
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(paramContent.getNSRSBH(), jqbh);
                if (StringUtils.isNotEmpty(registCodeStr)) {
                    RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                    fgInvalidInvoiceRsp.setZCM(registrationCode == null ? "" : registrationCode.getZcm());
                }
            }
            
            fgInvalidInvoiceRsp.setINVALID_INVOICE_INFOS(list);
            fgInvalidInvoiceRspfgOrderResponse.setData(fgInvalidInvoiceRsp);
            log.info("{},待作废数据获取成功", LOGGER_MSG);
        } catch (Exception e) {
            fgInvalidInvoiceRspfgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgInvalidInvoiceRspfgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgInvalidInvoiceRspfgOrderResponse;
    }
    
    /**
     * 发票作废修改状态
     */
    @Override
    public FG_ORDER_RESPONSE getDeprecateInvoicesStatus(FG_GET_INVOICE_INVALID_STATUS_REQ paramContent) {
        log.debug("[{}发票作废更新状态接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]发票作废更新状态接口请求参数为空", LOGGER_MSG);
                return fgOrderResponse;
            }
            if (StringUtils.isNotEmpty(paramContent.getZFPCH()) && StringUtils.isNotEmpty(paramContent.getNSRSBH()) && StringUtils.isNotEmpty(paramContent.getSJZT())) {
                //拔盘
                //更新数据库状态
                List<String> shList = NsrsbhUtils.transShListByNsrsbh(paramContent.getNSRSBH());
                InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
                invalidInvoiceInfo.setZfpch(paramContent.getZFPCH());
                invalidInvoiceInfo.setFgStatus(paramContent.getSJZT());
                int i = apiInvalidInvoiceService.updateFgInvalidInvoice(invalidInvoiceInfo, shList);
            } else {
                log.info("发票作废更新状态接口请求参数为空，{}", paramContent);
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            }
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        
        return fgOrderResponse;
    }
    
    /**
     * 发票作废完成修改状态
     */
    @Override
    public FG_ORDER_RESPONSE updateDeprecateInvoices(FG_INVALID_INVOICE_FINISH_REQ fgInvalidInvoiceFinishReq) {
        log.debug("[{}发票作废完成接口，参数为[{}]", LOGGER_MSG, fgInvalidInvoiceFinishReq);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = fgInvalidInvoiceFinishReq.getNSRSBH();
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(nsrsbh);
        String jqbh = "";
        try {
            //判断参数
            if (fgInvalidInvoiceFinishReq == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]发票作废完成接口请求参数为空，参数[{}]", LOGGER_MSG, fgInvalidInvoiceFinishReq);
                return fgOrderResponse;
            }
            List<FG_INVALID_INVOICE_FINISH_INFOS> list = fgInvalidInvoiceFinishReq.getINVALID_INVOICE_INFOS();
            if (!ConfigureConstant.STRING_2.equals(fgInvalidInvoiceFinishReq.getSTATUS_CODE())) {
                //获取数据成功
                for (FG_INVALID_INVOICE_FINISH_INFOS info : list) {
    
                    /**
                     * 查询作废表数据,判断作废数据是否存在
                     */
                    InvalidInvoiceInfo invalidInvoiceInfoReq = new InvalidInvoiceInfo();
                    invalidInvoiceInfoReq.setZfpch(fgInvalidInvoiceFinishReq.getZFPCH());
                    invalidInvoiceInfoReq.setFpdm(info.getFP_DM());
                    invalidInvoiceInfoReq.setFphm(info.getFP_HM());
                    InvalidInvoiceInfo invalidInvoiceInfo1 = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(invalidInvoiceInfoReq, shList);
                    if (invalidInvoiceInfo1 == null) {
                        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getKey());
                        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getMessage());
                        log.error("[{}]对应的发票数据不存在，参数[{}]", LOGGER_MSG, fgInvalidInvoiceFinishReq);
                        return fgOrderResponse;
                    }
                    OrderInvoiceInfo invoice = new OrderInvoiceInfo();
                    boolean updateInvoice = false;
                    if (OrderInfoEnum.INVOICE_VALID_ZFLX_0.getKey().equals(invalidInvoiceInfo1.getZflx())) {
        
        
                        jqbh = invalidInvoiceInfo1.getSld();
                    } else {
                        /**
                         * 根据发票代码号码查询发票表
                         */
                        OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(info.getFP_DM(), info.getFP_HM(), shList);
                        if (orderInvoiceInfo == null) {
                            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getKey());
                            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getMessage());
                            log.error("[{}]对应的发票数据不存在，参数[{}]", LOGGER_MSG, fgInvalidInvoiceFinishReq);
                            return fgOrderResponse;
                        }
                        invoice.setId(orderInvoiceInfo.getId());
                        invoice.setId(orderInvoiceInfo.getId());
                        invoice.setKplx(orderInvoiceInfo.getKplx());
                        invoice.setFpzlDm(orderInvoiceInfo.getFpzlDm());
                        invoice.setHzxxbbh(orderInvoiceInfo.getHzxxbbh());
                        invoice.setOrderInfoId(orderInvoiceInfo.getOrderInfoId());
                        jqbh = orderInvoiceInfo.getJqbh();
                        updateInvoice = true;
                    }
    
    
                    if (ConfigureConstant.STRING_0.equals(info.getZFZT())) {
                        //发票作废成功
                        /**
                         * 更新作废表状态
                         */
                        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
                        invalidInvoiceInfo.setZfpch(fgInvalidInvoiceFinishReq.getZFPCH());
                        invalidInvoiceInfo.setFpdm(info.getFP_DM());
                        invalidInvoiceInfo.setFphm(info.getFP_HM());
                        invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
                        apiInvalidInvoiceService.updateFgInvalidInvoice(invalidInvoiceInfo, shList);
                        if (updateInvoice) {
                            /**
                             * 更新发票表状态
                             */
                            invoice.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
                            int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(invoice, shList);
                            if (updateByPrimaryKeySelect <= 0) {
                                log.error("更新发票表作废标志失败");
                            }
                            //判断作废的发票是否是红票
                            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(invoice.getKplx())) {
                                log.info("作废成功，红票更新作废金额");
                                //更新发票表的数据为已作废
                                invalidInvoiceService.updateSykchJe(invoice, shList);
                            }
                        }
        
                        //发票数据放入发票推送队列
                        invalidInvoiceInfo.setXhfNsrsbh(nsrsbh);
                        INVALID_INVOICES_RSP invalidInvoicesRsp = buildInvalidPushData(invalidInvoiceInfo);
                        String jsonString = JsonUtils.getInstance().toJsonString(invalidInvoicesRsp);
                        log.info("作废发票数据推送信息{}", jsonString);
                        apiInvalidInvoiceService.invalidInvoice(jsonString, nsrsbh);
                    } else if (ConfigureConstant.STRING_1.equals(info.getZFZT())) {
                        //发票作废失败
                        /**
                         * 更新作废表状态
                         */
                        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
                        invalidInvoiceInfo.setZfpch(fgInvalidInvoiceFinishReq.getZFPCH());
                        invalidInvoiceInfo.setFpdm(info.getFP_DM());
                        invalidInvoiceInfo.setFphm(info.getFP_HM());
                        invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
                        apiInvalidInvoiceService.updateFgInvalidInvoice(invalidInvoiceInfo, shList);
                        if (updateInvoice) {
                            /**
                             * 更新发票表状态
                             */
                            invoice.setZfBz(OrderInfoEnum.INVALID_INVOICE_3.getKey());
                            int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(invoice, shList);
                            if (updateByPrimaryKeySelect <= 0) {
                                log.error("更新发票表作废标志失败");
                            }
                        }
        
                    }
                    
                    
                }
            } else {
                //未插盘  修改为批次下的所有数据为作废失败，页面重新发起作废
                /**
                 * 更新作废表作废标志
                 */
                InvalidInvoiceInfo invalid = new InvalidInvoiceInfo();
                invalid.setZfpch(fgInvalidInvoiceFinishReq.getZFPCH());
                invalid.setZfBz(OrderInfoEnum.INVALID_INVOICE_3.getKey());
                apiInvalidInvoiceService.updateFgInvalidInvoice(invalid, shList);
                List<InvalidInvoiceInfo> invalidInvoiceInfo = apiInvalidInvoiceService.selectInvalidInvoiceInfo(fgInvalidInvoiceFinishReq.getZFPCH(), shList);
                for (InvalidInvoiceInfo info : invalidInvoiceInfo) {
                    
                    /**
                     * 更新发票表作废标志
                     */
                    OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(info.getFpdm(), info.getFphm(), shList);
                    jqbh = orderInvoiceInfo.getJqbh();
                    OrderInvoiceInfo invoice = new OrderInvoiceInfo();
                    invoice.setId(orderInvoiceInfo.getId());
                    //作废失败
                    invoice.setZfBz(OrderInfoEnum.INVALID_INVOICE_3.getKey());
                    int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(invoice, shList);
                    if (updateByPrimaryKeySelect <= 0) {
                        log.error("更新发票表作废标志失败");
                    }
                    
                }
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(fgInvalidInvoiceFinishReq.getSTATUS_MESSAGE());
            }
            
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        //处理redis里面的mqtt消息
        handleRedisMsg(nsrsbh, jqbh);
        return fgOrderResponse;
    }
    
    /**
     * 获取发票待打印的数据
     */
    @Override
    public FG_ORDER_RESPONSE getPrintInvoices(FG_INVOICE_PRING_REQ paramContent) {
        log.debug("[{}发票待打印接口，参数为[{}]", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramContent));
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]发票作废更新状态接口请求参数为空，参数[{}]", LOGGER_MSG, paramContent);
                return fgOrderResponse;
            }
            FG_INVOICE_PRINT_RSP fgInvoicePrintRsp = new FG_INVOICE_PRINT_RSP();
            FgInvoicePrintDto fgInvoicePrintDto = apiFangGeInterfaceService.getPrintInvoices(paramContent.getDYPCH(), paramContent.getNSRSBH());
            if (ObjectUtils.isEmpty(fgInvoicePrintDto)) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.FG_INVOICE_PRINT_EMPTY.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.FG_INVOICE_PRINT_EMPTY.getMessage());
                log.error("[{}]没有打印发票", LOGGER_MSG, paramContent);
                return fgOrderResponse;
            }
            fgInvoicePrintRsp.setDYPCH(fgInvoicePrintDto.getDypch());
            fgInvoicePrintRsp.setFP_DM(fgInvoicePrintDto.getFpdm());
            fgInvoicePrintRsp.setNSRSBH(paramContent.getNSRSBH());
            fgInvoicePrintRsp.setFPQH(fgInvoicePrintDto.getFpqh());
            fgInvoicePrintRsp.setFPZH(fgInvoicePrintDto.getFpzh());
            fgInvoicePrintRsp.setFPZLDM(fgInvoicePrintDto.getFpzldm());
            fgInvoicePrintRsp.setDYLX(fgInvoicePrintDto.getDylx());
            fgInvoicePrintRsp.setDYJMC(fgInvoicePrintDto.getDyjmc());
            if (StringUtils.isNotEmpty(fgInvoicePrintDto.getDylx())) {
                if ("fp".equals(fgInvoicePrintDto.getDylx())) {
                    fgInvoicePrintRsp.setDYLX(ConfigureConstant.STRING_0);
                } else if ("qd".equals(fgInvoicePrintDto.getDylx())) {
                    fgInvoicePrintRsp.setDYLX(ConfigureConstant.STRING_1);
                }
            }
            fgInvoicePrintRsp.setDYDBS(fgInvoicePrintDto.getDydbs());
            fgInvoicePrintRsp.setZPY(StringUtils.isNotEmpty(fgInvoicePrintDto.getZpy()) ? fgInvoicePrintDto.getZpy() : "");
            fgInvoicePrintRsp.setSPY(StringUtils.isNotEmpty(fgInvoicePrintDto.getSpy()) ? fgInvoicePrintDto.getSpy() : "");
            fgInvoicePrintRsp.setDYJMC(StringUtils.isNotEmpty(fgInvoicePrintDto.getDyjmc()) ? fgInvoicePrintDto.getDyjmc() : "");
            //获取机器编号
            OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(fgInvoicePrintDto.getFpdm(), fgInvoicePrintDto.getFpqh(), NsrsbhUtils.transShListByNsrsbh(paramContent.getNSRSBH()));
            if (!ObjectUtils.isEmpty(orderInvoiceInfo)) {
                fgInvoicePrintRsp.setJQBH(orderInvoiceInfo.getJqbh());
                //redis获取注册码
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(paramContent.getNSRSBH(), orderInvoiceInfo.getJqbh());
                if (StringUtils.isNotEmpty(registCodeStr)) {
                    RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                    fgInvoicePrintRsp.setZCM(registrationCode == null ? "" : registrationCode.getZcm());
                }
            }
            fgOrderResponse.setData(fgInvoicePrintRsp);
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 发票打印状态更新
     */
    @Override
    public FG_ORDER_RESPONSE getPrintInvoicesStatus(FG_INVOICE_PRING_STATUS_REQ paramContent) {
        log.debug("[{}发票打印更新状态接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]发票打印更新状态接口请求参数为空，参数[{}]", LOGGER_MSG, paramContent);
                return fgOrderResponse;
            }
            //更新数据库状态
            apiFangGeInterfaceService.updatePrintInvoicesStatus(paramContent.getDYPCH(), paramContent.getNSRSBH(), paramContent.getSJZT());
            
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 打印完成接口
     */
    @Override
    public FG_ORDER_RESPONSE updatePrintInvoices(FG_INVOICE_PRING_FINISH_REQ paramContent) {
        log.debug("[{}发票打印完成接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        String nsrsbh = paramContent.getNSRSBH(), jqbh = "";
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(nsrsbh);
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL.getMessage());
                log.error("[{}]发票打印更新状态接口请求参数为空，参数[{}]", LOGGER_MSG, paramContent);
                return fgOrderResponse;
            }
            InvoicePrintInfo invoicePrintInfo = new InvoicePrintInfo();
            invoicePrintInfo.setFpqqlsh(paramContent.getDYPCH());
            invoicePrintInfo.setXhfNsrsbh(paramContent.getNSRSBH());
            if (ConfigureConstant.STRING_0.equals(paramContent.getDYJG())) {
                //打印成功
                invoicePrintInfo.setPrintStatus(OrderInfoEnum.INVOICE_PRINT_STATUS_2.getKey());
                invoicePrintInfo.setPrintMsg(OrderInfoContentEnum.INVOICE_PRINT_SUCCESS.getMessage());
            } else if (ConfigureConstant.STRING_1.equals(paramContent.getDYJG()) || ConfigureConstant.STRING_2.equals(paramContent.getDYJG())) {
                //打印失败或者盘断了
                invoicePrintInfo.setFgStatus(ConfigureConstant.STRING_2);
                invoicePrintInfo.setPrintStatus(OrderInfoEnum.INVOICE_PRINT_STATUS_3.getKey());
                invoicePrintInfo.setPrintMsg(OrderInfoContentEnum.INVOICE_PRINT_FAIL.getMessage());
            }
            //更新打印表里面的状态
            apiFangGeInterfaceService.updateFgPrintInvoice(invoicePrintInfo, shList);
            log.info("更新打印表数据成功");
            /**
             * 更新发票表里的数据状态
             */
            //根据税号和发票请求流水号查询打印的发票信息
            List<InvoicePrintInfo> list = apiFangGeInterfaceService.getPrintInvoicesList(paramContent.getDYPCH(), paramContent.getNSRSBH());
            for (InvoicePrintInfo printInfo : list) {
                OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(printInfo.getFpqqlsh(), NsrsbhUtils.transShListByNsrsbh(printInfo.getXhfNsrsbh()));
                if (!ObjectUtils.isEmpty(orderInvoiceInfo)) {
                    if (ConfigureConstant.STRING_0.equals(paramContent.getDYJG())) {
                        //打印成功
                        orderInvoiceInfo.setDyzt(ConfigureConstant.STRING_1);
                    } else {
                        orderInvoiceInfo.setDyzt(ConfigureConstant.STRING_0);
                    }
                    apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, NsrsbhUtils.transShListByNsrsbh(printInfo.getXhfNsrsbh()));
                }
                log.info("测试开票时间-打印完成：{},发票请求流水号：{}", DateUtil.now(), paramContent.getDYPCH());
                //打印回推
                //发票数据放入发票推送队列
                InvoicePush invoicePush = new InvoicePush();
                invoicePush.setNSRSBH(paramContent.getNSRSBH());
                invoicePush.setFPQQLSH(printInfo.getFpqqlsh());
                log.info("{},invociePush:{}", LOGGER_MSG + ": 打印回推信息", JsonUtils.getInstance().toJsonString(invoicePush));
                apiPushService.putDataInQueue(invoicePush, paramContent.getNSRSBH(), NsrQueueEnum.PRINT_MESSAGE.getValue());
            }
            
            
            /**
             * 获取待打印订单数据
             */
            OrderInvoiceInfo info = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(paramContent.getDYPCH(), NsrsbhUtils.transShListByNsrsbh(paramContent.getNSRSBH()));
            if (!ObjectUtils.isEmpty(info)) {
                jqbh = info.getJqbh();
                //处理redis里面的mqtt消息
                handleRedisMsg(nsrsbh, jqbh);
            }
        } catch (Exception e) {
            //处理redis里面的mqtt消息
            handleRedisMsg(nsrsbh, jqbh);
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    @Override
    public FG_ORDER_RESPONSE registTaxDisk(String paramContent) {
        log.debug("[{}税盘注册接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_REGIST_TAXDISK_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_REGIST_TAXDISK_NULL.getMessage());
                log.error("注册税盘参数为空，参数[{}]", paramContent);
                return fgOrderResponse;
            }
            
            // 调用dubbo接口,注册税盘信息
            SqZcxxParam sqZcxxParam = JsonUtils.getInstance().parseObject(paramContent, SqZcxxParam.class);
            /**
             * 此处可以写死为方格,因为需要区分方格航信,方格百望,方格UKey,调用底层不区分,统一为方格,因此默认为方格航信
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(sqZcxxParam.getNSRSBH());
            if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
                terminalCode = OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey();
            }
            String zcm = HttpInvoiceRequestUtilFg.sqZcxx(OpenApiConfig.sqZcxxFg, sqZcxxParam, terminalCode);
            log.info("调用底层接口注册税盘信息，返回数据为{}", zcm);
            if (StringUtils.isNotBlank(zcm)) {
                //注册成功，返回注册码
                
                log.info("税盘信息注册成功，参数[{}]", paramContent);
                Map<String, String> map = new HashMap<>(1);
                map.put("ZCM", zcm);
                fgOrderResponse.setData(map);
                
                //注册成功之后 保存注册码到数据库
                FG_REGIST_TAXDISK_REQ s = JsonUtils.getInstance().parseObject(paramContent, FG_REGIST_TAXDISK_REQ.class);
                //判断信息是否存在
                RegistrationCode code = apiRegistrationCodeService.getRegistrationCodeByNsrsbhAndJqbh(s.getNSRSBH(), s.getJQBH());
                if (ObjectUtils.isEmpty(code)) {
                    //添加
                    code = new RegistrationCode();
                    code.setXhfNsrsbh(s.getNSRSBH());
                    code.setJqbh(s.getJQBH());
                    code.setSplx(s.getZCLX());
                    code.setZcm(zcm);
                    code.setCreateTime(new Date());
                    code.setUpdateTime(new Date());
                    code.setId(apiInvoiceCommonService.getGenerateShotKey());
                    apiRegistrationCodeService.saveRegistrationCode(code);
                } else {
                    code.setZcm(zcm);
                    apiRegistrationCodeService.updateRegistrationCode(code);
                }
                
                /**
                 * 1、保存注册码到redis
                 */
                log.info("保存税盘信息到redis：{}", JsonUtils.getInstance().toJsonString(code));
                code.setSpzt(ConfigureConstant.STRING_0);
                apiFangGeInterfaceService.saveCodeToRedis(code);
                /**
                 * 2、存放同步税盘消息到redis队列
                 */
                PushPayload pushPayload = new PushPayload();
                //税盘信息同步
                pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_6);
                pushPayload.setNSRSBH(code.getXhfNsrsbh());
                pushPayload.setJQBH(code.getJqbh());
                pushPayload.setZCM(code.getZcm());
                apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                log.info("同步税盘信息消息存放redis队列成功,{}", JsonUtils.getInstance().toJsonString(pushPayload));
                
                /**
                 * 每次注册成功之后重置mqtt消息结果
                 */
                apiFangGeInterfaceService.updateMsgFlag(pushPayload.getNSRSBH(), pushPayload.getJQBH());
                
                /**
                 * 消费信息
                 */
                apiFangGeInterfaceService.pushMqttMsg(pushPayload.getNSRSBH(), pushPayload.getJQBH());
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_ZCM_SUCCESS.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_ZCM_SUCCESS.getMessage());
            } else {//注册失败
                log.info("税盘信息调用底层接口注册失败");
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_ZCM_FAIL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_ZCM_FAIL.getMessage());
            }
        } catch (Exception e) {
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
            log.error("{},系统异常{}", LOGGER_MSG, e);
        }
        return fgOrderResponse;
    }
    
    /**
     * 税盘信息同步
     */
    @Override
    public FG_ORDER_RESPONSE updateTaxDiskInfo(String paramContent) {
        log.debug("[{}税盘信息同步接口，参数为[{}]", LOGGER_MSG, paramContent);
        FG_ORDER_RESPONSE fgOrderResponse = new FG_ORDER_RESPONSE();
        fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        
        JSONObject parse = JSONObject.parseObject(paramContent);
        String nsrsbh = String.valueOf(parse.get("NSRSBH"));
        String jqbh = String.valueOf(parse.get("JQBH"));
        try {
            //判断参数
            if (paramContent == null) {
                fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.GET_UPDATE_TAXDISK_NULL.getKey());
                fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.GET_UPDATE_TAXDISK_NULL.getMessage());
                log.error("[{}]注册税盘参数为空，参数[{}]", LOGGER_MSG, paramContent);
                return fgOrderResponse;
            }
            String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
            if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
                terminalCode = OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey();
            }
            TbSpxxParam tbSpxxParam = JsonUtils.getInstance().parseObject(paramContent, TbSpxxParam.class);
            //   调用全税dubbo接口,同步税盘信息
            ResponseBaseBean result = HttpInvoiceRequestUtilFg.tbSpxx(OpenApiConfig.tbSpxxFg, tbSpxxParam, terminalCode);
            if (OrderInfoContentEnum.INVOICE_STAT_SUCCESS.getKey().equals(result.getCode())) {
                //同步信息成功
                log.info("同步税盘信息成功");
                fgOrderResponse.setSTATUS_CODE(result.getCode());
                fgOrderResponse.setSTATUS_MESSAGE(result.getMsg());
            } else {//同步失败
                log.info("同步税盘信息失败,错误信息:{}", result);
                fgOrderResponse.setSTATUS_CODE(result.getCode());
                fgOrderResponse.setSTATUS_MESSAGE(result.getMsg());
            }
            
            if (ConfigureConstant.STRING_1.equals(String.valueOf(parse.get("LASTFLAG")))) {
                //最后一条数据标识
                handleRedisMsg(nsrsbh, jqbh);
            }
        } catch (Exception e) {
            //异常信息处理，需要更新数据获取状态为可取并消费下一条数据，否则数据一直是不可取的状态
            log.error("{},程序异常，重新更新redis数据状态,税号：{},机器编号：{}", LOGGER_MSG, nsrsbh, jqbh);
            handleRedisMsg(nsrsbh, jqbh);
            fgOrderResponse.setSTATUS_CODE(OrderInfoContentEnum.ERROR.getKey());
            fgOrderResponse.setSTATUS_MESSAGE(OrderInfoContentEnum.ERROR.getMessage());
        }
        return fgOrderResponse;
    }
    
    public void handleRedisMsg(String nsrsbh, String jqbh) {
        log.info("修改redis里面的数据状态为可取，纳税人识别号:{},机器编号：{}", nsrsbh, jqbh);
        //更新rediskey为可取
        apiFangGeInterfaceService.updateMsgFlag(nsrsbh, jqbh);
        //消费下一条数据
        apiFangGeInterfaceService.pushMqttMsg(nsrsbh, jqbh);
    }
    
    /**
     * 构建作废推送数据
     *
     * @param invalidInvoiceInfo
     * @return
     */
    public INVALID_INVOICES_RSP buildInvalidPushData(InvalidInvoiceInfo invalidInvoiceInfo) {
        //已开发票作废推送参数组装
        List<INVALID_INVOICE_INFOS> invalidInvoiceInfos = new ArrayList<>();
        INVALID_INVOICES_RSP invalidInvoicesRsp = new INVALID_INVOICES_RSP();
        invalidInvoicesRsp.setZFPCH(invalidInvoiceInfo.getZfpch());
        invalidInvoicesRsp.setNSRSBH(invalidInvoiceInfo.getXhfNsrsbh());
        INVALID_INVOICE_INFOS invalidInvoiceInfos1 = new INVALID_INVOICE_INFOS();
        invalidInvoiceInfos1.setFP_DM(invalidInvoiceInfo.getFpdm());
        invalidInvoiceInfos1.setFP_HM(invalidInvoiceInfo.getFphm());
        invalidInvoiceInfos1.setZFLX(invalidInvoiceInfo.getZflx());
        invalidInvoiceInfos1.setZFYY(invalidInvoiceInfo.getZfyy());
        invalidInvoiceInfos1.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
        invalidInvoiceInfos1.setSTATUS_MESSAGE(OrderInfoContentEnum.SUCCESS.getMessage());
        invalidInvoiceInfos.add(invalidInvoiceInfos1);
        invalidInvoicesRsp.setINVALID_INVOICE_INFOS(invalidInvoiceInfos);
        return invalidInvoicesRsp;
    }
    
}
