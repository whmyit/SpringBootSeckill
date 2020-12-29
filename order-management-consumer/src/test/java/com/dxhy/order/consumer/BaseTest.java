package com.dxhy.order.consumer;

import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author fangyibai
 * @Description 单元测试基础类
 * @Date 2019/6/25
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public abstract class BaseTest {


    @Before
    public void setUp() throws Exception {
        log.info("test start...");
    }

    @After
    public void tearDown() throws Exception {
        log.info("test end...");
    }

    /**
     * 输出JSON格式
     */
    protected void printJSON(Object args){
        log.info(JsonUtils.getInstance().toJsonString(args));
    }

    //protected abstract void print(Object args);


}
