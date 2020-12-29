package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 成品油已下载库存数组协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:57
 */
@Setter
@Getter
public class CPY_YXZKC_MXS implements Serializable {
    private String SPBM;
    private String SL;
}
