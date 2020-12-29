package com.dxhy.order.consumer;

import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.utils.CommonFileUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName ：ExportExcelTests
 * @Description ：导出excel控制层
 * @author ：杨士勇
 * @date ：2019年5月25日 下午1:49:30
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class ExportExcelTests {
	
	private static final String TEMPORARY_FILE_NAME = "/temporary_file_";

	private static final String TEMP_INVOICE_ITEM_NAME = "ExportExcelFile";

	private static final String XLSX = ".xlsx";
	
	@Autowired
	ExcelReadService  excelReadService;
	
	@Autowired
	UserInfoService userInfoService;
	
	
	@Test
	public void test1() throws IOException {
		String filePrefix = "123";
		File file = CommonFileUtils.creaetFile(getExportTempFilePath(TEMP_INVOICE_ITEM_NAME),
				TEMPORARY_FILE_NAME + filePrefix + XLSX);
		
		Map<String, Object> map = new HashMap<>(5);
		String xhfNsrsbh = "[\"123123\"]";
		List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
		map.put("startTime", "2019-07-01");
		map.put("endTime", "2019-07-19");
		excelReadService.exportInvoiceDetailExcel(file, null, map, shList);
	}
	
	
	private String getExportTempFilePath(String str) {
		return this.getClass().getClassLoader().getResource("").getPath() + str;
	}

	@Test
	public void test2() throws IOException {
		

		//JSONObject deptInfoByDeptId = userInfoService.queryUserInfoByDeptId("1");
		//System.out.println(JsonUtils.getInstance().toJsonString(deptInfoByDeptId));
	}

}
