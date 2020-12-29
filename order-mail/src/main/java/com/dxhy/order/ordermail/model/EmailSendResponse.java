package com.dxhy.order.ordermail.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ：杨士勇
 * @ClassName ：OpenApiResponse
 * @Description ：
 * @date ：2020年4月16日 上午10:56:24
 */

@Setter
@Getter
public class EmailSendResponse implements Serializable{
	
	private ReturnStateInfo returnStateInfo;
	
}
