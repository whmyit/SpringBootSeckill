package com.dxhy.order.model.a9.kp;

import lombok.Getter;
import lombok.Setter;
/**
 * 新税控状态返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-24 9:19
 */
@Getter
@Setter
public class CommonInvoiceStatusNewTax {

    /**
     * 发票开票流水号
     */
    private String FPQQLSH;

    /**
     * 发票状态
     */
    private String FPZT;

    /**
     * 发票状态描述
     */
    private String FPZTMS;

}
