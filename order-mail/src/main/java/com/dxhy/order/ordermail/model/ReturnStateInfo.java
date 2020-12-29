package com.dxhy.order.ordermail.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @ClassName ：ReturnStateInfo
 * @Description ：
 * @author ：杨士勇
 * @date ：2020年4月16日 上午11:00:18
 * 
 * 
 */

@Setter
@Getter
public class ReturnStateInfo  implements Serializable {
	
	private String returnCode;
	
	private String returnMessage;

}
