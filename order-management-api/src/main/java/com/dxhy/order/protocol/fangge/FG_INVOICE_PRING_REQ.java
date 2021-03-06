package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description: 发票打印返回实体
 * @Author:xueanna
 * @Date:2019/7/2
 */
@Setter
@Getter
public class FG_INVOICE_PRING_REQ implements Serializable {
    
    /**
     * 打印批次号
     */
    private String DYPCH;
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
}
