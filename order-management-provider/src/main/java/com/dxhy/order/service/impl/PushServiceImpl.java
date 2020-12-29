package com.dxhy.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.*;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.*;
import com.dxhy.order.dao.InvoiceBatchRequestItemMapper;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.dao.PushInfoMapper;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.protocol.CommonReponse;
import com.dxhy.order.model.protocol.ResponseData;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.protocol.RESPONSE;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWNLOAD;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWN_HEAD;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.protocol.supplychain.SupplyChainBaseResponse;
import com.dxhy.order.protocol.supplychain.SupplyChainPushResponse;
import com.dxhy.order.protocol.v4.RESPONSEV4;
import com.dxhy.order.protocol.v4.invalid.ZFTSXX_REQ;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.service.webservice.WebServiceClient;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 推送企业数据servcie实现类
 *
 * @author ：杨士勇
 * @ClassName ：PushServiceImpl
 * @Description ：
 * @date ：2018年10月16日 下午2:43:05
 */
@Service
@Slf4j
public class PushServiceImpl implements ApiPushService {

    private static final String LOGGER_MSG = "(推送业务实现类)";

    /**
     * webservice 推送方法名
     */
    private static final String ws_fptx_method = "ZinvoicePush";
    private static final String ws_fpzf_method = "ZinvoiceDelete";
    private static final String ws_hzsqdcx_method = "ZinvoiceIssueReink";

    @Resource
    private ApiOrderInfoService orderInfoService;
    
    @Resource
    private ApiOrderProcessService apiOrderProcessService;
    
    @Resource
    private ApiOrderItemInfoService orderItemInfoService;
    
    @Resource
    private PushInfoMapper pushInfoMapper;
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    
    @Resource
    private InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
    
    @Resource
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;

    @Resource
    private ICommonDisposeService iCommonDisposeService;
    
    @Resource
    private ApiQuickCodeInfoService apiQuickCodeInfoService;

    @Resource
    private ValidateOrderInfo validateOrderInfo;

    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;

    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;

    @Resource
    private ApiHistoryDataPdfService apiHistoryDataPdfService;


