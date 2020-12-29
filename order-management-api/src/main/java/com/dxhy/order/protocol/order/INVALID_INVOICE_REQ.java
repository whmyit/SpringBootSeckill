package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票作废对外接口请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 20:18
 */
@Setter
@Getter
public class INVALID_INVOICE_REQ implements Serializable {
    
    /**
     * 作废批次号
     */
    private String ZFPCH;
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 发票代码
     */
    private String FP_DM;
    
    /**
     * 发票起号
     */
    private String FPQH;
    
    /**
     * 发票止号
     */
    private String FPZH;
    
    /**
     * 作废类型
     */
    private String ZFLX;
    
    /**
     * 作废原因
     */
    private String ZFYY;
    
    /**
     * 作废人
     */
    private String ZFR;
}
