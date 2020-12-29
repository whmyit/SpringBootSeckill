package com.dxhy.order.dao;

import com.dxhy.order.model.PushInfo;
import com.dxhy.order.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @Author fankunfeng
 * @Date 2019-05-22 16:03:28
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest("ServiceStarter.class")
@WebAppConfiguration
public class PushInfoMapperTest {

    @Autowired
    PushInfoMapper pushInfoMapper;
    @Test
    public void selectByprimaryId(){
//        System.out.println(new SqlDateType().getDataType());
        PushInfo pushInfo = new PushInfo();
        pushInfo.setId("12345677");
        PushInfo pushInfo1 = pushInfoMapper.selectByPushInfo(pushInfo);
        System.out.println(JsonUtils.getInstance().toJsonString(pushInfo1));
    }
}
