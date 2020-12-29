package com.dxhy.order.consumer.openapi.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.openapi.service.IAllocateInvoiceInterfaceServiceV3;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.consumer.utils.ReplaceCharacterUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;
import com.dxhy.order.model.a9.kp.InvoiceQuery;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.mqdata.FpkjMqData;
import com.dxhy.order.protocol.v4.order.DDPCXX_REQ;
import com.dxhy.order.protocol.v4.order.DDPCXX_RSP;
import com.dxhy.order.protocol.v4.order.DDZXX;
import com.dxhy.order.utils.CommonUtils;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.PriceTaxSeparationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.dxhy.order.consumer.utils.BeanTransitionUtils.convertMapToCommonRsp;
import static com.dxhy.order.consumer.utils.BeanTransitionUtils.createBuyerEntity;

/**
 * 订单对外接口业务实现类-发票开具接口
 *
 * @author: chengyafu
 * @date: 2018年8月9日 下午4:15:27
 */
@Service
@Slf4j
public class AllocateInvoiceInterfaceServiceImplV3 implements IAllocateInvoiceInterfaceServiceV3 {
    
    private static final String LOGGER_MSG = "(订单对外接口业务类V3)";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiBuyerService apiBuyerService;
    
    @Reference(retries = 0)
    private OpenInvoiceService openInvoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiQuickCodeInfoService apiQuickCodeInfoService;

    @Reference
    private ApiRushRedInvoiceRequestInfoService apiRushRedInvoiceRequestInfoService;
    /**
     * 发票开具接口
     *
     * @param ddpcxxReq
     * @return
     */
    @Override
    public DDPCXX_RSP allocateInvoicesV3(DDPCXX_REQ ddpcxxReq, String secretId, String kpjh,String protocol_type) {
        DDPCXX_RSP ddpcxxRsp = new DDPCXX_RSP();
        String sldMc = "";
        log.debug("{},发票开具数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(ddpcxxReq));
    
    
        /**
         * 数据校验
         * 1.限制请求数据不大于10000条,超过10000条(包括10000条)返回错误.
         * 2.校验所有数据判断数据中蓝票不能存在原发票代码号码,红票必须存在原发票代码号码
         *
         */
    
        if (ddpcxxReq == null || ddpcxxReq.getDDZXX() == null || ConfigureConstant.PC_MAX_ITEM_LENGTH <= ddpcxxReq.getDDZXX().size()) {
            log.error("{}开具发票数量超过1000限额", LOGGER_MSG);
            ddpcxxRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_107130.getKey());
            ddpcxxRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_107130.getMessage());
            return ddpcxxRsp;
        }
    
        ddpcxxRsp.setDDQQPCH(ddpcxxReq.getDDPCXX().getDDQQPCH());
        /**
         * 如果开票方式为空,设置开票方式为自动开票
         */
        if (StringUtils.isBlank(ddpcxxReq.getDDPCXX().getKPFS())) {
            ddpcxxReq.getDDPCXX().setKPFS(OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey());
        }
        /**
         * 如果是否成品油为空,设置是否成品油为非成品油
         */
        if (StringUtils.isBlank(ddpcxxReq.getDDPCXX().getCPYBS())) {
            ddpcxxReq.getDDPCXX().setCPYBS(OrderInfoEnum.ORDER_REQUEST_OIL_0.getKey());
        }
    
        /**
         *   添加特殊字符替换
         */
        log.info("替换前的字符：{}", JsonUtils.getInstance().toJsonString(ddpcxxReq));
        ReplaceCharacterUtils.replaceCharacter(ddpcxxReq);
    
        /**
         * 防止批次请求信息中出现异常,税号和terminalCode先使用初始化
         */
        String nsrsbh = "";
        String terminalCode = OrderInfoEnum.TAX_EQUIPMENT_C48.getKey();
    
        if (ObjectUtil.isNotNull(ddpcxxReq) && ObjectUtil.isNotNull(ddpcxxReq.getDDPCXX()) && StringUtils.isNotBlank(ddpcxxReq.getDDPCXX().getNSRSBH())) {
            nsrsbh = ddpcxxReq.getDDPCXX().getNSRSBH();
            terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
        }
    
