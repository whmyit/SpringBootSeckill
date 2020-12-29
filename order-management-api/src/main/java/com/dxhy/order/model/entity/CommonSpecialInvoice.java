package com.dxhy.order.model.entity;

import com.dxhy.order.model.SpecialInvoiceReversalItem;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * 对接前端业务专票主体类
 *
 * @author ZSC-DXHY
 */

@Setter
@Getter
public class CommonSpecialInvoice implements Serializable {
    private SpecialInvoiceReversalEntity specialInvoiceReversalEntity;
    private List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntities;
    
}
