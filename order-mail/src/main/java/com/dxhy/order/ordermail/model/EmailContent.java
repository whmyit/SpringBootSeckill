package com.dxhy.order.ordermail.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailContent implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 是否需要传账号密码，false不传，true传
	 */
	private boolean IF_NEED_ACCOUNT;
	/**
	 * 发送人邮箱地址
	 */
	private String FROM_ADDRESS;
	/**
	 * 发送人邮箱密码
	 */
	private String PASSWORD;
	/**
	 * 模板id
	 */
	private String template_id;
	/**
	 * 1
	 */
	private String serialnum;
	/**
	 * 发件人显示名称
	 */
	private String senderName;
	/**
	 * 郵件标题
	 */
	private String subjects;
	/**
	 * 邮件内容
	 */
	private String contents[];
	/**
	 * 图片地址
	 */
	private String PICS[];
	/**
	 * 接收方邮箱
	 */
	private String to[];
	/**
	 * 接收方名称
	 */
	private String TONAME[];
	/**
	 * 抄送方邮箱
	 */
	private String CC[];
	/**
	 * 抄送方名称
	 */
	private String CCNAME[];
	/**
	 * 附件
	 */
	private Attachments[] attachments;
	
	/**
	 *发票代码
	 */
	private String FP_DM;
	
	/**
	 *发票号码
	 */
	private String FP_HM;
	
	


}
