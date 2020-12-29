package com.dxhy.order.consumer;

import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

/**
 * @Author fankunfeng
 * @Date 2019-06-17 14:28:54
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class UserTest {
    
    @Resource
    private UserInfoService userInfoService;
    
    @Test
    public void user() {
        UserEntity user = userInfoService.getUser();
        System.out.println(user);
    }
    
}
