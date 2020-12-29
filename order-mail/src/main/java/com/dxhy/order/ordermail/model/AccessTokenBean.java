package com.dxhy.order.ordermail.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>[描述信息：调用获取token接口BEAN]</p>
 *
 * @author tengjy
 * @version 1.0 Created on 2017年3月31日 上午10:14:44
 */
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
