package com.dxhy.order.consumer.modules.fiscal.controller;

import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
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

/**
 * 税控 controller
 *
 * @author Dear
 */
@Slf4j
@RestController
@RequestMapping("/invoiceDot")
@Api(value = "税控信息", tags = {"税控模块"})
public class InvoiceDotController {
    private static final String LOGGER_MSG = "税控状态controller ";

    @Resource
    private UnifyService unifyService;
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    /**
     * 根据开票点id查询发票剩余发票份数
     * 在使用
     *
     * @param xhfNsrsbh 纳税人识别号 sldid
     * @return
     */
    @PostMapping("/queryFpfsBySldId")
    @ApiOperation(value = "税盘剩余份数查询", notes = "税控管理-税盘剩余份数查询")
    @SysLog(operation = "税盘剩余份数查询", operationDesc = "税盘剩余份数查询", key = "税盘管理")
    public R queryFpfsBySldId(@RequestParam String xhfNsrsbh, @RequestParam String sldId, @RequestParam String fpzldm) {
        log.info("根据开票点id查询发票剩余发票份数 参数 nsrsbh {}, sldId{}, fpzldm:{}", xhfNsrsbh, sldId, fpzldm);
        if (StringUtils.isEmpty(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
    
        /**
         * 根据税号查询用户设备信息
         */
    
        String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
    
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            return R.ok();
        } else {
            String taxpayerCode = xfshs[0];
            SldJspxxRequest sldJspxxRequest = new SldJspxxRequest();
            sldJspxxRequest.setNsrsbh(taxpayerCode);
            sldJspxxRequest.setSyzt("2");
            sldJspxxRequest.setFpzldm(fpzldm);
            sldJspxxRequest.setSldid(sldId);
            SldJspxxResponse sldJspxxResponse = unifyService.querSldFpfs(sldJspxxRequest, terminalCode);
        
            if (sldJspxxResponse != null) {
                if (ConfigureConstant.STRING_0000.equals(sldJspxxResponse.getStatusCode()) && CollectionUtils.isNotEmpty(sldJspxxResponse.getSldJspxxList())) {
                    int fpfs = sldJspxxResponse.getSldJspxxList().get(0).getFpfs();
                    log.info("根据开票点id查询发票剩余发票份数 返回值{}", fpfs);
                    return R.ok().put("fpfs", fpfs);
                } else {
                    return R.error(sldJspxxResponse.getStatusCode(), sldJspxxResponse.getStatusMessage()).put("fpfs", 0);
                }
            } else {
                return R.error("9999", "暂无数据");
            }
        }
    
    }
    
}
