package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.SalerWarningService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.SalerWarning;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
/**
 * 发票预警控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:34
 */
@Slf4j
@RestController
@RequestMapping("/saler")
@Api(value = "发票预警", tags = {"管理模块"})
public class SalerController {
    private static final String LOGGER_MSG = "(发票预警)";
    
    @Reference
    private SalerWarningService salerWarningService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * @param saWarning
     * @return
     */
    @PostMapping("/add")
    @SysLog(operation = "发票预警添加", operationDesc = "发票预警", key = "发票预警")
    @ApiOperation(value = "发票预警添加", notes = "发票预警-发票预警添加")
    public R add(SalerWarning saWarning) {
        String[] xfshs = JsonUtils.getInstance().fromJson(saWarning.getXhfNsrsbh(), String[].class);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
        
        saWarning.setXhfNsrsbh(nsrsbh);
        saWarning.setCreateId(String.valueOf(userInfoService.getUser().getUserId()));
        saWarning.setCreateUser(userInfoService.getUser().getUsername());
        saWarning.setDeptId(String.valueOf(userInfoService.getUser().getDept().getDeptId()));
        salerWarningService.addSalerWarning(saWarning);
        return R.ok();
    }
    
    /**
     * @param saWarning
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "发票预警更新", notes = "发票预警-发票预警更新")
    @SysLog(operation = "发票预警更新", operationDesc = "发票预警更新", key = "发票预警")
    public R update(SalerWarning saWarning) {
        String[] xfshs = JsonUtils.getInstance().fromJson(saWarning.getXhfNsrsbh(), String[].class);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
    
        saWarning.setXhfNsrsbh(nsrsbh);
        salerWarningService.update(saWarning);
        return R.ok();
    }
    
    /**
     * @param xhfNsrsbh
     * @return
     */
    @PostMapping("/query")
    @ApiOperation(value = "发票预警查询", notes = "发票预警-发票预警查询")
    @SysLog(operation = "发票预警查询", operationDesc = "发票预警查询", key = "发票预警")
    public R query(String xhfNsrsbh) {
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
    
        String createId = userInfoService.getUser().getUserId().toString();
        List<SalerWarning> salerWarningList = salerWarningService.selectSalerWaringByNsrsbh(nsrsbh, createId);
        if (salerWarningList != null && salerWarningList.size() > 0) {
            return R.ok().put("salerWarning", salerWarningList.get(0));
        } else {
            return R.ok();
        }
    }
    
}
