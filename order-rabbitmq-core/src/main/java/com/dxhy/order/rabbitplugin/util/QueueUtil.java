package com.dxhy.order.rabbitplugin.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
/**
 * 消息队列通用工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:30
 */
@Component
public class QueueUtil {
    
    private final Logger logger = LoggerFactory.getLogger(QueueUtil.class);
    
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private ConnectionFactory rabbitConnectionFactory;
    
    /**
     * 添加队列
     *
     * @param queueName
     * @return
     */
    public boolean addQueue(String queueName) {
        
        Connection conn = rabbitConnectionFactory.createConnection();
        Channel channel = conn.createChannel(false);
        try {
            AMQP.Queue.DeclareOk ok = channel.queueDeclare(queueName, true, false, false, null);
            logger.info("create queue:{} success ,ConsumerCount:{} MessageCount:{}", ok.getQueue(), ok.getConsumerCount() + "", ok.getMessageCount() + "");
            return true;
        } catch (IOException e) {
            logger.error("create queue {} fail", queueName, e);
            return false;
        } finally {
            try {
                channel.close();
            } catch (Exception e) {
                logger.error("close channel fail", e);
            }
        }
    }
    
    /**
     * 发送消息
     *
     * @param queueName
     * @param msg
     */
    public void pushMsg(String queueName, Object msg) {
        rabbitTemplate.convertAndSend(queueName, msg);
    }
}
