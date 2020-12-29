package com.dxhy.order.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字信息申请表上传结果协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class RED_INVOICE_FORM_UPLOAD_RESPONSE implements Serializable {
    
    /**
     * 申请表上传请求流水号
     */
    private String SQBSCQQLSH;
    
    /**
     * 申请单号
     */
    private String SQDH;
    
    /**
     * 状态代码
     */
    private String STATUS_CODE;
    
    /**
     * 状态信息
     */
    private String STATUS_MESSAGE;
    
    /**
     * 信息表编号
     */
    private String XXBBH;
}
