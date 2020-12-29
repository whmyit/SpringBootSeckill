package com.dxhy.order.consumer.aop;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.SysLogEntity;
import com.dxhy.order.consumer.modules.itaxlog.service.IItaxlogService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.consumer.utils.HttpContextUtils;
import com.dxhy.order.consumer.utils.IPUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;


/**
 * 系统日志，切面处理类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017年3月8日 上午11:07:35
 */
@Aspect
@Component
@Slf4j
public class SysLogAspect {
    
    private static final String LOG_MSG = "(日志环绕切面类)";
    
    @Resource
    private UserInfoService userInfoService;
    
    @Resource
    private IItaxlogService iItaxlogService;
    
    @Pointcut("@annotation(com.dxhy.order.consumer.annotation.SysLog)")
    public void logPointCut() {
    
    }
    
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        saveSysLog(point, time, result);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SysLogEntity sysLogEntity = new SysLogEntity();
        UserEntity user = new UserEntity();
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = req.getHeader(ConfigureConstant.TOKEN);
        if (StringUtils.isNotBlank(token)) {
            /**
             * 获取当前登录用户的用户信息
             */
            user = userInfoService.getUser();
        } else {
            user = null;
        }
    
        boolean sendMsg = true;
    
        if (user == null || user.getDept() == null || StringUtils.isBlank(String.valueOf(user.getUserId()))
                || StringUtils.isBlank(String.valueOf(user.getDept().getParentId()))) {
            sendMsg = false;
        
        } else {
            sysLogEntity.setUserId(user.getUserId().toString());
            sysLogEntity.setUserParentId(user.getDept().getDeptId());
        }
    
    
        
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog != null) {
            //注解上的描述
            sysLogEntity.setOperation(syslog.operation());
            sysLogEntity.setOperationDesc(syslog.operationDesc());
            sysLogEntity.setKey(syslog.key());
            sysLogEntity.setPrintRequest(syslog.printRequest());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLogEntity.setMethod(className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        try {
    
            Object[] args1 = args;
            for (int i = 0; i < args1.length; i++) {
    
                if ("RequestFacade".equals(args1[i].getClass().getSimpleName())) {
                    args1[i] = null;
                    continue;
                }
                if ("ResponseFacade".equals(args1[i].getClass().getSimpleName())) {
                    args1[i] = null;
                    continue;
                }
                if ("XssHttpServletRequestWrapper".equals(args1[i].getClass().getSimpleName())) {
                    args1[i] = null;
                    continue;
                }
            }
            String params = JsonUtils.getInstance().toJsonString(args1);
            if (StringUtils.isNotBlank(params) && params.length() > ConfigureConstant.INT_300) {
                params = params.substring(ConfigureConstant.INT_0, ConfigureConstant.INT_300);
            }
            sysLogEntity.setParams(params);
        } catch (Exception e) {
            log.error("{}日志打印出错,e:{}", LOG_MSG, e.getMessage());
        }

        //获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        //设置IP地址
        sysLogEntity.setIp(IPUtils.getIpAddr(request));
    
        sysLogEntity.setTime(time);
        sysLogEntity.setCreateDate(new Date());
        sysLogEntity.setResult(true);
    
        String reslutStr = "";
        if (result != null) {
            reslutStr = JsonUtils.getInstance().toJsonString(result);
        }
    
        if (StringUtils.isNotBlank(reslutStr) && reslutStr.length() > ConfigureConstant.INT_300) {
            reslutStr = reslutStr.substring(ConfigureConstant.INT_0, ConfigureConstant.INT_300);
        }
        if (!sysLogEntity.isPrintRequest()) {
            sysLogEntity.setParams("");
            reslutStr = "";
        }
        log.debug("{}调用时间:{}请求参数:{},返回参数:{}", LOG_MSG, sysLogEntity.getTime(), JsonUtils.getInstance().toJsonString(sysLogEntity), reslutStr);
        if (sendMsg) {
            iItaxlogService.saveItaxLog(sysLogEntity);
        }
        
    }
}
