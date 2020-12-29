package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 成品油局端库存明细协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:48
 */
@Setter
@Getter
public class CPY_JDKC_MX implements Serializable {
    private String SBBM;
    private String SLZT;
    private String SLLSH;
    private List<CPY_JDKC_XZZ_MXS> XZZMXS;
}
