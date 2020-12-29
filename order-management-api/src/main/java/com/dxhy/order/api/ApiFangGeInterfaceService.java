package com.dxhy.order.api;


import com.dxhy.order.model.InvoicePrintInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.dto.FgInvoicePrintDto;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.protocol.fangge.FG_COMMON_INVOICE_INFO;

import java.util.List;

/**
 * @Description:方格接口对接
 * @Author:xueanna
 * @Date:2019/6/27
 */
public interface ApiFangGeInterfaceService {
    
    /**
     * 方格接口  更新订单上传数据
     *
     * @param entity
     */
    void updateUploadRedInvoice(SpecialInvoiceReversalEntity entity);
    
    /**
     * 获取待打印数据
     * @param dypch
     * @param nsrsbh
     * @return
     */
    FgInvoicePrintDto getPrintInvoices(String dypch, String nsrsbh);
    
    /**
     * 更新待打印数据状态
     *
     * @param dypch
     * @param nsrsbh
     * @param sjzt
     */
    void updatePrintInvoicesStatus(String dypch, String nsrsbh, String sjzt);
    
    /**
     * 打印完成更新状态
     *
     * @param invoicePrintInfo
     * @param shList
     */
    void updateFgPrintInvoice(InvoicePrintInfo invoicePrintInfo, List<String> shList);
    
    /**
     * 从redis查询设备注册信息
     *
     * @param nsrsbh
     * @param jqbh
     * @return
     */
    String getRegistCodeByRedis(String nsrsbh, String jqbh);
    
    /**
     * 从redis中查询税号注册列表
     *
     * @param nsrsbh
     * @return
     */
    List<String> getRegistCodeListByRedis(String nsrsbh);
    
    /**
     * 保存注册码信息到redis
     *
     * @param code
     */
    void saveCodeToRedis(RegistrationCode code);
    
    /**
     * 先保存信息到redis队列
     * @param pushPayload
     */
    void saveMqttToRedis(PushPayload pushPayload);
    
    /**
     * 发送mqtt消息
     * @param nsrsbh
     * @param jqbh
     * @return
     */
    boolean pushMqttMsg(String nsrsbh, String jqbh);
    
    /**
     * 各业务结束接口完成后更新是否可获取数据状态
     * @param nsrsbh
     * @param jqbh
     */
    void updateMsgFlag(String nsrsbh, String jqbh);
    
    /**
     * 保存打印批次信息到数据库
     * @param printInfo
     */
    void saveInvoicePrintInfo(InvoicePrintInfo printInfo);
    
    /**
     * 根据批次号和税号查询打印的数据
     * @param dypch
     * @param nsrsbh
     * @return
     */
    List<InvoicePrintInfo> getPrintInvoicesList(String dypch, String nsrsbh);
    
    /**
     * 红票开具 更新原蓝票的冲红标志和剩余可冲红金额
     * @param info
     * @param orderInvoiceInfo
     */
    void dealRedInvoice(FG_COMMON_INVOICE_INFO info, OrderInvoiceInfo orderInvoiceInfo);
    
    /**
     * 处理是否发送消息状态
     */
    void handlerIsSendFlag();
    
    
}