        /**
         * 接口数据整体校验
         * 支持新税控长度校验,透传terminalCode,特殊字段根据terminalCode进行判断,后期考虑扩展
         */
        Map<String, String> checkInvParam1 = validateInterfaceOrder.checkInterfaceParamV3(ddpcxxReq, secretId, terminalCode);
        if (!ConfigureConstant.STRING_0000.equals(checkInvParam1.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}数据非空和长度校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam1);
            ddpcxxRsp.setZTDM(checkInvParam1.get(OrderManagementConstant.ERRORCODE));
            ddpcxxRsp.setZTXX(checkInvParam1.get(OrderManagementConstant.ERRORMESSAGE));
            return ddpcxxRsp;
        }
        
        
        /**
         * 数据请求重复性校验
         */
        R r = iCommonInterfaceService.checkOrderInfoIsRepeat(ddpcxxReq);
        
        if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
            log.error("{} 订单批次号重复：{}", LOGGER_MSG, ddpcxxReq.getDDPCXX().getDDQQPCH());
            ddpcxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010001_V3.getKey());
            ddpcxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010001_V3.getMessage());
            return ddpcxxRsp;
        }
    
    
        /**
         * 根据税号获取设备信息
         */
    
        String kpfs = ddpcxxReq.getDDPCXX().getKPFS();
        String sldid = ddpcxxReq.getDDPCXX().getKPZD();
    
    
        List<String> shList = new ArrayList<>();
        shList.add(nsrsbh);
        /**
         * 种类代码转换
         */
        String fplb = CommonUtils.transFpzldm(ddpcxxReq.getDDPCXX().getFPLXDM());
    
        ddpcxxReq.getDDPCXX().setFPLXDM(fplb);
        
        /**
         * 如果开票方式为自动开票或者是扫码开票,需要获取受理点,
         */
        
        if (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(kpfs) || OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(kpfs)) {
    
    
            /**
             * 处理受理点
             * 如果是电票跳过受理点赋值.
             * 如果是纸票,为用户自动匹配可用受理点
             */
            if (ConfigureConstant.STRING_1_.equals(sldid)) {
                sldid = "";
            }
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddpcxxReq.getDDPCXX().getFPLXDM())
                    || OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddpcxxReq.getDDPCXX().getFPLXDM())) {
                /**
                 * 1.新税控无开票点，也无接口
                 * 2.新税控在无传开票点时，会自动轮循开票点的
                 * 3.ukey 电票也无需传开票点，页面传入，底层开票也不用
                 */
        
                if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    R result = iCommonInterfaceService.dealWithSldStartV3(sldid, fplb, nsrsbh, ddpcxxReq.getDDZXX().get(0).getDDTXX().getQDBZ(), terminalCode);
                    log.debug("受理点查询成功!");
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
                        return convertMapToCommonRsp(result);
                    } else {
                        log.debug("受理点查询成功!");
                        sldid = String.valueOf(result.get("sldid"));
                        kpjh = String.valueOf(result.get("kpjh"));
                        sldMc = String.valueOf(result.get("sldmc"));
                    }
                } else {
                    sldid = "";
                }
            } else {
                //获取受理点
                R result = iCommonInterfaceService.dealWithSldStartV3(sldid, fplb, nsrsbh, ddpcxxReq.getDDZXX().get(0).getDDTXX().getQDBZ(), terminalCode);
        
        
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
                    return convertMapToCommonRsp(result);
                } else {
                    log.debug("受理点查询成功!");
                    sldid = String.valueOf(result.get("sldid"));
                    kpjh = String.valueOf(result.get("kpjh"));
                    sldMc = String.valueOf(result.get("sldmc"));
                }
                //校验是否开具成品油的发票 成品油的发票只能用成品油的受理点
        
        
                R result1 = iCommonInterfaceService.checkSldInfoA9(ddpcxxReq, sldid, terminalCode);
    
                if (!OrderInfoContentEnum.SUCCESS.getKey()
                        .equals(String.valueOf(result1.get(OrderManagementConstant.CODE)))) {
                    return convertMapToCommonRsp(result1);
                }
    
            }
    
        }
    
    
        /**
         * 套餐余量查询
         */
        com.dxhy.order.model.R r1 = iCommonInterfaceService.mealAllowance(ddpcxxReq.getDDPCXX().getNSRSBH());
        if (!ConfigureConstant.STRING_0000.equals(r1.get(OrderManagementConstant.CODE))) {
            log.error("{} 套餐余量查询：{}", LOGGER_MSG, r1.get(OrderManagementConstant.MESSAGE));
            ddpcxxRsp.setZTDM(String.valueOf(r1.get(OrderManagementConstant.CODE)));
            ddpcxxRsp.setZTXX(String.valueOf(r1.get(OrderManagementConstant.MESSAGE)));
            return ddpcxxRsp;
        }
    
        /**
         *  逻辑处理
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
        List<DDZXX> ddzxx = ddpcxxReq.getDDZXX();
    
        //订单批次号转换
        OrderBatchRequest orderBatchRequest = BeanTransitionUtils.transitionOrderBatchRequestV3(ddpcxxReq.getDDPCXX());
        orderBatchRequest.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
    
    
        List<OrderInfo> insertOrder = new ArrayList<>();
        List<List<OrderItemInfo>> insertOrderItem = new ArrayList<>();
        List<OrderProcessInfo> insertProcessInfo = new ArrayList<>();
        List<OrderProcessInfo> updateProcessInfo = new ArrayList<>();
        List<List<InvoiceBatchRequestItem>> insertBatchItem = new ArrayList<>();
        List<OrderInvoiceInfo> insertInvoiceInfo = new ArrayList<>();
        List<OrderInvoiceInfo> updateInvoiceInfo = new ArrayList<>();
        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
        List<OrderInfo> qrcodeOrderInfoList = new ArrayList<>();
        List<String> ddqqlshList = new ArrayList<>();
        List<FpkjMqData> fpkjMqDataList = new ArrayList<>();
        List<InvoiceBatchRequest> invoiceBatchRequestList = new ArrayList<>();
        try {
        
            for (int i = 0; i < ddzxx.size(); i++) {
                String invoiceRequestId = apiInvoiceCommonMapperService.getGenerateShotKey();
                InvoiceBatchRequest transitionBatchRequest = BeanTransitionUtils.transitionAutoBatchRequestV3(ddpcxxReq.getDDPCXX());
                transitionBatchRequest.setId(invoiceRequestId);
                transitionBatchRequest.setFpqqpch(apiInvoiceCommonMapperService.getGenerateShotKey());
    
                CommonOrderInfo commonOrderInfo1 = new CommonOrderInfo();
                /**
                 * 新增redis缓存,记录每张流水号的数据
                 */
                String ddqqlshKey = String.format(Constant.REDIS_INTERFACE_RECEIVE, ddzxx.get(i).getDDTXX().getDDQQLSH());
                String ddqqlshRedisStatus = iCommonInterfaceService.getDdqqlshRedisStatus(ddqqlshKey);
                if (OrderInfoEnum.ORDER_STATUS_99.getKey().equals(ddqqlshRedisStatus)) {
                    log.error("{} 请求流水号正在处理中,请勿重复请求!批次号为:{},流水号为:{}", LOGGER_MSG, ddpcxxReq.getDDPCXX().getDDQQPCH(),
                            ddzxx.get(i).getDDTXX().getDDQQLSH());
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_ERROR_CODE_010010_V3);
                
                } else {
                    ddqqlshList.add(ddqqlshKey);
                    iCommonInterfaceService.setDdqqlshRedisStatus(ddqqlshKey, OrderInfoEnum.ORDER_STATUS_99.getKey());
                
                }
                boolean flag = false;
                //校验请求流水号是否重复
                OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(ddzxx.get(i).getDDTXX().getDDQQLSH(), shList);
    
                log.debug("{}查询数据库处理表校验数据是否已经存在,结果为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderProcessInfo));
            
                //动态码不需要校验是否存在此请求流水号的数据
                if (!OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(kpfs) && orderProcessInfo != null) {
        
                    if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())) {
                        flag = true;
                    } else {
                        log.error("{} 请求流水号已存在!批次号为:{},流水号为:{}", LOGGER_MSG, ddpcxxReq.getDDPCXX().getDDQQPCH(),
                                ddzxx.get(i).getDDTXX().getDDQQLSH());
                        throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_ERROR_CODE_010002_V3);
                    }
                }
    
    
                /**
                 * 订单信息转换
                 */
                CommonOrderInfo commonOrderInfo = com.dxhy.order.utils.BeanTransitionUtils.transitionCommonOrderInfoV3(ddzxx.get(i));
                commonOrderInfo.setKpfs(ddpcxxReq.getDDPCXX().getKPFS());
                commonOrderInfo.setPcnsrsbh(ddpcxxReq.getDDPCXX().getNSRSBH());
                /**
                 * 如果购货方企业类型为空,默认为其他类型
                 */
                if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfQylx())) {
                    commonOrderInfo.getOrderInfo().setGhfQylx(OrderInfoEnum.GHF_QYLX_04.getKey());
                }
    
                /**
                 * 如果编码表版本号为空,默认是额33.0
                 */
                if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getBbmBbh())) {
                    commonOrderInfo.getOrderInfo().setBbmBbh(SystemConfig.bmbbbh);
                }
            
            
                /**
                 * 静态码开票时业务类型单独处理，订单表和订单处理表赋值业务类型
                 */
                QuickResponseCodeInfo info = null;
                OrderInfo qrcodeOrderInfo = null;

            
                /**
                 * 判断是否需要补全销方信息
                 * todo 后期优化,需要添加销方缓存,目前大部分都是单批次单个发票
                 */
                if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfDz()) || StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfYh())) {
                    DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(commonOrderInfo.getOrderInfo().getXhfNsrsbh(), commonOrderInfo.getOrderInfo().getXhfMc());
                    if (sysDeptEntity == null) {
                        log.error("{}补全企业信息异常,获取到的企业信息为空", LOGGER_MSG);
                        throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_ERROR_CODE_010008_V3);
                    } else {
                    
                        BeanTransitionUtils.transitionOrderSellerInfo(commonOrderInfo, sysDeptEntity);
                    }
                
                }
            
                /**
                 * 购方信息数据补全,
                 * 判断购方ID是否为空,如果不为空则根据销方税号和购方ID调用数据库获取数据,
                 * 判断查询到的数据是否为空,如果不为空,则调用购方信息补全方法,
                 * 购方信息补全:如果接口传递数据不为空,则使用接口传递数据,如果接口传递数据为空,则使用查询到的数据进行赋值操作.
                 */
                if (StringUtils.isNotBlank(ddzxx.get(i).getDDTXX().getGMFBM())) {
                
                    BuyerEntity buyerEntity = apiBuyerService.queryBuyerInfoByxhfNsrsbhAndBuyerCode(ddzxx.get(i).getDDTXX().getXHFSBH(), ddzxx.get(i).getDDTXX().getGMFBM());
                    if (buyerEntity != null && StringUtils.isNotBlank(buyerEntity.getTaxpayerCode())) {
                    
                        BeanTransitionUtils.transitionOrderBuyerInfo(commonOrderInfo, buyerEntity);
                    }
                
                }
            
            
                /**
                 *  补全商品简称
                 */
            
                iCommonInterfaceService.dealOrderItem(commonOrderInfo.getOrderItemInfo(), nsrsbh, commonOrderInfo.getOrderInfo().getQdBz(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            
            
                /**
                 * 价税分离
                 */
                TaxSeparateConfig config = new TaxSeparateConfig();
                config.setDealSeType(ConfigureConstant.STRING_1);
                config.setSingleSlSeparateType(ConfigureConstant.STRING_2);
                CommonOrderInfo taxSeparationService;
            
                taxSeparationService = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, config);
            
            
                /**
                 * 后续所有数据依据价税分离后的数据进行操作
                 */
                OrderInfo orderInfo = taxSeparationService.getOrderInfo();
                List<OrderItemInfo> orderItemInfos = taxSeparationService.getOrderItemInfo();
            
                /**
                 * 补全对象转换工具类中不能补充的数据
                 */
                orderInfo.setFpzlDm(ddpcxxReq.getDDPCXX().getFPLXDM());
                orderInfo.setKpjh(kpjh);
                orderInfo.setSld(sldid);
                orderInfo.setSldMc(sldMc);
            
                if (orderItemInfos.size() <= 2 && StringUtils.isNotBlank(orderItemInfos.get(0).getKce())) {
                    if (orderInfo.getKplx().equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey())) {
                        if (StringUtils.isNotBlank(orderInfo.getBz())) {
                            if (!orderInfo.getBz().startsWith(ConfigureConstant.STRING_CEZS)) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(ConfigureConstant.STRING_CEZS_).append(orderItemInfos.get(0).getKce()).append("。").append(orderInfo.getBz());
                                orderInfo.setBz(sb.toString());
                            }
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(ConfigureConstant.STRING_CEZS_).append(orderItemInfos.get(0).getKce()).append("。");
                            orderInfo.setBz(sb.toString());
                        }
                    }
                }
            
                /**
                 * 校验购方信息是否合法
                 */
                Map<String, String> checkGhfParam = validateInterfaceOrder.checkGhfParam(orderInfo, kpfs, terminalCode);
                if (!ConfigureConstant.STRING_0000.equals(checkGhfParam.get(OrderManagementConstant.ERRORCODE))) {
                    log.error("{}购货方数据非空和长度校验未通过，未通过数据:{}", LOGGER_MSG, checkGhfParam);
                    throw new OrderReceiveException(checkGhfParam.get(OrderManagementConstant.ERRORCODE), checkGhfParam.get(OrderManagementConstant.ERRORMESSAGE));
                }
            
                /**
                 * 对订单请求数据进行校验,校验失败直接返回
                 */
                Map<String, String> checkInvParam = validateOrderInfo.checkInvoiceData(taxSeparationService);
                if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
                    log.error("{}数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
                    throw new OrderReceiveException(checkInvParam.get(OrderManagementConstant.ERRORCODE), checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
                }

                /**
                 * 如果是红票需要做折扣行合并操作
                 */
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                    Map<String, Object> mergeResult = apiRushRedInvoiceRequestInfoService.itemMerge(taxSeparationService);
                    if (OrderInfoContentEnum.SUCCESS.getKey().equals(mergeResult.get(OrderManagementConstant.ERRORCODE))) {
                        taxSeparationService = (CommonOrderInfo) mergeResult.get(OrderManagementConstant.DATA);
                        orderItemInfos = taxSeparationService.getOrderItemInfo();
                    } else {
                        log.error("{}合并商品折扣行失败，未通过数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
                        throw new OrderReceiveException(checkInvParam.get(OrderManagementConstant.ERRORCODE), checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
                    }
                }



                String ywlxId = null;
                String ywlx = ddzxx.get(i).getDDTXX().getYWLX();
                String xhfmc = ddzxx.get(i).getDDTXX().getXHFMC();
                if (StringUtils.isNotBlank(ywlx)) {
                    //如果接口传递业务类型不为空，走业务类型采集流程
                    log.info("{} 业务类型采集，业务类型名称：{}，税号：{},销货方名称：{}", LOGGER_MSG, ywlx, nsrsbh, xhfmc);
                    ywlxId = iCommonInterfaceService.yesxInfoCollect(ywlx, nsrsbh, xhfmc);
                }
            
                /**
                 * 保存或更新购方信息
                 */
                BuyerEntity buyerEntity = createBuyerEntity(orderInfo);
                com.dxhy.order.model.R mapResult = apiBuyerService.saveOrUpdateBuyerInfo(buyerEntity);
            
                if (ConfigureConstant.STRING_9999.equals(mapResult.get(OrderManagementConstant.CODE))) {
                    log.warn("{}保存或更新购方信息结果:{}", LOGGER_MSG, mapResult.get(OrderManagementConstant.MESSAGE));
                }
            
            
                String invoiceRequestItemId = apiInvoiceCommonMapperService.getGenerateShotKey();
                DecimalFormat df = new DecimalFormat("000");
                String format = df.format(1);
                String fpqqpch = transitionBatchRequest.getFpqqpch();
                String kplsh = transitionBatchRequest.getFpqqpch() + format;
    
                if (flag) {
                    log.info("{} 异常重开流程", LOGGER_MSG);
    
                    /**
                     * 此时数据不能变动流水号,如果开票失败,需要调用底层再次请求
                     */
                    OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderProcessInfo.getFpqqlsh(), shList);
                    InvoiceQuery query = new InvoiceQuery();
                    query.setFPQQLSH(orderInvoiceInfo.getKplsh());
                    query.setNSRSBH(nsrsbh);
                    query.setTerminalCode(terminalCode);
                    String url = OpenApiConfig.queryInvoiceStatus;
    
                    if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                        url = OpenApiConfig.queryInvoiceStatusNewTax;
                    }
                    CommonInvoiceStatus commonInvoiceStatus = HttpInvoiceRequestUtil.queryInvoiceFinalSatusFromSk(url, query);
                    if (ObjectUtil.isNotNull(commonInvoiceStatus) && OrderInfoContentEnum.SUCCESS.getKey().equals(commonInvoiceStatus.getStatusCode()) && !OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
                        /**
                         * 如果发票数据不为空,并且不是开票成功状态,不是初始化状态,请求底层进行不换流水号开票
                         */
                        if (ObjectUtil.isNotNull(orderInvoiceInfo) && !OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(orderInvoiceInfo.getKpzt()) && !OrderInfoEnum.INVOICE_STATUS_0.getKey().equals(orderInvoiceInfo.getKpzt())) {
                            /**
                             * 数据放入开票队列
                             */
                            fpqqpch = orderInvoiceInfo.getKplsh().substring(0, orderInvoiceInfo.getKplsh().length() - 3);
                            kplsh = orderInvoiceInfo.getKplsh();
    
                            continue;
                        }
        
                    }
    
    
                    /**
                     * 订单入库前数据补全
                     */
                    orderInfo.setId(orderInvoiceInfo.getOrderInfoId());
                    orderInfo.setFpqqlsh(orderInvoiceInfo.getFpqqlsh());
                    orderInfo.setProcessId(orderProcessInfo.getId());
    
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
                    processInfo.setOrderInfoId(orderInfo.getId());
                    //订单处理表保存业务类型名称和Id
                    processInfo.setYwlx(ywlx);
                    processInfo.setYwlxId(ywlxId);
                    //订单处理表保存开票方式   企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
                    processInfo.setKpfs(kpfs);
                    //订单处理表保存订单请求批次号
                    processInfo.setDdqqpch(orderBatchRequest.getDdqqpch());
                    processInfo.setId(orderProcessInfo.getId());
                    processInfo.setProtocolType(protocol_type);
                    /**
                     * 判断开票方式,如果开票方式为非自动开票.需要修改订单处理表状态为初始化.
                     */
                    boolean kpz = StringUtils.isNotBlank(ddpcxxReq.getDDPCXX().getKPFS()) && (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS())
                            || OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()));
                    if (kpz) {
                        processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_4.getKey());
                    }
                    updateProcessInfo.add(processInfo);
    
    
                    /**
                     * 订单发票表入库前数据补全
                     * todo 2019-04-10添加订单可冲红金额  蓝票为开票价税合计，红票为0
                     */
                    OrderInvoiceInfo invoiceInfo = new OrderInvoiceInfo();
                    BeanTransitionUtils.transitionOrderInvoiceInfo(invoiceInfo, orderInfo);
                    invoiceInfo.setOrderInfoId(orderProcessInfo.getOrderInfoId());
                    invoiceInfo.setOrderProcessInfoId(orderProcessInfo.getId());
                    invoiceInfo.setKplsh(kplsh);
                    if (ConfigureConstant.STRING_0.equals(orderInfo.getKplx())) {
                        invoiceInfo.setSykchje(orderInfo.getKphjje());
                    } else if (ConfigureConstant.STRING_1.equals(orderInfo.getKplx())) {
                        invoiceInfo.setSykchje(ConfigureConstant.STRING_0);
                    }
    
                    /**
                     * 清单标志赋值
                     */
                    if (commonOrderInfo.getOrderItemInfo().size() > 8) {
                        invoiceInfo.setQdbz(OrderInfoEnum.QDBZ_CODE_0.getKey());
                    } else {
                        invoiceInfo.setQdbz(OrderInfoEnum.QDBZ_CODE_1.getKey());
                    }
                    updateInvoiceInfo.add(invoiceInfo);
                } else {
                    /**
                     * 提前赋值业务类型
                     */
                    if (StringUtils.isNotBlank(kpfs) && OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(kpfs)) {
                        //根据发票请求流水号查询业务类型
                        if (info != null) {
                            orderInfo.setYwlx(info.getYwlx());
                            orderInfo.setYwlxId(info.getYwlxId());
                        }
                    } else {
                        orderInfo.setYwlx(ywlx);
                        orderInfo.setYwlxId(ywlxId);
                    }
                    OrderProcessInfo processInfo = new OrderProcessInfo();
                    OrderInvoiceInfo invoiceInfo = new OrderInvoiceInfo();
                
                    iCommonInterfaceService.buildInsertOrderData(orderInfo, orderItemInfos, processInfo, invoiceInfo);
                
                    //动态码用原有订单的id
                    if (qrcodeOrderInfo != null) {
                        orderInfo.setId(qrcodeOrderInfo.getId());
                        orderInfo.setProcessId(qrcodeOrderInfo.getProcessId());
                        orderInfo.setFpqqlsh(orderInfo.getFpqqlsh());
                    
                        processInfo.setId(qrcodeOrderInfo.getProcessId());
                        processInfo.setOrderInfoId(qrcodeOrderInfo.getId());
                        processInfo.setFpqqlsh(qrcodeOrderInfo.getFpqqlsh());
                    
                        invoiceInfo.setOrderInfoId(qrcodeOrderInfo.getId());
                        invoiceInfo.setOrderProcessInfoId(qrcodeOrderInfo.getProcessId());
                        invoiceInfo.setFpqqlsh(qrcodeOrderInfo.getFpqqlsh());
                    
                        for (OrderItemInfo orderItem : orderItemInfos) {
                            orderItem.setOrderInfoId(qrcodeOrderInfo.getId());
                        }
                    }
    
                    //订单处理表保存开票方式   企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
                    processInfo.setKpfs(kpfs);
                    //订单处理表保存订单请求批次号
                    processInfo.setDdqqpch(orderBatchRequest.getDdqqpch());
                        processInfo.setProtocolType(protocol_type);
                    invoiceInfo.setKplsh(kplsh);
                    /**
                     * 判断开票方式,如果开票方式为非自动开票.需要修改订单处理表状态为初始化.
                     */
                    boolean kpz = StringUtils.isNotBlank(ddpcxxReq.getDDPCXX().getKPFS()) && (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS())
                            || OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()));
                    if (kpz) {
                        processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_4.getKey());
                    }
    
                    if (OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS())) {
                        processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_5.getKey());
                    } else if (OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS())) {
                        processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_6.getKey());
                    }
        
                    //插入原始订单关系表
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
                    insertOrder.add(orderInfo);
                    insertOrderItem.add(orderItemInfos);
                    insertProcessInfo.add(processInfo);
                    insertInvoiceInfo.add(invoiceInfo);
                }
    
                invoiceBatchRequestList.add(transitionBatchRequest);
                List<InvoiceBatchRequestItem> invoiceBatchRequestItems = new ArrayList<>();
                /**
                 * 发票开具请求明细表入库前数据补全
                 */
                InvoiceBatchRequestItem invoiceBatchRequestItem = new InvoiceBatchRequestItem();
                invoiceBatchRequestItem.setId(invoiceRequestItemId);
                invoiceBatchRequestItem.setInvoiceBatchId(invoiceRequestId);
                invoiceBatchRequestItem.setFpqqpch(fpqqpch);
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
                fpkjMqData.setFpqqpch(fpqqpch);
                fpkjMqData.setKplsh(kplsh);
                fpkjMqData.setNsrsbh(orderInfo.getXhfNsrsbh());
                fpkjMqData.setTerminalCode(terminalCode);
                fpkjMqDataList.add(fpkjMqData);
    
            }
            /**
             * 手动导入判断
             */
            boolean boolImport = StringUtils.isNotBlank(ddpcxxReq.getDDPCXX().getKPFS()) && (OrderInfoEnum.ORDER_REQUEST_TYPE_1.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()) || OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()));
            if (boolImport) {
                apiInvoiceCommonMapperService.saveData(insertOrder, insertOrderItem, insertProcessInfo, null, null, null, orderOriginList, shList);
            } else {
                //动态码领票的只更新购方信息11
                if (StringUtils.isNotBlank(ddpcxxReq.getDDPCXX().getKPFS()) && OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()) && qrcodeOrderInfoList.size() > 0) {
        
                    apiInvoiceCommonMapperService.saveDynamicQrCodeInfo(invoiceBatchRequestList, insertOrder, insertOrderItem, insertProcessInfo, insertBatchItem, insertInvoiceInfo, orderBatchRequest, updateProcessInfo, updateInvoiceInfo, orderOriginList, shList);
                } else {
                    apiInvoiceCommonMapperService.saveData(invoiceBatchRequestList, insertOrder, insertOrderItem, insertProcessInfo, insertBatchItem, insertInvoiceInfo, orderBatchRequest, updateProcessInfo, updateInvoiceInfo, orderOriginList, shList);
                }
            }
        
            /**
             * 开票方式特殊处理,如果开票方式为自动开票和动态码开票,就放入队列中,如果不是自动开票和动态码开票就不放入
             */
            if (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS()) || OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxxReq.getDDPCXX().getKPFS())) {
        
                if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    log.info("终端号为{}时，消息放入redis中", terminalCode);
                    String registCodeByRedis = apiFangGeInterfaceService.getRegistCodeByRedis(ddpcxxReq.getDDPCXX().getNSRSBH(), sldid);
                    if (StringUtils.isEmpty(registCodeByRedis)) {
                        log.error("redis中没有获取到注册码信息，税号为：{},机器编号为：{}", ddpcxxReq.getDDPCXX().getNSRSBH(), sldid);
                        ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
                        ddpcxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010014_V3.getKey());
                        ddpcxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010014_V3.getMessage());
                        return ddpcxxRsp;
                    }
                    /**
                     * 批量存放redis队列,用于发送mqtt消息
                     */
                    if (insertBatchItem != null && insertBatchItem.size() > 0) {
                        for (List<InvoiceBatchRequestItem> invoiceBatchRequestItem : insertBatchItem) {
                            for (InvoiceBatchRequestItem batchRequestItem : invoiceBatchRequestItem) {
                                /**
                                 *  方格存放开票信息到消息队列
                                 */
                                if (StringUtils.isNotEmpty(registCodeByRedis)) {
                                    RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeByRedis, RegistrationCode.class);
                                    /**
                                     * 存放开票信息到redis队列
                                     */
                                    PushPayload pushPayload = new PushPayload();
                                    //接口发票开具
                                    pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);
                                    pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                                    pushPayload.setJQBH(registrationCode.getJqbh());
                                    pushPayload.setZCM(registrationCode.getZcm());
                                    pushPayload.setDDQQLSH(batchRequestItem.getFpqqlsh());
                                    apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                                    //存放redis成功，修改开票状态为开票中
                                    boolean isSuccess = apiOrderProcessService.updateKpzt(batchRequestItem.getFpqqlsh(), OrderInfoEnum.ORDER_STATUS_4.getKey(), OrderInfoEnum.INVOICE_STATUS_1.getKey(), "", shList);
                                    if (isSuccess) {
                                        log.info("===========》开票中状态发票数据更新成功，流水号为：[{}]", batchRequestItem.getFpqqlsh());
                                    }
                                }
                            }
                        }
                    }
                    ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
                    ddpcxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getKey());
                    ddpcxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getMessage());
                    return ddpcxxRsp;
                } else {
                    for (FpkjMqData fpkjMqData : fpkjMqDataList) {
                        String jsonString = JsonUtils.getInstance().toJsonString(fpkjMqData);
                        log.debug("{}放入mq的数据:{}", LOGGER_MSG, jsonString);
                        com.dxhy.order.model.R openAnInvocie = openInvoiceService.openAnInvoice(jsonString, fpkjMqData.getNsrsbh());
                        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(openAnInvocie.get(OrderManagementConstant.CODE)))) {
                            throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_ERROR_CODE_010014_V3);
                        }
                    }
                }
    
    
            }
        
        } catch (OrderSeparationException e) {
            ddqqlshList.forEach(ddqqlsh -> iCommonInterfaceService.setDdqqlshRedisStatus(ddqqlsh, ""));
            log.error("{}价税分离未通过,查看provider获取异常数据,{}", LOGGER_MSG, e.getMessage());
            ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
            ddpcxxRsp.setZTDM(e.getCode());
            ddpcxxRsp.setZTXX(e.getMessage());
            return ddpcxxRsp;
        } catch (OrderReceiveException e) {
            ddqqlshList.forEach(ddqqlsh -> iCommonInterfaceService.setDdqqlshRedisStatus(ddqqlsh, ""));
            log.error("{}开票接口接收数据异常:{}", LOGGER_MSG, e.getMessage());
            ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
            ddpcxxRsp.setZTDM(e.getCode());
            ddpcxxRsp.setZTXX(e.getMessage());
            return ddpcxxRsp;
        } catch (Exception e) {
            ddqqlshList.forEach(ddqqlsh -> iCommonInterfaceService.setDdqqlshRedisStatus(ddqqlsh, ""));
            log.error("{}开票接口保存数据异常:{}", LOGGER_MSG, e);
            ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
            ddpcxxRsp.setZTDM(OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            ddpcxxRsp.setZTXX(OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            return ddpcxxRsp;
        }
    
        ddqqlshList.forEach(ddqqlsh -> iCommonInterfaceService.setDdqqlshRedisStatus(ddqqlsh, ""));
    
        ddpcxxRsp.setDDQQPCH(orderBatchRequest.getDdqqpch());
        ddpcxxRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getKey());
        ddpcxxRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getMessage());
    
        return ddpcxxRsp;
    }
    
}
