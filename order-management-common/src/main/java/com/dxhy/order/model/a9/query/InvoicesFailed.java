package com.dxhy.order.model.a9.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 第一张失败的发票信息
 * @Author: zgj
 * @CreateDate: 2018-07-23 17:24
 * @UpdateUser: zgj
 * @UpdateDate: 2018-07-23 17:24
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class InvoicesFailed {
    private String fPQQLSH;
    private String sTATUS_CODE;
    private String sTATUS_MESSAGE;
}
