package com.dxhy.order.consumer;

import com.dxhy.order.consumer.modules.order.controller.ReceiveOrderController;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class ImportExcelTest {
    
    @Autowired
    private ReceiveOrderController receiveController;


	@Test
	public void test1() throws IOException {
		log.info("excel导入测试开始:");
		File pdfFile = new File("D://testImport.xlsx");
		FileInputStream fileInputStream = new FileInputStream(pdfFile);
        MultipartFile multipartFile = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
                ContentType.APPLICATION_OCTET_STREAM.getMimeType(), fileInputStream);
		//File file = new File("D://aa.xlsx");
		//receiveController.acceptByExcel(multipartFile, "aaaaa");
    
    }

}
