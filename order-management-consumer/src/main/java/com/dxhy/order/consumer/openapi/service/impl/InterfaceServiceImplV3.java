package com.dxhy.order.consumer.openapi.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.config.SystemConfig;
//import com.dxhy.order.consumer.modules.fzyy.FzyyTaxEquipmentController;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceCountService;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceService;
import com.dxhy.order.consumer.modules.invoice.service.SpecialInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.openapi.service.IInterfaceServiceV3;
import com.dxhy.order.consumer.openapi.service.IValidateTaxEquipmentInfo;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.hp.HpInvocieRequest;
import com.dxhy.order.model.a9.hp.HpResponseBean;
import com.dxhy.order.model.a9.hp.HpUploadResponse;
import com.dxhy.order.model.a9.hp.HzfpsqbsReq;
import com.dxhy.order.model.a9.kp.CommonInvoiceStatus;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.vo.CountSurplusVO;
import com.dxhy.order.model.vo.CountToB;
import com.dxhy.order.protocol.v4.buyermanage.*;
import com.dxhy.order.protocol.v4.commodity.*;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_REQ;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_RSP;
import com.dxhy.order.protocol.v4.invoice.*;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_RSP;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 订单对外接口业务实现类
 *
 * @author: chengyafu
 * @date: 2018年8月9日 下午4:15:27
 */
@Service
@Slf4j
public class InterfaceServiceImplV3 implements IInterfaceServiceV3 {
    
    private static final String NEGATIVE_1 = "-1";
    
    private static final String LOGGER_MSG = "(订单对外接口业务类V3)";

    private static final String LOGGER_MSG_V4 = "(订单对外接口业务类V4)";

    /**
     *  模糊查询的标识(0表示不进行模糊查询，1表示模糊查询)
     */
    private static final String FUZZY_QUERY_NO_FLAG = "0";
    private static final String FUZZY_QUERY_YES_FLAG = "1";

    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Reference
    private ApiQuickCodeInfoService apiQuickCodeInfoService;
    
    @Reference
    private IValidateInterfaceSpecialInvoice validateInterfaceSpecialInvoice;

    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;

    @Reference
    private ApiCommodityService apiCommodityService;

    @Reference
    private ApiVerifyCommodityCode apiVerifyCommodityCode;
    
    @Reference
    private ApiVerifyBuyerManageInfo apiVerifyBuyerManageInfo;
    
    @Reference
    private ApiBuyerService apiBuyerService;
    
    @Resource
    private SpecialInvoiceService specialInvoiceService;
    
    @Reference
    private RedisService redisService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    @Resource
    private IValidateTaxEquipmentInfo iValidateTaxEquipmentInfo;
    
    @Resource
    private InvoiceCountService invoiceCountService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Resource
    private InvoiceService invoiceService;
    
    @Reference
    private ApiHistoryDataPdfService historyDataPdfService;
    
    /**
     * 发票结果查询接口业务处理
     *
     * @param ddkjxxReq
     * @return
     */
    @Override
    public DDKJXX_RSP getAllocatedInvoicesV3(DDKJXX_REQ ddkjxxReq) {
        
        DDKJXX_RSP getInvoiceRsp = new DDKJXX_RSP();
        getInvoiceRsp.setDDQQPCH(ddkjxxReq.getDDQQPCH());
        /**
         * 校验查询的字段是否为空
         */
        getInvoiceRsp = checkAllocatedInvoicesRequest(ddkjxxReq);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(getInvoiceRsp.getZTDM())) {
            return getInvoiceRsp;
        }
        boolean returnfail = false;
        if (StringUtils.isNotBlank(ddkjxxReq.getSFFHSBSJ())) {
            if (ConfigureConstant.STRING_0.equals(ddkjxxReq.getSFFHSBSJ())) {
                returnfail = true;
            }
        }

        //转换发票种类代码，兼容新税控
        ddkjxxReq.setFPLXDM(CommonUtils.transFpzldm(ddkjxxReq.getFPLXDM()));

