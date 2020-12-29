package com.dxhy.order.rabbitplugin.monitor;

import com.dxhy.order.rabbitplugin.core.LisenerFactory;
import com.dxhy.order.rabbitplugin.util.Utils;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 消息监控管理页面
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:17
 */
public class MonitorManager {
    public static List<PoolStatus> checkStatus() {
        Map<String, SimpleMessageListenerContainer> threadMap = LisenerFactory.instantiation().getAllLiseners();
        if (threadMap == null || threadMap.size() == 0) {
            return new ArrayList<>();
        } else {
            Iterator<Entry<String, SimpleMessageListenerContainer>> messageListenerEntity = threadMap.entrySet().iterator();
            List<PoolStatus> list = new ArrayList<>();
            while (messageListenerEntity.hasNext()) {
                Entry<String, SimpleMessageListenerContainer> lisnenrEntity = messageListenerEntity.next();
                SimpleMessageListenerContainer container = lisnenrEntity.getValue();
                if (container != null) {
                    PoolStatus ps = new PoolStatus();
                    ps.setHost(container.getConnectionFactory().getHost());
                    ps.setQueueName(Utils.linkString(container.getQueueNames()));
                    ps.setLisStatus(container.isActive());
                    ps.setActiveConsumerCount(container.getActiveConsumerCount());
                    ps.setRunning(container.isRunning());
                    list.add(ps);
                }
            }
            return list;
        }
    }
    
    public static List<PoolStatus> checkStatusByHost(String ip) {
        Map<String, SimpleMessageListenerContainer> threadMap = LisenerFactory.instantiation().getAllLiseners();
        if (threadMap == null || threadMap.size() == 0) {
            return new ArrayList<>();
        } else {
            Iterator<Entry<String, SimpleMessageListenerContainer>> messageListenerEntity = threadMap.entrySet().iterator();
            List<PoolStatus> list = new ArrayList<>();
            while (messageListenerEntity.hasNext()) {
                Entry<String, SimpleMessageListenerContainer> entity = messageListenerEntity.next();
                if (entity.getKey().indexOf(ip) > 0) {
                    SimpleMessageListenerContainer container = entity.getValue();
                    if (container != null) {
                        PoolStatus ps = new PoolStatus();
                        ps.setHost(container.getConnectionFactory().getHost());
                        ps.setQueueName(Utils.linkString(container.getQueueNames()));
                        ps.setLisStatus(container.isActive());
                        ps.setActiveConsumerCount(container.getActiveConsumerCount());
                        ps.setRunning(container.isRunning());
                        list.add(ps);
                    }
                }
            }
            return list;
        }
    }
    
    public static List<PoolStatus> checkStatusByQueue(String queueName) {
        Map<String, SimpleMessageListenerContainer> threadMap = LisenerFactory.instantiation().getAllLiseners();
        if (threadMap == null || threadMap.size() == 0) {
            return new ArrayList<>();
        } else {
            Iterator<Entry<String, SimpleMessageListenerContainer>> messageListenerEntity = threadMap.entrySet().iterator();
            List<PoolStatus> list = new ArrayList<>();
            while (messageListenerEntity.hasNext()) {
                Entry<String, SimpleMessageListenerContainer> entity = messageListenerEntity.next();
                if (entity.getKey().indexOf(queueName) > 0) {
                    SimpleMessageListenerContainer container = entity.getValue();
                    if (container != null) {
                        PoolStatus ps = new PoolStatus();
                        ps.setHost(container.getConnectionFactory().getHost());
                        ps.setQueueName(Utils.linkString(container.getQueueNames()));
                        ps.setLisStatus(container.isActive());
                        ps.setActiveConsumerCount(container.getActiveConsumerCount());
                        ps.setRunning(container.isRunning());
                        list.add(ps);
                    }
                }
            }
            return list;
        }
    }
    
    public static List<PoolStatus> checkStatusByQueueAndHost(String ip, String queueName) {
        Map<String, SimpleMessageListenerContainer> threadMap = LisenerFactory.instantiation().getAllLiseners();
        if (threadMap == null || threadMap.size() == 0) {
            return new ArrayList<>();
        } else {
            Iterator<Entry<String, SimpleMessageListenerContainer>> messageListenerEntity = threadMap.entrySet().iterator();
            List<PoolStatus> list = new ArrayList<>();
            while (messageListenerEntity.hasNext()) {
                Entry<String, SimpleMessageListenerContainer> entity = messageListenerEntity.next();
                if (entity.getKey().indexOf(ip) > 0 && entity.getKey().indexOf(queueName) > 0) {
                    SimpleMessageListenerContainer container = entity.getValue();
                    if (container != null) {
                        PoolStatus ps = new PoolStatus();
                        ps.setHost(container.getConnectionFactory().getHost());
                        ps.setQueueName(Utils.linkString(container.getQueueNames()));
                        ps.setLisStatus(container.isActive());
                        ps.setActiveConsumerCount(container.getActiveConsumerCount());
                        ps.setRunning(container.isRunning());
                        list.add(ps);
                    }
                }
            }
            return list;
        }
    }
}
