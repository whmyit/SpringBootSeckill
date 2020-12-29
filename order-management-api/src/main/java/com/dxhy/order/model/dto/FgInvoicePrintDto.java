package com.dxhy.order.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 发票打印业务实体   数据库查询结果bean
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FgInvoicePrintDto implements Serializable {
    /**
     * 打印批次号
     */
    private String dypch;
    /**
     * 发票代码
     */
    private String fpdm;
    
    /**
     * 发票起号
     */
    private String fpqh;
    
    /**
     * 发票止号
     */
    private String fpzh;
    
    /**
     * 发票种类
     */
    private String fpzldm;
    
    /**
     * 打印类型  fp:发票,qd:清单
     */
    private String dylx;
    
    /**
     * 打印点标识
     */
    private String dydbs;
    
    /**
     * 左偏移
     */
    private String zpy;
    /**
     * 上偏移
     */
    private String spy;
    /**
     * 打印机名称
     */
    private String dyjmc;
    
}
