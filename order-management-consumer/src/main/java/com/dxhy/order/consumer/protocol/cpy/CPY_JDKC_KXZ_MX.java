package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油局端库存可下载明细协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:45
 */
@Setter
@Getter
public class CPY_JDKC_KXZ_MX implements Serializable {
    private String SPBM;
    private String SL;
}
