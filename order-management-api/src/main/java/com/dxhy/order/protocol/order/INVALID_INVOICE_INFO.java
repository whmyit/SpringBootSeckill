package com.dxhy.order.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 发票作废结构数据协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 20:21
 */
@Setter
@Getter
public class INVALID_INVOICE_INFO implements Serializable {
    
    /**
     * 发票代码
     */
    private String FP_DM;
    
    /**
     * 发票号码
     */
    private String FP_HM;
    
    /**
     * 状态
     */
    private String STATUS_CODE;
    
    /**
     * 错误信息
     */
    private String STATUS_MESSAGE;
}
