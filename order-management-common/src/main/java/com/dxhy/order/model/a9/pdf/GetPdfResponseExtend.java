package com.dxhy.order.model.a9.pdf;


import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 调用底层获取pdf响应扩展信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:57
 */
@Getter
@Setter
public class GetPdfResponseExtend extends ResponseBaseBeanExtend {
    
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private String regenerateflag;
    private String fpqqpch;
    private List<InvoicePdf> response_EINVOICE_PDF;
}
