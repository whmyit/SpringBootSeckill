package com.dxhy.order.consumer.openapi.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(
        name="invoiceWsApi",
        targetNamespace = "http://webservice.openapi.order.dxhy.com"
)
public interface IInvoiceWsApi {
    @WebMethod
    @WebResult(name = "result")
    public String orderAPIForWS(@WebParam(name = "version") String version,//接口版本V3、V4
                                @WebParam(name = "method") String method,//业务方法名称
                                @WebParam(name = "timestamp") String timestamp,//时间戳
                                @WebParam(name = "nonce") String nonce,//随机数
                                @WebParam(name = "secretId") String secretId,//secretId 用于生成签名串
                                @WebParam(name = "signature") String signature,//调用方的签名串
                                @WebParam(name = "encryptCode") String encryptCode,//加密标识 0:不加密,1:加密
                                @WebParam(name = "zipCode") String zipCode,//压缩标识 0:不压缩,1:压缩
                                @WebParam(name = "content") String content);//业务报文
}
