package com.dxhy.order.model.c48.dy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 打印C48返回数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:16
 */
@Getter
@Setter
public class PrintInvoicesRes implements Serializable {
    
    private String DYPCH;
    
    private String STATUS_CODE;
    
    private String STATUS_MESSAGE;
}
