package com.dxhy.order.model.a9.pdf;

import lombok.Data;

/**
 * 
 * @ClassName ：InvoicePdf
 * @Description ：
 * @author ：杨士勇
 * @date ：2019年7月19日 下午10:53:47
 * 
 * 
 */
@Data
public class InvoicePdf {
	private String FP_DM;
	private String FP_HM;
	private String STATUS_CODE;
	private String STATUS_MESSAGE;
	private String PDF_FILE;
}
