package com.dxhy.order.rabbitplugin.core;

import com.dxhy.order.rabbit.config.RabbitConfig;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.AMQP.Queue.DeleteOk;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义消息队列
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:30
 */
public class QueueDeclare {
    private final boolean DEFAULT_LASTING = true;
    private final Logger log = LoggerFactory.getLogger(QueueDeclare.class);
    private final String queueName;
    private final boolean lasting;
    
    private ConnectionFactory connectionFactory;
    
    public QueueDeclare(String queueName, boolean lasting) {
        this.queueName = queueName;
        this.lasting = lasting;
    }
    
    public QueueDeclare(String queueName, ConnectionFactory connectionFactory) {
        this.queueName = queueName;
        this.lasting = DEFAULT_LASTING;
        this.connectionFactory = connectionFactory;
    }
    
    /**
     * 创建交换机,队列,绑定队列
     *
     * @return
     */
    public boolean declareQueue() {
        Connection conn = ConnectionForFactory.getConn(this.connectionFactory);
        Channel channel = conn.createChannel(false);
        try {
            
            channel.exchangeDeclare(RabbitConfig.getExchange(), BuiltinExchangeType.DIRECT, true);
            
            DeclareOk ok = channel.queueDeclare(queueName, lasting, false, false, null);
            channel.queueBind(queueName, RabbitConfig.getExchange(), queueName, null);
            log.info("create queue:{} success ,ConsumerCount:{} MessageCount:{}", ok.getQueue(), ok.getConsumerCount() + "", ok.getMessageCount() + "");
            return true;
        } catch (IOException e) {
            log.error("create queue {} fail", queueName, e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (Exception e) {
                log.error("close channel fail", e);
            }
        }
    }
    
    /**
     * 创建ttl队列,入参为队列中数据延时后需要发送到指定的队列名称
     *
     * @param ttlQueueName
     * @return
     */
    public boolean declarettlqueue(String ttlQueueName) {
        Connection conn = ConnectionForFactory.getConn(this.connectionFactory);
        Channel channel = conn.createChannel(false);
        try {
            
            channel.exchangeDeclare(RabbitConfig.getExchange(), BuiltinExchangeType.DIRECT, true);
            
            /**
             * 判断队列是否需要创建延时队列?
             */
            Map<String, Object> arguments = new HashMap<>(3);
            arguments.put("x-dead-letter-exchange", RabbitConfig.getExchange());
            arguments.put("x-dead-letter-routing-key", ttlQueueName);
            /**
             * 添加该功能后表示队列添加ttl
             */
//            arguments.put("x-message-ttl", 1000);
            DeclareOk declareOk = channel.queueDeclare(queueName, lasting, false, false, arguments);
            channel.queueBind(queueName, RabbitConfig.getExchange(), ttlQueueName, null);
            log.info("create delay queue:{} success ,ConsumerCount:{} MessageCount:{}", declareOk.getQueue(), declareOk.getConsumerCount() + "", declareOk.getMessageCount() + "");
            return true;
        } catch (IOException e) {
            log.error("create queue {} fail", queueName, e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (Exception e) {
                log.error("close channel fail", e);
            }
        }
    }
    
    /**
     * 往延时队列中发送消息,同时设置消息失效时间
     *
     * @param template
     * @param obj
     * @param ttl
     */
    public void convertAndSendttl(AmqpTemplate template, Object obj, long ttl) {
        template.convertAndSend(queueName, obj, new ExpirationMessagePostProcessor(ttl));
    }
    
    /**
     * 发送消息
     *
     * @param template
     * @param obj
     */
    public void convertAndSend(AmqpTemplate template, Object obj) {
        template.convertAndSend(queueName, obj);
    }
    
    public boolean deleteQueue() {
        Connection conn = ConnectionForFactory.getConn(connectionFactory);
        Channel channel = conn.createChannel(false);
        try {
            DeleteOk ok = channel.queueDelete(queueName);
            log.info("delete queue:{} success ,{} count data deleted at the same time", new Object[]{queueName, ok.getMessageCount() + ""});
            return true;
        } catch (IOException e) {
            log.error("delete queue:{} fail", queueName, e);
            return false;
        }
    }
}
