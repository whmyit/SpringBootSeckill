package com.dxhy.order.consumer.protocol.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点下票请求协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 10:38
 */
@Setter
@Getter
public class SLDDOWN_REQUEST implements Serializable {
    private String NSRSBH;
    private String SLDID;
    private String XPR;
}
