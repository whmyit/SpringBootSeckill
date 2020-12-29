package com.dxhy.order.consumer.modules.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.consumer.utils.InterfaceResponseUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import com.dxhy.order.model.entity.InvoiceQuotaEntity;
import com.dxhy.order.model.entity.RuleSplitEntity;
import com.dxhy.order.model.readyopen.GfInfoVo;
import com.dxhy.order.utils.CommonUtils;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.OrderSplitUtil;
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
import java.util.stream.Collectors;

/**
 * 生成待开单据业务实现
 *
 * @author 陈玉航
 * @version 1.0 Created on 2018年7月25日 下午3:29:29
 */
@Service
@Slf4j
public class GenerateReadyOpenOrderServiceImpl implements IGenerateReadyOpenOrderService {
    
    private static final String LOGGER_MSG = "(生成待开单据业务层)";
    
    private static final String CF = "cf";
    
    private static final DecimalFormat DDH_FORMAT = new DecimalFormat("#0000");
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;
    
    @Reference(timeout = 3600000)
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Reference
    private ApiCommodityService apiCommodityService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private UnifyService unifyService;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Reference
    private ApiTaxClassCodeService apiTaxClassCodeService;
    
    @Reference
    private ApiOriginOrderExtendService apiOriginOrderExtendService;
    
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    
    @Reference
    private ApiRuleSplitService ruleSplitService;

