package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油回退明细协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:04
 */
@Setter
@Getter
public class BACK_CPYKC_MX implements Serializable {
    private String SPBM;
    private String SL;
}
