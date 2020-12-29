package com.dxhy.order.protocol.order;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
/**
 * 二维码对外业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:39
 */
@Getter
@Setter
public class COMMON_ORDER_INFO implements Serializable {
    
    private TQM_ORDER_INFO ORDER_INVOICE_INFO;
    
    private List<TQM_ORDER_ITEM_IFNO> ORDER_INVOICE_ITEMS;
    
}
