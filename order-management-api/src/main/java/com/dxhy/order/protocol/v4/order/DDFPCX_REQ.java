package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 获取订单数据请求协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 14:57
 */
@Setter
@Getter
public class DDFPCX_REQ implements Serializable {
    
    private String NSRSBH;
    private String DDQQLSH;
    private String TQM;
    private String DDH;
}
