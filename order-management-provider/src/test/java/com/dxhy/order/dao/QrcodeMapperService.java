package com.dxhy.order.dao;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.model.InvoiceCount;
import com.dxhy.order.model.OrderInvoiceDetail;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.*;

/**
 * @Author fankunfeng
 * @Date 2019-05-28 11:38:40
 * @Describe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest("ServiceStarter.class")
@WebAppConfiguration
public class QrcodeMapperService {
	
	
	
    @Autowired
    QuickResponseCodeInfoMapper qrMapper;
    
    
    
    @Test
    public void testQuery(){
    	
    	Map<String,Object> paramMap = new HashMap<String,Object>();
    	
    	 paramMap.put("startTime", "2019-12-11 13:11:11");
         paramMap.put("endTime", "2020-2-19 11:11:11");
         //paramMap.put("minJe", "3.14");
         //paramMap.put("maxJe", "1000000000");
         paramMap.put("ghfmc","大象慧云信息技术");
         paramMap.put("fpzldm", "51");
         paramMap.put("kpzt", "0");
         //paramMap.put("ewmzt", "0");
         //paramMap.put("startValidTime", startValidTime);
         //paramMap.put("endValidTime", endValidTime);
         List<String> xfsh = new ArrayList<String>();
         xfsh.add("911101082018050516");
         paramMap.put("xfsh", xfsh);
    	 //qrMapper.selectDynamicQrCodeList(paramMap);
    }
}
