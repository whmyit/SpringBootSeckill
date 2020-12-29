package com.dxhy.order.consumer;

import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author fankunfeng
 * @Date 2019-03-04 11:26:19
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class ExcelFileInputTest {

    @Autowired
    ExcelReadService excelReadService;

    @Autowired
    UserInfoService userInfoService;

    @Before
    public void setup() {

    }
    @Test
    public void wxAccountListTest() throws Exception {
        log.info("----------------------------------==============>>>>>>>>>>>>>>>开始");
        File pdfFile = new File("C:\\Users\\thinkpad\\Desktop\\d.xlsx");
        FileInputStream fileInputStream = new FileInputStream(pdfFile);
        MultipartFile multipartFile = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
                ContentType.APPLICATION_OCTET_STREAM.getMimeType(), fileInputStream);

        //Map<String, Object> stringObjectMap = excelReadService.readOrderInfoFromExcelxls(multipartFile);
        log.info("执行结果{}","aa");
    }

}
