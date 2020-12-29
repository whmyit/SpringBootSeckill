package com.dxhy.order.consumer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：杨士勇
 * @ClassName ：SystemConfig
 * @Description ：系统内部配置
 * @date ：2019年6月25日 上午10:17:55
 */
@Component
public class SystemConfig {
    
    /**
     * 编码表版本号配置
     */
    public static String bmbbbh;

    /**
     * 部署web容器类型
     */
    public static String webServerType;

    /**
     * weblogic部署时文件存放路径
     */
    public static String downloadFileUrl;
    
    /**
     * 企业推送数据条数限制
     */
    public static String batchPushNum;
    
    /**
     * pdf打印水印(Y:打印水印,N:不打印)
     */
    public static String printPdfWaterMark;
    
    /**
     * pdf打印水印内容
     */
    public static String printPdfWaterMarkMsg;
    
    /**
     * 授权订单号前缀
     */
    public static String orderPrefix;
    
    public static String appid;
    @Autowired
    public void initConfig(PropertiesUtils propertiesUtils) {
        bmbbbh = propertiesUtils.getBmbbbh();
        webServerType = propertiesUtils.getWebServerType();
        downloadFileUrl = propertiesUtils.getDownloadFileUrl();
        batchPushNum = propertiesUtils.getBatchPushNum();
        printPdfWaterMark = propertiesUtils.getPrintPdfWaterMark();
        printPdfWaterMarkMsg = propertiesUtils.getPrintPdfWaterMarkMsg();
        orderPrefix = propertiesUtils.getOrderPrefix();
        appid = propertiesUtils.getAppid();
    }
}
