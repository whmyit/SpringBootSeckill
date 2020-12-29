package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单开票请求 批次 协议bean,
 * V4版本,统一对外输出
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 16:43
 */
@ToString
@Setter
@Getter
public class DDPCXX implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 开票终端
     */
    private String KPZD;
    
    /**
     * 统一发票类型代码
     * 增值税专用发票： 004
     * 增值税普通发票： 007
     * 增值税普通发票（电子）： 026
     * 增值税专用发票（电子）：028
     */
    private String FPLXDM;
    
    
    /**
     * 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
     */
    private String KPFS;
    
    /**
     * 是否是成品油(0:非成品油;1:成品油),默认为0
     */
    private String CPYBS;
    
    /**
     * 扩展字段
     */
    private String KZZD;
    
}
