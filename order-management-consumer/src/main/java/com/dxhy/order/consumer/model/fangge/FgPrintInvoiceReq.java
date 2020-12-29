package com.dxhy.order.consumer.model.fangge;

import com.alibaba.fastjson.annotation.JSONField;
import com.dxhy.invoice.protocol.dy.dto.PrintReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description:调用接口调用入参
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Getter
@Setter
public class FgPrintInvoiceReq {
    
    @JSONField(
            name = "DYPCH"
    )
    private String DYPCH;
    @JSONField(
            name = "DYLX"
    )
    private String DYLX;
    @JSONField(
            name = "L"
    )
    private List<PrintReq> L;
    
}
