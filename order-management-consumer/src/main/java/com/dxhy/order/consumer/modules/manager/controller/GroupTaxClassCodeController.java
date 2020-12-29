package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiCommodityService;
import com.dxhy.order.api.ApiGroupTaxClassCodeService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.TaxPlayerCodeDept;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.consumer.utils.ExcelUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupTaxClassCodeEntity;
import com.dxhy.order.model.page.QueryPage;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 集团税收分类编码
 * @Author:xueanna
 * @Date:2019/9/17
 */
@Slf4j
@RequestMapping("/groupTaxClassCodeController")
@RestController
@Api(value = "集团商品信息", tags = {"管理模块"})
public class GroupTaxClassCodeController {
    
    @Reference
    private ApiGroupTaxClassCodeService apiGroupTaxClassCodeService;
    
    @Reference
    private ApiCommodityService commodityService;
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 查询集团商品税编库接口
     *
     * @param map
     * @return
     */
    @ApiOperation(value = "查询集团商品税编库列表接口", notes = "集团商品信息-查询集团商品税编库列表接口")
    @SysLog(operation = "查询集团商品税编库接口", operationDesc = "查询集团商品税编库列表接口", key = "查询集团商品税编库接口")
    @PostMapping("/queryGroupTaxClassCode")
    public R queryGroupTaxClassCode(
            @ApiParam(name = "map", value = "查询参数", required = true)
            @RequestParam(required = true) Map<String,String> map) {
        DeptEntity dept = userInfoService.getDepartment();
        log.info(String.valueOf(dept.getParentId()));
        List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
        taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (taxplayerCodeList.size() > 1) {
            //集团账号
            map.put("deptId", String.valueOf(dept.getDeptId()));
        } else {
            log.info("非集团账户，查询企业的上级集团的数据");
            map.put("deptId", String.valueOf(dept.getParentId()));
        }
        log.info("参数 {}", JsonUtils.getInstance().toJsonString(map));
        //查询列表数据
        QueryPage query = new QueryPage(map);
        com.dxhy.order.model.PageUtils pageUtil = apiGroupTaxClassCodeService.queryGroupTaxClassCode(query);
        return R.ok().put("page", pageUtil);
    }
    
    /**
     * 集团模板下载接口
     *
     * @date: Created on 2018年7月25日 下午3:30:28
     */
    @ApiOperation(value = "集团模板下载接口", notes = "集团商品信息-下载集团模板下载接口文件")
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    @SysLog(operation = "集团模板下载接口", operationDesc = "集团模板下载接口", key = "集团模板下载接口")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        log.info("集团模板下载接口");
        try {
            String resource = "";
            //weblogic获取附件地址单独处理
            if (ConfigureConstant.STRING_1.equals(SystemConfig.webServerType)) {
                resource = SystemConfig.downloadFileUrl + "CS_new.xlsx";
                log.info("文件路径：{}", resource);
            } else {
                resource = Thread.currentThread().getContextClassLoader().getResource("download/GroupTaxClassCodeExcel.xlsx").getPath();
            }
            response.reset();
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String("集团税编模板".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            response.getOutputStream().write(FileUtils.readFileToByteArray(new File(resource)));
            log.info("集团税编模板下载完毕");
        } catch (Exception e) {
            log.error("集团税编模板下载异常 e:{}", e);
            throw new RuntimeException("集团税编模板下载异常");
        }
    }
    
    /**
     * 采集下级税编
     */
    @ApiOperation(value = "采集下级税编接口", notes = "集团商品信息-采集下级税编")
    @PostMapping("/collectSubordinateTaxCode")
    @SysLog(operation = "采集下级税编接口", operationDesc = "采集下级税编接口", key = "采集下级税编接口")
    public R collectSubordinateTaxCode(HttpServletRequest request, HttpServletResponse response) {
        log.info("采集下级税编接口");
        R r;
        try {
            DeptEntity dept = userInfoService.getDepartment();
            List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
            taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (taxplayerCodeList.size() > 1) {
                //集团账号
                UserEntity user = userInfoService.getUser();
                List<TaxPlayerCodeDept> deptList = user.getTaxplayercodeDeptList();
                List<String> taxpayerCodeList = new ArrayList<>();
                for (DeptEntity entity : deptList) {
                    if (StringUtils.isNotEmpty(entity.getTaxpayerCode())) {
                        taxpayerCodeList.add(entity.getTaxpayerCode());
                    }
                }
                //采集税编
                r = apiGroupTaxClassCodeService.collectTaxClassCode(taxpayerCodeList, String.valueOf(dept.getDeptId()));
            } else {
                return R.error("非集团账户");
            }
        } catch (Exception e) {
            log.error("采集下级税编接口 e:{}", e);
            return R.error("采集下级税编接口异常");
        }
        return r;
    }
    
