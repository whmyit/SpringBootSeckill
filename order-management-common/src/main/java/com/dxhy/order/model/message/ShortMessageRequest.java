package com.dxhy.order.model.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName ：ShortMessageRequest
 * @Description ：
 * @author ：杨士勇
 * @date ：2020年4月16日 上午10:01:55
 * 
 * 
 */

@Getter
@Setter
public class ShortMessageRequest {
	
	private String templateCode;
	
	private String serialNum;

	private String[] params;
	
	private String[] phones;

}
