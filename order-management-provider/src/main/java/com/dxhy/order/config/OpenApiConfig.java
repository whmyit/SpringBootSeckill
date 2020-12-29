package com.dxhy.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * openapi配置
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 19:39
 */
@Component
public class OpenApiConfig {
    
    /**
     * 请求地址
     */
    public static String OPENAPI_EMAIL_NOTE = null;
    /**
     * 获取token
     */
    public static String OPENAPI_TOKEN = null;
    
    /**
     * token获取配置参数
     */
    public static String TOKEN_CLIENT_ID = null;
    public static String TOKEN_CLIENT_SECRET = null;
    public static String TOKEN_GRANT_TYPE = null;
    public static String TOKEN_SCOPE = null;
    /**
     * 安全凭证SECRET_ID
     */
    public static String SECRET_ID = null;
    /**
     * 安全凭证SECRET_KEY
     */
    public static String SECRET_KEY = null;
    
    /**
     * 请求域名
     */
    public static String REQUEST_DOMAIN = null;
    
    /**
     * 请求路径
     */
    public static String REQUEST_PATH = null;
    
    /**
     * 企业信息加密标示
     */
    public static int SYNCENTERPRISE_ENCRYPT_CODE = 0;
    
    /**
     * 信息压缩
     */
    public static int SYNCENTERPRISE_ZIP_CODE = 0;
    
    /**
     * 信息加密标示
     */
    public static int SYNCENTERPRISE_STATISTICS_ENCRYPT_CODE = 0;
    
    /**
     * 信息压缩
     */
    public static int SYNCENTERPRISE_STATISTICS_ZIP_CODE = 0;
    
    /**
     * A9开票接口地址
     */
    public static String invoiceIssuing;
    
    /**
     * OFD转png
     */
    public static String OfdToPngUrl;
    /**
     * 受理点查询的接口
     */
    public static String querySldList;
    
    public static String queryKpdXxBw;
    
    /**
     * 新税控获取分机号
     */
    public static String queryNsrXnsbxx;
    
    /**
     * 查询pdf的接口
     */
    public static String getPdf;
    
    
    public static String querySldFpfs;
    
    /**
     * 异常订单邮件模板
     */
    public static String invoiceYiChang;
    
    public static String invoicePdfId;
    
    public static String invoiceWarning;

    public static String emailSendUrl;
    
    
    public static String EBusinessID;
    public static String appKey;
    public static String reqURL;
    
    public static String qrCodeShortUrl;
    
    public static String insertCardUrl;
    
    /**
     * 短信发送接口地址
     */
    public static String sendMessageUrl;
    /**
     * 短信短链接url
     */
    public static String messageShortUrl;
    
    /**
     * 扫码开票推送公众号secretId
     */
    public static String pushMyinvoiceSecretId;
    
    /**
     * 扫码开票推送公众号secretKey
     */
    public static String pushMyinvoiceSecretKey;
    
    /**
     * 我的发票公众号appid
     */
    public static String appid;
    
    /**
     * 短信模板id
     */
    public static String notesTemplateId;
    
    /**
     * 已开发票数据导入发票PDF文件存储在mongodb服务上集合的名称
     */
    public static String MONGODB_COLLECTION_NAME;
    
    /**
     * 供应链推送配置虚拟税号
     */
    public static String supplyVirtualTaxpayer;
    
    /**
     * mqtt相关配置
     */
    public static String mqttHost;
    public static String mqttUserName;
    public static String mqttPassword;
    public static String mqttTimeout;
    public static String mqttKeepAlive;
    
    /**
     * mqtt订阅列表
     */
    public static String mqttSubscriptions;
    /**
     * 方格相关接口url
     */
    public static String getPdfFg;

    /**
     * webservice auth 用户名
     */
    public static String wsAuthUsername;

    /**
     * webservice auth 密码
     */
    public static String wsAuthPassword;
    
