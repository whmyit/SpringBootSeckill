package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 方格接口 红票申请单上传请求参数bean
 * @Author:xueanna
 * @Date:2019/6/26
 */
@Setter
@Getter
public class FG_RED_INVOICE_FORM_REQ implements Serializable {
    
    /**
     * 红字申请单批次对象
     */
    private FG_RED_INVOICE_FORM_BATCH RED_INVOICE_FORM_BATCH;
    
    /**
     * 红字申请单明细对象
     */
    private List<FG_RED_INVOICE_FORM_UPLOAD> RED_INVOICE_FORM_UPLOADS;
}
