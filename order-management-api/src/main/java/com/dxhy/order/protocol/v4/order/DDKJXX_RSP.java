package com.dxhy.order.protocol.v4.order;

import com.dxhy.order.protocol.v4.RESPONSEV4;
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
public class DDKJXX_RSP extends RESPONSEV4 implements Serializable {
    
    /**
     * 订单请求批次号
     */
    private String DDQQPCH;
    
    /**
     * 发票明细信息
     */
    private List<FPZXX> FPZXX;
    
}
