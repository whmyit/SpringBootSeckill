package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 受理点库存响应扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:39
 */
@Getter
@Setter
public class SldKcResponseExtend extends ResponseBaseBeanExtend {
    
    private List<SldKcmx> kcmxes;
}
