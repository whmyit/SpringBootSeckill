package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 获取订单接口返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 15:05
 */
@Setter
@Getter
public class ORDER_RESPONSE extends RESPONSE implements Serializable {
    private COMMON_ORDER_INVOCIE[] COMMON_ORDER_INVOCIES;

}
