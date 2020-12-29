package com.dxhy.order.consumer.pdf;

import com.dxhy.order.consumer.ConsumerStarter;
import com.dxhy.order.consumer.generateinvoice.PDFProducer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

/**
 * 生成pdf测试工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/9/23 17:55
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class CreatePDF {
    
    @Test
    public void testCreate() throws IOException {
        String[] args = null;
        PDFProducer.test(args);
        
    }
}
