package com.dxhy.order.consumer.openapi.service.impl;


import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.openapi.service.IDynamicCodeInterfaceServiceV3;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.protocol.order.DYNAMIC_CODE_RSP;
import com.dxhy.order.protocol.v4.order.DDTXX;
import com.dxhy.order.protocol.v4.order.DDZXX;
import com.dxhy.order.protocol.v4.order.EWM_RSP;
import com.dxhy.order.utils.CommonUtils;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

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
public class DynamicCodeInterfaceServiceImplV3 implements IDynamicCodeInterfaceServiceV3 {
    
    private static final String LOGGER_MSG = "(订单对外接口业务类V3)";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Reference
    private ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;
    
    @Resource
    private ICommonInterfaceService iCommonInterfaceService;
    
    @Reference
    private ApiQuickCodeInfoService apiQuickCodeInfoService;
    
    @Reference
    private ApiVerifyOrderInfo apiVerifyOrderInfo;
    
    @Reference
    private ApiOrderQrcodeExtendService apiOrderQrcodeExtendService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private RedisService redisService;
    
    
    @Override
    public EWM_RSP getDynamicCode(DDZXX ddzxx) {
        
        EWM_RSP response = new EWM_RSP();
    
        // 校验数据信息
        Map<String, String> verifyDynamicOrderInfo = apiVerifyOrderInfo.verifyDynamicOrderInfo(ddzxx);
        if (!OrderInfoContentEnum.SUCCESS.getKey()
                .equals(verifyDynamicOrderInfo.get(OrderManagementConstant.ERRORCODE))) {
            log.error("动态码生成，订单信息校验失败，请求流水号:{}", ddzxx.getDDTXX().getDDQQLSH());
            response.setZTDM(verifyDynamicOrderInfo.get(OrderManagementConstant.ERRORCODE));
            response.setZTXX(verifyDynamicOrderInfo.get(OrderManagementConstant.ERRORMESSAGE));
            return response;
        }
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(ddzxx.getDDTXX().getXHFSBH());
        OrderQrcodeExtendInfo orderQrcodeExtendInfo = apiOrderQrcodeExtendService.queryQrCodeDetailByDdqqlshAndNsrsbh(ddzxx.getDDTXX().getDDQQLSH(), shList);
    
        if (orderQrcodeExtendInfo != null && StringUtils.isNotBlank(orderQrcodeExtendInfo.getQuickResponseCodeUrl())) {
            log.warn("动态码生成，数据已经存在,直接返回，请求流水号:{}", ddzxx.getDDTXX().getDDQQLSH());
            response.setSXSJ(DateUtils.format(orderQrcodeExtendInfo.getQuickResponseCodeValidTime(), "yyyy-MM-dd HH:mm:ss"));
            response.setDTM(String.format(OpenApiConfig.qrCodeShortUrl, orderQrcodeExtendInfo.getTqm()));
            response.setTQM(orderQrcodeExtendInfo.getTqm());
            response.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010002_V3.getKey());
            response.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010002_V3.getMessage());
            return response;
        }
    
    
        /**
         * 种类代码转换
         */
        String fplb = CommonUtils.transFpzldm(ddzxx.getDDTXX().getFPLXDM());
    
        ddzxx.getDDTXX().setFPLXDM(fplb);
        // 获取销方税号下的配置信息
        Map<String, Object> paramMap = new HashMap<>(5);
        paramMap.put("xhfNsrsbh", ddzxx.getDDTXX().getXHFSBH());
        EwmConfigInfo queryEwmConfigInfo = apiQuickCodeInfoService.queryEwmConfigInfo(paramMap);
        List<EwmConfigItemInfo> queryEwmConfigItemInfoById = new ArrayList<>();
        if (queryEwmConfigInfo != null) {
            queryEwmConfigItemInfoById = apiQuickCodeInfoService.queryEwmConfigItemInfoById(queryEwmConfigInfo.getId());
        }

        //收集业务类型信息
    
        // 二维码信息保存到订单信息中
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo orderInfo = com.dxhy.order.utils.BeanTransitionUtils
                .transitionOrderInfoV3(ddzxx.getDDTXX());
        List<OrderItemInfo> orderItemInfos = com.dxhy.order.utils.BeanTransitionUtils
                .transitionOrderItemInfoV3(ddzxx.getDDMXXX(), orderInfo.getXhfNsrsbh());
        orderInfo.setYwlx(ddzxx.getDDTXX().getYWLX());

        if (StringUtils.isNotBlank(ddzxx.getDDTXX().getYWLX())) {
            //如果接口传递业务类型不为空，走业务类型采集流程
            log.info("{} 业务类型采集，业务类型名称：{}，税号：{},销货方名称：{}", LOGGER_MSG, ddzxx.getDDTXX().getYWLX(), ddzxx.getDDTXX().getXHFSBH(),  ddzxx.getDDTXX().getXHFMC());
            String ywlxId = iCommonInterfaceService.yesxInfoCollect(ddzxx.getDDTXX().getYWLX(), ddzxx.getDDTXX().getXHFSBH(), ddzxx.getDDTXX().getXHFMC());
            orderInfo.setYwlxId(ywlxId);
        }


        //默认开票项目为明细行首行数据
        orderInfo.setKpxm(orderItemInfos.get(0).getXmmc());
        orderInfo.setFpzlDm(fplb);
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);
        
        //如果用户配置的发票种类代码不为空 默认使用用户配置的发票种类代码 如果用户没有配置发票种类代码默认电票
        for (EwmConfigItemInfo item : queryEwmConfigItemInfoById) {
            
            if (StringUtils.isBlank(orderInfo.getFpzlDm())) {
                orderInfo.setFpzlDm(fplb);
            }
            if (item.getFpzldm().equals(ddzxx.getDDTXX().getFPLXDM())) {
                orderInfo.setSld(item.getSld());
                orderInfo.setSldMc(item.getSldMc());
            }
        }
        if (StringUtils.isBlank(orderInfo.getFpzlDm())) {
            orderInfo.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        }
    
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
         * 判断是否需要补全销方信息
         * todo 后期优化,需要添加销方缓存,目前大部分都是单批次单个发票
         */
        if (StringUtils.isBlank(ddzxx.getDDTXX().getXHFDZ()) || StringUtils.isBlank(ddzxx.getDDTXX().getXHFYH())) {
            DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityFromUrl(ddzxx.getDDTXX().getXHFSBH(), ddzxx.getDDTXX().getXHFMC());
            if (sysDeptEntity == null) {
                log.error("动态码生成，订单信息校验失败，请求流水号:{},销售信息不全!", ddzxx.getDDTXX().getDDQQLSH());
                response.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_010008_V3.getKey());
                response.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_010008_V3.getMessage());
                return response;
            } else {

                BeanTransitionUtils.transitionOrderSellerInfo(commonOrderInfo, sysDeptEntity);
            }

        }
    
        OrderProcessInfo processInfo = new OrderProcessInfo();
        OrderInvoiceInfo invoiceInfo = new OrderInvoiceInfo();
    
        iCommonInterfaceService.buildInsertOrderData(orderInfo, orderItemInfos, processInfo, invoiceInfo);
        // 订单处理表保存开票方式 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票)
        processInfo.setKpfs("3");
        processInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
        processInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_6.getKey());
    
        List<OrderOriginExtendInfo> orderOriginList = new ArrayList<>();
        List<OrderInfo> insertOrder = new ArrayList<>();
        List<OrderProcessInfo> insertProcessInfo = new ArrayList<>();
        List<List<OrderItemInfo>> orderItemList = new ArrayList<>();
    
        // 插入原始订单关系表
        OrderOriginExtendInfo orderOrginOrder = new OrderOriginExtendInfo();
        orderOrginOrder.setCreateTime(new Date());
        orderOrginOrder.setUpdateTime(new Date());
        orderOrginOrder.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderOrginOrder.setOrderId(orderInfo.getId());
        orderOrginOrder.setFpqqlsh(orderInfo.getFpqqlsh());
        orderOrginOrder.setOriginFpqqlsh(orderInfo.getFpqqlsh());
        orderOrginOrder.setOriginOrderId(orderInfo.getId());
        orderOrginOrder.setOriginDdh(orderInfo.getDdh());
        orderOrginOrder.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        
        
        // 保存二维码扩展表
        OrderQrcodeExtendInfo commonOrderToQrCodeInfo = commonOrderToQrCodeInfo(ddzxx.getDDTXX());
        orderInfo.setTqm(commonOrderToQrCodeInfo.getTqm());
        processInfo.setTqm(commonOrderToQrCodeInfo.getTqm());
        orderOriginList.add(orderOrginOrder);
        insertOrder.add(orderInfo);
        insertProcessInfo.add(processInfo);
        orderItemList.add(orderItemInfos);
    
        apiInvoiceCommonService.saveData(insertOrder, orderItemList, insertProcessInfo, null, null,
                null, orderOriginList, shList);

        // 计算失效时间
        int invalidDays = 30;
        if (queryEwmConfigInfo != null) {
            invalidDays = StringUtils.isBlank(queryEwmConfigInfo.getInvalidTime()) ? 30
                    : Integer.parseInt(queryEwmConfigInfo.getInvalidTime());
        }
        //订单没有过期时间，永不失效 数据库中过期时间设置为2099 01 01 00：00：00
        if(invalidDays == 0){
        	Date validDate = DateUtil.parse("2099-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss");
        	commonOrderToQrCodeInfo.setQuickResponseCodeValidTime(validDate);
        }else{
        	 Date validDate = DateUtils.addDateDays(orderInfo.getDdrq(), invalidDays);
        	 commonOrderToQrCodeInfo.setQuickResponseCodeValidTime(validDate);
        }
        
        commonOrderToQrCodeInfo.setOrderInfoId(orderInfo.getId());
        boolean isSuccess = apiOrderQrcodeExtendService.saveQrcodeInfo(commonOrderToQrCodeInfo);
        if (!isSuccess) {
            log.error("动态码生成，保存数据库失败,订单请求流水号:{}", ddzxx.getDDTXX().getDDQQLSH());
            response.setZTDM("001999");
            response.setZTXX("动态码生成失败!");
            return response;
        }
        
        response.setDTM(String.format(OpenApiConfig.qrCodeShortUrl, commonOrderToQrCodeInfo.getTqm()));
        response.setTQM(commonOrderToQrCodeInfo.getTqm());
        response.setSXSJ(DateUtil.format(commonOrderToQrCodeInfo.getQuickResponseCodeValidTime(), "yyyy-MM-dd HH:mm:ss"));
        response.setZTDM("000000");
        response.setZTXX("动态码生成成功");
        return response;
    }
    
    @Override
    public DYNAMIC_CODE_RSP getEwmUrlByTqm(String tqm) {
    
        DYNAMIC_CODE_RSP response = new DYNAMIC_CODE_RSP();
    
        /**
         * 查询
         */
        /**
         * todo ,业务系统自己维护,支持mycat操作
         * 本次新增mycat查询,根据销方税号做分片规则,所以需要底层返回销方税号,如果返回为空不进行操作
         */
        /**
         * todo 为了满足mycat使用,从redis中读取销方税号,如果读取为空,全库查询后存到缓存.
         *
         */
        List<String> shList = null;
        String cacheTqm = String.format(Constant.REDIS_TQM, tqm);
        String xhfNsrsbh = redisService.get(cacheTqm);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            QuickResponseCodeInfo quickResponseCodeInfo = apiQuickCodeInfoService.queryQrCodeDetailByTqm(tqm, null, null);
            if (quickResponseCodeInfo != null && StringUtils.isNotBlank(quickResponseCodeInfo.getXhfNsrsbh())) {
        
                redisService.set(cacheTqm, quickResponseCodeInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                xhfNsrsbh = quickResponseCodeInfo.getXhfNsrsbh();
                shList = NsrsbhUtils.transShListByNsrsbh(xhfNsrsbh);
            }
        }
    
        QuickResponseCodeInfo quickResponseCodeInfo = apiQuickCodeInfoService.queryQrCodeDetailByTqm(tqm, shList, null);
    
        if (quickResponseCodeInfo == null) {
    
            OrderQrcodeExtendInfo orderQrcodeExtendInfo = apiOrderQrcodeExtendService.queryQrCodeDetailByTqm(tqm, shList, null);
            if (orderQrcodeExtendInfo != null && StringUtils.isNotBlank(orderQrcodeExtendInfo.getXhfNsrsbh())) {
    
                redisService.set(cacheTqm, orderQrcodeExtendInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
            }
    
            if (orderQrcodeExtendInfo != null) {
                //查询根据税号配置的appid
                EwmGzhConfig config = apiQuickCodeInfoService.queryGzhEwmConfig(orderQrcodeExtendInfo.getXhfNsrsbh());
                String appid = (config == null || StringUtils.isBlank(config.getAppid())) ? SystemConfig.appid : config.getAppid();
                response.setDISABLED_TIME(DateUtils.format(orderQrcodeExtendInfo.getQuickResponseCodeValidTime(), "yyyy-MM-dd HH:mm:ss"));
                response.setDYNAMIC_CODE_URL(orderQrcodeExtendInfo.getQuickResponseCodeUrl() + "&appid=" + appid);
                response.setTQM(orderQrcodeExtendInfo.getTqm());
                response.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getKey());
                response.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getMessage());
                return response;

            } else {
                log.error("动态码数据获取失败，提取码:{}", tqm);
                response.setSTATUS_CODE(OrderInfoContentEnum.EWM_ERROR_CODE_205998.getKey());
                response.setSTATUS_MESSAGE(OrderInfoContentEnum.EWM_ERROR_CODE_205998.getMessage());
                return response;
            }

        }else{
            log.debug("动态码数据已经存在,直接返回，tqm:{},二维码:{}", tqm, quickResponseCodeInfo.getQuickResponseCodeUrl());
                     EwmGzhConfig config = apiQuickCodeInfoService.queryGzhEwmConfig(quickResponseCodeInfo.getXhfNsrsbh());
            String appid = (config == null || StringUtils.isBlank(config.getAppid())) ? SystemConfig.appid : config.getAppid();
            response.setDISABLED_TIME(DateUtils.format(quickResponseCodeInfo.getQuickResponseCodeValidTime(), "yyyy-MM-dd HH:mm:ss"));
            response.setDYNAMIC_CODE_URL(quickResponseCodeInfo.getQuickResponseCodeUrl() + "&appid=" + appid);
            response.setTQM(quickResponseCodeInfo.getTqm());
            response.setSTATUS_CODE(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getKey());
            response.setSTATUS_MESSAGE(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getMessage());
            return response;

        }
        
    }
    
    
    /**
     * bean转换
     */
    private OrderQrcodeExtendInfo commonOrderToQrCodeInfo(DDTXX orderInfo) {
        String tqm = apiInvoiceCommonService.getGenerateShotKey();
        String qrcodeUrl = String.format(OpenApiConfig.qrCodeScanUrl, tqm, orderInfo.getXHFSBH(), ConfigureConstant.STRING_1);
    
        Date now = new Date();
        OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
        orderQrcodeExtendInfo.setCreateTime(now);
        orderQrcodeExtendInfo.setFpqqlsh(orderInfo.getDDQQLSH());
        orderQrcodeExtendInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        orderQrcodeExtendInfo.setQuickResponseCodeType("1");
        orderQrcodeExtendInfo.setQuickResponseCodeUrl(qrcodeUrl);
        orderQrcodeExtendInfo.setTqm(tqm);
        orderQrcodeExtendInfo.setUpdateTime(now);
        orderQrcodeExtendInfo.setXhfMc(orderInfo.getXHFMC());
        orderQrcodeExtendInfo.setXhfNsrsbh(orderInfo.getXHFSBH());
        orderQrcodeExtendInfo.setKphjje(orderInfo.getJSHJ());
        orderQrcodeExtendInfo.setDdh(orderInfo.getDDH());
        orderQrcodeExtendInfo.setEwmzt("0");
        orderQrcodeExtendInfo.setFpzlDm(orderInfo.getFPLXDM());
        orderQrcodeExtendInfo.setZfzt("0");
        orderQrcodeExtendInfo.setCardStatus("0");
        orderQrcodeExtendInfo.setDataStatus("0");
        //发票种类代码 稍后添加
        return orderQrcodeExtendInfo;
    }
}
