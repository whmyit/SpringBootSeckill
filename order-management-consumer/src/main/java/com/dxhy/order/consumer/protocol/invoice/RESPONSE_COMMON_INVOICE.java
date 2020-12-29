package com.dxhy.order.consumer.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ZSC-DXHY
 * @date 创建时间: 2020/5/15 20:07
 */
@Setter
@Getter
@Deprecated
public class RESPONSE_COMMON_INVOICE implements Serializable {
    
    private String FPQQLSH;
    
    private String JQBH;
    
    private String FP_DM;
    
    private String FP_HM;
    
    private String KPRQ;
    
    private String JYM;
    
    private String FWM;
    
    private String PDF_URL;
}
