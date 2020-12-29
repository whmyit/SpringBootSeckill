package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author fankunfeng
 * @Date 2019-04-11 16:27:18
 * @Describe
 */
@Setter
@Getter
@ToString
public class InvoiceCountByTimeVO {
    /**
     * 时间   总金额（元）  总税额（元）  总开票量（张）   普票数量   专票数量    电普票数量
     */
    private String time;
    private String jshj;
    private String hjse;
    private String count;
    private String ppCount;
    private String zpCount;
    private String dpCount;
    private String xhfmc;
    private String yhb;
    
    
}
