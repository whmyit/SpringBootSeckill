package com.dxhy.order.dao;

import com.dxhy.order.model.AuthenticationInfo;
import com.dxhy.order.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-05-23 19:43:30
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AuthenticationInfoMapperTest {
    @Autowired
    AuthenticationInfoMapper authenticationInfoMapper;
    @Test
    public void selectAuthticationAll(){
        List<AuthenticationInfo> authenticationInfos = authenticationInfoMapper.selectAuthticationAll("0");
        System.out.println(JsonUtils.getInstance().toJsonString(authenticationInfos));

    }
}