    @Reference
    private ApiRushRedInvoiceRequestInfoService apiRushRedInvoiceRequestInfoService;
    
    
    private CommonOrderInfo supplement(CommonOrderInfo commonOrderInfo) {
        commonOrderInfo.getOrderInfo().setFpqqlsh(apiInvoiceCommonMapperService.getGenerateShotKey());
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getDdh())) {
            commonOrderInfo.getOrderInfo().setDdh(RandomUtil.randomNumbers(12));
        }
        List<String> shList = new ArrayList<>();
        shList.add(commonOrderInfo.getOrderInfo().getXhfNsrsbh());
        commonOrderInfo.getOrderInfo().setDdrq(new Date());
        commonOrderInfo.getOrderInfo().setNsrsbh(commonOrderInfo.getOrderInfo().getXhfNsrsbh());
        //判断是否是红票
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(commonOrderInfo.getOrderInfo().getKplx())) {
            //通过原发票代码号码查询到原始蓝票
            if (StringUtils.isNotEmpty(commonOrderInfo.getOrderInfo().getYfpDm())
                    && StringUtils.isNotEmpty(commonOrderInfo.getOrderInfo().getYfpHm())) {
                OrderInvoiceInfo selectByYfp = apiInvoiceCommonMapperService.selectByYfp(commonOrderInfo.getOrderInfo().getYfpDm(), commonOrderInfo.getOrderInfo().getYfpHm(), shList);
            
                if (selectByYfp != null) {
                    commonOrderInfo.getOrderInfo().setThdh(selectByYfp.getDdh());
                }else{
                    commonOrderInfo.getOrderInfo().setTschbz(OrderInfoEnum.TSCHBZ_1.getKey());
                }
            } else {
                commonOrderInfo.getOrderInfo().setThdh(commonOrderInfo.getOrderInfo().getDdh());
            }
        }
        if (StringUtils.isBlank(commonOrderInfo.getHzfpxxbbh())) {
            commonOrderInfo.setFlagbs("");
        }
        return commonOrderInfo;
    }
    
    /**
     * 数据校验
     *
     * @param commonOrderInfo
     * @author:程亚甫
     * @date:2018/10/23
     */
    private Map validateCommonOrder(CommonOrderInfo commonOrderInfo) {
        //判断红蓝票折扣和被折扣金额正负
        List<CommonOrderInfo> list = new ArrayList<>();
        list.add(commonOrderInfo);
        List<CommonOrderInfo> redAndBlueMoney = redAndBlueMoney(list);
    
        /**
         * 根据税号查询税控设备
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInfo.getOrderInfo().getXhfNsrsbh());
    
        /**
         * 税控设备类型添加到订单主信息中
         */
        redAndBlueMoney.get(0).setTerminalCode(terminalCode);
    
        return validateOrderInfo.checkOrderInvoice(redAndBlueMoney.get(0));
    }
    
    /**
     * 转换红票折扣和被折扣金额正负
     *
     * @param commonOrderInfo
     * @return
     */
    public List<CommonOrderInfo> redAndBlueMoney(List<CommonOrderInfo> commonOrderInfo) {
        /**
         * 如果单价不等于空,则进行赋值操作.
         */
        for (CommonOrderInfo info : commonOrderInfo) {
            OrderInfo orderInfo = info.getOrderInfo();
            List<OrderItemInfo> orderItemInfos = info.getOrderItemInfo();
        
            orderInfo.setKphjje(orderInfo.getKphjje().contains("-") ? orderInfo.getKphjje() : ("-" + orderInfo.getKphjje()));
            orderInfo.setHjbhsje(orderInfo.getHjbhsje().contains("-") ? orderInfo.getHjbhsje() : ("-" + orderInfo.getHjbhsje()));
            orderInfo.setHjse(orderInfo.getHjse().contains("-") ? orderInfo.getHjse() : ("-" + orderInfo.getHjse()));
        
            //项目单价
            for (int i = 0; i < orderItemInfos.size(); i++) {
                /**
                 * 如果单价不等于空,则进行赋值操作.
                 */
                if (!StringUtils.isBlank(orderItemInfos.get(i).getXmdj())) {
        
                    orderItemInfos.get(i).setXmdj(orderItemInfos.get(i).getXmdj().startsWith("-") ? orderItemInfos.get(i).getXmdj().replace("-", "") : orderItemInfos.get(i).getXmdj());
                } else {
                    orderItemInfos.get(i).setXmdj("");
                }
                //扣除额变成负值
                if (StringUtils.isNotBlank(orderItemInfos.get(i).getKce())) {
                    if (!orderItemInfos.get(i).getKce().startsWith("-")) {
                        orderItemInfos.get(i).setKce("-" + orderItemInfos.get(i).getKce());
                    }
                }
    
    
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                    //红票时正常商品行和被折扣行金额为负
                    if (OrderInfoEnum.ORDER_LINE_TYPE_0.getKey().equals(orderItemInfos.get(i).getFphxz())
                            || OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfos.get(i).getFphxz())) {
            
                        setAmountNegative(orderItemInfos, i);
            
                    }
                    //红票时折扣行为正
                    if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfos.get(i).getFphxz())) {
    
                        setAmountPositive(orderItemInfos, i);
    
                    }
                }
                if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
                    //蓝票时正常商品行和被折扣行金额为正
                    if (OrderInfoEnum.ORDER_LINE_TYPE_0.getKey().equals(orderItemInfos.get(i).getFphxz())
                            || OrderInfoEnum.ORDER_LINE_TYPE_2.getKey().equals(orderItemInfos.get(i).getFphxz())) {
    
                        setAmountPositive(orderItemInfos, i);
    
                    }
                    //蓝票时折扣行为负
                    if (OrderInfoEnum.ORDER_LINE_TYPE_1.getKey().equals(orderItemInfos.get(i).getFphxz())) {
    
                        setAmountNegative(orderItemInfos, i);
    
                    }
                }
            }

        }
    
        return commonOrderInfo;
    }
    
    /**
     * 设置金额为正数
     *
     * @param orderItemInfos
     */
    private void setAmountPositive(List<OrderItemInfo> orderItemInfos, int i) {
    
        orderItemInfos.get(i).setSe(orderItemInfos.get(i).getSe().contains("-") ? orderItemInfos.get(i).getSe().replace("-", "") : orderItemInfos.get(i).getSe());
        orderItemInfos.get(i).setXmje(orderItemInfos.get(i).getXmje().contains("-") ? orderItemInfos.get(i).getXmje().replace("-", "") : orderItemInfos.get(i).getXmje());
    
        /**
         * 如果数量不等于空,则进行赋值操作
         */
        if (!StringUtils.isBlank(orderItemInfos.get(i).getXmsl())) {
            orderItemInfos.get(i).setXmsl(orderItemInfos.get(i).getXmsl().contains("-") ? orderItemInfos.get(i).getXmsl().replace("-", "") : orderItemInfos.get(i).getXmsl());
    
        } else {
            orderItemInfos.get(i).setXmsl("");
        }
    }
    
    /**
     * 设置金额为负数
     *
     * @param orderItemInfos
     */
    private void setAmountNegative(List<OrderItemInfo> orderItemInfos, int i) {
    
        orderItemInfos.get(i).setSe(orderItemInfos.get(i).getSe().contains("-") ? orderItemInfos.get(i).getSe() : ("-" + orderItemInfos.get(i).getSe()));
        orderItemInfos.get(i).setXmje(orderItemInfos.get(i).getXmje().contains("-") ? orderItemInfos.get(i).getXmje() : ("-" + orderItemInfos.get(i).getXmje()));
    
        /**
         * 如果数量不等于空,则进行赋值操作
         */
        if (!StringUtils.isBlank(orderItemInfos.get(i).getXmsl())) {
            orderItemInfos.get(i).setXmsl(orderItemInfos.get(i).getXmsl().contains("-") ? orderItemInfos.get(i).getXmsl() : ("-" + orderItemInfos.get(i).getXmsl()));
    
        } else {
            orderItemInfos.get(i).setXmsl("");
        }
    }
    
    /**
     * 价税分离
     *
     * @param orderInfo
     * @return List<CommonOrderInfo>
     * @throws OrderSeparationException
     */
    public List<CommonOrderInfo> separationTaxes(List<CommonOrderInfo> orderInfo) throws OrderSeparationException {
    
        List<CommonOrderInfo> list = new ArrayList<>();
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            //价税分离
            CommonOrderInfo taxSeparationService = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, new TaxSeparateConfig());
            if (taxSeparationService == null) {
                log.info("{} 订单号为：{} 的订单价税分离失败", LOGGER_MSG, commonOrderInfo.getOrderInfo().getDdh());
                continue;
            } else {
                if (!StringUtils.isBlank(commonOrderInfo.getFlagbs())) {
                    taxSeparationService.setFlagbs(commonOrderInfo.getFlagbs());
                }
                if (!StringUtils.isBlank(commonOrderInfo.getHzfpxxbbh())) {
                    taxSeparationService.setHzfpxxbbh(commonOrderInfo.getHzfpxxbbh());
                }
                list.add(taxSeparationService);
            }
        }
        return list;
    }
    
    /**
     * 补全订单信息
     *
     * @param commonOrderInfos void
     * @author: 陈玉航
     * @date: Created on 2018年7月25日 下午5:48:22
     */
    private R completionOrderItemInfo(List<CommonOrderInfo> commonOrderInfos, String uId, String deptId) {
        
        // 补全商品行单价和数量
        commonOrderInfos = apiOrderInfoService.completionSlAndDj(commonOrderInfos);
        
        /**
         * 循环处理订单数据 1.处理明细信息. 2.处理外层报文的销售方信息. 3.处理外层报文的开票人信息.
         */
        
        for (int i = 0; i < commonOrderInfos.size(); i++) {
            
            CommonOrderInfo commonOrderInfo = commonOrderInfos.get(i);
            OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
            List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
            
            //查询税控设备
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInfo.getXhfNsrsbh());
            
            
            /**
             * 红票备注处理
             * ,红票扣除额需要把扣除额放在原发票代码号码前面
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
                // 百望的税控设备自动添加备注，不需要补备注
                if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
                } else {
                    //扣除额的备注放到红字发票备注之前
                    if (orderItemInfos.size() > 0 && StringUtils.isNotBlank(orderItemInfos.get(0).getKce())) {
                        orderInfo.setBz((orderInfo.getBz() == null ? "" : orderInfo.getBz()) + "对应正数发票代码:"
                                + orderInfo.getYfpDm() + "号码:" + orderInfo.getYfpHm());
                    } else {
                        // 非专票冲红备注可不填
                        if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                            orderInfo.setBz("对应正数发票代码:" + orderInfo.getYfpDm() + "号码:" + orderInfo.getYfpHm()
                                    + (orderInfo.getBz() == null ? "" : orderInfo.getBz()));
                            if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                                orderInfo.setBz(ConfigureConstant.STRING_HZBZ + commonOrderInfo.getHzfpxxbbh());
                            }
                        }
                        
                    }
                    
                }
                
            }
            
            /**
             * 明细商品信息补全
             */
            try {
                iCommonInterfaceService.dealOrderItem(orderItemInfos, orderInfo.getXhfNsrsbh(), orderInfo.getQdBz(), terminalCode);
            } catch (OrderReceiveException e) {
                log.error("{}补全商品信息异常，税号：{}", LOGGER_MSG, orderInfo.getXhfNsrsbh());
                
                return R.error(e.getMessage());
            }
            
            /**
             * 销方信息校验 补全
             */
            if (StringUtils.isBlank(orderInfo.getXhfDz()) || StringUtils.isBlank(orderInfo.getXhfDh()) || StringUtils.isBlank(orderInfo.getXhfYh()) ||
                    StringUtils.isBlank(orderInfo.getXhfZh())) {
                DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(orderInfo.getXhfNsrsbh(), orderInfo.getXhfMc());
                if (sysDeptEntity == null) {
                    log.error("{}无法获取当前税号的销方信息，税号：{}", LOGGER_MSG, orderInfo.getXhfNsrsbh());
                    return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR, null);
                } else {
                    // 销方税号 地址 电话 销售方信息补全
                    commonOrderInfo.getOrderInfo().setXhfDh(StringUtils.isBlank(orderInfo.getXhfDh())
                            ? sysDeptEntity.getTaxpayerPhone() : orderInfo.getXhfDh());
                    commonOrderInfo.getOrderInfo().setXhfNsrsbh(StringUtils.isBlank(orderInfo.getXhfNsrsbh())
                            ? sysDeptEntity.getTaxpayerCode() : orderInfo.getXhfNsrsbh());
                    commonOrderInfo.getOrderInfo().setXhfMc(
                            StringUtils.isBlank(orderInfo.getXhfMc()) ? sysDeptEntity.getName() : orderInfo.getXhfMc());
                    commonOrderInfo.getOrderInfo().setXhfDz(StringUtils.isBlank(orderInfo.getXhfDz())
                            ? sysDeptEntity.getTaxpayerAddress() : orderInfo.getXhfDz());
                    commonOrderInfo.getOrderInfo().setXhfYh(StringUtils.isBlank(orderInfo.getXhfYh())
                            ? sysDeptEntity.getTaxpayerBank() : orderInfo.getXhfYh());
                    commonOrderInfo.getOrderInfo().setXhfZh(StringUtils.isBlank(orderInfo.getXhfZh())
                            ? sysDeptEntity.getTaxpayerAccount() : orderInfo.getXhfZh());
                    commonOrderInfo.getOrderInfo().setNsrmc(
                            StringUtils.isBlank(orderInfo.getNsrmc()) ? sysDeptEntity.getName() : orderInfo.getNsrmc());
                    commonOrderInfo.getOrderInfo().setNsrsbh(
                            StringUtils.isBlank(orderInfo.getNsrsbh()) ? sysDeptEntity.getTaxpayerCode() : orderInfo.getNsrsbh());
                    
                }
            }
        
        
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfNsrsbh())) {
                log.error("{}根据销方税号:{}和销方名称:{}查到销货方税号为空", LOGGER_MSG, orderInfo.getXhfNsrsbh(), orderInfo.getXhfNsrsbh());
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR, null);
            }
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfMc())) {
                log.error("{}根据销方税号:{}和销方名称:{}查到销货方名称为空", LOGGER_MSG, orderInfo.getXhfNsrsbh(), orderInfo.getXhfNsrsbh());
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR, null);
            }
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfDz())) {
                log.error("{}根据销方税号:{}和销方名称:{}查到销货方地址为空", LOGGER_MSG, orderInfo.getXhfNsrsbh(), orderInfo.getXhfNsrsbh());
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR, null);
            }
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfDh())) {
                log.error("{}根据销方税号:{}和销方名称:{}查到销货方电话为空", LOGGER_MSG, orderInfo.getXhfNsrsbh(), orderInfo.getXhfNsrsbh());
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR, null);
            }
        
            // 编码表版本号 清单标志 特殊冲红标志 代开标志 补全
            commonOrderInfo.getOrderInfo().setBbmBbh(SystemConfig.bmbbbh);
            commonOrderInfo.getOrderInfo().setQdBz(StringUtils.isNotBlank(orderInfo.getQdBz()) ? orderInfo.getQdBz() : OrderInfoEnum.QDBZ_CODE_0.getKey());
            commonOrderInfo.getOrderInfo().setTschbz(StringUtils.isBlank(commonOrderInfo.getOrderInfo().getTschbz()) ? OrderInfoEnum.TSCHBZ_0.getKey() : commonOrderInfo.getOrderInfo().getTschbz());
            commonOrderInfo.getOrderInfo().setDkbz(OrderInfoEnum.DKBZ_0.getKey());
        
            /**
             * 开票人信息补全
             */
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getKpr())) {
    
                log.debug("{}开票人信息查询,入参,税号:{},uid:{}", LOGGER_MSG, orderInfo.getNsrsbh(), uId);
                DrawerInfoEntity qi = invoiceService.queryDrawerInfo(orderInfo.getNsrsbh(), uId);
                log.debug("{}开票人信息查询,出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(qi));
    
                if (qi == null || StringUtils.isBlank(qi.getDrawerName())) {
        
                    log.error("{} 订单号为{}的订单开票人复核人收款人为空,返回前端补全", LOGGER_MSG, commonOrderInfo.getOrderInfo().getDdh());
                    return R.error(OrderInfoContentEnum.READY_OPEN_KPR_ERROR);
                }
    
                // 补全开票人 收款人 复核人
                orderInfo.setKpr(qi.getDrawerName());
                orderInfo.setSkr(StringUtils.isBlank(orderInfo.getSkr()) ? qi.getNameOfPayee() : orderInfo.getSkr());
                orderInfo.setFhr(StringUtils.isBlank(orderInfo.getFhr()) ? qi.getReCheckName() : orderInfo.getSkr());
    
            }
    
            // 主要开票项目为第一行明细
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getKpxm())) {
                commonOrderInfo.getOrderInfo().setKpxm(orderItemInfos.get(0).getXmmc());
            }
    
        }
        return R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000).put(OrderManagementConstant.MESSAGE, "补全订单信息成功")
                .put(OrderManagementConstant.DATA, commonOrderInfos);
    }
    
    /**
     * 补充购方信息
     *
     * @param orderInfo
     * @param gfInfo    void
     * @author: 陈玉航
     * @date: Created on 2018年7月25日 下午5:08:11
     */
    private void replacegfxx(List<CommonOrderInfo> orderInfo, GfInfoVo gfInfo) {
        
        for (CommonOrderInfo commonOrderInfo : orderInfo) {
            commonOrderInfo.getOrderInfo().setGhfDh(gfInfo.getGfdh());
            commonOrderInfo.getOrderInfo().setGhfDz(gfInfo.getGfdz());
            commonOrderInfo.getOrderInfo().setGhfEmail(gfInfo.getGfyx());
            commonOrderInfo.getOrderInfo().setGhfMc(gfInfo.getGfmc());
            commonOrderInfo.getOrderInfo().setGhfNsrsbh(gfInfo.getGfsh());
            commonOrderInfo.getOrderInfo().setGhfQylx(gfInfo.getGflx());
            commonOrderInfo.getOrderInfo().setGhfSj(gfInfo.getGfsj());
            commonOrderInfo.getOrderInfo().setGhfYh(gfInfo.getGfkhh());
            commonOrderInfo.getOrderInfo().setGhfZh(gfInfo.getGfyhzh());
        }
    }
    
    
    /**
     * 冲红流程
     *
     * @throws OrderSeparationException
     * @author: 程亚甫
     * @date: Created on 2018年8月3日 上午11:47:29
     */
    @Override
    public R reshRed(CommonOrderInfo grov, String uId, String deptId) throws OrderSeparationException {
        log.info("{} 红字发票单张冲红!", LOGGER_MSG);
        if (grov == null) {
            return R.setCodeAndMsg(OrderInfoContentEnum.READY_ORDER_CHECK_DATA_ERROR, null);
        }

        R result = new R();
        List<CommonOrderInfo> orderInfo = new ArrayList<>();
        orderInfo.add(grov);

        if (grov.getOrderItemInfo().size() <= 2 && StringUtils.isNotBlank(grov.getOrderItemInfo().get(0).getKce())) {

            //去掉原蓝票扣除额
            if (StringUtils.isNotBlank(grov.getOrderInfo().getBz()) && grov.getOrderInfo().getBz().startsWith(ConfigureConstant.STRING_CEZS)) {
                int indexOf = grov.getOrderInfo().getBz().indexOf("。");
                if (indexOf > 0) {
                    grov.getOrderInfo().setBz(grov.getOrderInfo().getBz().substring(indexOf + 1));
                }

            }
        }

        //补全订单和明细信息
        try {
            completeOrderInfo(orderInfo, uId);
        } catch (OrderReceiveException e) {
            return result.put(OrderManagementConstant.CODE,e.getCode()).put(OrderManagementConstant.MESSAGE,e.getMessage());
        }

        if (orderInfo == null || orderInfo.size() == 0) {
            log.info("{} 补全明细行信息结束后，订单信息为空", LOGGER_MSG);
            return result.put(OrderManagementConstant.CODE, OrderInfoContentEnum.PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, "补全明细行信息结束后，订单信息为空");
        }

        log.info("{} 订单信息补全完毕:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfo));
    
        if (!OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(orderInfo.get(0).getOrderItemInfo().get(0).getFphxz())) {
            log.info("{} 价税分离前数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfo));
            //价税分离
            orderInfo = separationTaxes(orderInfo);
            log.info("{} 价税分离后数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfo));
            if (orderInfo == null || orderInfo.size() == 0) {
                log.info("{} 价税分离失败，订单信息为空", LOGGER_MSG);
                return result.put(OrderManagementConstant.CODE, OrderInfoContentEnum.PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, "价税分离失败，订单信息为空");
            }
        }
    
        //补充校验所需数据
        CommonOrderInfo commonOrderInfo = supplement(orderInfo.get(0));
    
        log.info("{} 数据校验", LOGGER_MSG);
        //数据校验
        Map map = validateCommonOrder(commonOrderInfo);
        if (!ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.ERRORCODE))) {
            log.info("{} 订单校验失败：订单号：{}，失败原因：{}，错误代码：{}", LOGGER_MSG,
                    orderInfo.get(0).getOrderInfo().getDdh(), map.get(OrderManagementConstant.ERRORMESSAGE), map.get(OrderManagementConstant.ERRORCODE));
            return result.put(OrderManagementConstant.CODE, map.get(OrderManagementConstant.ERRORCODE)).put(OrderManagementConstant.MESSAGE, map.get(OrderManagementConstant.ERRORMESSAGE));
        }


        //待开订单信息入库
        List<CommonOrderInfo> commonOrderInfos = new ArrayList<>();

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
                return result.put(OrderManagementConstant.CODE, OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getKey());
            }
        }
    
        OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
        orderProcessInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_1.getKey());
        orderProcessInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_3.getKey());
        orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
        commonOrderInfo.setProcessInfo(orderProcessInfo);
        commonOrderInfo.getOrderInfo().setCreateTime(new Date());
        commonOrderInfo.getOrderInfo().setUpdateTime(new Date());
        commonOrderInfos.add(commonOrderInfo);
    
        boolean b = this.saveOrderInfo(commonOrderInfos);
    
        if (!b) {
            return result.put(OrderManagementConstant.CODE, OrderInfoContentEnum.PARAM_NULL.getKey()).put(OrderManagementConstant.MESSAGE, "数据入库失败");
        }


        return result.put(OrderManagementConstant.MESSAGE, "成功生成" + commonOrderInfos.size() + "条待开单据").put(OrderManagementConstant.DATA, commonOrderInfos);
    }
    
    /**
     * 补全订单信息
     */
    @Override
    public void completeOrderInfo(List<CommonOrderInfo> commonOrderInfos, String userId) throws OrderReceiveException {


        for (CommonOrderInfo commonOrderInfo : commonOrderInfos) {
            
            OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
            //查询税控设备
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInfo.getXhfNsrsbh());
            commonOrderInfo.setTerminalCode(terminalCode);

    
            //红字发票添加备注
			if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
				// 百旺的红字发票和扣除额的备注 底层做处理
                /* && !OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)*/
                if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                    // 有扣除额的红票需要 在红字备注前展示扣除额备注
                    
                    // 红字专票需要备注红字申请单编号
                    if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                        if (orderInfo.getBz() == null || !orderInfo.getBz().contains(ConfigureConstant.STRING_HZBZ)) {
                            orderInfo.setBz((orderInfo.getBz() == null ? "" : orderInfo.getBz())
                                    + ConfigureConstant.STRING_HZBZ + commonOrderInfo.getHzfpxxbbh());
                        }
                        // 普票冲红需要备注红字发票代码号码
                    } else {
                        if (StringUtils.isBlank(orderInfo.getBz()) ||  !orderInfo.getBz().contains("对应正数发票代码")) {
                            orderInfo.setBz("对应正数发票代码:" + orderInfo.getYfpDm() + "号码:" + orderInfo.getYfpHm()
                                    + (orderInfo.getBz() == null ? "" : orderInfo.getBz()));
                        }
                    }
                }
            }
            //扣除额的发票添加备注 扣除额
            if (commonOrderInfo.getOrderItemInfo().size() > 0 && commonOrderInfo.getOrderItemInfo().size() <= 2 && StringUtils.isNotBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce())) {

                if (StringUtils.isBlank(orderInfo.getBz()) || !orderInfo.getBz().contains(ConfigureConstant.STRING_CEZS)) {
                    if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInfo.getKplx())) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(ConfigureConstant.STRING_CEZS_).append(commonOrderInfo.getOrderItemInfo().get(0).getKce()).append("。").append(orderInfo.getBz());
                        orderInfo.setBz(sb.toString());
                    } else {
                        StringBuilder sb = new StringBuilder();
                        sb.append(ConfigureConstant.STRING_CEZS).append("。").append(orderInfo.getBz());
                        orderInfo.setBz(sb.toString());
                    }

                }
            }

    
            // 销方信息校验
            if (StringUtils.isBlank(orderInfo.getXhfDz()) || StringUtils.isBlank(orderInfo.getXhfDh()) || StringUtils.isBlank(orderInfo.getXhfYh()) ||
                    StringUtils.isBlank(orderInfo.getXhfZh())) {
                DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(orderInfo.getXhfNsrsbh(), orderInfo.getXhfMc());
                if (sysDeptEntity == null) {
                    log.error("{}无法获取当前税号的销方信息，税号：{}", LOGGER_MSG, orderInfo.getXhfNsrsbh());
                    throw new OrderReceiveException(OrderInfoContentEnum.READY_OPEN_XFXX_ERROR);
                } else {
                    // 销方税号 地址 电话 销售方信息补全
                    completeSellerInfo(orderInfo, sysDeptEntity);
                }
            }
    
            // 补全开票人信息
            if (StringUtils.isBlank(orderInfo.getKpr())) {
        
                if (StringUtils.isBlank(userId)) {
                    log.error("开票人为空");
                    throw new OrderReceiveException(OrderInfoContentEnum.READY_OPEN_KPR_ERROR);
                }
                
                log.debug("{}开票人信息查询,入参,税号:{},uid:{}", LOGGER_MSG, orderInfo.getNsrsbh(), userId);
                DrawerInfoEntity qi = invoiceService.queryDrawerInfo(orderInfo.getNsrsbh(), userId);
                log.debug("{}开票人信息查询,出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(qi));
                
                if (qi == null || StringUtils.isBlank(qi.getDrawerName())) {
                    log.error("{} 订单号为{}的订单开票人复核人收款人为空,返回前端补全", LOGGER_MSG, orderInfo.getDdh());
                    
                    throw new OrderReceiveException(OrderInfoContentEnum.READY_OPEN_KPR_ERROR);
                }
                // 补全开票人 收款人 复核人
                orderInfo.setKpr(qi.getDrawerName());
                orderInfo.setSkr(StringUtils.isBlank(orderInfo.getSkr()) ? qi.getNameOfPayee() : orderInfo.getSkr());
                orderInfo.setFhr(StringUtils.isBlank(orderInfo.getFhr()) ? qi.getReCheckName() : orderInfo.getSkr());
            }
    
            // 补齐开票项目
            if (StringUtils.isBlank(orderInfo.getKpxm())) {
                orderInfo.setKpxm(commonOrderInfo.getOrderItemInfo().get(0).getXmmc());
            }
    
            // 编码表版本号 清单标志 特殊冲红标志 代开标志 补全
            orderInfo.setBbmBbh(SystemConfig.bmbbbh);
    
            orderInfo.setTschbz(OrderInfoEnum.TSCHBZ_0.getKey());
            orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());
    
            // 补全明细信息
            List<OrderItemInfo> orderItemList = commonOrderInfo.getOrderItemInfo();
            
            iCommonInterfaceService.dealOrderItem(orderItemList, orderInfo.getXhfNsrsbh(), orderInfo.getQdBz(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            /**
             * 清单标志赋值
             */
            if (!OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz())) {
                BeanTransitionUtils.getOrderInvoiceInfoQdBz(terminalCode, orderInfo, orderItemList);
            }
        }
        
    }
    
    /**
     * @param @param order
     * @param @param sysDeptEntity
     * @return void
     * @throws
     * @Title : completeSellerInfo
     * @Description 销方信息补全
     */
    private void completeSellerInfo(OrderInfo order, DeptEntity sysDeptEntity) {
        order.setXhfDh(StringUtils.isBlank(order.getXhfDh()) ? sysDeptEntity.getTaxpayerPhone() : order.getXhfDh());
        order.setXhfNsrsbh(
                StringUtils.isBlank(order.getXhfNsrsbh()) ? sysDeptEntity.getTaxpayerCode() : order.getXhfNsrsbh());
        order.setXhfMc(StringUtils.isBlank(order.getXhfMc()) ? sysDeptEntity.getName() : order.getXhfMc());
        order.setXhfDz(StringUtils.isBlank(order.getXhfDz()) ? sysDeptEntity.getTaxpayerAddress() : order.getXhfDz());
        order.setXhfYh(StringUtils.isBlank(order.getXhfYh()) ? sysDeptEntity.getTaxpayerBank() : order.getXhfYh());
        order.setXhfZh(StringUtils.isBlank(order.getXhfZh()) ? sysDeptEntity.getTaxpayerAccount() : order.getXhfZh());
        order.setNsrmc(StringUtils.isBlank(order.getNsrmc()) ? sysDeptEntity.getName() : order.getNsrmc());
        order.setNsrsbh(StringUtils.isBlank(order.getNsrsbh()) ? sysDeptEntity.getTaxpayerCode() : order.getNsrsbh());
    }
    
    /**
     * 生成待开具订单
     */
	@Override
	public R excuSingle(CommonOrderInfo commonOrderInfo, String uid) {

		// 价税分离
		try {
			commonOrderInfo = PriceTaxSeparationUtil.taxSeparationService(commonOrderInfo, new TaxSeparateConfig());
		} catch (OrderSeparationException e1) {
			log.error("订单价税分离失败:{}", e1.getMessage());
			return R.error().put(OrderManagementConstant.CODE, e1.getCode()).put(OrderManagementConstant.MESSAGE,
					e1.getMessage());
		}
		try {

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
                    throw new OrderSplitException(OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getKey(), OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getMessage());
                }
            }

			// 补全订单信息
			List<CommonOrderInfo> commonOrderInfos = new ArrayList<>();
			commonOrderInfos.add(commonOrderInfo);
			completeOrderInfo(commonOrderInfos, uid);

			Map<String, String> check = validateOrderInfo.checkOrderInvoice(commonOrderInfos.get(0));
			if (!OrderInfoContentEnum.SUCCESS.getKey().equals(check.get(OrderManagementConstant.ERRORCODE))) {
				return R.error().put(OrderManagementConstant.CODE, check.get(OrderManagementConstant.ERRORCODE))
						.put(OrderManagementConstant.MESSAGE, check.get(OrderManagementConstant.ERRORMESSAGE));
            }
            
            OrderProcessInfo processInfo = new OrderProcessInfo();
            processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_1.getKey());
            processInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
            processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
            commonOrderInfos.get(0).setProcessInfo(processInfo);
            
            // 订单超限额拆分
            
            List<CommonOrderInfo> orderSplit = orderSplit(commonOrderInfos, commonOrderInfos.get(0).getTerminalCode(),
                    uid);
            for (CommonOrderInfo common : orderSplit) {
                /**
                 * 税控设备类型添加到订单主信息中
                 */
                common.setTerminalCode(commonOrderInfos.get(0).getTerminalCode());
    
                Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(common);
                if (!OrderInfoContentEnum.SUCCESS.getKey()
                        .equals(checkOrderInvoice.get(OrderManagementConstant.ERRORCODE))) {
                    return R.error()
                            .put(OrderManagementConstant.CODE, checkOrderInvoice.get(OrderManagementConstant.ERRORCODE))
                            .put(OrderManagementConstant.MESSAGE,
                                    checkOrderInvoice.get(OrderManagementConstant.ERRORMESSAGE));
                }
    
            }
			//保存订单信息
			commonOrderInfos = savePageOrderInfo(commonOrderInfos);

			if(orderSplit.size() > 1){
				//保存拆分后的订单信息
				for(CommonOrderInfo commonOrder : orderSplit){
					commonOrder.setOriginOrderId(commonOrderInfos.get(0).getOrderInfo().getId());
					commonOrder.setOriginProcessId(commonOrderInfos.get(0).getOrderInfo().getProcessId());
				}
				orderSplit = saveOrderSplitInfo(orderSplit);
			}
			return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
					.put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage())
					.put(OrderManagementConstant.DATA, orderSplit);
		} catch (OrderSplitException e) {
			log.error("拆分信息处理异常:{}", e.getMessage());
			return R.error(e.getCode(), e.getMessage());
		} catch (OrderReceiveException e) {
			log.error("补全信息处理异常:{}", e.getMessage());
			return R.error(e.getCode(), e.getMessage());
		}
	}
    
    /**
     * 订单拆分
     *
     * @throws OrderSplitException
     */
    @Override
    public List<CommonOrderInfo> orderSplit(List<CommonOrderInfo> value, String terminalCode,String userId) throws OrderSplitException {
        List<CommonOrderInfo> resultList = new ArrayList<>();
        
        
        for (CommonOrderInfo common : value) {
            InvoiceQuotaEntity queryInvoiceQuotaInfoFromRedis = unifyService.queryInvoiceQuotaInfoFromRedis(
                    common.getOrderInfo().getXhfNsrsbh(), common.getOrderInfo().getFpzlDm(), terminalCode);
            
            // 订单超限额拆分
            if (queryInvoiceQuotaInfoFromRedis == null
                    || StringUtils.isBlank(queryInvoiceQuotaInfoFromRedis.getInvoiceAmount())) {
                log.error("获取限额信息异常,税号：{}，发票种类代码:{}", common.getOrderInfo().getXhfNsrsbh(),
                        common.getOrderInfo().getFpzlDm());
                throw new OrderSplitException("1000", "未查询到税盘信息", null);
            }
            
            double kpxe = Double.parseDouble(queryInvoiceQuotaInfoFromRedis.getInvoiceAmount());
            if (Double.parseDouble(common.getOrderInfo().getHjbhsje()) > kpxe) {
            	
            	
                RuleSplitEntity queryRuleSplit = ruleSplitService.queryRuleSplit(common.getOrderInfo().getXhfNsrsbh(), userId);
                String ruleSplitRule = (queryRuleSplit == null ? "0" : queryRuleSplit.getRuleSplitType());

                if (StringUtils.isBlank(common.getOrderInfo().getDdh())) {
                    common.getOrderInfo().setDdh(RandomUtil.randomNumbers(12));
                }
    
                OrderSplitConfig config = new OrderSplitConfig();
                config.setSplitType(OrderSplitEnum.ORDER_SPLIT_TYPE_1.getKey());
                config.setSplitRule(ruleSplitRule);
                config.setLimitJe(kpxe);
                List<CommonOrderInfo> orderSplit = OrderSplitUtil.orderSplit(common, config);
                // 拆分后的数据校验
                int i = 1;
                for (CommonOrderInfo commonOrderInfo : orderSplit) {
        
                    /**
                     * 税控设备类型添加到订单主信息中
                     */
                    commonOrderInfo.setTerminalCode(terminalCode);
        
                    Map<String, String> checkOrderInvoice = validateOrderInfo.checkOrderInvoice(commonOrderInfo);
                    if (!OrderInfoContentEnum.SUCCESS.getKey()
                            .equals(checkOrderInvoice.get(OrderManagementConstant.ERRORCODE))) {
                        log.error("拆分后的订单校验异常:{}", checkOrderInvoice.get(OrderManagementConstant.ERRORMESSAGE));
                        throw new OrderSplitException(checkOrderInvoice.get(OrderManagementConstant.ERRORCODE),
                                checkOrderInvoice.get(OrderManagementConstant.ERRORMESSAGE), null);
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
                            throw new OrderSplitException(OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getKey(), OrderInfoContentEnum.ORDER_MERGE_EXCEPTION_ERROR.getMessage());
                        }
                    }


                    //重置清单标志
        
                    String qdbz = CommonUtils.getQdbz(commonOrderInfo.getOrderInfo().getQdBz(), commonOrderInfo.getOrderItemInfo().size());
                    commonOrderInfo.getOrderInfo().setQdBz(qdbz);
                    if ("1".equals(qdbz) && org.apache.commons.lang.StringUtils.isBlank(commonOrderInfo.getOrderInfo().getQdXmmc())) {
                        commonOrderInfo.getOrderInfo().setQdXmmc(ConfigureConstant.XJXHQD);
                    } else if ("0".equals(qdbz) && org.apache.commons.lang.StringUtils.isNotBlank(commonOrderInfo.getOrderInfo().getQdXmmc())) {
                        commonOrderInfo.getOrderInfo().setQdXmmc("");
            
                    }
                    commonOrderInfo.setOriginOrderId(common.getOrderInfo().getId());
                    commonOrderInfo.setOriginProcessId(common.getOrderInfo().getProcessId());
                    commonOrderInfo.getOrderInfo().setKpxm(commonOrderInfo.getOrderItemInfo().get(0).getXmmc());
                    commonOrderInfo.setIsSplitOrder(ConfigureConstant.STRING_0);
                    //重新设置id和发票请求流水号
                    commonOrderInfo.getOrderInfo().setId(apiInvoiceCommonMapperService.getGenerateShotKey());
                    commonOrderInfo.getOrderInfo().setFpqqlsh(apiInvoiceCommonMapperService.getGenerateShotKey());
                    commonOrderInfo.getOrderInfo().setDdlx(OrderInfoEnum.ORDER_TYPE_1.getKey());
                    StringBuilder sb = new StringBuilder();
        
                    String cfDdh = sb.append(commonOrderInfo.getOrderInfo().getDdh()).append(CF).append(DDH_FORMAT.format(i)).toString();
                    commonOrderInfo.getOrderInfo().setDdh(CommonUtils.dealDdh(cfDdh));
                    i++;
                }
                resultList.addAll(orderSplit);
            } else {
                if (StringUtils.isBlank(common.getOrderInfo().getDdh())) {
                    common.getOrderInfo().setDdh(RandomUtil.randomNumbers(12));
                }
                resultList.add(common);
            }
        }
        return resultList;
    }
    
    /**
     * 保存暂存的订单信息
     * 保存初始化的数据
     *
     * @throws OrderReceiveException
     */
    @Override
    public boolean saveOrderInfo(List<CommonOrderInfo> commonOrderInfoList) {
        
        List<OrderInfo> insertOrderInfoList = new ArrayList<>();
        List<List<OrderItemInfo>> insertOrderItemList = new ArrayList<>();
        List<OrderProcessInfo> insertOrderProcessInfoList = new ArrayList<>();
        List<OrderProcessInfo> updateOrderProcessInfoList = new ArrayList<>();
        List<OrderInvoiceInfo> insertOrderInvoiceInfoList = new ArrayList<>();
        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
        List<String> shList = new ArrayList<>();

        Date createTime = new Date();
        Date updateTime = createTime;
        for (CommonOrderInfo commonOrderInfo : commonOrderInfoList) {
            OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
            List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
            shList.add(orderInfo.getXhfNsrsbh());
            OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
            //OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
    
            /**
             * d订单类型以传递过来的数据为准,commonorderinfo中processinfo状态
             * 订单来源和订单状态都以这个对象传递为准.
             */
            orderInfo.setDdlx(commonOrderInfo.getProcessInfo().getDdlx());


            iCommonInterfaceService.buildInsertOrderData(orderInfo, orderItemInfos, orderProcessInfo, orderInvoiceInfo);
    
            orderProcessInfo.setDdly(commonOrderInfo.getProcessInfo().getDdly());
            orderProcessInfo.setDdzt(commonOrderInfo.getProcessInfo().getDdzt());
            
            
            //原始订单到最终订单的关系
            OrderOriginExtendInfo orderOriginExtendInfo = new OrderOriginExtendInfo();
            orderOriginExtendInfo.setCreateTime(orderInfo.getCreateTime() == null ? createTime : orderInfo.getCreateTime());
            orderOriginExtendInfo.setUpdateTime(orderInfo.getUpdateTime() == null ? updateTime : orderInfo.getUpdateTime());
            orderOriginExtendInfo.setFpqqlsh(orderInfo.getFpqqlsh());
            orderOriginExtendInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            orderOriginExtendInfo.setOrderId(orderInfo.getId());
            orderOriginExtendInfo.setOriginOrderId(orderInfo.getId());
            orderOriginExtendInfo.setOriginFpqqlsh(orderInfo.getFpqqlsh());
            orderOriginExtendInfo.setOriginDdh(orderInfo.getDdh());
            orderOriginExtendInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderOriginList.add(orderOriginExtendInfo);
            
            /**
             * 订单处理扩展表入库前数据补全
             */
            insertOrderInfoList.add(orderInfo);
            insertOrderItemList.add(orderItemInfos);
            insertOrderProcessInfoList.add(orderProcessInfo);
            insertOrderInvoiceInfoList.add(orderInvoiceInfo);
    
            commonOrderInfo.setOrderInfo(orderInfo);
            commonOrderInfo.setOrderItemInfo(orderItemInfos);
            commonOrderInfo.setProcessInfo(orderProcessInfo);
            
        }
        shList = shList.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        try {
            apiInvoiceCommonMapperService.saveData(insertOrderInfoList, insertOrderItemList, insertOrderProcessInfoList, null,
                    updateOrderProcessInfoList, null, orderOriginList, shList);
        } catch (Exception e) {
            log.error("{}保存数据库错误:错误信息为:{}", LOGGER_MSG, e);
            return false;
        }
        
        
        return true;
    }
    
    
    @Override
    public List<CommonOrderInfo> savePageOrderInfo(List<CommonOrderInfo> commonOrderInfoList) {
        
        List<OrderInfo> insertOrderInfoList = new ArrayList<>();
        List<List<OrderItemInfo>> insertOrderItemList = new ArrayList<>();
        List<OrderProcessInfo> insertOrderProcessInfoList = new ArrayList<>();
        List<OrderInvoiceInfo> insertOrderInvoiceInfoList = new ArrayList<>();
        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
        
        for (CommonOrderInfo commonOrderInfo : commonOrderInfoList) {
            OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
            List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
            
            OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
            //OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
            /**
             * d订单类型以传递过来的数据为准,commonorderinfo中processinfo状态
             * 订单来源和订单状态都以这个对象传递为准.
             */
            orderInfo.setDdlx(commonOrderInfo.getProcessInfo().getDdlx());
            
            iCommonInterfaceService.buildInsertOrderData(orderInfo, orderItemInfos, orderProcessInfo, orderInvoiceInfo);
            
            orderProcessInfo.setDdly(commonOrderInfo.getProcessInfo().getDdly());
            orderProcessInfo.setDdzt(commonOrderInfo.getProcessInfo().getDdzt());
            
            
            //原始订单到最终订单的关系
            OrderOriginExtendInfo orderOriginExtendInfo = new OrderOriginExtendInfo();
            orderOriginExtendInfo.setCreateTime(new Date());
            orderOriginExtendInfo.setUpdateTime(new Date());
            orderOriginExtendInfo.setFpqqlsh(orderInfo.getFpqqlsh());
            orderOriginExtendInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            orderOriginExtendInfo.setOrderId(orderInfo.getId());
            orderOriginExtendInfo.setOriginOrderId(orderInfo.getId());
            orderOriginExtendInfo.setOriginFpqqlsh(orderInfo.getFpqqlsh());
            orderOriginExtendInfo.setOriginDdh(orderInfo.getDdh());
            orderOriginExtendInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderOriginList.add(orderOriginExtendInfo);
            
            /**
             * 订单处理扩展表入库前数据补全
             */
            insertOrderInfoList.add(orderInfo);
            insertOrderItemList.add(orderItemInfos);
            insertOrderProcessInfoList.add(orderProcessInfo);
            insertOrderInvoiceInfoList.add(orderInvoiceInfo);
    
            commonOrderInfo.setOrderInfo(orderInfo);
            commonOrderInfo.setOrderItemInfo(orderItemInfos);
            commonOrderInfo.setProcessInfo(orderProcessInfo);
    
        }
        apiInvoiceCommonMapperService.savePageData(insertOrderInfoList, insertOrderItemList, insertOrderProcessInfoList, orderOriginList);
        return commonOrderInfoList;
    }
    
    
    /**
     * 保存合并后的订单信息
     *
     * @param commonOrder
     * @return
     */
    @Override
    public boolean saveOrderMergeInfo(CommonOrderInfo commonOrder) {
        return apiInvoiceCommonMapperService.saveMergeOrderInfo(commonOrder);
    }
    
    /**
     * 保存拆分后的订单信息
     *
     * @param resultList
     * @return
     * @throws OrderReceiveException
     */
    @Override
    public List<CommonOrderInfo> saveOrderSplitInfo(List<CommonOrderInfo> resultList) throws OrderReceiveException {
        return apiInvoiceCommonMapperService.saveOrderSplitInfo(resultList);
    }
    
    @Override
    public void completeOrderInfo(List<CommonOrderInfo> specialInvoiceList) throws OrderReceiveException {
        
        // 获取当前登陆部门下的所有用户信息
        for (CommonOrderInfo commonOrderInfo : specialInvoiceList) {
            
            OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
            //查询税控设备
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInfo.getXhfNsrsbh());
            
            //扣除额的发票添加备注
            
            if (commonOrderInfo.getOrderItemInfo().size() <= 2 && StringUtils.isNotBlank(commonOrderInfo.getOrderItemInfo().get(0).getKce())) {
    
                if (!orderInfo.getBz().contains(ConfigureConstant.STRING_CEZS)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ConfigureConstant.STRING_CEZS_).append(commonOrderInfo.getOrderItemInfo().get(0).getKce()).append("。").append(orderInfo.getBz());
                    orderInfo.setBz(sb.toString());
                }
            }
    
            //红字发票添加备注
			if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
				// 百望的红票税控自动补备注
                if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode)
                        && !OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    // 有扣除额的红票需要 在红字备注前展示扣除额备注
                    
                    // 红字专票需要备注红字申请单编号
                    if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
                        if (orderInfo.getBz() == null || !orderInfo.getBz().contains(ConfigureConstant.STRING_HZBZ)) {
                            orderInfo.setBz((orderInfo.getBz() == null ? "" : orderInfo.getBz())
                                    + ConfigureConstant.STRING_HZBZ + commonOrderInfo.getHzfpxxbbh());
                        }
                        // 普票冲红需要备注红字发票代码号码
                    } else {
						if(!orderInfo.getBz().contains("对应正数发票代码")){
							orderInfo.setBz("对应正数发票代码:" + orderInfo.getYfpDm() + "号码:" + orderInfo.getYfpHm()
							+ (orderInfo.getBz() == null ? "" : orderInfo.getBz()));
						}
						
					}
				}
			}
    
            // 补齐开票项目
            if (StringUtils.isBlank(orderInfo.getKpxm())) {
                orderInfo.setKpxm(commonOrderInfo.getOrderItemInfo().get(0).getXmmc());
            }
    
            // 编码表版本号 清单标志 特殊冲红标志 代开标志 补全
            orderInfo.setBbmBbh(SystemConfig.bmbbbh);
    
            orderInfo.setTschbz(OrderInfoEnum.TSCHBZ_0.getKey());
            orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());
    
            // 补全明细信息
            List<OrderItemInfo> orderItemList = commonOrderInfo.getOrderItemInfo();
            
            iCommonInterfaceService.dealOrderItem(orderItemList, orderInfo.getXhfNsrsbh(), orderInfo.getQdBz(), OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
            /**
             * 清单标志赋值
             */
            if (!OrderInfoEnum.QDBZ_CODE_4.getKey().equals(orderInfo.getQdBz())) {
                BeanTransitionUtils.getOrderInvoiceInfoQdBz(terminalCode, orderInfo, orderItemList);
            }
        }
		
	}
 public static void main(String[] args) {
     String str = "差额征税：5000.00。aaaaa";
     int indexOf = str.indexOf(ConfigureConstant.STRING_CEZS);
     int indexOf2 = str.indexOf("。");
     System.out.println(indexOf + "-------" + indexOf2);
     System.out.print(str.substring(indexOf2 + 1));
 }
 
}
