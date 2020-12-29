package com.dxhy.order.consumer.modules.fiscal.controller;

import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.api.ApiYpWarningService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.InvoiceWarningInfo;
import com.dxhy.order.model.ypyj.YpYjFront;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 余票预警controller
 *
 * @author yuchenguang
 * @ClassName: YpWarningController
 * @Description: 余票预警
 * @date 2018年9月12日 下午12:28:16
 */
@Slf4j
@RequestMapping("/ypWarning")
@RestController
@Api(value = "发票预警", tags = {"税控模块"})
public class YpWarningController {
    private final static String LOGGER_MSG = "发票预警controller";
    @Reference
    private ApiYpWarningService ypWarningService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    /**
     * 在使用
     * 余票预警查询
     *
     * @param xhfNsrsbh 纳税人识别号
     * @return
     */
    @PostMapping("/queryYpWarning")
    @ApiOperation(value = "余票预警列表", notes = "余票预警管理-余票预警列表")
    @SysLog(operation = "余票预警列表", operationDesc = "余票预警列表", key = "余票预警管理")
    public R queryYpWarning(@RequestParam String xhfNsrsbh, @RequestParam String spn, @RequestParam String pageSize, @RequestParam String currPage) {
        log.info("{},参数 纳税人识别号{} 税盘号{}", LOGGER_MSG, xhfNsrsbh, spn);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        InvoiceWarningInfo ypWarningEntity = new InvoiceWarningInfo();
        ypWarningEntity.setUserId(userInfoService.getUser().getUserId().toString());
        ypWarningEntity.setSbbh(spn);
        PageUtils invoiceWarningInfos = ypWarningService.selectPageYpWarning(ypWarningEntity, Integer.parseInt(pageSize), Integer.parseInt(currPage), shList);
        
        return R.ok().put("data", invoiceWarningInfos);
    }

    /**
     * 在使用
     * 删除余票预警
     *
     * @param id 预警实体
     *           * @return
     */
    @PostMapping("/delYpWarningInfo")
    @ApiOperation(value = "余票预警删除", notes = "余票预警管理-余票预警删除")
    @SysLog(operation = "余票预警删除", operationDesc = "余票预警删除", key = "余票预警管理")
    public R delYpWarningInfo(@RequestBody String id) {
        log.info("{},参数 {}", LOGGER_MSG, id);
        if (StringUtils.isBlank(id)) {
            return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
        }
        Map mapList = JsonUtils.getInstance().parseObject(id, Map.class);
        String delId = (String) mapList.get("id");
        String nsrsbh = (String) mapList.get("xhfNsrsbh");
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(nsrsbh);
        InvoiceWarningInfo invoiceWarningInfo = new InvoiceWarningInfo();
        invoiceWarningInfo.setId(delId);
        invoiceWarningInfo.setDeleteStatus(ConfigureConstant.STRING_1);
        int result = ypWarningService.updateYpWarnInfo(invoiceWarningInfo, shList);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    
    }
    
    /**
     * 在使用
     * 保存余票预警
     *
     * @param ypYjFront 预警实体
     */
    @PostMapping("/saveYpWarningInfo")
    @ApiOperation(value = "余票预警新增", notes = "余票预警管理-余票预警新增")
    @SysLog(operation = "余票预警新增", operationDesc = "余票预警新增", key = "余票预警管理")
    public R saveYpWarningInfo(@RequestBody YpYjFront ypYjFront) {
        log.info("{},请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(ypYjFront));
        if (ypYjFront == null) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        } else if (StringUtils.isBlank(ypYjFront.getXhfNsrsbh())) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(ypYjFront.getXhfNsrsbh());
        if (shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
    
        ypYjFront.setUserId(userInfoService.getUser().getUserId().toString());
        //更新人所属组织 ID
        ypYjFront.setDeptId(String.valueOf(userInfoService.getUser().getDept().getDeptId()));
    
        return ypWarningService.saveYpWarnInfo(ypYjFront);
    }

}
