package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * 发票开具结果获取请求协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class DDKJXX_REQ implements Serializable {
    
    /**
     * 发票类型
     */
    private String FPLXDM;
    
    /**
     * 是否返回失败数据
     */
    private String SFFHSBSJ;
    
    /**
     * 销方纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    
}
