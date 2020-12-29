package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 同步成品油库存请求协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:07
 */
@Setter
@Getter
public class SYNC_CPYKC_REQUEST implements Serializable {
    private String FJH;
    private String NSRSBH;
}
