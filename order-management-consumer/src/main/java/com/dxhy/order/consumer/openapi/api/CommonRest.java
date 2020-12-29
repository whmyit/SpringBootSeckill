package com.dxhy.order.consumer.openapi.api;

import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.constant.RespStatusEnum;
import com.dxhy.order.consumer.openapi.service.IDynamicCodeInterfaceServiceV3;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.order.DYNAMIC_CODE_RSP;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 订单对外接口入口
 *
 * @author ZSC-DXHY
 */
@RestController
@Api(value = "订单通用对外接口", tags = {"订单接口模块"})
@Slf4j
public class CommonRest {
    
    private static final String LOGGER_MSG = "(订单统一对外路由)";
    
    @Resource
    private IDynamicCodeInterfaceServiceV3 dynamicCodeInterfaceServiceV3;
    
    @Reference
    private RedisService redisService;
    
    
    private static final String SHORT_MESSAE_TQM_PREFIX = "sims_notes_tqm_";
    
    
    @ApiOperation(value = "订单对外接口-二维码跳转", notes = "订单对外接口-二维码跳转入口", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequestMapping(value = "/api/{interfaceVersion}/{tqm}", method = {RequestMethod.POST, RequestMethod.GET})
    public Result ewmRedirect(@PathVariable("interfaceVersion") String interfaceVersion,
                              @PathVariable("tqm") String tqm, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}:二维码短码转长码业务处理开始:", LOGGER_MSG);
        Result result = new Result();
        ResponseStatus responseStatus = new ResponseStatus();
        try {
        
            /**
             * 校验接口入参是否为空
             */
            result = checkEWMInterfaceParam(interfaceVersion, tqm);
            
            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);
            
            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},数据格式校验未通过.", LOGGER_MSG);
                return result;
            }
            
            /**
             * 根据提取码去数据库中查询数据
             */
            DYNAMIC_CODE_RSP ewmurlByTqm = dynamicCodeInterfaceServiceV3.getEwmUrlByTqm(tqm);
            
            if (OrderInfoContentEnum.INVOICE_ERROR_CODE_010000_V3.getKey().equals(ewmurlByTqm.getSTATUS_CODE())) {
                //获取公众号配置的appid
                log.info("{}:调用二维码短码转长码接口成功:重定向到:{}", LOGGER_MSG, ewmurlByTqm.getDYNAMIC_CODE_URL());
                response.sendRedirect(ewmurlByTqm.getDYNAMIC_CODE_URL());
                return null;
            } else {
                log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, ewmurlByTqm.getSTATUS_MESSAGE());
                return Result.error(new ResponseStatus(ewmurlByTqm.getSTATUS_CODE(), ewmurlByTqm.getSTATUS_MESSAGE()));
            }
            
        } catch (Exception e) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, e);
            return Result.error(new ResponseStatus(OrderInfoContentEnum.EWM_ERROR_CODE_205999.getKey(), OrderInfoContentEnum.EWM_ERROR_CODE_205999.getMessage()));
        }
    }
    
    @ApiOperation(value = "订单对外接口-短信短链接跳转", notes = "订单对外接口-短信短链接", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequestMapping(value = "/notes/{tqm}", method = {RequestMethod.POST, RequestMethod.GET})
    public Result shortMsgRedirect(@PathVariable("tqm") String tqm, HttpServletRequest request, HttpServletResponse response) {
        log.info("{}:二维码短码转长码业务处理开始:", LOGGER_MSG);
        Result result = new Result();
        String string = redisService.get(SHORT_MESSAE_TQM_PREFIX + tqm);
        if (StringUtils.isBlank(string)) {
            log.error("提取码不存在!,tqm:{}", tqm);
            result.put("code", "009999");
            result.put("message", "提取码不存在!");
            return result;
        } else {
            try {
                response.sendRedirect(OpenApiConfig.myFrontUrl + "?tqm=" + tqm);
            } catch (IOException e) {
                log.error("短信预览pdf短链接预览跳转异常，异常信息:{}", e);
            }
            return null;
        }
    }
    
    
    /**
     * 校验接口入参数据,非空和数据校验
     *
     * @param interfaceVersion
     * @param interfaceName
     * @return
     */
    public Result checkEWMInterfaceParam(String interfaceVersion, String interfaceName) {
        
        log.info("{},数据校验,请求的interfaceVersion:{},interfaceName:{}", LOGGER_MSG, interfaceVersion, interfaceName);
        
        if (StringUtils.isBlank(interfaceVersion)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getCode(), RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getDescribe()));
        } else if (StringUtils.isBlank(interfaceName)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACENAME_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACENAME_NULL.getCode(), RespStatusEnum.CHECK_INTERFACENAME_NULL.getDescribe()));
        }
        
        /**
         * 接口版本只支持v1
         * 接口名称暂时不作校验
         */
        if (!ConfigurerInfo.INTERFACE_VERSION_V3.equals(interfaceVersion)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getCode(), RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getDescribe()));
        }
        
        return Result.ok(new ResponseStatus(RespStatusEnum.SUCCESS.getCode(), RespStatusEnum.SUCCESS.getDescribe()));
        
    }
    
}
