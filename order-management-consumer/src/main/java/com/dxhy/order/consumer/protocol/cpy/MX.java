package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油明细信息协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:09
 */
@Setter
@Getter
public class MX implements Serializable {
    private String SPBM;
    private String SL;
}
