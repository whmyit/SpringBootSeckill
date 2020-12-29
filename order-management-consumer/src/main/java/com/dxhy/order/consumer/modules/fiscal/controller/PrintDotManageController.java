package com.dxhy.order.consumer.modules.fiscal.controller;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.invoice.protocol.sl.sld.DydxxcxRequest;
import com.dxhy.invoice.protocol.sl.sld.Nsrsbh;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.dy.DydResponseDetail;
import com.dxhy.order.model.a9.dy.DydResponseExtend;
import com.dxhy.order.model.fg.FgkpSkDyjEntity;
import com.dxhy.order.model.fg.FgkpSkDyjEntityList;
import com.dxhy.order.model.fg.FgkpSkDyjmcCxParam;
import com.dxhy.order.utils.HttpInvoiceRequestUtilFg;
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

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 打印点 controller
 *
 * @author Dear
 */
@Slf4j
@RequestMapping("/printDotManage")
@RestController
@Api(value = "打印点", tags = {"税控模块"})
public class PrintDotManageController {
    private static final String LOGGER_MSG = "打印点 controller";

    @Resource
    private UnifyService unifyService;
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    /**
     * 根据纳税人识别号查询
     * 在使用
     *
     * @param xhfNsrsbh
     * @param sydzt
     * @return
     */
    @PostMapping("/queryDydAndNsrsbh")
    @ApiOperation(value = "打印点查询", notes = "打印点管理-打印点查询")
    public R queryDydAndNsrsbh(@RequestParam String xhfNsrsbh, @RequestParam String sydzt) {
        log.info("{},参数{},{}", LOGGER_MSG, xhfNsrsbh, sydzt);
    
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
    
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        xfshs = NsrsbhUtils.getNsrsbhList(xfshs);
    
        //根据销方税号查询税控设备
        String terminalCode = apiTaxEquipmentService.getTerminalCode(xfshs[0]);
        //方格税盘单独处理
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
    
            List<DydResponseDetail> dydResponseDetailList = new ArrayList<>();
    
            FgkpSkDyjmcCxParam fgkpSkDyjmcCxParam = new FgkpSkDyjmcCxParam();
            fgkpSkDyjmcCxParam.setXhfNsrsbh(NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh));
            FgkpSkDyjEntityList skDyjByShZl = HttpInvoiceRequestUtilFg.findSkDyjByShZl(OpenApiConfig.queryFgDyjInfoList, fgkpSkDyjmcCxParam, terminalCode);
            if (ObjectUtil.isNotEmpty(skDyjByShZl) && ObjectUtil.isNotEmpty(skDyjByShZl.getData())) {
                List<FgkpSkDyjEntity> fgkpSkDyjEntityList = skDyjByShZl.getData();
                for (FgkpSkDyjEntity fgkpSkDyjEntity : fgkpSkDyjEntityList) {
                    DydResponseDetail dydResponseDetail = new DydResponseDetail();
                    dydResponseDetail.setDyjid(String.valueOf(fgkpSkDyjEntity.getId()));
                    dydResponseDetail.setDyjmc(fgkpSkDyjEntity.getMc());
                    dydResponseDetail.setSpotKey("");
                    dydResponseDetailList.add(dydResponseDetail);
                }
            }
            if (ObjectUtil.isNotEmpty(dydResponseDetailList)) {
                return R.ok().put("map", dydResponseDetailList);
            } else {
                return R.ok();
            }
        } else {
            Nsrsbh[] nsrsbhs = new Nsrsbh[xfshs.length];
            Nsrsbh nsrsbh = new Nsrsbh();
            nsrsbh.setNsrsbh(xfshs[0]);
            nsrsbhs[0] = nsrsbh;
        
            DydxxcxRequest dydxxcxRequest = new DydxxcxRequest();
            dydxxcxRequest.setNsrsbhs(nsrsbhs);
            dydxxcxRequest.setDydzt(sydzt);
        
            DydResponseExtend dydxxcxResponse = unifyService.queryDydxxcxList(dydxxcxRequest, terminalCode);
        
            if (dydxxcxResponse != null && dydxxcxResponse.getFpdyjs() != null) {
                return R.ok().put("map", dydxxcxResponse.getFpdyjs());
            }
        }
    
        return R.error(ConfigureConstant.STRING_9999, "打印机查询异常");
    }
    
    
}
