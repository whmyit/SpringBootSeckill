package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 发票开具结果获取请求协议bean
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class GET_INVOICE_RSP extends RESPONSE implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    /**
     * 发票明细信息
     */
    private List<COMMON_INVOICE_INFO> COMMON_INVOICE_INFOS;
    
}
