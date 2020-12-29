package com.dxhy.order.consumer.protocol.cpy;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 下载成品油库存响应协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:13
 */
@Setter
@Getter
public class DOWNLOAD_CPYKC_RESPONSE extends RESPONSE implements Serializable {
    private List<DOWNLOAD_CPYKC_MXS> MXS;
}
