package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;


/**
 * 发票对外协议bean
 * todo V1或者是V2版本,后续不再更新迭代.
 *
 * @author ZSC-DXHY
 * @deprecated
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel"})
@Setter
@Getter
@Deprecated
public class ALLOCATE_INVOICES_REQ extends DXHYCHECKINTERFACE {
    /**
     * 电子发票实体
     */
    private COMMON_INVOICE[] COMMON_INVOICE;
    
}
