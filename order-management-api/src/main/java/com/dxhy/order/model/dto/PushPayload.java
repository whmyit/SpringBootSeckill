package com.dxhy.order.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 方格消息通用类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:28
 */
@Setter
@Getter
public class PushPayload implements Serializable {
    /**
     * 接口类型
     */
    private String INTERFACETYPE;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    /**
     * 机器编号
     */
    private String JQBH;
    /**
     * 注册码
     */
    private String ZCM;
    
    /**
     * 订单请求流水号
     */
    private String DDQQLSH;
    /**
     * 申请表上传请求批次号
     */
    private String SQBSCQQPCH;
    /**
     * 申请表下载请求批次号
     */
    private String SQBXZQQPCH;
    /**
     * 作废批次号
     */
    private String ZFPCH;
    /**
     * 打印批次号
     */
    private String DYPCH;
    
    
}
