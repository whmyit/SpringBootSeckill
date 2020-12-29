package com.dxhy.order.vo;

import lombok.Data;

/**
 * @author: wangyang
 * @date: 2020/5/5 10:42
 * @description
 */
@Data
public class SendMailVo {

    /**
     * 发件人邮箱地址
     */
    private String sendAddress;

    /**
     * 授权密码
     */
    private String authPassword;

    /**
     * 发件人名称
     */
    private String sendName;

    /**
     * 发件服务器
     */
    private String smtpServer;

    /**
     * 是否验证授权密码
     */
    private String smtpAuth = "true";
}
