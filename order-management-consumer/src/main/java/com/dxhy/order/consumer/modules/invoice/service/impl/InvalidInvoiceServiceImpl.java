package com.dxhy.order.consumer.modules.invoice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceRequest;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceResponseExtend;
import com.dxhy.order.model.a9.zf.KbZfRequest;
import com.dxhy.order.model.a9.zf.KbZfResponseExtend;
import com.dxhy.order.model.a9.zf.ZfRequest;
import com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.protocol.order.INVALID_INVOICES_RSP;
import com.dxhy.order.protocol.order.INVALID_INVOICE_INFOS;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：InvalidInvoiceServiceImpl
 * @Description ：发票作废service层
 * @date ：2018年8月1日 下午5:11:19
 */
@Service
@Slf4j
public class InvalidInvoiceServiceImpl implements InvalidInvoiceService {

    private static final String LOGGER_MSG = "发票作废业务类";

    @Reference
    private ApiInvalidInvoiceService apiInvalidInvoiceService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Reference
    private ApiPushService apiPushService;
    
    @Resource
    private UnifyService unifyService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    /**
     * 空白发票作废
     */
    @Override
    public R validInvoice(String receviePoint, String invoiceType, String invoiceCode, String invoiceNum,
                          String kpjh, String nsrsbh, String xhfmc) throws OrderReceiveException {
    
        //todo fangge 作废人统一管理
        String username = userInfoService.getUser().getUsername();
        /**
         * 根据税号 获取已经配置的税盘信息
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
    
        List<String> shList = new ArrayList<>();
        shList.add(nsrsbh);
    
        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
        invalidInvoiceInfo.setFpdm(invoiceCode);
        invalidInvoiceInfo.setFphm(invoiceNum);
    
        //查询发票作废表是否存在数据
        InvalidInvoiceInfo selectByInvalidInvoiceInfo = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(invalidInvoiceInfo, shList);
        if (selectByInvalidInvoiceInfo != null) {
            //如果发票作废表 存在当前数据 说明 发票重复作废
            log.error("{}发票空白作废失败!请求数据为:{}", LOGGER_MSG, invoiceNum);
            return R.error(OrderInfoContentEnum.INVOICE_VALID_REPEAT);
        }
    
        //查询数据库是否存在此发票信息
        OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(invoiceCode, invoiceNum, shList);
        log.debug("{}根据发票代码，发票号码从数据库中查出的发票信息：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
        if (orderInvoiceInfo != null) {
            R checkInvocieInfo = checkInvocieInfo(orderInvoiceInfo);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkInvocieInfo.get(OrderManagementConstant.CODE))) {
                return checkInvocieInfo;
            }
        } else {
            if (StringUtils.isBlank(invoiceCode) || StringUtils.isBlank(invoiceNum)) {
                log.error("{}发票空白作废失败请求发票代码号码为空!", LOGGER_MSG);
                return R.error(OrderInfoContentEnum.INVOICE_VALID_ERROR2);
            }
    
        }
    
    
        //方格税盘特殊处理
    
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
            //组装数据库bean
            invalidInvoiceInfo = createInvalidInvoiceInfo(invoiceCode, invoiceNum, invoiceType, receviePoint, nsrsbh, xhfmc);
    
            //空白发票作废
            invalidInvoiceInfo.setZflx(ConfigureConstant.STRING_0);
            invalidInvoiceInfo.setZfr(username);
            //作废标志设置为作废中
            invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
            /**
             * 通知方格获取待作废消息
             */
            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(nsrsbh, kpjh);
            if (StringUtils.isNotEmpty(registCodeStr)) {
                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                /**
                 * 存放作废信息到redis队列
                 */
                PushPayload pushPayload = new PushPayload();
                //空白发票作废
                pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_4);
                pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                pushPayload.setJQBH(registrationCode.getJqbh());
                pushPayload.setZCM(registrationCode.getZcm());
                pushPayload.setZFPCH(invalidInvoiceInfo.getZfpch());
                apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
            }
            //将作废后的发票信息插入数据库
            boolean validInvoice = apiInvalidInvoiceService.validInvoice(invalidInvoiceInfo);
            if (!validInvoice) {
                return R.error();
            }
        } else {
    
            /**
             * 校验其他税控设备下一张发票是否一致
             */
            if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                QueryNextInvoiceRequest queryNextInvoiceRequest = new QueryNextInvoiceRequest();
                queryNextInvoiceRequest.setFpzlDm(invoiceType);
                queryNextInvoiceRequest.setNsrsbh(shList.get(0));
                queryNextInvoiceRequest.setSldId(receviePoint);
                QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = HttpInvoiceRequestUtil.queryNextInvoice(OpenApiConfig.queryNextInvoice, queryNextInvoiceRequest, terminalCode);
                if (queryNextInvoiceResponseExtend == null || !queryNextInvoiceResponseExtend.getStatusCode().equals(OrderInfoContentEnum.SUCCESS.getKey())) {
                    return R.error().put(OrderManagementConstant.MESSAGE, "获取下一张发票的接口异常");
                }
    
                if (!invoiceCode.equals(queryNextInvoiceResponseExtend.getFpdm()) || !invoiceNum.equals(queryNextInvoiceResponseExtend.getFphm())) {
                    log.error("{}发票空白作废失败请求发票代码号码为空!", LOGGER_MSG);
                    return R.error(OrderInfoContentEnum.INVOICE_VALID_ERROR3);
                }
            }
            //组装数据库bean
            invalidInvoiceInfo = createInvalidInvoiceInfo(invoiceCode, invoiceNum, invoiceType, receviePoint, nsrsbh, xhfmc);
    
            KbZfRequest dbir = createDeprecateReq(invalidInvoiceInfo, kpjh);
    
            //如果百望的话需要查询相应的开票人 然后传入相应的开票人 update By ysy 目前由于 ukey和新税控都需要作废人 所以不区分税控全都传作废人
            DrawerInfoEntity queryDrawerInfo = invoiceService.queryDrawerInfo(nsrsbh, null);
            if (queryDrawerInfo == null) {
        
                //没有开票人的话，默认System作废
                dbir.setZFR("System");
            } else {
                dbir.setZFR(queryDrawerInfo.getDrawerName());
            }
    
    
            //作废请求bean组装
    
            KbZfResponseExtend response = HttpInvoiceRequestUtil.kbZfInvoice(OpenApiConfig.blankInvoiceZf, dbir, terminalCode);
            log.debug("空白发票作废返回数据:{}", response);
    
            if (!OrderInfoEnum.BLANK_INVOICES_CODE_050000.getKey().equals(response.getStatusCode())) {
                return R.error();
            }
    
    
            //将作废后的发票信息插入数据库
            boolean validInvoice = apiInvalidInvoiceService.validInvoice(invalidInvoiceInfo);
            if (!validInvoice) {
                return R.error();
            }
    
            /**
             *  发票作废推送
             */
            INVALID_INVOICES_RSP invalidInvoicesRsp = buildInvalidPushData(invalidInvoiceInfo);
            //作废推送数据存放队列
            this.invalidInvoice(invalidInvoicesRsp);
    
        }
    
    
        return R.ok();
    }
    
    /**
     * bean组装
     * @param xhfmc
     */
    private InvalidInvoiceInfo createInvalidInvoiceInfo(String invoiceCode, String invoiceNum, String invoiceType,
			String receviePoint, String nsrsbh, String xhfmc) {
        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
        invalidInvoiceInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        invalidInvoiceInfo.setZfpch(apiInvoiceCommonService.getGenerateShotKey());
        invalidInvoiceInfo.setFpdm(invoiceCode);
        invalidInvoiceInfo.setFphm(invoiceNum);
        invalidInvoiceInfo.setFplx(invoiceType);
        invalidInvoiceInfo.setSld(receviePoint);
        invalidInvoiceInfo.setCreateTime(new Date());
        invalidInvoiceInfo.setUpdateTime(new Date());
        invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
        invalidInvoiceInfo.setZfsj(new Date());
        invalidInvoiceInfo.setZfyy("作废原因");
        invalidInvoiceInfo.setZflx(OrderInfoEnum.ZFLX_0.getKey());
        invalidInvoiceInfo.setXhfNsrsbh(nsrsbh);
        invalidInvoiceInfo.setXhfmc(xhfmc);
        return invalidInvoiceInfo;
    }
    
    /**
     * 发票作废请求bean组装
     */
    private KbZfRequest createDeprecateReq(InvalidInvoiceInfo invalidInvoiceInfo, String kpjh) {
        KbZfRequest dbir = new KbZfRequest();
        dbir.setFP_DM(invalidInvoiceInfo.getFpdm());
        dbir.setFP_HM(invalidInvoiceInfo.getFphm());
        dbir.setFPLB(invalidInvoiceInfo.getFplx());
        dbir.setKPJH(kpjh);
        dbir.setSLDID(invalidInvoiceInfo.getSld());
        dbir.setNSRSBH(invalidInvoiceInfo.getXhfNsrsbh());
        //作废类型 0 空白发票作废
        dbir.setZFLX(OrderInfoEnum.ZFLX_0.getKey());
        //作废原因的先默认为作废
        dbir.setZFYY("作废");
        return dbir;
    }
    
    @Override
    public PageUtils queryByInvalidInvoice(Map paramMap,List<String> xhfNsrsbh) {

        return apiInvalidInvoiceService.queryByInvalidInvoice(paramMap,xhfNsrsbh);
    }
    
    /**
     * 已开发票作废
     */
    @Override
    public R batchValidInvoice(List<Map> orderIdArrays) throws OrderReceiveException {
        
        List<Object> errorMessageList = new ArrayList<>();
        
        
        //根据id查询要作废的发票信息
        int successCount = 0;
        for (Map map : orderIdArrays) {
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectInvoiceListByOrderId(id, shList);
            //数据校验
            R checkInvocieInfo = checkInvocieInfo(orderInvoiceInfo);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkInvocieInfo.get(OrderManagementConstant.CODE))) {
                //将错误的信息放到要返回的错误列表
                errorMessageList.add(checkInvocieInfo);
            } else {
                
                /**
                 * 根据税号获取维护的税控设备信息
                 */
                String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInvoiceInfo.getXhfNsrsbh());
            
                /**
                 * 作废信息组装
                 */
                InvalidInvoiceInfo invalidInvoiceInfo = orderInvoice2invalidInvoiceInfo(orderInvoiceInfo);
                //调用作废接口请求参数组装
                ZfRequest zfRequest = new ZfRequest();
                zfRequest.setNSRSBH(orderInvoiceInfo.getXhfNsrsbh());
                zfRequest.setFP_DM(invalidInvoiceInfo.getFpdm());
                zfRequest.setFP_QH(invalidInvoiceInfo.getFphm());
                zfRequest.setFP_ZH(invalidInvoiceInfo.getFphm());
                zfRequest.setZFLX(invalidInvoiceInfo.getZflx());
                zfRequest.setZFPCH(invalidInvoiceInfo.getZfpch());
                zfRequest.setZFYY(invalidInvoiceInfo.getZfyy());
                zfRequest.setSLDID(invalidInvoiceInfo.getSld());
    
                //如果百望的话需要查询相应的开票人 然后传入相应的开票人 update By ysy 目前由于 ukey和新税控都需要作废人 所以不区分税控全都传作废人
                DrawerInfoEntity queryDrawerInfo = invoiceService.queryDrawerInfo(orderInvoiceInfo.getXhfNsrsbh(), userInfoService.getUser().getUserId().toString());
                if (queryDrawerInfo == null) {
                		/* return R.ok().put(OrderManaementConstant.CODE, OrderInfoContentEnum.INVOICE_VALID_REPEAT.getKey())
                				 .put(OrderManaementConstant.MESSAGE,"发票作废开票人不能为空!");*/
                    //没有开票人的话，默认System作废
                    zfRequest.setZFR("System");
                } else {
                    zfRequest.setZFR(queryDrawerInfo.getDrawerName());
                }
    
                if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                    //更新数据库为待作废
                    //作废标志设置为作废中
                    invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
                    if ("0".equals(orderInvoiceInfo.getKplx())) {
                        //蓝
                        //正数发票
                        invalidInvoiceInfo.setZflx(ConfigureConstant.STRING_1);
                    } else if ("1".equals(orderInvoiceInfo.getKplx())) {
                        //红
                        //负数发票
                        invalidInvoiceInfo.setZflx(ConfigureConstant.STRING_2);
                    }
                    invalidInvoiceInfo.setZfr(zfRequest.getZFR());
                    invalidInvoiceInfo.setZfpch(zfRequest.getZFPCH());

                    InvalidInvoiceInfo invalidInvoiceInfoReq = new InvalidInvoiceInfo();
                    invalidInvoiceInfoReq.setFpdm(orderInvoiceInfo.getFpdm());
                    invalidInvoiceInfoReq.setFphm(orderInvoiceInfo.getFphm());

                    InvalidInvoiceInfo selectByInvalidInvoiceInfo = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(invalidInvoiceInfoReq, shList);
                    if (ObjectUtil.isEmpty(selectByInvalidInvoiceInfo)) {

                        //将作废的信息插入作废表
                        boolean insert = apiInvalidInvoiceService.validInvoice(invalidInvoiceInfo);
                        if (!insert) {
                            log.error("发票作废数据插入作废表失败");
                        }
                    } else {
                        invalidInvoiceInfo.setZfpch(selectByInvalidInvoiceInfo.getZfpch());
                        invalidInvoiceInfo.setId(selectByInvalidInvoiceInfo.getId());
                        apiInvalidInvoiceService.updateFgInvalidInvoice(invalidInvoiceInfo, shList);
                    }

                    OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
                    orderInvoiceInfo1.setId(orderInvoiceInfo.getId());
                    //修改作废标志为作废中
                    orderInvoiceInfo1.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
                    int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo1, shList);
                    if (updateByPrimaryKeySelect <= 0) {
                        log.error("更新发票表作废标志失败");
                    }
                    /**
                     * 通知方格获取待作废消息
                     */
                    String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(orderInvoiceInfo.getXhfNsrsbh(), orderInvoiceInfo.getJqbh());
                    if (StringUtils.isNotEmpty(registCodeStr)) {
                        RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                        /**
                         * 存放作废信息到redis队列
                         */
                        PushPayload pushPayload = new PushPayload();
                        //已开发票作废
                        pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_4);
                        pushPayload.setNSRSBH(registrationCode.getXhfNsrsbh());
                        pushPayload.setJQBH(registrationCode.getJqbh());
                        pushPayload.setZCM(registrationCode.getZcm());
                        pushPayload.setZFPCH(invalidInvoiceInfo.getZfpch());
                        apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                        successCount++;
                    }
                } else {
                    //请求作废接口
                    DEPRECATE_INVOICES_RSP deprecateInvoices = HttpInvoiceRequestUtil.zfInvoice(OpenApiConfig.ykfpzf, zfRequest, terminalCode);
        
        
                    INVALID_INVOICES_RSP invalidInvoicesRsp = buildInvalidPushData(invalidInvoiceInfo);
        
                    /**
                     * 由于是单张作废,因此只能是全部作废成功或者是失败
                     */
                    //根据作废接口返回数据更新数据库
                    if (!OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey().equals(deprecateInvoices.getSTATUS_CODE())) {
            
            
                        if (deprecateInvoices.getDeprecate_failed_invoice() != null && deprecateInvoices.getDeprecate_failed_invoice().length > 0
                                && "108016".equals(deprecateInvoices.getDeprecate_failed_invoice()[0].getSTATUS_CODE()) && StringUtils.isNotBlank(deprecateInvoices.getDeprecate_failed_invoice()[0].getSTATUS_MESSAGE())
                                && deprecateInvoices.getDeprecate_failed_invoice()[0].getSTATUS_MESSAGE().contains("重复作废")) {
                
                            processSuccessInvalid(invalidInvoiceInfo, orderInvoiceInfo, shList);
                
                            //作废推送数据存放队列
                            this.invalidInvoice(invalidInvoicesRsp);
                            successCount++;
                
                
                        } else {
                            /**
                             * 返回详细的作废信息
                             */
                            String zfCode = deprecateInvoices.getSTATUS_CODE();
                            String zfMsg = deprecateInvoices.getSTATUS_MESSAGE();
                            if (deprecateInvoices.getDeprecate_failed_invoice() != null && deprecateInvoices.getDeprecate_failed_invoice().length > 0) {
                                zfCode = deprecateInvoices.getDeprecate_failed_invoice()[0].getSTATUS_CODE();
                                zfMsg = deprecateInvoices.getDeprecate_failed_invoice()[0].getSTATUS_MESSAGE();
                            }
                            R r = R.error().put(OrderManagementConstant.CODE, zfCode)
                                    .put(OrderManagementConstant.MESSAGE, zfMsg)
                                    .put("fpdm", orderInvoiceInfo.getFpdm()).put("fphm", orderInvoiceInfo.getFphm());
                            errorMessageList.add(r);
                            invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_3.getKey());
                        }
            
                    } else {
            
                        processSuccessInvalid(invalidInvoiceInfo, orderInvoiceInfo, shList);
            
                        //作废推送数据存放队列
                        this.invalidInvoice(invalidInvoicesRsp);
                        successCount++;
                    }
                }
    
            }
        }
    
    
        //返回前端发票作废结果
        if (successCount == 0) {
            return R.error().put(OrderManagementConstant.ERRORMESSAGE, errorMessageList);
        } else {
            return R.ok().put(OrderManagementConstant.ERRORMESSAGE, errorMessageList);
        }
    
    
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
    
    /**
     * 作废成功后更新数据
     *
     * @param invalidInvoiceInfo
     * @param orderInvoiceInfo
     * @throws OrderReceiveException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processSuccessInvalid(InvalidInvoiceInfo invalidInvoiceInfo, OrderInvoiceInfo orderInvoiceInfo, List<String> shList) throws OrderReceiveException {
        
        InvalidInvoiceInfo query = new InvalidInvoiceInfo();
        query.setFpdm(invalidInvoiceInfo.getFpdm());
        query.setFphm(invalidInvoiceInfo.getFphm());
        
        //查询发票作废表是否存在数据
        InvalidInvoiceInfo invalidInvoiceInfo1 = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(query, shList);
        if (invalidInvoiceInfo1 == null) {
            invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
            boolean insert = apiInvalidInvoiceService.validInvoice(invalidInvoiceInfo);
            if (!insert) {
                log.error("发票作废数据插入作废表失败");
                throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_VALID_ERROR);
            }
        } else {
            invalidInvoiceInfo.setZfpch(invalidInvoiceInfo1.getZfpch());
            invalidInvoiceInfo.setId(invalidInvoiceInfo1.getId());
            invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
            apiInvalidInvoiceService.updateFgInvalidInvoice(invalidInvoiceInfo, shList);
        }
        
        OrderInvoiceInfo invoice = new OrderInvoiceInfo();
        invoice.setId(orderInvoiceInfo.getId());
        invoice.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
        invoice.setZfsj(invalidInvoiceInfo.getZfsj());
        invoice.setZfyy(invalidInvoiceInfo.getZfyy());
        int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(invoice, shList);
        if (updateByPrimaryKeySelect <= 0) {
            log.error("更新发票表作废标志失败");
            throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_VALID_ERROR1);
        }
    
        //判断作废的发票是否是红票
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
            //更新发票表的作废状态和剩余可冲红金额
            updateSykchJe(orderInvoiceInfo, shList);
        }
    
    
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fgProcessSuccessInvalid(InvalidInvoiceInfo invalidInvoiceInfo, OrderInvoiceInfo orderInvoiceInfo, List<String> shList) throws
            OrderReceiveException {
        InvalidInvoiceInfo query = new InvalidInvoiceInfo();
        query.setFpdm(invalidInvoiceInfo.getFpdm());
        query.setFphm(invalidInvoiceInfo.getFphm());
        
        /**
         * 方格修改  因为是异步作废，可能中间流程断了，需要重新发起作废
         */
        //查询发票作废表是否存在数据
        InvalidInvoiceInfo invalidInvoiceInfo1 = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(query, shList);
        if (invalidInvoiceInfo1 == null) {
            invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_1.getKey());
            boolean insert = apiInvalidInvoiceService.validInvoice(invalidInvoiceInfo);
            if (!insert) {
                log.error("发票作废数据插入作废表失败");
                throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_VALID_ERROR);
            }
        } else {
            query.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
            query.setZfpch(invalidInvoiceInfo1.getZfpch());
            apiInvalidInvoiceService.updateFgInvalidInvoice(query, shList);
        }
        
        OrderInvoiceInfo invoice = new OrderInvoiceInfo();
        invoice.setId(orderInvoiceInfo.getId());
        invoice.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
        int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(invoice, shList);
        if (updateByPrimaryKeySelect <= 0) {
            log.error("更新发票表作废标志失败");
            throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_VALID_ERROR1);
        }
        
        
    }
    
    /**
     * 空白发票作废完成之后保存作废信息
     */
    @Override
    public R voidInvalidInvoiceActiveX(String fpdm, String fphm, String zfzt, String sldid, String nsrsbh, String fpzldm) {
        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
        invalidInvoiceInfo.setFpdm(fpdm);
        invalidInvoiceInfo.setFphm(fphm);
    
        List<String> shList = new ArrayList<>();
        shList.add(nsrsbh);
        //查询发票作废表是否存在数据
        InvalidInvoiceInfo selectByInvalidInvoiceInfo = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(invalidInvoiceInfo, shList);
        if (selectByInvalidInvoiceInfo != null) {
            //如果发票作废表 存在当前数据 说明 发票重复作废
            return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_VALID_REPEAT.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_VALID_REPEAT.getMessage());
        }
    
        //查询数据库是否存在此发票信息
        OrderInvoiceInfo selectByYfp = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(fpdm, fphm, shList);
        log.debug("{}根据发票代码，发票号码从数据库中查出的发票信息：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(selectByYfp));
        if (selectByYfp != null) {
            R checkInvocieInfo = checkInvocieInfo(selectByYfp);
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkInvocieInfo.get(OrderManagementConstant.CODE))) {
                return checkInvocieInfo;
            }
        }
        if (ConfigureConstant.STRING_0.equals(zfzt)) {
            //作废成功
            //组装数据库bean
            InvalidInvoiceInfo info = createInvalidInvoiceInfo(fpdm, fphm, fpzldm, sldid, nsrsbh, "");
            //将作废后的发票信息插入数据库
            boolean validInvoice = apiInvalidInvoiceService.validInvoice(info);
            if (!validInvoice) {
                return R.error();
            }
        }
        return R.ok();
    }
    
    /**
     * 手动推送作废发票状态
     */
    @Override
    public R manualPushInvalidInvoice(List<Map> orderIdArrays) {
        //根据id查询要作废的发票信息
        
        
        for (Map map : orderIdArrays) {
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectInvoiceListByOrderId(id, shList);
            InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
            invalidInvoiceInfo.setFpdm(orderInvoiceInfo.getFpdm());
            invalidInvoiceInfo.setFphm(orderInvoiceInfo.getFphm());
            //查询发票作废表是否存在数据
            InvalidInvoiceInfo invalidInvoiceInfoData = apiInvalidInvoiceService.selectByInvalidInvoiceInfo(invalidInvoiceInfo, shList);
            if (ObjectUtils.isEmpty(invalidInvoiceInfoData)) {
                log.error("发票没有作废,不能推送");
                continue;
            } else {
                
                /**
                 *  发票作废推送
                 */
                INVALID_INVOICES_RSP invalidInvoicesRsp = buildInvalidPushData(invalidInvoiceInfoData);
                //作废推送数据存放队列
                this.invalidInvoice(invalidInvoicesRsp);
            }
    
    
        }
    
        return R.ok();
    }
    
    private void invalidInvoice(INVALID_INVOICES_RSP invalidInvoicesRsp) {
        String jsonString = JsonUtils.getInstance().toJsonString(invalidInvoicesRsp);
        log.info("作废发票数据信息{}", jsonString);
        //推送数据存入队列
        apiInvalidInvoiceService.invalidInvoice(jsonString, invalidInvoicesRsp.getNSRSBH());
    }
    
    @Override
    public void updateSykchJe(OrderInvoiceInfo orderInvoiceInfo, List<String> shList) {
        OrderInvoiceInfo updateInvoiceInfo = new OrderInvoiceInfo();
        //获取冲红订单
        OrderInfo orderInfo = apiOrderInfoService.selectOrderInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
        if (orderInfo != null && StringUtils.isNotBlank(orderInfo.getYfpHm()) && StringUtils.isNotBlank(orderInfo.getYfpDm())) {
            
            //查询蓝票
            OrderInvoiceInfo blueInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(orderInfo.getYfpDm(), orderInfo.getYfpHm(), shList);
            if (blueInvoiceInfo != null) {
                
                String sykchje = blueInvoiceInfo.getSykchje();
                log.debug("原蓝票剩余可冲红金额:{},价税合计:{}", sykchje, blueInvoiceInfo.getKphjje());
            
                if (StringUtils.isBlank(sykchje)) {
                    sykchje = blueInvoiceInfo.getKphjje();
                }
    
                // 剩余可冲红金额等于 上次剩余可冲红金额减去本次红票金额(因为剩余可充红金额为整数,当前发票数据为红票数据,金额为负,并且本次操作为作废,所以应该相减)
                sykchje = new BigDecimal(sykchje).subtract(new BigDecimal(orderInfo.getKphjje()))
                        .setScale(2, RoundingMode.HALF_UP).toString();
            
                if (Double.parseDouble(sykchje) < 0) {
                    log.error("{}冲红失败:orderId:{}", LOGGER_MSG, orderInfo.getId());
                }
            
                /**
                 * 补全冲红标志
                 */
                if (StringUtils.isBlank(blueInvoiceInfo.getChBz()) || OrderInfoEnum.RED_INVOICE_0.getKey().equals(blueInvoiceInfo.getChBz())) {
                    if (Double.parseDouble(sykchje) <= 0) {
                        blueInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_1.getKey());
                    } else {
                        blueInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_4.getKey());
                    }
                
                }
    
                /**
                 * 根据冲红标志判断需要更改后的冲红标志,
                 * 如果是全部冲红修改为正常,
                 * 如果是部分冲红修改为部分冲红或者是正常,根据剩余可冲红金额判断
                 */
                if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(blueInvoiceInfo.getChBz())) {
                    //未冲红
                    updateInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_0.getKey());
                } else if (OrderInfoEnum.RED_INVOICE_4.getKey().equals(blueInvoiceInfo.getChBz())) {
                    //部分冲红成功,默认冲红标志为部分冲红成功,如果蓝票剩余可充红金额等于计算后的剩余可充红金额 ,修改为正常
                    updateInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_4.getKey());
                    if (sykchje.equals(blueInvoiceInfo.getKphjje())) {
                        updateInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_0.getKey());
                    }
        
                }
                updateInvoiceInfo.setSykchje(sykchje);
                
                //更新发票表中的剩余可冲红金额
                updateInvoiceInfo.setId(blueInvoiceInfo.getId());
                int updateByPrimaryKeySelect = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(updateInvoiceInfo, shList);
                if (updateByPrimaryKeySelect <= 0) {
                    log.error("更新原蓝票剩余可冲红金额失败，{}", blueInvoiceInfo.getId());
                }
            }
    
        }
        
        // 红字专票 作废后修改申请单的状态为未开票
		if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getFpzlDm())
				&& OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
			if (StringUtils.isNotBlank(orderInvoiceInfo.getHzxxbbh())) {
				SpecialInvoiceReversalEntity selectSpecialInvoiceReversalByCode = apiSpecialInvoiceReversalService
                        .selectSpecialInvoiceReversalBySubmitCode(orderInvoiceInfo.getHzxxbbh());
                SpecialInvoiceReversalEntity updateSpecialReversal = new SpecialInvoiceReversalEntity();
				updateSpecialReversal.setId(selectSpecialInvoiceReversalByCode.getId());
				updateSpecialReversal.setKpzt("0");
				int updateSpecialInvoiceReversal = apiSpecialInvoiceReversalService
						.updateSpecialInvoiceReversal(updateSpecialReversal);
				if (updateSpecialInvoiceReversal <= 0) {
					log.error("更新红字信息表失败!,申请单编号:{}", orderInvoiceInfo.getHzxxbbh());
				}
			}
		}
    }
    
    private InvalidInvoiceInfo orderInvoice2invalidInvoiceInfo(OrderInvoiceInfo orderInvoiceInfo) {
        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
        invalidInvoiceInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        invalidInvoiceInfo.setZfpch(apiInvoiceCommonService.getGenerateShotKey());
        invalidInvoiceInfo.setFpdm(orderInvoiceInfo.getFpdm());
        invalidInvoiceInfo.setFphm(orderInvoiceInfo.getFphm());
        invalidInvoiceInfo.setSld(orderInvoiceInfo.getSld());
        invalidInvoiceInfo.setZfyy("作废");
        invalidInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_2.getKey());
        invalidInvoiceInfo.setFplx(orderInvoiceInfo.getFpzlDm());
        invalidInvoiceInfo.setZfsj(new Date());
        invalidInvoiceInfo.setUpdateTime(new Date());
        invalidInvoiceInfo.setCreateTime(new Date());
        //正数发票作废
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
            invalidInvoiceInfo.setZflx(OrderInfoEnum.ZFLX_1.getKey());
        } else {
            invalidInvoiceInfo.setZflx(OrderInfoEnum.ZFLX_2.getKey());
        }
        invalidInvoiceInfo.setXhfNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
        invalidInvoiceInfo.setXhfmc(orderInvoiceInfo.getXhfMc());
        return invalidInvoiceInfo;
    }
	
    /**
     * 作废发票的数据校验
     */
    private R checkInvocieInfo(OrderInvoiceInfo selectByYfp) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        calendar.set(Calendar.MINUTE, 0);
        //将秒至0
        calendar.set(Calendar.SECOND, 0);
        //将毫秒至0
        calendar.set(Calendar.MILLISECOND, 0);
        //获得当前月第一天
        Date sdate = calendar.getTime();
        /**
         * 判断发票种类代码是否正确
         */
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(selectByYfp.getFpzlDm()) && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(selectByYfp.getFpzlDm())) {
            return R.error(OrderInfoContentEnum.INVOICE_VALID_ERROR5)
                    .put("fpdm", selectByYfp.getFpdm()).put("fphm", selectByYfp.getFphm());
        } else if (!selectByYfp.getKprq().after(sdate)) {
            log.warn("作废发票只能为本月发票");
            return R.error(OrderInfoContentEnum.INVOICE_VALID_ERROR4)
                    .put("fpdm", selectByYfp.getFpdm()).put("fphm", selectByYfp.getFphm());
        }
    
        return R.ok();
    }

    
	@Override
	public PageUtils queryKbInvoiceList(Map<String, Object> paramMap,List<String> xhfNsrsbh) {
		return apiInvalidInvoiceService.queryKbInvoiceList(paramMap,xhfNsrsbh);
	}

}
