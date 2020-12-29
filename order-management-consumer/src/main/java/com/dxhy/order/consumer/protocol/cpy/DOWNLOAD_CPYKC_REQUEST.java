package com.dxhy.order.consumer.protocol.cpy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 下载成品油库存请求协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:59
 */
@Setter
@Getter
public class DOWNLOAD_CPYKC_REQUEST implements Serializable {
    private String FJH;
    private String NSRSBH;
    private List<DOWNLOAD_CPYKC_RESMXS> MXS;
}
