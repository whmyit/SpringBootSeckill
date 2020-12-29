package com.dxhy.order.model.email;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 邮件附件
 * </p>
 *
 * @author tengjy
 * @version 1.0 Created on 2017年6月20日 下午4:41:17
 */
@Getter
@Setter
public class Attachments {
    
    private String name;
    private String content;
    private String type;
}
