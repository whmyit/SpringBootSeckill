//package com.dxhy.order.consumer.openapi.api;
//
//import com.dxhy.order.constant.ConfigurerInfo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 开放平台：订单对外接口 通过SpringCloud网关的方式发布
// * 后期服务以版本号进行区分,
// *
// * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
// * @createDate: Created in 2020/3/27
// */
//@RestController
//@Slf4j
//@Api(value = "订单开放平台", tags = {"订单接口模块"})
//public class InvoiceOpenGateRestApi {
//
//    private static final String LOGGER_MESSAGE_V1 = "(销项开放平台服务V1)";
//    private static final String LOGGER_MESSAGE_V2 = "(销项开放平台服务V2)";
//    private static final String LOGGER_MESSAGE_V3 = "(销项开放平台服务V3)";
//    private static final String LOGGER_MESSAGE_V4 = "(销项开放平台服务V4)";
//
//    /**
//     * 订单对外接口依赖 {@link InvoiceRestApi}
//     */
//    @Resource
//    private InvoiceRestApi invoiceRestApi;
//
//    @Resource
//    private InvoiceRestApiV1 invoiceRestApiV1;
//
//    @ApiOperation(value = "订单对内接口V3", notes = "订单开放平台-订单对内接口入口V3版本")
//    @RequestMapping(path = "/v3/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
//    public String orderApiV3(@PathVariable("interfaceName") String interfaceName,
//                             @RequestBody String requestBody, HttpServletRequest request) {
//
//        log.debug("{}-接口方法名称：{}", LOGGER_MESSAGE_V3, interfaceName);
//        log.debug("{}-需要交换的明文数据内容：{}", LOGGER_MESSAGE_V3, requestBody);
//
//
//        //返回数据
//        String resultString = "";
//        try {
//            //调用销项v3业务处理逻辑
//            resultString = invoiceRestApi.orderApiV3HandingBusiness(interfaceName, requestBody, "");
//            log.info("{}-业务处理结果：{}", LOGGER_MESSAGE_V3, resultString);
//        } catch (Exception e) {
//            log.error("{}-处理业务出现异常,异常原因为:{}", LOGGER_MESSAGE_V3, e);
//        }
//        return resultString;
//    }
//
//    @ApiOperation(value = "订单对内接口V4", notes = "订单开放平台-订单对内接口入口V4版本")
//    @RequestMapping(path = "/v4/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
//    public String orderApiV4(@PathVariable("interfaceName") String interfaceName,
//                             @RequestBody String requestBody, HttpServletRequest request) {
//
//        log.debug("{}-接口方法名称：{}", LOGGER_MESSAGE_V4, interfaceName);
//        log.debug("{}-需要交换的明文数据内容：{}", LOGGER_MESSAGE_V4, requestBody);
//
//        //返回数据
//        String resultString = "";
//        try {
//            //调用销项v4业务处理逻辑
//            resultString = invoiceRestApi.orderApiV4HandingBusiness(interfaceName, requestBody, "");
//            log.info("{}-业务处理结果：{}", LOGGER_MESSAGE_V4, resultString);
//        } catch (Exception e) {
//            log.error("{}-处理业务出现异常,异常原因为:{}", LOGGER_MESSAGE_V4, e);
//        }
//        return resultString;
//    }
//
//    @ApiOperation(value = "订单对内接口V2", notes = "订单开放平台-订单对内接口入口V2版本")
//    @RequestMapping(path = "/v2/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
//    public String orderApiV2(@PathVariable("interfaceName") String interfaceName,
//                             @RequestBody String requestBody, HttpServletRequest request) {
//
//        log.debug("{}-接口方法名称：{}", LOGGER_MESSAGE_V2, interfaceName);
//        log.debug("{}-需要交换的明文数据内容：{}", LOGGER_MESSAGE_V2, requestBody);
//
//
//        //返回数据
//        String resultString = "";
//        try {
//            //调用销项v3业务处理逻辑
//            resultString = invoiceRestApiV1.orderapiV2Process(interfaceName, ConfigurerInfo.INTERFACE_VERSION_V2, requestBody);
//            log.info("{}-业务处理结果：{}", LOGGER_MESSAGE_V2, resultString);
//        } catch (Exception e) {
//            log.error("{}-处理业务出现异常,异常原因为:{}", LOGGER_MESSAGE_V2, e);
//        }
//        return resultString;
//    }
//
//    @ApiOperation(value = "订单对内接口V1", notes = "订单开放平台-订单对内接口入口V1版本")
//    @RequestMapping(path = "/v1/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
//    public String orderApiV1(@PathVariable("interfaceName") String interfaceName,
//                             @RequestBody String requestBody, HttpServletRequest request) {
//
//        log.debug("{}-接口方法名称：{}", LOGGER_MESSAGE_V1, interfaceName);
//        log.debug("{}-需要交换的明文数据内容：{}", LOGGER_MESSAGE_V1, requestBody);
//
//
//        //返回数据
//        String resultString = "";
//        try {
//            //调用销项v3业务处理逻辑
//            resultString = invoiceRestApiV1.orderapiV2Process(interfaceName, ConfigurerInfo.INTERFACE_VERSION_V1, requestBody);
//            log.info("{}-业务处理结果：{}", LOGGER_MESSAGE_V1, resultString);
//        } catch (Exception e) {
//            log.error("{}-处理业务出现异常,异常原因为:{}", LOGGER_MESSAGE_V1, e);
//        }
//        return resultString;
//    }
//}
