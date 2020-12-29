package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 发票查询详情响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:14
 */
@Getter
@Setter
public class InvoiceResultResponse extends ResponseBaseBean {

    private GetAllocatedInvoicesRsp result;
    
    
}
