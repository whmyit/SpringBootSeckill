package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 数据库设置表,纳税人对应队列表
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class SysNsrQueue {
    
    /**
     * 税号队列表id
     */
    private String id;
    
    /**
     * 税号队列表-纳税人识别号
     */
    private String nsrsbh;
    
    /**
     * 税号队列表-队列前缀
     */
    private String queuePrefix;
    
    /**
     * 税号队列表-队列名称
     */
    private String queueName;
    
    /**
     * 税号队列表-状态(0:有效;1:无效)
     */
    private String status;
    
    /**
     * 税号队列表-监听数量,必须大于0,并且为整数
     */
    private String listenerSize;
    
    /**
     * 税号队列表-监听状态(0:有效;1:无效)
     */
    private String listenerStatus;
    
    /**
     * 税号队列表-创建时间
     */
    private Date createTime;
    
    /**
     * 税号队列表-更新时间
     */
    private Date updateTime;
    
    
}
