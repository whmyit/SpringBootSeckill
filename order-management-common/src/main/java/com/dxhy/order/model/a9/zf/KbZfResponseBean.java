package com.dxhy.order.model.a9.zf;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 空白发票作废响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:18
 */
@Getter
@Setter
public class KbZfResponseBean extends ResponseBaseBean {
    
    private KbZfResponseExtend result;
    
}
