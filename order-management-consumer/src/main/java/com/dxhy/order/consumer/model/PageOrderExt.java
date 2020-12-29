package com.dxhy.order.consumer.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 订单详情返回多订单号
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/8/7 11:44
 */
@Setter
@Getter
public class PageOrderExt implements Serializable {

    /**
     * 订单号
     */
    String ddh;

    /**
     * 发票请求流水号
     */
    String fpqqlsh;

    /**
     * 订单id
     */
    String orderId;

    /**
     * 订单状态
     */
    String ddzt;

}
