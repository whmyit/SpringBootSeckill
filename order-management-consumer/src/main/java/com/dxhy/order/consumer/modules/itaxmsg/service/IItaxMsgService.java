package com.dxhy.order.consumer.modules.itaxmsg.service;

/**
 * 发送消息业务层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 11:52
 */
public interface IItaxMsgService {
    /**
     * i-Tax发送消息
     *
     * @param infoTitle
     * @param messageInfo
     * @param messType
     * @param user
     * @param deptId
     * @return
     */
    Boolean sessMessageToTax(String infoTitle, String messageInfo, String messType, String user, String deptId);
    
}
