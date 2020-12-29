package com.dxhy.order.consumer.protocol.sld;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 受理点列表明细数据协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/11/29 20:43
 */
@Setter
@Getter
public class SLD_SEARCH implements Serializable {
    private String SLDID;
    private String SLDMC;
    private String JQBH;
}
