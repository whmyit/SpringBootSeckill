package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油局端库存明细数组协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:47
 */
@Setter
@Getter
public class CPY_JDKC_MXS implements Serializable {
    private CPY_JDKC_MX MX;
}
