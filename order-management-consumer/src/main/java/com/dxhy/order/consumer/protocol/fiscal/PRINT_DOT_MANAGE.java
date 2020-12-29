package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 10:41:53
 * @Describe
 */
@Setter
@Getter
public class PRINT_DOT_MANAGE {
    /**
     * 打印点id
     */
    private Integer dydid;
    /**
     * 打印点名称
     */
    private String dydmc;
    /**
     * 打印点状态
     */
    private String dydzt;
    /**
     * 在线状态
     */
    private String spotKey;
    /**
     * 打印服务器名称
     */
    private String serverName;
    /**
     * 打印服务id
     */
    private String serverId;
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 销货方名称
     */
    private String xhfMc;


}
