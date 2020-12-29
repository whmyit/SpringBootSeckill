package com.dxhy.order.model.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName ：GlobalInfo
 * @Description ：
 * @author ：杨士勇
 * @date ：2020年4月13日 下午5:18:05
 * 
 * 
 */

@Setter
@Getter
public class GlobalInfo {
	
	private String zipCode;
	
	private String encryptCode;
	
	private String dataExchangeId;
	
	private String content;

}
