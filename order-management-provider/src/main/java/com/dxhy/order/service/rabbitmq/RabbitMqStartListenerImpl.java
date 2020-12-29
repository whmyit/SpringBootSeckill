package com.dxhy.order.service.rabbitmq;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.NsrQueueEnum;
import com.dxhy.order.dao.SysNsrQueueMapper;
import com.dxhy.order.model.SysNsrQueue;
import com.dxhy.order.rabbitplugin.core.LisenerFactory;
import com.dxhy.order.rabbitplugin.core.ListenerDeclare;
import com.dxhy.order.rabbitplugin.core.QueueDeclare;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.service.IRabbitMqStartListener;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 启动rabbitmq监听实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/6/19 0:23
 */
@Service
@Slf4j
public class RabbitMqStartListenerImpl implements IRabbitMqStartListener {
    private static final String LOGGER_MSG = "(启动rabbitmq监听实现类)";
    
    @Resource
    private ConnectionFactory rabbitConnectionFactory;
    
    @Resource
    private SysNsrQueueMapper sysNsrQueueMapper;
    
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;
    
    @Resource
    private InvoiceFpkjHandler invoiceFpkjHandler;
    @Resource
    private InvoiceYxtsHandler invoiceYxtsHandler;
    
    @Resource
    private InvoiceZftsHandler invoiceZftsHandler;
    @Resource
    private InvoiceFptsHandler invoiceFptsHandler;
    @Resource
    private InvoiceInsertCardHander invoiceInsertCardHander;
    
    
    /**
     * 启动rabbitmq监听
     *
     * @return
     */
    @Override
    public boolean startRabbitMqListener() {
        try {
            log.info("(动态监听定时)线程ID:{}-->开始动态加载附件监听", Thread.currentThread().getId());
            LisenerFactory factory = LisenerFactory.instantiation();
            Map<String, SimpleMessageListenerContainer> map = factory.getAllLiseners();
            /**
             * 从数据库中读取数据,
             */
            List<SysNsrQueue> queueList = sysNsrQueueMapper.selectNsrQueueList();
            log.info("{}查询到数据库队列{}条,缓存中监听{}条", LOGGER_MSG, queueList == null ? "0" : queueList.size(), map.size());
            if (queueList != null && queueList.size() > 0) {
                for (SysNsrQueue queue : queueList) {
                    /**
                     * 循环数据库中的数据,判断有效无效状态.
                     * 如果无效,则跳过不进行处理
                     */
                    if (StringUtils.isNotBlank(queue.getStatus()) && !ConfigureConstant.STRING_0.equals(queue.getStatus())) {
                        continue;
                    }
                    String queueName = queue.getQueueName();
                    String queuePrefix = queue.getQueuePrefix();
                    String listenerSize = queue.getListenerSize();
                    /**
                     * 如果队列前缀为空,并且不在以下枚举类型中,不进行创建.
                     */
                    if (StringUtils.isBlank(queuePrefix) || !NsrQueueEnum.getValues().contains(queuePrefix)) {
                        log.error("{}添加数据不在支持数据列表中,错误数据为:{},允许列表为:{}", LOGGER_MSG, queuePrefix, JsonUtils.getInstance().toJsonString(NsrQueueEnum.getValues()));
                        continue;
                    }
                    /**
                     * 判断队列监听是不是整数,如果不是整数需要使用默认值,如果是就直接使用
                     */
                    if (!MathUtil.isNumeric(listenerSize)) {
                        listenerSize = ConfigureConstant.STRING_1;
                    }
                    
                    
                    try {
                        /**
                         * 根据队列名称从mq服务器中获取队列数据,如果为空则进行创建队列.
                         */
                        String realQueueName = queuePrefix + queueName;
                        
                        if (map.get(factory.getLisenerMapKey(rabbitConnectionFactory, realQueueName)) == null) {
                            log.info("(动态监听定时)线程ID:{}-->队列:{}开始启动监听", Thread.currentThread().getId(), realQueueName);
                            List<String> queueNameList = new ArrayList<>();
                            queueNameList.add(realQueueName);
    
                            /**
                             * 为每个监听服务设置参数,
                             * 已知的监听服务有:
                             * 每个税号都有一个-发票开具队列,
                             * 每个税号都有一个-邮箱推送队列,
                             * 每个税号都有一个-延时开票队列,(本次使用延时功能,不再进行监听数据,等待消息自动失效后转入到开票队列)
                             * 每个税号都有一个-作废推送队列,
                             * 每个税号都有一个-推送发票队列,
                             *
                             */
                            
                            ListenerDeclare lisener = null;
                            if (queuePrefix.startsWith(NsrQueueEnum.FPKJ_MESSAGE.getValue())) {
                                /**
                                 * 发票开具队列监听
                                 */
                                lisener = new ListenerDeclare(rabbitConnectionFactory, queueNameList, Integer.parseInt(listenerSize), invoiceFpkjHandler);
                            } else if (queuePrefix.startsWith(NsrQueueEnum.YXTS_MESSAGE.getValue())) {
                                /**
                                 * 邮箱推送队列监听
                                 */
                                lisener = new ListenerDeclare(rabbitConnectionFactory, queueNameList, Integer.parseInt(listenerSize), invoiceYxtsHandler);
                            } else if (queuePrefix.startsWith(NsrQueueEnum.INVALID_MESSAGE.getValue())) {
                                /**
                                 * 作废推送监听
                                 */
                                lisener = new ListenerDeclare(rabbitConnectionFactory, queueNameList, Integer.parseInt(listenerSize), invoiceZftsHandler);
                            } else if (queuePrefix.startsWith(NsrQueueEnum.PUSH_MESSAGE.getValue())) {
                                /**
                                 * 推送发票监听
                                 */
                                lisener = new ListenerDeclare(rabbitConnectionFactory, queueNameList, Integer.parseInt(listenerSize), invoiceFptsHandler);
                            } else if (queuePrefix.startsWith(NsrQueueEnum.INSERT_CARD_MESSAGE.getValue())) {
                                /**
                                 * 插卡队列监听
                                 */
                                lisener = new ListenerDeclare(rabbitConnectionFactory, queueNameList, Integer.parseInt(listenerSize), invoiceInsertCardHander);
                            } else {
                                log.warn("{}线程ID:{}-->队列:{},不需自动启动", LOGGER_MSG, Thread.currentThread().getId(), queuePrefix);
                                continue;
                            }
                            /**
                             * 启动监听前需要创建队列
                             */
                            QueueDeclare queueDeclare = iRabbitMqSendMessage.setQueueDeclare(queue.getNsrsbh(), queuePrefix, queueName);
                            if (queueDeclare.declareQueue()) {
                                boolean b = lisener.declareLisener();
                                log.info("(动态监听定时)线程ID:{}-->队列:{}启动监听,结果:{}", Thread.currentThread().getId(), queuePrefix, b);
                            }
                            SysNsrQueue sysNsrQueue = new SysNsrQueue();
                            sysNsrQueue.setId(queue.getId());
                            sysNsrQueue.setListenerStatus("0");
                            sysNsrQueueMapper.updateNsrQueueByPrimaryKeySelective(sysNsrQueue);
                        }
                    } catch (Exception e) {
                        log.error("(动态监听定时)线程ID:{}-->队列:{}添加监听失败,异常:", Thread.currentThread().getId(), queuePrefix, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("(动态监听定时)线程ID:{}-->队列定时任务异常", Thread.currentThread().getId(), e);
            return false;
        }
        
        return true;
    }
}
