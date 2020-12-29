package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiFpExpressService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.dto.KdniaoQueryReq;
import com.dxhy.order.model.dto.KdniaoRes;
import com.dxhy.order.model.entity.FpExpress;
import com.dxhy.order.model.page.QueryPage;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 快递信息控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:51
 */
@Slf4j
@RestController
@RequestMapping("/fpExpress")
@Api(value = "快递信息", tags = {"管理模块"})
public class FpExpressController {
    private static final String LOGGER_MSG = "(发票快递控制层)";
    @Reference
    private ApiFpExpressService fpExpressService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 分页查询快递单列表
     *
     * @param map
     * @return
     */
    @PostMapping("/queryListByPage")
    @ApiOperation(value = "快递列表", notes = "快递信息管理-查询快递列表")
    @SysLog(operation = "查询快递列表", operationDesc = "查询快递列表", key = "快递信息管理")
    public R queryListByPage(@RequestParam Map<String, Object> map) {
        log.info("参数{}", map);
        UserEntity u = userInfoService.getUser();
        if (u == null) {
            return R.error().put("msg", "用户未登录");
        }
        map.put("userId", u.getUserId().toString());
    
    
        //分页查询
        QueryPage query = new QueryPage(map);
        PageUtils pageUtils = fpExpressService.queryListByPage(query);
        return R.ok().put("page", pageUtils);
    }
    
    /**
     * 查询快递公司名称列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/expressCompanyList", method = RequestMethod.GET)
    @ApiOperation(value = "快递公司列表", notes = "快递信息管理-查询快递公司列表")
    @SysLog(operation = "查询快递公司列表", operationDesc = "查询快递公司列表", key = "快递信息管理")
    public R expressCompanyList(@RequestParam Map<String, Object> map) {
        log.debug("{}查询快递公司名称列表开始,入参为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
        UserEntity u = userInfoService.getUser();
    
        log.debug("{}查询到当前登录用户信息为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(u));
        if (u == null) {
            return R.error().put("msg", "用户未登录");
        }
    
        map.put("userId", u.getUserId().toString());
        log.debug("{}查询快递公司请求参数为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
        List<FpExpress> expressCompanyList = fpExpressService.expressCompanyList(map);
        log.debug("{}查询快递公司,返回数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(expressCompanyList));
        return R.ok().put("expressCompanyList", expressCompanyList);
    }
    
    /**
     * 生成快递单
     *
     * @param fpExpress
     * @return
     */
    @PostMapping("/track")
    @ApiOperation(value = "生成快递单", notes = "快递信息管理-生成快递单")
    @SysLog(operation = "生成快递单", operationDesc = "生成快递单", key = "快递信息管理")
    public R track(FpExpress fpExpress) {
        log.info("参数{}", fpExpress.toString());
        
        try {
            UserEntity u = userInfoService.getUser();
            if (u == null) {
                return R.error().put("msg", "用户未登录");
            }
            fpExpress.setUserId(u.getUserId().toString());
            fpExpress.setId(apiInvoiceCommonService.getGenerateShotKey());
    
            DeptEntity department = userInfoService.getDepartment();
            if (department != null) {
                fpExpress.setOrgId(department.getDeptId());
            }
            fpExpressService.track(fpExpress);
        } catch (Exception e) {
            log.info("{},生成快递单异常:", LOGGER_MSG, e);
            return R.error();
        }
        return R.ok().put("expCode", fpExpress.getExpressCompanyCode()).put("orderCode", fpExpress.getId()).put("expNo", fpExpress.getExpressNumber());
    }
    
    /**
     * 查询快递跟踪轨迹
     *
     * @param req
     * @return
     */
    @PostMapping("/query")
    @ApiOperation(value = "快递信息跟踪", notes = "快递信息管理-快递信息跟踪")
    @SysLog(operation = "快递信息跟踪", operationDesc = "快递信息跟踪", key = "快递信息管理")
    public R query(KdniaoQueryReq req) {
        log.info("参数{}", req.toString());
        try {
            KdniaoRes res = fpExpressService.getOrderTraces(req);
            if (res == null) {
                return R.error().put("msg", "查询该快递单失败");
            }
            return R.ok().put("r", res);
        } catch (Exception e) {
            log.info("{},生成快递单异常:", LOGGER_MSG, e);
            return R.error();
        }
    }
    
    
    /**
     * 查询快递跟踪轨迹
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新快递", notes = "快递信息管理-更新快递信息")
    @SysLog(operation = "更新快递信息", operationDesc = "更新快递信息", key = "快递信息管理")
    public R update(FpExpress fpExpress) {
        log.info("参数{}", JsonUtils.getInstance().toJsonString(fpExpress));
        try {
            int i = fpExpressService.updateExpressInfo(fpExpress);
            if (i <= 0) {
                return R.error();
            } else {
                return R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            }
        } catch (Exception e) {
            log.info("{},生成快递单异常:", LOGGER_MSG, e);
            return R.error();
        }
    }
    
}
