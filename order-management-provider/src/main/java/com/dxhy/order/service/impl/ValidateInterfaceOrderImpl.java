package com.dxhy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiSpecialInvoiceReversalService;
import com.dxhy.order.api.ApiTaxClassCodeService;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.service.manager.BuyerServiceImpl;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单对外开票数据校验接口
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class ValidateInterfaceOrderImpl implements IValidateInterfaceOrder {
    
    @Resource
    private ICommonDisposeService commonDisposeService;
    
    @Resource
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    @Resource
    private ApiTaxClassCodeService apiTaxClassCodeService;
    
    @Resource
    private BuyerServiceImpl buyerService;
    
    
    private final String LOGGER_MSG = "(订单开票数据校验)";
    
    /**
     * 订单主体校验-内部使用,不对外使用
     * 修改为批次返回错误信息,
     *
     * @param commonOrderInfo
     * @return
     */
    @Override
    public List<Map<String, String>> checkInvParam(CommonOrderInfo commonOrderInfo) {
        
        /**
         * TODO 修改单条返回为批次返回.
         * 1.主体信息为空的需要直接返回不用再继续校验.
         * 2.主体下面字段信息为空需要校验返回列表
         *
         *
         * 2020-08-06 10:13:52
         * 张双超
         */
        String terminalCode = commonOrderInfo.getTerminalCode();
        List<Map<String, String>> resultMapList = new ArrayList<>();
        // 声明校验结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        //1.数据非空和长度校验
        if (commonOrderInfo == null) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202008));
            return resultMapList;
        }
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        if (orderInfo == null) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202004));
            return resultMapList;
        }
    
        List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
        if (CollectionUtils.isEmpty(orderItemInfos)) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202009));
            return resultMapList;
        }
        
        /**
         * 订单发票明细-处理发票明细数据,明细大于2000行校验
         */
        if (ConfigureConstant.MAX_ITEM_LENGTH <= orderItemInfos.size()) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.INVOICE_AUTO_NUMBER));
            return resultMapList;
        }
    
        /**
         * 页面发票直接开具的时候受理点必填 生成待开具发票的时候不需要校验
         */
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm()) || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.getFpzlDm())) {
            if (OrderInfoEnum.ORDER_REQUEST_TYPE_9.getKey().equals(commonOrderInfo.getKpfs())) {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.STRING_FPKJ_SLD, orderInfo.getSld());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    resultMapList.add(checkResultMap);
                }
            }
        }
    
    
        /**
         * 订单主体-订单请求流水号
         */
        //开票方式为0时需要校验订单流水号
        if (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(commonOrderInfo.getKpfs())) {
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107014, orderInfo.getFpqqlsh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                resultMapList.add(checkResultMap);
            }
        } else {
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107014CC, orderInfo.getFpqqlsh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                resultMapList.add(checkResultMap);
            }
        }
    
        /**
         * 订单主体-纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, orderInfo.getNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //批次税号和税号不一致
        //TODO 去掉 与通用校验无关的业务层校验 放到上层业务
        if (StringUtils.isNotBlank(commonOrderInfo.getPcnsrsbh()) && !commonOrderInfo.getPcnsrsbh().equals(orderInfo.getNsrsbh())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107082));
        }
    
    
        /**
         * 订单主体-纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107018, orderInfo.getNsrmc(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-开票类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107020, orderInfo.getKplx());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        //订单主体-开票类型合法性(开票类型只能为0和1：0蓝字发票；1红字发票)
        if (StringUtils.isNotBlank(orderInfo.getKplx()) && !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx()) && !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107021));
        }
    
        /**
         * 订单主体-发票种类代码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107019, orderInfo.getFpzlDm());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //订单主体-发票种类代码合法性(只能为0:专票;2:普票;41:卷票;51:电子票)
        if (StringUtils.isNotBlank(orderInfo.getFpzlDm()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.getFpzlDm()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_41.getKey().equals(orderInfo.getFpzlDm()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107004));
        }
    
        /**
         * 订单主体-编码表版本号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107097, orderInfo.getBbmBbh(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107022, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, orderInfo.getXhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107024, orderInfo.getXhfMc(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 校验销方税号为必填,
         * 其他销方信息为非必填,如果填写进行合法性校验,
         * 校验地址+电话总长度不能大于100
         * 校验银行名称+帐号总长度不能大于100
         */
    
    
        /**
         * 订单主体-销售方地址
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107282, orderInfo.getXhfDz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方电话
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107283, orderInfo.getXhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方银行
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107284, orderInfo.getXhfYh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方帐号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107285, orderInfo.getXhfZh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-销售方地址和电话总长度
         * TODO 由于企业区分不开地址电话,所以校验支持地址电话总长度100,默认应该是85
         */
        String dz_dh = StringUtils.isBlank(orderInfo.getXhfDz()) ? "" : orderInfo.getXhfDz() + (StringUtils.isBlank(orderInfo.getXhfDh()) ? "" : orderInfo.getXhfDh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107267, dz_dh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
    
        /**
         * 订单主体-销售方银行和帐号总长度
         * TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100,默认应该是85
         */
        String yh_zh = StringUtils.isBlank(orderInfo.getXhfYh()) ? "" : orderInfo.getXhfYh() + (StringUtils.isBlank(orderInfo.getXhfZh()) ? "" : orderInfo.getXhfZh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107268, yh_zh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 整体校验购方信息
         */
        checkResultMap = checkGhfParam(orderInfo, commonOrderInfo.getKpfs(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-开票人
         */
    
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044CC, orderInfo.getKpr(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
    
        /**
         * 订单主体-收款人
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107046, orderInfo.getSkr(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单主体-复核人
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107048, orderInfo.getFhr(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
    
        /**
         * 订单主体-清单标志
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107098, orderInfo.getQdBz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //清单标志0-普通发票;1-普通发票(清单);2-收购发票;3-收购发票(清单);4-成品油发票
        if (StringUtils.isNotBlank(orderInfo.getQdBz()) && !OrderInfoEnum.QDBZ_CODE_0.getKey().equals(orderInfo.getQdBz())
                && !OrderInfoEnum.QDBZ_CODE_1.getKey().equals(orderInfo.getQdBz())
                && !OrderInfoEnum.QDBZ_CODE_2.getKey().equals(orderInfo.getQdBz())
                && !OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())
                && !OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107125));
        }
    
        /**
         * 订单主体-清单项目名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107124, orderInfo.getQdXmmc(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //清单标志为1(普通发票清单),3(收购发票清单)时,清单项目名称为必填
        if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(orderInfo.getQdBz()) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())) {
            if (StringUtils.isBlank(orderInfo.getQdXmmc())) {
                resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107099));
            }
        }
    
        /**
         * 订单主体-价税合计
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107141, orderInfo.getKphjje(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
            return resultMapList;
        } else {
            //价税合计金额不能为0或者0.00
            if (ConfigureConstant.STRING_0.equals(orderInfo.getKphjje()) || ConfigureConstant.STRING_000.equals(orderInfo.getKphjje()) || ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getKphjje())) {
                resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107066));
            }
            //开票类型为0(蓝票)时,金额必须大于0
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx()) && ConfigureConstant.DOUBLE_PENNY_ZERO >= new BigDecimal(orderInfo.getKphjje()).doubleValue()) {
                resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107080));
            }
            //开票类型为1(红票)时,金额必须小于0
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) && ConfigureConstant.DOUBLE_PENNY_ZERO <= new BigDecimal(orderInfo.getKphjje()).doubleValue()) {
                resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107083));
            }
        }
    
    
        /**
         * 订单主体-合计金额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107142, orderInfo.getHjbhsje(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //合计金额为不为0时,需要保证金额为小数点后两位
        if (StringUtils.isNotBlank(orderInfo.getHjbhsje()) && ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderInfo.getHjbhsje()).doubleValue() && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getHjbhsje())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107135));
        }
    
        /**
         * 订单主体-合计税额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107143, orderInfo.getHjse(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //合计金额为不为0时,需要保证金额为小数点后两位
        if (StringUtils.isNotBlank(orderInfo.getHjse()) && ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderInfo.getHjse()).doubleValue() && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderInfo.getHjse())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107136));
        }
    
    
        /**
         * 订单主体-特殊冲红标志
         */
    
        if (StringUtils.isNotBlank(orderInfo.getTschbz())) {
            //红票特殊冲红标志只能为0和1：0为正常冲红,1为特殊冲红
            if (!OrderInfoEnum.TSCHBZ_0.getKey().equals(orderInfo.getTschbz()) && !OrderInfoEnum.TSCHBZ_1.getKey().equals(orderInfo.getTschbz())) {
                resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107054));
            }
        }
    
        /**
         * 红票逻辑校验
         * 通过开票类型校验
         */
        if (StringUtils.isBlank(commonOrderInfo.getFlagbs())) {
        
            /**
             * 订单主体-特殊冲红标志
             * 开票类型为1时,特殊冲红标志为必填,
             * 红票特殊冲红标志只能为0和1：0为正常冲红,1为特殊冲红
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            
                /**
                 * 订单主体-特殊冲红标志
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107053, orderInfo.getTschbz());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    resultMapList.add(checkResultMap);
                }
            
                /**
                 * 订单主体-原发票代码和原发票号码
                 * 开票类型为1(红票)时,特殊冲红标识为0(正常冲红)时,发票种类为2(纸质普通发票),41(纸质卷票),51(电子普票)时,原发票代码号码必填
                 */
                if (OrderInfoEnum.TSCHBZ_0.getKey().equals(orderInfo.getTschbz())) {
                    //原发票代码
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107049, orderInfo.getYfpDm());
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        resultMapList.add(checkResultMap);
                    }
                    //原发票代码
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107050, orderInfo.getYfpHm());
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        resultMapList.add(checkResultMap);
                    }
                }
            
            
            } else {
                if (StringUtils.isNotBlank(orderInfo.getYfpDm()) || StringUtils.isNotBlank(orderInfo.getYfpHm())) {
                    resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107047));
                }
            
            }
        
        }
    
        /**
         * 订单主体-订单号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107067, orderInfo.getDdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
    
        /**
         * 订单主体-订单日期
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107068, orderInfo.getDdrq() == null ? "" : DateUtils.getDateStr(orderInfo.getDdrq(), DateUtils.DATE_TIME_PATTERN));
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 发票主体-门店号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107293, orderInfo.getMdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 发票主体-业务类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107294, orderInfo.getYwlx());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 发票主体-提取码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107295, orderInfo.getTqm());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //如果提取码为空,开票方式不为0,不为1时返回错误 开票方式为静态码开票时tqm不能为空
        if (StringUtils.isNotBlank(commonOrderInfo.getKpfs()) && commonOrderInfo.getKpfs().equals(OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey()) && StringUtils.isBlank(orderInfo.getTqm())) {
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107296));
        }

    
        /**
         * 订单主体-冲红原因
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107052, orderInfo.getChyy());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
    
        /**
         * 订单主体-备注
         */
    
        /**
         * 当开票类型为红票时
         * 1.专票备注不能为空.
         */
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            //发票类别为专票时,备注不能为空.
            if (StringUtils.isBlank(orderInfo.getBz())) {
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                    resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107127));
                }
            
            } else {
            
                /**
                 * 订单主体-备注
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107128, orderInfo.getBz());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    resultMapList.add(checkResultMap);
                }
            
                if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                    if (!orderInfo.getBz().contains(ConfigureConstant.STRING_HZBZ)) {
                        resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107129));
                    }
                    int index = orderInfo.getBz().indexOf(ConfigureConstant.STRING_HZBZ);
                    if (orderInfo.getBz().length() < ConfigureConstant.INT_32) {
                        resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107129));
                    } else {
                        String xxbbh = orderInfo.getBz().substring(index + 16, index + 32);
                        if (!ValidateUtil.isNumeric(xxbbh)) {
                            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107129));
                        }
                        /**
                         * 查询红字申请单信息表,
                         * 如果存在数据,信息表填写的代码号码和开票申请的代码号码不一致返回失败.
                         */
                        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySubmitCode(xxbbh);
                        boolean result = ObjectUtil.isNotEmpty(specialInvoiceReversalEntity) && StringUtils.isNotBlank(orderInfo.getYfpDm())
                                && StringUtils.isNotBlank(orderInfo.getYfpDm()) && (!orderInfo.getYfpDm().equals(specialInvoiceReversalEntity.getYfpDm()) || !orderInfo.getYfpHm().equals(specialInvoiceReversalEntity.getYfpHm()));
                        if (result) {
                            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107126));
                        }
                    }
    
    
                }
            }
    
        } else {
            /**
             * 订单主体-备注  为蓝票时非必填，限制长度200
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107128, orderInfo.getBz());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                resultMapList.add(checkResultMap);
            }
        }
        //订单主体-备注,处理差额征税验证.
        if (StringUtils.isNotEmpty(orderInfo.getBz())) {
            /**
             * 判断是否为扣除额数据
             * 如果为扣除额数据,判断备注是否填写扣除额
             * 如果未填写进行扣除额补全,蓝票补全格式为:差额征税：XXX.XX。YYYYY,红票补全格式为:差额征税。YYYY;
             * (XXX.XX表示扣除额,跟明细行扣除额一致,保留小数点后两位,YYYYY表示用户填写的备注)
             * 如果备注不为空,并且包含差额征税和句号.
             * 截取差额征税：和句号之间的差额值,做格式化校验
             * 如果截取字符串长度不对,提示错误
             * 如果扣除额为空提示错误,
             * 如果扣除额格式不合法提示错误
             *
             * 如果是红票需要检验格式,扣除额必须为空
             */
            String bz = orderInfo.getBz();
    
            //扣除额的发票添加备注 扣除额
            if (ObjectUtil.isNotEmpty(commonOrderInfo.getOrderItemInfo()) && commonOrderInfo.getOrderItemInfo().size() > 0 && commonOrderInfo.getOrderItemInfo().size() <= 2 && StringUtils.isNotBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce())) {
        
                //备注中不包含差额征税,需要补全差额征税,todo 该处赋值逻辑不生效
                if (!bz.contains(ConfigureConstant.STRING_CEZS)) {
                    if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
                        orderInfo.setBz(ConfigureConstant.STRING_CEZS_ + commonOrderInfo.getOrderItemInfo().get(0).getKce() + ConfigureConstant.STRING_JH + bz);
                    } else {
                        orderInfo.setBz(ConfigureConstant.STRING_CEZS + ConfigureConstant.STRING_JH + bz);
                    }
            
                } else {
                    //备注中包含差额征税,需要校验差额征税填写格式
                    if (bz.contains(ConfigureConstant.STRING_CEZS) && bz.contains(ConfigureConstant.STRING_JH)) {
                        int start = bz.indexOf(ConfigureConstant.STRING_CEZS);
                        int end = bz.indexOf(ConfigureConstant.STRING_JH);
                        if (start > end) {
                            resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107297));
                        } else {
                    
                            //判断红票和蓝票数据,如果为蓝票,扣除额必填,如果填写校验扣除额格式,校验扣除额和明细填写是否一致,如果为红票,校验格式,需要以差额征税。开头
                            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
                                String kce = bz.substring(start + 5, end);
                                if (StringUtils.isNotEmpty(kce)) {
                            
                            
                                    if (ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(kce)) {
                                        resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107297));
                                    }
                                    if (!commonOrderInfo.getOrderItemInfo().get(0).getKce().equals(kce)) {
                                        resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107300));
                                    }
                            
                                } else {
                                    resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107298));
                                }
                            } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                                if (!bz.contains(ConfigureConstant.STRING_CEZS + ConfigureConstant.STRING_JH)) {
                                    resultMapList.add(generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107299));
                                }
                            }
                    
                        }
                
                    }
                }
            }
    
    
        }
    
        /**
         * 订单主体-退回单号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107069, orderInfo.getThdh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            /**
             * 正常红票校验退回单号必填,
             * 专票校验,红字信息表编号查询是否存在红字申请单,如果存在红字申请单,根据申请单类型判断是否校验退回单号必填.
             * 购方已抵扣情况不校验必填,非购方已抵扣校验必填.
             * 即:电票,普票,专票的购方未抵扣,专票的销方申请校验退回单号必填
             */
            boolean isBuyer = false;
            if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm()) && StringUtils.isNotBlank(orderInfo.getBz()) && orderInfo.getBz().contains(ConfigureConstant.STRING_HZBZ) && orderInfo.getBz().length() > ConfigureConstant.INT_32) {
                int index = orderInfo.getBz().indexOf(ConfigureConstant.STRING_HZBZ);
                String xxbbh = orderInfo.getBz().substring(index + 16, index + 32);
                SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.selectSpecialInvoiceReversalBySubmitCode(xxbbh);
                if (specialInvoiceReversalEntity != null && StringUtils.isNotBlank(specialInvoiceReversalEntity.getSqsm())) {
            
                    if (OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoiceReversalEntity.getSqsm())) {
                        isBuyer = true;
                    }
                }
            }
            if (!isBuyer) {
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107070, orderInfo.getThdh());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    resultMapList.add(checkResultMap);
                }
            }
        
        }
    
        boolean sgbz = false;
        if (OrderInfoEnum.QDBZ_CODE_2.getKey().equals(orderInfo.getQdBz()) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())) {
            sgbz = true;
        }
    
        /**
         * 金额关系合法性校验
         */
        if (!StringUtils.isBlank(orderInfo.getKphjje()) && !StringUtils.isBlank(orderInfo.getHjse()) && !StringUtils.isBlank(orderInfo.getHjbhsje())) {
        
            double differ = MathUtil.sub(orderInfo.getKphjje(), String.valueOf(MathUtil.add(orderInfo.getHjbhsje(), orderInfo.getHjse())));
            //如果误差值等于含税金额,说明是含税金额不作校验,如果是尾插不等于0,校验返回
            if (DecimalCalculateUtil.decimalFormatToString(orderInfo.getKphjje(), ConfigureConstant.INT_2).equals(DecimalCalculateUtil.decimalFormatToString(String.valueOf(differ), ConfigureConstant.INT_2))) {
            
            } else if (ConfigureConstant.DOUBLE_PENNY_ZERO != differ) {
                checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), "", OrderInfoContentEnum.INVOICE_JSHJ_ERROR);
                resultMapList.add(checkResultMap);
            }
        
        }
    
        /**
         * 明细行数据与发票头数据进行校验
         */
        BigDecimal kphjje = new BigDecimal(orderInfo.getKphjje());
        BigDecimal sumKphjje = BigDecimal.ZERO;
        // 更新是否折扣行标志（连续折扣行标记）
        boolean upIsZkh = false;
        for (int j = 0; j < orderItemInfos.size(); j++) {
    
            String errorMsg = "发票请求流水号：" + orderInfo.getFpqqlsh() + ",第" + (j + 1) + "行,";
            OrderItemInfo orderItemInfo = orderItemInfos.get(j);
            List<Map<String, String>> checkItemResultMapList = checkCommonOrderItemsV3(orderItemInfo, orderItemInfos.size(), orderInfo.getKplx(), orderInfo.getFpzlDm(), sgbz, j, terminalCode);
            if (ObjectUtil.isNotEmpty(checkItemResultMapList) && checkItemResultMapList.size() > 0) {
                resultMapList.addAll(checkItemResultMapList);
                continue;
            } else {
                /**
                 * 对发票行性质进行校验
                 */
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
                    if (j == 0 || upIsZkh) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_ZKH_ERROR);
                        resultMapList.add(checkResultMap);
                        continue;
                    }
            
                    //如果走到这里说明第一行不是折扣行,当前行是折扣行需要判断上一行是否为被折扣行,如果不是,返回错误
                    if (!OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfos.get(j - 1).getFphxz())) {
                        //对于蓝字发票，金额为负的商品名称必须与与之相邻的上一行的商品名称相同
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_ZKH_ERROR);
                        resultMapList.add(checkResultMap);
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
            
                    if ((orderItemInfo.getXmmc()).equals(orderItemInfos.get(j - 1).getXmmc())) {
                        // 单行折扣的类型
                
                        //获取被折扣行的不含税金额加上税额,即反推含税金额
                        bzkzjeTotal = MathUtil.add(orderItemInfos.get(j - 1).getXmje().trim(), orderItemInfos.get(j - 1).getSe().trim());
                
                        //折扣校验
                        if ((Math.abs(bzkzjeTotal) < Math.abs(zke))) {
                            checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_ZKEANDBZKE_ERROR);
                            resultMapList.add(checkResultMap);
                        }
                
                
                        /**
                         * 单行折扣,校验 税率是否相等
                         */
                
                        //被折扣行税率
                        String bzkhsl = orderItemInfos.get(j - 1).getSl();
                        //折扣行税率
                        String zkhsl = orderItemInfo.getSl();
                        //判断折扣行税率与被折扣行税率是否一致
                        if (!bzkhsl.equals(zkhsl)) {
                            checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_ZKSL_ERROR);
                            resultMapList.add(checkResultMap);
                        }
                
                    }
            
                    /**
                     * 折扣行项目数量必须为空
                     */
                    if (StringUtils.isNotBlank(orderItemInfo.getXmsl())) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107084);
                        resultMapList.add(checkResultMap);
                    }
            
                    /**
                     * 折扣行项目数量必须为空
                     */
                    if (StringUtils.isNotBlank(orderItemInfo.getGgxh())) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107085);
                        resultMapList.add(checkResultMap);
                    }
            
                    /**
                     * 折扣行项目数量必须为空
                     */
                    if (StringUtils.isNotBlank(orderItemInfo.getXmdw())) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107086);
                        resultMapList.add(checkResultMap);
                    }
            
            
                    upIsZkh = true;
                } else {
                    // 非折扣行
                    upIsZkh = false;
            
                    //只有一个商品行时，发票行性质为必须为0
                    if (1 == orderItemInfos.size() && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_ONE_FPHXZ_ERROR);
                        resultMapList.add(checkResultMap);
                    }
            
                    //项目明细最后一行的FPHXZ发票行性质不能为2！2016年12月9日16:40:00  阳开国
                    if ((j == (orderItemInfos.size() - 1)) && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz())) {
                        checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_LAST_FPHXZ_ERROR);
                        resultMapList.add(checkResultMap);
                    }
            
                    // 蓝票数据,非最后一行数据,如果发票行性质为被折扣行,那么下一行必须为折扣行
                    if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx()) && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfo.getFphxz()) && j != (orderItemInfos.size() - 1)) {
                        if (!(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfos.get(j + 1).getFphxz()))) {
                            checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_LAST_FPHXZ_ERROR);
                            resultMapList.add(checkResultMap);
                        }
                    }
            
                    /**
                     * 被折扣行和正常商品行
                     * 蓝票单价数量,金额不等小于等于0
                     */
                    if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
                        // 非折扣行蓝票处理,明细行的金额都不能小于等于0
                        if (Double.parseDouble(orderItemInfo.getXmje()) <= 0) {
                            checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_THAN_ZERO_ERROR);
                            resultMapList.add(checkResultMap);
                        }
                
                    } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                        // 非折扣行红票处理,明细行的金额不能大于等于0
                        if (Double.parseDouble(orderItemInfo.getXmje()) >= 0) {
                            checkResultMap = generateErrorMap(orderInfo.getFpqqlsh(), errorMsg, OrderInfoContentEnum.INVOICE_XMMX_LESS_ZERO_ERROR);
                            resultMapList.add(checkResultMap);
                        }
                    }
                }
            }
    
            if (OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz())) {
                sumKphjje = sumKphjje.add(new BigDecimal(orderItemInfo.getXmje()));
            } else {
                sumKphjje = sumKphjje.add(new BigDecimal(orderItemInfo.getXmje())).add(new BigDecimal(orderItemInfo.getSe()));
            }
        
        }
    
    
        if (kphjje.subtract(sumKphjje).abs().compareTo(BigDecimal.ZERO) > 0) {
    
            resultMapList.add(generateErrorMap(OrderInfoContentEnum.PRICE_TAX_SEPARATION_NE_KPHJJE));
        }
        
        return resultMapList;
    }
    
    @Override
    public Map<String, String> checkGhfParam(OrderInfo orderInfo, String kpfs, String terminalCode) {
        
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        /**
         * 新增购买方ID,ID非必填,如果填写ID需要保证购方税号,名称,地址,电话,银行,帐号等非必填.
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107286, orderInfo.getGhfId());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 购货方名称,购货方税号,购货方地址,购货方电话,购货方银行,购货方帐号先按照非必填的进行校验
         */
        
        /**
         * 订单主体-购买方纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107288, orderInfo.getGhfMc(), terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107031, OrderInfoContentEnum.CHECK_ISS7PRI_107025, OrderInfoContentEnum.CHECK_ISS7PRI_107023, orderInfo.getGhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方地址
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107289, orderInfo.getGhfDz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方电话
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107290, orderInfo.getGhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方银行
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107291, orderInfo.getGhfYh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方帐号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107292, orderInfo.getGhfZh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购货方地址和电话总长度
         * TODO 由于企业区分不开银行帐号,所以校验支持地址和电话总长度100,默认应该是85
         */
        String ghfdzdh = (StringUtils.isBlank(orderInfo.getGhfDz()) ? "" : orderInfo.getGhfDz()) + (StringUtils.isBlank(orderInfo.getGhfDh()) ? "" : orderInfo.getGhfDh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107269, ghfdzdh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购货方银行和帐号总长度
         * TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100,默认应该是85
         */
        String ghfyhzh = (StringUtils.isBlank(orderInfo.getGhfYh()) ? "" : orderInfo.getGhfYh()) + (StringUtils.isBlank(orderInfo.getGhfZh()) ? "" : orderInfo.getGhfZh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107270, ghfyhzh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        //如果是专票并且购方id为空,购货方地址,购货方银行为必填,购货方名称,购货方税号为必填,同时校验税号合法性
        if (StringUtils.isBlank(orderInfo.getGhfId()) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
            /**
             * 专票-购买方地址
             * TODO 由于企业区分不开地址电话,所以校验支持地址电话总长度100,默认应该是80
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107265, orderInfo.getGhfDz());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            /**
             * 专票-购买方银行
             * TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107266, orderInfo.getGhfYh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            /**
             * 订单主体-购买方名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107032, orderInfo.getGhfMc());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            /**
             * 订单主体-购买方纳税人识别号
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107027, orderInfo.getGhfNsrsbh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            
        }
        
        /**
         * 如果开票方式为自动开票校验数据必填
         * 开票方式0为自动开票
         *
         */
        if (OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(kpfs)) {
            
            if (StringUtils.isBlank(orderInfo.getGhfId())) {
                
                
                /**
                 * 订单主体-购买方企业类型
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107035, orderInfo.getGhfQylx());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
                
                /**
                 * 订单主体-购买方名称
                 */
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107032, orderInfo.getGhfMc());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
            }
            
            
            /**
             * 订单主体-开票人
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044, orderInfo.getKpr());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }
        
        if (StringUtils.isNotBlank(orderInfo.getGhfQylx())) {
            
            //订单主体-企业类型合法性(企业类型只能为:01企业，02机关事业单位，03个人，04其他)
            if (!OrderInfoEnum.GHF_QYLX_01.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_02.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_03.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_04.getKey().equals(orderInfo.getGhfQylx())) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107036);
            }
            
            //订单主体-企业类型为01(企业),02(机关事业单位)时需要保证购方税号非空并且正确
            if (OrderInfoEnum.GHF_QYLX_01.getKey().equals(orderInfo.getGhfQylx())) {
                checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107027, OrderInfoContentEnum.CHECK_ISS7PRI_107025, OrderInfoContentEnum.CHECK_ISS7PRI_107023, orderInfo.getGhfNsrsbh());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
            }
        }
        
        
        /**
         * 订单主体-购买方省份
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107037, orderInfo.getGhfSf());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方手机
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107040, orderInfo.getGhfSj());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单主体-购买方邮箱
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107042, orderInfo.getGhfEmail());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        return checkResultMap;
    }
    
    /**
     * 组装返回信息
     *
     * @param fpqqlsh
     * @param errorMsg
     * @param orderInfoContentEnum
     * @return
     */
    @Override
    public Map<String, String> generateErrorMap(String fpqqlsh, String errorMsg, OrderInfoContentEnum orderInfoContentEnum) {
        StringBuilder stringBuilder = new StringBuilder();
        
        if (StringUtils.isBlank(fpqqlsh)) {
            stringBuilder.append(errorMsg).append(orderInfoContentEnum.getMessage());
        } else {
            stringBuilder.append("请求流水号:").append(fpqqlsh).append(",").append(errorMsg).append(orderInfoContentEnum.getMessage());
        }
        Map<String, String> errorMap = new HashMap<>(10);
        errorMap.put(OrderManagementConstant.ERRORCODE, orderInfoContentEnum.getKey());
        errorMap.put(OrderManagementConstant.ERRORMESSAGE, stringBuilder.toString());
        log.error("{}数据校验结果码为:{},校验结果信息为:{}", LOGGER_MSG, orderInfoContentEnum.getKey(), stringBuilder.toString());
        return errorMap;
    }
    
    /**
     * 组装返回信息
     *
     * @param orderInfoContentEnum
     * @return
     */
    public Map<String, String> generateErrorMap(OrderInfoContentEnum orderInfoContentEnum) {
        
        Map<String, String> errorMap = new HashMap<>(2);
        errorMap.put(OrderManagementConstant.ERRORCODE, orderInfoContentEnum.getKey());
        errorMap.put(OrderManagementConstant.ERRORMESSAGE, orderInfoContentEnum.getMessage());
        log.error("{}单条数据校验结果码为:{},校验结果信息为:{}", LOGGER_MSG, orderInfoContentEnum.getKey(), orderInfoContentEnum.getMessage());
        return errorMap;
    }
    
    /**
     * 开票接口数据校验
     *
     * @param ddpcxx_req
     * @param secretId
     * @param terminalCode
     * @return
     */
    @Override
    public Map<String, String> checkInterfaceParamV3(DDPCXX_REQ ddpcxx_req, String secretId, String terminalCode) {
        
        //声明返回结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        /**
         * 1.发票请求批次数据进行校验
         * 2.首先校验请求数据是否为空
         * 3.校验批次表数据正确性与合法性
         * 4.校验订单表主体数据是否为空
         * 5.校验订单主体数据正确性和合法性
         * 6.校验订单明细表数据是否为空
         * 7.校验订单明细表数据正确性与合法性
         * 8.所有异常数据统一返回.
         */
        //1入参数据非空校验
        if (ddpcxx_req == null) {
            return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202008);
        }
        DDPCXX ddpcxx = ddpcxx_req.getDDPCXX();
        if (ddpcxx == null) {
            return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202008);
        }
        List<DDZXX> ddzxx = ddpcxx_req.getDDZXX();
        if (ddzxx == null || ddzxx.size() <= 0) {
            return generateErrorMap(OrderInfoContentEnum.HANDLE_ISSUE_202004);
        }
        
        //3校验批次表数据正确性与合法性
        checkResultMap = checkCommonOrderBatchV3(ddpcxx, secretId);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        //4校验发票主体数据
        checkResultMap = checkCommonOrderV3(ddzxx, ddpcxx, terminalCode);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        
        return checkResultMap;
        
    }
    
    /**
     * 校验请求发票数据信息
     *
     * @param ddzxxes
     * @param ddpcxx
     * @param terminalCode
     * @return
     */
    public Map<String, String> checkCommonOrderV3(List<DDZXX> ddzxxes, DDPCXX ddpcxx, String terminalCode) {
        //声明返回结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        /**
         * 循环处理数据
         */
        List<Map<String, String>> checkInvParamResultList = new ArrayList<>();
        /**
         * 校验订单主体信息和订单明细信息
         */
        for (DDZXX ddzxx : ddzxxes) {
        
            CommonOrderInfo commonOrderInfo = BeanTransitionUtils.transitionCommonOrderInfoV3(ddzxx);
    
            /**
             * 种类代码转换
             */
            String fplb = CommonUtils.transFpzldm(ddpcxx.getFPLXDM());
    
    
            commonOrderInfo.getOrderInfo().setFpzlDm(fplb);
            commonOrderInfo.setKpfs(ddpcxx.getKPFS());
            commonOrderInfo.setPcnsrsbh(ddpcxx.getNSRSBH());
            commonOrderInfo.setSld(ddpcxx.getKPZD());
            commonOrderInfo.getOrderInfo().setSld(ddpcxx.getKPZD());
            commonOrderInfo.getOrderInfo().setYwlx(ddzxx.getDDTXX().getYWLX());
            /**
             * 税控设备类型添加到订单主信息中
             */
            commonOrderInfo.setTerminalCode(terminalCode);
    
    
            /**
             * 校验订单主体信息和订单明细信息
             */
            List<Map<String, String>> checkInvParamList = checkInvParam(commonOrderInfo);
            if (ObjectUtil.isNotEmpty(checkInvParamList) && checkInvParamList.size() > 0) {
                checkInvParamResultList.addAll(checkInvParamList);
        
            }
    
    
        }
    
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
        
        }
    
        return checkResultMap;
    
    }
    
    /**
     * 校验请求订单批次数据信息正确性与合法性
     *
     * @param ddpcxx
     * @return
     */
    public Map<String, String> checkCommonOrderBatchV3(DDPCXX ddpcxx, String secretId) {
        //声明返回结果map
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        /**
         * 订单请求批次号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107003, ddpcxx.getDDQQPCH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 订单请求纳税人识别号
         */
        
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107005, OrderInfoContentEnum.CHECK_ISS7PRI_107006, OrderInfoContentEnum.CHECK_ISS7PRI_107163, ddpcxx.getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 订单请求发票类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107010, ddpcxx.getFPLXDM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //订单请求发票类型合法性
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddpcxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddpcxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(ddpcxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(ddpcxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddpcxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddpcxx.getFPLXDM())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107013);
        }
    
        /**
         * 订单请求开票点
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107009, ddpcxx.getKPZD());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 订单请求开票方式
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107015, ddpcxx.getKPFS());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //订单请求开票方式校验
        if (StringUtils.isNotBlank(ddpcxx.getKPFS()) && !OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey().equals(ddpcxx.getKPFS()) && !OrderInfoEnum.ORDER_REQUEST_TYPE_1.getKey().equals(ddpcxx.getKPFS()) && !OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(ddpcxx.getKPFS()) && !OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxx.getKPFS())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107165);
        }
        
        /**
         * 订单请求是否成品油
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107008, ddpcxx.getCPYBS());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //订单请求是否成品油校验
        if (StringUtils.isNotBlank(ddpcxx.getCPYBS()) && !OrderInfoEnum.ORDER_REQUEST_OIL_0.getKey().equals(ddpcxx.getCPYBS()) && !OrderInfoEnum.ORDER_REQUEST_OIL_1.getKey().equals(ddpcxx.getCPYBS())) {
            return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107166);
        }
        
        /**
         * 校验当前税号数据合法性
         * 1.校验当前税号secretId和数据库维护的数据是否一致?+
         */
        if (!OrderInfoEnum.ORDER_REQUEST_TYPE_2.getKey().equals(ddpcxx.getKPFS()) && !OrderInfoEnum.ORDER_REQUEST_TYPE_3.getKey().equals(ddpcxx.getKPFS())) {
            //当secretId不为空时，校验id是否一致
            if (StringUtils.isNotBlank(secretId)) {
                String dbSecretId = commonDisposeService.getAuthMap(ddpcxx.getNSRSBH());
                //校验id是否一致?
                if (!secretId.equals(dbSecretId)) {
                    return generateErrorMap(OrderInfoContentEnum.INVOICE_ERROR_CODE_010004_V3);
                }
            }
        }
        
        
        // TODO: 2019/9/2 后期添加超限额拆分提示信息
        
        
        return checkResultMap;
    }
    
    /**
     * 校验请求订单批次数据信息中的订单明细信息正确性与合法性
     *
     * @param orderItemInfo
     * @return
     */
    private List<Map<String, String>> checkCommonOrderItemsV3(OrderItemInfo orderItemInfo, int itemLength, String kplx, String fplb, boolean sgfp, int sphh, String terminalCode) {
        List<Map<String, String>> resultMapList = new ArrayList<>();
        Map<String, String> checkResultMap = new HashMap<>(5);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        String errorMsg = "第" + (sphh + 1) + "行,";
        /**
         * 订单明细信息-商品行序号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107058, orderItemInfo.getSphxh(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        /**
         * 订单明细信息-规格型号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107059, orderItemInfo.getGgxh(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        /**
         * 订单明细信息-发票行性质
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107055, orderItemInfo.getFphxz(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票
        if (StringUtils.isNotBlank(orderItemInfo.getFphxz()) && !OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(orderItemInfo.getFphxz()) && !OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(orderItemInfo.getFphxz()) && !OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(orderItemInfo.getFphxz()) && !OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderItemInfo.getFphxz())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107056));
        }
        //发票行性质为清单红票时处理逻辑.
        /**
         * 清单红票时开票类型必须为红票;
         * 2.清单红票时,明细行数量不能大于1
         * 3.清单红票时,规格型号,单位,单价,数量必须为空.
         */
        if (OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderItemInfo.getFphxz())) {
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
                if (itemLength > 1) {
                    resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107147));
                }
                if (!StringUtils.isBlank(orderItemInfo.getGgxh()) || !StringUtils.isBlank(orderItemInfo.getXmdw()) || !StringUtils.isBlank(orderItemInfo.getXmsl()) || !StringUtils.isBlank(orderItemInfo.getXmdj())) {
                    resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107150));
                }
            } else {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107148));
            }
        } else {
            /**
             * 非清单红票时:
             * 1.备用字段如果为1,商品数量不能为空或者为0
             * 2.备用字段如果为1,商品单价不能为空或者为0
             * 3.商品编码必填
             * 4.税率必填
             * 5.收购发票,税率和税额不能为空
             */
            if (ConfigureConstant.STRING_1.equals(orderItemInfo.getByzd1())) {
                if (StringUtils.isBlank(orderItemInfo.getXmsl()) || ConfigureConstant.STRING_0.equals(orderItemInfo.getXmsl())) {
                    resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107151));
                } else {
                    if (new BigDecimal(orderItemInfo.getXmsl()).doubleValue() == ConfigureConstant.DOUBLE_PENNY_ZERO) {
                        resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107151));
                    }
        
                    if (StringUtils.isBlank(orderItemInfo.getXmdj()) || ConfigureConstant.STRING_0.equals(orderItemInfo.getXmdj())) {
                        resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107144));
                    }
                    if (new BigDecimal(orderItemInfo.getXmdj()).doubleValue() == ConfigureConstant.DOUBLE_PENNY_ZERO) {
                        resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107144));
                    }
                }
                
            }
            //商品编码非必传
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSpbm(), sphh);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                resultMapList.add(checkResultMap);
            }
            //税率必传
            if (StringUtils.isBlank(orderItemInfo.getSl())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107146));
            }
            //收购发票
            if (sgfp) {
                if (StringUtils.isNotBlank(orderItemInfo.getSl())) {
                    if (!ConfigureConstant.STRING_0.equals(orderItemInfo.getSl()) && !ConfigureConstant.STRING_000.equals(orderItemInfo.getSl())) {
                        resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107157));
                    }
                }
                if (StringUtils.isNotBlank(orderItemInfo.getSe())) {
                    if (!ConfigureConstant.STRING_0.equals(orderItemInfo.getSe()) && !ConfigureConstant.STRING_000.equals(orderItemInfo.getSe()) && !ConfigureConstant.STRING_000_.equals(orderItemInfo.getSe())) {
                        resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107157));
                    }
                }
                
                //清单标志为收购发票时,发票种类只能是专票或者是电票
                if (!OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(fplb) && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(fplb)) {
                    resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107158));
                }
            }
        }
        
        
        
        /**
         * 订单明细信息-项目名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107057, orderItemInfo.getXmmc(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        /**
         * 订单明细信息-项目单位
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107060, orderItemInfo.getXmdw(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
    
        /**
         * 订单明细信息-扣除额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107061, orderItemInfo.getKce(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //合计税额为不为0时,需要保证税额为小数点后两位
        if (StringUtils.isNotBlank(orderItemInfo.getKce()) && ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getKce()).doubleValue() && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getKce())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107154));
        }
    
        /**
         * 订单明细信息-项目金额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107145, orderItemInfo.getXmje(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        } else {
            //项目金额不能为0或者0.00
            if (ConfigureConstant.STRING_0.equals(orderItemInfo.getXmje()) || ConfigureConstant.STRING_000.equals(orderItemInfo.getXmje())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107081));
            }
            //合计金额为不为0时,需要保证金额为小数点后两位
            if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getXmje()).doubleValue() && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getXmje())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107062));
            }
        }
    
    
        /**
         * 订单明细信息-项目税额
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107134, orderItemInfo.getSe(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //合计税额为不为0时,需要保证税额为小数点后两位
        if (StringUtils.isNotBlank(orderItemInfo.getSe()) && ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSe()).doubleValue() && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(orderItemInfo.getSe())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107133));
        }
        //含税标志为0时,税额不能为空
        if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz()) && StringUtils.isBlank(orderItemInfo.getSe())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107131));
        }
        //清单红字发票时,税额不能为空
        if (OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderItemInfo.getFphxz()) && StringUtils.isBlank(orderItemInfo.getSe())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107137));
        }
        
        /**
         * 订单明细信息-项目数量
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107051, orderItemInfo.getXmsl(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        if (StringUtils.isNotBlank(orderItemInfo.getXmsl()) && !orderItemInfo.getXmsl().matches("-?\\d+?[.]?\\d{0,8}")) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107153));
        }
    
        /**
         * 订单明细信息-项目单价
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107149, orderItemInfo.getXmdj(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) && !orderItemInfo.getXmdj().matches("\\d+?[.]?\\d{0,8}")) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107152));
        }
    
        /**
         * 项目单价乘以项目数量 必须等于项目金额 误差 0.01
         */
        if (StringUtils.isNotBlank(orderItemInfo.getXmdj()) && StringUtils.isNotBlank(orderItemInfo.getXmsl()) && StringUtils.isNotBlank(orderItemInfo.getXmje())) {
    
            String xmje = DecimalCalculateUtil.mul(orderItemInfo.getXmdj(), orderItemInfo.getXmsl());
    
            if (new BigDecimal(xmje).setScale(ConfigureConstant.INT_2, BigDecimal.ROUND_HALF_UP).abs().subtract(new BigDecimal(orderItemInfo.getXmje()).setScale(ConfigureConstant.INT_2, BigDecimal.ROUND_UP).abs()).compareTo(new BigDecimal(ConfigureConstant.DOUBLE_PENNY)) > 0) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.INVOICE_XMMX_JE_ERROR));
            }
    
        }
        
        /**
         * 订单明细信息-自行编码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107063, orderItemInfo.getZxbm(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        
        /**
         * 订单明细信息-税率
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107071, orderItemInfo.getSl(), terminalCode, sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        /**
         * 订单明细信息-含税标志
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107064, orderItemInfo.getHsbz(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //含税标志只能为0和1：0表示都不含税,1表示都含税
        if (StringUtils.isNotBlank(orderItemInfo.getHsbz()) && !OrderInfoEnum.HSBZ_1.getKey().equals(orderItemInfo.getHsbz()) && !OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107065));
        }
        //含税标志为0时,税额不能为空
        if (OrderInfoEnum.HSBZ_0.getKey().equals(orderItemInfo.getHsbz()) && StringUtils.isBlank(orderItemInfo.getSe())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107139));
        }
        
        /**
         * 订单明细信息-商品编码
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, orderItemInfo.getSpbm(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //商品编码必须为19位数字
        if (StringUtils.isNotBlank(orderItemInfo.getSpbm())) {
            boolean spbm = false;
            for (int j = 0; j < orderItemInfo.getSpbm().length(); j++) {
                char c = orderItemInfo.getSpbm().charAt(j);
                if ((c < '0' || c > '9')) {
                    spbm = true;
                }
            }
            if (spbm) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107101));
            }
        }
        
        
        /**
         * 订单明细信息-增值税特殊管理
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107105, orderItemInfo.getZzstsgl(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        
        /**
         * 订单明细信息-优惠政策标识
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107103, orderItemInfo.getYhzcbs(), sphh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            resultMapList.add(checkResultMap);
        }
        //优惠政策标识只能为0或1,0:不使用,1:使用
        if (StringUtils.isNotBlank(orderItemInfo.getYhzcbs()) && !OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs()) && !OrderInfoEnum.YHZCBS_1.getKey().equals(orderItemInfo.getYhzcbs())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107102));
        }
        //优惠政策标识为1时;
        if (ConfigureConstant.STRING_1.equals(orderItemInfo.getYhzcbs())) {
            if (StringUtils.isBlank(orderItemInfo.getZzstsgl())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107104));
            }
            //订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
            if (!StringUtils.isBlank(orderItemInfo.getSl()) &&
                    ConfigureConstant.STRING_0.equals(orderItemInfo.getSl()) &&
                    !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs()) &&
                    !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs()) &&
                    !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs()) &&
                    (StringUtils.isBlank(orderItemInfo.getZzstsgl()))) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107132));
            }
            
        }
        if (OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs())) {
            if (!StringUtils.isBlank(orderItemInfo.getZzstsgl())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107106));
            }
        }
        
        /**
         * 订单明细信息-零税率标识
         */
        if (!StringUtils.isBlank(orderItemInfo.getLslbs()) && !OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs()) && !OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs()) && !OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs()) && !OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLslbs())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107138));
        }
        
        
        /**
         * 税率非空时,逻辑判断
         */
        if (StringUtils.isNotBlank(orderItemInfo.getSl())) {
            /**
             * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理
             * 如果是按5%简易征收需要保证税率为0.05
             * 如果是按3%简易征收需要保证税率为0.03
             * 如果是简易征收需要保证税率为0.03或0.04或0.05
             * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
             */
            if ((!StringUtils.isBlank(orderItemInfo.getZzstsgl())) &&
                    (!ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZzstsgl())) &&
                    (!ConfigureConstant.STRING_MS.equals(orderItemInfo.getZzstsgl())) &&
                    (!ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getZzstsgl()))) {
    
                if (orderItemInfo.getZzstsgl().contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
                    resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173033));
                }
                switch (orderItemInfo.getZzstsgl()) {
                    case ConfigureConstant.STRING_JYZS5:
                        if (!ConfigureConstant.STRING_005.equals(orderItemInfo.getSl())) {
                            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107108));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS3:
                        if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSl())) {
                            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107109));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS:
                        if (!ConfigureConstant.STRING_003.equals(orderItemInfo.getSl()) || !ConfigureConstant.STRING_004.equals(orderItemInfo.getSl()) || !ConfigureConstant.STRING_005.equals(orderItemInfo.getSl())) {
                            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107110));
                        }
                        break;
                    case ConfigureConstant.STRING_JYZS5_1:
                        if (!ConfigureConstant.STRING_0015.equals(orderItemInfo.getSl())) {
                            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107111));
                        }
            
                        break;
                    default:
                        break;
                }
            
            }
        
            //零税率标识不为空,税率必须为0
            if ((!StringUtils.isBlank(orderItemInfo.getLslbs())) && (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(orderItemInfo.getSl()).doubleValue())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107112));
            }
            //零税率标识为空,税率不能为0
            if ((StringUtils.isBlank(orderItemInfo.getLslbs())) && (new BigDecimal(ConfigureConstant.DOUBLE_PENNY_ZERO).doubleValue() == new BigDecimal(orderItemInfo.getSl()).doubleValue())) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107113));
            }
            /**
             * 税率不为空时,如果是专票,并且税率为0,提示错误,专票不可以开具0税率发票
             */
            boolean result = OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fplb) && ConfigureConstant.STRING_000.equals(new BigDecimal(orderItemInfo.getSl()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
    
            if (result) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107118));
            }
    
            /**
             * 如果税率为0,并且是差额票提示不允许开具
             */
            boolean result1 = ConfigureConstant.STRING_000.equals(new BigDecimal(orderItemInfo.getSl()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString()) && StringUtils.isNotEmpty(orderItemInfo.getKce());
    
            if (result1) {
                resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107119));
            }
    
        }
    
        //订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
        boolean result1 = StringUtils.isBlank(orderItemInfo.getZzstsgl()) &&
                (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs()) ||
                        OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs()) ||
                        OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs()));
        if (result1) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107114));
        }
    
        if (OrderInfoEnum.LSLBS_0.getKey().equals(orderItemInfo.getLslbs()) && !ConfigureConstant.STRING_CKLS.equals(orderItemInfo.getZzstsgl())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107114));
        }
        if (OrderInfoEnum.LSLBS_1.getKey().equals(orderItemInfo.getLslbs()) && !ConfigureConstant.STRING_MS.equals(orderItemInfo.getZzstsgl())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107114));
        }
        if (OrderInfoEnum.LSLBS_2.getKey().equals(orderItemInfo.getLslbs()) && !ConfigureConstant.STRING_BZS.equals(orderItemInfo.getZzstsgl())) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107114));
        }
        boolean result2 = OrderInfoEnum.LSLBS_3.getKey().equals(orderItemInfo.getLslbs()) && (!StringUtils.isBlank(orderItemInfo.getZzstsgl()) || !(OrderInfoEnum.YHZCBS_0.getKey().equals(orderItemInfo.getYhzcbs())));
        if (result2) {
            resultMapList.add(generateErrorMap(null, errorMsg, OrderInfoContentEnum.CHECK_ISS7PRI_107140));
        }
    
        
        return resultMapList;
    }
    
    /**
     * 校验税号规则
     *
     * @param nsrsbh
     * @return
     */
    @Override
    public Map<String, String> checkNsrsbhParam(OrderInfoContentEnum contentEnum, OrderInfoContentEnum contentEnum1, OrderInfoContentEnum contentEnum2, String nsrsbh) {
        
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        checkResultMap = CheckParamUtil.checkParam(contentEnum, nsrsbh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (StringUtils.isNotEmpty(nsrsbh)) {
            //是否包含空格
            if (nsrsbh.contains(" ")) {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.CHECK_ISS7PRI_107164.getKey());
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, OrderInfoContentEnum.CHECK_ISS7PRI_107164.getMessage());
                return checkResultMap;
            }
            //判断税号长度合法性问题,长度必须15,17,18,20位
            if (ConfigureConstant.INT_15 != ValidateUtil.getStrBytesLength(nsrsbh) && ConfigureConstant.INT_17 != ValidateUtil.getStrBytesLength(nsrsbh) && ConfigureConstant.INT_18 != ValidateUtil.getStrBytesLength(nsrsbh) && ConfigureConstant.INT_20 != ValidateUtil.getStrBytesLength(nsrsbh)) {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, contentEnum1.getKey());
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, contentEnum1.getMessage());
                return checkResultMap;
            }
            //纳税人识别号需要全部大写
            if (!ValidateUtil.isAcronym(nsrsbh)) {
                checkResultMap.put(OrderManagementConstant.ERRORCODE, contentEnum2.getKey());
                checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, contentEnum2.getMessage());
                return checkResultMap;
            }
        }
        
        return checkResultMap;
    }

    @Override
    public Map<String,String> checkCommonDdffzxx(DDFPZXX ddfpzxx){
        Map<String,String> checkResultMap = new HashMap<>(10);

        //1.入参数据非空校验
        DDFPXX ddfpxx = ddfpzxx.getDDFPXX();
        if(Objects.isNull(ddfpxx)){
            return generateErrorMap(null, null, OrderInfoContentEnum.INVOICE_HEAD_INFO_IMPORT_ERROR_NULL);
        }
        List<DDMXXX> ddmxxxList = ddfpzxx.getDDMXXX();
        if(CollectionUtils.isEmpty(ddmxxxList)){
            return generateErrorMap(null, null, OrderInfoContentEnum.INVOICE_MX_INFO_IMPORT_ERROR_NULL);
        }

        //2.校验订单发票信息
        checkResultMap = checkCommonDdfpxx(ddfpxx,checkResultMap);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //3.校验订单发票明细信息
        checkResultMap = checkCommonDdmxxx(ddfpxx,ddmxxxList,checkResultMap);
        return checkResultMap;
    }

    @Override
    public Map<String, String> checkGhfInfo(OrderInfo orderInfo, String key) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());

        /**
         * 订单主体-购买方纳税人名称
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107288, orderInfo.getGhfMc());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方纳税人识别号
         */
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107031, OrderInfoContentEnum.CHECK_ISS7PRI_107025, OrderInfoContentEnum.CHECK_ISS7PRI_107023, orderInfo.getGhfNsrsbh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方地址
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107289, orderInfo.getGhfDz());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方电话
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107290, orderInfo.getGhfDh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方银行
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107291, orderInfo.getGhfYh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方帐号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107292, orderInfo.getGhfZh());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购货方地址和电话总长度
         * TODO 由于企业区分不开银行帐号,所以校验支持地址和电话总长度100,默认应该是85
         */
        String ghfdzdh = (StringUtils.isBlank(orderInfo.getGhfDz()) ? "" : orderInfo.getGhfDz()) + (StringUtils.isBlank(orderInfo.getGhfDh()) ? "" : orderInfo.getGhfDh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107269, ghfdzdh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购货方银行和帐号总长度
         * TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100,默认应该是85
         */
        String ghfyhzh = (StringUtils.isBlank(orderInfo.getGhfYh()) ? "" : orderInfo.getGhfYh()) + (StringUtils.isBlank(orderInfo.getGhfZh()) ? "" : orderInfo.getGhfZh());
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107270, ghfyhzh);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //如果是专票并且购方id为空,购货方地址,购货方银行为必填,购货方名称,购货方税号为必填,同时校验税号合法性
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
            /**
             * 专票-购买方地址
             * TODO 由于企业区分不开地址电话,所以校验支持地址电话总长度100,默认应该是80
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107265, orderInfo.getGhfDz());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 专票-购买方银行
             * TODO 由于企业区分不开银行帐号,所以校验支持银行帐号总长度100
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107266, orderInfo.getGhfYh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 订单主体-购买方名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107032, orderInfo.getGhfMc());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 订单主体-购买方纳税人识别号
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107027, orderInfo.getGhfNsrsbh());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 订单主体-购买方企业类型
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107035, orderInfo.getGhfQylx());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 订单主体-购买方名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107032, orderInfo.getGhfMc());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /**
             * 订单主体-开票人
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107044, orderInfo.getKpr());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        if (StringUtils.isNotBlank(orderInfo.getGhfQylx())) {

            //订单主体-企业类型合法性(企业类型只能为:01企业，02机关事业单位，03个人，04其他)
            if (!OrderInfoEnum.GHF_QYLX_01.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_02.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_03.getKey().equals(orderInfo.getGhfQylx())
                    && !OrderInfoEnum.GHF_QYLX_04.getKey().equals(orderInfo.getGhfQylx())) {
                return generateErrorMap(OrderInfoContentEnum.CHECK_ISS7PRI_107036);
            }

            //订单主体-企业类型为01(企业),02(机关事业单位)时需要保证购方税号非空并且正确
            if (OrderInfoEnum.GHF_QYLX_01.getKey().equals(orderInfo.getGhfQylx())) {
                checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107027, OrderInfoContentEnum.CHECK_ISS7PRI_107025, OrderInfoContentEnum.CHECK_ISS7PRI_107023, orderInfo.getGhfNsrsbh());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
            }
        }


        /**
         * 订单主体-购买方省份
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107037, orderInfo.getGhfSf());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方手机
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107040, orderInfo.getGhfSj());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        /**
         * 订单主体-购买方邮箱
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107042, orderInfo.getGhfEmail());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        return checkResultMap;
    }

    @Override
    public DDFPZXX setDatabaseValueToddfpzxx(DDFPZXX ddfpzxx) {
        DDFPXX ddfpxx = ddfpzxx.getDDFPXX();
        /*
         * 购买方编码
         */
        if(StringUtils.isNotBlank(ddfpxx.getGMFBM())){
            BuyerEntity buyerEntity = buyerService.queryBuyerInfoByxhfNsrsbhAndBuyerCode(ddfpxx.getXHFSBH(), ddfpxx.getGMFBM());
            //购方税号
            ddfpxx.setGMFSBH(buyerEntity.getTaxpayerCode());
            //购方名称
            ddfpxx.setGMFMC(buyerEntity.getPurchaseName());
            //购方地址
            ddfpxx.setGMFDZ(buyerEntity.getAddress());
            //购方电话
            ddfpxx.setGMFDH(buyerEntity.getPhone());
            //购方银行
            ddfpxx.setGMFYH(buyerEntity.getBankOfDeposit());
            //购方帐号
            ddfpxx.setGMFZH(buyerEntity.getBankNumber());
        }
        return ddfpzxx;
    }

    private Map<String,String> checkCommonDdfpxx(DDFPXX ddfpxx,Map<String,String> checkResultMap){
        //订单请求流水号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_DDQQLSH_ERROR_144004,
                ddfpxx.getDDQQLSH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //纳税人识别号
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144005,
                OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144006, OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144007,
                ddfpxx.getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //纳税人名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRMC_ERROR_144008,
                ddfpxx.getNSRMC());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //红蓝票标识(只能为0和1：0蓝字发票；1红字发票)
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_KPLX_ERROR_144009,
                ddfpxx.getKPLX());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(ddfpxx.getKPLX())
                && !OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(ddfpxx.getKPLX())) {
            return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_KPLX_ERROR_144010);
        }

        //编码表版本号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_BMBBBH_ERROR_144011,
                ddfpxx.getBMBBBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方纳税人识别号
        checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144005,
                OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144006, OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144007,
                ddfpxx.getXHFSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //销货方纳税人识别号必须和纳税人识别号一致
        if(!StringUtils.equals(ddfpxx.getXHFSBH(),ddfpxx.getNSRSBH())){
            return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_NSRSBH_ERROR_144095);
        }

        //销货方名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_XHFMC_ERROR_144012,
                ddfpxx.getXHFMC());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方地址
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_XHFDZ_ERROR_144013,
                ddfpxx.getXHFDZ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方电话
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_XHFDH_ERROR_144014,
                ddfpxx.getXHFDH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方银行名称
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_XHFYH_ERROR_144015,
                ddfpxx.getXHFYH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //销货方银行账号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_XHFZH_ERROR_144016,
                ddfpxx.getXHFZH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //购买方类型
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFLX_ERROR_144017,
                ddfpxx.getGMFLX());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!OrderInfoEnum.GHF_QYLX_01.getKey().equals(ddfpxx.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_02.getKey().equals(ddfpxx.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_03.getKey().equals(ddfpxx.getGMFLX())
                && !OrderInfoEnum.GHF_QYLX_04.getKey().equals(ddfpxx.getGMFLX())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFLX_ERROR_144018);
        }

        //购买方编码 (购买方编码,非必填,如果填写时,(购方税号,购方名称,购方地址,购方电话,购方银行,购方帐号)这些字段非必填)
        if(StringUtils.isNotBlank(ddfpxx.getGMFBM())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFBM_ERROR_144019,
                    ddfpxx.getGMFBM());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            BuyerEntity buyerEntity = buyerService.queryBuyerInfoByxhfNsrsbhAndBuyerCode(ddfpxx.getXHFSBH(), ddfpxx.getGMFBM());
            if(Objects.isNull(buyerEntity)){
                //如果按购买方识别号在数据库中未查询到信息则返回提示信息
                return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFMC_ERROR_144136);
            }
        }else{
            //购买方纳税人识别号 (当”购买方类型”为企业时(即01),购方税号需要必填)
            if(StringUtils.equals(OrderInfoEnum.GHF_QYLX_01.getKey(),ddfpxx.getGMFLX())){
                checkResultMap = checkNsrsbhParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFSBH_ERROR_144020,
                        OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFSBH_ERROR_144021, OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFSBH_ERROR_144022,
                        ddfpxx.getGMFSBH());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
            }
            //购买方名称
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFMC_ERROR_144023,
                    ddfpxx.getGMFMC());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            if (! ddfpxx.getGMFMC().matches("^[A-Za-z0-9\\u4e00-\\u9fa5]+$")) {
                return generateErrorMap("","", OrderInfoContentEnum.INVOICE_HEAD_INFO_GMFMC_ERROR_144117);
            }
        }

        //开票人
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_KPR_ERROR_144024,
                ddfpxx.getKPR());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //原发票代码:当开票类型字段为1(红字发票)时,该字段必填
        //原发票号码:当开票类型字段为1(红字发票)时,该字段必填
        if(StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey(),ddfpxx.getKPLX())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_YFPDM_ERROR_144025,
                    ddfpxx.getYFPDM());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_YFPHM_ERROR_144026,
                    ddfpxx.getYFPHM());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //清单标志
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_QDBZ_ERROR_144027, ddfpxx.getQDBZ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //清单标志0-普通发票;1-普通发票(清单);2-收购发票;3-收购发票(清单);4-成品油发票
        if (!OrderInfoEnum.QDBZ_CODE_0.getKey().equals(ddfpxx.getQDBZ())
                && !OrderInfoEnum.QDBZ_CODE_1.getKey().equals(ddfpxx.getQDBZ())
                && !OrderInfoEnum.QDBZ_CODE_2.getKey().equals(ddfpxx.getQDBZ())
                && !OrderInfoEnum.QDBZ_CODE_3.getKey().equals(ddfpxx.getQDBZ())
                && !OrderInfoEnum.QDBZ_CODE_4.getKey().equals(ddfpxx.getQDBZ())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_QDBZ_ERROR_144028);
        }

        //清单标志为1(普通发票清单),3(收购发票清单)时,清单项目名称为必填
        if(OrderInfoEnum.QDBZ_CODE_1.getKey().equals(ddfpxx.getQDBZ()) ||
                 OrderInfoEnum.QDBZ_CODE_3.getKey().equals(ddfpxx.getQDBZ())) {
            if(StringUtils.isBlank(ddfpxx.getQDXMMC())) {
                return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_QDXMMC_ERROR_144029);
            }
        }

        //价税合计(价税合计金额不能为0或者0.00、开票类型为0(蓝票)时,金额必须大于0、开票类型为1(红票)时,金额必须小于0)
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_JSHJ_ERROR_144030,
                ddfpxx.getJSHJ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (ConfigureConstant.STRING_0.equals(ddfpxx.getJSHJ())
                || ConfigureConstant.STRING_000.equals(ddfpxx.getJSHJ())
                || ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddfpxx.getJSHJ())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_JSHJ_ERROR_144031);
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(ddfpxx.getKPLX())
                && ConfigureConstant.DOUBLE_PENNY_ZERO >= new BigDecimal(ddfpxx.getJSHJ()).doubleValue()) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_JSHJ_ERROR_144032);
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(ddfpxx.getKPLX())
                && ConfigureConstant.DOUBLE_PENNY_ZERO <= new BigDecimal(ddfpxx.getJSHJ()).doubleValue()) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_JSHJ_ERROR_144033);
        }
    
        //合计金额(不含税)(合计金额为不为0时,需要保证金额为小数点后两位)
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_HJJE_ERROR_144034,
                ddfpxx.getHJJE());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddfpxx.getHJJE()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddfpxx.getHJJE())) {
            return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_HJJE_ERROR_144035);
        }

        //合计税额(合计金额为不为0时,需要保证金额为小数点后两位)
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_HJSE_ERROR_144036,
                ddfpxx.getHJSE());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddfpxx.getHJSE()).doubleValue()
                && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddfpxx.getHJSE())) {
            return generateErrorMap("","",OrderInfoContentEnum.INVOICE_HEAD_INFO_HJSE_ERROR_144037);
        }

        /**
         * 金额关系合法性校验
         */
        if (!StringUtils.isBlank(ddfpxx.getJSHJ()) && !StringUtils.isBlank(ddfpxx.getHJSE())
                && !StringUtils.isBlank(ddfpxx.getHJJE())) {

            double differ = MathUtil.sub(ddfpxx.getJSHJ(), String.valueOf(MathUtil.add(ddfpxx.getHJJE(), ddfpxx.getHJSE())));
            //如果误差值等于含税金额,说明是含税金额不作校验,如果是尾插不等于0,校验返回
            if (DecimalCalculateUtil.decimalFormatToString(ddfpxx.getJSHJ(), ConfigureConstant.INT_2)
                    .equals(DecimalCalculateUtil.decimalFormatToString(String.valueOf(differ), ConfigureConstant.INT_2))) {

            } else if (ConfigureConstant.DOUBLE_PENNY_ZERO != differ) {
                checkResultMap = generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_CALCULATE_ERROR_144096);
                return checkResultMap;
            }
        }

        //发票类型代码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_FPLXDM_ERROR_144038, ddfpxx.getFPLXDM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //订单请求发票类型合法性
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddfpxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddfpxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddfpxx.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_028.getKey().equals(ddfpxx.getFPLXDM())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPLXDM_ERROR_144039);
        }

        //备注:冲红时必填
        //增值税发票红字发票（非专票）开具时，备注要求:
        //开具负数发票，必须在备注中注明“对应正数发票代码:XXXXXXXXX号码:YYYYYYYY”字样，其中“X”为发票代码，“Y”为发票号码。如未注明，系统自动追加。
        //增值税发票红字发票（专票）开具时，备注要求:
        //开具负数发票，必须在备注中注明“开具红字增值税专用发票信息表编号ZZZZZZZZZZZZZZZZ”字样，其中“Z”为开具红字增值税专用发票所需要的长度为16位信息表编号
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(ddfpxx.getKPLX())) {
            if (StringUtils.isBlank(ddfpxx.getBZ())) {
                //增值税发票红字发票（专票）
                if(OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddfpxx.getFPLXDM())){
                    return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_BZ_ERROR_144040);
                }
            }else{
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_BZ_ERROR_144135, ddfpxx.getBZ());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }

                if (OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddfpxx.getFPLXDM())) {
                    if (!ddfpxx.getBZ().contains(ConfigureConstant.STRING_HZBZ)) {
                        return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_BZ_ERROR_144041);
                    }
                    int index = ddfpxx.getBZ().indexOf(ConfigureConstant.STRING_HZBZ);
                    if (ddfpxx.getBZ().length() < ConfigureConstant.INT_32) {
                        return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_BZ_ERROR_144041);
                    }
                    String xxbbh = ddfpxx.getBZ().substring(index + 16, index + 32);
                    if (!ValidateUtil.isNumeric(xxbbh)) {
                        return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_BZ_ERROR_144041);
                    }
                }
            }
        }else{
            /**
             * 订单主体-备注  为蓝票时非必填，限制长度200
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_BZ_ERROR_144135, ddfpxx.getBZ());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //冲红原因(当开票类型字段为1(红字发票)时,描述冲红具体原因)
        if(StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey(),ddfpxx.getKPLX())){
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_CHYY_ERROR_144042, ddfpxx.getCHYY());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        }

        //特殊冲红标志 当开票类型字段为1(红字发票)时,该字段必填:0正常冲红(电子发票)
        //1特殊冲红(冲红纸质等)
        //纸质发票:0:专票 2:普票41:卷票
        //电子发票: 51:电子发票
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(ddfpxx.getKPLX())) {
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_TSCHBZ_ERROR_144043,
                    ddfpxx.getTSCHBZ());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
        } else {
            if (StringUtils.isNotBlank(ddfpxx.getYFPDM()) || StringUtils.isNotBlank(ddfpxx.getYFPHM())) {
                return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_TSCHBZ_ERROR_144044);
            }
        }

        //机器编号
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_JQBH_ERROR_144045,
                ddfpxx.getJQBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //发票代码
        if (StringUtils.isBlank(ddfpxx.getFPDM())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPDM_ERROR_144046);
        } else if (ddfpxx.getFPDM().length() != ConfigureConstant.INT_10 && ddfpxx.getFPDM().length() != ConfigureConstant.INT_12) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPDM_ERROR_144047);
        } else if (!MathUtil.isNumeric(ddfpxx.getFPDM())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPDM_ERROR_144048);
        }

        //发票号码
        if (StringUtils.isBlank(ddfpxx.getFPHM())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPHM_ERROR_144049);
        } else if (ddfpxx.getFPHM().length() != ConfigureConstant.INT_8) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPHM_ERROR_144050);
        } else if (!MathUtil.isNumeric(ddfpxx.getFPHM())) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_FPHM_ERROR_144051);
        }

        //开票日期
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_KPRQ_ERROR_144052,ddfpxx.getKPRQ());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!DateUtilsLocal.DEFAULT_SDF.equals(DateUtilsLocal.checkDate(ddfpxx.getKPRQ()))) {
            return generateErrorMap("", "", OrderInfoContentEnum.INVOICE_HEAD_INFO_KPRQ_ERROR_144053);
        }

        //校验码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_JYM_ERROR_144054,ddfpxx.getJYM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //防伪码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_FWM_ERROR_144055,ddfpxx.getFWM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }

        //二维码
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_HEAD_INFO_EWM_ERROR_144056,ddfpxx.getEWM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        return checkResultMap;
    }

    private Map<String,String> checkCommonDdmxxx(DDFPXX ddfpxx,List<DDMXXX> ddmxxxList,Map<String,String> checkResultMap){
        //电子发票发票明细行数不超过2000行
        if (ConfigureConstant.MAX_ITEM_LENGTH <= ddmxxxList.size()) {
            return generateErrorMap("","",OrderInfoContentEnum.INVOICE_MX_INFO_IMPORT_ERROR_LENGTH_144097);
        }
        for (int i = 0; i < ddmxxxList.size(); i++) {
            DDMXXX ddmxxx = ddmxxxList.get(i);
            int num = i;
            String errorMsg = "第" + (num + 1) + "行";
            /*
             * 发票行性质
             */
            if (StringUtils.isBlank(ddmxxx.getFPHXZ())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_FPHXZ_NULL_144057);
            }
            if (!OrderInfoEnum.FPHXZ_CODE_0.getKey().equals(ddmxxx.getFPHXZ())
                    && !OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(ddmxxx.getFPHXZ())
                    && !OrderInfoEnum.FPHXZ_CODE_2.getKey().equals(ddmxxx.getFPHXZ())
                    && !OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(ddmxxx.getFPHXZ())) {
                return generateErrorMap("",errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_FPHXZ_ERROR_144058);
            }

            /*
             * 商品税收分类编码
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_SPBM_ERROR_144059,
                    ddmxxx.getSPBM(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            if (StringUtils.isNotEmpty(ddmxxx.getSPBM())) {
                boolean spbm = false;
                for (int j = 0; j < ddmxxx.getSPBM().length(); j++) {
                    char c = ddmxxx.getSPBM().charAt(j);
                    if ((c < '0' || c > '9')) {
                        spbm = true;
                    }
                }
                if (spbm) {
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_SPBM_ERROR_144118);
                }
            }
            //商品编码不为空,需要调用底层商品编码获取简码接口获取数据
            TaxClassCodeEntity qtc = apiTaxClassCodeService.queryTaxClassCodeEntity(ddmxxx.getSPBM());
            if(Objects.isNull(qtc)){
                return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_SPBM_ERROR_144116);
            }

            /*
             * 项目名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_XMMC_ERROR_144060,
                    ddmxxx.getXMMC(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /*
             * 规格型号(红字清单和折扣行必须为空)
             */
            if(StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_6.getKey(),ddmxxx.getFPHXZ())
              || StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
                if(StringUtils.isNotEmpty(ddmxxx.getGGXH())){
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_GGXH_ERROR_144061);
                }
            }

            /*
             * 单位
             */
            //清单红字填充为空
            if(StringUtils.equals(ddmxxx.getFPHXZ(),OrderInfoEnum.FPHXZ_CODE_6.getKey())
                    || StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
                if(StringUtils.isNotEmpty(ddmxxx.getDW())){
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_DW_ERROR_144130);
                }
            }else{
                //如果清单标识为4，则单位必填
                if(StringUtils.equals(OrderInfoEnum.QDBZ_CODE_4.getKey(),ddfpxx.getQDBZ())){
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_DW_ERROR_144062,
                            ddmxxx.getDW(),num);
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        return checkResultMap;
                    }
                    //（成品油项目必填且必须为"升"或"吨"，清单红字填充为空）
                    if (!ConfigureConstant.STRING_DUN.equals(ddmxxx.getDW()) && !ConfigureConstant.STRING_SHENG.equals(ddmxxx.getDW())) {
                        return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_DW_ERROR_144063);
                    }
                }
            }

            /*
             * 商品数量
             */
            //清单红字填充为空
            if(StringUtils.equals(ddmxxx.getFPHXZ(),OrderInfoEnum.FPHXZ_CODE_6.getKey())
                    || StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
                if(StringUtils.isNotEmpty(ddmxxx.getSPSL())){
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_SPSL_ERROR_144131);
                }
            }else{
                //为非折扣行，数量不允许为空或0
                if(!StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_SPSL_ERROR_144064,
                            ddmxxx.getSPSL(),num);
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        return checkResultMap;
                    }
                    if(StringUtils.equals(ConfigureConstant.STRING_0,ddmxxx.getSPSL())){
                        generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_SPSL_ERROR_144132);
                    }
                }
                if (StringUtils.isNotBlank(ddmxxx.getSPSL()) && !ddmxxx.getSPSL().matches("-?\\d+?[.]?\\d{0,8}")) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SPSL_ERROR_144065);
                }
            }

            /*
             * 单价
             */
            if(StringUtils.equals(ddmxxx.getFPHXZ(),OrderInfoEnum.FPHXZ_CODE_6.getKey())
            || StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
               if(StringUtils.isNotEmpty(ddmxxx.getDJ())){
                   return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_DJ_ERROR_144133);
               }
            }else{
                checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_DJ_ERROR_144066,
                        ddmxxx.getDJ(),num);
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
                if(StringUtils.equals(ConfigureConstant.STRING_0,ddmxxx.getDJ())|| ConfigureConstant.STRING_000.equals(ddmxxx.getJE())){
                    generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_DJ_ERROR_144134);
                }
                if (StringUtils.isNotBlank(ddmxxx.getDJ()) && !ddmxxx.getDJ().matches("\\d+?[.]?\\d{0,8}")) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_DJ_ERROR_144067);
                }
            }

            /*
             * 增值税特殊管理
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_ZZSTSGL_ERROR_144068,
                    ddmxxx.getZZSTSGL(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }

            /*
             * 优惠政策标识
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_YHZCBS_ERROR_144069,
                    ddmxxx.getYHZCBS(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //优惠政策标识只能为0或1,0:不使用,1:使用
            if (!OrderInfoEnum.YHZCBS_0.getKey().equals(ddmxxx.getYHZCBS()) &&
                    !OrderInfoEnum.YHZCBS_1.getKey().equals(ddmxxx.getYHZCBS())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_YHZCBS_ERROR_144070);
            }
            //优惠政策标识为1时;
            if (ConfigureConstant.STRING_1.equals(ddmxxx.getYHZCBS())) {
                if (StringUtils.isBlank(ddmxxx.getZZSTSGL())) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_YHZCBS_ERROR_144071);
                }
                //订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
                if (!StringUtils.isBlank(ddmxxx.getSL()) && ConfigureConstant.STRING_0.equals(ddmxxx.getSL())
                        && !OrderInfoEnum.LSLBS_0.getKey().equals(ddmxxx.getLSLBS())
                        && !OrderInfoEnum.LSLBS_1.getKey().equals(ddmxxx.getLSLBS())
                        && !OrderInfoEnum.LSLBS_2.getKey().equals(ddmxxx.getLSLBS())
                        && (StringUtils.isBlank(ddmxxx.getZZSTSGL()))){
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_YHZCBS_ERROR_144072);
                }
            }
            if (OrderInfoEnum.YHZCBS_0.getKey().equals(ddmxxx.getYHZCBS())) {
                if (!StringUtils.isBlank(ddmxxx.getZZSTSGL())) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_YHZCBS_ERROR_144073);
                }
            }

            /*
             * 订单明细信息-零税率标识
             */
            if (!StringUtils.isBlank(ddmxxx.getLSLBS()) && !OrderInfoEnum.LSLBS_0.getKey().equals(ddmxxx.getLSLBS())
                    && !OrderInfoEnum.LSLBS_1.getKey().equals(ddmxxx.getLSLBS())
                    && !OrderInfoEnum.LSLBS_2.getKey().equals(ddmxxx.getLSLBS())
                    && !OrderInfoEnum.LSLBS_3.getKey().equals(ddmxxx.getLSLBS())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_LSLBS_ERROR_144074);
            }

            /*
             * 项目金额
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_JE_ERROR_144075,
                    ddmxxx.getJE(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //项目金额不能为0或者0.00
            if (ConfigureConstant.STRING_0.equals(ddmxxx.getJE()) || ConfigureConstant.STRING_000.equals(ddmxxx.getJE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_JE_ERROR_144076);
            }
            //合计金额为不为0时,需要保证金额为小数点后两位
            if (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddmxxx.getJE()).doubleValue()
                    && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddmxxx.getJE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_JE_ERROR_144077);
            }
    
    
            /*
             * 含税标志
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_HSBZ_ERROR_144079,
                    ddmxxx.getHSBZ(),num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //含税标志只能为0和1：0表示都不含税,1表示都含税
            //todo 历史数据导入时，暂定只能导入不含税的数据
            if (!OrderInfoEnum.HSBZ_0.getKey().equals(ddmxxx.getHSBZ())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_HSBZ_ERROR_144080);
            }
            //含税标志为0时,税额不能为空
            if (OrderInfoEnum.HSBZ_0.getKey().equals(ddmxxx.getHSBZ()) && StringUtils.isBlank(ddmxxx.getSE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_HSBZ_ERROR_144081);
            }

            /*
             * 税率
             */
            if (StringUtils.isBlank(ddmxxx.getSL())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144082);
            }else{
                /*
                 * 增值税特殊管理不为空,不为不征税,不为免税,不为出口零税逻辑处理
                 * 如果是按5%简易征收需要保证税率为0.05
                 * 如果是按3%简易征收需要保证税率为0.03
                 * 如果是简易征收需要保证税率为0.03或0.04或0.05
                 * 如果是按5%简易征收减按1.5%计征需要保证税率为0.015
                 */
                if ((!StringUtils.isBlank(ddmxxx.getZZSTSGL())) &&
                        (!ConfigureConstant.STRING_BZS.equals(ddmxxx.getZZSTSGL())) &&
                        (!ConfigureConstant.STRING_MS.equals(ddmxxx.getZZSTSGL())) &&
                        (!ConfigureConstant.STRING_CKLS.equals(ddmxxx.getZZSTSGL()))) {
                    if (ddmxxx.getZZSTSGL().contains(ConfigureConstant.STRING_ERROR_PERCENT)) {
                        return generateErrorMap("", "",
                                OrderInfoContentEnum.COMMODITY_MESSAGE_SYNC_ERROR_173033);
                    }
                    switch (ddmxxx.getZZSTSGL()) {
                        case ConfigureConstant.STRING_JYZS5:
                            if (!ConfigureConstant.STRING_005.equals(ddmxxx.getSL())) {
                                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144083);
                            }
                            break;
                        case ConfigureConstant.STRING_JYZS3:
                            if (!ConfigureConstant.STRING_003.equals(ddmxxx.getSL())) {
                                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144084);
                            }
                            break;
                        case ConfigureConstant.STRING_JYZS:
                            if (!ConfigureConstant.STRING_003.equals(ddmxxx.getSL()) ||
                                    !ConfigureConstant.STRING_004.equals(ddmxxx.getSL()) ||
                                    !ConfigureConstant.STRING_005.equals(ddmxxx.getSL())) {
                                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144085);
                            }
                            break;
                        case ConfigureConstant.STRING_JYZS5_1:
                            if (!ConfigureConstant.STRING_0015.equals(ddmxxx.getSL())) {
                                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144086);
                            }
                            break;
                        default:
                            break;
                    }
                }
                //零税率标识不为空,税率必须为0
                if ((!StringUtils.isBlank(ddmxxx.getLSLBS())) &&
                        (ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddmxxx.getSL()).doubleValue())) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144087);
                }
                //零税率标识为空,税率不能为0
                if ((StringUtils.isBlank(ddmxxx.getLSLBS())) &&
                        (new BigDecimal(ConfigureConstant.DOUBLE_PENNY_ZERO).doubleValue() == new BigDecimal(ddmxxx.getSL()).doubleValue())) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144088);
                }
            }
            //订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';
            boolean result3 = StringUtils.isBlank(ddmxxx.getZZSTSGL()) &&
                    (OrderInfoEnum.LSLBS_0.getKey().equals(ddmxxx.getLSLBS()) ||
                            OrderInfoEnum.LSLBS_1.getKey().equals(ddmxxx.getLSLBS()) ||
                            OrderInfoEnum.LSLBS_2.getKey().equals(ddmxxx.getLSLBS()));
            if (result3) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144089);
            }
            if (OrderInfoEnum.LSLBS_0.getKey().equals(ddmxxx.getLSLBS()) &&
                    !ConfigureConstant.STRING_CKLS.equals(ddmxxx.getZZSTSGL())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144089);
            }
            if (OrderInfoEnum.LSLBS_1.getKey().equals(ddmxxx.getLSLBS()) &&
                    !ConfigureConstant.STRING_MS.equals(ddmxxx.getZZSTSGL())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144089);
            }
            if (OrderInfoEnum.LSLBS_2.getKey().equals(ddmxxx.getLSLBS()) &&
                    !ConfigureConstant.STRING_BZS.equals(ddmxxx.getZZSTSGL())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144089);
            }
            boolean result4 = OrderInfoEnum.LSLBS_3.getKey().equals(ddmxxx.getLSLBS()) && (!StringUtils.isBlank(ddmxxx.getZZSTSGL())
                    || !(OrderInfoEnum.YHZCBS_0.getKey().equals(ddmxxx.getYHZCBS())));
            if (result4) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SL_ERROR_144090);
            }
    
            /*
             * 项目税额
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.INVOICE_MX_INFO_SE_ERROR_144091,
                    ddmxxx.getSE(), num);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //合计税额为不为0时,需要保证税额为小数点后两位
            if (!StringUtils.isBlank(ddmxxx.getSE()) &&
                    ConfigureConstant.DOUBLE_PENNY_ZERO != new BigDecimal(ddmxxx.getSE()).doubleValue()
                    && ConfigureConstant.INT_2 != ValidateUtil.checkNumberic(ddmxxx.getSE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SE_ERROR_144092);
            }
            //含税标志为0时,税额不能为空
            if (OrderInfoEnum.HSBZ_0.getKey().equals(ddmxxx.getHSBZ()) && StringUtils.isBlank(ddmxxx.getSE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SE_ERROR_144093);
            }
            //清单红字发票时,税额不能为空
            if (OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(ddmxxx.getFPHXZ()) && StringUtils.isBlank(ddmxxx.getSE())) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_SE_ERROR_144094);
            }

            /*
             * 扣除额
             */
            if(StringUtils.isNotBlank(ddmxxx.getKCE())){
                //校验扣除额是否位合法数字
                if(!ddmxxx.getKCE().matches("-?[0-9]+.*[0-9]*")){
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144121);
                }
                //判断明细行数中是否有折扣行
                List<DDMXXX> ddmxxxzkhList = ddmxxxList.stream().filter(ddmxxx1 -> StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),
                        ddmxxx1.getFPHXZ())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(ddmxxxzkhList)){
                    //如果明细中没有折扣行，那么明细行只能是一行
                    if(CollectionUtils.size(ddmxxxList) != 1){
                        return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144122);
                    }
                }else{
                    //如果明细中有折扣行，那么明细行只能是两行
                    if(CollectionUtils.size(ddmxxxList) != 2){
                        return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144123);
                    }
                }
                if(StringUtils.isBlank(ddfpxx.getBZ())){
                    return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144124);
                }else{
                    if (!ddfpxx.getBZ().contains(ConfigureConstant.STRING_CEZS_)) {
                        return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144128);
                    }
                }
                BigDecimal kce = new BigDecimal(ddmxxx.getKCE());
                //蓝票
                if(StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey(),ddfpxx.getKPLX())){
                    if (kce.compareTo(BigDecimal.ZERO) == -1) {
                        return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144125);
                    }
                    if(CollectionUtils.size(ddmxxxList) == 2){
                        if(StringUtils.equals(OrderInfoEnum.FPHXZ_CODE_1.getKey(),ddmxxx.getFPHXZ())){
                            return generateErrorMap("",errorMsg,OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144126);
                        }
                    }
                }
                //红票
                if (StringUtils.equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey(), ddfpxx.getKPLX())) {
                    if (kce.compareTo(BigDecimal.ZERO) == 1) {
                        return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144127);
                    }
                }
                //扣除额不能大于项目金额
                if (new BigDecimal(ddmxxx.getJE()).abs().subtract(kce.abs()).compareTo(BigDecimal.ZERO) == -1) {
                    return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144129);
                }
    
            } else if ((i == 0) && StringUtils.isNotBlank(ddfpxx.getBZ()) && ddfpxx.getBZ().contains(ConfigureConstant.STRING_CEZS_)) {
                return generateErrorMap("", errorMsg, OrderInfoContentEnum.INVOICE_MX_INFO_KCE_ERROR_144120);
            }
        }

        /*
         * 明细行和计算相关数据校验
         */
        checkResultMap = checkDetailsDiscount(ddfpxx,ddmxxxList);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        return checkResultMap;
    }

    private Map<String, String> checkDetailsDiscount(DDFPXX ddfpxx,List<DDMXXX> ddmxxxList) {
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
        String kplx = ddfpxx.getKPLX();
        double mxse_total_full = 0;
        double xmje_total = 0;
        //更新是否折扣行标志（连续折扣行标记）
        boolean upIsZkh = false;
        boolean isCpySpbm = false;
        if (apiTaxClassCodeService.queryOilBySpbm(ddmxxxList.get(0).getSPBM()) != null) {
            isCpySpbm = true;
        }
        for (int i = 0; i < ddmxxxList.size(); i++) {
            DDMXXX ddmxxx = ddmxxxList.get(i);
            String errorMsgString = "第" + (i+1) + "行";

            /**
             * 金额（不含税） * 税率 = 税额
             * 计算出的税额 与 订单传递的税额 比较，误差不能超过6分（即小于或等于6分）
             */
            String sl = StringUtil.formatSl(ddmxxx.getSL());
            double jsse = Double.parseDouble(ddmxxx.getSE());
            if (StringUtils.isNotBlank(sl)) {
                /**
                 * 支持扣除额进行校验,
                 * 如果是扣除额,需要用金额减去扣除额然后再去计算税额.
                 *
                 */
                // 计算出的税额
                jsse = MathUtil.mul(ddmxxx.getJE(), sl);
                if (StringUtils.isNotBlank(ddmxxx.getKCE())) {
                    jsse = MathUtil.mul(DecimalCalculateUtil.decimalFormat(MathUtil.sub(ddmxxx.getJE(),
                            ddmxxx.getKCE()), ConfigureConstant.INT_2), sl);
                }
                // 订单中传递的税额
                double se = Double.parseDouble(ddmxxx.getSE());
                double seCompareResult = MathUtil.sub(DecimalCalculateUtil.decimalFormat(jsse, 2), String.valueOf(se));

                // 误差大于6分钱，则税额有误
                if (Math.abs(seCompareResult) > 0.06) {
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_SE_ERROR_144098);
                    return checkResultMap;
                }
            }


            /**
             * 项目数量、项目单价都不为空,并且非折扣行的情况下，要求 项目金额   与  （项目数量 * 项目单价） 之差，误差不能大于1分钱（即误差小于或等于1分钱）。
             */
            //非折扣行计算,折扣行不进行计算
            if (!StringUtils.isBlank(ddmxxx.getDJ()) && !StringUtils.isBlank(ddmxxx.getSL()) &&
                    (!OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxx.getFPHXZ()))) {
                /**
                 * 金额（不含税） = 项目单价 * 项目数量
                 * 计算出的项目金额 与 订单传递的项目金额 比较，误差不能大于1分钱（即误差小于或等于1分钱）
                 */
                //yxmje - js_xmje; 有误差
                // 金额 = 项目单价 * 项目数量
                double js_xmje = MathUtil.mul(ddmxxx.getDJ(), ddmxxx.getSPSL());
                // 项目金额
                double yxmje = Double.parseDouble(ddmxxx.getJE());
                double xmjeCompareResult = MathUtil.sub(String.valueOf(yxmje), DecimalCalculateUtil.decimalFormat(js_xmje, 2));
                // 误差不能大于1分钱（即误差小于或等于1分钱），否则项目金额有误
                if (Math.abs(xmjeCompareResult) > 0.01) {
                    /**
                     * 判断单价是否一致,用金额除以数量得到单价,保留小数点后8位后比较,如果不一致再返回错误,
                     */
                    String js_xmdj = DecimalCalculateUtil.div(ddmxxx.getJE(), ddmxxx.getSL(), ConfigureConstant.INT_8);
                    if (!js_xmdj.equals(DecimalCalculateUtil.decimalFormatToString(ddmxxx.getDJ(), ConfigureConstant.INT_8))) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_JE_ERROR_144099);
                        log.error("{}项目金额有误,误差不能大于1分钱", LOGGER_MSG);
                        return checkResultMap;
                    }

                }
            }
            //成品油的单位只能为吨或者升
            OilEntity queryTaxClassCodeEntityBySpbm = apiTaxClassCodeService.queryOilBySpbm(ddmxxx.getSPBM());
            if (queryTaxClassCodeEntityBySpbm != null) {
                if (!OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(ddmxxx.getFPHXZ()) && !ConfigureConstant.STRING_DUN.equals(ddmxxx.getDW())
                        && !ConfigureConstant.STRING_SHENG.equals(ddmxxx.getDW())) {
                    log.error("{}成品油项目单位只能为吨或升", LOGGER_MSG);
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_DW_ERROR_144063);
                    return checkResultMap;
                }
                if (!OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(ddmxxx.getFPHXZ())) {
                    if (StringUtils.isBlank(ddmxxx.getSPSL())) {
                        log.error("{}成品油项目数量不能为空", LOGGER_MSG);
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_SPSL_ERROR_144100);
                        return checkResultMap;
            
                    }
                }
    
            }
            boolean result5 = (isCpySpbm && queryTaxClassCodeEntityBySpbm == null) || (!isCpySpbm && queryTaxClassCodeEntityBySpbm != null);
            if (result5) {
                //成品油商品编码和非成品油商品编码混开
                log.error("{}发票只能为成品油或者非成品油", LOGGER_MSG);
                checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_SPBM_ERROR_144101);
                return checkResultMap;
            }
    
    
            /**
             * 判断折扣行金额是否合法
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
                // 蓝票
                if (Double.parseDouble(ddmxxx.getJE()) >= 0 && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxx.getFPHXZ()))) {
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKJE_ERROR_144102);
                    return checkResultMap;
                }
            } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
                // 红票
                if (Double.parseDouble(ddmxxx.getJE()) <= 0 && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxx.getFPHXZ()))) {
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKJE_ERROR_144103);
                    return checkResultMap;
                }
            } else {
                // 非法开票类型
                checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_KPLX_ERROR_144104);
                return checkResultMap;
            }


            // 是折扣行(根据发票行性质判断是否是折扣行)
            if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxx.getFPHXZ())) {

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
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKH_ERROR_144105);
                    return checkResultMap;
                }
    
                //如果走到这里说明第一行不是折扣行,当前行是折扣行需要判断上一行是否为被折扣行,如果不是,返回错误
                if (!OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(ddmxxxList.get(i - 1).getFPHXZ())) {
                    //对于蓝字发票，金额为负的商品名称必须与与之相邻的上一行的商品名称相同
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKH_ERROR_144105);
                    return checkResultMap;
                }
    
                //如果是老蓝票对应得新红票报文不校验SPBM和XMMC
                boolean result6 = !(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)
                        && (ddmxxx.getXMMC().startsWith("折扣行数") || ddmxxx.getXMMC().startsWith("折扣"))
                        && (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxx.getFPHXZ())));
                if (result6) {
                    //如果走到这里说明第一行不是折扣行
                    if (!(ddmxxx.getXMMC()).equals(ddmxxxList.get(i - 1).getXMMC())) {
                        //对于蓝字发票，金额为负的商品名称必须与与之相邻的上一行的商品名称相同
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144119);
                        return checkResultMap;
                    }
        
                    //折扣行与被折扣行的商品编码相同
                    if (!(ddmxxx.getSPBM().equals(ddmxxxList.get(i - 1).getSPBM()))) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144106);
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
                double bzkzje_total = 0.0;
                Double zke = MathUtil.add(ddmxxx.getJE(), ddmxxx.getSE());

                if ((ddmxxx.getXMMC()).equals(ddmxxxList.get(i - 1).getXMMC())) {
                    // 单行折扣的类型

                    //获取被折扣行的不含税金额加上税额,即反推含税金额
                    bzkzje_total = MathUtil.add(ddmxxxList.get(i - 1).getJE().trim(), ddmxxxList.get(i - 1).getSE().trim());

                    //折扣校验
                    if ((Math.abs(bzkzje_total) < Math.abs(zke))) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144107);
                        return checkResultMap;
                    }

                    /**
                     * 单行折扣,校验 税率是否相等
                     */
                    //被折扣行税率
                    String bzkhsl = ddmxxxList.get(i - 1).getSL();
                    //折扣行税率
                    String zkhsl = ddmxxx.getSL();
                    //判断折扣行税率与被折扣行税率是否一致
                    if (!bzkhsl.equals(zkhsl)) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_ZKSL_ERROR_144108);
                        return checkResultMap;
                    }
                }
                upIsZkh = true;
            } else {
                // 非折扣行
                upIsZkh = false;

                //只有一个商品行时，发票行性质为必须为0
                if (1 == ddmxxxList.size() && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(ddmxxx.getFPHXZ())) {
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_FPHXZ_ERROR_144109);
                    return checkResultMap;
                }

                //项目明细最后一行的FPHXZ发票行性质不能为2！2016年12月9日16:40:00  阳开国
                if ((i == (ddmxxxList.size() - 1)) && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(ddmxxx.getFPHXZ())) {
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_FPHXZ_ERROR_144110);
                    return checkResultMap;
                }

                // 蓝票数据,非最后一行数据,如果发票行性质为被折扣行,那么下一行必须为折扣行
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)
                        && OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(ddmxxx.getFPHXZ()) && i != (ddmxxxList.size() - 1)) {
                    if (!(OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(ddmxxxList.get(i + 1).getFPHXZ()))) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_FPHXZ_ERROR_144110);
                        return checkResultMap;
                    }
                }

                /**
                 * 被折扣行和正常商品行
                 * 蓝票单价数量,金额不等小于等于0
                 */
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
                    // 非折扣行蓝票处理,明细行的金额都不能小于等于0
                    if (Double.parseDouble(ddmxxx.getJE()) <= 0) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_THAN_ZERO_ERROR_144111);
                        return checkResultMap;
                    }
                } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(kplx)) {
                    // 非折扣行红票处理,明细行的金额不能大于等于0
                    if (Double.parseDouble(ddmxxx.getJE()) >= 0) {
                        checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_LESS_ZERO_ERROR_144112);
                        return checkResultMap;
                    }
                } else {
                    // 非法开票类型
                    checkResultMap = generateErrorMap("", errorMsgString, OrderInfoContentEnum.INVOICE_MX_INFO_KPLX_ERROR_144104);
                    return checkResultMap;
                }
            }
            // 使用BigDecimal作运算
            //总税额(没有做格式化的)
            mxse_total_full = DecimalCalculateUtil.add(mxse_total_full, jsse);
            xmje_total = MathUtil.add(String.valueOf(xmje_total), ddmxxx.getJE());

        }
        if (isCpySpbm && ddmxxxList.size() > ConfigureConstant.INT_8) {
            checkResultMap = generateErrorMap("", "", OrderInfoContentEnum.INVOICE_MX_SPSL_OVER_8_ERROR_144113);
            log.error("{}成品油明细不能超过8行", LOGGER_MSG);
            return checkResultMap;
        }

        xmje_total = Double.parseDouble(DecimalCalculateUtil.decimalFormat(xmje_total, 2));
        Double HJBHSJE = DecimalCalculateUtil.decimalFormatToDouble(ddfpxx.getHJJE(), 2);

        if (HJBHSJE != xmje_total) {
            checkResultMap = generateErrorMap("", "", OrderInfoContentEnum.INVOICE_MX_HJJEANDMXJE_ERROR_144114);
            log.error("{}开具合计金额和明细金额不相等", LOGGER_MSG);
            return checkResultMap;
        }
        /**
         * 明细税额累加 与 发票头中的开票合计税额 比较，误差不能超过127分钱（即小于或等于127分钱）
         */
        double mxse_total_compareResult = Double.parseDouble(DecimalCalculateUtil.decimalFormat(MathUtil.sub(ddfpxx.getHJSE(),
                String.valueOf(mxse_total_full)), ConfigureConstant.INT_2));
        if (Math.abs(mxse_total_compareResult) > ConfigureConstant.DOUBLE_PENNY_127) {

            // 误差大于127分钱，则合计税额有误
            checkResultMap = generateErrorMap("", "", OrderInfoContentEnum.INVOICE_MX_HJSE_ERROR_144115);
            checkResultMap.put(OrderManagementConstant.ERRORMESSAGE, "当合计税额的误差大于1.27元时，根据税局要求不允许开票，请先将订单拆分后再开票。");
            log.error("{}合计税额有误,误差大于127分钱", LOGGER_MSG);
            return checkResultMap;
        }
        return checkResultMap;
    }
}
