package com.dxhy.order.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.SpecialExcelImport;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单数据校验接口
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class ValidateOrderInfoImpl implements ValidateOrderInfo {
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    
    @Resource
    private ApiRushRedInvoiceRequestInfoService apiRushRedInvoiceRequestInfoService;
    
    @Resource
    private ApiTaxClassCodeService apiTaxClassCodeService;
    
    @Resource
    private ICommonDisposeService commonDisposeService;
    
    @Resource
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    private final String LOGGER_MSG = "(订单数据校验)";

    @Override
    public Map<String, String> volidateOrder(CommonOrderInfo commonOrderInfo) {
        log.info("进入验证订单service");
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        if (commonOrderInfo == null) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.PARAM_NULL);
        }
    
        //校验订单和明细信息
        checkResultMap = checkOrderParem(commonOrderInfo);
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
        }
        return checkResultMap;
    }
    
    private Map<String, String> checkOrderParem(CommonOrderInfo commonOrderInfo) {
        //获取订单详情和订单明细
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        List<OrderItemInfo> itemList = commonOrderInfo.getOrderItemInfo();
        
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        if (orderInfo == null) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.PARAM_NULL);
        }
        if (itemList == null || itemList.size() <= 0) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.PARAM_NULL);
        }
        //备注校验
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_BZ, orderInfo.getBz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购方纳税人识别号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GHF_NSRSBH, orderInfo.getGhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //订单号校验
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_DDH, orderInfo.getDdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //开票类型校验
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_KPLX, orderInfo.getKplx());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购方地址
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GHF_DZ, orderInfo.getGhfDz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购放名称校验
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GHF_MC, orderInfo.getGhfMc());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购方电话
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GHF_DH, orderInfo.getGhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //购方银行账号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GHF_YHZH, orderInfo.getGhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        if (StringUtils.isNotBlank(orderInfo.getKphjje()) && StringUtils.isNotBlank(orderInfo.getHjbhsje())
                && StringUtils.isNotBlank(orderInfo.getHjse())) {
            
            double kphjje = Double.parseDouble(orderInfo.getKphjje());
            double kphjse = Double.parseDouble(orderInfo.getHjse());
            double hjbhsje = Double.parseDouble(orderInfo.getHjbhsje());
            //differ = kphjje - (kphjse+hjbhsje) ;
            double differ = MathUtil.sub(String.valueOf(kphjje), String.valueOf(MathUtil.add(String.valueOf(kphjse), String.valueOf(hjbhsje))));
            if (Math.abs(differ) > ConfigureConstant.DOUBLE_PENNY_SIX) {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_JSHJ_ERROR.getKey());
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_JSHJ_ERROR.getMessage());
                return checkResultMap;
            }
        }
        boolean isCpySpbm = false;
        if (apiTaxClassCodeService.queryOilBySpbm(itemList.get(0).getSpbm()) != null) {
            isCpySpbm = true;
        }
        for (int i = 0; i < itemList.size(); i++) {
            OrderItemInfo orderItemInfo = itemList.get(i);
            //明细项目名称校验
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_SPMC, orderItemInfo.getXmmc());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //明细的规格型号
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_GGXH, orderItemInfo.getGgxh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //明细的项目单位
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_XMDW, orderItemInfo.getXmdw());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //数量
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_XMSL, orderItemInfo.getXmsl());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //项目单价
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_XMDJ, orderItemInfo.getXmdj());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //明细项目金额
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_XMJE, orderItemInfo.getXmje());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //明细商品编码
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_SPBM, orderItemInfo.getSpbm());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            if (StringUtils.isNotEmpty(orderItemInfo.getSpbm())) {
                boolean spbm = false;
                for (int j = 0; j < orderItemInfo.getSpbm().length(); j++) {
                    char c = orderItemInfo.getSpbm().charAt(j);
                    if ((c < '0' || c > '9')) {
                        spbm = true;
                    }
                }
                if (spbm) {
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.STRING_ORDER_SPBM_CHINESE.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.STRING_ORDER_SPBM_CHINESE.getMessage());
                    return checkResultMap;
                }
            }
            //明细商品税率
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_ORDER_SL, orderItemInfo.getSl());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
    
            /**
             * 税率不为空时,如果是专票,并且税率为0,提示错误,专票不可以开具0税率发票
             */
            boolean result = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm()) && ConfigureConstant.STRING_000.equals(new BigDecimal(orderItemInfo.getSl()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
    
            if (result) {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.CHECK_ISS7PRI_107118.getKey());
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.CHECK_ISS7PRI_107118.getMessage());
                return checkResultMap;
            }
    
            if (!StringUtils.isBlank(orderItemInfo.getXmsl()) && !StringUtils.isBlank(orderItemInfo.getXmdj())
                    && !StringUtils.isBlank(orderItemInfo.getXmje())) {
                // resultList.add(buildReturnMap(orderExcel.getRowIndex(),
                // ExcelErroMessageEnum.SUCCESSCODE, false));
                try {
                    double je = Double.parseDouble(orderItemInfo.getXmsl()) * Double.parseDouble(orderItemInfo.getXmdj());
                    double ce = new BigDecimal(je).subtract(new BigDecimal(orderItemInfo.getXmje())).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    if (Math.abs(ce) > ConfigureConstant.DOUBLE_PENNY) {
                        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_XMMX_JE_ERROR.getKey());
                        checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_XMMX_JE_ERROR.getMessage());
                        return checkResultMap;
                    }
                } catch (NumberFormatException e) {
    
                }
            }
            //成品油清单标志和单位校验
            OilEntity queryTaxClassCodeEntityBySpbm = apiTaxClassCodeService.queryOilBySpbm(orderItemInfo.getSpbm());
            if (isCpySpbm) {
                if (queryTaxClassCodeEntityBySpbm == null) {
                    log.error("发票中的上品编码只能为成品油或者非成品油中的一种");
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR.getMessage());
                    return checkResultMap;
                }
                if (!OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz()) && !ConfigureConstant.STRING_DUN.equals(orderItemInfo.getXmdw()) && !ConfigureConstant.STRING_SHENG.equals(orderItemInfo.getXmdw())) {
                    log.error("成品油项目单位只能为升或者吨");
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_XMXX_XMDW_ERROR.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_XMXX_XMDW_ERROR.getMessage());
                    return checkResultMap;
                }
                if (StringUtils.isBlank(orderItemInfo.getXmsl())) {
                    log.error("成品油的项目数量不能为空");
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.ORDER__CPY_XMSL_NOTNULL.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.ORDER__CPY_XMSL_NOTNULL.getMessage());
                    return checkResultMap;
    
                }
                if (itemList.size() > 8) {
                    log.error("成品油的项目明细不能超过8行");
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.ORDER__CPY_XMMX_SL_OVER_8_ERROR.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.ORDER__CPY_XMMX_SL_OVER_8_ERROR.getMessage());
                    return checkResultMap;
    
                }
                
            }
            if (!isCpySpbm) {
                if (queryTaxClassCodeEntityBySpbm != null) {
                    log.error("发票中的上品编码只能为成品油或者非成品油中的一种");
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR.getMessage());
                    return checkResultMap;
                }
                
            }
    
        }
        if (isCpySpbm && !OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz())) {
            log.error("成品油发票清单标志必须为4");
            checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.ORDER_CPY_QDBZ_ERROR.getKey());
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.ORDER_CPY_QDBZ_ERROR.getMessage());
            return checkResultMap;
        
        }
        return checkResultMap;
    }
    
    /**
     * 校验开票订单数据,terminalCode必须填写用于区分税控设备
     *
     * @param commonOrderInfo
     * @return
     */
    @Override
    public Map<String, String> checkOrderInvoice(CommonOrderInfo commonOrderInfo) {
        log.debug("{},数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
        // 声明校验结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        /**
         * 校验税控设备是否维护
         */
        if (StringUtils.isBlank(commonOrderInfo.getTerminalCode())) {
            checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.HANDLE_ISSUE_202010.getKey());
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.HANDLE_ISSUE_202010.getKey());
            return checkResultMap;
        }
        //发票开具校验 1.非空和长度校验 2 数据校验
        //1.非空校验 和数据长度校验
        /**
         * 校验订单主体信息和订单明细信息
         */
        List<Map<String, String>> checkInvParamResultList = validateInterfaceOrder.checkInvParam(commonOrderInfo);
        if (ObjectUtil.isNotEmpty(checkInvParamResultList) && checkInvParamResultList.size() > 0) {
            if (checkInvParamResultList.size() > 1) {
                String errorCode = OrderInfoContentEnum.INVOICE_ERROR_CODE_709999.getKey();
                StringBuilder errorMsg = new StringBuilder();
                for (Map<String, String> stringStringMap : checkInvParamResultList) {
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(stringStringMap.get(OrderManagementConstant.ERRORCODE))) {
                        errorMsg.append(stringStringMap.get(OrderManagementConstant.ERRORMESSAGE)).append("\r\n");
                    }
                }
                checkResultMap.put(OrderManagementConstant.ERRORCODE, errorCode);
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, errorMsg.toString());
            } else {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, checkInvParamResultList.get(0).get(OrderManagementConstant.ERRORCODE));
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, checkInvParamResultList.get(0).get(OrderManagementConstant.ERRORMESSAGE));
            }
            
            return checkResultMap;
        }
        
        //2.数据尾插处理
        checkResultMap = checkInvoiceData(commonOrderInfo);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        return checkResultMap;
    }
    
    @Override
    public Map<String, String> checkInvoiceData(CommonOrderInfo commonOrderInfo) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
            // 蓝票数据校验
            return this.checkBlueInvoiceData(commonOrderInfo);
        } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            // 红票数据校验
            return this.checkRedInvoiceData(commonOrderInfo);
        } else {
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_KPLX_ERROR);
            return checkResultMap;
        }
    }
    
    
    /**
     * 蓝票数据校验
     *
     * @param commonOrderInfo
     * @return
     */
    private Map<String, String> checkBlueInvoiceData(CommonOrderInfo commonOrderInfo) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
    
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        List<OrderItemInfo> orderItemInfo = commonOrderInfo.getOrderItemInfo();
        if (Double.parseDouble(orderInfo.getKphjje()) <= 0) {
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_HJJE_ZERO_ERROR);
            return checkResultMap;
        }
        checkResultMap = this.checkDetailsDiscount(orderInfo, orderItemInfo, orderInfo.getKplx());
        return checkResultMap;
    }
    
    /**
     * 对开票明细的数据校验
     *
     * @param orderInfo
     * @param orderItemInfos
     * @param kplx
     * @return
     */
    private Map<String, String> checkDetailsDiscount(OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos, String kplx) {
    
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
    
        /**
         * 发票明细行数据校验----------------------------------------------------------------
         * 1.明细行税额校验，根据 金额 * 税率 = 税额，与 订单中  传递的税额比较，误差不能大于6分钱。
         * 2.明细行金额校验，根据 单价 * 数量 = 金额，与 订单中  传递的金额比较，误差不能大于1分钱。
         * 3.明细行累计税额与合计税额比较，误差不能大于127分钱。
         * 4.明细行第一行不能为折扣行
         * 5.不能连续两行为折扣行
         * 6.折扣行数校验，即必须有足够的被折扣行数。
         * 7.折扣率校验，折扣率不能大于100%，或者 不能小于或者等于0%
         * 8.红票折扣金额不能小于或等于零
         * 9.蓝票折扣金额不能大于或等于零
         */
    
    
        double mxseTotal = 0;
        double mxseTotalFull = 0;
        //项目明细金额之和170.94-17.09
        double xmjeTotal = 0;
        // 更新是否折扣行标志（连续折扣行标记）
        boolean upIsZkh = false;
        boolean isCpySpbm = false;
        if (apiTaxClassCodeService.queryOilBySpbm(orderItemInfos.get(0).getSpbm()) != null) {
            isCpySpbm = true;
        }
        for (int i = 0; i < orderItemInfos.size(); i++) {
            String errorMsgString = "第" + (i + 1) + "行:";
            OrderItemInfo orderItemInfo = orderItemInfos.get(i);
    
            /**
             * 金额（不含税） * 税率 = 税额
             * 计算出的税额 与 订单传递的税额 比较，误差不能超过6分（即小于或等于6分）
             */
            String sl = StringUtil.formatSl(orderItemInfo.getSl());
            double jsse = Double.parseDouble(orderItemInfo.getSe());
            if (StringUtils.isNotBlank(sl)) {
                /**
                 * 支持扣除额进行校验,
                 * 如果是扣除额,需要用金额减去扣除额然后再去计算税额.
                 *
                 */
                // 计算出的税额
                jsse = MathUtil.mul(orderItemInfo.getXmje(), sl);
                if (StringUtils.isNotBlank(orderItemInfo.getKce())) {
                	jsse = MathUtil.mul(DecimalCalculateUtil.decimalFormat(MathUtil.sub(orderItemInfo.getXmje(), orderItemInfo.getKce()), ConfigureConstant.INT_2), sl);
                }
                // 订单中传递的税额
                double se = Double.parseDouble(orderItemInfo.getSe());
                double seCompareResult = MathUtil.sub(DecimalCalculateUtil.decimalFormat(jsse, 2), String.valueOf(se));
        
                // 误差大于6分钱，则税额有误
                if (Math.abs(seCompareResult) > 0.06) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_SE_ERROR);
                    return checkResultMap;
                }
            }
    
    
            /**
             * 项目数量、项目单价都不为空,并且非折扣行的情况下，要求 项目金额  与  （项目数量 * 项目单价） 之差，误差不能大于1分钱（即误差小于或等于1分钱）。
             */
            //非折扣行计算,折扣行不进行计算
            if (!StringUtils.isBlank(orderItemInfo.getXmdj()) && !StringUtils.isBlank(orderItemInfo.getXmsl()) &&
                    (!OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz()))) {
                /**
                 * 金额（不含税） = 项目单价 * 项目数量
                 * 计算出的项目金额 与 订单传递的项目金额 比较，误差不能大于1分钱（即误差小于或等于1分钱）
                 */
                //yxmje - jsXmje; 有误差
                // 金额 = 项目单价 * 项目数量
                double jsXmje = MathUtil.mul(orderItemInfo.getXmdj(), orderItemInfo.getXmsl());
                // 项目金额
                double yxmje = Double.parseDouble(orderItemInfo.getXmje());
                double xmjeCompareResult = MathUtil.sub(String.valueOf(yxmje), DecimalCalculateUtil.decimalFormat(jsXmje, 2));
                // 误差不能大于1分钱（即误差小于或等于1分钱），否则项目金额有误
                if (Math.abs(xmjeCompareResult) > 0.01) {
                    /**
                     * 判断单价是否一致,用金额除以数量得到单价,保留小数点后8位后比较,如果不一致再返回错误,
                     */
                    String jsXmdj = DecimalCalculateUtil.div(orderItemInfo.getXmje(), orderItemInfo.getXmsl(), ConfigureConstant.INT_8);
                    if (!jsXmdj.equals(DecimalCalculateUtil.decimalFormatToString(orderItemInfo.getXmdj(), ConfigureConstant.INT_8))) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_JE_ERROR);
                        log.error("{}项目金额有误,误差不能大于1分钱", LOGGER_MSG);
                        return checkResultMap;
                    }
    
                }
            }
            //成品油的单位只能为吨或者升
            OilEntity queryTaxClassCodeEntityBySpbm = apiTaxClassCodeService.queryOilBySpbm(orderItemInfo.getSpbm());
            if (queryTaxClassCodeEntityBySpbm != null) {
                if (!OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz()) && !ConfigureConstant.STRING_DUN.equals(orderItemInfo.getXmdw()) && !ConfigureConstant.STRING_SHENG.equals(orderItemInfo.getXmdw())) {
                    log.error("{}成品油项目单位只能为吨或升", LOGGER_MSG);
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMXX_XMDW_ERROR);
                    return checkResultMap;
                }
                if (!OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz())) {
                    if (StringUtils.isBlank(orderItemInfo.getXmsl())) {
                        log.error("{}成品油项目数量不能为空", LOGGER_MSG);
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.ORDER__CPY_XMSL_NOTNULL);
                        return checkResultMap;
            
                    }
                }
    
            }
            boolean isOil = (isCpySpbm && queryTaxClassCodeEntityBySpbm == null) || (!isCpySpbm && queryTaxClassCodeEntityBySpbm != null);
            if (isOil) {
                //成品油商品编码和非成品油商品编码混开
                log.error("{}发票只能为成品油或者非成品油", LOGGER_MSG);
                checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR);
                return checkResultMap;
            }
    
    
            /**
             * 判断折扣行金额是否合法
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
                // 蓝票
                if (Double.parseDouble(orderItemInfo.getXmje()) >= 0 && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz()))) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_THAN_ZERO_ERROR);
                    return checkResultMap;
                }
            } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
                // 红票
                if (Double.parseDouble(orderItemInfo.getXmje()) <= 0 && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz()))) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_LESS_ZERO_ERROR);
                    return checkResultMap;
                }
            } else {
                // 非法开票类型
                checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_KPLX_ERROR);
                return checkResultMap;
            }
    
    
            // 是折扣行(根据发票行性质判断是否是折扣行)
            if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz())) {
    
                //判断红票和蓝票的折扣行没有折扣率的,如果以折扣开头的进行校验
                /**
                 * 折扣行格式校验:
                 * 1.如果以折扣开头的项目名称中,不包含英文()和%的返回折扣行格式错误(2016年7月11日 15:14:43 版本升级后不存在这个校验)
                 * 2.括号内去掉百分后后值为空或者折扣率小于0%或者是大与100%,需抛异常(2016年7月11日 15:14:43 版本升级后不存在这个校验)
                 * 3.折扣行数没有行数或折扣行数小于等于1,需抛异常(2016年7月11日 15:14:43 版本升级后不存在这个校验)
                 * 4.单独一个折扣的折扣行,如果折扣两个字和后面的(之间有值,抛异常(2016年7月11日 15:14:43 版本升级后不存在这个校验)
                 */
    
                //折扣行不能为第一行或不能连续两个折扣行！
                if (i == 0 || upIsZkh) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKH_ERROR);
                    return checkResultMap;
                }
    
                //如果走到这里说明第一行不是折扣行,当前行是折扣行需要判断上一行是否为被折扣行,如果不是,返回错误
                if (!OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfos.get(i - 1).getFphxz())) {
                    //对于蓝字发票，金额为负的商品名称必须与与之相邻的上一行的商品名称相同
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKH_ERROR);
                    return checkResultMap;
                }
    
                //如果是老蓝票对应得新红票报文不校验SPBM和XMMC
                boolean result = !(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) && (orderItemInfo.getXmmc().startsWith("折扣行数") || orderItemInfo.getXmmc().startsWith("折扣")) && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfo.getFphxz())));
                if (result) {
                    //如果走到这里说明第一行不是折扣行
                    if (!(orderItemInfo.getXmmc()).equals(orderItemInfos.get(i - 1).getXmmc())) {
                        //对于蓝字发票，金额为负的商品名称必须与与之相邻的上一行的商品名称相同
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKH_ERROR);
                        return checkResultMap;
                    }
        
                    //折扣行与被折扣行的商品编码相同
                    if (!(orderItemInfo.getSpbm().equals(orderItemInfos.get(i - 1).getSpbm()))) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKHANDBZKH_ERROR);
                        return checkResultMap;
                    }
                }
    
    
                /**
                 * ====校验“被折扣商品行金额” 乘以 “折扣率” 是否等于 “折扣额”=============
                 * 逻辑：
                 * 	1、如果第i行商品行是折扣行：商品名称判断是单行折扣还是多行折扣
                 * 		1.1 、单行折扣：(折扣额) /(第i-1行“商品金额”)，如果计算结果和折扣率不相等，返回错误信息。（版本升级后不存在多行折扣，只坐单行折扣校验 2016年7月11日 15:33:43）
                 * 		1.2、 多行折扣：(折扣额) /(第i-n行到i-1行“商品金额”之和)，如果计算结果和折扣率不相等，返回错误信息。（版本升级后不存在多行折扣，只坐单行折扣校验 2016年7月11日 15:33:52）
                 */
    
                // 被折扣行金额加税额之和
                double bzkzjeTotal = 0.0;
                double zke = MathUtil.add(orderItemInfo.getXmje(), orderItemInfo.getSe());
    
                if ((orderItemInfo.getXmmc()).equals(orderItemInfos.get(i - 1).getXmmc())) {
                    // 单行折扣的类型
        
                    //获取被折扣行的不含税金额加上税额,即反推含税金额
                    bzkzjeTotal = MathUtil.add(orderItemInfos.get(i - 1).getXmje().trim(), orderItemInfos.get(i - 1).getSe().trim());
        
                    //折扣校验
                    if ((Math.abs(bzkzjeTotal) < Math.abs(zke))) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKEANDBZKE_ERROR);
                        return checkResultMap;
                    }
        
        
                    /**
                     * 单行折扣,校验 税率是否相等
                     */
        
                    //被折扣行税率
                    String bzkhsl = orderItemInfos.get(i - 1).getSl();
                    //折扣行税率
                    String zkhsl = orderItemInfo.getSl();
                    //判断折扣行税率与被折扣行税率是否一致
                    if (!bzkhsl.equals(zkhsl)) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ZKSL_ERROR);
                        return checkResultMap;
                    }
        
                }
    
    
                upIsZkh = true;
            } else {
                // 非折扣行
                upIsZkh = false;
    
                //只有一个商品行时，发票行性质为必须为0
                if (1 == orderItemInfos.size() && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_ONE_FPHXZ_ERROR);
                    return checkResultMap;
                }
    
                //项目明细最后一行的FPHXZ发票行性质不能为2！2016年12月9日16:40:00  阳开国
                if ((i == (orderItemInfos.size() - 1)) && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_LAST_FPHXZ_ERROR);
                    return checkResultMap;
                }
    
                // 蓝票数据,非最后一行数据,如果发票行性质为被折扣行,那么下一行必须为折扣行
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx()) && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz()) && i != (orderItemInfos.size() - 1)) {
                    if (!(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfos.get(i + 1).getFphxz()))) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_LAST_FPHXZ_ERROR);
                        return checkResultMap;
                    }
                }
    
                /**
                 * 被折扣行和正常商品行
                 * 蓝票单价数量,金额不等小于等于0
                 */
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
                    // 非折扣行蓝票处理,明细行的金额都不能小于等于0
                    if (Double.parseDouble(orderItemInfo.getXmje()) <= 0) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_THAN_ZERO_ERROR);
                        return checkResultMap;
                    }

                } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
                    // 非折扣行红票处理,明细行的金额不能大于等于0
                    if (Double.parseDouble(orderItemInfo.getXmje()) >= 0) {
                        checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_XMMX_LESS_ZERO_ERROR);
                        return checkResultMap;
                    }
                } else {
                    // 非法开票类型
                    checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), errorMsgString, OrderInfoContentEnum.INVOICE_KPLX_ERROR);
                    return checkResultMap;
                }
            }
            // 使用BigDecimal作运算
            mxseTotal = MathUtil.add(String.valueOf(mxseTotal), orderItemInfo.getSe());
        
            //总税额(没有做格式化的)
            mxseTotalFull = DecimalCalculateUtil.add(mxseTotalFull, jsse);
            mxseTotal = MathUtil.add(String.valueOf(mxseTotal), orderItemInfo.getSe());
            xmjeTotal = MathUtil.add(String.valueOf(xmjeTotal), orderItemInfo.getXmje());
        
        }
        if (isCpySpbm && orderItemInfos.size() > ConfigureConstant.INT_8) {
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.ORDER__CPY_XMMX_SL_OVER_8_ERROR);
            log.error("{}成品油明细不能超过8行", LOGGER_MSG);
            return checkResultMap;
        }
    
        String hjbhsje = DecimalCalculateUtil.decimalFormatToString(orderInfo.getHjbhsje(), 2);
    
        if (!hjbhsje.equals(DecimalCalculateUtil.decimalFormat(xmjeTotal, 2))) {
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_XMMX_HJJEANDMXJE_ERROR);
            log.error("{}开具合计金额和明细金额不相等", LOGGER_MSG);
            return checkResultMap;
        }
        /**
         * 明细税额累加 与 发票头中的开票合计税额 比较，误差不能超过127分钱（即小于或等于127分钱）
         */
        double mxseTotalCompareresult = Double.parseDouble(DecimalCalculateUtil.decimalFormat(MathUtil.sub(orderInfo.getHjse(), String.valueOf(mxseTotalFull)), ConfigureConstant.INT_2));
        if (Math.abs(mxseTotalCompareresult) > ConfigureConstant.DOUBLE_PENNY_127) {
        
            // 误差大于127分钱，则合计税额有误
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_XMMX_HJSE_ERROR);
        
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, "当合计税额的误差大于1.27元时，根据税局要求不允许开票，请先将订单拆分后再开票。");
            log.error("{}合计税额有误,误差大于127分钱", LOGGER_MSG);
            return checkResultMap;
        }
    
        return checkResultMap;
    }
    
    /**
     * 红票数据校验
     *
     * @param commonOrderInfo
     * @return
     * @throws Exception
     */
    private Map<String, String> checkRedInvoiceData(CommonOrderInfo commonOrderInfo) {
        String jsonString = JsonUtils.getInstance().toJsonString(commonOrderInfo);
        log.debug("{},红票数据入参:{}", LOGGER_MSG, jsonString);
        Map<String, String> checkResultMap = new HashMap<String, String>(5);
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        List<String> shList = new ArrayList<>();
        shList.add(orderInfo.getXhfNsrsbh());
        List<OrderItemInfo> orderItemInfo = commonOrderInfo.getOrderItemInfo();
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        // 标识不为空 不走下面逻辑，直接开票 可以这样，只有红票走这里，页面开票生成待开时，不走这里
        if (!StringUtils.isBlank(commonOrderInfo.getFlagbs())) {
            return checkResultMap;
        }
        //判断金额正负,如为正转为负
        orderInfo.setHjbhsje(orderInfo.getHjbhsje().contains("-") ? orderInfo.getHjbhsje() : ("-" + orderInfo.getHjbhsje()));
        orderInfo.setKphjje(orderInfo.getKphjje().contains("-") ? orderInfo.getKphjje() : ("-" + orderInfo.getKphjje()));
        orderInfo.setHjse(orderInfo.getHjse().contains("-") ? orderInfo.getHjse() : ("-" + orderInfo.getHjse()));
        if (ConfigureConstant.STRING_000_.equals(orderInfo.getHjse())) {
            orderInfo.setHjse(orderInfo.getHjse().replace("-", ""));
        }
        //红票开票合计金额不能大于或者等于0
        if (Double.parseDouble(orderInfo.getKphjje()) >= 0) {
            checkResultMap = validateInterfaceOrder.generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_HEAD_ERROR_009631);
            log.error("{}红票开票合计金额不能大于或者等于0", LOGGER_MSG);
            return checkResultMap;
        }
        /**
         *  冲红校验
         * 1.首先判断原发票代码和原发票号码是否为空，如果不为空则进行查询原发票数据。
         * 2.判断原发票数据是否存在，如果不为空则进行判断冲红金额是否小于等于剩余可冲红金额。
         */
        if (StringUtils.isNotBlank(orderInfo.getYfpDm()) && StringUtils.isNotBlank(orderInfo.getYfpHm())) {
            //根据发票代码号码查询原发票信息
            OrderInvoiceInfo orderInvoice = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(orderInfo.getYfpDm(), orderInfo.getYfpHm(), shList);
            if (orderInvoice != null) {
                //如果可冲红金额为空，取原蓝票合计金额
                if (StringUtils.isBlank(orderInvoice.getSykchje())) {
                    orderInvoice.setSykchje(orderInvoice.getKphjje());
                }
                /**
                 * 红票对应蓝字发票剩余可充红金额为正.红票开票合计金额为负.
                 * 此处判断最好使用绝对值进行判断.
                 */
                double sykchje = Double.parseDouble(orderInvoice.getSykchje());
                double abs = Math.abs(sykchje);
                double hkphjje = Double.parseDouble(orderInfo.getKphjje());
                double abs2 = Math.abs(hkphjje);
                if (abs2 > abs) {
                    log.error("{}开红票 红票合计金额大于剩余可冲红金额,冲红金额：{}，可冲红金额：{}", LOGGER_MSG, hkphjje, sykchje);
                    checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SYKCHJE_LESS.getKey());
                    checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SYKCHJE_LESS.getMessage());
                    return checkResultMap;
                }
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoice.getFpzlDm())
                        || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInvoice.getFpzlDm())) {
                    if (OrderInfoEnum.READY_ORDER_SJLY_2.getKey().equals(commonOrderInfo.getSjywly())) {
                        if (StringUtils.isBlank(orderInfo.getSld())) {
                            log.debug("{},纸票必须传开票点,发票种类代码{}", LOGGER_MSG, orderInvoice.getFpzlDm());
                            checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SLD.getKey());
                            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_SLD.getMessage());
                            return checkResultMap;
                        }
                    }
                }
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoice.getFpzlDm()) || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInvoice.getFpzlDm())) {
                    Date mm = orderInvoice.getKprq();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String format = sdf.format(new Date());
                    String[] split = format.split("-");
                    String format2 = sdf.format(mm);
                    String[] split2 = format2.split("-");
                    boolean flag = false;
                    int dqDate = Integer.parseInt(split[1]);
                    int invoiceDate = Integer.parseInt(split2[1]);
                    int dd = dqDate - 1;
                    if (ConfigureConstant.STRING_12.equals(split2[1]) && ConfigureConstant.STRING_01.equals(split[1])) {
                        flag = true;
                    } else if (dd == invoiceDate) {
                        flag = true;
                    }
                    if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoice.getChBz())) {
                        log.error("{},原蓝票已冲红", LOGGER_MSG);
                        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_YCH.getKey());
                        checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_YCH.getMessage());
                        return checkResultMap;
                    }
                    if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoice.getZfBz())) {
                        log.error("{},原蓝票已作废", LOGGER_MSG);
                        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_YZF.getMessage());
                        checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_YZF.getMessage());
                        return checkResultMap;
                    } else if (OrderInfoEnum.INVALID_INVOICE_2.getKey().equals(orderInvoice.getZfBz())) {
                        log.error("{},原蓝票状态有处于作废中的数据", LOGGER_MSG);
                        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_ZFZ.getKey());
                        checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_ZFZ.getMessage());
                        return checkResultMap;
                    }
                
                }
            }
        }
        

    
        //明细行合法性校验
        checkResultMap = this.checkDetailsDiscount(orderInfo, orderItemInfo, orderInfo.getKplx());

        return checkResultMap;
    }
    
    /**
     * 专票表格导入校验
     *
     * @param specialExcelImportList
     * @return
     */
    @Override
    public Map<String, Object> checkSpecialExcelImport(List<SpecialExcelImport> specialExcelImportList) {
        //声明返回结果map
        Map<String, Object> resultMap = new HashMap<>(10);
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        List<String> errorList = new ArrayList<>();
        
        /**
         * 循环处理数据
         */
        for (int i = 0; i < specialExcelImportList.size(); i++) {
            SpecialExcelImport specialExcelImport = specialExcelImportList.get(i);
    
            if (specialExcelImport == null) {
                resultMap.putAll(checkResultMap);
                errorList.add(OrderInfoContentEnum.HANDLE_ISSUE_202004.getMessage());
                resultMap.put(OrderManagementConstant.ERROR_MESSAGE_LIST, errorList);
                return resultMap;
            }
            String numMsg = "第" + (i + 1) + "行,";
    
    
            /**
             * 申请单唯一编号
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SQDWYBH, specialExcelImport.getSqdwybh(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
            /**
             * 申请单类型
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SQLX, specialExcelImport.getCypzyfplx(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (!OrderInfoEnum.SPECIAL_INVOICE_TYPE_0.getKey().equals(specialExcelImport.getCypzyfplx()) && !OrderInfoEnum.SPECIAL_INVOICE_TYPE_1.getKey().equals(specialExcelImport.getCypzyfplx()) && !OrderInfoEnum.SPECIAL_INVOICE_TYPE_2.getKey().equals(specialExcelImport.getCypzyfplx()) && !OrderInfoEnum.SPECIAL_INVOICE_TYPE_3.getKey().equals(specialExcelImport.getCypzyfplx())) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SQLX_ERROR.getMessage());
            }
    
            /**
             * 申请单原因
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SQYY, specialExcelImport.getSqyy(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (!OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy()) && !OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialExcelImport.getSqyy()) && !OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialExcelImport.getSqyy())) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SQYY_ERROR.getMessage());
            }
    
            /**
             * 申请单商品名称,销方申请不校验必填,购方校验
             */
            if (OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy())) {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMMC1, specialExcelImport.getSpMc(), i);
            } else {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMMC, specialExcelImport.getSpMc(), i);
            }
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (specialExcelImport.getSpMc().equals(ConfigureConstant.XJZSXHQD) && !OrderInfoEnum.SPECIAL_INVOICE_TYPE_0.getKey().equals(specialExcelImport.getCypzyfplx())) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMMC_ERROR.getMessage());
            }
    
            /**
             * 税收分类编码
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SPBM, specialExcelImport.getSpBm(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
            /**
             * 税率
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SL, specialExcelImport.getSLv(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            //税率必须是百分号格式
            if (StringUtils.isNotBlank(specialExcelImport.getSLv())) {
                if (!specialExcelImport.getSLv().contains(ConfigureConstant.STRING_PERCENT)) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SL_ERROR.getMessage());
                }
                String sl = "";
                try {
                	sl = StringUtil.formatSl(specialExcelImport.getSLv());
				} catch (Exception e) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SL_ERROR.getMessage());
				}
                if (StringUtils.isNotBlank(sl) && BigDecimal.ONE.compareTo(new BigDecimal(sl).abs()) < 0) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SL_ERROR.getMessage());
                }
        
            }
            //税率必须是小于1
            if (StringUtils.isNotBlank(specialExcelImport.getSLv()) && !specialExcelImport.getSLv().contains(ConfigureConstant.STRING_PERCENT)) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SL_ERROR.getMessage());
            }
    
            /**
             * 规格型号
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_GGXH, specialExcelImport.getGgXh(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
            /**
             * 计量单位
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_DW, specialExcelImport.getXmDw(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            //成品油销售金额变更，单位必须为空
            if (StringUtils.isNotBlank(specialExcelImport.getXmDw()) && OrderInfoEnum.SPECIAL_INVOICE_TYPE_2.getKey().equals(specialExcelImport.getCypzyfplx())) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_DW_ERROR.getMessage());
            }
            //成品油销售数量变更，单位必须为吨或升
            if (!ConfigureConstant.STRING_DUN.equals(specialExcelImport.getXmDw()) && !ConfigureConstant.STRING_SHENG.equals(specialExcelImport.getXmDw()) && OrderInfoEnum.SPECIAL_INVOICE_TYPE_1.getKey().equals(specialExcelImport.getCypzyfplx())) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_DW_ERROR1.getMessage());
            }
    
            /**
             * 单价
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMDJ, specialExcelImport.getXmDj(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (!ConfigureConstant.XJZSXHQD.equals(specialExcelImport.getSpMc())) {
                if (StringUtils.isNotBlank(specialExcelImport.getXmDj())) {
                    if (OrderInfoEnum.SPECIAL_INVOICE_TYPE_2.getKey().equals(specialExcelImport.getCypzyfplx())) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMDJ_ERROR.getMessage());
                    }
                    if (!specialExcelImport.getXmDj().matches("\\d+?[.]?\\d{0,8}")) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMDJ_ERROR1.getMessage());
                    }
                }
    
            } else {
                if (StringUtils.isNotBlank(specialExcelImport.getXmDj())) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMDJ_ERROR3.getMessage());
                }
            }
    
            /**
             * 数量
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL, specialExcelImport.getXmSl(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (!ConfigureConstant.XJZSXHQD.equals(specialExcelImport.getSpMc())) {
                if (StringUtils.isNotBlank(specialExcelImport.getXmSl())) {
                    if (OrderInfoEnum.SPECIAL_INVOICE_TYPE_2.getKey().equals(specialExcelImport.getCypzyfplx())) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL_ERROR.getMessage());
                    }
                    if (!specialExcelImport.getXmSl().matches("[-]\\d+?[.]?\\d{0,8}") || "0".equals(specialExcelImport.getXmSl())) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL_ERROR1.getMessage());
                    }
                } else {
                    if (OrderInfoEnum.SPECIAL_INVOICE_TYPE_1.getKey().equals(specialExcelImport.getCypzyfplx())) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL_ERROR2.getMessage());
                    }
                }
            } else {
                if (StringUtils.isNotBlank(specialExcelImport.getXmSl())) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL_ERROR4.getMessage());
                }
            }
            boolean result2 = (StringUtils.isNotBlank(specialExcelImport.getXmSl()) && StringUtils.isBlank(specialExcelImport.getXmDj())) ||
                    StringUtils.isBlank(specialExcelImport.getXmSl()) && StringUtils.isNotBlank(specialExcelImport.getXmDj());
            if (result2) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMSL_ERROR5.getMessage());
            }
    
            /**
             * 金额
             */
            if (OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy())) {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMJE1, specialExcelImport.getXmje(), i);
            } else {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMJE, specialExcelImport.getXmje(), i);
            }
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
    
            if (StringUtils.isNotBlank(specialExcelImport.getXmje()) && !specialExcelImport.getXmje().matches("[-]\\d+?[.]?\\d{0,2}")) {
                errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMJE_ERROR1.getMessage());
            }
    
            /**
             * 税额
             */
            if (OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy())) {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SE1, specialExcelImport.getXmSe(), i);
            } else {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SE, specialExcelImport.getXmSe(), i);
            }
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (StringUtils.isNotBlank(specialExcelImport.getXmSe())) {
                if ("0%".equals(specialExcelImport.getSLv()) && new BigDecimal(specialExcelImport.getXmSe()).compareTo(BigDecimal.ZERO) != 0) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SE_ERROR.getMessage());
                } else {
                    if (!"0%".equals(specialExcelImport.getSLv()) && !specialExcelImport.getXmSe().matches("[-]\\d+?[.]?\\d{0,2}")) {
                        errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_SE_ERROR1.getMessage());
                    }
                }
            }
    
    
            /**
             * 优惠政策标识
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YHZCBS, specialExcelImport.getYhzcbs(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (StringUtils.isNotBlank(specialExcelImport.getYhzcbs())) {
                if (!ConfigureConstant.STRING_SHI.equals(specialExcelImport.getYhzcbs()) && !ConfigureConstant.STRING_FOU.equals(specialExcelImport.getYhzcbs())) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YHZCBS_ERROR.getMessage());
                }
            }
    
            /**
             * 增值税特殊管理
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_ZZSTSGL, specialExcelImport.getZzstsgl(), i);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
            }
            if (StringUtils.isNotBlank(specialExcelImport.getZzstsgl())) {
                if (!ConfigureConstant.STRING_MS.equals(specialExcelImport.getZzstsgl()) && !ConfigureConstant.STRING_BZS.equals(specialExcelImport.getZzstsgl())) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_ZZSTSGL_ERROR.getMessage());
                }
            } else {
                if (ConfigureConstant.STRING_SHI.equals(specialExcelImport.getYhzcbs())) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_ZZSTSGL_ERROR1.getMessage());
                }
            }
            
            /**
             * 针对不同类型进行判断,
             * 购方已抵扣需要校验
             */
            //购方已抵扣
            if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialExcelImport.getSqyy())) {
    
                /**
                 * 销方名称
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XHFMC, specialExcelImport.getXhfMc(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
    
                /**
                 * 销方税号
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XHFSH, specialExcelImport.getXhfSh(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, specialExcelImport.getXhfSh());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                //购方未抵扣
            } else if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialExcelImport.getSqyy())) {
    
                /**
                 * 原发票代码
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPDM, specialExcelImport.getYfpDm(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                if (!specialExcelImport.getYfpDm().matches("\\d+")) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPDM_ERROR.getMessage());
                }
    
                /**
                 * 原发票号码
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPHM, specialExcelImport.getYfpHm(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                if (!specialExcelImport.getYfpHm().matches("\\d+")) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPHM_ERROR.getMessage());
                }
    
                /**
                 * 销方名称
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XHFMC, specialExcelImport.getXhfMc(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
    
                /**
                 * 销方税号
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XHFSH, specialExcelImport.getXhfSh(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, specialExcelImport.getXhfSh());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }

                /**
                 * 原蓝票日期
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YLPRQ, specialExcelImport.getYlprq(), i);
                if(!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))){
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                /**
                 * 日期格式校验
                 */
                if(StringUtils.isNotBlank(specialExcelImport.getYlprq())){
                    try {
                        DateTime parse = DateUtil.parse(specialExcelImport.getYlprq(), "yyyy-mm-dd");
                    } catch (Exception e ){
                        log.warn("日期解析异常，异常信息:{}",e);
                        errorList.add("原蓝票日期格式错误,日期格式只能为: yyyy-mm-dd");
                    }
                }

            } else if (OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy())) {
    
                /**
                 * 原发票代码
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPDM, specialExcelImport.getYfpDm(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                if (!specialExcelImport.getYfpDm().matches("\\d+")) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPDM_ERROR.getMessage());
                }
    
                /**
                 * 原发票号码
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPHM, specialExcelImport.getYfpHm(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
                if (!specialExcelImport.getYfpHm().matches("\\d+")) {
                    errorList.add(numMsg + OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_YFPHM_ERROR.getMessage());
                }
    
                /**
                 * 销方申请时,已经填写代码号码,购方信息和发票信息非必填
                 */
                /**
                 * 购方名称
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_GHFMC, specialExcelImport.getGhfMc(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
    
                /**
                 * 购方名称
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_GHFSH, specialExcelImport.getGhfSh(), i);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    errorList.add(checkResultMap.get(OrderManagementConstant.ERRORMESSAGE));
                }
    
            }
            
            /**
             * 校验商品编码合法性
             * 如果是成品油需要简要是否是成品油商品编码
             */
            if (OrderInfoEnum.SPECIAL_INVOICE_TYPE_1.getKey().equals(specialExcelImport.getCypzyfplx()) || OrderInfoEnum.SPECIAL_INVOICE_TYPE_2.getKey().equals(specialExcelImport.getCypzyfplx())) {
                OilEntity oilEntity = apiTaxClassCodeService.queryOilBySpbm(specialExcelImport.getSpBm());
                if (oilEntity == null) {
                    errorList.add(numMsg + OrderInfoContentEnum.ORDER_SPBM_CPY_ERROR.getMessage());
                }
            }
            
            
        }
        if (errorList.size() > 0) {
            resultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.INTERNAL_SERVER_ERROR.getKey());
            resultMap.put(OrderManagementConstant.ERROR_MESSAGE_LIST, errorList);
            return resultMap;
        }
        resultMap.putAll(checkResultMap);
        return resultMap;
    }
    
    
    /**
     * 校验接口数据合法性(不包含非空校验)
     */
    @Override
    public Map<String, String> checkInvalidInvoice(ZFXX_REQ zfxxReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        if (zfxxReq == null) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108001);
        }
        //批次号
        if (StringUtils.isBlank(zfxxReq.getZFPCH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108002);
        } else if (zfxxReq.getZFPCH().length() > ConfigureConstant.INT_40) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108008);
        }
    
        /**
         * 订单主体-纳税人识别号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_108034, OrderInfoContentEnum.CHECK_ISS7PRI_108035, OrderInfoContentEnum.CHECK_ISS7PRI_107163, zfxxReq.getXHFSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //发票代码
        if (StringUtils.isBlank(zfxxReq.getFPDM())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108003);
        } else if (zfxxReq.getFPDM().length() > ConfigureConstant.INT_12) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108009);
        } else if (!MathUtil.isNumeric(zfxxReq.getFPDM())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108028);
        }
        //发票起号
        if (StringUtils.isBlank(zfxxReq.getFPQH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108004);
        } else if (zfxxReq.getFPQH().length() != ConfigureConstant.INT_8) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108010);
        } else if (!MathUtil.isNumeric(zfxxReq.getFPQH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108029);
        }
        //发票止号
        if (StringUtils.isBlank(zfxxReq.getFPZH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108005);
        } else if (zfxxReq.getFPZH().length() != ConfigureConstant.INT_8) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108011);
        } else if (!MathUtil.isNumeric(zfxxReq.getFPZH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108030);
        }
        //发票起号大于发票止号
        if (Integer.parseInt(zfxxReq.getFPQH()) > Integer.parseInt(zfxxReq.getFPZH())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108031);
        }
        //作废类型
        if (StringUtils.isBlank(zfxxReq.getZFLX())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108006);
        } else if (zfxxReq.getZFLX().length() != 1) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108012);
        } else if (!OrderInfoEnum.ZFLX_0.getKey().equals(zfxxReq.getZFLX()) && !OrderInfoEnum.ZFLX_1.getKey().equals(zfxxReq.getZFLX()) && !OrderInfoEnum.ZFLX_2.getKey().equals(zfxxReq.getZFLX())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108014);
        }
        //作废原因
        if (StringUtils.isNotBlank(zfxxReq.getZFYY()) && zfxxReq.getZFYY().getBytes().length > ConfigureConstant.INT_200) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_108013);
        }
        
        return checkResultMap;
    }
    
    
}
