package com.dxhy.order.config;

import com.dxhy.order.service.IRabbitMqStartListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 在服务启动后,启动rabbitmq监听
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/6/19 0:17
 */
@Component
@Slf4j
public class RabbitMqListenerAfterSystemRun implements ApplicationRunner {
    private final static String LOGGER_MSG = "(RabbitMq监听启动类)";
    
    @Resource
    IRabbitMqStartListener iRabbitMqStartListener;
    
    @Override
    public void run(ApplicationArguments args) {
        
        log.info("{}监听服务启动!", LOGGER_MSG);
        
        boolean result = iRabbitMqStartListener.startRabbitMqListener();
        
        if (result) {
            log.info("{}监听服务启动成功!", LOGGER_MSG);
        } else {
            log.error("{}监听服务启动失败!", LOGGER_MSG);
        }
    }
}
