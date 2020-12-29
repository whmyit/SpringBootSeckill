package com.dxhy.order.service;

/**
 * 轮询开票服务接口
 *
 * @author ZSC-DXHY
 */
public interface IPollinvoiceService {
    
    /**
     * 轮询开票服务接口
     *
     * @param message
     */
    void pollInvoice(String message);
}
