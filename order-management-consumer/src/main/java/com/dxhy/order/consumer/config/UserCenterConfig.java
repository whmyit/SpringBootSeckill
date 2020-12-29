package com.dxhy.order.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 用户中心认证：配置文件信息读取(application-{profiles}.yml)
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8
 */
@Getter
@Setter
@Configuration
public class UserCenterConfig {
    
    /**
     * 用户退出登录URL
     */
    public static String logoutPath;
    
    /**
     * 过滤器不拦截的URL,以逗号隔开
     */
    public static String excludedPaths;
    
    /**
     * token失效时重定向的地址
     */
    public static String redirectUrl;

    public static String samllAppPath = "supplyChain";
    
    
    @Autowired
    public void initConfig(PropertiesUtils propertiesUtils) {
        logoutPath = propertiesUtils.getLogoutPath();
        excludedPaths = propertiesUtils.getExcludedPaths();
        redirectUrl = propertiesUtils.getRedirectUrl();
    }
}
