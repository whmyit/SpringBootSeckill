package com.dxhy.order.service.rabbitmq;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.dao.SysNsrQueueMapper;
import com.dxhy.order.model.SysNsrQueue;
import com.dxhy.order.rabbit.queueutil.QueueCache;
import com.dxhy.order.rabbitplugin.core.QueueDeclare;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.service.IRabbitMqStartListener;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * rabbitmq发送消息实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/6/19 0:23
 */
@Service
@Slf4j
public class RabbitMqSendMessageImpl implements IRabbitMqSendMessage {
    private static final String LOGGER_MSG = "(rabbitmq发送消息实现类)";
    
    @Resource
    private ConnectionFactory rabbitConnectionFactory;
    
    @Resource
    private AmqpTemplate rabbitTemplate;
    
    @Resource
    private SysNsrQueueMapper sysNsrQueueMapper;
    
    @Resource
    private IRabbitMqStartListener iRabbitMqStartListener;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 保存消息
     *
     * @param queueName
     * @param obj
     * @return
     */
    @Override
    public void sendRabbitMqMessage(String queueName, Object obj) {
        
        QueueDeclare queueDeclare = new QueueDeclare(queueName, rabbitConnectionFactory);
        queueDeclare.convertAndSend(rabbitTemplate, obj);
    }
    
    /**
     * 自动保存消息到队列
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param obj
     */
    @Override
    public void autoSendRabbitMqMessage(String nsrsbh, String queuePrefix, Object obj) {
        log.debug("{}消息数据存入队列,请求税号为:{},队列前缀为:{},存入数据为:{}", LOGGER_MSG, nsrsbh, queuePrefix, JsonUtils.getInstance().toJsonString(obj));
        String queueName = nsrsbh;
        SysNsrQueue queue = (SysNsrQueue) QueueCache.QUEUE_MAP.get(nsrsbh + queuePrefix);
        if (queue != null) {
            log.debug("{}消息数据存入队列,根据税号加前缀:{},查询到的数据为:{}", LOGGER_MSG, nsrsbh + queuePrefix, JsonUtils.getInstance().toJsonString(queue));
            queueName = queue.getQueueName();
        } else {
            log.warn("{}消息存入队列,从缓存中查询到的数据为空!", LOGGER_MSG);
        }
        QueueDeclare queueDeclare = getQueueDeclare(nsrsbh, queuePrefix, queueName);
        queueDeclare.convertAndSend(rabbitTemplate, obj);
    
    }
    
    /**
     * 往延时队列中发送消息,如果未创建队列自动创建队列,每次调用需要指定延时队列数据失效后发送到指定队列的队列名称,
     * 默认消息延时时间为10秒,后期支持自动匹配延时.
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param obj
     * @param ttlQueueName
     * @param ttl
     */
    @Override
    public void autoSendRabbitMqMessageDelay(String nsrsbh, String queuePrefix, Object obj, String ttlQueueName, long ttl) {
        log.debug("{}消息数据存入队列,请求税号为:{},队列前缀为:{},存入数据为:{}", LOGGER_MSG, nsrsbh, queuePrefix, JsonUtils.getInstance().toJsonString(obj));
        SysNsrQueue queue = (SysNsrQueue) QueueCache.QUEUE_MAP.get(nsrsbh + queuePrefix);
        QueueDeclare queueDeclare = new QueueDeclare(queuePrefix + nsrsbh, rabbitConnectionFactory);
        if (queue == null) {
            
            boolean declare = queueDeclare.declarettlqueue(ttlQueueName);
            if (declare) {
                SysNsrQueue sysNsrQueue = new SysNsrQueue();
                sysNsrQueue.setId(apiInvoiceCommonService.getGenerateShotKey());
                sysNsrQueue.setNsrsbh(nsrsbh);
                sysNsrQueue.setQueuePrefix(queuePrefix);
                sysNsrQueue.setQueueName(nsrsbh);
                sysNsrQueue.setListenerStatus(ConfigureConstant.STRING_1);
                sysNsrQueue.setListenerSize(ConfigureConstant.STRING_1);
                sysNsrQueue.setStatus(ConfigureConstant.STRING_0);
                sysNsrQueue.setCreateTime(new Date());
                sysNsrQueue.setUpdateTime(new Date());
                QueueCache.QUEUE_MAP.put(nsrsbh + queuePrefix, sysNsrQueue);
            }
        }
        
        queueDeclare.convertAndSendttl(rabbitTemplate, obj, ttl);
        
    }
    
    /**
     * 设置消息
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param queueName
     * @return
     */
    @Override
    public QueueDeclare setQueueDeclare(String nsrsbh, String queuePrefix, String queueName) {
        return getQueueDeclare(nsrsbh, queuePrefix, queueName);
    }
    
    /**
     * 保存消息或者是缓存消息
     *
     * @param nsrsbh
     * @param queuePrefix
     * @param queueName
     * @return
     */
    private QueueDeclare getQueueDeclare(String nsrsbh, String queuePrefix, String queueName) {
        SysNsrQueue queue = (SysNsrQueue) QueueCache.QUEUE_MAP.get(nsrsbh + queuePrefix);
        QueueDeclare queueDeclare = new QueueDeclare(queuePrefix + queueName, rabbitConnectionFactory);
        if (queue == null) {
            boolean declare = queueDeclare.declareQueue();
            if (declare) {
                int count = 0;
                SysNsrQueue sysNsrQueues = sysNsrQueueMapper.selectNsrQueueListByNsrsbh(nsrsbh, queuePrefix);
                
                if (sysNsrQueues == null) {
                    SysNsrQueue sysNsrQueue = new SysNsrQueue();
                    sysNsrQueue.setId(apiInvoiceCommonService.getGenerateShotKey());
                    sysNsrQueue.setNsrsbh(nsrsbh);
                    sysNsrQueue.setQueuePrefix(queuePrefix);
                    sysNsrQueue.setQueueName(queueName);
                    sysNsrQueue.setListenerStatus(ConfigureConstant.STRING_1);
                    sysNsrQueue.setListenerSize(ConfigureConstant.STRING_1);
                    sysNsrQueue.setStatus(ConfigureConstant.STRING_0);
                    sysNsrQueue.setCreateTime(new Date());
                    sysNsrQueue.setUpdateTime(new Date());
                    
                    count = sysNsrQueueMapper.insertNsrQueueSelective(sysNsrQueue);
                    
                    if (count > 0) {
                        /**
                         * 初始化数据时直接启动监听
                         */
                        iRabbitMqStartListener.startRabbitMqListener();
                        QueueCache.QUEUE_MAP.put(nsrsbh + queuePrefix, sysNsrQueue);
                    }
                } else {
                    QueueCache.QUEUE_MAP.put(nsrsbh + queuePrefix, sysNsrQueues);
                }
                
                log.info("线程ID:{}--> queue {} insert db success", Thread.currentThread().getId(), queueName);
            } else {
                log.error("线程ID:{}--> 创建queue {}失败", Thread.currentThread().getId(), queueName);
            }
        }
        return queueDeclare;
    }
}
