package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * C48调用修改为http 返回报文
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:18
 */
@Getter
@Setter
public class JspxxResponseResult extends ResponseBaseBean {
    
    private JspxxResponse result;
    
    
}
