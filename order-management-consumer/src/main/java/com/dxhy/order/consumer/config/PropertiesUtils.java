package com.dxhy.order.consumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * 订单配置文件读取
 *
 * @author ZSC-DXHY
 */
@Configuration
@Getter
@Setter
public class PropertiesUtils {
    
    /**
     * 当前服务器地址
     */
    @Value("${order.url.domain}")
    private String localDomain;
    
    /**
     * 我的发票服务器地址
     */
    @Value("${order.url.myinvoiceUrl}")
    private String myinvoiceDomain;
    
    /**
     * 调用大B服务URL
     */
    @Value("${order.url.ssoUrl}")
    private String ssoUrl;
    
    /**
     * 调用大B服务URL
     */
    @Value("${order.url.dBUserInfoUrl}")
    private String dBUserInfoUrl;
    
    /**
     * 辅助运营服务URL
     */
    @Value("${order.url.fzyyUrl}")
    private String fzyyUrl;
    
    /**
     * 辅助运营eureka服务URL
     */
    @Value("${order.url.fzyyEurekaUrl}")
    private String fzyyEurekaUrl;
    
    /**
     * C48打印服务地址
     */
    @Value("${order.url.C48PrintUrl}")
    private String printDomain;
    
    /**
     * 底层开票服务地址
     */
    @Value("${order.url.A9}")
    private String interfaceA9BusinessUrl;
    
    /**
     * 底层开票服务地址-方格
     */
    @Value("${order.url.FG}")
    private String interfaceFgBusinessUrl;
    
    /**
     * ofd转Png地址
     */
    @Value("${order.url.OfdToPngUrl}")
    private String ofdToPngUrl;
    
    /**
     * 编码表版本号
     */
    @Value("${order.system.bmbbbh}")
    private String bmbbbh;
    
    /**
     * 底层开票服务地址
     */
    @Value("${order.url.frontUrl}")
    private String frontUrl;
    
    /**
     * 预览pdf水印
     */
    @Value("${order.system.printPdfWaterMark}")
    private String printPdfWaterMark;
    
    /**
     * 预览pdf水印内容
     */
    @Value("${order.system.printPdfWaterMarkMsg}")
    private String printPdfWaterMarkMsg;
    
    /**
     * 套餐余量产品ID
     */
    @Value("${order.system.productId}")
    private String systemProductId;
    
    /**
     * ofd文件预览URL
     */
    @Value("${order.system.ofdUrl}")
    private String ofdUrl;
    
    /**
     * 判断不是方式是不是weblogic
     */
    @Value("${order.weblogic.webServerType}")
    private String webServerType;
    
    /**
     * weblogic方式部署,读取模板地址
     */
    @Value("${order.weblogic.downloadFileUrl}")
    private String downloadFileUrl;
    
    /**
     * 批次推送数量
     */
    @Value("${order.push.batchPushNum}")
    private String batchPushNum;
    
    /**
     * 用户退出登录URL
     */
    @Value("${dxhy.userCenter.logoutPath}")
    private String logoutPath;
    
    /**
     * 过滤器不拦截的URL,以逗号隔开
     */
    @Value("${dxhy.userCenter.excludedPaths}")
    private String excludedPaths;
    
    /**
     * token失效时重定向的地址
     */
    @Value("${dxhy.userCenter.redirectUrl}")
    private String redirectUrl;
    
    
    /**
     * 微信授权订单号前缀
     */
    @Value("${order.system.orderCardPrefix}")
    private String orderPrefix;
    /**
     * 公众号appid
     */
    
    @Value("${order.gzh.appid}")
    private String appid;


    @Value("${order.url.newtax}")
    public String interfaceNewTaxBusinessUrl;
}
