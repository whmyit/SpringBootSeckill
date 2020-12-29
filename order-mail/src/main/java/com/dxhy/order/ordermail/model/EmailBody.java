package com.dxhy.order.ordermail.model;

/**
 *
 * <p>全局参数</p>
 *
 * @author tengjy
 * @version 1.0 Created on 2017年6月14日 上午10:14:11
 */
public class EmailBody {

	/**
	 * 压缩标识0：不压缩 1：压缩 (用GZip压缩)默认 0
	 */
	private String zipCode;
	/**
	 * 0:base64加密  1: 3DES加密 默认0
	 */
	private String encryptCode;
	/**
	 * "数据交换流水号
	 */
	private String dataExchangeId;
	/**
	 * base64请求数据内容或返回数据内容
	 */
	private String content;
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getEncryptCode() {
		return encryptCode;
	}
	public void setEncryptCode(String encryptCode) {
		this.encryptCode = encryptCode;
	}
	public String getDataExchangeId() {
		return dataExchangeId;
	}
	public void setDataExchangeId(String dataExchangeId) {
		this.dataExchangeId = dataExchangeId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "GlobalInfo [zipCode=" + zipCode + ", encryptCode=" + encryptCode + ", dataExchangeId=" + dataExchangeId
				+ ", content=" + content + "]";
	}
}
