package com.dxhy.order.model.a9.pdf;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取PDF接口请求bean
 *
 * @author ZSC-DXHY
 */
@Getter
@Setter
public class GetPdfRequest extends RequestBaseBean{


    private String id;
    private String FPQQPCH;
    private String NSRSBH;
    private GetPdfRequestExtend[] REQUEST_EINVOICE_PDF;
    
}
