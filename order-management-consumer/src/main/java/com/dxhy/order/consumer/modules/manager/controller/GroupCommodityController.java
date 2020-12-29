package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiGroupCommodityService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.ExcelUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupCommodity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 集团分组控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:58
 */
@Slf4j
@RequestMapping("/groupCommodity")
@RestController
@Api(value = "集团分组", tags = {"管理模块"})
public class GroupCommodityController {
    
    private static final String LOGGER_MSG = "(集团分组)";
    
    @Reference
    private ApiGroupCommodityService groupCommodityService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 查询所有分组
     *
     * @return
     */
    @PostMapping("/queryGroupList")
    @ApiOperation(value = "集团分组列表", notes = "集团分组-分组列表")
    public R queryGroupList() {
        String userId = userInfoService.getUser().getUserId().toString();
        List<GroupCommodity> list = groupCommodityService.queryGroupList(userId);
        log.info("返回值 {} ", list);
        return R.ok().put("list", list);
    }
    
    /**
     * 保存分组
     *
     * @param groupCommodity
     * @return
     */
    @PostMapping("/saveGroup")
    @ApiOperation(value = "保存集团分组", notes = "集团分组-保存集团分组")
    public R saveGroup(GroupCommodity groupCommodity) {
        //获取当前登录人ID
        if (groupCommodity == null) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        } else if (StringUtils.isBlank(groupCommodity.getXhfNsrsbh())) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        Long userId = userInfoService.getUser().getUserId();
        groupCommodity.setUserId(userId.toString());
        if (StringUtils.isBlank(groupCommodity.getXhfNsrsbh())) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }

        log.info("参数 {} ", groupCommodity);
        groupCommodity.setXhfNsrsbh("");
        R r = groupCommodityService.saveGroup(groupCommodity, null);
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
            log.error("添加分组失败");
        }
    
        return R.ok();
    }
    
    /**
     * 删除分组
     *
     * @param id
     * @return
     */
    @PostMapping("/removeGroup")
    @ApiOperation(value = "删除集团分组", notes = "集团分组-删除集团分组")
    public R removeGroup(@RequestParam String id,
                         @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        log.info("参数 {} ", id);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }

        R r = groupCommodityService.removeGroup(id, null);
        log.info("返回值 {}", r);
        return r;
    }
    
    /**
     * 校验
     *
     * @param map
     * @return
     */
    @PostMapping("/checkGroup")
    @ApiOperation(value = "校验集团分组", notes = "集团分组-校验集团分组是否重复")
    public R checkGroup(@RequestParam Map<String, String> map) {
        //获取当前登录人ID
        Long userId = userInfoService.getUser().getUserId();
    
        map.put("userId", userId.toString());
        R r = groupCommodityService.checkGroup(map);
        return r;
    }
    
    /**
     * excel导入
     * 该接口已不再使用
     *
     * @param file
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @Deprecated
    @PostMapping("/uploadExcel")
    @ApiOperation(value = "导入集团分组", notes = "集团分组-导入集团分组")
    public R uploadExcel(@RequestParam("file") MultipartFile file,
                         HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.debug("商品分组管理");
        List<GroupCommodity> groupCommodity = ExcelUtils.getExcelGroupCommodityInfo(file.getInputStream(), file.getOriginalFilename());
    
        //获取当前登录人ID  获取税号，拦截获取第一个
        List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
        Long userId = userInfoService.getUser().getUserId();
        for (GroupCommodity group : groupCommodity) {
            group.setXhfNsrsbh(taxplayerCodeList.get(0));
            group.setUserId(userId.toString());
        }
        R r = groupCommodityService.uploadGrop(groupCommodity);
        return r;
    }
    
}
