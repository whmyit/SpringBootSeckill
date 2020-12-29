package com.dxhy.order.api;

import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.InvoiceWarningInfo;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName ：ApiEmailServiceImpl
 * @Description ：邮件发送
 * @author ：杨士勇
 * @date ：2019年3月7日 下午2:59:31
 *
 *
 */

public interface ApiEmailService {
	
	/**
	 * 发送发票版式文件到邮箱
	 *
	 * @param invoiceIds
	 * @param emailAddress
	 * @return
	 */
    R sendPdfEmail(List<Map> invoiceIds, String emailAddress);
    
    /**
     * 发送邮箱
     *
     * @param content
     * @return
     */
    Map<String, Object> sendEmail(String content);
	
	/**
	 * 业务邮箱推送接口
	 *
	 * @param messgae
	 */
	void pushWithEmail(String messgae);
	
	/**
	 * 余票预警
	 *
	 * @param invoiceWarningInfo
	 * @param dqfpfs
	 * @param mc
	 */
	void sendInvoiceWarningInfoEmail(InvoiceWarningInfo invoiceWarningInfo, String dqfpfs, String mc);
	
	
}
