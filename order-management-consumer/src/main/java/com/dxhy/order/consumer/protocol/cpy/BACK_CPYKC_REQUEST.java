package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油库存回退请求协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:03
 */
@SuppressWarnings("ALL")
@Setter
@Getter
public class BACK_CPYKC_REQUEST implements Serializable {
    private String NSRSBH;
    private String FJH;
    private BACK_CPYKC_MX MX;
}
