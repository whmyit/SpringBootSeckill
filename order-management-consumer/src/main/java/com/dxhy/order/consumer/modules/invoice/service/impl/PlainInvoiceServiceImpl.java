package com.dxhy.order.consumer.modules.invoice.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.invoice.service.PlainInvoiceService;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.dy.DyRequest;
import com.dxhy.order.model.a9.dy.DyRequestExtend;
import com.dxhy.order.model.a9.dy.DyResponse;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.PrintEntity;
import com.dxhy.order.model.fg.FgkpSkDyjEntity;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 描述信息：纸质发票Service实现类
 *
 * @author 谢元强
 * @date Created on 2018-08-17
 */
@Service
@Slf4j
public class PlainInvoiceServiceImpl implements PlainInvoiceService {


    private static final String LOGGER_MSG = "纸质发票实现类";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;

    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Reference
    private ApiOrderProcessService apiOrderProcessService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;




    /**
     * @param printEntity 发票打印类型
     * @return png文件的base64编码
     * @Description 打印纸票发票或纸票清单
     * @Author xieyuanqiang
     * @Date 11:02 2018-08-02
     */
    @Override
    public DyResponse printInvoice(PrintEntity printEntity) throws OrderReceiveException {
        log.info("{}打印接口参数：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(printEntity));

        DyRequest requset = new DyRequest();
        requset.setDyjId(printEntity.getPrintId());
    
        requset.setDypch(apiInvoiceCommonService.getGenerateShotKey() + DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        requset.setZbj(printEntity.getZbj());
        requset.setSbj(printEntity.getSbj());
        requset.setSpotKey(printEntity.getSpotKey());
    
        List<DyRequestExtend> invoicePrintPackageDetailList = new ArrayList<>();
    
        String xhfNsrsbh = "";
        String jqbh = "";
        int i = 0;
    
        for (Map map : printEntity.getIds()) {
        
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
            orderInvoiceInfo.setId(id);
            OrderInvoiceInfo orderInvoiceInfo1 = apiOrderInvoiceInfoService.selectOrderInvoiceInfo(orderInvoiceInfo, shList);
            if (i == 0) {
                xhfNsrsbh = orderInvoiceInfo1.getXhfNsrsbh();
                jqbh = orderInvoiceInfo1.getJqbh();
            } else {
                if (!xhfNsrsbh.equals(orderInvoiceInfo1.getXhfNsrsbh())) {
                    log.warn("同一打印批次中只能选择同一税号");
                    throw new OrderReceiveException(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), "同一批次只能选择同一税号");
                }
            }
            /**
             * 根据纳税人识别号获取维护的税控设备信息,税号规则不一样,需要获取两次税号
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
            /**
             * 已作废发票不支持打印,只有方格百望不支持
             *
             */
            if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) && ObjectUtil.isNotEmpty(orderInvoiceInfo1) && OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(orderInvoiceInfo1.getZfBz())) {
                log.error("{}已作废发票暂不支持打印", LOGGER_MSG);
                throw new OrderReceiveException(OrderInfoContentEnum.PRINT_INVOICE_9998);
            }
            /**
             * 判断历史数据导入的数据不能进行打印
             */
            OrderProcessInfo orderProcessInfo = apiOrderProcessService.queryOrderProcessInfoByFpqqlsh(orderInvoiceInfo1.getFpqqlsh(), shList);
            if (ObjectUtil.isNotEmpty(orderProcessInfo)) {
                if (OrderInfoEnum.ORDER_SOURCE_7.getKey().equals(orderProcessInfo.getDdly())) {
                
                    log.error("{}历史数据无法进行打印", LOGGER_MSG);
                    throw new OrderReceiveException(OrderInfoContentEnum.PRINT_INVOICE_9999);
                }
            }
            DyRequestExtend req = new DyRequestExtend();
            req.setFpqqlsh(orderInvoiceInfo1.getKplsh());
            req.setDdqqlsh(orderInvoiceInfo1.getFpqqlsh());
            req.setFpdm(orderInvoiceInfo1.getFpdm());
            req.setFpqh(orderInvoiceInfo1.getFphm());
            req.setFpzh(orderInvoiceInfo1.getFphm());
            req.setNsrsbh(orderInvoiceInfo1.getXhfNsrsbh());
            req.setSldId(orderInvoiceInfo1.getSld());
            req.setFjh(orderInvoiceInfo1.getFjh());
            req.setKpzdbs(orderInvoiceInfo1.getFjh());
            req.setFpzlDm(orderInvoiceInfo1.getFpzlDm());
            invoicePrintPackageDetailList.add(req);
            i++;
        }
        /**
         * 根据纳税人识别号获取维护的税控设备信息
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
        // 打印标识 c48 fp 打印发票  qd 打印清单 A9及百望 1 打印清单 0 打印发票
        /**
         * "fp"==发票
         * "qd"==清单
         *
         * 税控盘托管
         * 无清单时传0，有清单时传1
         */
        DyResponse batchPrint = new DyResponse();
        //方格税盘单独处理
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
            //redis获取里面获取注册的税盘信息
            log.info("=============》从redis里面获取注册信息入参: 税号：{},机器编号{}", xhfNsrsbh, jqbh);
            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(xhfNsrsbh, jqbh);
            log.info("=============》从redis里面获取注册信息出参:{}", registCodeStr);
            for (DyRequestExtend dyRequestExtend : invoicePrintPackageDetailList) {
        
                log.info("查询发票请求流水号是否存在：{}", dyRequestExtend.getFpqqlsh());
        
                FgkpSkDyjEntity fgkpSkDyjEntity = HttpInvoiceRequestUtilFg.getDyjxx(OpenApiConfig.queryFgDyjInfo, printEntity.getPrintId(), terminalCode);
            
                InvoicePrintInfo printInfo = new InvoicePrintInfo();
                printInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
                printInfo.setZpy(String.valueOf(fgkpSkDyjEntity.getZbj()));
                printInfo.setSpy(String.valueOf(fgkpSkDyjEntity.getSbj()));
                printInfo.setDyjmc(fgkpSkDyjEntity.getMc());
                String dylx = OrderInfoEnum.PRINT_TYPE_1.getKey();
                if ("fp".equals(printEntity.getPrintType())) {
                    dylx = OrderInfoEnum.PRINT_TYPE_0.getKey();
                }
                printInfo.setDylx(dylx);
                printInfo.setFpqqlsh(dyRequestExtend.getDdqqlsh());
                printInfo.setXhfNsrsbh(dyRequestExtend.getNsrsbh());
                printInfo.setFpid(printEntity.getPrintId());
                printInfo.setFpzldm(dyRequestExtend.getFpzlDm());
                printInfo.setFpdm(dyRequestExtend.getFpdm());
                printInfo.setFphm(dyRequestExtend.getFpqh());
                printInfo.setPrintStatus(OrderInfoEnum.INVOICE_PRINT_STATUS_0.getKey());
                printInfo.setCreateTime(new Date());
                printInfo.setUpdateTime(new Date());
                printInfo.setFpdypch(requset.getDypch());
                //设置为待打印
                printInfo.setFgStatus(ConfigureConstant.STRING_2);
                apiFangGeInterfaceService.saveInvoicePrintInfo(printInfo);
    
                if (StringUtils.isNotEmpty(registCodeStr)) {
                    RegistrationCode registCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                    /**
                     * 存放打印信息到redis队列
                     */
                    PushPayload pushPayload = new PushPayload();
                    //发票打印
                    pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_5);
                    pushPayload.setNSRSBH(registCode.getXhfNsrsbh());
                    pushPayload.setJQBH(registCode.getJqbh());
                    pushPayload.setZCM(registCode.getZcm());
                    //方格单张打印，单条发消息
                    pushPayload.setDYPCH(dyRequestExtend.getDdqqlsh());
                    log.info("======================>存放消息到redis开始，内容：{}", JsonUtils.getInstance().toJsonString(pushPayload));
                    apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
                    log.info("======================>存放消息到redis结束");
                }
            }
    
            batchPrint.setCode(OrderInfoContentEnum.PRINT_INVOICE_0000.getKey());
            batchPrint.setMsg(OrderInfoContentEnum.PRINT_INVOICE_0000.getMessage());
            batchPrint.setDypch(requset.getDypch());
        } else {
            if (ConfigureConstant.STRING_FP.equals(printEntity.getPrintType())) {
                if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
                    requset.setFpbs(printEntity.getPrintType());
                } else {
                    requset.setFpbs(ConfigureConstant.STRING_1);
                }
            } else {
                if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
                    requset.setFpbs(printEntity.getPrintType());
                } else {
                    requset.setFpbs(ConfigureConstant.STRING_0);
                }
            }
        
            requset.setInvoicePrintPackageDetailList(invoicePrintPackageDetailList);
        
            batchPrint = HttpInvoiceRequestUtil.batchPrint(OpenApiConfig.printInvoice, requset, terminalCode);
        }

    
        return batchPrint;
    }
    
    /**
     * @param ids 发票id
     * @Description 更新纸票打印信息
     * @Author xieyuanqiang
     * @Date 11:02 2018-08-02
     */
    @Override
    public void updateInvoiceDyztById(List<Map> ids) {
        log.info("{}调用订单接口 打印纸票发票或纸票清单 结果{}", LOGGER_MSG, ids == null ? "" : ids.toString());
        //0 是正常发票 1 是作废发票
        String invoiceType = "0";
        // 打印状态 0 未打印 1 已打印
        String dyzt = "1";
        try {
            if (ids != null && ids.size() > 0) {
                for (Map map : ids) {
                
                    String id = (String) map.get("id");
                    String nsrsbh = (String) map.get("xhfNsrsbh");
                    List<String> shList = new ArrayList<>();
                    shList.add(nsrsbh);
                    log.info("{}调用订单系统 更新纸票打印状态 参数 invoiceType {}，打印状态{}，发票id{}", LOGGER_MSG, invoiceType, dyzt, id);
                    int i = apiOrderInvoiceInfoService.updateDyztById(id, dyzt, invoiceType, shList);
                    log.info("{}调用订单系统 更新纸票打印状态 返回结果:{}", LOGGER_MSG, i);

                }
            }
        } catch (Exception e) {
            log.debug("{}调用订单系统 更新纸票打印状态 出错了{}", LOGGER_MSG, e);
        }
    }


    /**
     * 单张打印受理测试接口
     *
     * @param dydbs
     * @param dyfpzl
     * @param dylx
     * @return
     */
    @Override
    public R printTest(String dydbs, String dyfpzl, String dylx) {
        log.info("调用开票系统 单张打印受理测试接口 rest接口 参数dydbs{} dyfpzl{} dylx{} ", dydbs, dyfpzl, dylx);
        Map<String, String> params = new HashMap<>(5);
        params.put("dydbs", dydbs);
        params.put("dyfpzl", dyfpzl);
        params.put("dylx", dylx);
        R r = new R();
        Map result = new HashMap(5);
        try {
            log.info("调用开票系统 单张打印受理测试接口 rest接口 参数url{}  params{}", OpenApiConfig.printTest_c48, JsonUtils.getInstance().toJsonString(params));
            String respResult = HttpUtils.doPost(OpenApiConfig.printTest_c48, JsonUtils.getInstance().toJsonString(params));
            if (StringUtils.isNotBlank(respResult)) {
                result = JsonUtils.getInstance().parseObject(respResult, Map.class);
            }
            log.info("调用开票系统 单张打印受理测试接口 rest接口 结果{} ", respResult);
            if (null != result && !result.isEmpty()) {
                String code = result.get(OrderManagementConstant.CODE).toString();
                if (ConfigureConstant.STRING_0000.equals(code)) {
                    r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000).put("msg", "打印测试成功");
                } else {
                    r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "打印测试失败");
                }
            } else {
                r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "打印测试失败");
            }
        } catch (Exception e) {
            r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "打印测试失败");
            e.printStackTrace();
        }
        return r;
    }
    
}