    /**
     * 静态变量注入
     *
     * @param openApiInitBean
     */
    @Autowired
    public void initConfig(OpenApiInitBean openApiInitBean) {
        OPENAPI_EMAIL_NOTE = openApiInitBean.getOpenApiEmailNote();
        OPENAPI_TOKEN = openApiInitBean.getOpenApiToken();
        
        /**  token获取配置参数  */
        TOKEN_CLIENT_ID = openApiInitBean.getTokenClientId();
        TOKEN_CLIENT_SECRET = openApiInitBean.getTokenClientSecret();
        TOKEN_GRANT_TYPE = openApiInitBean.getTokenGrantType();
        TOKEN_SCOPE = openApiInitBean.getTokenScope();
        
        
        SECRET_ID = openApiInitBean.getSecretId();
        
        SECRET_KEY = openApiInitBean.getSecretKey();
        
        REQUEST_DOMAIN = openApiInitBean.getRequestDomain();
        
        REQUEST_PATH = openApiInitBean.getRequestPath();
        
        /** A9接口 */
        invoiceIssuing = openApiInitBean.getDomain() + "/invoice/business/v1.0/invoiceIssuing";
        
        getPdf = openApiInitBean.getDomain() + "/invoice/business/v1.0/getPdf";
    
        getPdfFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/getPdf";
    
        querySldList = openApiInitBean.getDomain() + "/invoice/business/v1.0/querySld";
    
        queryKpdXxBw = openApiInitBean.getDomain() + "/sk/invoice/BWActiveX/web/v1.0/SkKpd/queryAll";
    
        queryNsrXnsbxx = openApiInitBean.getDomain() + "/invoice/business/v1.0/queryNsrXnsbxx";
    
        querySldFpfs = openApiInitBean.getDomain() + "/invoice/business/v1.0/querySldKykc";
    
        OfdToPngUrl = openApiInitBean.getOfdToPngUrl() + "/taxControl/invoice/business/v1.0/convertPng";
    
        invoiceYiChang = openApiInitBean.getInvoiceYiChang();
    
        invoicePdfId = openApiInitBean.getInvoicePdfId();
    
        invoiceWarning = openApiInitBean.getInvoiceWarning();

        emailSendUrl = openApiInitBean.getEmailSendUrl();

        EBusinessID = openApiInitBean.getEBusinessID();
        appKey = openApiInitBean.getAppKey();
        reqURL = openApiInitBean.getReqURL();
        
        pushMyinvoiceSecretId = openApiInitBean.getPushMyinvoiceSecretId();
        pushMyinvoiceSecretKey = openApiInitBean.getPushMyinvoiceSecretKey();
        
        qrCodeShortUrl = openApiInitBean.getLocalDomain() + "/api/v3/%s";
        
        insertCardUrl = openApiInitBean.getOpenApiEmailNote() + "insertCard";
        
        appid = openApiInitBean.getAppid();
        
        sendMessageUrl = openApiInitBean.getOpenApiEmailNote() + "smsSend";
        
        messageShortUrl = openApiInitBean.getLocalDomain() + "/notes/%s";
        
        notesTemplateId = openApiInitBean.getNotesTemplateId();
        
        MONGODB_COLLECTION_NAME = openApiInitBean.getMongodbCollectionName();
        
        supplyVirtualTaxpayer = openApiInitBean.getSupplyVirtualTaxpayerCode();
        /**
         * mqtt相关配置
         */
        mqttHost = openApiInitBean.getMqttHost();
        mqttUserName = openApiInitBean.getMqttUserName();
        mqttPassword = openApiInitBean.getMqttPassword();
        mqttTimeout = openApiInitBean.getMqttTimeout();
        mqttKeepAlive = openApiInitBean.getMqttKeepAlive();
        mqttSubscriptions = openApiInitBean.getMqttSubscriptions();
        /**
         * webservice 授权用户名和密码
         */
        wsAuthUsername = openApiInitBean.getWsUsername();
        wsAuthPassword = openApiInitBean.getWsPassword();

    }
    
    
}
