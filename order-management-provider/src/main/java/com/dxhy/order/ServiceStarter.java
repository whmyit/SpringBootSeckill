package com.dxhy.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单provider启动类
 *
 * @author ZSC-DXHY
 */
@EnableRabbit

@SpringBootApplication
@EnableDubbo
@ComponentScan(value = {"com.dxhy","com.elephant"})
@MapperScan(basePackages = "com.dxhy.order.dao")
@EnableConfigurationProperties
public class ServiceStarter extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServiceStarter.class);
    }
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceStarter.class)
                .run(args);
    }
}
