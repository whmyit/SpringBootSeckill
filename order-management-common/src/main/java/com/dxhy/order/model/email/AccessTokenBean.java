package com.dxhy.order.model.email;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 调用获取token接口BEAN
 *
 * @author tengjy
 * @version 1.0 Created on 2017年3月31日 上午10:14:44
 */
@SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
@Setter
@Getter
@ToString
public class AccessTokenBean {

	private String access_token;
	private String token_type;
	private String expires_in;
	
	private String error_description;
	private String error;
 
 
}
