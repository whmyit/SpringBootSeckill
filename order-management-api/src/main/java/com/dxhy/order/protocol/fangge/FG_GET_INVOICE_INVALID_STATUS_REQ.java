package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description:方格接口 作废接口更新数据状态  参数实体bean
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_GET_INVOICE_INVALID_STATUS_REQ implements Serializable {
    
    /**
     * 作废批次号
     */
    private String ZFPCH;
    /**
     * 作废批次号
     */
    private String NSRSBH;
    /**
     * 数据状态
     */
    private String SJZT;
    
}
