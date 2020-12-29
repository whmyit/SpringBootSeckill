package com.dxhy.order.service;

import com.dxhy.order.rabbitplugin.core.QueueDeclare;

/**
 * rabbitMq存数据接口
 *
 * @author ZSC-DXHY
 */
public interface IRabbitMqSendMessage {
    
    /**
     * 设置消息,
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param queueName
     * @return
     */
    QueueDeclare setQueueDeclare(String nsrsbh, String queuePrefix, String queueName);
    
    /**
     * 保存消息
     *
     * @param queueName
     * @param obj
     */
    void sendRabbitMqMessage(String queueName, Object obj);
    
    /**
     * 自动保存延时队列发送消息
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param obj
     * @param ttlQueueName
     * @param ttl
     */
    void autoSendRabbitMqMessageDelay(String nsrsbh, String queuePrefix, Object obj, String ttlQueueName, long ttl);
    
    /**
     * 自动保存消息到队列
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param obj
     */
    void autoSendRabbitMqMessage(String nsrsbh, String queuePrefix, Object obj);
    
}
