package com.dxhy.order.model.a9.query;


import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票查询请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:11
 */
@Getter
@Setter
public class InvoiceQueryRequest extends RequestBaseBean implements Serializable {
    
    /**
     * 发票类型
     */
    private String FPLX;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 发票请求批次号
     */
    private String FPQQPCH;
    
}
