package com.dxhy.order.ordermail.model;

import java.io.Serializable;

/**
 * 
 * <p>
 * 邮件附件
 * </p>
 *
 * @author tengjy
 * @version 1.0 Created on 2017年6月20日 下午4:41:17
 */
public class Attachments implements Serializable{

	private String name;
	private String content;
	private String type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
