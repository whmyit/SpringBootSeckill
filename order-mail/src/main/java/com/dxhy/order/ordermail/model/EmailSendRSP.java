package com.dxhy.order.ordermail.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EmailSendRSP implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//返回状态
	private String code;

	//返回消息
	private String msg;
	
	//返回数据体
	private Object data;
	
	

}
