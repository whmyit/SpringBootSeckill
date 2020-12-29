package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 受理点库存信息扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:37
 */
@Getter
@Setter
public class SldKcByFjhResponseExtend extends ResponseBaseBeanExtend{
    
    private List<SldKcmxByFjh> kcmxes;
}
