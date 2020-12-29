package com.dxhy.order.model.a9.kp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
/**
 * 发票批次信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:13
 */
@Getter
@Setter
public class CommonInvoicesBatch implements Serializable {
    
    private String FPQQPCH;
    private String NSRSBH;
    private String SLDID;
    private String KPJH;
    private String FPLX;
    private String FPLB;
    private String KZZD;
    /**
     * false非成品油，true成品油
     */
    private boolean CPYFP;
    
}
