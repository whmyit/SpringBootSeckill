package com.dxhy.order.model.email;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>[请去回去参数token]</p>
 *
 * @author tengjy
 * @version 1.0 Created on 2017年3月31日 上午10:33:15
 */
@Getter
@Setter
@ToString
public class RquestToken {

	private String client_id;
	private String client_secret;
	private String grant_type;
	private String scope;
	
	
	
	
}
