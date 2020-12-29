package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description:方格打印机返回结果
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_PRINTER_RESPONSE implements Serializable {
    
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    
}
