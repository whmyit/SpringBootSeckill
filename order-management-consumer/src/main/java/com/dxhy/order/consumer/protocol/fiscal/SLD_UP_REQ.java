package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:26:10
 * @Describe
 */
@Setter
@Getter
public class SLD_UP_REQ {
    private String xhfNsrsbh;
    private String sldId;
    private String fpzlDm;
    private String fpdm;
    private String fpqshm;
    private String fpzzhm;
    private String spr;
}
