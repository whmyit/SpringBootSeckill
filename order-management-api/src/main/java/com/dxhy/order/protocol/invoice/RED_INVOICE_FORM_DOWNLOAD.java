package com.dxhy.order.protocol.invoice;

import com.dxhy.order.protocol.order.ORDER_INVOICE_ITEM;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 专票冲红 红字信息申请表 下载明细协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 9:25
 */
@Setter
@Getter
public class RED_INVOICE_FORM_DOWNLOAD implements Serializable {
    
    /**
     * 红字信息表下载头信息
     */
    private RED_INVOICE_FORM_DOWN_HEAD RED_INVOICE_FORM_DOWN_HEAD;
    
    /**
     * 红字信息表明细信息
     */
    private List<ORDER_INVOICE_ITEM> ORDER_INVOICE_ITEMS;
}
