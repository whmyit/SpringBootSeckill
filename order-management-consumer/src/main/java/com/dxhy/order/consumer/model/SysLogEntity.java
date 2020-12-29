package com.dxhy.order.consumer.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统日志
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-08 10:40:56
 */
@Getter
@Setter
public class SysLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 日志ID
     */
    private String id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户父级ID
     */
    private String userParentId;
    
    /**
     * 用户操作
     */
    private String operation;
    
    /**
     * 用户操作具体描述
     */
    private String operationDesc;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * 执行时长(毫秒)
     */
    private Long time;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 操作关键字
     */
    private String key;
    
    /**
     * 创建时间
     */
    private Date createDate;
    
    /**
     * 操作结果
     */
    private boolean result = true;
    
    /**
     * 是否打印入参和出参
     */
    private boolean printRequest = true;

}
