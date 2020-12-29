package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票开具请求信息订单信息
 * todo V1和V2使用,后期不改更新维护
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
@Deprecated
public class COMMON_INVOICE_ORDER implements Serializable {
    
    private static final long serialVersionUID = -7856710771221706166L;
    /**
     * 订单号
     */
    private String DDH;
    /**
     * 退货单号
     */
    private String THDH;
    /**
     * 订单时间
     */
    private String DDDATE;
}
