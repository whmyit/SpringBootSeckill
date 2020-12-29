package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 获取下一张发票响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:30
 */
@Getter
@Setter
public class QueryNextInvoiceResponse extends ResponseBaseBean {
    
    private QueryNextInvoiceResponseExtend result;
    
}
