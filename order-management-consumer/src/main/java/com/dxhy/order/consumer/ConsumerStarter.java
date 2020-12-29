package com.dxhy.order.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单服务启动类
 *
 * @author ZSC-DXHY
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDubbo
@ComponentScan("com.dxhy")
//@EnableEurekaClient
public class ConsumerStarter extends SpringBootServletInitializer {
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ConsumerStarter.class);
    }
    
    public static void main(String[] args) {
        new SpringApplicationBuilder(ConsumerStarter.class)
                .run(args);
    }
    
    /**
     * eureka调用地址,仅调用辅助运营
     *
     * @return
     */
/*    @Bean
    @LoadBalanced
    public RestTemplate eurekaRestTemplate() {
        return new RestTemplate();
    }*/
    
}
