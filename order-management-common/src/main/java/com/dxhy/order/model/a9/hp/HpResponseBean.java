package com.dxhy.order.model.a9.hp;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 发票响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:04
 */
@Setter
@Getter
public class HpResponseBean extends ResponseBaseBean {
    
    private HpResponseExtend result;
    
}
