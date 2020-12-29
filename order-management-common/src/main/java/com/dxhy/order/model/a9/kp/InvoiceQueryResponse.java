package com.dxhy.order.model.a9.kp;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;
/**
 * 发票查询响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:11
 */
@Setter
@Getter
public class InvoiceQueryResponse extends ResponseBaseBeanExtend{


    private CommonInvoiceStatus commonInvoicestatus;

}
