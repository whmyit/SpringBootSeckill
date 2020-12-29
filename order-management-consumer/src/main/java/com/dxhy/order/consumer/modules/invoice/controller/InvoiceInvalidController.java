package com.dxhy.order.consumer.modules.invoice.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.dxhy.common.generatepdf.util.StringUtil;
import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.model.page.OrderListQuery;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import com.dxhy.order.consumer.utils.PageBeanConvertUtil;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceRequest;
import com.dxhy.order.model.a9.sld.QueryNextInvoiceResponseExtend;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：InvoiceInvalidController
 * @Description ：发票作废和专票冲红接口
 * @date ：2018年7月31日 下午7:37:23
 */
@Api(value = "作废冲红", tags = {"发票模块"})
@RestController
@RequestMapping("/invoiceInvalid")
@Slf4j
public class InvoiceInvalidController {
    
    private static final String LOGGER_MSG = "(发票作废控制层)";
    
    @Resource
    private InvalidInvoiceService invalidInvoiceService;
    
    @Resource
    private UnifyService unifyService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    
    @ApiOperation(value = "空白发票作废", notes = "发票作废管理-空白作废发票")
    @PostMapping("/valid")
    @SysLog(operation = "空白发票作废rest接口", operationDesc = "空白发票作废接口", key = "发票作废")
    public R whiteInvoiceInvalid(HttpServletRequest request,
                                 @ApiParam(name = "receviePoint", value = "受理点", required = true) @RequestParam(value = "receviePoint", required = true) String receviePoint,
                                 @ApiParam(name = "invoiceType", value = "发票类型", required = true) @RequestParam(value = "invoiceType", required = true) String invoiceType,
                                 @ApiParam(name = "invoiceCode", value = "发票代码", required = true) @RequestParam(value = "invoiceCode", required = true) String invoiceCode,
                                 @ApiParam(name = "invoiceNum", value = "发票号码", required = true) @RequestParam(value = "invoiceNum", required = true) String invoiceNum,
                                 @ApiParam(name = "kpjh", value = "开票机号", required = true) @RequestParam(value = "kpjh", required = true) String kpjh,
                                 @ApiParam(name = "nsrsbh", value = "纳税人识别号", required = true) @RequestParam(value = "nsrsbh", required = true) String nsrsbh,
                                 @ApiParam(name = "xhfmc", value = "纳税人识别号", required = true) @RequestParam(value = "xhfmc", required = true) String xhfmc) {
        log.info("{}发票作废的接口，前端传入的参数,受理点:{},发票类型:{},发票代码:{},发票号码:{},开票机号:{},销方名称:{}", LOGGER_MSG,
                receviePoint, invoiceType, invoiceCode, invoiceNum, kpjh, xhfmc);
        try {
            //发票号码不足8位的前面补零
            invoiceNum = StringUtil.addZero(invoiceNum, 8);
            R validInvoice = invalidInvoiceService.validInvoice(receviePoint, invoiceType, invoiceCode, invoiceNum, kpjh, nsrsbh, xhfmc);
            return validInvoice;
        } catch (Exception e) {
            log.error("{}发票作废接口异常:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    
    @ApiOperation(value = "发票批量作废", notes = "发票作废管理-批量作废发票")
    @PostMapping("/batchValid")
    @SysLog(operation = "批量作废rest接口", operationDesc = "提供产品使用,批量作废发票数据", key = "发票作废")
    public R batchValid(HttpServletRequest request,
                        @ApiParam(name = "orderIdArray", value = "订单id和销方税号", required = true) @RequestBody String orderIdArray) {
    
        try {
            if (StringUtils.isBlank(orderIdArray)) {
                return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
            }
        
            List<Map> idList = JSON.parseArray(orderIdArray, Map.class);
            return invalidInvoiceService.batchValidInvoice(idList);
        } catch (Exception e) {
            log.error("{}发票作废接口异常:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    
    @ApiOperation(value = "手动发票作废状态推送", notes = "发票作废管理-手动发票作废状态推送")
    @PostMapping("/manualPushInvalidInvoice")
    @SysLog(operation = "手动发票作废状态推送", operationDesc = "手动发票作废状态推送,批量作废发票数据", key = "发票作废")
    public R manualPushInvalidInvoice(HttpServletRequest request,
                                      @ApiParam(name = "orderIdArray", value = "订单id和销方税号", required = true) @RequestParam(value = "orderIdArray", required = true) String orderIdArray) {
        try {
            if (StringUtils.isBlank(orderIdArray)) {
                return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
            }
            
            List<Map> idList = JSON.parseArray(orderIdArray, Map.class);
            R batchValidInvoice = invalidInvoiceService.manualPushInvalidInvoice(idList);
            return batchValidInvoice;
        } catch (Exception e) {
            log.error("{}手动发票作废状态推送:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    /**
     * 空白发票作废完成保存信息
     * activex 专用
     */
    @ApiOperation(value = "空白作废保存作废信息", notes = "发票作废管理-空白作废保存作废信息")
    @PostMapping("/voidInvalidInvoiceActiveX")
    @SysLog(operation = "已开发票作废更新作废信息", operationDesc = "空白作废保存作废信息", key = "空白作废保存作废信息")
    public R voidInvalidInvoiceActiveX(@ApiParam(name = "fpdm", value = "发票代码", required = true) @RequestParam(value = "fpdm", required = true) String fpdm,
                                       @ApiParam(name = "fphm", value = "发票号码", required = true) @RequestParam(value = "fphm", required = true) String fphm,
                                       @ApiParam(name = "zfzt", value = "作废状态", required = true) @RequestParam(value = "zfzt", required = true) String zfzt,
                                       @ApiParam(name = "sldid", value = "受理点", required = true) @RequestParam(value = "sldid", required = true) String sldid,
                                       @ApiParam(name = "nsrsbh", value = "纳税人识别号", required = true) @RequestParam(value = "nsrsbh", required = true) String nsrsbh,
                                       @ApiParam(name = "fpzldm", value = "发票种类代码", required = true) @RequestParam(value = "fpzldm", required = true) String fpzldm) {
        try {
            R r = invalidInvoiceService.voidInvalidInvoiceActiveX(fpdm, fphm, zfzt, sldid, nsrsbh, fpzldm);
            return r;
        } catch (Exception e) {
            log.error("{}发票作废接口异常:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    @ApiOperation(value = "查询作废订单列表", notes = "发票作废管理-查询作废订单列表")
    @PostMapping("/queryInvalidInvoice")
    @SysLog(operation = "作废列表rest接口", operationDesc = "提供产品使用,展示作废列表", key = "发票作废列表")
    public R queryInvalidInvoice(@RequestBody OrderListQuery orderBatchQuery) {
    
        log.debug("{}作废列表查询参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
        if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
    
        //参数转换
        Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
    
        PageUtils queryByInvalidInvoice = invalidInvoiceService.queryByInvalidInvoice(paramMap, shList);
    
        return R.ok().put("result", queryByInvalidInvoice);
    }
    
    
    @ApiOperation(value = "空白发票作废列表查询", notes = "发票作废管理-空白发票作废列表查询")
    @PostMapping("/queryKbInvoiceList")
    @SysLog(operation = "空白作废列表rest接口", operationDesc = "提供产品使用,展示作废列表", key = "空白发票作废列表")
    public R queryKbInvoiceList(@RequestBody OrderListQuery orderBatchQuery) {
    
        log.debug("{}作废列表查询参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderBatchQuery));
        if (StringUtils.isBlank(orderBatchQuery.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(orderBatchQuery.getXhfNsrsbh());
    
        //参数转换
        Map<String, Object> paramMap = PageBeanConvertUtil.convertToMap(orderBatchQuery);
    
        PageUtils queryByInvalidInvoice = invalidInvoiceService.queryKbInvoiceList(paramMap, shList);
    
        return R.ok().put("result", queryByInvalidInvoice);
    }
    
    
    @ApiOperation(value = "获取受理点信息", notes = "发票作废管理-获取受理点详细信息的接口")
    @PostMapping("/getReceivePointDetail")
    @SysLog(operation = "受理点详情获取rest接口", operationDesc = "提供产品使用,作废受理点获取明细数据", key = "受理点查询")
    public R getCodeAndNumber(HttpServletRequest request,
                              @ApiParam(name = "receivePoint", value = "发票受理点", required = true) @RequestParam(value = "receivePoint", required = true) String receivePoint,
                              @ApiParam(name = "fplx", value = "发票受理点", required = true) @RequestParam(value = "fplx", required = true) String fplx,
                              @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        
        R r = new R();
        try {
            List<String> shList = new ArrayList<>();
            if (StringUtils.isNotBlank(xhfNsrsbh)) {
                shList = JsonUtils.getInstance().parseObject(xhfNsrsbh, List.class);
            }
    
            if (shList.size() > 1) {
                log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xhfNsrsbh));
                return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
            }
            /**
             * 根据纳税人识别号获取维护的税控设备信息
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(shList.get(0));
            if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = new QueryNextInvoiceResponseExtend();
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(shList.get(0), null);
                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                if (ObjectUtil.isNotEmpty(registrationCode)) {
                    queryNextInvoiceResponseExtend.setFjh(registrationCode.getJqbh());
                    queryNextInvoiceResponseExtend.setNsrsbh(registrationCode.getXhfNsrsbh());
                    queryNextInvoiceResponseExtend.setJqbh(registrationCode.getJqbh());
                }
                r.put("invoice", queryNextInvoiceResponseExtend);
                return r;
            } else if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                QueryNextInvoiceRequest queryNextInvoiceRequest = new QueryNextInvoiceRequest();
                queryNextInvoiceRequest.setFpzlDm(fplx);
                queryNextInvoiceRequest.setNsrsbh(shList.get(0));
                queryNextInvoiceRequest.setSldId(receivePoint);
                QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = HttpInvoiceRequestUtil.queryNextInvoice(OpenApiConfig.queryNextInvoice, queryNextInvoiceRequest, terminalCode);
                if (queryNextInvoiceResponseExtend == null || !queryNextInvoiceResponseExtend.getStatusCode().equals(OrderInfoContentEnum.SUCCESS.getKey())) {
                    return R.error().put(OrderManagementConstant.MESSAGE, "获取下一张发票的接口异常");
                }
                r.put("invoice", queryNextInvoiceResponseExtend);
            }
            return r;
        } catch (Exception e) {
            log.error("{}发票作废接口异常:{}", LOGGER_MSG, e);
            return R.error();
        }
    }
    
}
