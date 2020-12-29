package com.dxhy.order.protocol.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 红字发票申请表上传 请求协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:20
 */
@Setter
@Getter
public class RED_INVOICE_FORM_REQ implements Serializable {
    
    /**
     * 红字申请单批次对象
     */
    private RED_INVOICE_FORM_BATCH RED_INVOICE_FORM_BATCH;
    
    /**
     * 红字申请单明细对象
     */
    private List<RED_INVOICE_FORM_UPLOAD> RED_INVOICE_FORM_UPLOADS;
}
