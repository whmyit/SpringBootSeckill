package com.dxhy.order.consumer.config;

import com.dxhy.order.consumer.filter.UserCenterAuthenticationFilter;
import com.dxhy.order.xss.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 *
 * @author Mark sunlightcs@gmail.com
 * @since 2.1.0 2017-04-21
 */
@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
    
    @Bean
    public FilterRegistrationBean<UserCenterAuthenticationFilter> authenticationFilterRegistration() {
        FilterRegistrationBean<UserCenterAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setName("UserCenterAuthenticationFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(new UserCenterAuthenticationFilter());
        return registration;
    }
}
