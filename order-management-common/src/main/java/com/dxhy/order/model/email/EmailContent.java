package com.dxhy.order.model.email;

import lombok.Getter;
import lombok.Setter;
/**
 * 邮件模板内容
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:47
 */
@Setter
@Getter
public class EmailContent{

	/**
	 * 模板id
	 */
	private String templateId;

	/**
	 * 1
	 */
	private String serialNum;
	/**
	 * 郵件标题
	 */
    private String[] subjects;
	/**
	 * 邮件内容
	 */
    private String[] contents;
	/**
	 * 接收方邮箱
	 */
    private String[] to;
	/**
	 * 接收方名称
	 */
    private String[] toName;
	/**
	 * 抄送方邮箱
	 */
    private String[] cc;
	/**
	 * 抄送方名称
	 */
    private String[] ccName;
	/**
	 * 附件
	 */
	private Attachments[] attachments;

}
