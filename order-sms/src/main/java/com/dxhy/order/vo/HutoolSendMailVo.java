package com.dxhy.order.vo;

import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: wangyang
 * @date: 2020/5/8 9:36
 * @description
 */
@Data
public class HutoolSendMailVo {
    /**
     * 发件人邮箱地址 若需要发件人显示昵称，赋值时：昵称<邮箱地址>
     */
    private String from;

    /**
     * 发件人的邮箱的SMTP服务器地址
     */
    private String host;

    /**
     * 邮件服务器端口
     */
    private Integer port = 465;

    /**
     * 是否开启授权密码校验
     */
    private Boolean auth = true;

    /**
     * 邮箱前缀
     */
    private String user;

    /**
     * 授权密码
     */
    private String pass;

    /**
     * 使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展
     */
    private boolean startttlsEnable = true;

    /**
     * 使用SSL安全连接
     */
    private Boolean sslEnable = true;

    /**
     * 字符集
     */
    private Charset charset = StandardCharsets.UTF_8;
}
