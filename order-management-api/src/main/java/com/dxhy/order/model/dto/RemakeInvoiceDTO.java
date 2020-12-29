package com.dxhy.order.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author fankunfeng
 * @Date 2019-04-12 14:45:05
 * @Describe
 */
@Getter
@Setter
@ToString
public class RemakeInvoiceDTO implements Serializable{
    /**
     * 对应orderInfo表id
     */
    private String orderId;
    /**
     * 受理点
     */
    private String sld;
    /**
     * 受理点名称
     */
    private String sldmc;
}
