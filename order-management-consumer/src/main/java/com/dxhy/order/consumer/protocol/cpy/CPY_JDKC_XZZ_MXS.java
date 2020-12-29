package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油局端库存下载中明细协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:49
 */
@Setter
@Getter
public class CPY_JDKC_XZZ_MXS implements Serializable {
    private String SPBM;
    private String SL;
}
