package com.dxhy.order.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * swagger配置
 * 1.全局配置,所有订单相关的,大而全的东西
 * 2.订单相关配置
 * 3.发票相关配置
 * 4.税控底层相关配置
 *
 * @author ZSC-DXHY
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    
    @Bean
    public Docket simsOrderSwagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("order-api-swagger")
                .genericModelSubstitutes(DeferredResult.class)
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
                // base，最终调用接口后会和paths拼接在一起
                .pathMapping("/")
                .select()
                .paths(regex("/.*"))
                .build()
                .apiInfo(apiInfo());
    }
    
    private ApiInfo apiInfo() {
        // 大标题
        return new ApiInfoBuilder().title("销项订单")
                // 详细描述
                .description("订单管理系统")
                // 版本
                .version("1.0")
                // 作者
//                .contact(new Contact("chengyafu", "", "chengyafu@ele-cloud.com"))
                .build();
    }
    
}
