package com.dxhy.order.model.a9.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 发票开具请求执行状态查询返回bean
 * @Author: zgj
 * @CreateDate: 2018-07-23 17:22
 * @UpdateUser: zgj
 * @UpdateDate: 2018-07-23 17:22
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class GetAllocateInvoicesStatusRsp {
    private String FPQQPCH;
    private String NSRSBH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private InvoicesFailed INVOICES_FAILED;
}
