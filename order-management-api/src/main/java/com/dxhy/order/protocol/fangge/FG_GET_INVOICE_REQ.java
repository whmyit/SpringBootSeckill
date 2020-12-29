package com.dxhy.order.protocol.fangge;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @Description:方格接口获取待开票数据
 * @Author:xueanna
 * @Date:2019/6/25
 */
@Setter
@Getter
public class FG_GET_INVOICE_REQ implements Serializable {
    
    /**
     * 纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 订单请求唯一流水号
     */
    private String DDQQLSH;
    
    /**
     * 开始时间
     */
    private String KSSJ;
    /**
     * 结束时间
     */
    private String JSSJ;
    
}
