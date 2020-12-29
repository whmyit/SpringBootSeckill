package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:03:55
 * @Describe
 */
@Setter
@Getter
public class KPD_DEL_REQ {
    private String kpdId;
    private String xhfNsrsbh;
}
