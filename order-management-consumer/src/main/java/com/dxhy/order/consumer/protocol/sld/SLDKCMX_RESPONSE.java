package com.dxhy.order.consumer.protocol.sld;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 受理点上下票管理列表查询返回协议bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/9/19 9:56
 */
@Setter
@Getter
public class SLDKCMX_RESPONSE extends RESPONSE {
    private List<SLDKCMX> SLDKCMXES;
}