    @Override
    public R pushRouting(String pushMsg){
        //解析传递过来的数据
        InvoicePush invoicePush = JsonUtils.getInstance().parseObject(pushMsg, InvoicePush.class);
        log.info("{}推送接口，入参:invoicePush:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoicePush));

        List<String> shList = new ArrayList<>();
        shList.add(invoicePush.getNSRSBH());

        //根据订单来源区分 内部服务之间的服务交互还是推送到企业 如果是红票的话需要查询原蓝票的订单来源
        OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(invoicePush.getFPQQLSH(), shList);
        if(orderProcessInfo == null){
            log.error("{}订单处理表信息不存在,fpqqlsh:{}",LOGGER_MSG,invoicePush.getFPQQLSH());
            return R.error().put(OrderManagementConstant.MESSAGE,"订单处理表信息不存在!");
        }
        String ddly = orderProcessInfo.getDdly();
        if(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(invoicePush.getFPLX())){
            OrderInfo order = orderInfoService.queryOrderInfoByFpqqlsh(invoicePush.getFPQQLSH(), shList);
            //查询原蓝票的信息
            OrderInvoiceInfo orderInvoiceInfo = null;
            if (StringUtils.isNotBlank(order.getYfpDm()) && StringUtils.isNotBlank(order.getYfpHm())) {
                orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(order.getYfpDm(), order.getYfpHm(), shList);
            }
            if (orderInvoiceInfo != null) {
                OrderProcessInfo lpProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(orderInvoiceInfo.getFpqqlsh(), shList);
                if (OrderInfoEnum.ORDER_SOURCE_8.getKey().equals(lpProcessInfo.getDdly())) {
                    ddly = lpProcessInfo.getDdly();
                    InvoiceBatchRequestItem invoiceBatchRequestItem = invoiceBatchRequestItemMapper.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                    invoicePush.setFPQQLSH(lpProcessInfo.getFpqqlsh());
                    invoicePush.setEWM(orderInvoiceInfo.getEwm());
                    invoicePush.setKPHJSE(Double.valueOf(orderInvoiceInfo.getKpse()));
                    invoicePush.setFJH(orderInvoiceInfo.getFjh());
                    invoicePush.setFP_DM(orderInvoiceInfo.getFpdm());
                    invoicePush.setFPLX(orderInvoiceInfo.getKplx());
                    invoicePush.setFP_HM(orderInvoiceInfo.getFphm());
                    invoicePush.setFPQQPCH(invoiceBatchRequestItem.getFpqqpch());
                }
            }
        }

        if(OrderInfoEnum.ORDER_SOURCE_8.getKey().equals(ddly)){
            //供应链数据推送根据配置的虚拟税号控制推送内容 公司內部系统间数据交互
            return pushToSupplyChain(invoicePush);
        }else{
            //推送发票数据到第三方企业
            return pushToEnterprise(invoicePush);
        }

    }

    
    public R pushToEnterprise(InvoicePush invoicePush) {

        /**
         *1.数据预处理,根据关键数据查询数据库中数据,组装数据
         *
         * 2.使用组装后的content,去调用接口预处理
         *
         * 3.接口返回数据处理.
         *
         */
        List<String> shList = new ArrayList<>();
        shList.add(invoicePush.getNSRSBH());
        try {
            if (StringUtils.isBlank(invoicePush.getFP_DM())) {
                log.warn("{}发票代码号码为空，此条开票数据不予推送，发票的流水号为：{}", LOGGER_MSG, invoicePush.getFPQQLSH());
                return R.ok();
            }

            List<PushInfo> pushInfoList = getPushUrlList(invoicePush.getNSRSBH(),OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_1.getKey(),"v4");
            if (pushInfoList.isEmpty()) {
                log.warn("{}税号:{},推送地址没有配置", LOGGER_MSG, invoicePush.getNSRSBH());
                return R.ok();
            }
            //多个地址循环推送
            for (PushInfo pushInfo : pushInfoList) {
                if (StringUtils.isBlank(pushInfo.getPushUrl())) {
                    log.warn("税号:{},推送地址未配置", invoicePush.getNSRSBH());
                    continue;
                } else if (!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_HTTP.getKey().equals(pushInfo.getProtocolType())
                        &&!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_WEBSERVICE.getKey().equals(pushInfo.getProtocolType())) {
                    log.error("税号:{}推送地址中接口协议类型只能为http或webservice!", pushInfo.getNsrsbh());
                    continue;
                }
                //参数组装
                String content = getPushContent(invoicePush, pushInfo.getVersionIdent(), shList);
                String result = push(pushInfo,ws_fptx_method, content);
                boolean isSuccess = false;
                try {
                    R r = parseResult(result, pushInfo.getNsrsbh(), pushInfo.getVersionIdent());
                    if (ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
                        isSuccess = true;
                    }
                    OrderInvoiceInfo orderQuery = new OrderInvoiceInfo();
                    /**
                     * 入参数据,前面已经转换
                     */
                    orderQuery.setFpqqlsh(invoicePush.getFPQQLSH());
                    if (isSuccess) {
                        log.info("==>推送返回解析成功,更新数据库表开始！");
                        orderQuery.setPushStatus(OrderInfoEnum.PUSH_STATUS_1.getKey());
                        int updateByPrimaryKeySelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderQuery, shList);
                        if (updateByPrimaryKeySelective <= 0) {
                            log.error("{}推送状态更新失败", LOGGER_MSG);
                        }
                    } else {
                        orderQuery.setPushStatus(OrderInfoEnum.PUSH_STATUS_2.getKey());
                        int updateByPrimaryKeySelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderQuery, shList);
                        if (updateByPrimaryKeySelective <= 0) {
                            log.error("{}推送状态更新失败", LOGGER_MSG);
                        }
                    }
                } catch (Exception e) {
                    /**
                     * 多路径推送,返回数据异常后继续推送
                     */
                    log.error("{}推送企业异常", LOGGER_MSG);
                }
            }
            return R.ok();
        } catch (Exception e) {
            log.error("推送异常，异常信息:{}",e);
            OrderInvoiceInfo orderQuery = new OrderInvoiceInfo();
            orderQuery.setFpqqlsh(invoicePush.getFPQQLSH());
            orderQuery.setPushStatus(OrderInfoEnum.PUSH_STATUS_2.getKey());
            int updateByPrimaryKeySelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderQuery, shList);
            if (updateByPrimaryKeySelective <= 0) {
                log.error("{}推送状态更新失败", LOGGER_MSG);
            }
            return R.error();
        }
    }

    private String push(PushInfo pushInfo,String mehtod, String content) {
        Map<String, String> requestMap = iCommonDisposeService.getRequestParameter(pushInfo.getNsrsbh(), pushInfo.getZipCode(), pushInfo.getEncryptCode(), content, pushInfo.getPushUrl(), pushInfo.getInterfaceType());
        log.info("{}订单推送企业开始，推送企业url：{},推送参数:{}", LOGGER_MSG, pushInfo.getPushUrl(), JsonUtils.getInstance().toJsonString(requestMap));
        String result = null;
        if (OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_HTTP.getKey().equals(pushInfo.getProtocolType())) {
            result = HttpUtils.doPost(pushInfo.getPushUrl(), requestMap);
        }
        if (OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_WEBSERVICE.getKey().equals(pushInfo.getProtocolType())) {
            //静态调用
//            result = WebServiceClient.clientForStaticProxy(pushInfo.getPushUrl(), pushInfo.getInterfaceType(), requestMap);
            //动态调用
//          result = WebServiceClient.clientForDynamicProxy(pushInfo.getPushUrl(), mehtod, requestMap);
            //axis 动态调用
            result = WebServiceClient.clientForAxis(pushInfo.getPushUrl(), mehtod, requestMap);
        }
        log.info("{}订单推送企业结束，企业返回结果:{}", LOGGER_MSG, result);
        return result;
    }

    private List<PushInfo> getPushUrlList(String nsrsbh, String interfaceType, String version) {
        PushInfo queryPushInfo = new PushInfo();
        queryPushInfo.setStatus(ConfigureConstant.STRING_0);
        queryPushInfo.setNsrsbh(nsrsbh);
        queryPushInfo.setVersionIdent(version);
        queryPushInfo.setInterfaceType(interfaceType);
        log.info("{}推送地址获取入参：{}", LOGGER_MSG,JsonUtils.getInstance().toJsonString(queryPushInfo));
        List<PushInfo> pushInfoList = pushInfoMapper.selectListByPushInfo(queryPushInfo);
        log.info("查询到的推送地址信息:{}", JsonUtils.getInstance().toJsonString(pushInfoList));
        return pushInfoList;
    }


    /**
     * 推送开具完成的订单信息到供应链
     * @param invoicePush
     * @return
     */
    public R pushToSupplyChain(InvoicePush invoicePush){

        return pushCompleteOrder(invoicePush,OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_1.getKey());

    }


    /**
     * 组装推送content
     *
     * @param invoicePush
     * @param version
     * @return
     */
    public String getPushContent(InvoicePush invoicePush, String version, List<String> shList) {
        String content = "";

        OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(invoicePush.getFPQQLSH(), shList);
        log.info("{}推送接口orderProcessInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderProcessInfo));

        OrderInfo orderInfo = orderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
        log.info("{}推送接口orderInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfo));

        List<OrderItemInfo> orderItemInfos = orderItemInfoService.selectOrderItemInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
        List<DDMXXX> ddmxxxList = BeanTransitionUtils.transitionORDER_INVOICE_ITEMV3(orderItemInfos);
        List<ORDER_INVOICE_ITEM> orderInvoiceItems = BeanTransitionUtils.transitionORDER_INVOICE_ITEM(orderItemInfos);

        OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
        orderInvoiceInfo1.setFpqqlsh(orderProcessInfo.getFpqqlsh());
        OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
        log.info("{}推送接口orderInvoiceInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));

        List<OrderProcessInfo> orderProcessInfoRelevantList = apiOrderProcessService.findTopParentList(orderProcessInfo, shList);
        List<DDKZXX> orderExtensionInfos = BeanTransitionUtils.transitionORDER_EXTENSION_INFOS(orderProcessInfoRelevantList);
        log.info("{}推送接口orderExtensionInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderExtensionInfos));

        /**
         * 查询税控设备信息
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(orderProcessInfo.getXhfNsrsbh());

        //判断推送版本
        if (ConfigurerInfo.INTERFACE_VERSION_V4.equals(version) || ConfigurerInfo.INTERFACE_VERSION_V3.equals(version)) {
            //新版本请求参数组装response
            DDFPCX_RSP ddfpcxRsp = new DDFPCX_RSP();
            //新版本组装数据方式
            List<DDFPZXX> ddfpzxxes = new ArrayList<>();
            DDFPZXX ddfpzxx = new DDFPZXX();
            log.info("==>新版本接口，版本号:{}", version);
            DDFPXX ddfpxx = BeanTransitionUtils.transitionORDER_INVOICE_INFOV3(orderInfo, orderProcessInfo, orderInvoiceInfo);

            /**
             * 订单状态返回:
             * 根据当前订单状态进行返回,
             * 订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中;11.删除状态）
             *  对外状态有:
             *  000000:订单开票成功,001000:订单处理成功,001999:开票异常
             *  如果订单状态为0,1,2,3,为订单处理成功状态
             *  如果订单状态为4,9,10,为订单开票中状态.
             *  如果订单状态为5,7,为开票成功该状态
             *  如果订状态为6,8,为开票失败状态
             *  如果订单状态为11为订单删除状态
             */
            if (StringUtils.isNotBlank(orderProcessInfo.getDdzt())) {

                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getMessage());
                    /**
                     * 详细判断开票状态
                     * 优先判断冲红状态,返回对应冲红状态,然后判断作废状态
                     * 目前只返回全部冲红成功和部分冲红成功
                     * 目前只返回作废成功和作废失败.
                     */
                    if (StringUtils.isNotBlank(orderInvoiceInfo.getChBz())) {
                        if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoiceInfo.getChBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_004000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_004000.getMessage());
                        } else if (OrderInfoEnum.RED_INVOICE_4.getKey().equals(orderInvoiceInfo.getChBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_005000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_005000.getMessage());
                        }
                    }
                    if (StringUtils.isNotBlank(orderInvoiceInfo.getZfBz())) {
                        if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoiceInfo.getZfBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_003000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_003000.getMessage());
                        }
                    }
                } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getMessage());
                }
            }
            /**
             * 获取PDF判断
             */
            GetPdfResponseExtend getPdfResponseExtend = null;
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
    
                /**
                 * 方格UKey的电票调用monggodb获取数据
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    HistoryDataPdfEntity historyDataPdfEntity = apiHistoryDataPdfService.find(orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), NsrsbhUtils.transShListByNsrsbh(orderInvoiceInfo.getXhfNsrsbh()));
                    if (Objects.nonNull(historyDataPdfEntity)) {
                        ddfpxx.setPDFZJL(historyDataPdfEntity.getPdfFileData());
                    }
                } else {
                    GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(invoicePush.getFPQQPCH(), invoicePush.getNSRSBH(), terminalCode, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getPdfUrl());
        
                    getPdfResponseExtend = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
        
                    ddfpxx.setPDFZJL(getPdfResponseExtend.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
                }
    
    
            } else {
                ddfpxx.setPDFZJL("");
            }
    
            //补全动态码
            if (StringUtils.isNotBlank(ddfpxx.getTQM())) {
                QuickResponseCodeInfo quickResponseCodeInfo = apiQuickCodeInfoService.queryQrCodeDetailByTqm(ddfpxx.getTQM(), shList, null);
                if (quickResponseCodeInfo != null) {
                    ddfpxx.setTQM(String.format(OpenApiConfig.qrCodeShortUrl, quickResponseCodeInfo.getTqm()));
                }
            }
    
            /**
             * 发票种类代码处理,改为底层的004,007,026
             */
            String fpzldm = ddfpxx.getFPLXDM();
            if (StringUtils.isNotBlank(fpzldm)) {
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzldm)) {
                    ddfpxx.setFPLXDM(OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fpzldm)) {
                    ddfpxx.setFPLXDM(OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey());
                } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(fpzldm) || OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fpzldm)) {
                    ddfpxx.setFPLXDM(OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey());
                }
            }
    
            ddfpzxx.setDDFPXX(ddfpxx);
            ddfpzxx.setDDMXXX(ddmxxxList);
            //设置和当前订单相关的订单拆分合并关系协议beanV3
            ddfpzxx.setDDKZXX(orderExtensionInfos);
            ddfpzxxes.add(ddfpzxx);
            ddfpcxRsp.setZTDM(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getKey());
            ddfpcxRsp.setZTXX(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getMessage());
            ddfpcxRsp.setDDFPZXX(ddfpzxxes);
    
            if (ConfigurerInfo.INTERFACE_VERSION_V3.equals(version)) {
                ORDER_INVOICE_RESPONSE response1 = InterfaceBeanTransUtils.transDdfpcxRsp(ddfpcxRsp);
                content = JsonUtils.getInstance().toJsonString(response1);
            } else {
                content = JsonUtils.getInstance().toJsonString(ddfpcxRsp);
            }
            log.info("推送的参数：{}", content);
        } else {
            //旧版本请求参数组装response
            PUSH_RSP response = new PUSH_RSP();
            //旧版本组装方式
            List<COMMON_ORDER_INVOCIE> commonOrderInvocies = new ArrayList<>();
            COMMON_ORDER_INVOCIE commonOrderInvocie = new COMMON_ORDER_INVOCIE();
            log.info("==>推送旧的版本接口");
            ORDER_INVOICE_HEAD orderInvoiceHead = BeanTransitionUtils.transitionORDER_INVOICE_HEAD(orderInfo, orderInvoiceInfo);
            /**
             * 获取PDF判断
             */
            GetPdfResponseExtend getPdfResponseExtend = null;
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
    
                /**
                 * 方格UKey的电票调用monggodb获取数据
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    HistoryDataPdfEntity historyDataPdfEntity = apiHistoryDataPdfService.find(orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), NsrsbhUtils.transShListByNsrsbh(orderInvoiceInfo.getXhfNsrsbh()));
                    if (Objects.nonNull(historyDataPdfEntity)) {
                        orderInvoiceHead.setPDF_FILE(historyDataPdfEntity.getPdfFileData());
                    }
                } else {
                    GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(invoicePush.getFPQQPCH(), invoicePush.getNSRSBH(), terminalCode, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getPdfUrl());
        
                    getPdfResponseExtend = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
        
                    orderInvoiceHead.setPDF_FILE(getPdfResponseExtend.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
                }
    
    
            } else {
                orderInvoiceHead.setPDF_FILE("");
            }
            //组装
            commonOrderInvocie.setORDER_INVOICE_HEAD(orderInvoiceHead);
            commonOrderInvocie.setORDER_INVOICE_ITEMS(orderInvoiceItems);
            commonOrderInvocies.add(commonOrderInvocie);
    
            //发票开具状态码 0000 成功  9999 失败
            response.setSTATUS_CODE(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getKey());
            response.setSTATUS_MESSAGE(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getMessage());
            response.setCOMMON_ORDER_INVOCIE(commonOrderInvocies);
            log.info("推送的参数：{}", response);
            content = JsonUtils.getInstance().toJsonString(response);
        }
    
        return content;
    }
    
    /**
     * 邮件推送的获取最近一个月的数据
     *
     * @param shList
     */
    @Override
    public void pushInvoiceEmailMonthTask(List<String> shList) {
        List<String> kpzt = new ArrayList<>();
        kpzt.add("2");
        List<String> emailPushStatsus = new ArrayList<>();
        emailPushStatsus.add("0");
        emailPushStatsus.add("3");
        Date date = new Date();
        String startTime = new SimpleDateFormat(ConfigureConstant.DATE_FORMAT_DATE_Y_M_DH_M_S).format(date);
        Calendar c = Calendar.getInstance();
        //过去一月
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String endTime = new SimpleDateFormat(ConfigureConstant.DATE_FORMAT_DATE_Y_M_DH_M_S).format(m);
        List<OrderInvoiceInfo> selectInvoiceInfoByEmailPushStatus = orderInvoiceInfoMapper.selectInvoiceInfoByEmailPushStatus(kpzt, emailPushStatsus,
                startTime, endTime, OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey(), shList);
        
        convertAndSend(selectInvoiceInfoByEmailPushStatus, shList);
    }
    
    @Override
    public void putDataInQueue(InvoicePush invoicePush, String xhfNsrsbh, String queueName) {
        iRabbitMqSendMessage.autoSendRabbitMqMessage(xhfNsrsbh, queueName, JsonUtils.getInstance().toJsonString(invoicePush));
    }

    private void convertAndSend(List<OrderInvoiceInfo> selectInvoiceInfoByEmailPushStatus, List<String> shList) {
        for (OrderInvoiceInfo orderInvoiceInfo : selectInvoiceInfoByEmailPushStatus) {
            OrderInvoiceInfo updateInfo = new OrderInvoiceInfo();
            updateInfo.setId(orderInvoiceInfo.getId());
            updateInfo.setEmailPushStatus("1");
            orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(updateInfo, shList);
    
            Map<String, String> map = new HashMap<>(5);
    
            map.put(ConfigureConstant.INVOICE_ID, orderInvoiceInfo.getId());
    
            map.put(ConfigureConstant.SHLIST, JsonUtils.getInstance().toJsonString(shList));
            //定时任务的优先级高于自动推送的优先级 低于手动开票的优先级
            iRabbitMqSendMessage.autoSendRabbitMqMessage(orderInvoiceInfo.getXhfNsrsbh(), NsrQueueEnum.YXTS_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(map));
    
        }
	}


    /**
     * 组装补全订单推送信息
     * @return
     */
    public String getPushOrderContent(InvoicePush invoicePush,List<String> shList,String type) throws OrderReceiveException{
        String content = "";

        OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(invoicePush.getFPQQLSH(), shList);
        log.info("{}推送数据到供应链的数据,orderProcessInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderProcessInfo));

        if(OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_0.getKey().equals(type)){

            //待审核订单推送
            if(!OrderInfoEnum.ORDER_SOURCE_8.getKey().equals(orderProcessInfo.getDdly())){
                log.error("{}fpqqlsh:{}非供应链数据!",LOGGER_MSG,invoicePush.getFPQQLSH());
                throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD.getKey(),"非供应链数据不支持审核推送");
            }

            if(!(OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt()) ||
                    OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt()))) {
                log.error("{}fpqqlsh:{}已开具或者开具中的订单不能再审核!", LOGGER_MSG, invoicePush.getFPQQLSH());
                throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), "已开具或者开具中的订单不能再审核!");
            }
    
            OrderInfo orderInfo = orderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
    
            List<OrderItemInfo> orderItemInfos = orderItemInfoService.selectOrderItemInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
    
    
            CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
            commonOrderInfo.setOrderInfo(orderInfo);
            commonOrderInfo.setOrderItemInfo(orderItemInfos);
            /**
             * 查询税控设备信息
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderProcessInfo.getXhfNsrsbh());
    
            /**
             * 税控设备类型添加到订单主信息中
             */
            commonOrderInfo.setTerminalCode(terminalCode);
            //推送前走开票校验
            Map<String, String> resultMap = validateOrderInfo.checkOrderInvoice(commonOrderInfo);
    
            if (!OrderInfoContentEnum.SUCCESS.getKey()
                    .equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
                throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), resultMap.get(OrderManagementConstant.ERRORMESSAGE));
        
            }
    
            List<DDMXXX> ddmxxxList = BeanTransitionUtils.transitionORDER_INVOICE_ITEMV3(orderItemInfos);

            List<OrderProcessInfo> orderProcessInfoRelevantList = apiOrderProcessService.findTopParentList(orderProcessInfo, shList);
            List<DDKZXX> orderExtensionInfos = BeanTransitionUtils.transitionORDER_EXTENSION_INFOS(orderProcessInfoRelevantList);
            log.info("{}推送接口orderExtensionInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderExtensionInfos));
    
    
            //判断推送版本
            //新版本请求参数组装response
            DDFPCX_RSP ddfpcxRsp = new DDFPCX_RSP();
            //新版本组装数据方式
            List<DDFPZXX> ddfpzxxes = new ArrayList<>();
            DDFPZXX ddfpzxx = new DDFPZXX();
            DDFPXX ddfpxx = BeanTransitionUtils.transitionORDER_INVOICE_INFOV3(orderInfo, orderProcessInfo, new OrderInvoiceInfo());

            /**
             * 订单状态返回:
             * 根据当前订单状态进行返回,
             * 订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中;11.删除状态）
             *  对外状态有:
             *  000000:订单开票成功,001000:订单处理成功,001999:开票异常
             *  如果订单状态为0,1,2,3,为订单处理成功状态
             *  如果订单状态为4,9,10,为订单开票中状态.
             *  如果订单状态为5,7,为开票成功该状态
             *  如果订状态为6,8,为开票失败状态
             *  如果订单状态为11为订单删除状态
             */
            if (StringUtils.isNotBlank(orderProcessInfo.getDdzt())) {

                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getMessage());
                }
            }
    
            ddfpxx.setPDFZJL("");
            ddfpzxx.setDDFPXX(ddfpxx);
            ddfpzxx.setDDMXXX(ddmxxxList);
            //设置和当前订单相关的订单拆分合并关系协议beanV3
            ddfpzxx.setDDKZXX(orderExtensionInfos);
            ddfpzxxes.add(ddfpzxx);
            ddfpcxRsp.setZTDM(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getKey());
            ddfpcxRsp.setZTXX(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getMessage());
            ddfpcxRsp.setDDFPZXX(ddfpzxxes);
            content = JsonUtils.getInstance().toJsonString(ddfpcxRsp);
    
        }else{

            //开票数据推送
            OrderInfo orderInfo = orderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);

            List<OrderItemInfo> orderItemInfos = orderItemInfoService.selectOrderItemInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
            List<DDMXXX> ddmxxxList = BeanTransitionUtils.transitionORDER_INVOICE_ITEMV3(orderItemInfos);

            OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
            orderInvoiceInfo1.setFpqqlsh(orderProcessInfo.getFpqqlsh());
            OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
            log.info("{}推送接口orderInvoiceInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));

            List<OrderProcessInfo> orderProcessInfoRelevantList = apiOrderProcessService.findTopParentList(orderProcessInfo, shList);
            List<DDKZXX> orderExtensionInfos = BeanTransitionUtils.transitionORDER_EXTENSION_INFOS(orderProcessInfoRelevantList);
            log.info("{}推送接口orderExtensionInfo数据：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderExtensionInfos));

            /**
             * 查询税控设备信息
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderProcessInfo.getXhfNsrsbh());

            //判断推送版本
            //新版本请求参数组装response
            DDFPCX_RSP ddfpcxRsp = new DDFPCX_RSP();
            //新版本组装数据方式
            List<DDFPZXX> ddfpzxxes = new ArrayList<>();
            DDFPZXX ddfpzxx = new DDFPZXX();
            DDFPXX ddfpxx = BeanTransitionUtils.transitionORDER_INVOICE_INFOV3(orderInfo, orderProcessInfo, orderInvoiceInfo);

            /**
             * 订单状态返回:
             * 根据当前订单状态进行返回,
             * 订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中;11.删除状态）
             *  对外状态有:
             *  000000:订单开票成功,001000:订单处理成功,001999:开票异常
             *  如果订单状态为0,1,2,3,为订单处理成功状态
             *  如果订单状态为4,9,10,为订单开票中状态.
             *  如果订单状态为5,7,为开票成功该状态
             *  如果订状态为6,8,为开票失败状态
             *  如果订单状态为11为订单删除状态
             */
            if (StringUtils.isNotBlank(orderProcessInfo.getDdzt())) {

                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(orderProcessInfo.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getMessage());
                    /**
                     * 详细判断开票状态
                     * 优先判断冲红状态,返回对应冲红状态,然后判断作废状态
                     * 目前只返回全部冲红成功和部分冲红成功
                     * 目前只返回作废成功和作废失败.
                     */
                    if (StringUtils.isNotBlank(orderInvoiceInfo.getChBz())) {
                        if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoiceInfo.getChBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_004000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_004000.getMessage());
                        } else if (OrderInfoEnum.RED_INVOICE_4.getKey().equals(orderInvoiceInfo.getChBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_005000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_005000.getMessage());
                        }
                    }
                    if (StringUtils.isNotBlank(orderInvoiceInfo.getZfBz())) {
                        if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoiceInfo.getZfBz())) {
                            ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_003000.getKey());
                            ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_003000.getMessage());
                        }
                    }
                } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(orderProcessInfo.getDdzt())) {
                    ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getKey());
                    ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getMessage());
                }
            }
            /**
             * 获取PDF判断
             */
            GetPdfResponseExtend getPdfResponseExtend = null;
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
    
                /**
                 * 方格UKey的电票调用monggodb获取数据
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    HistoryDataPdfEntity historyDataPdfEntity = apiHistoryDataPdfService.find(orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), NsrsbhUtils.transShListByNsrsbh(orderInvoiceInfo.getXhfNsrsbh()));
                    if (Objects.nonNull(historyDataPdfEntity)) {
                        ddfpxx.setPDFZJL(historyDataPdfEntity.getPdfFileData());
                    }
                } else {
                    GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(invoicePush.getFPQQPCH(), invoicePush.getNSRSBH(), terminalCode, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getPdfUrl());
        
                    getPdfResponseExtend = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
        
                    ddfpxx.setPDFZJL(getPdfResponseExtend.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
                }
    
    
            } else {
                ddfpxx.setPDFZJL("");
            }

            //补全动态码
            if (StringUtils.isNotBlank(ddfpxx.getTQM())) {
                QuickResponseCodeInfo quickResponseCodeInfo = apiQuickCodeInfoService.queryQrCodeDetailByTqm(ddfpxx.getTQM(), shList, null);
                if (quickResponseCodeInfo != null) {
                    ddfpxx.setTQM(String.format(OpenApiConfig.qrCodeShortUrl, quickResponseCodeInfo.getTqm()));
                }
            }
    
            ddfpzxx.setDDFPXX(ddfpxx);
            ddfpzxx.setDDMXXX(ddmxxxList);
            //设置和当前订单相关的订单拆分合并关系协议beanV3
            ddfpzxx.setDDKZXX(orderExtensionInfos);
            ddfpzxxes.add(ddfpzxx);
            ddfpcxRsp.setZTDM(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getKey());
            ddfpcxRsp.setZTXX(OrderInfoContentEnum.PUSH_ENTERPRISE_SUCCESS.getMessage());
            ddfpcxRsp.setDDFPZXX(ddfpzxxes);
    
            content = JsonUtils.getInstance().toJsonString(ddfpcxRsp);
            log.info("推送的参数：{}", content);
        }
        return content;
    }

    @Override
    public R pushInvoiceInvalidRouting(String message) {
    
        INVALID_INVOICES_RSP invalidInvoicesRsp = JsonUtils.getInstance().parseObject(message, INVALID_INVOICES_RSP.class);
        List<INVALID_INVOICE_INFOS> invalidInvoiceInfos = invalidInvoicesRsp.getINVALID_INVOICE_INFOS();
    
    
        //供应链逻辑处理
        for (INVALID_INVOICE_INFOS info : invalidInvoiceInfos) {
        
            List<String> shList = new ArrayList<>();
            shList.add(invalidInvoicesRsp.getNSRSBH());
            OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(info.getFP_DM(), info.getFP_HM(), shList);
        
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
                OrderInfo order = orderInfoService.queryOrderInfoByFpqqlsh(orderInvoiceInfo.getFpqqlsh(), shList);
                //查询原蓝票的信息
            
                OrderInvoiceInfo blueInvoice = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(order.getYfpDm(), order.getYfpHm(), shList);
                if (blueInvoice != null) {
                    OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(blueInvoice.getFpqqlsh(), shList);
                    if (OrderInfoEnum.ORDER_SOURCE_8.getKey().equals(orderProcessInfo.getDdly())) {
                        InvoiceBatchRequestItem invoiceBatchRequestItem = invoiceBatchRequestItemMapper.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                        InvoicePush invoicePush = new InvoicePush();
                        invoicePush.setFPQQLSH(orderProcessInfo.getFpqqlsh());
                        invoicePush.setEWM(blueInvoice.getEwm());
                        invoicePush.setKPHJSE(Double.valueOf(blueInvoice.getKpse()));
                        invoicePush.setFJH(blueInvoice.getFjh());
                        invoicePush.setFP_DM(blueInvoice.getFpdm());
                        invoicePush.setFPLX(blueInvoice.getKplx());
                        invoicePush.setFP_HM(blueInvoice.getFphm());
                        invoicePush.setNSRSBH(orderProcessInfo.getXhfNsrsbh());
                        invoicePush.setFPQQPCH(invoiceBatchRequestItem.getFpqqpch());
                        pushCompleteOrder(invoicePush, OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_1.getKey());
                    }

                }

            }else{
                OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(orderInvoiceInfo.getFpqqlsh(), shList);
                if (OrderInfoEnum.ORDER_SOURCE_8.getKey().equals(orderProcessInfo.getDdly())) {
                    InvoicePush invoicePush = new InvoicePush();
                    InvoiceBatchRequestItem invoiceBatchRequestItem = invoiceBatchRequestItemMapper.selectInvoiceBatchItemByKplsh(orderInvoiceInfo.getKplsh(), shList);
                    invoicePush.setFPQQLSH(orderProcessInfo.getFpqqlsh());
                    invoicePush.setEWM(orderInvoiceInfo.getEwm());
                    invoicePush.setKPHJSE(Double.valueOf(orderInvoiceInfo.getKpse()));
                    invoicePush.setFJH(orderInvoiceInfo.getFjh());
                    invoicePush.setFP_DM(orderInvoiceInfo.getFpdm());
                    invoicePush.setFPLX(orderInvoiceInfo.getKplx());
                    invoicePush.setFP_HM(orderInvoiceInfo.getFphm());
                    invoicePush.setFPQQPCH(invoiceBatchRequestItem.getFpqqpch());
                    invoicePush.setNSRSBH(orderProcessInfo.getXhfNsrsbh());
                    pushCompleteOrder(invoicePush, OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_1.getKey());
                }

            }
        }
        //走自定义推送配置
        return pushInvocieInvalid(message);
    }


    @Override
    public List<PushInfo> queryPushInfoList(PushInfo pushInfo, List<String> shList) {
        return pushInfoMapper.queryPushInfoList(pushInfo,shList);
    }

    @Override
    public PushInfo queryPushInfo(PushInfo pushInfo) {
        return pushInfoMapper.selectByPushInfo(pushInfo);
    }

    @Override
    public int updatePushInfo(PushInfo pushInfo) {
        return pushInfoMapper.updateByPrimaryKeySelective(pushInfo);
    }

    @Override
    public int addPushInfo(PushInfo insertPushInfo) {
        return pushInfoMapper.insertSelective(insertPushInfo);
    }


    /**
	* 推送作废数据状态
	*/
    public R pushInvocieInvalid(String message) {
    
        log.info("推送发票作废数据状态的接口：{}", message);
        INVALID_INVOICES_RSP invalidInvoicesRsp = JsonUtils.getInstance().parseObject(message, INVALID_INVOICES_RSP.class);
        List<PushInfo> selectListByPushInfo = getPushUrlList(invalidInvoicesRsp.getNSRSBH(),OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_2.getKey(),"v4");
        if (CollectionUtils.isEmpty(selectListByPushInfo)) {
            log.error("税号:{}的作废推送地址未配置", invalidInvoicesRsp.getNSRSBH());
            return R.ok();
        }
    
        //多url推送
        for (PushInfo pushInfo : selectListByPushInfo) {
            //判断推送版本
            if (StringUtils.isBlank(pushInfo.getVersionIdent())) {
                log.error("税号:{}推送地址中推送版本不能为空!", invalidInvoicesRsp.getNSRSBH());
                continue;
            } else if (!(ConfigurerInfo.INTERFACE_VERSION_V3.equals(pushInfo.getVersionIdent()) || ConfigurerInfo.INTERFACE_VERSION_V4.equals(pushInfo.getVersionIdent()))) {
                log.error("税号:{}推送地址中推送版本只能为v3或v4!", invalidInvoicesRsp.getNSRSBH());
                continue;
            } else if (!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_HTTP.getKey().equals(pushInfo.getProtocolType())
                    &&!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_WEBSERVICE.getKey().equals(pushInfo.getProtocolType())) {
                log.error("税号:{}推送地址中接口协议类型只能为http或webservice!", invalidInvoicesRsp.getNSRSBH());
                continue;
            }
    
            /**
             * 根据版本号判断作废推送数据
             */
    
            if (ConfigurerInfo.INTERFACE_VERSION_V4.equals(pushInfo.getVersionIdent())) {
                INVALID_INVOICES_RSP invalidInvoicesRsp1 = JsonUtils.getInstance().parseObject(message, INVALID_INVOICES_RSP.class);
                ZFTSXX_REQ zftsxxReq = InterfaceBeanTransUtils.transZftsReq(invalidInvoicesRsp1);
                message = JsonUtils.getInstance().toJsonString(zftsxxReq);
            }

            String result = push(pushInfo,ws_fpzf_method, message);
            parseResult(result, pushInfo.getNsrsbh(), pushInfo.getVersionIdent());
        }
        return R.ok();
    }
    
    
    @Override
    public PushInfo selectByPushInfo(PushInfo pushInfo) {
        return pushInfoMapper.selectByPushInfo(pushInfo);
    }




    /**
     * 中移项目 推送待审核的数据到进项
     *
     * @return
     */
    @Override
    public R pushCompleteOrder(InvoicePush invoicePush,String pushType) {

        //数据库查询推送配置
        try {
            PushInfo queryPushInfo = new PushInfo();
            queryPushInfo.setStatus(ConfigureConstant.STRING_0);
            queryPushInfo.setInterfaceType(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_5.getKey());
            queryPushInfo.setNsrsbh(OpenApiConfig.supplyVirtualTaxpayer);
            log.info("{}推送地址获取的纳税人识别号：{}", LOGGER_MSG, OpenApiConfig.supplyVirtualTaxpayer);
            List<PushInfo> pushInfoList = pushInfoMapper.selectListByPushInfo(queryPushInfo);
            log.debug("{}获取到推送配置信息:{}",LOGGER_MSG,JsonUtils.getInstance().toJsonString(pushInfoList));


            if(pushInfoList == null || pushInfoList.size() <= 0){

                return R.error().put(OrderManagementConstant.MESSAGE,"待审核订单推送地址未配置");
            }else{

                //推送及补全数据报文
                List<String> shList = new ArrayList<>();
                shList.add(invoicePush.getNSRSBH());
                String pushOrderContent = getPushOrderContent(invoicePush,shList,pushType);


                log.info("{}推送到进项的url:{},报文:{}",LOGGER_MSG,pushInfoList.get(0).getPushUrl(),pushOrderContent);
                Map<String, String> head = new HashMap<>(5);
                head.put("Content-Type","application/json");
                String result = HttpUtils.doPostWithHeader(pushInfoList.get(0).getPushUrl(), pushOrderContent,head);
                log.info("{}推送到进项的接口，返回参数:{}",LOGGER_MSG,result);

                boolean isSuccess = false;
                if (!StringUtils.isBlank(result)) {
                    SupplyChainBaseResponse commonReponse = JsonUtils.getInstance().parseObject(result,SupplyChainBaseResponse.class);

                    log.debug("{}转换后的返回后的信息:{}",LOGGER_MSG,JsonUtils.getInstance().toJsonString(commonReponse));
                    if (commonReponse != null && OrderInfoContentEnum.SUCCESS_000000.getKey().equals(commonReponse.getZTDM()) && CollectionUtils.isNotEmpty(commonReponse.getDDFPTS())) {
                        SupplyChainPushResponse supplyChainPushResponse = commonReponse.getDDFPTS().get(0);
                        if(OrderInfoContentEnum.SUCCESS_000000.getKey().equals(supplyChainPushResponse.getZTDM())){
                            log.info("{},fqqqlsh:{}数据推送成功!",LOGGER_MSG,invoicePush.getFPQQLSH());
                            isSuccess = true;
                        }
                    }
                }

                //推送完成后处理 推送或审核状态
                if(OrderInfoEnum.SUPPLY_CHINA_PUSH_TYPE_0.getKey().equals(pushType)){
                    //推送订单成功的更新审核状态
                    if(isSuccess){
                        OrderProcessInfo updateProcessInfo = new OrderProcessInfo();
                        updateProcessInfo.setFpqqlsh(invoicePush.getFPQQLSH());
                        updateProcessInfo.setCheckStatus(OrderInfoEnum.CHECK_STATUS_1.getKey());
                        int i = apiOrderProcessService.updateOrderProcessInfoByFpqqlsh(updateProcessInfo,shList);
                        if(i <= 0){
                            log.error("{}更新订单审核状态失败,fpqqlsh:{}",LOGGER_MSG,invoicePush.getFPQQLSH());
                            return R.error().put(OrderManagementConstant.MESSAGE,"更新订单审核状态失败!");
                        }else{
                            return R.ok();
                        }
                    }else{
                        log.error("{},fpqqsh:{}数据推送失败!",LOGGER_MSG,invoicePush.getFPQQLSH());
                        return R.error().put(OrderManagementConstant.MESSAGE,"推送待审核数据失败!");
                    }
                }else{
                    /**
                     * 发票推送成功的更新推送状态
                     */
                    OrderInvoiceInfo orderQuery = new OrderInvoiceInfo();
                    orderQuery.setFpqqlsh(invoicePush.getFPQQLSH());
                    if (isSuccess) {
                        log.info("==>推送返回解析成功,更新数据库表开始！");
                        orderQuery.setPushStatus(OrderInfoEnum.PUSH_STATUS_1.getKey());
                        int updateByPrimaryKeySelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderQuery, shList);
                        if (updateByPrimaryKeySelective <= 0) {
                            log.error("{}推送状态更新失败", LOGGER_MSG);
                        }
                        return R.ok().put(OrderManagementConstant.MESSAGE,"推送成功!");
                    } else {
                        orderQuery.setPushStatus(OrderInfoEnum.PUSH_STATUS_2.getKey());
                        int updateByPrimaryKeySelective = orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderQuery, shList);
                        if (updateByPrimaryKeySelective <= 0) {
                            log.error("{}推送状态更新失败", LOGGER_MSG);
                        }
                        return R.error().put(OrderManagementConstant.MESSAGE,"推送失败!");
                    }

                }

            }
        } catch (OrderReceiveException e) {
           log.error(e.getMessage());
           return R.error().put(OrderManagementConstant.CODE,e.getCode()).put(OrderManagementConstant.MESSAGE,e.getMessage());
        }

    }


    public static void main(String[] args) {
        String str  = "{\n" +
                "\t\"DDFPTS\": [{\n" +
                "\t\t\"DDH\": \"0002\",\n" +
                "\t\t\"DDQQLSH\": \"437509149854494720\",\n" +
                "\t\t\"ZTDM\": \"000000\",\n" +
                "\t\t\"ZTXX\": \"\\u5904\\u7406\\u6210\\u529F\"\n" +
                "\t}],\n" +
                "\t\"zTDM\": \"000000\",\n" +
                "\t\"zTXX\": \"\\u5904\\u7406\\u6210\\u529F\"\n" +
                "}";
        SupplyChainBaseResponse scbr = JsonUtils.getInstance().parseObject(str, SupplyChainBaseResponse.class);
        System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(scbr));
    }

    private Map<String,Object> assemblyPrintData(cn.hutool.json.JSONObject mesJson){
        Map<String,Object> map = new HashMap<>(3);
        String fpqqlsh = mesJson.getStr("FPQQLSH");
        String nsrsbh = mesJson.getStr("NSRSBH");
        List<InvoicePrintInfo> printInvoicesList = apiFangGeInterfaceService.getPrintInvoicesList(fpqqlsh, nsrsbh);
        if(CollectionUtil.isNotEmpty(printInvoicesList)){
            InvoicePrintInfo invoicePrintInfo = printInvoicesList.get(0);
            map.put("FPQQLSH",invoicePrintInfo.getFpqqlsh());
//            打印状态:wait:待打印,printing:打印中,success:打印成功,fail:打印失败
            if (invoicePrintInfo.getPrintStatus().equals(OrderInfoEnum.INVOICE_PRINT_STATUS_0.getKey())) {
                map.put("STATUS_CODE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009001.getKey());
                map.put("STATUS_MESSAGE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009001.getMessage());
            } else if (invoicePrintInfo.getPrintStatus().equals(OrderInfoEnum.INVOICE_PRINT_STATUS_1.getKey())) {
                map.put("STATUS_CODE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009000.getKey());
                map.put("STATUS_MESSAGE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009000.getMessage());
            } else if (invoicePrintInfo.getPrintStatus().equals(OrderInfoEnum.INVOICE_PRINT_STATUS_2.getKey())) {
                map.put("STATUS_CODE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_000000.getKey());
                map.put("STATUS_MESSAGE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_000000.getMessage());
            } else if (invoicePrintInfo.getPrintStatus().equals(OrderInfoEnum.INVOICE_PRINT_STATUS_3.getKey())) {
                map.put("STATUS_CODE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009999.getKey());
                map.put("STATUS_MESSAGE", OrderInfoContentEnum.FG_PRINTER_CONFIGURATION_009999.getMessage());
            }
        }
        return map;
    }
    private RED_INVOICE_FORM_DOWNLOAD assemblyUploadDownloadData(InvoicePush mqInvoicePush) {
    
        RED_INVOICE_FORM_DOWNLOAD redInvoiceFormDownload = new RED_INVOICE_FORM_DOWNLOAD();
        SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(mqInvoicePush.getSQBSCQQLSH());
        //List<SpecialInvoiceReversalItemEntity> itemList =
        List<SpecialInvoiceReversalItem> itemList = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(specialInvoiceReversal.getId());
        RED_INVOICE_FORM_DOWN_HEAD redInvoiceFormDownHead = new RED_INVOICE_FORM_DOWN_HEAD();
        List<ORDER_INVOICE_ITEM> orderInvoiceItems = new ArrayList<>();
        redInvoiceFormDownHead.setSQDH(specialInvoiceReversal.getSqdh());
        redInvoiceFormDownHead.setXXBBH(specialInvoiceReversal.getXxbbh());
        redInvoiceFormDownHead.setSTATUS_CODE(specialInvoiceReversal.getStatusCode());
        redInvoiceFormDownHead.setSTATUS_MESSAGE(specialInvoiceReversal.getStatusMessage());
        redInvoiceFormDownHead.setYFP_DM(specialInvoiceReversal.getYfpDm());
        redInvoiceFormDownHead.setYFP_HM(specialInvoiceReversal.getYfpHm());
        redInvoiceFormDownHead.setFPLX(specialInvoiceReversal.getInvoiceType());
        redInvoiceFormDownHead.setFPLB(specialInvoiceReversal.getFpzlDm());
    
        redInvoiceFormDownHead.setTKSJ(DateUtils.format(specialInvoiceReversal.getTksj(), "yyyy-MM"));
        redInvoiceFormDownHead.setXSF_NSRSBH(specialInvoiceReversal.getXhfNsrsbh());
        redInvoiceFormDownHead.setXSF_MC(specialInvoiceReversal.getXhfMc());
        redInvoiceFormDownHead.setGMF_NSRSBH(specialInvoiceReversal.getGhfNsrsbh());
        redInvoiceFormDownHead.setGMF_MC(specialInvoiceReversal.getGhfMc());
        redInvoiceFormDownHead.setHJJE(specialInvoiceReversal.getHjbhsje());
        redInvoiceFormDownHead.setHJSE(specialInvoiceReversal.getHjse());
        redInvoiceFormDownHead.setSQSM(specialInvoiceReversal.getSqsm());
        redInvoiceFormDownHead.setBMB_BBH(specialInvoiceReversal.getBmbbbh());
        redInvoiceFormDownHead.setYYSBZ(specialInvoiceReversal.getYysbz());
        redInvoiceFormDownload.setRED_INVOICE_FORM_DOWN_HEAD(redInvoiceFormDownHead);
        String sl = itemList.get(0).getSl();
        String dslbz = "";
        if (StringUtils.isEmpty(sl)) {
            dslbz = "1";
        }
        String fphxz = "0";
        if (itemList.size() == 1 && itemList.get(0).getXmmc().equals(ConfigureConstant.XJZSXHQD)) {
            fphxz = "6";
        }
        for (SpecialInvoiceReversalItem itemEntity : itemList) {
            ORDER_INVOICE_ITEM orderInvoiceItem = new ORDER_INVOICE_ITEM();
            orderInvoiceItem.setXMXH(itemEntity.getSphxh());
            orderInvoiceItem.setSPBM(itemEntity.getSpbm());
            orderInvoiceItem.setFPHXZ(fphxz);
            orderInvoiceItem.setYHZCBS(itemEntity.getYhzcbs());
            orderInvoiceItem.setLSLBS(itemEntity.getLslbs());
            orderInvoiceItem.setZZSTSGL(itemEntity.getZzstsgl());
            orderInvoiceItem.setXMMC(itemEntity.getXmmc());
            orderInvoiceItem.setGGXH(itemEntity.getGgxh());
            orderInvoiceItem.setDW(itemEntity.getXmdw());
            orderInvoiceItem.setXMSL(itemEntity.getXmsl());
            orderInvoiceItem.setXMDJ(itemEntity.getXmdj());
            orderInvoiceItem.setXMJE(itemEntity.getXmje());
            orderInvoiceItem.setHSBZ(itemEntity.getHsbz());
            if (StringUtils.isEmpty(dslbz)) {
                if (!sl.equals(itemEntity.getSl())) {
                    dslbz = "1";
                }
            }
            orderInvoiceItem.setSL(itemEntity.getSl());
            orderInvoiceItem.setSE(itemEntity.getSe());
            orderInvoiceItems.add(orderInvoiceItem);
        }
        redInvoiceFormDownHead.setDSLBZ(dslbz);
        redInvoiceFormDownload.setORDER_INVOICE_ITEMS(orderInvoiceItems);
    
        return redInvoiceFormDownload;
    }

    @Override
    public void pushHZXXBtatus(String xxbbh, String xhfNsrsbh, String ghfNsrsbh, String status) {
        log.info("推送红字信息表作废状态的接口：xxbbh：{}，xhfNsrsbh：{}，ghfNsrsbh：{}，status：{}", xxbbh,xhfNsrsbh,ghfNsrsbh,status);
        List<PushInfo> selectListByPushInfo = getPushUrlList(xhfNsrsbh,OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_6.getKey(),"v4");
        if (CollectionUtils.isEmpty(selectListByPushInfo)) {
            log.error("税号:{}的作废推送地址未配置", xhfNsrsbh);
            return;
        }
        //多url推送
        for (PushInfo pushInfo : selectListByPushInfo) {
            //判断推送版本
            if (StringUtils.isBlank(pushInfo.getVersionIdent())) {
                log.error("税号:{}推送地址中推送版本不能为空!", xhfNsrsbh);
                continue;
            } else if (!(ConfigurerInfo.INTERFACE_VERSION_V3.equals(pushInfo.getVersionIdent()) || ConfigurerInfo.INTERFACE_VERSION_V4.equals(pushInfo.getVersionIdent()))) {
                log.error("税号:{}推送地址中推送版本只能为v3或v4!", xhfNsrsbh);
                continue;
            } else if (!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_HTTP.getKey().equals(pushInfo.getProtocolType())
                    &&!OrderInfoEnum.INTERFACE_PROTOCAL_TYPE_WEBSERVICE.getKey().equals(pushInfo.getProtocolType())) {
                log.error("税号:{}推送地址中接口协议类型只能为http或webservice!", xhfNsrsbh);
                continue;
            }

            /**
             * 根据版本号判断作废推送数据
             */
            Map<String, String> map = new HashMap<>();
            map.put("XXBBH", xxbbh);
            map.put("XHFNSRSBH", xhfNsrsbh);
            map.put("GHFNSRSBH", ghfNsrsbh);
            map.put("XXBZT", status);
            String result = push(pushInfo,ws_hzsqdcx_method, JsonUtils.getInstance().toJsonString(map));

            //处理返回报文
            R resultR = parseResult(result,xhfNsrsbh,pushInfo.getVersionIdent());
            log.info("推送结果：{}",JsonUtils.getInstance().toJsonString(resultR));
        }
        return;
    }

    private R parseResult(String result,String xhfNsrsbh,String version) {

        if (!StringUtils.isBlank(result)) {
            String statusCode = null;
            CommonReponse commonReponse = JSONObject.parseObject(result, CommonReponse.class);
            if (commonReponse.getResponseStatus() != null) {
                ResponseStatus res = commonReponse.getResponseStatus();
                String code = res.getCode();
                ResponseData responseData = commonReponse.getResponseData();

                if (ConfigureConstant.STRING_0000.equals(code) || ConfigureConstant.STRING_2000.equals(code)) {
                    CommonRequestParam commonRequestParam1 = new CommonRequestParam();
                    commonRequestParam1.setContent(responseData.getContent());
                    commonRequestParam1.setEncryptCode(responseData.getEncryptCode());
                    commonRequestParam1.setZipCode(responseData.getZipCode());
                    commonRequestParam1.setSecretId(iCommonDisposeService.getAuthMap(xhfNsrsbh));
                    String s = iCommonDisposeService.commonDecrypt(commonRequestParam1);
                    log.info("解析结果为,{}", s);
                    RESPONSEV4 pushRsp = new RESPONSEV4();
                    if (ConfigurerInfo.INTERFACE_VERSION_V4.equals(version)) {
                        pushRsp = JsonUtils.getInstance().parseObject(s, RESPONSEV4.class);
                    } else if (ConfigurerInfo.INTERFACE_VERSION_V3.equals(version)) {
                        RESPONSE response = JsonUtils.getInstance().parseObject(s, RESPONSE.class);
                        pushRsp.setZTDM(response.getSTATUS_CODE());
                        pushRsp.setZTXX(response.getSTATUS_MESSAGE());
                    }
                    statusCode = pushRsp.getZTDM();
                } else {
                    log.info("推送数据返回的状态结果为失败！");
                    return R.error("推送数据返回的状态结果为失败！");
                }
            } else {
                log.error("推送返回没有接收到状态码！");
                return R.error("推送返回没有接收到状态码！");
            }
            if (!ConfigureConstant.STRING_0000.equals(statusCode)&&!ConfigureConstant.STRING_000000.equals(statusCode)) {
                log.error("推送返回结果失败");
                return R.error("推送返回结果失败");
            }else {
                return R.ok();
            }
        } else {
            log.info("推送请求返回无结果信息。");
            return R.error("推送请求返回无结果信息");
        }
    }

}
