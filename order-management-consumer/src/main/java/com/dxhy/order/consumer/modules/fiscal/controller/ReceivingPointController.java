package com.dxhy.order.consumer.modules.fiscal.controller;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.model.R;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.a9.sld.SearchSldResponse;
import com.dxhy.order.utils.CommonUtils;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 开票点 controller
 *
 * @author Dear
 */
@Slf4j
@RestController
@RequestMapping("/receivingPoint")
@Api(value = "开票点", tags = {"税控模块"})
public class ReceivingPointController {
    
    private static final String LOGGER_MSG = "开票点 controller";
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    
    /**
     * 获取受理点列表
     *
     * @param xhfNsrsbh   纳税人识别号
     * @param invoiceType 发票种类
     * @param cpylx       成品油类型 1成品油 0 非成品油
     * @Description 根据纳税人识别号查询受理点列表查询
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryAccessPointList")
    @ApiOperation(value = "纸票获取受理点", notes = "纸票管理-纸票获取受理点")
    @SysLog(operation = "纸票获取受理点", operationDesc = "纸票获取受理点", key = "纸票管理")
    public R queryAccessPointList(@RequestParam String xhfNsrsbh, @RequestParam String invoiceType, @RequestParam String cpylx,
                                  @RequestParam(value = "kpdId", required = false) String kpdId, @RequestParam(value = "fjh", required = false) String fjh) {
        log.info("{}根据纳税人识别号查询开票点列表查询 参数 taxpayerCode {} invoiceType{} cpybs{}", LOGGER_MSG, xhfNsrsbh, invoiceType, cpylx);
        try {
            if (StringUtils.isBlank(xhfNsrsbh)) {
                return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
            }
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
            log.info("{}根据纳税人识别号查询受理点列表查询 参数 纳税人识别号{} 发票类型 {} ", LOGGER_MSG, xhfNsrsbh, invoiceType);
            
            Set<SearchSld> searchSlds = new HashSet<>();
            
            for (String nsrsbh : shList) {
                try {
                    /**
                     * 根据税号查询税控设备
                     */
                    String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
                    /**
                     * 支持方格开票
                     */
                    if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                        //redis获取里面获取注册的税盘信息
                        List<String> registCodeList = apiFangGeInterfaceService.getRegistCodeListByRedis(nsrsbh);
                        if (ObjectUtil.isNotEmpty(registCodeList)) {
                            for (String registCodeStr : registCodeList) {
                                SearchSld searchSld = new SearchSld();
                                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                                searchSld.setJqbh(registrationCode.getJqbh());
                                searchSld.setSldId(registrationCode.getJqbh());
                                searchSld.setSldMc(registrationCode.getJqbh());
                                searchSld.setNsrsbh(registrationCode.getXhfNsrsbh());
                                searchSld.setTerminalCode(terminalCode);
                                searchSlds.add(searchSld);
                            }
                        }
            
                    }else {
                        String url = OpenApiConfig.querySldList;
                        if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                            url = OpenApiConfig.queryKpdXxBw;
                        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                            url = OpenApiConfig.queryNsrXnsbxx;
                            /**
                             * 如果是新税控转换发票种类代码
                             */
                            invoiceType = CommonUtils.transFplxdm(invoiceType);
                        }
                        /**
                         * cpylx如果为0:成品油,为1:非成品油,为空查询全部
                         */
                        String cpybs = "";
                        if (StringUtils.isBlank(cpylx)) {
                            cpybs = "";
                            HttpInvoiceRequestUtil.getSldList(searchSlds, url, invoiceType, cpybs, nsrsbh, fjh, kpdId, terminalCode);
                        } else if (ConfigureConstant.STRING_1.equals(cpylx)) {
                            cpybs = OrderInfoEnum.OIL_TYPE_0.getKey();
                            HttpInvoiceRequestUtil.getSldList(searchSlds, url, invoiceType, cpybs, nsrsbh, fjh, kpdId, terminalCode);
                        } else {
                            cpybs = OrderInfoEnum.OIL_TYPE_1.getKey();
                            HttpInvoiceRequestUtil.getSldList(searchSlds, url, invoiceType, cpybs, nsrsbh, fjh, kpdId, terminalCode);
                            cpybs = OrderInfoEnum.OIL_TYPE_2.getKey();
                            HttpInvoiceRequestUtil.getSldList(searchSlds, url, invoiceType, cpybs, nsrsbh, fjh, kpdId, terminalCode);
                        }
            
                    }
                } catch (Exception e) {
                    log.error("获取受理点的接口异常，异常信息为:{}", e);
                    continue;
                }
    
    
            }
            if (searchSlds.size() > 0) {
                Map<String, Object> map = new HashMap<>(5);
                SearchSldResponse sldSearchResponse = new SearchSldResponse();
                sldSearchResponse.setStatusCode(ConfigureConstant.STRING_0000);
                sldSearchResponse.setStatusMessage("处理成功");
                sldSearchResponse.setSlds(new ArrayList<>(searchSlds));
                map.put("sldSearchResponse", sldSearchResponse);
                return R.ok().put("data", map);
            } else {
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                        .put(OrderManagementConstant.MESSAGE, "没有可用的开票点");
            }
            
        } catch (Exception e) {
            log.error("获取受理点的接口异常，异常信息为:{}", e);
            return R.error();
        }
        
    }
    
}
