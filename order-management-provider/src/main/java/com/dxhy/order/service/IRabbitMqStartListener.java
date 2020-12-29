package com.dxhy.order.service;

/**
 * rabbitMq启动监听接口
 *
 * @author ZSC-DXHY
 */
public interface IRabbitMqStartListener {
    
    /**
     * 启动rabbitmq监听
     *
     * @return
     */
    boolean startRabbitMqListener();
}
