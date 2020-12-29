package com.dxhy.order.rabbitplugin.core;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * 延时处理
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-11 20:53
 */
public class ExpirationMessagePostProcessor implements MessagePostProcessor {
    /**
     * 毫秒
     */
    private final Long ttl;
    
    public ExpirationMessagePostProcessor(Long ttl) {
        this.ttl = ttl;
    }
    
    /**
     * Change (or replace) the message.
     *
     * @param message the message.
     * @return the message.
     * @throws AmqpException an exception.
     */
    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        // 设置per-message的失效时间
        message.getMessageProperties()
                .setExpiration(ttl.toString());
        return message;
    }
}
