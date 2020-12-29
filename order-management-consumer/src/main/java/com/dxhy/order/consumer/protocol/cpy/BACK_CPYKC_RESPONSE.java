package com.dxhy.order.consumer.protocol.cpy;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 成品油回退响应协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 18:05
 */
@Setter
@Getter
public class BACK_CPYKC_RESPONSE extends RESPONSE implements Serializable {
    private List<BACK_CPYKC_MX> MXS;
}
