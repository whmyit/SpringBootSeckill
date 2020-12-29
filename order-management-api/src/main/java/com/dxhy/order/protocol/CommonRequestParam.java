package com.dxhy.order.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 对外API公共参数
 *
 * @author thinkpad
 */
@Setter
@Getter
public class CommonRequestParam implements Serializable {

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 随机数
     */
    private String nonce;
    /**
     * 用户id
     */
    private String secretId;
    /**
     * 签名
     */
    private String signature;

    /**
     * 加密标识
     */
    private String encryptCode;

    /**
     * 压缩表示
     */
    private String zipCode;

    /**
     * 业务请求参数
     */
    private String content;

}
