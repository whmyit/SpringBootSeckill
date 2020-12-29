package com.dxhy.order.consumer.protocol.cpy;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 成品油已下载库存返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:56
 */
@Setter
@Getter
public class CPY_YXZKC_RESPONSE extends RESPONSE implements Serializable {
    private List<CPY_YXZKC_MXS> MXS;
}
