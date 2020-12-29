package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiTaxClassCodeService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.R;
import com.dxhy.order.model.page.QueryPage;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 税收分类编码查询控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:20
 */
@Slf4j
@RequestMapping("/taxClassCode")
@RestController
@Api(value = "税编信息", tags = {"管理模块"})
public class TaxClassCodeController {
    
    @Reference
    private ApiTaxClassCodeService taxClassCodeService;
    
    /**
     * 在使用
     * 查询税收分类编码
     *
     * @param map
     * @return
     */
    @PostMapping("/queryTaxClassCode")
    @ApiOperation(value = "税编查询", notes = "税编信息管理-税编查询")
    @SysLog(operation = "税编查询", operationDesc = "税编查询", key = "税编查询")
    public R queryTaxClassCode(@RequestParam Map<String, Object> map) {
        log.info("参数 {}", map);
        //查询列表数据
        QueryPage query = new QueryPage(map);
        //新增局端税编表功能,查询所有税编
        if (!ObjectUtils.isEmpty(map.get(ConfigureConstant.STRING_IS_FLAG))) {
            query.put(ConfigureConstant.STRING_IS_FLAG, ConfigureConstant.STRING_0);
        }
        com.dxhy.order.model.PageUtils taxClassCodeList = taxClassCodeService.queryTaxClassCode(query);
        log.info("结果 {}", JsonUtils.getInstance().toJsonString(taxClassCodeList));
        return R.ok().put("page", taxClassCodeList);
    }
//    /**
//     * 查询税收分类编码详情
//     * @param taxClassCodeID
//     * @return
//     */
//    @PostMapping("/queryTaxClassCodeDetail")
//    @ResponseBody
//    public R queryTaxClassCodeDetail(@RequestParam String taxClassCodeID) {
//        log.info("税局税编详情,参数 {}", taxClassCodeID);
//        //查询列表数据
//        TaxClassCodeEntity taxClassCodeEntity = taxClassCodeService.queryTaxClassCodeById(taxClassCodeID);
//        if(ObjectUtils.isEmpty(taxClassCodeEntity)){
//            return R.error("没有查询到税编信息，参数id为{}",taxClassCodeID);
//        }
//        return R.ok().put("data", taxClassCodeEntity);
//    }
}
