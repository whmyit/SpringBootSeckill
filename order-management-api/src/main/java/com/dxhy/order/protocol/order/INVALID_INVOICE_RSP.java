package com.dxhy.order.protocol.order;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 发票作废对外接口响应协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/18 20:18
 */
@Setter
@Getter
public class INVALID_INVOICE_RSP extends RESPONSE implements Serializable {
    
    /**
     * 作废批次号
     */
    private String ZFPCH;
    
    /**
     * 作废发票信息
     */
    private List<INVALID_INVOICE_INFO> INVALID_INVOICE_INFOS;
}
