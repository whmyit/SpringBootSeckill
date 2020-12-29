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
public class DDSC_REQ implements Serializable {

    /**
     * 销货方纳税人识别号
     */
    private String NSRSBH;

    /**
     * 订单请求流水号
     */
    private String DDQQLSH;
}
