package com.dxhy.order.consumer.modules.manager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.ApiBuyerService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.ExcelUtils;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.page.QueryPage;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 购方信息控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:11
 */
@Slf4j
@RestController
@Api(value = "购方信息", tags = {"管理模块"})
@RequestMapping("/buyer")
public class BuyerController {
    private static final String LOGGER_MSG = "购方信息控制层";
    
    @Reference
    private ApiBuyerService buyerService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 查询购方信息数据
     * 在使用
     *
     * @param map
     * @return
     */
    @PostMapping("/queryBuyerList")
    @ApiOperation(value = "购方信息列表", notes = "购方信息管理-购方信息列表")
    @SysLog(operation = "购方信息列表查询", operationDesc = "购方信息列表查询", key = "购方信息管理")
    public R queryBuyerList(@RequestParam Map<String, Object> map) {
        String xhfNsrsbh = (String) map.get("xhfNsrsbh");
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        
        log.info("所属销方纳税人识别号：{}", JsonUtils.getInstance().toJsonString(xfshs));
        
        List<String> shList = Arrays.asList(xfshs);
    
        log.info("客户信息列表查询参数：{}", map);
        QueryPage query = new QueryPage(map);
        PageUtils page = buyerService.queryBuyerList(query,shList);
        return R.ok().put("page", page);
    }
    
    /**
     * 保存购方信息
     *
     * @param buyerEntity
     * @return
     */
    @SysLog(operation = "购方信息保存", operationDesc = "购方信息保存", key = "购方信息管理")
    @ApiOperation(value = "购方信息保存", notes = "购方信息管理-购方信息保存")
    @PostMapping("/saveBuyer")
    public R saveBuyer(BuyerEntity buyerEntity) {
        if (buyerEntity == null) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        } else if (StringUtils.isBlank(buyerEntity.getXhfNsrsbh())) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        
        //购货方企业类型 默认值01 企业
        buyerEntity.setGhfQylx(OrderInfoEnum.GHF_QYLX_01.getKey());
        
        //购方名称 带中文括号的转换成英文括号
        buyerEntity.setPurchaseName(StringUtil.replaceStr(buyerEntity.getPurchaseName(), true));
        
