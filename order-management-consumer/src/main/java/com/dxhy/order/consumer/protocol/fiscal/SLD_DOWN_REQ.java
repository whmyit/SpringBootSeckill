package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:22:20
 * @Describe
 */
@Setter
@Getter
public class SLD_DOWN_REQ {
    private String xhfNsrsbh;
    private String sldId;
    private String xpr;
}
