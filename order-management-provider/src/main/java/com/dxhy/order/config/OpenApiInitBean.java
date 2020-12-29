package com.dxhy.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA
 * Created By YangF
 * Date: 2018/2/6
 * Time: 16:34
 * @author ZSC-DXHY
 */
@Configuration
@Getter
@Setter
public class OpenApiInitBean {

    @Value("${openapi.OPENAPI_EMAIL_NOTE}")
    private String openApiEmailNote;
    @Value("${openapi.OPENAPI_TOKEN}")
    private String openApiToken;
    @Value("${openapi.TOKEN_CLIENT_ID}")
    private String tokenClientId;
    @Value("${openapi.TOKEN_CLIENT_SECRET}")
    private String tokenClientSecret;
    @Value("${openapi.TOKEN_GRANT_TYPE}")
    private String tokenGrantType;
    @Value("${openapi.TOKEN_SCOPE}")
    private String tokenScope;
    @Value("${openapi.SECRET_ID}")
    private String secretId;
    @Value("${openapi.SECRET_KEY}")
    private String secretKey;
    @Value("${openapi.REQUEST_PATH}")
    private String requestPath;
    @Value("${openapi.REQUEST_DOMAIN}")
    private String requestDomain;
    @Value("${interfacePath.A9.domain}")
    private String domain;

    @Value("${interfacePath.FG.domain}")
    private String interfaceFgBusinessUrl;
    
    /**
     * OFD转png服务
     */
    @Value("${orderProvider.url.OfdToPngUrl}")
    private String OfdToPngUrl;
    
    /**
     * 异常订单邮件模板
     */
    @Value("${email-template.Invoice_Yichang}")
    public String invoiceYiChang;
    @Value("${email-template.Invoice_Pdf_push}")
    public String invoicePdfId;
    /**
     * 余票预警URL地址
     */
    @Value("${email-template.invoiceWarning}")
    private String invoiceWarning;
    @Value("${mail.emailSendUrl}")
    private String emailSendUrl;
    
    @Value("${express.kdniao.eBusinessID}")
    private String EBusinessID;
    @Value("${express.kdniao.appKey}")
    private String AppKey;
    @Value("${express.kdniao.reqURL}")
    private String ReqURL;
    
    /**
     * 扫码开票推送公众号secretId
     */
    @Value("${push.myinvoice.secretId}")
    private String pushMyinvoiceSecretId;
    
    /**
     * 扫码开票推送公众号secretKey
     */
    @Value("${push.myinvoice.secretKey}")
    private String pushMyinvoiceSecretKey;
    
    @Value("${my.domain}")
    private String localDomain;
    
    @Value("${myinvoice.appid}")
    private String appid;
    
    @Value("${notes.templateid}")
    private String notesTemplateId;

    /**
     * 已开发票数据导入发票PDF文件存储在mongodb服务上集合的名称
     */
    @Value("${mongodb.collection.name}")
    private String mongodbCollectionName;
    
    /**
     * 供应链 待审核发票推送虚拟税号 配置
     */
    @Value("${push.supplychain.virtual_taxpayer}")
    private String supplyVirtualTaxpayerCode;
    
    /**
     * mqtt相关配置
     */
    @Value("${com.mqtt.host}")
    private String mqttHost;
    @Value("${com.mqtt.username}")
    private String mqttUserName;
    @Value("${com.mqtt.password}")
    private String mqttPassword;
    @Value("${com.mqtt.timeout}")
    private String mqttTimeout;
    @Value("${com.mqtt.keepalive}")
    private String mqttKeepAlive;
    @Value("${com.mqtt.subscriptions}")
    private String mqttSubscriptions;

    @Value("${push.ws.username}")
    private String wsUsername;
    @Value("${push.ws.password}")
    private String wsPassword;
    
}
