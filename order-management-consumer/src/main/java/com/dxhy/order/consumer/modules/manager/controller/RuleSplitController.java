package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiRuleSplitService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.RuleSplitEntity;
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
import java.util.List;

/**
 * @author liangyuhuan
 * @date 2018/10/23
 * 拆分规则
 */
@Slf4j
@RestController
@RequestMapping("/split")
@Api(value = "拆分规则", tags = {"管理模块"})
public class RuleSplitController {
    
    private static final String LOGGER_MSG = "(拆分规则控制层)";
    
    @Reference
    private ApiRuleSplitService ruleSplitService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 获取拆分规则
     *
     * @return
     */
    @PostMapping("/queryRuleSplit")
    @SysLog(operation = "查询拆分规则", operationDesc = "查询拆分规则", key = "拆分规则管理")
    @ApiOperation(value = "查询拆分规则", notes = "拆分规则管理-查询拆分规则")
    public R queryRuleSplit(String xhfNsrsbh) {
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        xfshs = NsrsbhUtils.getNsrsbhList(xfshs);
        if (xfshs.length > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
        
        log.info("参数 纳税人识别号 {}, 当前登录人id{}", nsrsbh, userInfoService.getUser().getUserId());
        RuleSplitEntity queryRuleSplit = ruleSplitService.queryRuleSplit(xfshs[0], userInfoService.getUser().getUserId().toString());
        return R.ok().put("ruleSplit", queryRuleSplit);
    }
    
    
    /**
     * 保存拆分规则
     *
     * @param ruleSplitType
     * @return
     */
    @PostMapping("/saveRuleSplit")
    @SysLog(operation = "保存拆分规则", operationDesc = "保存拆分规则", key = "拆分规则管理")
    @ApiOperation(value = "保存拆分规则", notes = "拆分规则管理-保存拆分规则")
    public R saveRuleSplit(@RequestParam String ruleSplitType, @RequestParam String xhfNsrsbh) {
        RuleSplitEntity ruleSplitEntity = new RuleSplitEntity();
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        if (shList == null || shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = shList.get(0);
    
        String userId = userInfoService.getUser().getUserId().toString();
        ruleSplitEntity.setUserId(userId);
        ruleSplitEntity.setTaxpayerCode(nsrsbh);
        ruleSplitEntity.setRuleSplitType(ruleSplitType);
        return ruleSplitService.saveRuleSplit(ruleSplitEntity);
    }
    
}
