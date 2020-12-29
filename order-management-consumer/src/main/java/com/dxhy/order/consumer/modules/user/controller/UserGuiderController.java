package com.dxhy.order.consumer.modules.user.controller;

import com.dxhy.order.api.ApiUserGuiderService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.model.R;
import com.dxhy.order.model.UserGuiderInfo;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户引导控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:46
 */
@Slf4j
@RestController
@Api(value = "用户引导", tags = {"管理模块"})
@RequestMapping("/userGuider")
public class UserGuiderController{

    private static final String LOGGER_MSG = "用户引导控制层";
    
    @Reference
    private ApiUserGuiderService apiUserGuiderService;
    
    @Reference
    private RedisService redisService;


    @PostMapping("/queryUserGuider")
    @SysLog(operation = "查询用户引导", operationDesc = "查询用户引导", key = "用户引导")
    public R queryUserGuider(@RequestBody UserGuiderInfo queryUserGuider) {

        log.debug("用户引导步骤查询，入参:{}",JsonUtils.getInstance().toJsonString(queryUserGuider));
        if(StringUtils.isBlank(queryUserGuider.getXhfNsrsbh()) || StringUtils.isBlank(queryUserGuider.getUid())){
            return R.error().put(OrderManagementConstant.MESSAGE,"请求参数不能为空");
        }
        List<UserGuiderInfo> userGuiderInfoList = apiUserGuiderService.queryUserGuiderList(queryUserGuider);
        return R.ok().put("data",userGuiderInfoList);

    }


    @PostMapping("/updateUserGuider")
    @SysLog(operation = "更新用户引导执行状态", operationDesc = "更新用户引导执行状", key = "用户引导")
    public R updateUserGuider(@RequestBody UserGuiderInfo userGuiderInfo) {

        boolean isSuccess = apiUserGuiderService.updateUserGuider(userGuiderInfo);
        if(isSuccess){
            return R.ok();
        }else{
            return R.error();
        }

    }


    @PostMapping("/queryUserGuiderFirstStatus")
    @SysLog(operation = "更新用户引导执行状态", operationDesc = "更新用户引导执行状", key = "用户引导")
    public R setUserGuiderFirstStatus(
            @ApiParam(name = "uid", value = "当前用户唯一标识", required = true) @RequestParam(name = "uid", required = true) String uid,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(name = "xhfNsrsbh", required = false) String xhfNsrsbh,
            @ApiParam(name = "operateGroup", value = "操作分组", required = true) @RequestParam(name = "operateGroup", required = true) String operateGroup) {

        String str = uid + "_" + operateGroup;
        String s = redisService.get(String.format(Constant.REDIS_USER_GUIDER, str));
        if (ConfigureConstant.STRING_1.equals(s)) {
            return R.ok();
        
        } else {
            redisService.set(String.format(Constant.REDIS_USER_GUIDER, str), "1");
            return R.error();
        
        
        }

    }

    public static void main(String[] args) {
        UserGuiderInfo guider = new UserGuiderInfo();
        String s = JsonUtils.getInstance().toJsonStringNullToEmpty(guider);
        System.out.println(s);
    }


}
