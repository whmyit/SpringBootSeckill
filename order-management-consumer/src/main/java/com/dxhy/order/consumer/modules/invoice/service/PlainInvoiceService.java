package com.dxhy.order.consumer.modules.invoice.service;

import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.dy.DyResponse;
import com.dxhy.order.model.entity.PrintEntity;

import java.util.List;
import java.util.Map;

/**
 * 描述信息： 纸质发票Service
 *
 * @author 谢元强
 * @date Created on 2018-08-17
 */
public interface PlainInvoiceService {
    /**
     * 根据发票流水号打印发票或者清单
     *
     * @param printEntity
     * @return
     * @throws OrderReceiveException
     */
    DyResponse printInvoice(PrintEntity printEntity) throws OrderReceiveException;
    
    /**
     * 更新纸票打印信息
     *
     * @param ids
     */
    void updateInvoiceDyztById(List<Map> ids);
    
    /**
     * 单张打印受理测试接口
     *
     * @param dydbs
     * @param dyfpzl
     * @param dylx
     * @return
     */
    R printTest(String dydbs, String dyfpzl, String dylx);
    
}
