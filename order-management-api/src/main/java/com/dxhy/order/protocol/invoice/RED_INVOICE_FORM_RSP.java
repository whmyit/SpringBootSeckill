package com.dxhy.order.protocol.invoice;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 红字信息表上传返回协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 10:27
 */
@Setter
@Getter
public class RED_INVOICE_FORM_RSP extends RESPONSE {
    
    /**
     * 申请单上传请求批次号
     */
    private String SQBSCQQPCH;
    
    /**
     * 申请单上传返回对象
     */
    private List<RED_INVOICE_FORM_UPLOAD_RESPONSE> RED_INVOICE_FORM_UPLOAD_RESPONSES;
}
