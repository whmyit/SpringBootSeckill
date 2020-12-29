package com.dxhy.order.consumer.modules.fiscal.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.invoice.service.sl.SldManagerService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.sld.SearchFjh;
import com.dxhy.order.model.bwactivexs.server.SkServerRequest;
import com.dxhy.order.model.bwactivexs.server.SkServerResponse;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 税控状态 controller
 *
 * @author Dear
 */
@Slf4j
@RestController
@Api(value = "税控状态", tags = {"税控模块"})
@RequestMapping("/fiscalState")
public class FiscalStateController {
    private static final String LOGGER_MSG = "(税控状态controller)";
    
    @Resource
    private UnifyService unifyService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private SldManagerService sldManagerService;
    
    /**
     * 查询税号下的税盘和分级下的发票种类代码
     */
    @PostMapping("/queryFpzlDm")
    @ApiOperation(value = "开票点-查询(根据税号查询)", notes = "查询税号下的税盘和分级下的发票种类代码")
    public R queryFjh(@RequestParam String xhfNsrsbh, @RequestParam String fjh) {
        log.info("{}首页税盘状态查询 参数{}", LOGGER_MSG, xhfNsrsbh);
        try {
            if (StringUtils.isBlank(xhfNsrsbh)) {
                return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
            }
            List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
            if (shList.size() > 1) {
                log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
                return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
            }
    
            Set<SearchFjh> resultList = unifyService.getFjh(shList, fjh);
    
    
            if (CollectionUtils.isNotEmpty(resultList)) {
                return R.ok().put("list", resultList);
            } else {
                return R.error("9999", "暂无数据");
            }
    
    
        } catch (Exception e) {
            log.error("{}根据税号获取税盘信息的接口异常 ,e:{}", LOGGER_MSG, e);
            return R.error("9999", "暂无数据");
        }
    }
    
    
    /**
     * 供active-x调用 根据受理点id和纳税人识别号 查询服务信息
     */
    @PostMapping("/queryServerInfo")
    @ApiOperation(value = "服务信息-查询(activex专用)", notes = "供active-x调用 根据受理点id和纳税人识别号 查询服务信息")
    public R queryServerInfo(@RequestParam String xhfNsrsbh,@RequestParam String kpdId) {
    	if(StringUtils.isBlank(xhfNsrsbh) || StringUtils.isBlank(kpdId)) {
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                    .put(OrderManagementConstant.MESSAGE, "税号和开票点id不能为空!");
        }
        List<String> shList = JSON.parseArray(xhfNsrsbh, String.class);
    	SkServerRequest request = new SkServerRequest();
    	request.setKpdId(kpdId);
    	request.setNsrsbh(shList.get(0));
        SkServerResponse response = unifyService.queryServerInfo(request, OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey());
        if(OrderInfoContentEnum.SUCCESS.getKey().equals(response.getCode())){
            return R.ok().put("msg", response.getMsg()).put(OrderManagementConstant.DATA, response.getContent());
        }else{
            return R.error().put("msg",response.getMsg());
        }

    }
    
    
    
}