        /**
         * todo 底层不愿意新增税号返回,业务系统自己维护,支持mycat操作
         * 本次新增mycat查询,根据销方税号做分片规则,所以需要底层返回销方税号,如果返回为空不进行操作
         */
        if (StringUtils.isBlank(ddkjxxReq.getNSRSBH()) && StringUtils.isNotBlank(ddkjxxReq.getDDQQPCH())) {
            /**
             * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
             *
             */
            String cacheFpqqpch = String.format(Constant.REDIS_FPQQPCH, ddkjxxReq.getDDQQPCH());
            String xhfNsrsbh = redisService.get(cacheFpqqpch);
            if (StringUtils.isBlank(xhfNsrsbh)) {
                List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessInfoByDdqqpch(ddkjxxReq.getDDQQPCH(), null);
                if (orderProcessInfos != null && orderProcessInfos.size() > 0 && StringUtils.isNotBlank(orderProcessInfos.get(0).getXhfNsrsbh())) {
        
                    redisService.set(cacheFpqqpch, orderProcessInfos.get(0).getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                    xhfNsrsbh = orderProcessInfos.get(0).getXhfNsrsbh();
                }
            }
        
            ddkjxxReq.setNSRSBH(xhfNsrsbh);
        }
        List<String> shList = new ArrayList<>();
        shList.add(ddkjxxReq.getNSRSBH());
        /**
         * 1.根据订单请求批次号获取订单处理表中的数据
         *  如果查询不到数据,就返回失败
         * 2.查询到数据后,根据数据判断,返回开票成功的数据给客户
         */
    
        List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessInfoByDdqqpch(ddkjxxReq.getDDQQPCH(), shList);
        if (orderProcessInfos == null || orderProcessInfos.size() <= 0) {
            log.error("{}发票开具结果数据获取，请求批次号不存在!", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_204001_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_204001_V3.getMessage());
            return getInvoiceRsp;
        }
        //2.根据查询出来的数据,循环处理.
        List<FPZXX> fpzxxes = new ArrayList<>();
        int successCount = 0;
        int invoiceingCount = 0;
        for (OrderProcessInfo orderProcessInfo : orderProcessInfos) {
            FPZXX fpzxx = new FPZXX();
            //根据处理表的发票请求流水号获取发票表数据.
            OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderProcessInfo.getFpqqlsh(), shList);
            if (orderInvoiceInfo == null) {
                /**
                 * 如果查询到发票数据为空,并且不返回失败数据,则直接跳过当前数据
                 */
                if (!returnfail) {
                    continue;
                }
                
            } else {
                //如果发票代码号码不等于空,并且发票状态不是开票中,则返回数据,
                if (StringUtils.isNotBlank(orderInvoiceInfo.getFpdm()) && StringUtils.isNotBlank(orderInvoiceInfo.getFphm()) && !OrderInfoEnum.INVOICE_STATUS_1.getKey().equals(orderInvoiceInfo.getKpzt())) {
                    fpzxx = BeanTransitionUtils.transitionCommonInvoiceInfoV3(orderInvoiceInfo);
                    
                } else {
                    /**
                     * 如果发票代码号码为空,并且不返回失败数据,则跳过当前数据
                     */
                    if (!returnfail) {
                        continue;
                    }
                }
            }
            /**
             * 赋值数据对应的订单状态
             */
            fpzxx.setDDQQLSH(orderProcessInfo.getFpqqlsh());
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

                    /**
                     * 订单状态为异常时,需要调用底层提供的查询接口进行查询,判断是否是开具中状态
                     *
                     */
                    invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021999_V3;
                    if (ObjectUtil.isNotNull(orderInvoiceInfo) && StringUtils.isNotBlank(orderInvoiceInfo.getFpqqlsh())) {

                        /**
                         * 税控设备为C48和A9的数据,如果是开票失败的,就是开票失败的直接返回,不用查询最终状态去判断是否需要进行编辑
                         * 其他税控设备还需要判断下最终状态,看下底层是否是真的开票失败
                         */
                        String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInvoiceInfo.getXhfNsrsbh());
                        if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {

                        } else {
                            CommonInvoiceStatus commonInvoiceStatus = invoiceService.queryInvoiceStatus(orderInvoiceInfo.getFpqqlsh(), orderInvoiceInfo.getXhfNsrsbh());
                            if (ObjectUtil.isNotNull(commonInvoiceStatus) && ConfigureConstant.STRING_0000.equals(commonInvoiceStatus.getStatusCode())) {

                                // 2101换流水号,其他都不换,2001时提示开票失败,不允许编辑,不换流水号直接重试.
                                if (OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
                                    invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021999_V3;

                                } else {
                                    invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021002_V3;

                                }

                            }
                        }

                    }


                } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(orderProcessInfo.getDdzt())) {
                    invoiceStatus = OrderInfoContentEnum.INVOICE_ERROR_CODE_021003_V3;
                }
            }
            fpzxx.setZTDM(invoiceStatus.getKey());
            fpzxx.setZTXX(StringUtils.isBlank(orderProcessInfo.getSbyy()) ? "" : orderProcessInfo.getSbyy());
            fpzxxes.add(fpzxx);
        }
        if (successCount == orderProcessInfos.size()) {
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_020000_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_020000_V3.getMessage());
        } else if (successCount == 0) {
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_020002_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_020002_V3.getMessage());
            if (invoiceingCount > 0) {
                getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_020111_V3.getKey());
                getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_020111_V3.getMessage());
            }
        } else if (successCount < orderProcessInfos.size()) {
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_020001_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_020001_V3.getMessage());
        }
        if (fpzxxes.size() > 0) {
            
            getInvoiceRsp.setFPZXX(fpzxxes);
        }
        
        return getInvoiceRsp;
        
    }
    
    /**
     * 数据校验
     */
    private DDKJXX_RSP checkAllocatedInvoicesRequest(DDKJXX_REQ ddkjxxReq) {
        DDKJXX_RSP getInvoiceRsp = new DDKJXX_RSP();
        getInvoiceRsp.setDDQQPCH(ddkjxxReq.getDDQQPCH());
        if (ddkjxxReq == null) {
            log.error("{}发票开具结果数据获取，请求对象不能为空", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104001_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104001_V3.getMessage());
            return getInvoiceRsp;
        }
        if (StringUtils.isBlank(ddkjxxReq.getDDQQPCH())) {
            log.error("{}发票开具结果数据获取，请求批次号不能为空", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104002_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104002_V3.getMessage());
            return getInvoiceRsp;
        } else if (ddkjxxReq.getDDQQPCH().length() > ConfigureConstant.INT_40) {
            log.error("{}发票开具结果数据获取，请求批次号长度不匹配", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104003_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104003_V3.getMessage());
            return getInvoiceRsp;
        }
        if (StringUtils.isBlank(ddkjxxReq.getFPLXDM())) {
            log.error("{}发票开具结果数据获取，发票类型不能为空", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104004_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104004_V3.getMessage());
            return getInvoiceRsp;
        } else if (ddkjxxReq.getFPLXDM().length() > ConfigureConstant.INT_3) {
            log.error("{}发票开具结果数据获取，发票类型长度不匹配", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104005_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104005_V3.getMessage());
            return getInvoiceRsp;
        }
        //订单请求发票类型合法性
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddkjxxReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddkjxxReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(ddkjxxReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(ddkjxxReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddkjxxReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddkjxxReq.getFPLXDM())) {
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104006_V3.getMessage());
            return getInvoiceRsp;
        }
        /**
         * 订单请求纳税人识别号
         */
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_108034, OrderInfoContentEnum.CHECK_ISS7PRI_107006, OrderInfoContentEnum.CHECK_ISS7PRI_107163, ddkjxxReq.getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104009_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104009_V3.getMessage());
            return getInvoiceRsp;
        } else if (StringUtils.isBlank(ddkjxxReq.getNSRSBH())) {
            log.error("{}发票开具结果数据获取，税号不能为空", LOGGER_MSG);
            getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104010_V3.getKey());
            getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104010_V3.getMessage());
            return getInvoiceRsp;
        }
        /**
         * 是否返回失败,0:返回失败;1:不返回失败;默认为1;
         */
        if (StringUtils.isBlank(ddkjxxReq.getSFFHSBSJ())) {
        
        } else {
            if (!ConfigureConstant.STRING_0.equals(ddkjxxReq.getSFFHSBSJ()) && !ConfigureConstant.STRING_1.equals(ddkjxxReq.getSFFHSBSJ())) {
                
                log.error("{}发票开具结果数据获取，是否返回失败数据参数只能为0或1", LOGGER_MSG);
                getInvoiceRsp.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_104007_V3.getKey());
                getInvoiceRsp.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_104007_V3.getMessage());
                return getInvoiceRsp;
            }
        }
        
        getInvoiceRsp.setZTDM(OrderInfoContentEnum.SUCCESS.getKey());
        getInvoiceRsp.setZTXX(OrderInfoContentEnum.SUCCESS.getMessage());
        return getInvoiceRsp;
    }
    
    /**
     * 根据订单号获取订单数据接口业务逻辑处理
     *
     * @param ddfpcxReq
     * @return
     */
    @Override
    public DDFPCX_RSP getOrderInfoAndInvoiceInfoV3(DDFPCX_REQ ddfpcxReq) {
        String jsonString2 = JsonUtils.getInstance().toJsonString(ddfpcxReq);
        log.debug("{},根据订单号获取订单数据以及发票数据接口数据:{}", LOGGER_MSG, jsonString2);
        
        DDFPCX_RSP ddfpcxRsp = new DDFPCX_RSP();
        ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getKey());
        ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getMessage());
        
        try {
            if (ddfpcxReq == null) {
                ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL_V3_009661.getKey());
                ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NULL_V3_009661.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据为空", LOGGER_MSG);
                return ddfpcxRsp;
            } else if (StringUtils.isBlank(ddfpcxReq.getNSRSBH())) {
                ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH_V3_009662.getKey());
                ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_NSRSBH_V3_009662.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据销方税号为空", LOGGER_MSG);
                return ddfpcxRsp;
            } else if (StringUtils.isBlank(ddfpcxReq.getDDH()) && StringUtils.isBlank(ddfpcxReq.getDDQQLSH()) && StringUtils.isBlank(ddfpcxReq.getTQM())) {
                ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_DDH_OR_DDQQLSH_V3_009666.getKey());
                ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_PARAM_DDH_OR_DDQQLSH_V3_009666.getMessage());
                log.error("{},根据订单号获取订单数据以及发票数据接口,请求数据订单号和订单请求流水号不能同时为空", LOGGER_MSG);
                return ddfpcxRsp;
            }
            
            /**
             * 根据请求数据获取对应的订单信息.
             * 1.循环数据列表
             * 2.补全头信息
             * 3.根据订单状态判断是否需要查询发票表数据进行补全
             * 4.补全明细信息
             */
            List<String> shList = new ArrayList<>();
            shList.add(ddfpcxReq.getNSRSBH());
            Map<String, Object> paraMap = new HashMap<>(5);
            paraMap.put(ConfigureConstant.REQUEST_PARAM_FPQQLSH, ddfpcxReq.getDDQQLSH());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_DDH, ddfpcxReq.getDDH());
            paraMap.put(ConfigureConstant.REQUEST_PARAM_TQM, ddfpcxReq.getTQM());
            
            List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(paraMap, shList);
            
            List<OrderProcessInfo> finalList = new ArrayList<>(orderProcessInfos);
            
            for (OrderProcessInfo orderProcessInfo : orderProcessInfos) {
                
                List<OrderProcessInfo> findChildList = apiOrderProcessService.findChildList(orderProcessInfo.getId(), shList);
                finalList.addAll(findChildList);
            }
    
            if (finalList == null || finalList.size() <= 0) {
                ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getKey());
                ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getMessage());
                log.error("{},根据订单号:{}获取订单数据以及发票数据接口,查询订单处理表数据为空", LOGGER_MSG, ddfpcxReq.getDDH());
                return ddfpcxRsp;
            }
            
            List<DDFPZXX> commonOrderInvocies = new ArrayList<>();
            /**
             * 遍历数据
             */
            for (int i = 0; i < finalList.size(); i++) {
                DDFPZXX ddfpzxx = new DDFPZXX();
                /**
                 * 根据orderid查询order信息表数据.
                 */
                OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
                OrderProcessInfo orderProcessInfo = finalList.get(i);
                OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderProcessInfo.getOrderInfoId(), shList);
                String ddzt = orderProcessInfo.getDdzt();
                String sbyy = orderProcessInfo.getSbyy();
                if (orderInfo == null) {
                    ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getKey());
                    ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getMessage());
                    log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求id为:{}查询订单表数据为空", LOGGER_MSG, ddfpcxReq.getDDH(), orderProcessInfo.getOrderInfoId());
                    return ddfpcxRsp;
                }
                if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(ddzt)) {
                    orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(orderProcessInfo.getFpqqlsh(), shList);
                    if (orderInvoiceInfo == null) {
                        ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getKey());
                        ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_NULL_V3_009664.getMessage());
                        log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求id为:{}查询订单发票表数据为空", LOGGER_MSG, ddfpcxReq.getDDH(), orderProcessInfo.getId());
                        return ddfpcxRsp;
                    }
                }
                
                
                /**
                 * 数据组装
                 * 订单状态  0 未开具 1 开具成功 2 开具失败
                 * ==>订单未开具状态 通过 流水号查询 批次明细表获取message
                 */
                DDFPXX ddfpxx = com.dxhy.order.utils.BeanTransitionUtils.transitionORDER_INVOICE_INFOV3(orderInfo, orderProcessInfo, orderInvoiceInfo);
    
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
                boolean checkFinalResult = false;
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
                        checkFinalResult = true;
                        ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                        ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());
                    } else if (OrderInfoEnum.ORDER_STATUS_11.getKey().equals(orderProcessInfo.getDdzt())) {
                        ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getKey());
                        ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002999.getMessage());
                    }
                }
    
                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(ddzt)
                        || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_4.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(ddzt) ||
                        OrderInfoEnum.ORDER_STATUS_10.getKey().equals(ddzt)) {
                    ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getKey());
                    ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());
                    /**
                     *  查询message,如果为空继续使用原提示
                     *  1.根据请求的流水号查询批次明细信息
                     *  2.如果查不到，或者查到的message为空，都是用原来的提示
                     *   否则使用数据库中的提示
                     *  注意1：下面电票的时候还会有一次查询，并不冲突，两种情况不会并存
                     *  注意2：批次号唯一，只有一条，此处直接取第一条
                     */
                    List<InvoiceBatchRequestItem> invoiceBatchRequestItems = apiInvoiceCommonService.selectInvoiceBatchItemByFpqqlsh(ddfpxx.getDDQQLSH(), shList);
                    if (invoiceBatchRequestItems != null && invoiceBatchRequestItems.size() > 0) {
            
                        for (int j = 0; j < invoiceBatchRequestItems.size(); j++) {
                            if (!StringUtils.isBlank(invoiceBatchRequestItems.get(i).getMessage())) {
                                ddfpcxRsp.setZTXX(invoiceBatchRequestItems.get(i).getMessage());
                            }
                        }
                    }
        
                    if (StringUtils.isBlank(ddfpcxRsp.getZTDM())) {
                        ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());
                    }
                } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(ddzt)) {
                    ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getKey());
                    ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_000000.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(ddzt) || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(ddzt)) {
                    checkFinalResult = true;
                    ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                    ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());
                    if (!StringUtils.isBlank(sbyy)) {
                        ddfpcxRsp.setZTXX(sbyy);
                    }
        
                }

                /**
                 * 订单状态为异常时,需要调用底层提供的查询接口进行查询,判断是否是开具中状态
                 *
                 */
                /**
                 * 税控设备为C48和A9的数据,如果是开票失败的,就是开票失败的直接返回,不用查询最终状态去判断是否需要进行编辑
                 * 其他税控设备还需要判断下最终状态,看下底层是否是真的开票失败
                 */
                String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInvoiceInfo.getXhfNsrsbh());
                if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {
                    checkFinalResult = false;
                }
                if (checkFinalResult) {
                    if (ObjectUtil.isNotNull(orderInvoiceInfo) && StringUtils.isNotBlank(orderInvoiceInfo.getFpqqlsh())) {

                        CommonInvoiceStatus commonInvoiceStatus = invoiceService.queryInvoiceStatus(orderInvoiceInfo.getFpqqlsh(), orderInvoiceInfo.getXhfNsrsbh());
                        if (ObjectUtil.isNotNull(commonInvoiceStatus) && ConfigureConstant.STRING_0000.equals(commonInvoiceStatus.getStatusCode())) {

                            // 2101换流水号,其他都不换,2001时提示开票失败,不允许编辑,不换流水号直接重试.
                            if (OrderInfoEnum.INVOICE_QUERY_STATUS_2101.getKey().equals(commonInvoiceStatus.getFpzt())) {
                                /**
                                 * 判断订单错误数据是否已经存在?
                                 * 如果存在就返回订单异常原因
                                 * 如果不存在就返回底层错误原因
                                 */

                                ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                                ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());

                                ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getKey());
                                ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001999.getMessage());
                                if (StringUtils.isNotEmpty(sbyy)) {
                                    ddfpxx.setDDZTXX(sbyy);
                                    ddfpcxRsp.setZTXX(sbyy);
                                }
                            } else {
                                ddfpxx.setDDZT(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getKey());
                                ddfpxx.setDDZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_002000.getMessage());

                                ddfpcxRsp.setZTDM(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getKey());
                                ddfpcxRsp.setZTXX(OrderInfoContentEnum.INTERFACE_GETORDERANDINVOICE_STATUS_001000.getMessage());

                            }

                        }
                    }
                }


                if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddfpxx.getFPLXDM()) && StringUtils.isNotEmpty(ddfpxx.getFPDM()) && StringUtils.isNotEmpty(ddfpxx.getFPHM())) {
                    /**
                     * 根据发票代码号码调用接口获取pdf字节流
                     */
                    if (ObjectUtil.isNotEmpty(orderInvoiceInfo)) {
        
                        /**
                         * 方格UKey的电票调用monggodb获取数据
                         */
                        if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                            HistoryDataPdfEntity historyDataPdfEntity = historyDataPdfService.find(ddfpxx.getFPDM(), ddfpxx.getFPHM(), shList);
                            if (Objects.nonNull(historyDataPdfEntity)) {
                                ddfpxx.setPDFZJL(historyDataPdfEntity.getPdfFileData());
                            }
                        } else {
                            String fpqqpch = orderInvoiceInfo.getKplsh().substring(ConfigureConstant.INT_0, orderInvoiceInfo.getKplsh().length() - 3);
                            GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(fpqqpch, orderProcessInfo.getXhfNsrsbh(), terminalCode, ddfpxx.getFPDM(), ddfpxx.getFPHM(), orderInvoiceInfo.getPdfUrl());
                            GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
            
                            if (OrderInfoContentEnum.INVOICE_ERROR_CODE_114004.getKey().equals(pdf.getSTATUS_CODE()) || pdf.getResponse_EINVOICE_PDF().size() <= 0) {
                
                                ddfpcxRsp.setZTDM(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getKey());
                                ddfpcxRsp.setZTXX(OrderInfoContentEnum.INVOICE_QUERY_ERROR_V3_009562.getMessage());
                                log.error("{},根据订单号:{}获取订单数据以及发票数据接口,调用pdf获取接口返回数据为空", LOGGER_MSG, ddfpcxReq.getDDH());
                                return ddfpcxRsp;
                            }
                            ddfpxx.setPDFZJL(pdf.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
                        }
        
        
                    }
                    if (StringUtils.isNotBlank(ddfpxx.getPDFDZ()) && !ddfpxx.getPDFDZ().contains("http:")) {
                        ddfpxx.setPDFDZ("");
                    }
        
                    //暂时不返回pdfurl
                    ddfpxx.setPDFDZ("");
    
                    //补全动态码
                    if (StringUtils.isNotBlank(ddfpxx.getTQM())) {
                        QuickResponseCodeInfo quickResponseCodeInfo = apiQuickCodeInfoService.queryQrCodeDetailByTqm(ddfpxx.getTQM(), shList, null);
                        if (quickResponseCodeInfo != null) {
                            ddfpxx.setDTM(String.format(OpenApiConfig.qrCodeShortUrl, quickResponseCodeInfo.getTqm()));
                        }
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
    
                /**
                 * 组装明细信息
                 */
                List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
                if (orderItemInfos == null || orderItemInfos.size() <= 0) {
                    ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL_V3_009665.getKey());
                    ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_DATA_ITEM_NULL_V3_009665.getMessage());
                    log.error("{},根据订单号:{}获取订单数据以及发票数据接口,请求查询参数为:{}明细数据为空", LOGGER_MSG, ddfpcxReq.getDDH(), orderInfo.getId());
                    return ddfpcxRsp;
                }
                List<DDMXXX> orderInvoiceItems = com.dxhy.order.utils.BeanTransitionUtils.transitionORDER_INVOICE_ITEMV3(orderItemInfos);
    
                /**
                 *
                 * 组装订单拆分合并关系协议信息：
                 * 查询（当前订单、和当前订单有拆分合并关系的订单）的原始订单
                 */
                List<OrderProcessInfo> orderProcessInfoRelevantList = apiOrderProcessService.findTopParentList(orderProcessInfo, shList);
                List<DDKZXX> orderExtensionInfos = com.dxhy.order.utils.BeanTransitionUtils.transitionORDER_EXTENSION_INFOS(orderProcessInfoRelevantList);
    
                ddfpzxx.setDDFPXX(ddfpxx);
                ddfpzxx.setDDMXXX(orderInvoiceItems);
                ddfpzxx.setDDKZXX(orderExtensionInfos);
                commonOrderInvocies.add(ddfpzxx);
            }
            
            ddfpcxRsp.setDDFPZXX(commonOrderInvocies);
            
        } catch (Exception e) {
            ddfpcxRsp.setZTDM(OrderInfoContentEnum.GET_ORDERS_INVOICE_ERROR_V3_009999.getKey());
            ddfpcxRsp.setZTXX(OrderInfoContentEnum.GET_ORDERS_INVOICE_ERROR_V3_009999.getMessage());
            log.error("{},根据订单号:{}获取订单数据以及发票数据接口异常,异常原因为:{}", LOGGER_MSG, ddfpcxReq.getDDH(), e);
        }
        
        String jsonString = JsonUtils.getInstance().toJsonString(ddfpcxRsp);
        log.debug("{},企业订单导入请求状态返回数据:{}", LOGGER_MSG, jsonString);
        return ddfpcxRsp;
    }
    
    
    /**
     * 红字发票申请单上传接口V3
     *
     * @param hzsqdscReq
     * @return
     */
    @Override
    public HZSQDSC_RSP specialInvoiceRushRedV3(HZSQDSC_REQ hzsqdscReq, String kpjh) {
        
        
        HZSQDSC_RSP hzsqdscRsp = new HZSQDSC_RSP();
        log.debug("{},接收红字发票申请单:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(hzsqdscReq));
        
        Map<String, String> checkInvParam = validateInterfaceSpecialInvoice.checkSpecialInvoiceUpload(hzsqdscReq);
        if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}红字申请单上传数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
            hzsqdscRsp.setZTDM(checkInvParam.get(OrderManagementConstant.ERRORCODE));
            hzsqdscRsp.setZTXX(checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
            return hzsqdscRsp;
        }
        
        String nsrsbh = hzsqdscReq.getHZSQDSCPC().getNSRSBH();
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(nsrsbh);
        /**
         * 如果开票日期为空 补全开票日期
         */
        for (HZSQDSCZXX hzsqdsczxx : hzsqdscReq.getHZSQDSCZXX()) {
            
            if (StringUtils.isBlank(hzsqdsczxx.getHZSQDTXX().getYFPKPRQ())) {
                log.warn("专票冲红企业传递的原开票日期为空,红字申请单编号:{}", hzsqdsczxx.getHZSQDTXX().getSQBSCQQLSH());
                if (StringUtils.isNotBlank(hzsqdsczxx.getHZSQDTXX().getYFPDM()) && StringUtils.isNotBlank(hzsqdsczxx.getHZSQDTXX().getYFPHM())) {
                    OrderInvoiceInfo selectOrderInvoiceInfoByFpdmAndFphm = apiOrderInvoiceInfoService
                            .selectOrderInvoiceInfoByFpdmAndFphm(hzsqdsczxx.getHZSQDTXX().getYFPDM(),
                                    hzsqdsczxx.getHZSQDTXX().getYFPHM(), shList);
                    if (selectOrderInvoiceInfoByFpdmAndFphm != null) {
                        log.info("根据发票代码号码查到的发票信息不为空，补全原发票开票日期:{}", hzsqdsczxx.getHZSQDTXX().getSQBSCQQLSH());
                        hzsqdsczxx.getHZSQDTXX().setYFPKPRQ(DateUtil.format(selectOrderInvoiceInfoByFpdmAndFphm.getKprq(), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
            }
            
            /**
             * 补全编码表版本号
             */
            if (StringUtils.isBlank(hzsqdsczxx.getHZSQDTXX().getBMBBBH())) {
                hzsqdsczxx.getHZSQDTXX().setBMBBBH(SystemConfig.bmbbbh);
            }
        }
        
        HZSQDSCPC hzsqdscpc = hzsqdscReq.getHZSQDSCPC();
        String sldid = hzsqdscReq.getHZSQDSCPC().getKPZD();

        String fplx = "";
        /**
         * 种类代码转换
         */
        String fplb = CommonUtils.transFpzldm(hzsqdscReq.getHZSQDSCPC().getFPLXDM());
    
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fplb)) {
            fplx = OrderInfoEnum.INVOICE_TYPE_2.getKey();
        } else {
            fplx = OrderInfoEnum.INVOICE_TYPE_1.getKey();
        }
    
        /**
         * 获取配置的设备信息
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(hzsqdscReq.getHZSQDSCPC().getNSRSBH());
    
        /**
         * 获取sldid ，如果sldid= -1 调用sldidUtils.sldIdFormat(COMMON_INVOICES_BATCH common_invoices_batch)为sldid和kpjh赋值,
         * 如果不为-1 则保持原来处理逻辑不变，直接为sldid和kpjh赋值
         */
        if (StringUtils.isBlank(sldid) || NEGATIVE_1.equals(sldid)) {
            sldid = "";
        }
    
        String qdbz = OrderInfoEnum.QDBZ_CODE_0.getKey();
        if (OrderInfoEnum.SPECIAL_YYSBZ_0000000090.getKey().equals(hzsqdscReq.getHZSQDSCZXX().get(0).getHZSQDTXX().getYYSBZ())) {
            qdbz = OrderInfoEnum.QDBZ_CODE_4.getKey();
        }
    
        R result = iCommonInterfaceService.dealWithSldStartV3(sldid, fplb, nsrsbh, qdbz, terminalCode);
    
    
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(String.valueOf(result.get(OrderManagementConstant.CODE)))) {
            log.error("{}红字申请单上传数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
            hzsqdscRsp.setZTDM(result.get(OrderManagementConstant.CODE) == null ? ""
                    : String.valueOf(result.get(OrderManagementConstant.CODE)));
            hzsqdscRsp.setZTXX(result.get(OrderManagementConstant.MESSAGE) == null ? ""
                    : String.valueOf(result.get(OrderManagementConstant.MESSAGE)));
            return hzsqdscRsp;
        } else {
            log.debug("受理点查询成功!");
            sldid = String.valueOf(result.get("sldid"));
            kpjh = String.valueOf(result.get("kpjh"));
        }
    
    
        //方格税盘单独处理
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            //redis获取里面获取注册的税盘信息
            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(hzsqdscReq.getHZSQDSCPC().getNSRSBH(), sldid);
            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
            List<HZSQDSCJG> hzsqdscjgs = new ArrayList<>();
            for (HZSQDSCZXX hzsqdsczxx : hzsqdscReq.getHZSQDSCZXX()) {
                HZSQDTXX hzsqdtxx = hzsqdsczxx.getHZSQDTXX();
                HZSQDSCJG hzsqdscjg = new HZSQDSCJG();
                /**
                 * 查询数据库中是否存在数据
                 */
                SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySqdqqlsh(hzsqdsczxx.getHZSQDTXX().getSQBSCQQLSH());
                if (!ObjectUtils.isEmpty(specialInvoiceReversal)) {
                    hzsqdscjg.setSQBSCQQLSH(hzsqdtxx.getSQBSCQQLSH());
                    hzsqdscjg.setSQDH(specialInvoiceReversal.getSqdh());
                    hzsqdscjg.setZTDM(specialInvoiceReversal.getStatusCode());
                    hzsqdscjg.setZTXX(specialInvoiceReversal.getStatusMessage());
                    hzsqdscjg.setXXBBH(specialInvoiceReversal.getXxbbh());
                    hzsqdscjgs.add(hzsqdscjg);
                    log.error("红字信息表上传保存数据失败,数据已存在不再进行插入");
                    continue;
                }
                hzsqdscjg.setSQBSCQQLSH(hzsqdtxx.getSQBSCQQLSH());
                hzsqdscjg.setSQDH(hzsqdtxx.getSQBSCQQLSH());
                hzsqdscjg.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_TZD0500.getKey());
                hzsqdscjg.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_TZD0500.getMessage());
                hzsqdscjg.setXXBBH("");
                hzsqdscjgs.add(hzsqdscjg);
                if (StringUtils.isNotEmpty(registCodeStr)) {
    
                    /**
                     * 存放上传信息到redis队列
                     */
                    PushPayload pushPayload = new PushPayload();
                    //接口发票上传税局
                    pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_2);
                    pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                    pushPayload.setJQBH(registrationCode.getJqbh());
                    pushPayload.setZCM(registrationCode.getZcm());
                    //单张发送上传
                    pushPayload.setSQBSCQQPCH(hzsqdsczxx.getHZSQDTXX().getSQBSCQQLSH());
                    apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                }
            }
    
            /**
             * 组装返回结果数据
             */
            hzsqdscRsp.setSQBSCQQPCH(hzsqdscpc.getSQBSCQQPCH());
            hzsqdscRsp.setZTDM(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getKey());
            hzsqdscRsp.setZTXX(OrderInfoContentEnum.CHECK_ISS7PRI_060112.getMessage());
            hzsqdscRsp.setHZSQDSCJG(hzsqdscjgs);
        } else {
            /**
             * 请求协议bean转换
             */
            HzfpsqbsReq req = BeanTransitionUtils.transitionSpecialInvoiceRushRedV3(hzsqdscReq, sldid, kpjh, fplx, fplb);
            log.debug("{},转换后数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(req));
        
        
            HpUploadResponse redInvoiceUpload = HttpInvoiceRequestUtil.redInvoiceUpload(OpenApiConfig.redInvoiceUpload, req, terminalCode);
    
            hzsqdscRsp = BeanTransitionUtils.transitionSpecialInvoiceRushRedRspV3(redInvoiceUpload);
        
        }
    
        /**
         * 红字信息表上传成功后需要保存到数据库
         */
        try {
            specialInvoiceService.saveSpecialInvoiceRequest(hzsqdscRsp, hzsqdscReq, sldid, kpjh, fplx, fplb);
        } catch (Exception e) {
            log.error("{}红字信息表上传异常:{}", LOGGER_MSG, e);
        }
        
        return hzsqdscRsp;
    }
    
    /**
     * 红字发票申请单审核结果下载V3
     *
     * @param hzsqdxzReq
     * @return
     */
    @Override
    public HZSQDXZ_RSP downSpecialInvoiceV3(HZSQDXZ_REQ hzsqdxzReq, String sldid, String kpjh) {
        
        HZSQDXZ_RSP hzsqdxzRsp = new HZSQDXZ_RSP();
        hzsqdxzRsp.setSQBXZQQPCH(hzsqdxzReq.getSQBXZQQPCH());
        if (StringUtils.isBlank(hzsqdxzReq.getTKRQQ()) || StringUtils.isBlank(hzsqdxzReq.getTKRQZ())) {
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.DATE, -5);
            hzsqdxzReq.setTKRQQ(DateUtils.getYYYYMMDDFormatStr(currentCalendar.getTime()));
            hzsqdxzReq.setTKRQZ(DateUtils.getYYYYMMDDFormatStr(new Date()));
        }
        
        
        Map<String, String> checkInvParam = validateInterfaceSpecialInvoice.checkSpecialInvoiceDownload(hzsqdxzReq);
        if (!ConfigureConstant.STRING_0000.equals(checkInvParam.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}红字申请单下载数据非空校验未通过，未通过数据:{}", LOGGER_MSG, checkInvParam);
            hzsqdxzRsp.setZTDM(checkInvParam.get(OrderManagementConstant.ERRORCODE));
            hzsqdxzRsp.setZTXX(checkInvParam.get(OrderManagementConstant.ERRORMESSAGE));
            return hzsqdxzRsp;
        }
    
        String fplx = "";
        /**
         * 种类代码转换
         */
        String fplb = CommonUtils.transFpzldm(hzsqdxzReq.getFPLXDM());
    
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fplb)) {
            fplx = OrderInfoEnum.INVOICE_TYPE_2.getKey();
        } else {
            fplx = OrderInfoEnum.INVOICE_TYPE_1.getKey();
        }
    
        /**
         * 获取配置的设备信息
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(hzsqdxzReq.getNSRSBH());
        log.info("税盘编码：{}", terminalCode);
        //方格税盘特殊处理
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
            HpResponseBean redInvoiceDown = specialInvoiceService.downloadSpecialInvoiceReversalFg(hzsqdxzReq);
            hzsqdxzRsp = BeanTransitionUtils.transitionDownSpecialInvoiceRspV3(redInvoiceDown);
        } else {
            /**
             * 请求协议转换
             */
            HpInvocieRequest hpInvocieRequest = BeanTransitionUtils.transitionDownSpecialInvoiceV3(hzsqdxzReq, sldid, kpjh, fplx, fplb);
    
            HpResponseBean redInvoiceDown = HttpInvoiceRequestUtil.redInvoiceDown(OpenApiConfig.redInvoiceDown, hpInvocieRequest, terminalCode);
    
            hzsqdxzRsp = BeanTransitionUtils.transitionDownSpecialInvoiceRspV3(redInvoiceDown);
        
        }
        
        String jsonString2 = JsonUtils.getInstance().toJsonString(hzsqdxzRsp);
        log.debug("红字发票下载研二返回数据:{},{}", jsonString2, LOGGER_MSG);
        return hzsqdxzRsp;
    }

    @Override
    public List<DDFPDR_RSP> importIssuedInvoice(List<DDFPZXX> ddfpzxxList) {
        //构造响应信息集合
        List<DDFPDR_RSP> ddfpdrRspList = CollectionUtil.newArrayList();
        //判断入参是否为空
        if (CollectionUtil.isEmpty(ddfpzxxList)) {
            DDFPDR_RSP ddfpdrRsp = DDFPDR_RSP.build(OrderInfoContentEnum.INVOICE_INFO_IMPORT_ERROR_NULL);
            ddfpdrRspList.add(ddfpdrRsp);
            return ddfpdrRspList;
        }
        for (int i = 0; i < ddfpzxxList.size(); i++) {
            DDFPDR_RSP ddfpdrRsp = DDFPDR_RSP.build(OrderInfoContentEnum.INVOICE_INFO_IMPORT_SUCCESS);
            DDFPZXX ddfpzxx = ddfpzxxList.get(i);
            List<String> shList = new ArrayList<>();
            shList.add(ddfpzxx.getDDFPXX().getNSRSBH());
            //初始化接口返回参数对象
            DDFPDR_RSP.initResponse(ddfpdrRsp, ddfpzxx);
            try {
                //1.订单发票协议bean数据校验
                Map<String, String> checkCommonDdffzxx = validateInterfaceOrder.checkCommonDdffzxx(ddfpzxx);
                if (!ConfigureConstant.STRING_0000.equals(checkCommonDdffzxx.get(OrderManagementConstant.ERRORCODE))) {
                    log.error("{}已开发票历史数据导入校验未通过，未通过数据:{}", "(订单对外接口业务类V4)",
                            JsonUtils.getInstance().toJsonStringNullToEmpty(ddfpzxx));
                    ddfpdrRsp.setZTDM(checkCommonDdffzxx.get(OrderManagementConstant.ERRORCODE));
                    ddfpdrRsp.setZTXX(" [发票信息第" + (i + 1) + "条] " + checkCommonDdffzxx.get(OrderManagementConstant.ERRORMESSAGE));
                    ddfpdrRspList.add(ddfpdrRsp);
                    continue;
                }
    
                //请求参数中设置数据库查询的值
                ddfpzxx = validateInterfaceOrder.setDatabaseValueToddfpzxx(ddfpzxx);
                DDFPXX ddfpxx = ddfpzxx.getDDFPXX();
    
                Map<String, Object> paraMap = new HashMap<>(5);
                paraMap.put(ConfigureConstant.REQUEST_PARAM_FPQQLSH, ddfpxx.getDDQQLSH());
                paraMap.put(ConfigureConstant.REQUEST_PARAM_DDH, ddfpxx.getDDH());
                paraMap.put(ConfigureConstant.REQUEST_PARAM_TQM, ddfpxx.getTQM());
    
                //2.查询订单处理表，判断导入数据是否已存在
                List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(paraMap, shList);
                if (CollectionUtil.isEmpty(orderProcessInfos)) {
                    //用发票代码和发票号码作为唯一键判断数据是否存在
                    OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(
                            ddfpxx.getFPDM(), ddfpxx.getFPHM(), shList);
                    if (Objects.isNull(orderInvoiceInfo)) {
                        //3.订单发票协议bean的转换
                        /*
                         * 订单业务bean
                         */
                        List<OrderInfo> insertOrder = buildOrderInfo(ddfpzxx);
                        /*
                         * 订单明细业务bean
                         */
                        List<List<OrderItemInfo>> insertOrderItem = buildOrderItemInfo(ddfpzxx,insertOrder.get(0));
                        /*
                         * 批量开票bean
                         */
                        List<InvoiceBatchRequest> transitionBatchRequest = buildInvoiceBatchRequest(ddfpxx);
                        /*
                         * 发票批量开票明细bean
                         */
                        List<List<InvoiceBatchRequestItem>> insertBatchItem = buildInvoiceBatchRequestItem(transitionBatchRequest,
                                insertOrder.get(0), i);
                        /*
                         * 订单请求批次bean
                         */
                        OrderBatchRequest obr = buildOrderBatchRequest(ddfpxx, transitionBatchRequest);
                        /*
                         * 订单处理bean
                         */
                        List<OrderProcessInfo> insertProcessInfo = buildOrderProcessInfo(insertOrder.get(0));
                        /*
                         * 订单与发票对应关系业务bean
                         */
                        List<OrderInvoiceInfo> insertInvoiceInfo = buildOrderInvoiceInfo(ddfpxx, insertOrder.get(0),
                                insertBatchItem.get(0).get(0));
                        /*
                         * 原始订单到最终订单的关系bean
                         */
                        List<OrderOriginExtendInfo> originExtendList = buildOrderOriginExtendInfo(insertOrder.get(0));

                        //4.调用保存接口
                        apiInvoiceCommonService.saveHistoryData(transitionBatchRequest, insertOrder, insertOrderItem, insertProcessInfo,
                                insertBatchItem, insertInvoiceInfo, obr, CollectionUtil.newArrayList(),
                                CollectionUtil.newArrayList(), originExtendList, ddfpxx.getPDFZJL(), shList);
                    }else {
                        log.info("历史导入数据发票代码:{},发票号码{}已存在!", ddfpxx.getFPDM(), ddfpxx.getFPHM());
                        DDFPDR_RSP.build(ddfpdrRsp, OrderInfoContentEnum.INVOICE_INFO_FPDM_FPHM_ERROR_EXIST);
                    }
                }else {
                    log.info("历史数据已存在:{}",JsonUtils.getInstance().toJsonStringNullToEmpty(ddfpzxx));
                    DDFPDR_RSP.build(ddfpdrRsp,OrderInfoContentEnum.INVOICE_INFO_IMPORT_ERROR_EXIST);
                }
            } catch (Exception e) {
                log.error("{}已开发票历史数据保存数据库异常:{}", LOGGER_MSG_V4, e);
                DDFPDR_RSP.build(ddfpdrRsp,OrderInfoContentEnum.INVOICE_INFO_IMPORT_ERROR);
            }
            ddfpdrRspList.add(ddfpdrRsp);
        }
        return ddfpdrRspList;
    }

    @Override
    public SPXXCX_RSP queryCommodityMessage(SPXXCX_REQ spxxcxReq) {
        SPXXCX_RSP spxxcxRsp = SPXXCX_RSP.build(OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_SUCCESS);
        //初始化返回参数对象
        SPXXCX_RSP.initResponse(spxxcxReq,spxxcxRsp);
        try {
            //1.校验请求参数
            Map<String,String> verifyCommodityMap = apiVerifyCommodityCode.checkQueryCommodityRequestParam(spxxcxReq);
            if(!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                    verifyCommodityMap.get(OrderManagementConstant.ERRORCODE))){
                log.error("{}查询商品信息校验未通过，未通过数据:{}",LOGGER_MSG_V4,
                        JsonUtils.getInstance().toJsonStringNullToEmpty(spxxcxReq));
                spxxcxRsp.setZTDM(verifyCommodityMap.get(OrderManagementConstant.ERRORCODE));
                spxxcxRsp.setZTXX(verifyCommodityMap.get(OrderManagementConstant.ERRORMESSAGE));
                return spxxcxRsp;
            }
    
            //2.请求参数的转换
            Map<String, Object> param = new HashMap<>(5);
            param.put("limit", spxxcxReq.getGS());
            param.put("page", spxxcxReq.getYS());
            //商品对应的ID
            param.put("id",spxxcxReq.getSPID());
            //商品名称
            param.put("spmc", spxxcxReq.getXMMC());
            //销货方纳税人识别号
            List<String> shList = NsrsbhUtils.transShListByNsrsbh(spxxcxReq.getXHFSBH());
            //销货方纳税人名称
            param.put("qymc",spxxcxReq.getXHFMC());
            param.put("fuzzyQuery",InterfaceServiceImplV3.FUZZY_QUERY_NO_FLAG);

    
            //3.按照纳税人识别号查询
            PageUtils pageUtils = apiCommodityService.queryCommodity(param, shList);
            List<SPXX> spxxCommonList = new ArrayList<>();
            List<CommodityCodeEntity> commodityCodeEntityList = (List<CommodityCodeEntity>) pageUtils.getList();
            log.info("{}查询商品信息结果:{}", LOGGER_MSG_V4, commodityCodeEntityList);
            if (CollectionUtil.isNotEmpty(commodityCodeEntityList)) {
                commodityCodeEntityList.forEach(commodityCodeEntity -> {
                    SPXX spxx = BeanTransitionUtils.transitionSpxx(commodityCodeEntity);
                    spxxCommonList.add(spxx);
                });
            }else {
                SPXXCX_RSP.build(spxxcxRsp,OrderInfoContentEnum.BUYER_MESSAGE_QUERY_RESULT_NULL);
            }
            spxxcxRsp.setSPXX(spxxCommonList);
            spxxcxRsp.setZGS(String.valueOf(pageUtils.getTotalCount()));
        } catch (Exception e) {
            log.error("{}查询商品信息异常:{}",LOGGER_MSG_V4,e);
            SPXXCX_RSP.build(spxxcxRsp,OrderInfoContentEnum.COMMODITY_MESSAGE_QUERY_ERROR);
        }
        return spxxcxRsp;
    }

    @Override
    public List<SPXXTB_RSP> syncCommodityMessage(List<SPXXTB_REQ> spxxtbReqList) {
        log.info("{}同步商品信息接收参数：{}", LOGGER_MSG_V4, spxxtbReqList);
        List<SPXXTB_RSP> spxxtbRspList = new ArrayList<>();
        //判断入参集合是否为空
        if(CollectionUtil.isEmpty(spxxtbReqList)){
            SPXXTB_RSP spxxtbRsp = SPXXTB_RSP.build(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_NULL);
            spxxtbRspList.add(spxxtbRsp);
            return spxxtbRspList;
        }
        for (SPXXTB_REQ spxxtbReq : spxxtbReqList) {
            SPXXTB_RSP spxxtbRsp = SPXXTB_RSP.build(OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_SUCCESS);
            //初始化返回参数对象
            SPXXTB_RSP.initResponse(spxxtbReq,spxxtbRsp);
            try {
                //1.请求参数校验
                Map<String,String> verifyCommodityMap = apiVerifyCommodityCode.checkSyncCommodityRequestParam(spxxtbReq);
                if(!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                        verifyCommodityMap.get(OrderManagementConstant.ERRORCODE))){
                    log.error("{}同步商品信息校验未通过，未通过数据:{}", LOGGER_MSG_V4,
                            JsonUtils.getInstance().toJsonStringNullToEmpty(spxxtbReq));
                    spxxtbRsp.setZTDM(verifyCommodityMap.get(OrderManagementConstant.ERRORCODE));
                    spxxtbRsp.setZTXX(verifyCommodityMap.get(OrderManagementConstant.ERRORMESSAGE));
                    spxxtbRspList.add(spxxtbRsp);
                    continue;
                }
    
                //2.请求协议bean转换
                CommodityCodeEntity commodityCodeEntity = BeanTransitionUtils.transitionCommodityCodeEntity(spxxtbReq);
                if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate()) && commodityCodeEntity.getTaxRate().contains(ConfigureConstant.STRING_PERCENT)) {
                    commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
                }
    
                //3.根据操作类型，调用DAO层同步数据库
                R r = apiCommodityService.syncCommodity(commodityCodeEntity, spxxtbReq.getCZLX());
                spxxtbRsp.setZTDM(String.valueOf(r.get(OrderManagementConstant.CODE)));
                spxxtbRsp.setZTXX(String.valueOf(r.get(OrderManagementConstant.MESSAGE)));
            } catch (Exception e) {
                log.error("{}同步商品信息异常：{}",LOGGER_MSG_V4,e);
                SPXXTB_RSP.build(spxxtbRsp,OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR);
            }
            spxxtbRspList.add(spxxtbRsp);
        }
        return spxxtbRspList;
    }

    @Override
    public GMFXXCX_RSP queryBuyerMessage(GMFXXCX_REQ gmfxxcxReq) {
        log.info("{}查询购买方信息接收参数：{}",LOGGER_MSG_V4,JsonUtils.getInstance().toJsonString(gmfxxcxReq));
        GMFXXCX_RSP gmfxxcxRsp = GMFXXCX_RSP.build(OrderInfoContentEnum.BUYER_MESSAGE_QUERY_SUCCESS);
        //初始化返回参数对象
        GMFXXCX_RSP.initResponse(gmfxxcxReq,gmfxxcxRsp);
        try {
            //1.校验请求参数
            Map<String,String> verifyBuyerMap = apiVerifyBuyerManageInfo.checkQueryBuyerRequestParam(gmfxxcxReq);
            if (!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                    verifyBuyerMap.get(OrderManagementConstant.ERRORCODE))) {
                log.error("{}查询购买方信息校验未通过，未通过数据:{}", LOGGER_MSG_V4,
                        JsonUtils.getInstance().toJsonStringNullToEmpty(gmfxxcxReq));
                gmfxxcxRsp.setZTDM(verifyBuyerMap.get(OrderManagementConstant.ERRORCODE));
                gmfxxcxRsp.setZTXX(verifyBuyerMap.get(OrderManagementConstant.ERRORMESSAGE));
                return gmfxxcxRsp;
            }
            //2.组装查询数据
            //组装参数
            Map<String, Object> param = new HashMap<>(5);
            //页号
            param.put("limit", Integer.parseInt(gmfxxcxReq.getGS()));
            //个数
            param.put("page", Integer.parseInt(gmfxxcxReq.getYS()));
            //购买方编码
            param.put("buyerCode", gmfxxcxReq.getGMFBM());
            //销货方纳税人名称
            param.put("xhfMc", gmfxxcxReq.getXHFMC());
            //购买方识别号
            param.put("taxpayerCode", gmfxxcxReq.getGMFSBH());
            //购买方名称
            param.put("purchaseName", gmfxxcxReq.getGMFMC());
            List<String> shList = new ArrayList<>();
            shList.add(gmfxxcxReq.getXHFSBH());
            param.put("fuzzyQuery", InterfaceServiceImplV3.FUZZY_QUERY_NO_FLAG);
    
            //3.查询购买方信息
            PageUtils pageUtils = apiBuyerService.queryBuyerList(param, shList);
            List<BuyerEntity> buyerEntityList = (List<BuyerEntity>) pageUtils.getList();
            List<GMFXX_COMMON> gmfxxCommonList = CollectionUtil.newArrayList();
            log.info("{}查询购买方信息结果：{}", LOGGER_MSG_V4, JsonUtils.getInstance().toJsonString(buyerEntityList));
            if (CollectionUtil.isNotEmpty(buyerEntityList)) {
                buyerEntityList.forEach(buyerEntity -> {
                    GMFXX_COMMON gmfxxCommon = BeanTransitionUtils.transitionGmfxxCommon(buyerEntity);
                    gmfxxCommonList.add(gmfxxCommon);
                });
            }else{
                GMFXXCX_RSP.build(gmfxxcxRsp,OrderInfoContentEnum.BUYER_MESSAGE_QUERY_RESULT_NULL);
            }
            gmfxxcxRsp.setGMFXX(gmfxxCommonList);
            gmfxxcxRsp.setZGS(String.valueOf(pageUtils.getTotalCount()));
        } catch (Exception e) {
            log.error("{}查询购买方信息异常：{}",LOGGER_MSG_V4,e);
            GMFXXCX_RSP.build(gmfxxcxRsp,OrderInfoContentEnum.BUYER_MESSAGE_QUERY_ERROR);
        }
        return gmfxxcxRsp;
    }

    @Override
    public List<GMFXXTB_RSP> syncBuyerMessage(List<GMFXXTB_REQ> gmfxxtbReqList) {
        log.info("{}同步购买方信息接收参数：{}",LOGGER_MSG_V4,JsonUtils.getInstance().toJsonString(gmfxxtbReqList));
        List<GMFXXTB_RSP> gmfxxtbRspList = CollectionUtil.newArrayList();
        //判断入参集合是否为空
        if(CollectionUtil.isEmpty(gmfxxtbReqList)){
            GMFXXTB_RSP gmfxxtbRsp = GMFXXTB_RSP.build(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_NULL);
            gmfxxtbRspList.add(gmfxxtbRsp);
            return gmfxxtbRspList;
        }
        for (GMFXXTB_REQ gmfxxtbReq : gmfxxtbReqList) {
            GMFXXTB_RSP gmfxxtbRsp = GMFXXTB_RSP.build(OrderInfoContentEnum.BUYER_MESSAGE_SYNC_SUCCESS);
            //初始化返回参数实体
            GMFXXTB_RSP.initResponse(gmfxxtbReq,gmfxxtbRsp);
            try {
                //1.请求参数校验
                Map<String,String> verifyCommodityMap = apiVerifyBuyerManageInfo.checkSyncBuyerRequestParam(gmfxxtbReq);
                if(!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                        verifyCommodityMap.get(OrderManagementConstant.ERRORCODE))){
                    log.error("{}同步购货方信息校验未通过，未通过数据:{}",LOGGER_MSG_V4,
                            JsonUtils.getInstance().toJsonStringNullToEmpty(gmfxxtbReq));
                    gmfxxtbRsp.setZTDM(verifyCommodityMap.get(OrderManagementConstant.ERRORCODE));
                    gmfxxtbRsp.setZTXX(verifyCommodityMap.get(OrderManagementConstant.ERRORMESSAGE));
                    gmfxxtbRspList.add(gmfxxtbRsp);
                    continue;
                }
            
                //2.请求协议bean转换
                BuyerEntity buyerEntity = BeanTransitionUtils.transitionBuyerEntity(gmfxxtbReq);
            
                //3.根据操作类型，调用DAO层同步数据库
                R r = apiBuyerService.syncBuyer(buyerEntity, gmfxxtbReq.getCZLX());
                gmfxxtbRsp.setZTDM(String.valueOf(r.get(OrderManagementConstant.CODE)));
                gmfxxtbRsp.setZTXX(String.valueOf(r.get(OrderManagementConstant.MESSAGE)));
            } catch (Exception e) {
                log.error("{}同步购买方信息异常：{}", LOGGER_MSG_V4, e);
                GMFXXTB_RSP.build(gmfxxtbRsp, OrderInfoContentEnum.BUYER_MESSAGE_SYNC_ERROR);
            }
            gmfxxtbRspList.add(gmfxxtbRsp);
        }
        return gmfxxtbRspList;
    }
    
    
    @Override
    public List<SKSBXXTB_RSP> syncTaxEquipmentInfo(List<SKSBXXTB_REQ> sksbxxtbReqList) {
        log.info("{}同步税控设备信息接收参数：{}", LOGGER_MSG_V4, JsonUtils.getInstance().toJsonString(sksbxxtbReqList));
        List<SKSBXXTB_RSP> sksbxxtbRsps = CollectionUtil.newArrayList();
        
        if (ObjectUtil.isEmpty(sksbxxtbReqList)) {
            SKSBXXTB_RSP sksbxxtbRsp = SKSBXXTB_RSP.build(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193001);
            sksbxxtbRsps.add(sksbxxtbRsp);
            return sksbxxtbRsps;
        }
        
        for (SKSBXXTB_REQ sksbxxtbReq : sksbxxtbReqList) {
            SKSBXXTB_RSP sksbxxtbRsp = SKSBXXTB_RSP.build(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193000);
            sksbxxtbRsp.setXHFSBH(sksbxxtbReq.getXHFSBH());
            sksbxxtbRsp.setXHFMC(sksbxxtbReq.getXHFMC());
            sksbxxtbRsp.setSKSBDM(sksbxxtbReq.getSKSBDM());
            try {
                //1.请求参数校验
                Map<String, String> checkSyncTaxEquipmentInfo = iValidateTaxEquipmentInfo.checkSyncTaxEquipmentInfo(sksbxxtbReq);
                if (!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                        checkSyncTaxEquipmentInfo.get(OrderManagementConstant.ERRORCODE))) {
                    log.error("{}同步税控设备信息校验未通过，未通过数据:{}", LOGGER_MSG_V4,
                            JsonUtils.getInstance().toJsonStringNullToEmpty(sksbxxtbReq));
                    sksbxxtbRsp.setZTDM(checkSyncTaxEquipmentInfo.get(OrderManagementConstant.ERRORCODE));
                    sksbxxtbRsp.setZTXX(checkSyncTaxEquipmentInfo.get(OrderManagementConstant.ERRORMESSAGE));
                    sksbxxtbRsps.add(sksbxxtbRsp);
                    continue;
                }
    
                DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(sksbxxtbReq.getXHFSBH(), sksbxxtbReq.getXHFMC());
                if (ObjectUtil.isEmpty(sysDeptEntity)) {
                    log.error("{}无法获取当前税号的销方信息，税号：{}", LOGGER_MSG, sksbxxtbReq.getXHFSBH());
                    sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193015.getKey());
                    sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193015.getMessage());
                    sksbxxtbRsps.add(sksbxxtbRsp);
                    continue;
                }
    
                //2.请求协议bean转换
                TaxEquipmentInfo taxEquipmentInfo = BeanTransitionUtils.transitionTaxEquipment(sksbxxtbReq);
    
                //3.根据操作类型，调用DAO层同步数据库
                //查询当前税号是否已经存在
                TaxEquipmentInfo queryTaxEquip = new TaxEquipmentInfo();
                queryTaxEquip.setXhfNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
                List<String> shList = NsrsbhUtils.transShListByNsrsbh(taxEquipmentInfo.getXhfNsrsbh());
                List<TaxEquipmentInfo> queryTaxEquipment = apiTaxEquipmentService.queryTaxEquipmentList(queryTaxEquip, shList);
                if (ObjectUtil.isEmpty(queryTaxEquipment)) {
                    if (ConfigureConstant.STRING_2.equals(sksbxxtbReq.getCZLX())) {
                        sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193016.getKey());
                        sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193016.getMessage());
                    } else {
        
                        taxEquipmentInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        
                        taxEquipmentInfo.setDeleted(OrderInfoEnum.DATE_DELETE_STATUS_0.getKey());
                        int i = apiTaxEquipmentService.addTaxEquipment(taxEquipmentInfo);
                        if (i > 0) {
                            sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193000.getKey());
                            sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193000.getMessage());
                        } else {
                            sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193013.getKey());
                            sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193013.getMessage());
                        }
                    }
    
                } else {
                    TaxEquipmentInfo taxEquipmentInfo1 = new TaxEquipmentInfo();
                    if (ConfigureConstant.STRING_2.equals(sksbxxtbReq.getCZLX())) {
                        taxEquipmentInfo1.setUpdateTime(taxEquipmentInfo.getUpdateTime());
                        taxEquipmentInfo1.setId(queryTaxEquipment.get(0).getId());
                        taxEquipmentInfo1.setDeleted(OrderInfoEnum.DATE_DELETE_STATUS_1.getKey());
                    } else {
    
                        BeanUtil.copyProperties(queryTaxEquipment.get(0), taxEquipmentInfo1);
                        taxEquipmentInfo1.setSksbType(taxEquipmentInfo.getSksbType(

                        ));
                        taxEquipmentInfo1.setBz(taxEquipmentInfo.getBz());
                        taxEquipmentInfo1.setSksbCode(taxEquipmentInfo.getSksbCode());
                        taxEquipmentInfo1.setSksbName(taxEquipmentInfo.getSksbName());
                        taxEquipmentInfo1.setLinkTime(taxEquipmentInfo.getLinkTime());
                        taxEquipmentInfo1.setUpdateTime(taxEquipmentInfo.getUpdateTime());
    
                    }
                    int i = apiTaxEquipmentService.updateTaxEquipment(taxEquipmentInfo1);
                    if (i > 0) {
        
        
                        sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193000.getKey());
                        sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193000.getMessage());
                    } else {
                        sksbxxtbRsp.setZTDM(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193014.getKey());
                        sksbxxtbRsp.setZTXX(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193014.getMessage());
                    }
                }
    
                /**
                 * 更新成功后,调用用户中心接口,同步税控设备信息
                 */
                //FzyyTaxEquipmentController.pushTaxEquipment(taxEquipmentInfo);
    
    
            } catch (Exception e) {
                log.error("{}同步税控设备信息异常：{}", LOGGER_MSG_V4, e);
                SKSBXXTB_RSP.build(OrderInfoContentEnum.TAX_EQUIPMENT_INFO_193014);
            }
            sksbxxtbRsps.add(sksbxxtbRsp);
        }
        return sksbxxtbRsps;
    }
    
    /**
     * 发票余量信息查询
     *
     * @param fpylcxReq
     * @return
     */
    @Override
    public List<FPYLCX_RSP> queryInvoiceStore(FPYLCX_REQ fpylcxReq) {
        log.info("{}查询发票余量信息接收参数：{}", LOGGER_MSG_V4, JsonUtils.getInstance().toJsonString(fpylcxReq));
        List<FPYLCX_RSP> fpylcxRsps = CollectionUtil.newArrayList();
        
        
        try {
            //1.校验请求参数
            Map<String, String> checkResultMap = new HashMap<>(10);
            String successCode = OrderInfoContentEnum.SUCCESS.getKey();
            checkResultMap.put(OrderManagementConstant.ERRORCODE, successCode);
            //销货方纳税人识别号
            checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.QUERY_INVOICE_STORE_194001,
                    OrderInfoContentEnum.QUERY_INVOICE_STORE_194002,
                    OrderInfoContentEnum.QUERY_INVOICE_STORE_194003,
                    fpylcxReq.getXHFSBH());
            if (!successCode.equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                FPYLCX_RSP fpylcxRsp = FPYLCX_RSP.build(OrderInfoContentEnum.QUERY_INVOICE_STORE_194000);
                fpylcxRsp.setXHFSBH(fpylcxReq.getXHFSBH());
                fpylcxRsps.add(fpylcxRsp);
                return fpylcxRsps;
            }
            
            String terminalCode = apiTaxEquipmentService.getTerminalCode(fpylcxReq.getXHFSBH());
            CountToB countToB = new CountToB();
            countToB.setTaxpayerCode(fpylcxReq.getXHFSBH());
            R fpyl = invoiceCountService.getFpyl(countToB, terminalCode, null);
            if (!StringUtils.equals(OrderInfoContentEnum.SUCCESS.getKey(),
                    fpyl.get(OrderManagementConstant.CODE).toString())) {
                log.error("{}查询发票余量失败:{}", LOGGER_MSG_V4,
                        JsonUtils.getInstance().toJsonStringNullToEmpty(fpylcxReq));
                FPYLCX_RSP fpylcxRsp = FPYLCX_RSP.build(OrderInfoContentEnum.QUERY_INVOICE_STORE_194999);
                fpylcxRsp.setXHFSBH(fpylcxReq.getXHFSBH());
                fpylcxRsps.add(fpylcxRsp);
                return fpylcxRsps;
            } else {
                Collection<CountSurplusVO> values = (Collection<CountSurplusVO>) fpyl.get(ConfigureConstant.STRING_CONTENT);
                if (ObjectUtil.isNotEmpty(values)) {
                    values.forEach(countSurplusVO -> {
                        FPYLCX_RSP fpylcxRsp = FPYLCX_RSP.build(OrderInfoContentEnum.QUERY_INVOICE_STORE_194000);
                        fpylcxRsp.setXHFSBH(fpylcxReq.getXHFSBH());
                        fpylcxRsp.setXHFMC(countSurplusVO.getNsrmc());
                        fpylcxRsp.setFJH(countSurplusVO.getFjh());
                        fpylcxRsp.setZPYL(countSurplusVO.getZpyl());
                        fpylcxRsp.setPPYL(countSurplusVO.getPpyl());
                        fpylcxRsp.setDPYL(countSurplusVO.getDpyl());
                        fpylcxRsps.add(fpylcxRsp);
                    });
                    
                }
            }
            //2.组装查询数据
            //组装参数
            
        } catch (Exception e) {
            log.error("{}查询发票余量信息异常：{}", LOGGER_MSG_V4, e);
            FPYLCX_RSP fpylcxRsp = FPYLCX_RSP.build(OrderInfoContentEnum.QUERY_INVOICE_STORE_194999);
            fpylcxRsp.setXHFSBH(fpylcxReq.getXHFSBH());
            fpylcxRsps.add(fpylcxRsp);
        }
        return fpylcxRsps;
    }

    /**
     * 订单删除接口
     * @param ddsc_req
     * @return
     */
    @Override
    public DDSC_RSP orderDelete(DDSC_REQ ddsc_req) {
        log.info("订单删除入参：{}",JsonUtils.getInstance().toJsonString(ddsc_req));
        DDSC_RSP ddsc_rsp = new DDSC_RSP();
        ddsc_rsp.setDDQQLSH(ddsc_req.getDDQQLSH());
        ddsc_rsp.setNSRSBH(ddsc_req.getNSRSBH());

        //销方纳税人识别号不能为空
        if(StringUtils.isEmpty(ddsc_req.getNSRSBH())){
            log.info("订单删除入参错误：销方纳税人识别号不能为空");
            ddsc_rsp.setZTDM(OrderInfoContentEnum.ORDER_DELETE_120001.getKey());
            ddsc_rsp.setZTXX(OrderInfoContentEnum.ORDER_DELETE_120001.getMessage());
            return ddsc_rsp;
        }
        //订单请求流水号不能为空
        if(StringUtils.isEmpty(ddsc_req.getDDQQLSH())){
            log.info("订单删除入参错误：订单请求流水号不能为空");
            ddsc_rsp.setZTDM(OrderInfoContentEnum.ORDER_DELETE_120002.getKey());
            ddsc_rsp.setZTXX(OrderInfoContentEnum.ORDER_DELETE_120002.getMessage());
            return ddsc_rsp;
        }
        //查询订单process
        Map<String,Object> csmap = new HashMap<String,Object>();
        csmap.put("fpqqlsh", ddsc_req.getDDQQLSH());
        List<String> nsrsbhList = new LinkedList<String>();
        nsrsbhList.add(ddsc_req.getNSRSBH());
        List<OrderProcessInfo> orderProcessInfos = apiOrderProcessService.selectOrderProcessByFpqqlshDdhNsrsbh(csmap, nsrsbhList);

        if(orderProcessInfos == null || orderProcessInfos.size()==0){
            log.info("订单不存在");
            ddsc_rsp.setZTDM(OrderInfoContentEnum.ORDER_DELETE_120003.getKey());
            ddsc_rsp.setZTXX(OrderInfoContentEnum.ORDER_DELETE_120003.getMessage());
            return ddsc_rsp;
        }
        OrderProcessInfo orderProcessInfo = orderProcessInfos.get(0);
        log.info("当前订单状态：{}",orderProcessInfo.getDdzt());
//        4:开票中;5:开票成功;6.开票失败;
        if(OrderInfoEnum.ORDER_STATUS_4.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_5.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_6.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(orderProcessInfo.getDdzt())
        || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(orderProcessInfo.getDdzt())
                ){
            log.info("订单处于开票中、开票成功、开票失败状态，不允许删除");
            ddsc_rsp.setZTDM(OrderInfoContentEnum.ORDER_DELETE_120004.getKey());
            ddsc_rsp.setZTXX(OrderInfoContentEnum.ORDER_DELETE_120004.getMessage());
            return ddsc_rsp;
        }

        //修改订单状态业务
        OrderProcessInfo updateOpi = new OrderProcessInfo();
        updateOpi.setFpqqlsh(ddsc_req.getDDQQLSH());
        updateOpi.setOrderStatus("1");

        log.info("修改订单状态为1：失效");
        if (apiOrderProcessService.updateOrderProcessInfoByFpqqlsh(updateOpi, nsrsbhList) > 0) {
            log.info("作废订单成功");
        } else {
            log.info("作废订单失败");
            ddsc_rsp.setZTDM(OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            ddsc_rsp.setZTXX(OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            return ddsc_rsp;
        }
        ddsc_rsp.setZTDM(OrderInfoContentEnum.SUCCESS.getKey());
        ddsc_rsp.setZTXX(OrderInfoContentEnum.SUCCESS.getMessage());
        return ddsc_rsp;


    }

    /**
     * 构建批量开票数据库实体对象
     *
     * @param ddfpxx 订单发票全数据协议bean
     * @return com.dxhy.order.model.InvoiceBatchRequest
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     */
    private List<InvoiceBatchRequest> buildInvoiceBatchRequest(DDFPXX ddfpxx) {
        List<InvoiceBatchRequest> invoiceBatchRequestList = new ArrayList<>();
        InvoiceBatchRequest transitionBatchRequest = new InvoiceBatchRequest();
        transitionBatchRequest = BeanTransitionUtils.transitionInvoiceBatchRequest(ddfpxx);
        //批量开票表主键
        transitionBatchRequest.setId(apiInvoiceCommonService.getGenerateShotKey());
        //根据税号查询税控设备
        String terminalCode = apiTaxEquipmentService.getTerminalCode(ddfpxx.getXHFSBH());
        String fpqqpch = "";
        if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)
                || OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            fpqqpch = RandomUtil.randomNumbers(17);
        } else {
            fpqqpch = apiInvoiceCommonService.getGenerateShotKey();
        }
        transitionBatchRequest.setFpqqpch(fpqqpch);
        invoiceBatchRequestList.add(transitionBatchRequest);
        return invoiceBatchRequestList;
    }
    
    /**
     * 构建订单请求批次对象
     *
     * @param ddfpxx                 订单发票全数据协议bean
     * @param transitionBatchRequest 批量开票数据库实体类
     * @return com.dxhy.order.model.OrderBatchRequest
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     */
    private OrderBatchRequest buildOrderBatchRequest(DDFPXX ddfpxx, List<InvoiceBatchRequest> transitionBatchRequest) {
        OrderBatchRequest obr = new OrderBatchRequest();
        obr = BeanTransitionUtils.transitionOrderBatchRequest(ddfpxx);
        //订单请求批次主键
        obr.setId(apiInvoiceCommonService.getGenerateShotKey());
        //订单请求批次号
        obr.setDdqqpch(transitionBatchRequest.get(0).getFpqqpch());
        return obr;
    }

    /**
     *  构建订单发票协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param ddfpzxx 订单发票全数据返回协议bean
     * @return java.util.List<com.dxhy.order.model.OrderInfo>
     */
    private List<OrderInfo> buildOrderInfo(DDFPZXX ddfpzxx){
        List<OrderInfo> insertOrder = CollectionUtil.newArrayList();
        OrderInfo orderInfo = BeanTransitionUtils.transitionInsertOrderInfo(ddfpzxx.getDDFPXX());
        //订单表主键
        orderInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        //订单处理表id
        orderInfo.setProcessId(apiInvoiceCommonService.getGenerateShotKey());
        //开票项目
        orderInfo.setKpxm(ddfpzxx.getDDMXXX().get(0).getXMMC());
        insertOrder.add(orderInfo);
        return insertOrder;
    }

    /**
     * 构建订单明细业务bean集合
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param ddfpzxx 订单发票全数据返回协议bean
     * @param orderInfo 订单业务bean
     * @return java.util.List<java.util.List<com.dxhy.order.model.OrderItemInfo>>
     */
    private List<List<OrderItemInfo>> buildOrderItemInfo(DDFPZXX ddfpzxx,OrderInfo orderInfo){
        List<List<OrderItemInfo>> insertOrderItem = CollectionUtil.newArrayList();
        List<OrderItemInfo> orderItemInfos = com.dxhy.order.utils.BeanTransitionUtils.transitionOrderItemInfoV3(ddfpzxx.getDDMXXX(), orderInfo.getXhfNsrsbh());
        //订单明细表主键
        orderItemInfos.forEach(orderItemInfo -> {
            orderItemInfo.setOrderInfoId(orderInfo.getId());
            orderItemInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        });
        insertOrderItem.add(orderItemInfos);
        return insertOrderItem;
    }

    /**
     * 构建原始订单到最终订单的关系bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param orderInfo 订单业务bean
     * @return java.util.List<com.dxhy.order.model.OrderOriginExtendInfo>
     */
    private List<OrderOriginExtendInfo> buildOrderOriginExtendInfo(OrderInfo orderInfo){
        List<OrderOriginExtendInfo> originExtendList = CollectionUtil.newArrayList();
        OrderOriginExtendInfo orderOriginExtendInfo = BeanTransitionUtils.buildOrderOriginExtendInfo(orderInfo);
        orderOriginExtendInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        originExtendList.add(orderOriginExtendInfo);
        return originExtendList;
    }

    /**
     * 构建订单处理业务bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param orderInfo 订单业务bean
     * @return java.util.List<com.dxhy.order.model.OrderProcessInfo>
     */
    private List<OrderProcessInfo>  buildOrderProcessInfo(OrderInfo orderInfo){
        List<OrderProcessInfo> insertProcessInfo = CollectionUtil.newArrayList();
        OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
        BeanTransitionUtils.transitionAutoProcessInfo(orderProcessInfo, orderInfo);
    
        orderProcessInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_7.getKey());
        orderProcessInfo.setKpfs(OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey());
        //蓝票
        if (StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey(), orderInfo.getKplx())) {
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_5.getKey());
        }
        //红票
        if (StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey(), orderInfo.getKplx())) {
            orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_7.getKey());
        }
        //订单处理表主键
        orderProcessInfo.setId(orderInfo.getProcessId());
        //订单表id
        orderProcessInfo.setOrderInfoId(orderInfo.getId());
        insertProcessInfo.add(orderProcessInfo);
        return insertProcessInfo;
    }
    
    /**
     * 构建发票批量开票明细bean
     *
     * @param transitionBatchRequest 批量开票bean
     * @param orderInfo              订单业务bean
     * @param i                      已开发票信息集合游标
     * @return java.util.List<com.dxhy.order.model.InvoiceBatchRequestItem>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     */
    private List<List<InvoiceBatchRequestItem>> buildInvoiceBatchRequestItem(List<InvoiceBatchRequest> transitionBatchRequest,
                                                                             OrderInfo orderInfo, int i) {
        List<List<InvoiceBatchRequestItem>> invoiceList = CollectionUtil.newArrayList();
        List<InvoiceBatchRequestItem> insertBatchItem = CollectionUtil.newArrayList();
        InvoiceBatchRequestItem invoiceBatchRequestItem = BeanTransitionUtils.transitionInvoiceBatchRequestItem(
                transitionBatchRequest.get(0), orderInfo, i);
        invoiceBatchRequestItem.setId(apiInvoiceCommonService.getGenerateShotKey());
        insertBatchItem.add(invoiceBatchRequestItem);
        invoiceList.add(insertBatchItem);
        return invoiceList;
    }

    /**
     * 构建订单与发票对应关系业务bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/24
     * @param ddfpxx 订单发票全数据协议bean
     * @param orderInfo 订单业务bean
     * @param invoiceBatchRequestItem 发票批量开票明细bean
     * @return java.util.List<com.dxhy.order.model.OrderInvoiceInfo>
     */
    private List<OrderInvoiceInfo> buildOrderInvoiceInfo(DDFPXX ddfpxx, OrderInfo orderInfo,
                                                         InvoiceBatchRequestItem invoiceBatchRequestItem) {
        List<OrderInvoiceInfo> insertInvoiceInfo = CollectionUtil.newArrayList();
        OrderInvoiceInfo orderInvoiceInfo = BeanTransitionUtils.transitionOrderInvoiceInfo(
                ddfpxx, orderInfo, invoiceBatchRequestItem);
        orderInvoiceInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        insertInvoiceInfo.add(orderInvoiceInfo);
        return insertInvoiceInfo;
    }
}
