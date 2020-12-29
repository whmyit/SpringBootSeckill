package com.dxhy.order.model.a9.sld;


import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;
/**
 * 获取下一张发票请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:29
 */
@Getter
@Setter
public class QueryNextInvoiceRequest extends RequestBaseBean{
    
    private String fpzlDm;
    private String nsrsbh;
    private String sldId;
    
}
