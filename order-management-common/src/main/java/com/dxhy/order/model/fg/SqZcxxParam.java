package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 申请注册码协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-07-03
 */
@Getter
@Setter
public class SqZcxxParam implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 纳税人名称
     */
    private String NSRMC;
    
    /**
     * 机器编号
     */
    private String JQBH;
    
    /**
     * 0航信 1百望 2方格UKey
     */
    private String ZCLX;
    
}
