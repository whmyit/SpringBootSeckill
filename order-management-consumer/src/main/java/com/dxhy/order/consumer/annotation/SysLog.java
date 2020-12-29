package com.dxhy.order.consumer.annotation;

import java.lang.annotation.*;

/**
 * @author ：杨士勇
 * @ClassName ：SysLog
 * @Description ：系统日志注解,格式:操作类型-操作描述-关键字-是否打印入参出参
 * @date ：2018年8月8日 上午11:52:16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    
    
    /**
     * 操作类型
     */
    String operation() default "";
    
    /**
     * 具体操作描述
     */
    String operationDesc() default "";
    
    /**
     * 关键字
     */
    String key() default "";
    
    boolean printRequest() default true;
}
