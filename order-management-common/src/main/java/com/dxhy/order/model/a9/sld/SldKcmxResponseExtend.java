package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 受理点库存明细响应扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:38
 */
@Getter
@Setter
public class SldKcmxResponseExtend extends ResponseBaseBeanExtend {
    private List<SldKcmx> kclb;
}