        //获取当前登录人id
        Long userId = userInfoService.getUser().getUserId();
        String id = buyerEntity.getId();
        if (StringUtils.isNotBlank(id)) {
            //修改
            buyerEntity.setModifyUserId(userId.toString());
        } else {
            //添加
            buyerEntity.setModifyUserId(userId.toString());
            buyerEntity.setCreateUserId(userId.toString());
        }
        R r = buyerService.saveOrUpdateBuyerInfo(buyerEntity);
        return r;
    }
    
    
    @SysLog(operation = "购方信息保存", operationDesc = "购方信息保存", key = "购方信息管理")
    @ApiOperation(value = "购方信息批量保存", notes = "购方信息管理-购方信息批量保存")
    @PostMapping("/saveBuyerInfoList")
    public R saveBuyerInfoList(@RequestBody List<BuyerEntity> buyerList) throws Exception {
    
        //业务逻辑放到provider
        return buyerService.saveBuyerInfoList(buyerList,
                userInfoService.getUser().getUserId() == null ? "" : String.valueOf(userInfoService.getUser().getUserId()));
    }

    @PostMapping("/removeBuyerbyId")
    @ApiOperation(value = "购方信息删除", notes = "购方信息管理-购方信息删除")
    public R removeBuyerbyId(@RequestBody String ids) {
        log.info("删除购方信息参数 {}", ids);
        if (StringUtils.isBlank(ids)) {
            return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
        }
        
        List<Map> idList = JSON.parseArray(ids, Map.class);
        return buyerService.removeBuyerbyId(idList);
    }
    
    /**
     * excel导入
     *
     * @param file
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/uploadExcel")
    @ApiOperation(value = "购方信息导入", notes = "购方信息管理-购方信息导入")
    @SysLog(operation = "购方信息导入", operationDesc = "购方信息excel导入", key = "购方信息管理")
    public R uploadExcel(HttpServletResponse response, HttpServletRequest request,
                         @ApiParam(name = "file", value = "导入的excel文件", required = true) @RequestParam(value = "file", required = true) MultipartFile file,
                         @ApiParam(name = "size", value = "文件大小", required = true) @RequestParam(value = "size", required = true) String size,
                         @ApiParam(name = "lastModifiedDate", value = "上次传输时间", required = true) @RequestParam(value = "lastModifiedDate", required = true) String lastModifiedDate,
                         @ApiParam(name = "xhfYh", value = "销货方银行", required = true) @RequestParam(value = "xhfYh", required = true) String xhfYh,
                         @ApiParam(name = "xhfDz", value = "销货方地址", required = true) @RequestParam(value = "xhfDz", required = true) String xhfDz,
                         @ApiParam(name = "xhfDh", value = "销货方电话", required = true) @RequestParam(value = "xhfDh", required = true) String xhfDh,
                         @ApiParam(name = "xhfZh", value = "销货方账号", required = true) @RequestParam(value = "xhfZh", required = true) String xhfZh,
                         @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
                         @ApiParam(name = "xhfMc", value = "销货方纳税人名称", required = true) @RequestParam(value = "xhfMc", required = true) String xhfMc) throws Exception {
        log.info("所属销方纳税人识别号：{}", xhfNsrsbh);
        
        List<BuyerEntity> buyerEntityList = ExcelUtils.getExcelBuyerEntityInfo(file.getInputStream(), file.getOriginalFilename());
        for (BuyerEntity buyerEntity : buyerEntityList) {
            buyerEntity.setXhfNsrsbh(xhfNsrsbh);
            buyerEntity.setXhfMc(xhfMc);
            buyerEntity.setGhfQylx(OrderInfoEnum.GHF_QYLX_01.getKey());
            //购方名称 带中文括号的转换成英文括号
            buyerEntity.setPurchaseName(StringUtil.replaceStr(buyerEntity.getPurchaseName(), true));
        }
        R r = buyerService.uploadGrop(buyerEntityList);
        return r;
    }
    
    /**
     * @param purchaseName 够方名称
     * @Description 购方模糊信息查询
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryBuyerInfoList")
    @ApiOperation(value = "购方信息模糊查询", notes = "购方信息管理-购方信息模糊查询")
    @SysLog(operation = "购方信息模糊查询", operationDesc = "购方信息模糊查询操作", key = "购方信息管理")
    public R queryBuyerInfoList(@RequestParam String purchaseName, @RequestParam String xhfNsrsbh) {
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error();
        }
        
        List<String> taxplayerCodeList = JSON.parseArray(xhfNsrsbh, String.class);
        log.info("父级纳税人识别号：{}", taxplayerCodeList);
        log.info("{}查询购方发票列表开始 参数 {}", LOGGER_MSG, purchaseName, taxplayerCodeList);
        List<BuyerEntity> dataList = buyerService.queryBuyerByName(purchaseName, taxplayerCodeList);
        log.info("{}本地调用购方发票列表结果{}", LOGGER_MSG, dataList);
        if (CollectionUtils.isEmpty(dataList)) {
            log.info("{}本地调用购方发票列表数据为空，开始调用大数据接口", LOGGER_MSG);
            //dataList = queryEnterpriseInfo(purchaseName);
            log.info("{}本地调用大数据接口 结果{}", LOGGER_MSG, dataList);
        }
        return R.ok().put("data", dataList);
    }
    
    /**
     * @Description 调用大象云平台企业模糊查询
     * @Author xieyuanqiang
     * @Date 9:38 2018-08-09
     */
    private List<BuyerEntity> queryEnterpriseInfo(String purchaseName) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("Authorization", "36988e62ae0dd");
    
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("ptcode", "XiaoXiang");
        jsonObj.put("nsrmc", purchaseName);
        List<BuyerEntity> dataList = new ArrayList<>();
        HttpEntity<String> formEntity = new HttpEntity<>(jsonObj.toString(), headers);
        try {
            List resultStr = new RestTemplate().postForObject("https://qypt.ele-cloud.com/enterprise/platform/fuzzyQuery", formEntity, List.class);
            log.info("{}调用大象云平台企业模糊查询  {} ", LOGGER_MSG, JsonUtils.getInstance().toJsonString(resultStr));
            if (!resultStr.isEmpty()) {
                for (Object obj : resultStr) {
                    JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                    BuyerEntity buyerEntity = new BuyerEntity();
                    buyerEntity.setPurchaseName(jsonObject.get("nsrmc") + "");
                    buyerEntity.setTaxpayerCode(jsonObject.get("nsrsbh") + "");
                    dataList.add(buyerEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
    
}
