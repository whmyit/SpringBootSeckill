package com.dxhy.order.consumer.modules.supplychain.model;


import lombok.Data;

/**
 * 供应链接口响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:42
 */
@Data
public class SupplyChainBaseResponseBean {

    private String returnCode;
    private String returnMessage;
    private Object data;
}
