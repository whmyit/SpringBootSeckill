package com.dxhy.order.ordermail.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 协议接口统一返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 9:52
 */
@ToString
@Setter
@Getter
public class RESPONSE implements Serializable {
    
    /**
     * 通用code
     */
    private String STATUS_CODE;
    
    /**
     * 通用错误信息
     */
    private String STATUS_MESSAGE;
}
