package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;
/**
 * 获取下一张发票响应扩展信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:30
 */
@Getter
@Setter
public class QueryNextInvoiceResponseExtend extends ResponseBaseBeanExtend {
    
    private String nsrsbh;
    private String fjh;
    private String jqbh;
    private String fpzlDm;
    private String fpdm;
    private String fphm;
    
}
