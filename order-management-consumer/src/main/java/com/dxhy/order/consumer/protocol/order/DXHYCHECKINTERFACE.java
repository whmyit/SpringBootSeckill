package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 通用参数实体
 * todo V1和V2使用,后期不改更新维护
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@Deprecated
public class DXHYCHECKINTERFACE implements Serializable {
    
    private static final long serialVersionUID = 7487312611186518703L;
    
    /**
     * 终端类型标识代码
     */
    private String TERMINALCODE;
    /**
     * 版本信息
     */
    private String VERSION;
    /**
     * 数据签名
     */
    private String DATASIGN;
    /**
     * 预留字段
     */
    private String EXTDATA;
}
