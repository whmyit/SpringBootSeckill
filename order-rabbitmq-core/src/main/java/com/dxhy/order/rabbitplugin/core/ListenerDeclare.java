package com.dxhy.order.rabbitplugin.core;

import com.dxhy.order.rabbitplugin.listener.BaseChannelListener;
import com.dxhy.order.rabbitplugin.listener.BaseListener;
import com.dxhy.order.rabbitplugin.util.LisenerStauts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.Assert;

import java.util.List;
/**
 * 监听队列定义
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:20
 */
public class ListenerDeclare {
    private final Logger log = LoggerFactory.getLogger(ListenerDeclare.class);
    public int DEFAULT_CONSUMER_SIZE = 1;
    private final String[] queueNames;
    private int consumerSize;
    private BaseListener baseListener;
    private BaseChannelListener baseChannelListener;
    private final ConnectionFactory connectionFactory;
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
    }
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList, int consumerSize) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
        this.consumerSize = consumerSize;
    }
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList, BaseListener baseListener) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
        this.consumerSize = DEFAULT_CONSUMER_SIZE;
        this.baseListener = baseListener;
    }
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList, BaseChannelListener baseChannelListener) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
        this.consumerSize = DEFAULT_CONSUMER_SIZE;
        this.baseChannelListener = baseChannelListener;
    }
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList, int consumerSize, BaseListener baseListener) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
        this.consumerSize = consumerSize;
        this.baseListener = baseListener;
    }
    
    public ListenerDeclare(ConnectionFactory connectionFactory, List<String> queueNameList, int consumerSize, BaseChannelListener baseChannelListener) {
        this.connectionFactory = connectionFactory;
        this.queueNames = new String[queueNameList.size()];
        queueNameList.toArray(this.queueNames);
        this.consumerSize = consumerSize;
        this.baseChannelListener = baseChannelListener;
    }
    
    public boolean declareLisener() {
        if (baseListener == null && baseChannelListener == null) {
            log.error("baseListener and baseChannelListener is empty");
            return false;
        }
        if (baseListener != null && baseChannelListener != null) {
            log.error("baseListener and baseChannelListener must be one that cannot be empty");
            return false;
        }
        MessageListener messageListener = null;
        if (baseListener != null) {
            messageListener = baseListener;
        } else {
            messageListener = baseChannelListener;
        }
        
        Assert.notNull(connectionFactory, "rabbitmq connectionFactory is null");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setConcurrentConsumers(consumerSize);
        container.setAutoStartup(true);
        container.setMessageListener(messageListener);
        container.setQueueNames(queueNames);
        container.afterPropertiesSet();
        LisenerStauts status = LisenerFactory.instantiation().keepLisener(connectionFactory, container);
        if (status.getCode() == 0) {
            container.setPrefetchCount(25);
            container.start();
    
            return true;
        } else {
            log.error(status.getMsg());
            return false;
        }
    }
    
    public boolean changeLisener(int consumerSize) {
        String key = getLisenerKey();
        SimpleMessageListenerContainer listenerContainer = LisenerFactory.instantiation().getLisener(key);
        if (listenerContainer == null) {
            log.error("not find queue:{}", key);
            return false;
        }
        listenerContainer.setConcurrentConsumers(consumerSize);
        listenerContainer.stop();
        listenerContainer.start();
        return true;
    }
    
    public boolean lisenerStart() {
        String key = getLisenerKey();
        SimpleMessageListenerContainer listenerContainer = LisenerFactory.instantiation().getLisener(key);
        if (listenerContainer == null) {
            log.error("not find queue:{}", key);
            return false;
        }
        listenerContainer.setTxSize(1);
        listenerContainer.start();
        return true;
    }
    
    public boolean lisenerShutdown() {
        String key = getLisenerKey();
        SimpleMessageListenerContainer listenerContainer = LisenerFactory.instantiation().getLisener(key);
        if (listenerContainer == null) {
            log.error("not find queue:{}", key);
            return false;
        }
        listenerContainer.setTxSize(0);
        return true;
    }
    
    public String getLisenerKey() {
        String host = connectionFactory.getHost();
        StringBuilder keySb = new StringBuilder();
        keySb.append(host);
        if (queueNames != null && queueNames.length > 0) {
            for (String queueName : queueNames) {
                keySb.append("[").append(queueName).append("]");
            }
        }
        return keySb.toString();
    }
}
