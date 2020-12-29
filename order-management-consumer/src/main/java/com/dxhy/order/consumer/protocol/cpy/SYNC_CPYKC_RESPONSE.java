package com.dxhy.order.consumer.protocol.cpy;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 同步成品油库存响应协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:08
 */
@Setter
@Getter
public class SYNC_CPYKC_RESPONSE extends RESPONSE implements Serializable {
    private String TBBS;
    private List<MX> MXS;
}
