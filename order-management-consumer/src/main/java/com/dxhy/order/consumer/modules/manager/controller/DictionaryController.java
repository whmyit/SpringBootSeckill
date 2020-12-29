package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiDictionaryService;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.DictionaryEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字典信息控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:13
 */
@Slf4j
@RestController
@RequestMapping("/dictionaries")
@Api(value = "字典信息", tags = {"管理模块"})
public class DictionaryController {
    @Reference
    private ApiDictionaryService service;
    
    @RequestMapping("/{type}")
    @ApiOperation(value = "字典查询", notes = "字典信息管理-字典查询")
    public R info(@PathVariable("type") String type) {
        List<DictionaryEntity> dictionaries = service.queryDictionaries(type);
        
        return R.ok().put("dictionaries", dictionaries);
    }
}
