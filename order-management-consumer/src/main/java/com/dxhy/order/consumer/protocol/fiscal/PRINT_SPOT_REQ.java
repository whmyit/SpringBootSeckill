package com.dxhy.order.consumer.protocol.fiscal;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:46:55
 * @Describe
 */
@Setter
@Getter
public class PRINT_SPOT_REQ {
    @JSONField(
            name = "DYDMC"
    )
    private String DYDMC;
    @JSONField(
            name = "xhfNsrsbh"
    )
    private String xhfNsrsbh;
    @JSONField(
            name = "DYDZT"
    )
    private String DYDZT;
    @JSONField(
            name = "SERVER_ID"
    )
    private Integer SERVER_ID;
    @JSONField(
            name = "DYDBS"
    )
    private String DYDBS;
    private String xhfMc;

}
