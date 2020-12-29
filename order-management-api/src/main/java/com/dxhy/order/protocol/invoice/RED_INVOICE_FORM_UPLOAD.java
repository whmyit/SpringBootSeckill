package com.dxhy.order.protocol.invoice;

import com.dxhy.order.protocol.order.ORDER_INVOICE_ITEM;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 红字信息申请表上传明细协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class RED_INVOICE_FORM_UPLOAD implements Serializable {
    
    /**
     * 红字信息申请表头信息
     */
    private RED_INVOICE_FORM_HEAD RED_INVOICE_FORM_HEAD;
    
    /**
     * 明细信息
     */
    private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
}
