package com.dxhy.order.constant;

/**
 * 常量
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0 2016-11-15
 */
public class Constant {
    
    /**
     * 业务数据对前端返回字符串
     */

    public static final String ORDERINFOID = "orderInfoId";
    
    public static final String ORDERID = "orderId";
    
    public static final String FPQQLSH = "fpqqlsh";
    
    public static final String CLIENT_PREFIX = "order-fg";
    
    public static final String EXIST = "exist";
    
    public static final String SUCCSSCODE = "0000";
    
    public static final String CHARSET_1 = "[";
    
    public static final String STRING_POINT = ".";
    
    public static final String TAXPAYERCODE = "taxpayerCode";
    
    public static final String TAXPAYERNAME = "name";
    
    public static final String OPENAPIACCESSTOKEN = "?access_token=";
    
    /**
     * redis key
     */
    
    public static final int REDIS_EXPIRE_TIME_DEFAULT = 30 * 24 * 60 * 60;
    /**
     * 发票限额RedisKey值
     */
    public static final String REDIS_INVOICE_QUO_PREFIX = "xxfp_invoice_quo_%s_%s_%s";
    
    
    public static final String REDIS_INVOICE_JSPXX_PREFIX = "sims:jsp:%s";
    
    public static final String REDIS_INVOICE_SPLIT_PREFIX = "xxfp:split:fpqqlsh:%s";
    
    /**
     * 静态码rediskey
     */
    public static final String REDIS_EWM_STATIC = "xxfp:ewm:static:%s";

    /**
     * 二维码开票接收微信推送redis同步锁前缀
     */
    public static final String REDIS_EWM_SYN_LOCK = "xxfp:ewm:receive:lock:%s";


    /**
     * 动态码Rediskey
     */
    public static final String REDIS_EWM_DYNAMIC = "dynamic";
    
    /**
     * 订单接收数据key
     */
    public static final String REDIS_INTERFACE_RECEIVE = "xxfp:order:receive:%s";
    
    /**
     * 缓存用户抬头信息的前缀
     */
    public static final String REDIS_EWM_TITLE = "xxfp:ewm:title:%s";
    /**
     * 用户引导缓存redis前缀
     */
    public static final String REDIS_USER_GUIDER = "xxfp:guider:%s";
    
    /**
     * 开票流水号与销方税号对应关系
     */
    public static final String REDIS_KPLSH = "xxfp:cache:kplsh:%s";
    
    /**
     * 发票批次号与销方税号对应关系
     */
    public static final String REDIS_FPQQPCH = "xxfp:cache:fpqqpch:%s";
    
    /**
     * 发票批次号与销方税号对应关系
     */
    public static final String REDIS_FPDMHM = "xxfp:cache:fpdmhm:%s";
    
    /**
     * 提取码与销方税号对应关系
     */
    public static final String REDIS_TQM = "xxfp:cache:tqm:%s";
    
    
    /**
     * 提取码与销方税号对应关系
     */
    public static final String REDIS_AUTHID = "xxfp:cache:authid:%s";
    
    
    /**
     * 方格存放税盘信息的  redis key
     */
    public static final String FG_TAX_DISK_INFO = "fa_tax_disk_info";
    
    public static final String REDIS_FG_TAX_DISK_INFO = "xxfp:cache:" + FG_TAX_DISK_INFO + ":%s";
    /**
     * 方格存放消息队列  redis key
     */
    public static final String REDIS_FG_MQTT_MSG = "xxfp:cache:fg_mqtt_msg:%s:%s";
    
    public static final String FG_MQTT_MSG_FLAG = "fg_mqtt_msg_flag";
    /**
     * 方格存放消息标识是否可取消息  redis key
     */
    public static final String REDIS_FG_MQTT_MSG_FLAG = "xxfp:cache:" + FG_MQTT_MSG_FLAG + ":%s:%s";
    
    /**
     * 方格发送消息topic
     */
    public static String FG_MQTT_TOPIC_PUB_FANGGE = "invoice/fangge/%s/%s";
    
    public static final String ERROR_MESSAGE_ORDER = "【税号:%s,发票请求唯一流水号:%s,订单号:%s】异常信息:%s";
    
    public static final String ERROR_MESSAGE_INVOICE = "【税号:%s,分机号:%s】剩余%s张%s发票，已低于预警值:%s;";
    
    
}
