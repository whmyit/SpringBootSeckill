package com.dxhy.order.consumer.protocol.cpy;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 成品油局端库存返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/13 17:44
 */
@Setter
@Getter
public class CPY_JDKC_RESPONSE extends RESPONSE implements Serializable {
    private List<CPY_JDKC_KXZ_MX> KXZMXS;
    private CPY_JDKC_XZZ XZZ;
}