    /**
     * 集团税编库处理共享数据状态
     *
     * @param groupTaxClassCodeIds 集团id
     * @param shareStatus          共享状态 0-允许共享；1-待核实；
     */
    @ApiOperation(value = "集团税编库处理共享数据状态", notes = "集团商品信息-集团税编库处理共享数据状态")
    @PostMapping("/taxClassCodeHandleShareStatus")
    @SysLog(operation = "集团税编库处理共享数据状态", operationDesc = "集团税编库处理共享数据状态", key = "集团税编库处理共享数据状态")
    public R taxClassCodeHandleShareStatus(@RequestParam(value = "groupTaxClassCodeIds") String groupTaxClassCodeIds, String shareStatus) {
        log.info("集团税编库共享数据状态处理");
        R r;
        try {
            if (StringUtils.isEmpty(groupTaxClassCodeIds) || StringUtils.isEmpty(shareStatus)) {
                return R.error("参数错误");
            }
            String[] taxClassCodeIdArray = JsonUtils.getInstance().parseObject(groupTaxClassCodeIds, String[].class);
            r = apiGroupTaxClassCodeService.taxClassCodeHandleShareStatus(taxClassCodeIdArray, shareStatus);
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 集团税编库启用数据状态处理
     *
     * @param groupTaxClassCodeIds 集团id
     * @param dataStatus           数据状态 0-启用；1-停用
     */
    @ApiOperation(value = "集团税编库启用数据状态处理", notes = "集团商品信息-集团税编库启用数据状态处理")
    @PostMapping("/taxClassCodeHandleDataStatus")
    @SysLog(operation = "集团税编库启用数据状态处理", operationDesc = "集团税编库启用数据状态处理", key = "集团税编库启用数据状态处理")
    public R taxClassCodeHandleDataStatus(@RequestParam(value = "groupTaxClassCodeIds") String groupTaxClassCodeIds, String dataStatus) {
        log.info("集团税编库启用数据状态处理");
        R r;
        try {
            if (StringUtils.isEmpty(groupTaxClassCodeIds) || StringUtils.isEmpty(dataStatus)) {
                return R.error("参数错误");
            }
            String[] taxClassCodeIdArray = JsonUtils.getInstance().parseObject(groupTaxClassCodeIds, String[].class);
            r = apiGroupTaxClassCodeService.taxClassCodeHandleDataStatus(taxClassCodeIdArray, dataStatus);
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 集团税编库新增
     */
    @ApiOperation(value = "新增集团税编库", notes = "集团商品信息-新增集团税编库")
    @PostMapping("/saveGroupTaxClassCode")
    @SysLog(operation = "新增集团税编库", operationDesc = "新增集团税编库", key = "新增集团税编库")
    public R saveGroupTaxClassCode(@RequestBody(required = true) String groupTaxClassCodeStr) {
        log.info("新增集团税编库,入参:{}", groupTaxClassCodeStr);
        R r;
        try {
            if (StringUtils.isEmpty(groupTaxClassCodeStr)) {
                return R.error("参数错误");
            }
            DeptEntity dept = userInfoService.getDepartment();
            List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
            taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (taxplayerCodeList.size() <= 1) {
                //非集团账号
                return R.error("非集团账户,不能添加");
            }
            GroupTaxClassCodeEntity groupTaxClassCodeEntity = JsonUtils.getInstance().parseObject(groupTaxClassCodeStr, GroupTaxClassCodeEntity.class);
            r = apiGroupTaxClassCodeService.saveGroupTaxClassCode(groupTaxClassCodeEntity, String.valueOf(dept.getDeptId()));
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 集团税编详情
     */
    @ApiOperation(value = "集团税编详情", notes = "集团商品信息-集团税编详情")
    @PostMapping("/getGroupTaxClassCodeDetail")
    @SysLog(operation = "集团税编详情", operationDesc = "集团税编详情", key = "新增集团税编库")
    public R getGroupTaxClassCodeDetail(@RequestParam(value = "groupTaxClassCodeId") String groupTaxClassCodeId) {
        log.info("新增集团税编库");
        R r;
        try {
            if (StringUtils.isEmpty(groupTaxClassCodeId)) {
                return R.error("参数错误");
            }
            r = apiGroupTaxClassCodeService.queryGroupTaxClassCodeDetail(groupTaxClassCodeId);
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 删除集团税编 逻辑删除
     */
    @ApiOperation(value = "删除集团税编", notes = "集团商品信息-删除集团税编")
    @PostMapping("/delGroupTaxClassCode")
    @SysLog(operation = "删除集团税编", operationDesc = "删除集团税编", key = "删除集团税编")
    public R delGroupTaxClassCode(@RequestParam(value = "groupTaxClassCodeId") String groupTaxClassCodeId) {
        log.info("删除集团税编");
        R r;
        try {
            if (StringUtils.isEmpty(groupTaxClassCodeId)) {
                return R.error("参数错误");
            }
            DeptEntity dept = userInfoService.getDepartment();
            List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
            taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (taxplayerCodeList.size() <= 1) {
                //非集团账号
                return R.error("非集团账户,不能删除");
            }
            r = apiGroupTaxClassCodeService.delGroupTaxClassCode(groupTaxClassCodeId, String.valueOf(dept.getDeptId()));
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 修改集团税编信息
     */
    @ApiOperation(value = "修改集团税编信息", notes = "集团商品信息-修改集团税编信息")
    @PostMapping("/updateGroupTaxClassCode")
    @SysLog(operation = "修改集团税编信息", operationDesc = "修改集团税编信息", key = "修改集团税编信息")
    public R updateGroupTaxClassCode(@RequestBody(required = true) String groupTaxClassCodeStr) {
        log.info("修改集团税编");
        R r;
        try {
            DeptEntity dept = userInfoService.getDepartment();
            List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
            taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (taxplayerCodeList.size() <= 1) {
                //集团账号
                return R.error("非集团账户");
            }
            GroupTaxClassCodeEntity groupTaxClassCodeEntity = JsonUtils.getInstance().parseObject(groupTaxClassCodeStr, GroupTaxClassCodeEntity.class);
            if (StringUtils.isEmpty(groupTaxClassCodeEntity.getId())) {
                return R.error("集团税编id不能为空");
            }
            r = apiGroupTaxClassCodeService.updateGroupTaxClassCode(groupTaxClassCodeEntity);
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 集团模板导入
     *
     * @param file
     * @throws Exception
     */
    @PostMapping("/uploadGroupTaxClassCodeExcel")
    @ApiOperation(value = "集团税编信息模板导入", notes = "集团商品信息-集团税编信息模板导入")
    @SysLog(operation = "集团税编信息模板导入", operationDesc = "集团税编信息模板导入", key = "集团税编信息")
    public R uploadGroupTaxClassCodeExcel(@RequestParam("file") MultipartFile file) throws Exception {
        log.debug("上传集团excel信息");
        Map<String, String> map = new HashMap<>(5);
        DeptEntity dept = userInfoService.getDepartment();
        List<String> taxplayerCodeList = userInfoService.getTaxpayerCodeList();
        taxplayerCodeList = taxplayerCodeList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (taxplayerCodeList.size() > 1) {
            //集团账号
            map.put("deptId", String.valueOf(dept.getDeptId()));
        } else {
            return R.error("不是集团账号，不能导入集团模板");
        }
        //获取当前登录人ID
        Long userId = userInfoService.getUser().getUserId();
        map.put("userId", String.valueOf(userId));
        List<GroupTaxClassCodeEntity> commodityCodeEntities = ExcelUtils.getExcelGroupTaxClassCodeEntityInfo(file.getInputStream(), file.getOriginalFilename());
        R r = noIntelligenceCode(commodityCodeEntities, map);
    
        return r;
    }
    
    /**
     * 不使用智能编码
     *
     * @param commodityCodeEntityList
     * @param paraMap
     * @return
     */
    private R noIntelligenceCode(List<GroupTaxClassCodeEntity> commodityCodeEntityList, Map<String, String> paraMap) {
        
        String deptId = paraMap.get("deptId");
        log.info("集团Id：{}", deptId);
        log.debug("商品编码导入");
        for (GroupTaxClassCodeEntity groupTaxClassCodeEntity : commodityCodeEntityList) {
            groupTaxClassCodeEntity.setDeptId(deptId);
        }
        return apiGroupTaxClassCodeService.uploadCommodityCode(commodityCodeEntityList, paraMap);
    }
}
