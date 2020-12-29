package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 受理点库存明细响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:38
 */
@Getter
@Setter
public class SldKcmxResponse extends ResponseBaseBean {
    
    private SldKcmxResponseExtend result;
    
}
