package com.dxhy.order.rabbitplugin.core;

import com.dxhy.order.rabbitplugin.util.LisenerStauts;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;
/**
 * 监听工厂
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:20
 */
public class LisenerFactory {
    private static LisenerFactory lisenerFactory = null;
    private final Map<String, SimpleMessageListenerContainer> lisenerMap = new HashMap<>(5);
    
    public static LisenerFactory instantiation() {
        if (lisenerFactory == null) {
            lisenerFactory = new LisenerFactory();
        }
        return lisenerFactory;
    }
    
    public LisenerStauts keepLisener(ConnectionFactory connectionFactory, SimpleMessageListenerContainer listenerContainer) {
        String key = getLisenerMapKey(connectionFactory, listenerContainer);
        Object obj = lisenerMap.get(key);
        if (obj != null) {
            return LisenerStauts.isExit;
        } else {
            lisenerMap.put(key, listenerContainer);
            return LisenerStauts.success;
        }
    }
    
    public SimpleMessageListenerContainer getLisener(String key) {
        return lisenerMap.get(key);
    }
    
    public Map<String, SimpleMessageListenerContainer> getAllLiseners() {
        return lisenerMap;
    }
    
    public String getLisenerMapKey(ConnectionFactory connectionFactory, SimpleMessageListenerContainer listenerContainer) {
        String host = connectionFactory.getHost();
        String[] queueNames = listenerContainer.getQueueNames();
        StringBuilder keySb = new StringBuilder();
        keySb.append(host);
        if (queueNames != null && queueNames.length > 0) {
            for (String queueName : queueNames) {
                keySb.append("[").append(queueName).append("]");
            }
        }
        return keySb.toString();
    }
    
    public String getLisenerMapKey(ConnectionFactory connectionFactory, String queueName) {
        String host = connectionFactory.getHost();
        StringBuilder keySb = new StringBuilder();
        keySb.append(host);
        keySb.append("[").append(queueName).append("]");
        return keySb.toString();
    }
}
