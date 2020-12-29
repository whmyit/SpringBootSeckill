package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.model.R;

import java.util.List;

/**
 * 电票冲红业务接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:15
 */
public interface InvoiceRushRedService {
    
    /**
     * 电票冲红
     *
     * @param fpdm
     * @param fphm
     * @param chyy
     * @param shList
     * @return
     * @throws OrderSeparationException
     */
    R eleRush(String fpdm, String fphm, String chyy, List<String> shList) throws OrderSeparationException;
    
}
