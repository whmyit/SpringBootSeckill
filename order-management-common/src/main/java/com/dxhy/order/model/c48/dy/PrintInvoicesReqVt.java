package com.dxhy.order.model.c48.dy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 打印C48请求底层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:15
 */
@Setter
@Getter
public class PrintInvoicesReqVt implements Serializable {
    private String DYPCH;
    private String DYDBS;
    private String DYLX;
    private List<PrintReq> L;
}
