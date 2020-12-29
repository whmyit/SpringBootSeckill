package com.dxhy.order.consumer.openapi.webservice.impl;

import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.consumer.constant.RespStatusEnum;
import com.dxhy.order.consumer.openapi.api.InvoiceRestApi;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.openapi.webservice.IInvoiceWsApi;
import com.dxhy.order.model.protocol.ResponseData;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;

import static org.joda.time.DateTime.now;

@WebService(
        targetNamespace = "http://webservice.openapi.order.dxhy.com",
        endpointInterface = "com.dxhy.order.consumer.openapi.webservice.IInvoiceWsApi"
)
@Component
@Slf4j
public class InvoiceWsApiImpl implements IInvoiceWsApi{
    private static final String LOGGER_MSG = "(WS对外接口V3/V4)";
/*
    @Resource
    private WebServiceContext context;
*/

    @Reference
    private ICommonDisposeService commonDisposeService;

    @Resource
    private ICommonInterfaceService commonInterfaceService;

    @Autowired
    private InvoiceRestApi invoiceRestApi;

    @Override
    public String orderAPIForWS(
            String version,
            String method,
            String timestamp,
            String nonce,
            String secretId,
            String signature,
            String encryptCode,
            String zipCode,
            String content) {
        log.info("开放接口webservice请求，版本：{}，接口方法：{}，时间戳：{}，随机数：{}，secretId：{}，签名串：{}，加密标识：{}，压缩标识：{}，业务报文：{}"
                ,version
                ,method
                ,timestamp
                ,nonce
                ,secretId
                ,signature
                ,encryptCode
                ,zipCode
                ,content);
        //获取http对象
/*        MessageContext messageContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);
        HttpServletResponse response = (HttpServletResponse) messageContext.get(MessageContext.SERVLET_RESPONSE);
        String responseStr = invoiceRestApi.orderApiV3(request, response, version, method, timestamp, nonce, secretId, signature, encryptCode, zipCode, content);*/

        Result result = new Result();
        ResponseStatus responseStatus = new ResponseStatus();
        ResponseData responseData = new ResponseData();
        try {
            /**
             * 校验接口入参是否为空
             */
            result = commonInterfaceService.checkInterfaceParam(version, method, timestamp, nonce, secretId, signature, encryptCode, zipCode, content);
            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);
            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},数据格式校验未通过.", LOGGER_MSG);
                return JsonUtils.getInstance().toJsonString(result);
            }

            final DateTime begin1 = now();
            /**
             * 鉴权
             */
            result = commonInterfaceService.auth(timestamp, nonce, secretId, signature, encryptCode, zipCode, content);
            final long millSeconds1 = new Duration(begin1, now()).getMillis();
            log.debug("{}鉴权耗时{}毫秒", LOGGER_MSG, millSeconds1);
            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);
            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},鉴权未通过", LOGGER_MSG);
                return JsonUtils.getInstance().toJsonString(result);
            }

            //解密
            CommonRequestParam commonRequestParam = new CommonRequestParam();
            commonRequestParam.setZipCode(zipCode);
            commonRequestParam.setEncryptCode(encryptCode);
            commonRequestParam.setContent(content);
            commonRequestParam.setSecretId(secretId);
            String commonDecrypt2 = commonDisposeService.commonDecrypt(commonRequestParam);
            log.debug("{}解密日志：{}", LOGGER_MSG, commonDecrypt2);
            String returnJsonString = "";
            //调用业务处理逻辑方法
            if (ConfigurerInfo.INTERFACE_VERSION_V4.equals(version)) {
                returnJsonString = invoiceRestApi.orderApiV4HandingBusiness(method, commonDecrypt2, secretId,"1");
            } else {
                returnJsonString = invoiceRestApi.orderApiV3HandingBusiness(method, commonDecrypt2, secretId,"1");
            }

            String data = null;
            log.debug("{},接口返回数据:{}", LOGGER_MSG, returnJsonString);
            if (!StringUtils.isBlank(returnJsonString)) {
                commonRequestParam.setContent(returnJsonString);
                /**
                 * 加密
                 */
                data = commonDisposeService.commonEncrypt(commonRequestParam);
                log.debug("{},加密后返回数据:{}", LOGGER_MSG, data);
            }
            if (data != null) {
                responseStatus.setCode(RespStatusEnum.SUCCESS.getCode());
                responseStatus.setMessage(RespStatusEnum.SUCCESS.getDescribe());
                responseData.setContent(data);
                responseData.setEncryptCode(commonRequestParam.getEncryptCode());
                responseData.setZipCode(commonRequestParam.getZipCode());
                result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
                result.put(ConfigurerInfo.RESPONSEDATA, responseData);
                log.info("{},开放接口webservice处理结束:{}调用成功,返回数据:{}", LOGGER_MSG, method, JsonUtils.getInstance().toJsonString(result));
                return JsonUtils.getInstance().toJsonString(result);
            }
        } catch (Exception e) {
            log.error("{},开放接口webservice处理结束，接口请求数据出现异常,异常原因为:{}", LOGGER_MSG, e);
        }
        responseStatus.setCode(RespStatusEnum.FAIL.getCode());
        responseStatus.setMessage(RespStatusEnum.FAIL.getDescribe());
        result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        log.info("{},开放接口webservice处理结束：接口:{}调用失败,返回数据:{}", LOGGER_MSG, method, JsonUtils.getInstance().toJsonString(result));
        return JsonUtils.getInstance().toJsonString(result);
    }
}
