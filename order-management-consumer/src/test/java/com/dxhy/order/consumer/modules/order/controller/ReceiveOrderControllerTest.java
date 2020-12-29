//package com.dxhy.order.consumer.modules.order.controller;
//
//import com.dxhy.order.consumer.BaseTest;
//import com.dxhy.order.consumer.config.OpenApiConfig;
//import com.dxhy.order.model.R;
//import com.dxhy.order.utils.JsonUtils;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import javax.annotation.Resource;
//
//public class ReceiveOrderControllerTest extends BaseTest {
//
//    @Autowired
//    private ReceiveOrderController receiveOrderController;
//
//    @Resource
//    private RestTemplate eurekaRestTemplate;
//
//    @Override
//    public void setUp() throws Exception {
//        //初始化mock数据
//        System.out.println("-----------------");
//    }
//
//    @Override
//    public void tearDown() throws Exception {
//        System.out.println("-----------------");
//
//    }
//
//
//    @Test
//    public void acceptByExcel() throws Exception {
//      /*  MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
//        request.setMethod("POST");
//        request.setContentType("multipart/form-data");
//        request.addHeader("Content-type", "multipart/form-data");
//        FileInputStream fis = new FileInputStream("/Users/fangyibai/Downloads/订单导入excel模板.xlsx");
//        MockMultipartFile mfile = new MockMultipartFile("/Users/fangyibai/Desktop/", "订单导入excel模板.xlsx", "application/vnd_ms-excel", fis);
//        R r = receiveOrderController.acceptByExcel("504", mfile, "10815", "2019-06-28");
//        printJSON(r);*/
//    }
//
//
//    @Test
//    public void test1() {
//        String url = "http://aosp-customer/customer/company-customer/getCustomerPage?productId=%s&sourceType=9&page=%s&limit=%s&location=&companyName=";
//        url = String.format(url, OpenApiConfig.systemProductId, 1, 10);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("distributorId", "0");
//        headers.add("Accept", "application/json");
//
//        ResponseEntity<String> resEntity = eurekaRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
//        url = "http://10.1.2.5:8085/customer/company-customer/getCustomerPage?productId=%s&sourceType=9&page=%s&limit=%s&location=&companyName=";
//        url = String.format(url, OpenApiConfig.systemProductId, 1, 10);
//        resEntity = new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
//        R r = JsonUtils.getInstance().parseObject(resEntity.getBody(), R.class);
//        System.out.println(JsonUtils.getInstance().toJsonString(r));
//    }
//}
