package com.dxhy.order.consumer.modules.manager.controller;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.ApiBusinessTypeService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.BusinessTypeInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务类型管理
 *
 * @author 陈玉航
 * @version 1.0 Created on 2019年6月29日 下午6:21:11
 */
@Slf4j
@RestController
@Api(value = "业务类型", tags = {"管理模块"})
@RequestMapping("/businessType")
public class BusinessTypeController {
    private static final String LOGGER_MSG = "业务类型管理";
    
    @Reference
    private ApiBusinessTypeService apiBusinessTypeService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 业务类型新增
     * 在使用
     *
     * @param businessName
     * @param xhfNsrsbh
     * @param xhfMc
     * @param description
     * @return
     */
    @PostMapping("/addYwlx")
    @ApiOperation(value = "业务类型新增", notes = "业务类型-业务类型新增")
    @SysLog(operation = "业务类型添加", operationDesc = "业务类型新增", key = "业务类型")
    public R addYwlx(
            @ApiParam(name = "businessName", value = "业务类型名称", required = true) @RequestParam(name = "businessName", required = true) String businessName,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(name = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "xhfMc", value = "销货方名称", required = true) @RequestParam(name = "xhfMc", required = true) String xhfMc,
            @ApiParam(name = "description", value = "业务类型描述", required = false) @RequestParam(name = "description", required = false) String description) {
        if (StringUtils.isBlank(businessName)) {
            return R.error("业务类型名称不能为空");
        }
        if (StringUtils.isBlank(xhfMc)) {
            return R.error("销货方名称不能为空");
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        log.info("父级纳税人识别号：{}", JsonUtils.getInstance().toJsonString(shList));
        if (shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = shList.get(0);
    
        //fangyibai标注：确认前端是否只传了一个税号。
        BusinessTypeInfo businessTypeInfo = new BusinessTypeInfo();
        businessTypeInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
        String businessId = RandomUtil.randomString(20);
        businessTypeInfo.setBusinessId(businessId);
        businessTypeInfo.setBusinessName(businessName);
        businessTypeInfo.setXhfNsrsbh(nsrsbh);
        businessTypeInfo.setXhfMc(xhfMc);
        businessTypeInfo.setLy(ConfigureConstant.STRING_1);
        businessTypeInfo.setDescription(description);
        businessTypeInfo.setStatus(ConfigureConstant.STRING_0);
        businessTypeInfo.setCreateTime(new Date());
        businessTypeInfo.setUpdateTime(new Date());
        // 验证，同一销货方下面的属性名称不能一样
        BusinessTypeInfo isHave = apiBusinessTypeService.queryYwlxInfoByNameAndNsrsbh(businessName, shList);
        if (null != isHave) {
            return R.error("同一销货方下面的属性名称不能一样");
        } else {
            apiBusinessTypeService.saveBusinessTypeInfo(businessTypeInfo);
            return R.ok();
        }
    }
    
    
    /**
     * 业务类型查询界面接口
     *
     * @param xhfNsrsbh
     * @param businessName
     * @param pageSize
     * @param currPage
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年8月2日
     */
    @PostMapping("/selectYwlxByParam")
    @ApiOperation(value = "业务类型查询", notes = "业务类型-业务类型查询")
    @SysLog(operation = "业务类型查询", operationDesc = "业务类型列表展示", key = "业务类型")
    public R selectYwlxByParam(
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号,数组形式", required = true) @RequestParam(name = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "businessName", value = "业务类型名称", required = true) @RequestParam(name = "businessName", required = true) String businessName,
            @ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam(name = "pageSize", required = true) String pageSize,
            @ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam(name = "currPage", required = true) String currPage) {
        if (StringUtils.isBlank(pageSize)) {
            return R.error("每页显示个数不能为空");
        }
        if (StringUtils.isBlank(currPage)) {
            return R.error("当前页数不能为空");
        }
        Map<String, Object> csmap = new HashMap<>(5);
    
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        if (StringUtils.isNotBlank(businessName)) {
            csmap.put("businessName", businessName);
        }
        csmap.put("pageSize", Integer.valueOf(pageSize));
        csmap.put("currPage", Integer.valueOf(currPage));
        PageUtils list = apiBusinessTypeService.selectYwlxByParam(csmap, shList);
        return R.ok().put("data", list);
    }
    
    
    /**
     * 业务类型和销方名称联动。
     *
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年7月10日
     */
    @PostMapping("/queryYwlxAndNsrsbh")
    @ApiOperation(value = "业务类型查询-联动", notes = "业务类型-业务类型查询")
    public R queryYwlxAndNsrsbh(
            @ApiParam(name = "xhfNsrsbh", value = "纳税人识别号id,数组形式", required = true) @RequestParam(name="xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "ywlxName", value = "业务类型名称", required = false) @RequestParam(name = "ywlxName", required = false) String ywlxName
    ) {
        Map<String, Object> map = new HashMap<>(5);
    
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        if (StringUtils.isNotBlank(ywlxName)) {
            map.put("ywlxName", ywlxName);
        }
    
        List<Map<String, Object>> resultList = apiBusinessTypeService.queryYwlxOrNsrsbh(map, shList);
        return R.ok().put("page", resultList);
    }
    
    /**
     * 业务类型修改
     *
     * @param businessName
     * @param xhfNsrsbh
     * @param description
     * @return BusinessTypeController.java
     * author wangruwei
     * 2019年7月11日
     */
    @SysLog(operation = "业务类型更新操作", operationDesc = "业务类型更新", key = "业务类型")
    @ApiOperation(value = "业务类型更新", notes = "业务类型-业务类型更新")
    @PostMapping("/updateYwlx")
    public R updateYwlx(
            @ApiParam(name = "id", value = "主键id", required = true) @RequestParam(name = "id", required = true) String id,
            @ApiParam(name = "businessName", value = "业务类型名称", required = true) @RequestParam(name = "businessName", required = true) String businessName,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(name = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "xhfMc", value = "销货方名称", required = true) @RequestParam(name = "xhfMc", required = true) String xhfMc,
            @ApiParam(name = "description", value = "业务类型描述", required = true) @RequestParam(name = "description", required = true) String description
    ) {
        if (StringUtils.isBlank(id)) {
            return R.error("id不能为空");
        }
        if (StringUtils.isBlank(businessName)) {
            return R.error("业务类型名称不能为空");
        }
    
        if (StringUtils.isBlank(xhfMc)) {
            return R.error("销货方名称不能为空");
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        log.info("父级纳税人识别号：{}", JsonUtils.getInstance().toJsonString(shList));
        if (shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = shList.get(0);
    
    
        BusinessTypeInfo businessTypeInfo = new BusinessTypeInfo();
        businessTypeInfo.setId(id);
        businessTypeInfo.setBusinessName(businessName);
        businessTypeInfo.setXhfNsrsbh(nsrsbh);
        businessTypeInfo.setXhfMc(xhfMc);
        businessTypeInfo.setDescription(description);
        businessTypeInfo.setCreateTime(new Date());
        businessTypeInfo.setUpdateTime(new Date());
        //验证，同一销货方下面的属性名称不能一样
        BusinessTypeInfo isHave = apiBusinessTypeService.queryYwlxInfoByNameAndNsrsbhAndId(businessName, shList,id);
        
        if (null != isHave) {
            return R.error(businessName + "已存在");
        } else {
            apiBusinessTypeService.updateYwlxInfo(businessTypeInfo, shList);
            return R.ok();
        }
    }
    
    @PostMapping("/deleteYwlx")
    @SysLog(operation = "业务类型删除", operationDesc = "业务类型删除", key = "业务类型")
    @ApiOperation(value = "业务类型删除", notes = "业务类型-业务类型删除")
    public R deleteYwlx(
            @ApiParam(name = "id", value = "主键id", required = true) @RequestParam(name = "id", required = true) String id,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(name = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        log.debug("业务类型删除接口，页面入参id:{} 税号:{}", id, xhfNsrsbh);
        if (StringUtils.isBlank(id)) {
            return R.error("id不能为空");
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
    
        log.info("父级纳税人识别号：{}", JsonUtils.getInstance().toJsonString(shList));
        if (shList.size() > 1) {
            log.error("{}当前操作不支持多税号进行操作.请求参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(shList));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
    
        BusinessTypeInfo businessTypeInfo = new BusinessTypeInfo();
        businessTypeInfo.setId(id);
        businessTypeInfo.setStatus(ConfigureConstant.STRING_1);
        businessTypeInfo.setXhfNsrsbh(shList.get(0));
        if (apiBusinessTypeService.updateYwlxInfo(businessTypeInfo, shList)) {
            return R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000).put(OrderManagementConstant.MESSAGE, "删除成功!");
        } else {
            log.error("业务类型删除失败");
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "删除失败");
        }
    }
    
    
}
