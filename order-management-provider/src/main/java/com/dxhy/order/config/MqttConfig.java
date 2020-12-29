package com.dxhy.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Author xueanna
 * @Date 2019/7/3 19:05
 */
@Component
@Configuration
public class MqttConfig {

    //todo 待优化

    @Bean
    public MqttPushClient getMqttPushClient() {
        MqttPushClient mqttPushClient = new MqttPushClient();
        mqttPushClient.connect(OpenApiConfig.mqttHost, OpenApiConfig.mqttUserName, OpenApiConfig.mqttPassword, Integer.parseInt(OpenApiConfig.mqttTimeout), Integer.parseInt(OpenApiConfig.mqttKeepAlive));
        return mqttPushClient;
    }

}
