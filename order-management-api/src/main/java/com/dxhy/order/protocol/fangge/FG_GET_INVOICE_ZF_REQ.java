package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description:方格接口 获取作废数据请求参数
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_GET_INVOICE_ZF_REQ implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 作废数据批次号
     */
    private String ZFPCH;
    
}
