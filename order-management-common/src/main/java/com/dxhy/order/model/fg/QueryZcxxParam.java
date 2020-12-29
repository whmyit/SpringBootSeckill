package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 查询注册码协议bean
 *
 * @author liudongjie
 * @version 1.0.0 2019-07-03
 */
@Getter
@Setter
public class QueryZcxxParam implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 机器编号
     */
    private String JQBH;
    
}
