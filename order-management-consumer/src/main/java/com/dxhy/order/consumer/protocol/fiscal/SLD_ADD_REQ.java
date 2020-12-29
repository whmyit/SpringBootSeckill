package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:57:45
 * @Describe
 */
@Setter
@Getter
public class SLD_ADD_REQ {
    private String xhfNsrsbh;
    private String sldMc;
    private String dydMc;
    private String kpdId;
    private String cjr;
    private String bz;
    private String serverId;
    private String qybs;
    private String sldId;
}
