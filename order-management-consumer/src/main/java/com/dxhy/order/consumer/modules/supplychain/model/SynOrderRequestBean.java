package com.dxhy.order.consumer.modules.supplychain.model;


import lombok.Data;

/**
 * 同步订单
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:20
 */
@Data
public class SynOrderRequestBean {

    private String poNo;
    private String amount;
    private String gfTaxNo;
    private String gfName;
    private String xfTaxNo;
    private String xfName;
    private String period;
}
