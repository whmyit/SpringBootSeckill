package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单开票请求 批次 协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 16:43
 */
@ToString
@Setter
@Getter
public class COMMON_ORDER_BATCH implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 开票点ID
     */
    private String SLDID;
    
    /**
     * 开票机号,对应金税盘分机号
     */
    private String KPJH;
    
    /**
     * 发票类型(1:纸质发票,2:电子发票)
     */
    private String FPLX;
    
    /**
     * 发票类别(发票类型为1时, 0:专票 2:普票41:卷票
     * 发票类型为2时, 51:电子发票)
     */
    private String FPLB;
    
    /**
     * 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
     */
    private String KPFS;
    
    /**
     * 是否是成品油(0:非成品油;1:成品油),默认为0
     */
    private String SFCPY;
    
    /**
     * 扩展字段
     */
    private String KZZD;
    
}
