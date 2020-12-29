package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 下载成品油库存明细数组协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:14
 */
@Setter
@Getter
public class DOWNLOAD_CPYKC_MXS implements Serializable {
    private String SPBM;
    private String SL;
}
